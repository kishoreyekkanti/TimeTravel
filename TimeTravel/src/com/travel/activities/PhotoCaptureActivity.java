package com.travel.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class PhotoCaptureActivity extends Activity {
	/** Called when the activity is first created. */
	private Camera camera;
	private SurfaceView preview;
	private SurfaceHolder previewHolder;
	protected static final String IMAGE_PATH = "IMAGE_PATH";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_capture);
		ImageView cameraButton = (ImageView) findViewById(R.id.camera_click);
		cameraButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				takePicture();
			}

		});
		Log.i("NewReviewActivity", "Initializing the camera surface now");
		preview = (SurfaceView) findViewById(R.id.camera_preview);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	private void takePicture() {
		camera.takePicture(null, null, photoCallBack);
	}

	Camera.PictureCallback photoCallBack = new Camera.PictureCallback() {

		public void onPictureTaken(final byte[] data, Camera camera) {
			Log.i("PhotoCaptureActivity", "Captured photo!");
			new Thread(new Runnable() {
				public void run() {
					String imagePath = savePictureInCache(data);
					// Triggers upload activity since snap has been taken.
					Intent uploadDetails = new Intent(PhotoCaptureActivity.this,UploadDetailsActivity.class);
					uploadDetails.putExtra(PhotoCaptureActivity.IMAGE_PATH,imagePath);
					startActivity(uploadDetails);
				}
			}).start();		
			PhotoCaptureActivity.this.finish();

		}
	};

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Camera.Parameters parameters = camera.getParameters();
			parameters.setPictureFormat(PixelFormat.JPEG);
			camera.setParameters(parameters);
			camera.startPreview();
		}

		public void surfaceCreated(SurfaceHolder holder) {
			camera = Camera.open();
			try {
				camera.setPreviewDisplay(previewHolder);
			} catch (IOException e) {
				Log.e("PhotoCaptureActivity", e.toString());
			}

		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.i("PhotoCaptureActivity", "Surface destroyed called!");
			camera.stopPreview();
			camera.release();
			camera = null;
		}

	};

	private String savePictureInCache(byte[] pictureData) {
		if (pictureData != null) {
			String photoName = getCurrentTime() + ".jpg";
			File photo = new File(getDir("gv_img_cache", Context.MODE_PRIVATE),
					photoName);
			try {
				FileOutputStream fos = new FileOutputStream(photo.getPath());
				fos.write(pictureData);
				fos.close();
				Log.i(this.getClass().getName(), "Saved picture with path:"	+ photo.getPath());
				return photo.getPath();
			} catch (java.io.IOException e) {
				Log.e("SavePhotoTask","Exception in photoCallback for photo name :"+ photoName, e);
			}
		}
		return null;
	}
	
	 public static long getCurrentTime() {
		Time captureTime = new Time();
		captureTime.setToNow();
		return captureTime.toMillis(true);
		}
}