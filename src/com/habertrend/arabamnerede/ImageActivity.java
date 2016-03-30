package com.habertrend.arabamnerede;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.widget.ImageView;

public class ImageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
		File photoFile = new File(
				Environment
						.getExternalStorageDirectory()
						.getAbsolutePath()
						+ File.separator
						+ "arabam_nerede.jpg");		
		
		ImageView image = (ImageView) findViewById(R.id.imageView1);
		image.setImageURI(Uri.fromFile(photoFile));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image, menu);
		return true;
	}

}
