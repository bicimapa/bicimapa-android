package fr.ylecuyer.colazo.activities.bicirevision;

import java.util.ArrayList;

import com.google.analytics.tracking.android.EasyTracker;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify.IconValue;

import fr.ylecuyer.colazo.BuildConfig;
import fr.ylecuyer.colazo.R;
import fr.ylecuyer.colazo.adapters.BicycleAdapter;
import fr.ylecuyer.colazo.helpers.BiciRevisionHelper;
import fr.ylecuyer.colazo.world.Bicycle;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class BiciRevisionActivity extends ActionBarActivity {

	private static final int DELETE_ID = 0;
	private static final int EDIT_ID = 1;
	private ListView list_bicycle = null;
	private BicycleAdapter adapter;
	private ArrayList<Bicycle> bicycles;
	private TextView no_data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bici_revision);
		// Show the Up button in the action bar.
		setupActionBar();
		
		final BiciRevisionHelper helper = new BiciRevisionHelper(this);
		
		helper.openBdd();
		
		list_bicycle = (ListView)findViewById(R.id.list_bicycle);
		no_data = (TextView)findViewById(R.id.no_data);
	
		list_bicycle.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
				
				Intent intent = new Intent(BiciRevisionActivity.this, ViewBicycleActivity.class);
				
				//TODO change ID to BYCICLE_ID
				intent.putExtra("ID", id);
				
				startActivity(intent);
			}
		
		
		});
		
		bicycles = helper.getBicycles();

		
		adapter = new BicycleAdapter(this, bicycles);
		
		list_bicycle.setEmptyView(no_data);
		list_bicycle.setAdapter(adapter);
		
		helper.closeBdd();
		
		registerForContextMenu(list_bicycle);
		
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
	
	//On action_add click
	public void addBicycle() {
		
		Intent intent = new Intent(this, AddBycicleActivity.class);
		
		startActivity(intent);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	
		refresh();

	}

	protected void refresh() {
		
		BiciRevisionHelper helper = new BiciRevisionHelper(this);

		helper.openBdd();
		
		bicycles = helper.getBicycles();
						
		adapter.setBicycles(bicycles);
		
		adapter.notifyDataSetChanged();
		
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.bicirevision_activity_action, menu);
	    
		menu.findItem(R.id.action_add).setIcon(new IconDrawable(this, IconValue.fa_plus).actionBarSize());

	    
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			
			addBicycle();
			
			break;
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.add(0, DELETE_ID, 0, getString(R.string.txt_delete));
		menu.add(0, EDIT_ID, 0, getString(R.string.txt_edit));

	}
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) {
		case DELETE_ID:
			
			
			BiciRevisionHelper helper = new BiciRevisionHelper(this);
			
			helper.openBdd();
			
			helper.deleteBicycle(info.id);
			
			helper.closeBdd();
			
			refresh();
			
			return true;
		case EDIT_ID:
			
			Intent intent = new Intent(this, EditBicycleActivity.class);
			
			intent.putExtra("BICYCLE_ID", info.id);
			
			startActivity(intent);
			
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
}
