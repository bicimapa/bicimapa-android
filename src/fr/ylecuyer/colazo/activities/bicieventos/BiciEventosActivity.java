package fr.ylecuyer.colazo.activities.bicieventos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.analytics.tracking.android.EasyTracker;

import fr.ylecuyer.colazo.BuildConfig;
import fr.ylecuyer.colazo.R;
import fr.ylecuyer.colazo.adapters.BiciEventosAdapter;
import fr.ylecuyer.colazo.helpers.BiciEventosHelper;
import fr.ylecuyer.colazo.tasks.RetrieveEventsTask;
import fr.ylecuyer.colazo.world.Event;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;

public class BiciEventosActivity extends ActionBarActivity {

	ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bici_eventos);
		// Show the Up button in the action bar.
		setupActionBar();

		listView = (ListView) findViewById(R.id.listView);


		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

		//Check for update if connected
		if (isConnected) {

			//Ask for update
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(this.getString(R.string.bicievento))
			.setMessage(getString(R.string.update_events_question))
			.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {


					//Create progressdialog
					ProgressDialog progress_dialog = ProgressDialog.show(BiciEventosActivity.this, "", 
							getString(R.string.loading), true);


					Date date = new Date();
					SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

					String now = parser.format(date);

					if (BuildConfig.DEBUG)
						Log.d("BiciEventos", "Now = " + now);

					

					//TODO remove hard coded URL
					//TODO remove hard coded googlemapAPI
					new RetrieveEventsTask(BiciEventosActivity.this, listView, progress_dialog).execute("https://www.googleapis.com/calendar/v3/calendars/lagranrodada%40gmail.com/events?orderBy=startTime&singleEvents=true&maxResults=20&timeMin="+now+"&key=YOUR_GOOGLE_API_KEY");


				}
			})
			.setNegativeButton(getString(R.string.no), new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					dialog.dismiss();
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();

		}
		else {

			Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG);

		}





		BiciEventosHelper helper = new BiciEventosHelper(this);

		helper.openBdd();

		ArrayList<Event> events = helper.getEvents();

		if (BuildConfig.DEBUG)
			Log.d("BiciEventos", "There is " + events.size() + " events");

		helper.closeBdd();

		listView.setAdapter(new BiciEventosAdapter(this, events));
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				Event event = (Event) parent.getAdapter().getItem(position);
				
				SpannableString s = new SpannableString(event.getLongDescription());
			    Linkify.addLinks(s, Linkify.ALL);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(BiciEventosActivity.this);
				builder.setTitle(getString(R.string.event_detail))
					.setMessage(s)
					.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {

				        	   dialog.dismiss();
				           
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
				TextView text_view = (TextView)alert.findViewById(android.R.id.message);
				text_view.setMovementMethod(LinkMovementMethod.getInstance());
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bici_eventos, menu);
		return true;
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
