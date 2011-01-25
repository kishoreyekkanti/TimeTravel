package com.travel.activities;

import java.math.BigDecimal;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.travel.utils.CurrentLocation;
import com.travel.utils.ImageLocationOverlay;

public class MapViewActivity extends MapActivity {

	private GeoPoint geopoint;
	private GeoPoint currentGeoPoint;
	private MapController controller;
	private Location lastBestKnownLocation;
	private LocationManager locationManager;
	String message;
	private Resources resources;
	MapView mapView;
	TextView distance;
	ImageLocationOverlay imageItemOverlay;
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		resources = this.getResources();
		setLocationProviders();
		mapView = (MapView) findViewById(R.id.mapview);
		String latitude = getIntent().getStringExtra("latitude");
		String longitude = getIntent().getStringExtra("longitude");
		message = getIntent().getStringExtra("imageDescription");
		Log.d("MapViewActivity","LastBestKnownLocation "+lastBestKnownLocation);
		
		controller = mapView.getController();
		controller.setZoom(12);
		geopoint = new GeoPoint((int)(Double.parseDouble(latitude) * 1E6),(int) (Double.parseDouble(longitude) * 1E6));
		distance = (TextView)findViewById(R.id.distance);
		
		if(lastBestKnownLocation == null){
			Toast.makeText(MapViewActivity.this, R.string.location_unavailable, Toast.LENGTH_LONG).show();
			controller.animateTo(geopoint);
			controller.setCenter(geopoint);
			distance.setText("Unable to compute");
		}else{
			double currentLatitude = lastBestKnownLocation.getLatitude() != 0 ? lastBestKnownLocation.getLatitude(): 37.422006;
			double currentLongitude = lastBestKnownLocation.getLongitude() != 0 ? lastBestKnownLocation.getLongitude(): -122.084095;
			currentGeoPoint = new GeoPoint((int)(currentLatitude * 1E6),(int) (currentLongitude * 1E6));
			controller.animateTo(currentGeoPoint);
			controller.setCenter(currentGeoPoint);
			distance.setText("Distance "+String.valueOf(round(calculationByDistance(currentGeoPoint,geopoint),3,BigDecimal.ROUND_HALF_UP))+"Km");
		}
		
		List<Overlay> overlays = mapView.getOverlays();
		overlays.clear();
		initialiseOverlays();
		mapView.invalidate();
		
		mapView.setBuiltInZoomControls(true);
		mapView.displayZoomControls(true);
		mapView.setStreetView(true);
	}

	private void setLocationProviders() {
		CurrentLocation currentLocation = new CurrentLocation(locationManager);
		currentLocation.setLocationProvider(locationManager);
		lastBestKnownLocation = currentLocation.getBestLastKnownLocation();
	}
	
	  private void initialiseOverlays() {
		    // Create an ItemizedOverlay to display a list of markers
		    Drawable defaultMarker = getResources().getDrawable(R.drawable.marker);
		    imageItemOverlay = new ImageLocationOverlay(this, defaultMarker);
		 
		    if(currentGeoPoint != null)
		    imageItemOverlay.addOverlayItem(new OverlayItem(currentGeoPoint,"Current Location","Latitude:"+currentGeoPoint.getLatitudeE6()/1000000.0+
		    												" Longitude:"+currentGeoPoint.getLongitudeE6()/1000000.0));
		    if(geopoint != null)
		    imageItemOverlay.addOverlayItem(new OverlayItem(geopoint,"Latitude:"+geopoint.getLatitudeE6()/1000000.0+" Longitude:"+geopoint.getLongitudeE6()/1000000.0,
		    								message));
		    // Add the overlays to the map
		    mapView.getOverlays().add(imageItemOverlay);
		  }
	  
	public double calculationByDistance(GeoPoint StartP, GeoPoint EndP) {
	        double lat1 = StartP.getLatitudeE6()/1E6;
	        double lat2 = EndP.getLatitudeE6()/1E6;
	        double lon1 = StartP.getLongitudeE6()/1E6;
	        double lon2 = EndP.getLongitudeE6()/1E6;
	        double dLat = Math.toRadians(lat2-lat1);
	        double dLon = Math.toRadians(lon2-lon1);
	        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	        Math.sin(dLon/2) * Math.sin(dLon/2);
	        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	        return 6371 * c;
	     }
	
	public double round(double unrounded, int precision, int roundingMode)
	{
	    BigDecimal bd = new BigDecimal(unrounded);
	    BigDecimal rounded = bd.setScale(precision, roundingMode);
	    return rounded.doubleValue();
	}

	
}
