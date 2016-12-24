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
import android.os.Build;

/**
 * @author ylecuyer
 *
 * Activiy that shows the form to add a bicycle part
 */
public class AddBicyclePartActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_bicycle_part);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Adds the new bicycle part on btn_add onClick event
	 * 
	 * @param view View that fired the onClick event
	 */
	public void addBicyclePart(View view) {

		EditText editText = (EditText)findViewById(R.id.edit_name);

		String name = editText.getText().toString();

		//if non empty
		if ("".equalsIgnoreCase(name)) {

			Toast toast = Toast.makeText(this, getString(R.string.part_name_cant_be_empty), Toast.LENGTH_SHORT);
			toast.show();

			return;
		}

		long id = (Long)getIntent().getExtras().get("ID");


		//if !UNIQUE
		BiciRevisionHelper helper = new BiciRevisionHelper(this);
		helper.openBdd();

		boolean exists_bicycle_part_with_name = helper.existsBicyclePartWithName(id, name);

		helper.closeBdd();

		if (exists_bicycle_part_with_name) {

			Toast toast = Toast.makeText(this, getString(R.string.part_name_already_in_use_for_this_bicycle), Toast.LENGTH_SHORT);
			toast.show();

			return;
		}


		helper.openBdd();

		helper.addPart(id, name);

		helper.closeBdd();

		Toast toast = Toast.makeText(this, getString(R.string.part_added_successfully), Toast.LENGTH_SHORT);
		toast.show();

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
