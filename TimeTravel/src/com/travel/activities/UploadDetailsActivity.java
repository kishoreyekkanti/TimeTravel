package com.travel.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.travel.services.TwitpicUpload;
import com.travel.utils.CurrentLocation;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

public class UploadDetailsActivity extends Activity{

    private String imageUriPath;
	private EditText description;
	TwitpicUpload twitPicUpload;
	LocationManager locationManager;
	Location lastBestKnownLocation;

    public void onCreate(Bundle savedInstanceState){
		Log.d("UploadDetailsActivity","Stareted uploaded details activiy");
		super.onCreate(savedInstanceState);
		imageUriPath = getIntent().getStringExtra(PhotoCaptureActivity.IMAGE_PATH);
		setContentView(R.layout.upload_details);
		twitPicUpload = new TwitpicUpload(this);
		description = (EditText)findViewById(R.id.description);
		Button upload = (Button)findViewById(R.id.photo_upload);
		
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        setLocationProviders();
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

	private void setLocationProviders() {
		CurrentLocation currentLocation = new CurrentLocation(locationManager);
		currentLocation.setLocationProvider(locationManager);
		lastBestKnownLocation = currentLocation.getBestLastKnownLocation();
	}
	
	private void triggerUpload(){
		Log.i("UploadDetailsActivity","Uploading the photo!");
			try {
				Uri imageUri = Uri.parse(imageUriPath);
				String[] projection = {MediaStore.Images.Media.DATA};
				Cursor imageCursor =  managedQuery(imageUri, projection, null, null, null);
				int index = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); 
				imageCursor.moveToFirst();
				String image_path = imageCursor.getString(index);
				Log.e("Upload Details Activity",image_path);
				String twitpic_url = twitPicUpload.uploadImageFor(image_path, description.getText().toString());
		         // prepare post method  
		         HttpPost httpPost = new HttpPost(getString(R.string.webservice_url_post));
		   
		         // add parameters to the post method  
		         List <NameValuePair> parameters = new ArrayList <NameValuePair>();  
		         parameters.add(new BasicNameValuePair("url", twitpic_url));  
		         parameters.add(new BasicNameValuePair("description", description.getText().toString()));
		         parameters.add(new BasicNameValuePair("latitude",  lastBestKnownLocation!=null?String.valueOf(lastBestKnownLocation.getLatitude()):"37.422006"));
		         parameters.add(new BasicNameValuePair("longitude", lastBestKnownLocation!=null?String.valueOf(lastBestKnownLocation.getLongitude()):"-122.084095"));
		   
		         UrlEncodedFormEntity sendentity = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);  
		         httpPost.setEntity(sendentity);
		   
		         // create the client and execute the post method  
		         HttpClient client = new DefaultHttpClient();  
		         HttpResponse wsResponse = client.execute(httpPost);
		   
		         // retrieve the output and display it in console  
		         client.getConnectionManager().shutdown();				
				Log.d("Twitpic Upload details",twitpic_url);
				Log.d("Webservice post details",wsResponse.toString());
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("UploadDetailsActivity",e.toString());
			}
	}

}
