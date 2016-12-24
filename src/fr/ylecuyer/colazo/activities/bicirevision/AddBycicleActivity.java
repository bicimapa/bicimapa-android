package fr.ylecuyer.colazo.activities.bicirevision;

import fr.ylecuyer.colazo.R;
import fr.ylecuyer.colazo.helpers.BiciRevisionHelper;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class AddBycicleActivity extends ActionBarActivity {

	private static final int PICK_PHOTO = 0;

	private String photo_URI = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_bycicle);
		// Show the Up button in the action bar.
		setupActionBar();		
	}
	
	/**
	 * Select bicycle photo on btn_select_photo onClick event
	 * 
	 * @param view View that fired the onClick event
	 */
	public void selectPhoto(View view) {
		
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		
        intent.setType("image/*");
        
        startActivityForResult(Intent.createChooser(intent, getString(R.string.title_select_photo)), PICK_PHOTO);
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (PICK_PHOTO == requestCode) {
			
			if (RESULT_OK == resultCode) {
				
				photo_URI = data.getDataString();
				
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	//On btn_add click
	public void addBicycle(View v) {

		EditText input_description = (EditText)findViewById(R.id.input_description_bicycle);

		String description = input_description.getText().toString();

		//No empty description
		if ("".equalsIgnoreCase(description)) {

			Toast toast = Toast.makeText(this, getString(R.string.description_bicycle_empty), Toast.LENGTH_SHORT);
			toast.show();

			return;
		}

		BiciRevisionHelper helper = new BiciRevisionHelper(this);
		helper.openBdd();

		//Unique description
		if (helper.existsBicycleWithDescription(description)) {

			Toast toast = Toast.makeText(this, getString(R.string.description_bicycle_already_exists), Toast.LENGTH_SHORT);
			toast.show();

			return;
		}


		//ADD bici
		long id = helper.addBicyle(description, photo_URI);

		if (id == -1) {

			Toast toast = Toast.makeText(this, getString(R.string.add_bicycle_error), Toast.LENGTH_SHORT);
			toast.show();

		}
		else {
			Toast toast = Toast.makeText(this, getString(R.string.add_bicycle_successful), Toast.LENGTH_SHORT);
			toast.show();
		}

		helper.closeBdd();

		finish();

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
