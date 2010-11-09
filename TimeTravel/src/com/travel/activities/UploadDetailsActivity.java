package com.travel.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.travel.services.TwitpicUpload;

public class UploadDetailsActivity extends Activity{
	private String imagePath;
	private EditText description;
	TwitpicUpload twitPicUpload;
	LocationManager locationManager;
	public void onCreate(Bundle savedInstanceState){
		Log.d("UploadDetailsActivity","Stareted uploaded details activiy");
		super.onCreate(savedInstanceState);
		imagePath = getIntent().getStringExtra(PhotoCaptureActivity.IMAGE_PATH);
		setContentView(R.layout.upload_details);
		twitPicUpload = new TwitpicUpload(this);
		description = (EditText)findViewById(R.id.description);
		Button upload = (Button)findViewById(R.id.photo_upload);
		
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 1000, new MyLocationListener());
		upload.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d("Upload Details Activity", "triggering the upload");
				new Thread(new Runnable() {
					public void run() {
						Intent capturePhoto = new Intent(UploadDetailsActivity.this,PanoramaTabWidget.class);
						startActivity(capturePhoto);
						triggerUpload();
					}
				}).start();
				UploadDetailsActivity.this.finish();
			}
		});
	}
	
	private void triggerUpload(){
		Log.i("UploadDetailsActivity","Uploading the photo!");
			try {
				String twitpic_url = twitPicUpload.uploadImageFor(imagePath, description.getText().toString());
				Location location = getCurrentLocation();
		         // prepare post method  
		         HttpPost post = new HttpPost(getString(R.string.webservice_url_post));  
		   
		         // add parameters to the post method  
		         List <NameValuePair> parameters = new ArrayList <NameValuePair>();  
		         parameters.add(new BasicNameValuePair("url", twitpic_url));  
		         parameters.add(new BasicNameValuePair("description", description.getText().toString()));
		         parameters.add(new BasicNameValuePair("latitude",  location!=null?String.valueOf(location.getLatitude()):""));
		         parameters.add(new BasicNameValuePair("longitude", location!=null?String.valueOf(location.getLongitude()):""));
		   
		         UrlEncodedFormEntity sendentity = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);  
		         post.setEntity(sendentity);   
		   
		         // create the client and execute the post method  
		         HttpClient client = new DefaultHttpClient();  
		         HttpResponse wsResponse = client.execute(post);  
		   
		         // retrieve the output and display it in console  
		         client.getConnectionManager().shutdown();				
				Log.d("Twitpic Upload details",twitpic_url);
				Log.d("Webservice post details",wsResponse.toString());
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("UploadDetailsActivity",e.toString());
			}
	}

	private Location getCurrentLocation(){
		return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}
	
	private class MyLocationListener implements LocationListener{

		public void onLocationChanged(Location location) {
			String message = String.format(
					"New Location \n Latitude: %1$s \n Longitude: %2$s",
					location.getLatitude(), location.getLongitude()
					);
			Log.d("UploadDetailsActivity", message);
		}

		public void onProviderDisabled(String provider) {
			Log.d("UploadDetailsActivity", "Provider disabled by the user, GPS is turned off!");
		}

		public void onProviderEnabled(String provider) {
			Log.d("UploadDetailsActivity", "Provider enabled by the user, GPS is truned on!");
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d("UploadDetailsActivity", "Provider status changed!");
			
		}
    	
    }	
	
}
