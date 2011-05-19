package com.travel.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class PanoramaTabWidget extends TabActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, PhotoCaptureActivity.class);
        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("camera")
                .setIndicator("", res.getDrawable(R.drawable.camera_64))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, ImageViewActivity.class);
        spec = tabHost.newTabSpec("gallery")
                .setIndicator("", res.getDrawable(R.drawable.gallery_64))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, UserPreferenceActivity.class);
        spec = tabHost.newTabSpec("preferences")
                .setIndicator("", res.getDrawable(R.drawable.settings_64))
                .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }
}