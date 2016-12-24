package fr.ylecuyer.colazo.activities.bicirevision;

import java.util.ArrayList;

import fr.ylecuyer.colazo.R;
import fr.ylecuyer.colazo.adapters.HistoryAdapter;
import fr.ylecuyer.colazo.helpers.BiciRevisionHelper;
import fr.ylecuyer.colazo.world.LogEntry;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.os.Build;

public class ViewHistoryActivity extends ActionBarActivity {

	private ListView list_view;
	private long id;
	private HistoryAdapter adapter;
	private ArrayList<LogEntry> logs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_history);
		// Show the Up button in the action bar.
		setupActionBar();
		
		id = (Long)getIntent().getExtras().get("ID");
		
		list_view = (ListView)findViewById(R.id.list_view_history);
		
		BiciRevisionHelper helper = new BiciRevisionHelper(this);
		
		helper.openBdd();
		
		logs = helper.getHistory(id);
		
		double total = 0;
		
		for (LogEntry log: logs) {
			
			total += log.getCost();

		}
		
		TextView lbl_total = (TextView)findViewById(R.id.lbl_total);
		lbl_total.setText(getString(R.string.lbl_total) + total);
		
		adapter = new HistoryAdapter(this, logs);
		
		list_view.setEmptyView(findViewById(R.id.no_data));
		list_view.setAdapter(adapter);
		
		helper.closeBdd();
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
