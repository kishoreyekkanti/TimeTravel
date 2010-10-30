package com.travel.activities;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class ImageViewActivity extends Activity{
	LocationManager locationManager;
	Location location;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_view);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 1000, new MyLocationListener());
        location = getCurrentLocation();
		Log.d("STALENESS",String.valueOf(preferences.getInt("staleness", 1)));
		Log.d("RADIUS",String.valueOf(preferences.getInt("radius", 1)));
		Log.d("Latitude",location!=null?String.valueOf(location.getLatitude()):"");
		Log.d("Longitude",location!=null?String.valueOf(location.getLongitude()):"");
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
