package fr.ylecuyer.colazo.activities.bicisegura;

import com.google.analytics.tracking.android.EasyTracker;

import fr.ylecuyer.colazo.BuildConfig;
import fr.ylecuyer.colazo.R;
import fr.ylecuyer.colazo.helpers.BiciSeguraHelper;
import fr.ylecuyer.colazo.world.PersonalData;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class LockScreenActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_screen);
		// Show the Up button in the action bar.
		setupActionBar();

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

		//Set up values
		PersonalData data = null;

		BiciSeguraHelper helper = new BiciSeguraHelper(this);
		helper.openBdd();
		data = helper.getPersonalData();
		helper.closeBdd();

		if (data != null) {

			TextView txt_nombres = (TextView)findViewById(R.id.txt_nombres);
			txt_nombres.setText(data.getFirst_names());

			TextView txt_apellidos = (TextView)findViewById(R.id.txt_apellidos);
			txt_apellidos.setText(data.getLast_names());

			TextView txt_cedula = (TextView)findViewById(R.id.txt_cedula);
			txt_cedula.setText(data.getCedula());

			TextView txt_RH = (TextView)findViewById(R.id.txt_RH);
			txt_RH.setText(data.getRH());
		}
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

	//On btn_emergency_call click
	public void callEmergencyContact(View view) {

		BiciSeguraHelper helper = new BiciSeguraHelper(this);

		helper.openBdd();

		String emergency_contact_uri = helper.get_emergency_contact();	

		helper.closeBdd();

		boolean has_emergency_contact = !"".equalsIgnoreCase(emergency_contact_uri);


		//If emergency contact defined
		if (has_emergency_contact){

			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse(emergency_contact_uri));

			startActivity(intent);

		}
		else {//else

			Toast toast = Toast.makeText(this, getString(R.string.txt_define_emergency_contact), Toast.LENGTH_SHORT);
			toast.show();
		}

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
