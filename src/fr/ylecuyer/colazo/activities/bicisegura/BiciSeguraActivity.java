package fr.ylecuyer.colazo.activities.bicisegura;

import com.google.analytics.tracking.android.EasyTracker;

import fr.ylecuyer.colazo.BuildConfig;
import fr.ylecuyer.colazo.R;
import fr.ylecuyer.colazo.helpers.BiciSeguraHelper;
import fr.ylecuyer.colazo.world.PersonalData;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;

public class BiciSeguraActivity extends ActionBarActivity {

	protected static final int CONTACT_RESULT = 0;
	private String[] Rhs = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
	String emergency_contact_uri = "";
	private boolean unsaved_changes = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bici_segura);
		// Show the Up button in the action bar.
		setupActionBar();		
		
		Spinner spinner_RH = (Spinner)findViewById(R.id.spinner_RH);
		
		spinner_RH.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1, Rhs));
				
			
		Button btn_emergency_contact = (Button)findViewById(R.id.btn_emergency_contact);
			
		
		//Set up values
		PersonalData data = null;
		
		BiciSeguraHelper helper = new BiciSeguraHelper(this);
		helper.openBdd();
		data = helper.getPersonalData();
		helper.closeBdd();
		
		EditText last_names = (EditText)findViewById(R.id.edit_apellidos);
		EditText first_names = (EditText)findViewById(R.id.edit_nombres);
		EditText cedula = (EditText)findViewById(R.id.edit_cedula);

		
		if (data != null) {
			
			last_names.setText(data.getLast_names());
	
			first_names.setText(data.getFirst_names());
		
			cedula.setText(data.getCedula());
		
			//RH
			
			int position = 0;
			for (int i = 0; i < Rhs.length; i++) {
				
				if (Rhs[i].equalsIgnoreCase(data.getRH()))
					position = i;
			}
			spinner_RH.setSelection(position);
			
			//emergency contact URI
			if (!"".equalsIgnoreCase(data.getEmergency_contact_URI())) {
				emergency_contact_uri = data.getEmergency_contact_URI(); 
				btn_emergency_contact.setText(getContactName(Uri.parse(data.getEmergency_contact_URI())));
			}
		}
		
		TextWatcher textWatcher = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				unsaved_changes  = true;
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		};
		
		
		last_names.addTextChangedListener(textWatcher);
		first_names.addTextChangedListener(textWatcher);
		cedula.addTextChangedListener(textWatcher);
		spinner_RH.setOnItemSelectedListener(new OnItemSelectedListener() {

			private boolean first = true;

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				if (first) {
					first = false;
					return;
				}
				
				
				if (BuildConfig.DEBUG)
				Log.d("BiciSegura", "Changed");
				unsaved_changes = true;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
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
	
	//On btn_emergency_contact click
	public void chooseContact(View view) {
		
		Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, CONTACT_RESULT);
	}
	
	//On btn_save click
	public void save(View v) {
		
		BiciSeguraHelper helper = new BiciSeguraHelper(this);
		
		helper.openBdd();
						
		String last_names = ((TextView)findViewById(R.id.edit_apellidos)).getText().toString();
		String first_names = ((TextView)findViewById(R.id.edit_nombres)).getText().toString();
		String cedula = ((TextView)findViewById(R.id.edit_cedula)).getText().toString();
		String RH = (String)((Spinner)findViewById(R.id.spinner_RH)).getSelectedItem();
		
		
		helper.setPersonalData(last_names, first_names, cedula, RH, emergency_contact_uri);
		
		helper.closeBdd();
		
		Toast toast = Toast.makeText(this, getString(R.string.personal_data_save_successful), Toast.LENGTH_SHORT);
		toast.show();
		
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == CONTACT_RESULT) {
			
			if (resultCode == RESULT_OK) {
				
				Uri selected_uri = data.getData();
				
				Button btn_emergency_contact = (Button)findViewById(R.id.btn_emergency_contact);
				
				emergency_contact_uri = selected_uri.toString();
				
				btn_emergency_contact.setText(getContactName(selected_uri));
					
				unsaved_changes = true;
			}
			
		}
		
		super.onActivityResult(requestCode, resultCode, data);

	}

	private String getContactName(Uri selected_uri) {
				
		
		Cursor cursor = getContentResolver().query(selected_uri, new String[] {ContactsContract.Data.DISPLAY_NAME}, null, null, null);
		
		String name = "";

		if (cursor.moveToFirst()) {	
			
			int index = cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);		
		
			name = cursor.getString(index);
		}
		
		cursor.close();
		
		return name;
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
			
			if (unsaved_changes == false) {
				NavUtils.navigateUpFromSameTask(BiciSeguraActivity.this);
				return true;
			}
				
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle(getString(R.string.quit_without_saving))
			.setMessage(getString(R.string.last_changes_not_saved))
			.setPositiveButton(android.R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {

					NavUtils.navigateUpFromSameTask(BiciSeguraActivity.this);

				}
			})
			.setNegativeButton(getString(R.string.cancel), new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					dialog.dismiss();

				}
			});

			builder.show();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {

		if (unsaved_changes == false) {
			super.onBackPressed();
			return;
		}
		
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(getString(R.string.quit_without_saving))
		.setMessage(getString(R.string.last_changes_not_saved))
		.setPositiveButton(android.R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {

				BiciSeguraActivity.super.onBackPressed();

			}
		})
		.setNegativeButton(getString(R.string.cancel), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();

			}
		});

		builder.show();
		
	}

}
