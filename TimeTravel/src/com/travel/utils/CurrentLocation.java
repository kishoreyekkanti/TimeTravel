package com.travel.utils;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class CurrentLocation {
	private LocationManager locationManager;

	public CurrentLocation(LocationManager locationManager) {
		this.locationManager = locationManager;
	}

	public void setLocationProvider(LocationManager locationManager) {
		if (isGPSSupported()) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 1000, new GPSLocationListener());
			//return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}else if(isNetworkSupported()){
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 1000, new NetworkLocaitonListener());
		}
	}

	private boolean isGPSSupported() {
		boolean gpsSupported = false;
		try {
			gpsSupported = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception e) {
			Log.e("CurrentLocation","Error occured while querying gps availability. "	+ e.getMessage());
		}
		return gpsSupported;
	}
	
	public boolean isNetworkSupported() {
		boolean networkSupported = false;
		try {
		networkSupported = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch(Exception e) {
		Log.e("CurrentLocation", "Error occured while querying network availability. " + e.getMessage());
		}

		return networkSupported;
		}

	
	private class NetworkLocaitonListener implements LocationListener{

		public void onLocationChanged(Location location) {
			String message = String.format(
					"New Location \n Latitude: %1$s \n Longitude: %2$s",
					location.getLatitude(), location.getLongitude()
					);
			Log.d("NetworkLocaitonListener", message);
		}

		public void onProviderDisabled(String provider) {
			Log.d("NetworkLocaitonListener", "Provider disabled by the user, GPS is turned off!");
		}

		public void onProviderEnabled(String provider) {
			Log.d("NetworkLocaitonListener", "Provider enabled by the user, GPS is truned on!");
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d("NetworkLocaitonListener", "Provider status changed!");
			
		}
    	
    }
	
	private class GPSLocationListener implements LocationListener{

		public void onLocationChanged(Location location) {
			String message = String.format(
					"New Location \n Latitude: %1$s \n Longitude: %2$s",
					location.getLatitude(), location.getLongitude()
					);
			Log.d("GPSLocationListener", message);
		}

		public void onProviderDisabled(String provider) {
			Log.d("GPSLocationListener", "Provider disabled by the user, GPS is turned off!");
		}

		public void onProviderEnabled(String provider) {
			Log.d("GPSLocationListener", "Provider enabled by the user, GPS is truned on!");
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d("GPSLocationListener", "Provider status changed!");
			
		}
    	
    }
	
	public Location getBestLastKnownLocation() {
		Location networkLocation =null, gpsLocation =null;
		        if(isGPSSupported())
		            gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		        if(isNetworkSupported())
		            networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		        //if there are both values use the latest one
		        if(gpsLocation != null && networkLocation != null){
		        	Log.d("GPS Location:"+gpsLocation.getTime()," NetworkLocation:"+networkLocation.getTime());
		        	return gpsLocation.getTime() > networkLocation.getTime()? gpsLocation : networkLocation;
		        }

		        //In case one of them was null then use the one that is not null
		        if (gpsLocation != null) {
		         return gpsLocation;
		        }
		        
		        if (networkLocation != null){
		            return networkLocation;
		        }
		        Log.e("CurrentLocation","Unable to determine the current location");
		        return null;
		}	

}
