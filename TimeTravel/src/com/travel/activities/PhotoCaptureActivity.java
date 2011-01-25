package com.travel.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;

public class PhotoCaptureActivity extends Activity {
	/** Called when the activity is first created. */
	private Camera camera;
	private SurfaceView preview;
	private SurfaceHolder previewHolder;
	private boolean isPreviewRunning = false;
	protected static final String IMAGE_PATH = "IMAGE_PATH";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_capture);
		Log.i("PhotoCaptureActivity", "Initializing the camera surface now");
		preview = (SurfaceView) findViewById(R.id.camera_preview);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		preview.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				takePicture();
			}
		});
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


    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
	
 SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {

			if (isPreviewRunning) {
				camera.stopPreview();
			}
			try {
				camera.startPreview();
			} catch (Exception e) {
				Log.e("CAMERA PREVIEW", "Cannot start preview", e);
			}
			isPreviewRunning = true;
		}

		public void surfaceCreated(SurfaceHolder holder) {
			try {
				Log.e("Photo Capture activity","Surface created");
				camera = Camera.open();
				camera.setPreviewDisplay(previewHolder);
				Camera.Parameters parameters = camera.getParameters();

//		        Camera.Size s = parameters.getSupportedPreviewSizes().get(0);
//		        parameters.setPreviewSize( s.width, s.height );
				parameters.setPictureFormat(PixelFormat.JPEG);
				camera.setParameters(parameters);	
				camera.setPreviewDisplay(holder);
			} catch (IOException e) {
				Log.e("PhotoCaptureActivity", e.toString());
				finish();
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.i("PhotoCaptureActivity", "Surface destroyed called!");
			if(isPreviewRunning && camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
			}
			isPreviewRunning = false;
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