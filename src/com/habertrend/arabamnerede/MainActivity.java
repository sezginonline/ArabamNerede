package com.habertrend.arabamnerede;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.*;

public class MainActivity extends Activity {
	
	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    // Create the adView.
	    adView = new AdView(this);
	    adView.setAdUnitId("ca-app-pub-5510488825715939/2137900234");
	    adView.setAdSize(AdSize.BANNER);

	    // Lookup your LinearLayout assuming it's been given
	    // the attribute android:id="@+id/mainLayout".
	    LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout);

	    // Add the adView to it.
	    layout.addView(adView);

	    // Initiate a generic request.
	    AdRequest adRequest = new AdRequest.Builder().build();

	    // Load the adView with the ad request.
	    adView.loadAd(adRequest);
		
	}
	
	@Override
	public void onPause() {
	    adView.pause();
	    super.onPause();
	}

	@Override
	public void onResume() {
	    super.onResume();
	    adView.resume();
	}

	@Override
	public void onDestroy() {
	    adView.destroy();
	    super.onDestroy();
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void openMap(View view, int from) {
		Intent intent = new Intent(this, MapActivity.class);
		Bundle b = new Bundle();
		b.putInt("from", from);
		intent.putExtras(b);
		startActivity(intent);		
	}
	
	public void openMap1(View view) {
		openMap(view, 1);
	}
	
	public void openMap2(View view) {
		openMap(view, 2);
	}

}
