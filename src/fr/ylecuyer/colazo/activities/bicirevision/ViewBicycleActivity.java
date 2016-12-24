package fr.ylecuyer.colazo.activities.bicirevision;

import java.util.ArrayList;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify.IconValue;

import fr.ylecuyer.colazo.R;
import fr.ylecuyer.colazo.adapters.BicyclePartAdapter;
import fr.ylecuyer.colazo.helpers.BiciRevisionHelper;
import fr.ylecuyer.colazo.world.BicyclePart;
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
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class ViewBicycleActivity extends ActionBarActivity {

	private static final int DELETE_ID = 0;
	private static final int EDIT_ID = 1;
	private Long id;
	private BicyclePartAdapter adapter;
	private ListView list_bicycle_parts;
	private ArrayList<BicyclePart> parts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_bicycle);
		// Show the Up button in the action bar.
		setupActionBar();

		id = (Long)getIntent().getExtras().get("ID");

		BiciRevisionHelper helper = new BiciRevisionHelper(this);
			
		list_bicycle_parts = (ListView)findViewById(R.id.list_view_parts);

		helper.openBdd();

		parts = helper.getBicycleParts(id);

		adapter = new BicyclePartAdapter(this, parts);

		list_bicycle_parts.setEmptyView(findViewById(R.id.no_data));
		list_bicycle_parts.setAdapter(adapter);

		list_bicycle_parts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {

				Intent intent = new Intent(ViewBicycleActivity.this, ViewPartActivity.class);

				//TODO change ID to PART_ID
				intent.putExtra("ID", id);

				startActivity(intent);

			}


		});

		helper.closeBdd();


		registerForContextMenu(list_bicycle_parts);
	}

	//On action_add click
	public void addPart() {

		Intent intent = new Intent(this, AddBicyclePartActivity.class);

		intent.putExtra("ID", id);

		startActivity(intent);
	}

	//On action_history click
	public void showHistory() {

		Intent intent = new Intent(this, ViewHistoryActivity.class);

		intent.putExtra("ID", id);

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

		parts = helper.getBicycleParts(id);

		adapter.setParts(parts);

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
	    inflater.inflate(R.menu.bicirevision_add_part_activity_action, menu);
	    
		menu.findItem(R.id.action_add).setIcon(new IconDrawable(this, IconValue.fa_plus).actionBarSize());
		menu.findItem(R.id.action_history).setIcon(new IconDrawable(this, IconValue.fa_clock_o).actionBarSize());

	    
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_history:
			showHistory();
			break;
		case R.id.action_add:
			addPart();
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
		menu.add(0, EDIT_ID, 1, getString(R.string.txt_edit));

	}
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) {
		case DELETE_ID:


			BiciRevisionHelper helper = new BiciRevisionHelper(this);

			helper.openBdd();

			helper.deletePart(info.id);

			helper.closeBdd();

			refresh();

			return true;
		case EDIT_ID:
			
			Intent intent = new Intent(this, EditPartActivity.class);
			
			intent.putExtra("PART_ID", info.id);
			intent.putExtra("BICYCLE_ID", id);
			
			startActivity(intent);
			
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

}
