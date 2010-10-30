package com.travel.utils;



import android.location.Location;
import android.location.LocationManager;

public class CurrentLocation {

public static Location getCurrentLocation(LocationManager locationManager){
	return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
}
	
}
