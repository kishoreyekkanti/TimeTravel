package com.travel.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;

public class PhotoCaptureActivity extends Activity {

    protected static final String IMAGE_PATH = "IMAGE_PATH";
    private static final int IMAGE_CAPTURE = 0;
    private ImageView imageView;
    String SD_CARD_TEMP_DIR = Environment.getExternalStorageDirectory() + File.separator + getCurrentTime() + ".jpg";
    ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_capture);
        Log.i("PhotoCaptureActivity", "Initializing the camera surface now");
        imageView = (ImageView) findViewById(R.id.camera_preview);
        Button camera = (Button) findViewById(R.id.start_camera);
        camera.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startCamera();
            }
        });

    }

    public void startCamera() {
        Log.d("PhotoCaptureActivity", "Starting camera on the phone...");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(SD_CARD_TEMP_DIR)));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }

    private void startProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Processing the image...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void stopProgressDialog() {
        mProgressDialog.dismiss();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                new Thread(new Runnable() {
                    public void run() {
                        Intent uploadDetails = new Intent(PhotoCaptureActivity.this, UploadDetailsActivity.class);
                        startActivity(uploadDetails);
                        Log.e("PhotoCaptureActivity", "Photo Captured!!!");
                        File f = new File(SD_CARD_TEMP_DIR);
                        Uri capturedImage = null;
                        try {
                            capturedImage = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), f.getAbsolutePath(), null, null));
                        } catch (FileNotFoundException e) {
                            Log.e("PhotoCaptureActivity", e.toString());
                        }
                        Log.e("PhotoCaptureActivity", "capturedImage: " + capturedImage.toString());
                        imageView.setImageURI(capturedImage);
                        uploadDetails.putExtra(PhotoCaptureActivity.IMAGE_PATH, capturedImage.toString());
                        Log.e("PhotoCaptureActivity", "Starting the Upload activity");
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