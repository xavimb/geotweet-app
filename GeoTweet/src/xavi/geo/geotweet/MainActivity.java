package xavi.geo.geotweet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements LocationListener {
	private GoogleMap mMap;
	private double lat, lon;
	private Boolean finished = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpMapIfNeeded();
		addMarkers();
		
		getLocation();
		
	}
	private void getLocation()  {

		LocationManager locManager = (LocationManager)getSystemService(
		        Context.LOCATION_SERVICE);
		    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		        5000, 0, this);
		    locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = locManager.getBestProvider(crit, true);
		Location loc = locManager.getLastKnownLocation(provider);
		if (loc != null) {
			lat = loc.getLatitude();
			lon = loc.getLongitude();
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		addMarkers();
		
		getLocation();
	}

	private void setUpMapIfNeeded() {

		if (mMap != null) {
			return;
		}
		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		if (mMap == null) {
			return;
		}
		// Initialize map options. For example:
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.setMyLocationEnabled(true);

		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
				41.4060537, 2.1686564));
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);

		mMap.moveCamera(center);
		mMap.animateCamera(zoom);
	}

	private void addMarkers() {
		ArrayList<LocationPoint> points = new ArrayList<LocationPoint>();
		try {
			points = APIRequest.getAll();
		} catch (IOException e) {
			System.err.println("ERROR FETCHING REQUEST: " + e);
		}
		mMap.clear();
		for (int i = 0; i < points.size(); i++) {
			System.out.println(i);
			mMap.addMarker(new MarkerOptions().position(
					new LatLng(Double.valueOf(points.get(i).x), Double
							.valueOf(points.get(i).y))).title(
					points.get(i).message));
		}
	}

	public void sendTweet(View view) {
		Toast.makeText(getApplicationContext(), "TWEET SENT",
				Toast.LENGTH_SHORT).show();

		new Thread() {
			public void run() {

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						"http://ec2-107-22-139-130.compute-1.amazonaws.com/post");

				EditText taskView = ((EditText) findViewById(R.id.editText1));
				
				String task = taskView.getText().toString();

				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							2);
					nameValuePairs.add(new BasicNameValuePair("task", task));
					nameValuePairs.add(new BasicNameValuePair("x", String
							.valueOf(lat)));
					nameValuePairs.add(new BasicNameValuePair("y", String
							.valueOf(lon)));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);

				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
				synchronized (finished) {
					finished = true;
				}
			}
		}.start();
		while (!finished);
		finished = false;
		
		EditText taskView = ((EditText) findViewById(R.id.editText1));
		taskView.setText("");
		addMarkers();
		
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		lat = location.getLatitude();
		lon = location.getLongitude();
		CameraUpdate center = CameraUpdateFactory
				.newLatLng(new LatLng(lat, lon));
		
		mMap.moveCamera(center);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}
