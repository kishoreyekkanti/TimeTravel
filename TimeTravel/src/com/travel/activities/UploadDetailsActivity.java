package com.travel.activities;

import android.app.Activity;
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
	public void onCreate(Bundle savedInstanceState){
		Log.d("UploadDetailsActivity","Stareted uploaded details activiy");
		super.onCreate(savedInstanceState);
		imagePath = getIntent().getStringExtra(PhotoCaptureActivity.IMAGE_PATH);
		setContentView(R.layout.upload_details);
		twitPicUpload = new TwitpicUpload(this);
		description = (EditText)findViewById(R.id.description);
		Button done = (Button)findViewById(R.id.photo_upload);
		done.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
		Log.d("Upload Details Activity","triggering the upload");		
			triggerUpload();
			//UploadDetailsActivity.this.finish();	
			}
		});
	}
	
	private void triggerUpload(){
		Log.i("UploadDetailsActivity","Uploading the photo!");
//		new Thread(new Runnable(){
//			public void run(){
			try {
				String response = twitPicUpload.uploadImageFor(imagePath, description.getText().toString());
				Log.d("Upload details",response);
			} catch (Exception e) {
				Log.e("UploadDetailsActivity",e.toString());
			}
//		}});
	}
	
}
