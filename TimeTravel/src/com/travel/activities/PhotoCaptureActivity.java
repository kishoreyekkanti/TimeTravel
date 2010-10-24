package com.travel.activities;

import java.io.IOException;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_capture);
        ImageView cameraButton = (ImageView)findViewById(R.id.camera_click);
        cameraButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				takePicture();
			}
        	
        });
        Log.i("NewReviewActivity", "Initializing the camera surface now");
        preview=(SurfaceView)findViewById(R.id.camera_preview);
        previewHolder=preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
    }
  
    private void takePicture(){
		camera.takePicture(null, null, photoCallBack);
    }
    
    Camera.PictureCallback photoCallBack =  new Camera.PictureCallback() {
		
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.i("PhotoCaptureActivity", "Captured photo!");
			PhotoCaptureActivity.this.finish();
			
		}
	};
	
	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback(){

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
			Log.i("PhotoCaptureActivity","Surface destroyed called!");
			camera.stopPreview();
			camera.release();
			camera = null;
		}
		
	};
}