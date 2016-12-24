package fr.ylecuyer.colazo.activities;

import com.google.analytics.tracking.android.EasyTracker;

import fr.ylecuyer.colazo.BuildConfig;
import fr.ylecuyer.colazo.R;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class AboutActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		if (!BuildConfig.DEBUG)
			EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		if (!BuildConfig.DEBUG)
			EasyTracker.getInstance(this).activityStop(this); // Add this method.

	}

	public void share(View view) {
		
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.el_bicimapa));
		intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_msg));
		
		startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
	}
	
	/**
	 * Called on follow_us_on_twitter button click
	 * 
	 * @param view View which throw the onClick event
	 */
	public void followUsOnTwitter(View view) {
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("https://twitter.com/bicimapa"));
		
		startActivity(intent);
	}
	
	/**
	 * Called on follow_us_on_facebook on click
	 * 
	 * @param view View which throw the onClick event
	 */
	public void followUsOnFacebook(View view) {
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("https://www.facebook.com/bicimapa"));
		
		startActivity(intent);
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		else {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
