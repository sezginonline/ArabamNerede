package com.habertrend.arabamnerede;

import java.io.File;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class MapActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
		OnMyLocationButtonClickListener {

	static final int REQUEST_TAKE_PHOTO = 1;

	private GoogleMap mMap;

	private int from;

	private LocationClient mLocationClient;

	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		Bundle b = getIntent().getExtras();
		from = b.getInt("from");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				mMap.setMyLocationEnabled(true);
				mMap.setOnMyLocationButtonClickListener(this);
			}
		}

		if (from == 2) {
			SharedPreferences sharedPref = getSharedPreferences(
					"LocationPreferences", Context.MODE_PRIVATE);
			String latitude = sharedPref.getString("Latitude", "0");
			String longitude = sharedPref.getString("Longitude", "0");

			mMap.addMarker(new MarkerOptions().position(
					new LatLng(Double.parseDouble(latitude), Double
							.parseDouble(longitude))).title(
					getString(R.string.car_pic)));

			mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(Marker arg0) {
		               Intent intent = new Intent(MapActivity.this, ImageActivity.class);
		               startActivity(intent);
				}
			});

		}
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getApplicationContext(), this,
					this);
		}
	}

	/**
	 * Implementation of {@link LocationListener}.
	 */
	@Override
	public void onLocationChanged(Location location) {
		// Do nothing
	}

	/**
	 * Callback called when connected to GCore. Implementation of
	 * {@link ConnectionCallbacks}.
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(REQUEST, this); // LocationListener
	}

	/**
	 * Callback called when disconnected from GCore. Implementation of
	 * {@link ConnectionCallbacks}.
	 */
	@Override
	public void onDisconnected() {
		// Do nothing
	}

	/**
	 * Implementation of {@link OnConnectionFailedListener}.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Do nothing
	}

	@Override
	public boolean onMyLocationButtonClick() {

		if (from == 2) {
			SharedPreferences sharedPref = getSharedPreferences(
					"LocationPreferences", Context.MODE_PRIVATE);
			String latitude = sharedPref.getString("Latitude", "0");
			String longitude = sharedPref.getString("Longitude", "0");

			CameraUpdate center = CameraUpdateFactory
					.newLatLng(new LatLng(Double.parseDouble(latitude), Double
							.parseDouble(longitude)));
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

			mMap.moveCamera(center);
			mMap.animateCamera(zoom);

			return true;
		}

		new CountDownTimer(3000, 1000) {

			public void onTick(long millisUntilFinished) {
				if (mLocationClient != null && mLocationClient.isConnected()) {
					String msg = getString(R.string.location_calc);
					Toast.makeText(getApplicationContext(), msg,
							Toast.LENGTH_SHORT).show();
				}
			}

			public void onFinish() {

				try {

					SharedPreferences sharedPref = getSharedPreferences(
							"LocationPreferences", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPref.edit();

					String latitude = Double.toString(mLocationClient
							.getLastLocation().getLatitude());
					String longitude = Double.toString(mLocationClient
							.getLastLocation().getLongitude());
					String lastLocation = mLocationClient.getLastLocation()
							.toString();

					editor.putString("Latitude", latitude);
					editor.putString("Longitude", longitude);
					editor.putString("LastLocation", lastLocation);

					editor.commit();

					new AlertDialog.Builder(MapActivity.this)
							.setMessage(getString(R.string.take_photo))
							.setCancelable(false)
							.setPositiveButton(getString(R.string.yes),
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {

											Intent takePictureIntent = new Intent(
													MediaStore.ACTION_IMAGE_CAPTURE);
											// Ensure that there's a camera
											// activity
											// to handle the intent
											if (takePictureIntent
													.resolveActivity(getPackageManager()) != null) {
												File photoFile = null;
												try {
													photoFile = new File(
															Environment
																	.getExternalStorageDirectory()
																	.getAbsolutePath()
																	+ File.separator
																	+ "arabam_nerede.jpg");
												} catch (Exception ex) {
													//
												}
												if (photoFile != null) {
													takePictureIntent
															.putExtra(
																	MediaStore.EXTRA_OUTPUT,
																	Uri.fromFile(photoFile));
													startActivityForResult(
															takePictureIntent,
															REQUEST_TAKE_PHOTO);
												}
											}

										}
									}).setNegativeButton(getString(R.string.no), null).show();

				} catch (Exception e) {

					// Needed when back button is pressed

				}

			}
		}.start();

		// Return false so the camera animates
		return false;
	}
}