package fr.ylecuyer.colazo.activities.bicirevision;

import java.util.Calendar;
import java.util.GregorianCalendar;

import fr.ylecuyer.colazo.BuildConfig;
import fr.ylecuyer.colazo.R;
import fr.ylecuyer.colazo.bdd.BiciRevisionOpenHelper;
import fr.ylecuyer.colazo.helpers.BiciRevisionHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.os.Build;

public class EditLogActivity extends ActionBarActivity {

	private long id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_log);
		// Show the Up button in the action bar.
		setupActionBar();

		id = getIntent().getLongExtra("LOG_ID", -1);

		if (BuildConfig.DEBUG)
			Log.d("BiciRevisión", "LOG_ID: " + id);
		
		BiciRevisionHelper helper = new BiciRevisionHelper(this);

		helper.openBdd();

		Cursor log = helper.getPartLog(id);

		log.moveToFirst();

		EditText txt_description = (EditText)findViewById(R.id.txt_description);
		txt_description.setText(log.getString(log.getColumnIndex(BiciRevisionOpenHelper.COLUMN_DESCRIPTION)));

		EditText txt_reason = (EditText)findViewById(R.id.txt_reason);
		txt_reason.setText(log.getString(log.getColumnIndex(BiciRevisionOpenHelper.COLUMN_REASON)));

		EditText txt_cost = (EditText)findViewById(R.id.txt_cost);
		txt_cost.setText(""+log.getDouble(log.getColumnIndex(BiciRevisionOpenHelper.COLUMN_COST)));

		DatePicker datepicker = (DatePicker)findViewById(R.id.datepicker);

		long milliseconds = log.getLong(log.getColumnIndex(BiciRevisionOpenHelper.COLUMN_TIMESTAMP));
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(milliseconds);;

		int year = cal.get(Calendar.YEAR);
		int monthOfYear = cal.get(Calendar.MONTH);
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

		datepicker.init(year, monthOfYear, dayOfMonth, null);

		helper.closeBdd();

	}

	public void saveLog(View view) {

		EditText txt_description = (EditText)findViewById(R.id.txt_description);
		String description = txt_description.getText().toString();

		//if non empty
		if ("".equalsIgnoreCase(description)) {

			Toast toast = Toast.makeText(this, getString(R.string.description_cant_be_empty), Toast.LENGTH_SHORT);
			toast.show();

			return;
		}

		EditText txt_reason = (EditText)findViewById(R.id.txt_reason);
		String reason = txt_reason.getText().toString();

		//if non empty
		if ("".equalsIgnoreCase(reason)) {

			Toast toast = Toast.makeText(this, getString(R.string.reason_cant_be_empty), Toast.LENGTH_SHORT);
			toast.show();

			return;
		}

		EditText txt_cost = (EditText)findViewById(R.id.txt_cost);

		//if non empty
		if ("".equalsIgnoreCase(txt_cost.getText().toString())) {

			Toast toast = Toast.makeText(this, getString(R.string.cost_cant_be_empty), Toast.LENGTH_SHORT);
			toast.show();

			return;
		}

		double cost = Double.parseDouble(txt_cost.getText().toString());

		DatePicker datepicker = (DatePicker)findViewById(R.id.datepicker);

		GregorianCalendar cal = new GregorianCalendar(datepicker.getYear(), datepicker.getMonth(), datepicker.getDayOfMonth());

		long timestamp = cal.getTimeInMillis();

		BiciRevisionHelper helper = new BiciRevisionHelper(this);

		helper.openBdd();

		helper.editLog(id, description, reason, cost, timestamp);

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
