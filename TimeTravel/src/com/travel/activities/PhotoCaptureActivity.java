package com.travel.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.widget.ImageView;

public class PhotoCaptureActivity extends Activity {
	/** Called when the activity is first created. */
	protected static final String IMAGE_PATH = "IMAGE_PATH";
    private static final int IMAGE_CAPTURE = 0;
    private Uri imageUri;
    private ImageView imageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_capture);
		Log.i("PhotoCaptureActivity", "Initializing the camera surface now");
		imageView = (ImageView)findViewById(R.id.camera_preview);
		startCamera();
	}

    public void startCamera() {
        Log.d("PhotoCaptureActivity", "Starting camera on the phone...");
        String fileName = getCurrentTime()+".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == IMAGE_CAPTURE) {
			if (resultCode == RESULT_OK) {
				new Thread(new Runnable() {
					public void run() {
						Log.d("PhotoCaptureActivity", "Photo Captured!!!");
						imageView.setImageURI(imageUri);
						Intent uploadDetails = new Intent(PhotoCaptureActivity.this,UploadDetailsActivity.class);
						uploadDetails.putExtra(PhotoCaptureActivity.IMAGE_PATH,imageUri.getPath());
						startActivity(uploadDetails);
					}
				}).start();
			}
		}
		PhotoCaptureActivity.this.finish();
    }
    
	 public static long getCurrentTime() {
		Time captureTime = new Time();
		captureTime.setToNow();
		return captureTime.toMillis(true);
		}
}