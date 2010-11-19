package com.travel.activities;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.travel.utils.CurrentLocation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ImageViewActivity extends Activity {
	LocationManager locationManager;
	static SharedPreferences preferences;
	String[] remoteImageURLs;
	private static final String JPG = ".jpg";
	private static final String TWITPIC_BASE_URL = "http://twitpic.com/show/thumb/";
	JSONArray jsonArray;
	Gallery gallery;
	Context imageViewContext;
	ProgressDialog mProgressDialog;
	 Location lastBestKnownLocation;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_view);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		setLocationProviders();
		/*
		 * Find the gallery defined in the main.xml Apply a new (custom)
		 * ImageAdapter to it.
		 */
		gallery = (Gallery) findViewById(R.id.gallery);
		imageViewContext = this;
		new JSONTask().execute(getString(R.string.webservice_url_get));

	}
	
	private void setLocationProviders() {
		CurrentLocation currentLocation = new CurrentLocation(locationManager);
		currentLocation.setLocationProvider(locationManager);
		lastBestKnownLocation = currentLocation.getBestLastKnownLocation();
	}
	public class JSONTask extends AsyncTask<String, Integer, JSONArray>{
		
		@Override
		protected void onPreExecute(){
			mProgressDialog = new ProgressDialog(imageViewContext);
			mProgressDialog.setMessage("Downloading Images...");
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}

		@Override
		protected JSONArray doInBackground(String... getUrl) {
			
			return getJson(getUrl[0]);
		}
		
		@Override
		protected void onPostExecute(JSONArray extractedJSON){
			jsonArray = extractedJSON;
			remoteImageURLs = extractRemoteImageURLs(jsonArray);
			setImageDescription(jsonArray, 0);
			setGeoLocation(jsonArray, 0);
			gallery.setAdapter(new ImageAdapter(imageViewContext, remoteImageURLs));
			gallery.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView parent, View v, int position,
						long id) {
					setImageDescription(jsonArray, position);
					setGeoLocation(jsonArray, position);
				}
			});
			mProgressDialog.dismiss();	
		}
		
	}
	
	private void setImageDescription(JSONArray jsonArray,int index) {
		TextView textView = (TextView)findViewById(R.id.image_description);
        try {
			textView.setText(jsonArray.getJSONObject(index).getString("description"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setGeoLocation(JSONArray jsonArray,int index) {
		TextView textView = (TextView)findViewById(R.id.geo_location);
        try {
			textView.setText("Latitude:"+jsonArray.getJSONObject(index).getDouble("latitude")+"\n"+
							 "Longitude:"+jsonArray.getJSONObject(index).getDouble("longitude"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private JSONArray getJson(String getUrl) {
		JSONArray jArray = null;
		try {
			HttpGet request = new HttpGet(getUrlForCriteria(getUrl));
			request.setHeader("Accept", "application/json");

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(request);

			HttpEntity responseEntity = response.getEntity();

			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(responseEntity.getContent()));
			StringBuilder finalJsonString = new StringBuilder();
			String eachLine;
			while ((eachLine = bufferedReader.readLine()) != null) {
				finalJsonString.append(eachLine);
			}
			Log.d("ImageViewActivity", finalJsonString.toString());
			jArray = new JSONArray(finalJsonString.toString());
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject jsonData = jArray.getJSONObject(i);
				Log.d("url", jsonData.getString("url"));
				Log.d("latitude", String
						.valueOf(jsonData.getDouble("latitude")));
				Log.d("longitude", String.valueOf(jsonData
						.getDouble("longitude")));
				Log.d("description", jsonData.getString("description"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jArray;
	}

	private  String getUrlForCriteria(String getUrl) {
		StringBuilder remoteServiceUrl = new StringBuilder(getUrl);
		remoteServiceUrl.append("?").append(
				"latitude=" + (lastBestKnownLocation!=null?String.valueOf(lastBestKnownLocation.getLatitude()):"37.422006") + "&").append(
				"longitude=" + (lastBestKnownLocation!=null?String.valueOf(lastBestKnownLocation.getLongitude()):"-122.084095") + "&").append(
				"staleness=" + preferences.getInt("staleness", 1) + "&")
				.append("radius=" + preferences.getInt("radius", 1));
		return remoteServiceUrl.toString();
	}

	private String[] extractRemoteImageURLs(JSONArray jArray) {
		String[] remoteUrls = new String[jArray.length()];
		Log.d("Remote Image URL length",jArray.length()+"");
		try {
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject jsonData = jArray.getJSONObject(i);
				Log.d("JSON DATA",jsonData.getString("url"));
				remoteUrls[i] = TWITPIC_BASE_URL+jsonData.getString("url")+JPG;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return remoteUrls;
	}	
	
	public class ImageAdapter extends BaseAdapter {
		/** The parent context */
		private Context context;
		private int itemBackground;

		/** URL-Strings to some remote images. */
		private String[] myRemoteImages;

		/** Simple Constructor saving the 'parent' context. */
		public ImageAdapter(Context c,String[] remoteImageURLs) {
			this.context = c;
			myRemoteImages = remoteImageURLs;
			TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
            itemBackground = a.getResourceId(R.styleable.Gallery1_android_galleryItemBackground, 0);
            a.recycle(); 
		}

		/** Returns the amount of images we have defined. */
		public int getCount() {
			return this.myRemoteImages.length;
		}

		/* Use the array-Positions as unique IDs */
		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		/**
		 * Returns a new ImageView to be displayed, depending on the position
		 * passed.
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(this.context);

			try {
				/* Open a new URL and get the InputStream to load data from it. */
				Log.d("ImageActivity","for the image "+myRemoteImages[position]);
				URL aURL = new URL(myRemoteImages[position]);
				URLConnection conn = aURL.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				/* Buffered is always good for a performance plus. */
				BufferedInputStream bis = new BufferedInputStream(is, 8);
				/* Decode url-data to a bitmap. */
				Bitmap bm = BitmapFactory.decodeStream(bis);
				bis.close();
				is.close();
				/* Apply the Bitmap to the ImageView that will be returned. */
				imageView.setImageBitmap(bm);
			} catch (IOException e) {
				// i.setImageResource(R.drawable.error);
				Log.e("DEBUGTAG", "Remtoe Image Exception", e);
			}

			/* Image should be scaled as width/height are set. */
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			/* Set the Width/Height of the ImageView. */
			imageView.setLayoutParams(new Gallery.LayoutParams(250, 220));
			imageView.setBackgroundResource(itemBackground);
			return imageView;
		}

		/**
		 * Returns the size (0.0f to 1.0f) of the views depending on the
		 * 'offset' to the center.
		 */
		public float getScale(boolean focused, int offset) {
			/* Formula: 1 / (2 ^ offset) */
			return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
		}

	}

}
