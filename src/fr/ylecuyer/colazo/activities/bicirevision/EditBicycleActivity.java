package fr.ylecuyer.colazo.activities.bicirevision;

import fr.ylecuyer.colazo.R;
import fr.ylecuyer.colazo.helpers.BiciRevisionHelper;
import fr.ylecuyer.colazo.world.Bicycle;
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

public class EditBicycleActivity extends ActionBarActivity {

	private static final int PICK_PHOTO = 0;
	private long bicycle_id;
	private String photo_URI = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_bicycle);
		// Show the Up button in the action bar.
		setupActionBar();

		bicycle_id = getIntent().getLongExtra("BICYCLE_ID", -1);

		BiciRevisionHelper helper = new BiciRevisionHelper(this);

		helper.openBdd();

		Bicycle bicycle = helper.getBicycle(bicycle_id);

		EditText input_description_bicycle = (EditText)findViewById(R.id.input_description_bicycle);
		input_description_bicycle.setText(bicycle.getDescription());

		photo_URI = bicycle.getPhoto_URI();

		helper.closeBdd();
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

				photo_URI  = data.getDataString();

			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public void saveBicycle(View view) {

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


		//ADD bici
		helper.editBicyle(bicycle_id, description, photo_URI);


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
