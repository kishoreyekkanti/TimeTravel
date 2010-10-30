package com.travel.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TabHost;

public class PanoramaTabWidget extends TabActivity{

public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    Resources res = getResources(); // Resource object to get Drawables
    TabHost tabHost = getTabHost();  // The activity TabHost
    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
    Intent intent;  // Reusable Intent for each tab

    // Create an Intent to launch an Activity for the tab (to be reused)
    intent = new Intent().setClass(this, PhotoCaptureActivity.class);
    // Initialize a TabSpec for each tab and add it to the TabHost
    spec = tabHost.newTabSpec("photo").setIndicator("",res.getDrawable(R.drawable.camera)).setContent(intent);
    tabHost.addTab(spec);

    intent = new Intent().setClass(this, ImageViewActivity.class);
    spec = tabHost.newTabSpec("imageView").setIndicator("",res.getDrawable(R.drawable.icon)).setContent(intent);
    tabHost.addTab(spec);
    
    intent = new Intent().setClass(this, UserPreferenceActivity.class);
    spec = tabHost.newTabSpec("preferences").setIndicator("",res.getDrawable(R.drawable.icon)).setContent(intent);
    tabHost.addTab(spec);
    
    tabHost.setCurrentTab(0);
}
}