package fr.ylecuyer.colazo.activities.bicirevision;

import fr.ylecuyer.colazo.R;
import fr.ylecuyer.colazo.helpers.BiciRevisionHelper;
import fr.ylecuyer.colazo.world.BicyclePart;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.os.Build;

public class EditPartActivity extends ActionBarActivity {

	private long part_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_part);
		// Show the Up button in the action bar.
		setupActionBar();

		BiciRevisionHelper helper = new BiciRevisionHelper(this);

		helper.openBdd();

		part_id = getIntent().getLongExtra("PART_ID", -1);
		
		
		BicyclePart part = helper.getPart(part_id);



		EditText edit_name = (EditText)findViewById(R.id.edit_name);
		edit_name.setText(part.getName());

		helper.closeBdd();
	}

	public void saveBicyclePart(View view) {

		BiciRevisionHelper helper = new BiciRevisionHelper(this);

		helper.openBdd();



		EditText edit_name = (EditText)findViewById(R.id.edit_name);
		String part_name = edit_name.getText().toString();

		//if non empty
		if ("".equalsIgnoreCase(part_name)) {

			Toast toast = Toast.makeText(this, getString(R.string.part_name_cant_be_empty), Toast.LENGTH_SHORT);
			toast.show();

			return;
		}


		helper.editPart(part_id, part_name);

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
