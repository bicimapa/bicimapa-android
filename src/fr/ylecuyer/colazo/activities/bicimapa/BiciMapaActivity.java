package fr.ylecuyer.colazo.activities.bicimapa;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify.IconValue;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import fr.ylecuyer.colazo.BuildConfig;
import fr.ylecuyer.colazo.R;
import fr.ylecuyer.colazo.activities.AboutActivity;
import fr.ylecuyer.colazo.activities.bicieventos.BiciEventosActivity;
import fr.ylecuyer.colazo.activities.bicirevision.BiciRevisionActivity;
import fr.ylecuyer.colazo.activities.bicisegura.BiciSeguraActivity;
import fr.ylecuyer.colazo.activities.bicisegura.LockScreenActivity;
import fr.ylecuyer.colazo.api.ApiHelper;
import fr.ylecuyer.colazo.helpers.BiciMapaHelper;
import fr.ylecuyer.colazo.helpers.BiciSeguraHelper;
import fr.ylecuyer.colazo.tasks.RetrievePlacesTask;
import fr.ylecuyer.colazo.world.Place;
import fr.ylecuyer.colazo.world.Point;
import fr.ylecuyer.colazo.world.Route;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;

public class BiciMapaActivity extends ActionBarActivity implements OnMarkerClickListener, OnItemClickListener {

	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String TALLERES_VISIBLES = "talleres_visibles";
	private static final String TIENDAS_VISIBLES = "tiendas_visibles";
	private static final String PARQUEADEROS_VISIBLES = "parqueaderos_visibles";
	private static final String ADVERTENCIAS_VISIBLES = "advertencias_visibles";
	private static final String RUTAS_VISIBLES = "rutas_visibles";
	private static final String PUNTOS_ELBICITANTE_VISIBLES = "puntos_elbicitante_visibles";
	private static final String BICIAMIGOS_VISIBLES = "biciamigos_visibles";
	private static final String CICLORRUTAS_VISIBLES = "ciclorrutas_visibles";
	private static final String TRAFFIC_VISIBLE = "traffic_visibles";
	private static final String CICLOVIAS_VISIBLES = "ciclovias_visible";

	/**
	 * Substitute you own sender ID here. This is the project number you got
	 * from the API Console, as described in "Getting Started."
	 */
	String SENDER_ID = "124991657290";

	private ArrayList<Place> places = new ArrayList<Place>();
	private GoogleMap map;
	private ArrayList<Marker> tienda_items;
	private ArrayList<Marker> parqueadero_items;
	private ArrayList<Marker> taller_items;
	private ArrayList<Marker> atencion_items;
	private boolean tienda_activated;
	private boolean taller_activated;
	private boolean parqueadero_activated;
	private boolean atencion_activated;
	private ArrayList<Polyline> routes_items;
	private ArrayList<Route> routes = new ArrayList<Route>();
	private boolean routes_activated;

	private ArrayList<Polyline> ciclovias_items;
	private ArrayList<Route> ciclovias = new ArrayList<Route>();
	private boolean ciclovias_activated;


	private String[] drawerListViewItems;
	private ListView drawerListView;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ArrayList<Marker> rutas_items;
	private boolean rutas_activated;
	private ArrayList<Marker> elbicitante_items;
	private boolean elbicitante_activated;
	private SharedPreferences sharedPreferences;
	private GoogleCloudMessaging gcm;
	private String regid;
	private Context context;
	private boolean trafic_enabled;
	private SlidingUpPanelLayout sliding_layout;

	private boolean currently_adding_place = false;
	private Marker adding_place_marker;
	private Polyline route;
	private boolean updating_for_update = false;
	private boolean old_taller_activated;
	private boolean old_tienda_activated;
	private boolean old_parqueadero_activated;
	private boolean old_atencion_activated;
	private boolean old_rutas_activated;
	private boolean old_elbicitante_activated;
	private boolean old_routes_activated;
	private boolean old_trafic_activated;
	private long selected_item;
	private ArrayList<Marker> biciamigos_items;
	private boolean biciamigos_activated;
	private boolean old_biciamigos_activated;
	private boolean old_ciclovias_activated;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_bici_mapa);


		initSlidingLayout();

		manageGCM();

		setupDrawer();

		manageRating();

		manageWhatsNew();

		initMap();

		manageAlerts();

	}
	
	@Override
	public void onBackPressed() {
		
		if (BuildConfig.DEBUG)
			Log.d("Bicimapa", ""+sliding_layout.isExpanded());
		
		if (sliding_layout.isExpanded()) {
			//collapse
			sliding_layout.collapsePane();
		}
		else {
			//quit activity
			finish();
		}
		
		
	}

	private void initSlidingLayout() {
		sliding_layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		sliding_layout.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));
		sliding_layout.setDragView(findViewById(R.id.trigger));
		sliding_layout.setSlidingEnabled(false);
	}

	private void manageAlerts() {

        Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //Cancel vibrate if it is vibrating
        mVibrator.cancel();
		
		double alert_lat = getIntent().getDoubleExtra("alert_lat", 0);
		double alert_lng = getIntent().getDoubleExtra("alert_lng", 0);
		String alert_type = getIntent().getStringExtra("alert_type");
 		
		if (alert_lat != 0 && alert_lng != 0) {



			if (BuildConfig.DEBUG)
				Log.d("BiciMapa", "Received parameters " + alert_lat + " " + alert_lng);

			MarkerOptions options = new MarkerOptions();

			if (alert_type.equalsIgnoreCase("falla_mecanica"))
				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.taller));
			else 
				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.atencion));

			LatLng position= new LatLng(alert_lat, alert_lng);
			options.position(position);


			map.addMarker(options);

			parqueadero_activated = false;
			set_markers(parqueadero_items, parqueadero_activated);
			
			elbicitante_activated = false;
			set_markers(elbicitante_items, elbicitante_activated);
		}

	}

	private void manageGCM() {
		context = getApplicationContext();

		gcm = GoogleCloudMessaging.getInstance(this);
		regid = getRegistrationId(context);

		if (regid.length() == 0) {
			registerInBackground();
		}
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					// You should send the registration ID to your server over HTTP,
					// so it can use GCM/HTTP or CCS to send messages to your app.
					// The request to your server should be authenticated if your app
					// is using accounts.
					sendRegistrationIdToBackend();

					// For this demo: we don't need to send it because the device
					// will send upstream messages to a server that echo back the
					// message using the 'from' address in the message.


				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.d("BiciMapa GCM", msg + "\n");
			}
		}.execute(null, null, null);


	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	 * or CCS to send messages to your app. Not needed for this demo since the
	 * device sends upstream messages to a server that echoes back the message
	 * using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				Log.d("BiciMapa", "Sengind to backend " + regid);

				// Create a new HttpClient and Post Header
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://colazo-gcm.aws.af.cm/register_new_phone.php");

				try {
					// Add your data
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("registration_id", regid));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					// Execute HTTP Post Request
					ResponseHandler<String> responseHandler=new BasicResponseHandler();
					String response = httpclient.execute(httppost, responseHandler);

					Log.d("BiciMapa", "Response: " + response);

					// Persist the regID - no need to register again.
					storeRegistrationId(context, regid);
				}
				catch (Exception e) {
					e.printStackTrace();
				}




				return null;

			}

		}.execute(null, null);

	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i("BiciMapa", "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.length() == 0) {
			Log.i("BiciMapa", "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i("BiciMapa", "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences, but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(BiciMapaActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	private void manageWhatsNew() {

		sharedPreferences = getPreferences(MODE_PRIVATE);

		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);

			if (sharedPreferences.getLong( "lastRunVersionCode", 0) < pInfo.versionCode ) {
				
				update_data();
				updating_for_update = true;
				
				// TODO: showDialog
				AlertDialog.Builder builder = new AlertDialog.Builder(this);

				builder.setTitle(getString(R.string.whats_new));
				builder.setMessage(getString(R.string.version_3_4_1));

				builder.setPositiveButton(getString(R.string.ok), new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				AlertDialog dialog = builder.create();
				dialog.show();

				Editor editor = sharedPreferences.edit();
				editor.putLong("lastRunVersionCode", pInfo.versionCode);
				editor.commit();
				

				
			}
		} catch (NameNotFoundException e) {
			// TODO Something pretty serious went wrong if you got here...
			e.printStackTrace();
		}
	}

	public void manageRating() {

		int days_until_prompt = 5;
		int launches_until_prompt = 10;

		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

		if (sharedPreferences.getBoolean("dontshowagain", false)) { return ; }

		final Editor editor = sharedPreferences.edit();

		// Increment launch counter
		long launch_count = sharedPreferences.getLong("launch_count", 0) + 1;
		editor.putLong("launch_count", launch_count);

		// Get date of first launch
		Long date_firstLaunch = sharedPreferences.getLong("date_firstlaunch", 0);
		if (date_firstLaunch == 0) {
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong("date_firstlaunch", date_firstLaunch);
		}

		// Wait at least n days before opening
		if (launch_count >= launches_until_prompt) {
			if (System.currentTimeMillis() >= date_firstLaunch + 
					(days_until_prompt * 24 * 60 * 60 * 1000)) {

				//Show dialog
				final Dialog dialog = new Dialog(this);

				dialog.setContentView(R.layout.bicimapa_rate_dialog);
				dialog.setTitle(getString(R.string.rate_title));

				dialog.setCancelable(false);


				Button button_yes = (Button)dialog.findViewById(R.id.button_yes);

				button_yes.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=fr.ylecuyer.colazo")));
						if (editor != null) {
							editor.putBoolean("dontshowagain", true);
							editor.commit();
						}
						dialog.dismiss();
					}
				});

				Button button_no = (Button)dialog.findViewById(R.id.button_no);

				button_no.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (editor != null) {
							editor.putBoolean("dontshowagain", true);
							editor.commit();
						}
						dialog.dismiss();
					}
				});

				Button button_remind_me = (Button)dialog.findViewById(R.id.button_remind_me);
				button_remind_me.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (editor != null) {
							editor.putLong("launch_count", 0);
							editor.commit();
						}
						dialog.dismiss();
					}
				});

				dialog.show();        
			}


		}

		editor.commit();

	}


	private void initMap() {

		FragmentManager fragmentManager = getSupportFragmentManager();
		SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager
				.findFragmentById(R.id.map);
		map = mapFragment.getMap();

		map.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {
				
				hideRoute();
				
				hideItem();
				
				

			}
		});

		map.setMyLocationEnabled(true);
		
		sharedPreferences = getPreferences(MODE_PRIVATE);
		trafic_enabled = sharedPreferences.getBoolean(TRAFFIC_VISIBLE, false);
		
		map.setTrafficEnabled(trafic_enabled);

		BiciMapaHelper helper = new BiciMapaHelper(this);

		if (!updating_for_update ) {

			
			helper.openBdd();
	
			places = helper.getPlaces();			
			routes = helper.getRoutes();
			ciclovias = helper.getCiclovias();
	
			helper.closeBdd();
	
			//Initial update
			if (places.size() == 0 || routes.size() == 0 || ciclovias.size() == 0) {
	
					update_data();
	
			}
	
			if (BuildConfig.DEBUG)
				Log.d("BiciMapa", "There is " + places.size() + " places !");

		}
		
		init_overlays();


		LatLng bogota = new LatLng(4.669, -74.100);		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(bogota , 11));

		Location current = getCurrentPosition();
		if (current != null) {

			LatLng position = new LatLng(current.getLatitude(), current.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));

		}


		map.setOnMarkerClickListener(this);
	}

	private void hideItem() {

		findViewById(R.id.item_selected).setVisibility(View.GONE);
		findViewById(R.id.no_item).setVisibility(View.VISIBLE);
		sliding_layout.setSlidingEnabled(false);
	
		selected_item = -1;
		
	}

	private void setupDrawer() {

		// get list items from strings.xml
		drawerListViewItems = getResources().getStringArray(R.array.drawer_layout_items);

		// get ListView defined in activity_main.xml
		drawerListView = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		drawerListView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_listview_item, drawerListViewItems));

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description */
				R.string.drawer_close  /* "close drawer" description */
				) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				//getActionBar().setTitle(mTitle);
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				//getActionBar().setTitle(mDrawerTitle);
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		drawerListView.setOnItemClickListener(this);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	private void update_data() {

		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();


		//update if connected
		if (isConnected) {

			if (BuildConfig.DEBUG)
				Log.d("BiciMapa", "Retrieving update");

			//TODO Remove hard coded url
			new RetrievePlacesTask(this).execute(ApiHelper.URL);
		}
		else {

			Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();

		}
	}

	public void setPlaces(ArrayList<Place> places) {

		remove_markers();

		this.places = places;

		update_overlays();

	}

	private void remove_markers() {

		for (Marker marker: taller_items)
			marker.remove();

		for (Marker marker: tienda_items)
			marker.remove();

		for (Marker marker: parqueadero_items)
			marker.remove();

		for (Marker marker: atencion_items)
			marker.remove();
		
		for (Marker marker: elbicitante_items)
			marker.remove();
		
		for (Marker marker: rutas_items)
			marker.remove();
	}

	private void update_overlays() {

		/******************** PLACES ********************/

		tienda_items = new ArrayList<Marker>();
		parqueadero_items = new ArrayList<Marker>();
		taller_items = new ArrayList<Marker>();
		atencion_items=  new ArrayList<Marker>();
		rutas_items = new ArrayList<Marker>();
		elbicitante_items = new ArrayList<Marker>();
		biciamigos_items = new ArrayList<Marker>();

		for(int i = 0; i < places.size(); i++) {


			Place place = places.get(i);

			LatLng latlng = new LatLng(place.getLatitude(), place.getLongitude());

			MarkerOptions options = new MarkerOptions();
			options.position(latlng);
			options.snippet(""+i);

			String category = place.getCategory();

			if ("TALLER".equalsIgnoreCase(category)) {

				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.taller));

				Marker marker = map.addMarker(options);
				marker.setVisible(taller_activated);
				taller_items.add(marker);

			}
			else if ("TIENDA".equalsIgnoreCase(category)) {

				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.tienda));

				Marker marker = map.addMarker(options);
				marker.setVisible(tienda_activated);
				tienda_items.add(marker);
			}
			else if ("ATENCION".equalsIgnoreCase(category)) {

				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.atencion));

				Marker marker = map.addMarker(options);
				marker.setVisible(atencion_activated);
				atencion_items.add(marker);
			}
			else if ("PARQUEADERO".equalsIgnoreCase(category)) {

				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.parqueadero));

				Marker marker = map.addMarker(options);
				marker.setVisible(parqueadero_activated);
				parqueadero_items.add(marker);
			}
			else if ("RUTA".equalsIgnoreCase(category)) {

				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ruta));

				Marker marker = map.addMarker(options);
				marker.setVisible(rutas_activated);
				rutas_items.add(marker);
			}
			else if ("ELBICITANTE".equalsIgnoreCase(category)) {

				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.elbicitante));

				Marker marker = map.addMarker(options);
				marker.setVisible(elbicitante_activated);
				elbicitante_items.add(marker);
			}
			else if ("BICIAMIGO".equalsIgnoreCase(category)) {

				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.biciamigo));

				Marker marker = map.addMarker(options);
				marker.setVisible(biciamigos_activated);
				biciamigos_items.add(marker);
			}



		}





		/******************** !PLACES ********************/
	}

	private void init_overlays() {
		
		/******************** ROUTES ********************/

		routes_items = new ArrayList<Polyline>();

	 	routes_activated = sharedPreferences.getBoolean(CICLORRUTAS_VISIBLES, false);

		for (Route route: routes) {

			// Intantiates a new Polyline object and adds points to define a rectangle
			PolylineOptions rectOptions = new PolylineOptions();
			rectOptions.width(5);
			rectOptions.color(getResources().getColor(R.color.ciclorutas));

			for (Point point: route.getPoints()) {

				rectOptions.add(new LatLng(point.getLatitude(), point.getLongitude()));

			}

			Polyline polyline = map.addPolyline(rectOptions);
			polyline.setVisible(routes_activated);
			routes_items.add(polyline);
		}


		/******************** !ROUTES ********************/
		
		/******************** CICLOVIAS ********************/

		ciclovias_items = new ArrayList<Polyline>();

		ciclovias_activated = sharedPreferences.getBoolean(CICLOVIAS_VISIBLES, false);

		for (Route ciclovia: ciclovias) {

			// Intantiates a new Polyline object and adds points to define a rectangle
			PolylineOptions rectOptions = new PolylineOptions();
			rectOptions.width(5);
			rectOptions.color(getResources().getColor(R.color.ciclovias));

			for (Point point: ciclovia.getPoints()) {

				rectOptions.add(new LatLng(point.getLatitude(), point.getLongitude()));

			}

			Polyline polyline = map.addPolyline(rectOptions);
			polyline.setVisible(ciclovias_activated);
			ciclovias_items.add(polyline);
		}


		/******************** !CICLOVIAS ********************/


		/******************** PLACES ********************/

		tienda_items = new ArrayList<Marker>();
		parqueadero_items = new ArrayList<Marker>();
		taller_items = new ArrayList<Marker>();
		atencion_items=  new ArrayList<Marker>();
		rutas_items = new ArrayList<Marker>();
		elbicitante_items = new ArrayList<Marker>();
		biciamigos_items = new ArrayList<Marker>();

		//Starts only with parkings, tiendas and biciamigos

		sharedPreferences = getPreferences(MODE_PRIVATE);
	 	taller_activated = sharedPreferences.getBoolean(TALLERES_VISIBLES, false);
	 	tienda_activated = sharedPreferences.getBoolean(TIENDAS_VISIBLES, true);
	 	parqueadero_activated = sharedPreferences.getBoolean(PARQUEADEROS_VISIBLES, true);
	 	atencion_activated = sharedPreferences.getBoolean(ADVERTENCIAS_VISIBLES, false);
	 	rutas_activated = sharedPreferences.getBoolean(RUTAS_VISIBLES, false);
	 	elbicitante_activated = sharedPreferences.getBoolean(PUNTOS_ELBICITANTE_VISIBLES, false);
	 	biciamigos_activated = sharedPreferences.getBoolean(BICIAMIGOS_VISIBLES, true);
	 	trafic_enabled = sharedPreferences.getBoolean(TRAFFIC_VISIBLE, false);
	 	

		for(int i = 0; i < places.size(); i++) {


			Place place = places.get(i);

			LatLng latlng = new LatLng(place.getLatitude(), place.getLongitude());

			MarkerOptions options = new MarkerOptions();
			options.position(latlng);
			options.snippet(""+i);

			String category = place.getCategory();

			if ("TALLER".equalsIgnoreCase(category)) {

				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.taller));

				Marker marker = map.addMarker(options);
				marker.setVisible(taller_activated);
				taller_items.add(marker);

			}
			else if ("TIENDA".equalsIgnoreCase(category)) {

				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.tienda));

				Marker marker = map.addMarker(options);
				marker.setVisible(tienda_activated);
				tienda_items.add(marker);
			}
			else if ("INFO".equalsIgnoreCase(category)) {

				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.atencion));

				Marker marker = map.addMarker(options);
				marker.setVisible(atencion_activated);
				atencion_items.add(marker);
			}
			else if ("PARQUEADERO".equalsIgnoreCase(category)) {

				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.parqueadero));

				Marker marker = map.addMarker(options);
				marker.setVisible(parqueadero_activated);
				parqueadero_items.add(marker);
			}
			else if ("PELIGRO".equalsIgnoreCase(category)) {

				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.atencion));

				Marker marker = map.addMarker(options);
				marker.setVisible(atencion_activated);
				atencion_items.add(marker);
			}
			else if ("RUTA".equalsIgnoreCase(category)) {

				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ruta));

				Marker marker = map.addMarker(options);
				marker.setVisible(rutas_activated);
				rutas_items.add(marker);
			}
			else if ("ELBICITANTE".equalsIgnoreCase(category)) {

				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.elbicitante));

				Marker marker = map.addMarker(options);
				marker.setVisible(elbicitante_activated);
				elbicitante_items.add(marker);
			}
			else if ("BICIAMIGO".equalsIgnoreCase(category)) {

				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.biciamigo));

				Marker marker = map.addMarker(options);
				marker.setVisible(biciamigos_activated);
				biciamigos_items.add(marker);
			}


		}





		/******************** !PLACES ********************/

	}

	private void set_markers(ArrayList<Marker> markers, boolean visible) {

		for (Marker marker: markers) {
			marker.setVisible(visible);
		}

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		if (!BuildConfig.DEBUG)
			EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		if (!BuildConfig.DEBUG)
			EasyTracker.getInstance(this).activityStop(this); // Add this method.

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public void filter() {


		final CharSequence[] items = {getString(R.string.talleres), getString(R.string.tiendas), getString(R.string.parqueaderos), getString(R.string.atencion), getString(R.string.rutas), getString(R.string.el_bicitante), getString(R.string.biciamigos), getString(R.string.ciclorutas), getString(R.string.ciclovias), getString(R.string.trafico)};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.filter_against));

		boolean[] checked = {taller_activated, tienda_activated, parqueadero_activated, atencion_activated, rutas_activated, elbicitante_activated, biciamigos_activated, routes_activated, ciclovias_activated, trafic_enabled};

		builder.setMultiChoiceItems(items, checked, new OnMultiChoiceClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {

				switch (which) {
				case 0://TALLER
					taller_activated = isChecked;
					set_markers(taller_items, taller_activated);
					break;
				case 1://TIENDA
					tienda_activated = isChecked;
					set_markers(tienda_items, tienda_activated);
					break;
				case 2://PARQUEADERO
					parqueadero_activated = isChecked;
					set_markers(parqueadero_items, parqueadero_activated);
					break;
				case 3://ATENCION
					atencion_activated = isChecked;
					set_markers(atencion_items, atencion_activated);
					break;
				case 4://RUTAS
					rutas_activated= isChecked;
					set_markers(rutas_items, rutas_activated);
					break;
				case 5://ELBICITANTE
					elbicitante_activated = isChecked;
					set_markers(elbicitante_items, elbicitante_activated);
					break;
				case 6://BICIAMIGOS
					biciamigos_activated = isChecked;
					set_markers(biciamigos_items, biciamigos_activated);
					break;
				case 7://CICLORUTAS
					routes_activated = isChecked;
					set_polylines(routes_items, routes_activated);
					break;
				case 8://CICLORUTAS
					ciclovias_activated = isChecked;
					set_polylines(ciclovias_items, ciclovias_activated);
					break;
				case 9://TRAFICO
					trafic_enabled = isChecked;
					map.setTrafficEnabled(trafic_enabled);
					break;

				}
				
				
				

			 	

			}
		});

		builder.setPositiveButton(getString(R.string.ok), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				
				
				
				sharedPreferences = getPreferences(MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();
				
				editor.putBoolean(TALLERES_VISIBLES, taller_activated);
				editor.putBoolean(TIENDAS_VISIBLES, tienda_activated);
				editor.putBoolean(PARQUEADEROS_VISIBLES, parqueadero_activated);
				editor.putBoolean(ADVERTENCIAS_VISIBLES, atencion_activated);
				editor.putBoolean(RUTAS_VISIBLES, rutas_activated);
				editor.putBoolean(PUNTOS_ELBICITANTE_VISIBLES, elbicitante_activated);
				editor.putBoolean(BICIAMIGOS_VISIBLES, biciamigos_activated);
				editor.putBoolean(CICLORRUTAS_VISIBLES, routes_activated);
				editor.putBoolean(CICLOVIAS_VISIBLES, ciclovias_activated);
				editor.putBoolean(TRAFFIC_VISIBLE, trafic_enabled);	
				
				editor.commit();
				
				dialog.dismiss();
			
				hideItem();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();


	}

	private void set_polylines(ArrayList<Polyline> items, boolean isVisible) {

		for (Polyline item: items) {
			item.setVisible(isVisible);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		if (!currently_adding_place) {


			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.bici_mapa, menu);

			menu.findItem(R.id.action_filter).setIcon(new IconDrawable(this, IconValue.fa_filter).actionBarSize());
			menu.findItem(R.id.add_place).setIcon(new IconDrawable(this, IconValue.fa_plus).actionBarSize());
			menu.findItem(R.id.action_update).setIcon(new IconDrawable(this, IconValue.fa_refresh).actionBarSize());


			return true;
		}
		else {

			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.adding_place, menu);

			menu.findItem(R.id.action_valid_place).setIcon(new IconDrawable(this, IconValue.fa_check).actionBarSize());
			menu.findItem(R.id.action_cancel_place).setIcon(new IconDrawable(this, IconValue.fa_times).actionBarSize());			

			return true;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {


		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		if (item.getItemId() == R.id.action_cancel_place) {

			adding_place_marker.remove();
			currently_adding_place = false;
			supportInvalidateOptionsMenu();
			sliding_layout.showPane();
			
			taller_activated = old_taller_activated;
			set_markers(taller_items, taller_activated);
			
			tienda_activated = old_tienda_activated;
			set_markers(tienda_items, tienda_activated);
			
			parqueadero_activated = old_parqueadero_activated;
			set_markers(parqueadero_items, parqueadero_activated);
			
			atencion_activated = old_atencion_activated;
			set_markers(atencion_items, atencion_activated);
			
			rutas_activated = old_rutas_activated;
			set_markers(rutas_items, rutas_activated);
			
			elbicitante_activated = old_elbicitante_activated;
			set_markers(elbicitante_items, elbicitante_activated);
			
			biciamigos_activated = old_biciamigos_activated;
			set_markers(biciamigos_items, biciamigos_activated);
			
			routes_activated = old_routes_activated;
			set_polylines(routes_items, routes_activated);
			
			ciclovias_activated = old_ciclovias_activated;
			set_polylines(ciclovias_items, ciclovias_activated);
			
			trafic_enabled = old_trafic_activated;
			map.setTrafficEnabled(trafic_enabled);
			
			Location current = getCurrentPosition();
			if (current != null) {

				LatLng position = new LatLng(current.getLatitude(), current.getLongitude());
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));

			}
			
		}

		if (item.getItemId() == R.id.action_valid_place) {

			LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

			final View content = inflater.inflate(R.layout.bicimapa_add_place_dialog, null);

			Spinner spinner_type = (Spinner)content.findViewById(R.id.spinner_type);

			String[] types = getResources().getStringArray(R.array.types);

			ArrayAdapter<String> adpater = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1, types);
			spinner_type.setAdapter(adpater);


			//Show dialog
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle(getString(R.string.add_place_title))
			.setView(content)
			.setPositiveButton(android.R.string.ok, null)
			.setNegativeButton(getString(R.string.cancel), new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					dialog.dismiss();

				}
			});

			final AlertDialog alert = builder.create();

			alert.setOnShowListener(new DialogInterface.OnShowListener() {

				@Override
				public void onShow(DialogInterface dialog) {

					Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
					b.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View view) {

							EditText name_edit = (EditText)content.findViewById(R.id.edit_name);
							final String name = name_edit.getText().toString();

							if (name.length() == 0) {
								Toast.makeText(BiciMapaActivity.this, "Name is required", Toast.LENGTH_SHORT).show();
								return;
							}

							EditText description_edit = (EditText)content.findViewById(R.id.edit_description);
							final String description = description_edit.getText().toString();

							if (description.length() == 0) {
								Toast.makeText(BiciMapaActivity.this, "Description is required", Toast.LENGTH_SHORT).show();
								return;
							}

							EditText user_name_edit = (EditText)content.findViewById(R.id.edit_user_name);
							final String user_name = user_name_edit.getText().toString();

							EditText user_email_edit = (EditText)content.findViewById(R.id.edit_user_email);
							final String user_email = user_email_edit.getText().toString();

							Spinner spinner = (Spinner)content.findViewById(R.id.spinner_type);
							final String type = (String)spinner.getSelectedItem();

							final ProgressDialog progress_dialog = ProgressDialog.show(BiciMapaActivity.this, "", 
									getString(R.string.loading), true);

							final LatLng m_position = adding_place_marker.getPosition();

							new AsyncTask<Void, Void, Void>() {

								protected void onPostExecute(Void result) {

									Handler handler = new Handler();

									handler.post(new Runnable() {

										@Override
										public void run() {
											adding_place_marker.remove();
											currently_adding_place = false;
											supportInvalidateOptionsMenu();
											sliding_layout.showPane();
											
											taller_activated = old_taller_activated;
											set_markers(taller_items, taller_activated);
											
											tienda_activated = old_tienda_activated;
											set_markers(tienda_items, tienda_activated);
											
											parqueadero_activated = old_parqueadero_activated;
											set_markers(parqueadero_items, parqueadero_activated);
											
											atencion_activated = old_atencion_activated;
											set_markers(atencion_items, atencion_activated);
											
											rutas_activated = old_rutas_activated;
											set_markers(rutas_items, rutas_activated);
											
											elbicitante_activated = old_elbicitante_activated;
											set_markers(elbicitante_items, elbicitante_activated);
											
											biciamigos_activated = old_biciamigos_activated;
											set_markers(biciamigos_items, biciamigos_activated);
											
											routes_activated = old_routes_activated;
											set_polylines(routes_items, routes_activated);
											
											ciclovias_activated = old_ciclovias_activated;
											set_polylines(ciclovias_items, ciclovias_activated);
											
											trafic_enabled = old_trafic_activated;
											map.setTrafficEnabled(trafic_enabled);
											
											Location current = getCurrentPosition();
											if (current != null) {

												LatLng position = new LatLng(current.getLatitude(), current.getLongitude());
												map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));

											}
										}
									});


									progress_dialog.dismiss();

								};

								@Override
								protected Void doInBackground(Void... params) {


									if (BuildConfig.DEBUG)
										Log.d("BiciMapa", "Sending place");


									int type_int = 0;
									if (type.equalsIgnoreCase("Taller")) {
										type_int = 3;
									}
									else if (type.equalsIgnoreCase("Tienda")) {
										type_int = 1;
									}
									else if (type.equalsIgnoreCase("Parqueadero")) {
										type_int = 2;
									}
									else if (type.equalsIgnoreCase("Atención")) {
										type_int = 5;
									}
									else if (type.equalsIgnoreCase("El Bicictante")) {
										type_int = 6;
									}
									else if (type.equalsIgnoreCase("Biciamigo")) {
										type_int = 7;
									}



									String latlong = m_position.latitude+","+m_position.longitude;

									// Create a new HttpClient and Post Header
									HttpClient httpclient = new DefaultHttpClient();
									HttpPost httppost = new HttpPost("http://www.bicimapa.com"+"/agregar");

									try {
										// Add your data
										ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
										nameValuePairs.add(new BasicNameValuePair("nombre", name));
										nameValuePairs.add(new BasicNameValuePair("tipo", ""+type_int));
										nameValuePairs.add(new BasicNameValuePair("descripcion", description));
										nameValuePairs.add(new BasicNameValuePair("quien", user_name));
										nameValuePairs.add(new BasicNameValuePair("email", user_email));
										nameValuePairs.add(new BasicNameValuePair("latlong", latlong));

										httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

										// Execute HTTP Post Request
										ResponseHandler<String> responseHandler=new BasicResponseHandler();
										String response = httpclient.execute(httppost, responseHandler);

										Log.d("BiciMapa", "Response: " + response);




									}
									catch (Exception e) {
										e.printStackTrace();
									}

									return null;
								}



							}.execute();


							alert.dismiss();
						}
					});
				}
			});

			alert.show();
			alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		}

		if (item.getItemId() == R.id.add_place) {

			//Change action bar items
			currently_adding_place = true;
			supportInvalidateOptionsMenu();
			sliding_layout.hidePane();
			
			old_taller_activated = taller_activated;
			taller_activated = false;
			set_markers(taller_items, taller_activated);
			
			old_tienda_activated = tienda_activated;
			tienda_activated = false;
			set_markers(tienda_items, tienda_activated);
			
			old_parqueadero_activated = parqueadero_activated;
			parqueadero_activated = false;
			set_markers(parqueadero_items, parqueadero_activated);
			
			old_atencion_activated= atencion_activated;
			atencion_activated = false;
			set_markers(atencion_items, atencion_activated);
			
			old_rutas_activated = rutas_activated;
			rutas_activated = false;
			set_markers(rutas_items, rutas_activated);
			
			old_elbicitante_activated = elbicitante_activated;
			elbicitante_activated = false;
			set_markers(elbicitante_items, elbicitante_activated);
			
			old_biciamigos_activated = biciamigos_activated;
			biciamigos_activated = false;
			set_markers(biciamigos_items, biciamigos_activated);
			
			old_routes_activated = routes_activated;
			routes_activated = false;
			set_polylines(routes_items, routes_activated);

			old_ciclovias_activated = ciclovias_activated;
			ciclovias_activated = false;
			set_polylines(ciclovias_items, ciclovias_activated);
			
			old_trafic_activated = trafic_enabled;
			trafic_enabled = false;
			map.setTrafficEnabled(trafic_enabled);
			
			Location current = getCurrentPosition();
			if (current == null) {
				
				current = new Location("map_center");
				LatLng map_center = map.getCameraPosition().target;
				
				current.setLatitude(map_center.latitude);
				current.setLongitude(map_center.longitude);
			}
			
			if (current != null) {

				LatLng position = new LatLng(current.getLatitude(), current.getLongitude());
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));

			}

			//Put dragable marker on map

			MarkerOptions options = new MarkerOptions();
			
			LatLng current_position = new LatLng(current.getLatitude(), current.getLongitude());
			options.position(current_position);
			options.draggable(true);

			adding_place_marker = map.addMarker(options);



			Toast.makeText(this, getString(R.string.long_press_marker), Toast.LENGTH_LONG).show();

			//Put button to say ok
			//Launch add dialog

			//Remove markers on add ok / cancel
			//reset action bar items
		}

		if (item.getItemId() == R.id.action_update) {

			update_data();

		}

		if (item.getItemId() == R.id.action_filter) {

			filter();
		}

		if (item.getItemId() == R.id.help) {

			Dialog dialog = new Dialog(this);

			dialog.setContentView(R.layout.bicimapa_help_dialog);
			dialog.setTitle(getString(R.string.help));

			dialog.show();

			return true;
		}

		if (item.getItemId() == android.R.id.home) {
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

		return false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {

		//The snippet holds the marker id
		String snippet = marker.getSnippet();

		if (snippet != null) {

			int index = Integer.parseInt(marker.getSnippet());

			
			Place place = places.get(index);
			
			selected_item = place.getSitio_id();
			
			
			new AsyncTask<Void, Void, Boolean>() {

				int nb_calificaciones = 0;
				float calificacion = 0;
				
				protected void onPreExecute() {
					
					RatingBar starRating = (RatingBar)findViewById(R.id.starRating);
					starRating.setVisibility(View.INVISIBLE);
					
					TextView rateTxt = (TextView)findViewById(R.id.rateTxt);
					rateTxt.setVisibility(View.INVISIBLE);
					
					ProgressBar pbspinner = (ProgressBar)findViewById(R.id.pbspinner);
					pbspinner.setVisibility(View.VISIBLE);
				}
				
				@Override
				protected Boolean doInBackground(Void... params) {

					// Create a new HttpClient and Post Header
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(ApiHelper.URL);

					try {
						// Add your data
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("pass", ApiHelper.getPass()));
						nameValuePairs.add(new BasicNameValuePair("api", "calificacion"));
						nameValuePairs.add(new BasicNameValuePair("id", ""+selected_item));

						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

						// Execute HTTP Post Request
						ResponseHandler<String> responseHandler=new BasicResponseHandler();
						String response = httpclient.execute(httppost, responseHandler);

						if (BuildConfig.DEBUG)
							Log.d("BiciMapa", "Response: " + response);

						JSONObject obj = new JSONObject(response);

						nb_calificaciones = Integer.parseInt(obj.getString("numero_calificaciones"));
						
						if (nb_calificaciones > 0)
							calificacion = Float.parseFloat(obj.getString("calificacion"));
						
						return true;
					
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
					return false;
				}
				
				protected void onPostExecute(Boolean result) {
					
					if (result) {
						
						RatingBar starRating = (RatingBar)findViewById(R.id.starRating);
						
						starRating.setRating(calificacion);
						starRating.setVisibility(View.VISIBLE);
						
						TextView rateTxt = (TextView)findViewById(R.id.rateTxt);
						rateTxt.setText("("+nb_calificaciones+")");
						rateTxt.setVisibility(View.VISIBLE);
						
						ProgressBar pbspinner = (ProgressBar)findViewById(R.id.pbspinner);
						pbspinner.setVisibility(View.INVISIBLE);
					
					}
				
				}
				
				
			}.execute();
			
			if (place.getCategory().equalsIgnoreCase("RUTA"))
				showRoute(place);
			else 
				hideRoute();


			findViewById(R.id.map).playSoundEffect(SoundEffectConstants.CLICK);
			//AudioManager audioManager = 
			//        (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
			//audioManager.playSoundEffect(SoundEffectConstants.CLICK);   


			TextView name = (TextView)findViewById(R.id.name);
			name.setText(place.getName());

			TextView description = (TextView)findViewById(R.id.description);
			description.setText(Html.fromHtml(place.getDescription()));

			Linkify.addLinks(description, Linkify.ALL);


			//String price = extract_price(place);

			//TextView price_txt = (TextView)findViewById(R.id.price);
			//price_txt.setText("{fa-money} " + price);


			ImageView logo = (ImageView)findViewById(R.id.logo);

			String category = place.getCategory();

			if ("TALLER".equalsIgnoreCase(category)) {

				logo.setImageDrawable(getResources().getDrawable(R.drawable.taller));

			}
			else if ("TIENDA".equalsIgnoreCase(category)) {

				logo.setImageDrawable(getResources().getDrawable(R.drawable.tienda));

			}
			else if ("INFO".equalsIgnoreCase(category)) {

				logo.setImageDrawable(getResources().getDrawable(R.drawable.atencion));

			}
			else if ("PARQUEADERO".equalsIgnoreCase(category)) {

				logo.setImageDrawable(getResources().getDrawable(R.drawable.parqueadero));

			}
			else if ("PELIGRO".equalsIgnoreCase(category)) {

				logo.setImageDrawable(getResources().getDrawable(R.drawable.atencion));

			}
			else if ("RUTA".equalsIgnoreCase(category)) {

				logo.setImageDrawable(getResources().getDrawable(R.drawable.ruta));

			}
			else if ("ELBICITANTE".equalsIgnoreCase(category)) {

				logo.setImageDrawable(getResources().getDrawable(R.drawable.elbicitante));

			}
			else if ("BICIAMIGO".equalsIgnoreCase(category)) {

				logo.setImageDrawable(getResources().getDrawable(R.drawable.biciamigo));

			}



			findViewById(R.id.no_item).setVisibility(View.GONE);
			findViewById(R.id.item_selected).setVisibility(View.VISIBLE);
			sliding_layout.setSlidingEnabled(true);




			/*AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(place.getName())
			.setMessage(place.getDescription())
			.setPositiveButton(BiciMapaActivity.this.getString(R.string.ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					dialog.dismiss();

				}
			});
			AlertDialog alert = builder.create();
			alert.show();*/

		}

		return true;
	}

	private void hideRoute() {

		if (route != null) 
			route.remove();

	}

	private void showRoute(Place place) {

		hideRoute();
		
		PolylineOptions options = new PolylineOptions();
		options.width(5);

		
		
		String json = place.getRoute();

		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(json);


			JSONArray points = jsonObject.getJSONArray("b");

			for (int i = 0; i < points.length(); i++) {

				JSONObject point = points.getJSONObject(i);

				if (BuildConfig.DEBUG)
					Log.d("BiciMapa", point.toString());

				double latitude = 0;
				
				if (point.has("lat"))
					latitude = point.getDouble("lat");
				
				double longitude = 0;
				
				if (point.has("lon"))
					longitude = point.getDouble("lon");
				
				LatLng latlng = new LatLng(latitude, longitude);

				options.add(latlng);

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		route = map.addPolyline(options);

	}

	/*
	private String extract_price(Place place) {

		String description = place.getDescription();
		String name = place.getName();

		//gratuito
		//ningún costo
		//sin costo
		//Publico
		//gratis
		Pattern pattern = Pattern.compile("gratis|gratuito|ning[uú]n costo|sin costo|p[uú]blico", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		if (pattern.matcher(description + " " + name).find())
			return "gratis";


		//10 pesos el minuto
		pattern = Pattern.compile("\\$?\\d+\\s*pesos (el minuto)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);		
		Matcher matcher = pattern.matcher(description);
		if (matcher.find())
			return matcher.group();

		//transmilenio
		//gratis usuario transmilenio
		pattern = Pattern.compile("transmilenio", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		if (pattern.matcher(description + " " + name).find())
			return "gratis usuario transmilenio";

		//$10/min
		                                      |
		                                    * v
		pattern = Pattern.compile("\\$?\\d+\\s/(\\w+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);		
		matcher = pattern.matcher(description);
		if (matcher.find())
			return matcher.group();

		// 1.500 más de una hora
		pattern = Pattern.compile("\\$*\\d+.?\\d+[^.]*m[aá]s de una hora", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);		
		matcher = pattern.matcher(description);
		if (matcher.find())
			return matcher.group();

		// $800[^.]*todo el dia
		//Precio: 500 todo el día
		pattern = Pattern.compile("\\$*\\d+.?\\d+[^.]*todo el d[íi]a", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);		
		matcher = pattern.matcher(description);
		if (matcher.find())
			return matcher.group();

		// $1000
		// $1.000
		// 900 pesos
		pattern = Pattern.compile("\\$\\s*\\d+.?\\d+|\\d+.?\\d+\\s*pesos", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);		
		matcher = pattern.matcher(description);
		if (matcher.find())
			return matcher.group();





		return "Unknown";
	}
	*/
	
	public void setRoutes(ArrayList<Route> routes) {

		remove_routes();

		this.routes = routes;

		update_route_overlay();
	}

	private void update_route_overlay() {

		routes_items = new ArrayList<Polyline>();

		for (Route route: routes) {

			// Intantiates a new Polyline object and adds points to define a rectangle
			PolylineOptions rectOptions = new PolylineOptions();
			rectOptions.width(5);
			rectOptions.color(getResources().getColor(R.color.ciclorutas));

			for (Point point: route.getPoints()) {

				rectOptions.add(new LatLng(point.getLatitude(), point.getLongitude()));

			}

			Polyline polyline = map.addPolyline(rectOptions);
			polyline.setVisible(routes_activated);
			routes_items.add(polyline);
		}

	}

	private void remove_routes() {

		for (Polyline route: routes_items) {

			route.remove();

		}

	}

	public void setCiclovias(ArrayList<Route> ciclovias) {

		remove_ciclovias();

		this.ciclovias = ciclovias;

		update_ciclovias_overlay();
	}

	private void update_ciclovias_overlay() {

		ciclovias_items = new ArrayList<Polyline>();

		for (Route ciclovia: ciclovias) {

			// Intantiates a new Polyline object and adds points to define a rectangle
			PolylineOptions rectOptions = new PolylineOptions();
			rectOptions.width(5);
			rectOptions.color(getResources().getColor(R.color.ciclovias));

			for (Point point: ciclovia.getPoints()) {

				rectOptions.add(new LatLng(point.getLatitude(), point.getLongitude()));

			}

			Polyline polyline = map.addPolyline(rectOptions);
			polyline.setVisible(ciclovias_activated);
			ciclovias_items.add(polyline);
		}

	}

	private void remove_ciclovias() {

		for (Polyline ciclovia: ciclovias_items) {

			ciclovia.remove();

		}

	}
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Intent intent = null;

		switch (position) {

		case 0://BICIREVISION
			intent = new Intent(this, BiciRevisionActivity.class);
			startActivity(intent);
			break;
		case 1://BICISEGURA
			intent = new Intent(this, BiciSeguraActivity.class);
			startActivity(intent);
			break;
		case 2://BICIEVENTOS
			intent = new Intent(this, BiciEventosActivity.class);
			startActivity(intent);
			break;
		case 3://LLAMADA DE EMERGENCIA
			callEmergencyContact();
			break;
		case 4://MOSTRAR LOCKSCREEN
			intent = new Intent(this, LockScreenActivity.class);
			startActivity(intent);
			break;
		case 5://MANDAR ALERTA
			sendAlert();
			break;
		case 6://ABOUT			
			intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			break;
		}


	}

	private void sendAlert() {

		//Dialog to choose alert type
		final CharSequence[] items = {getString(R.string.alert_acidente), getString(R.string.alert_falla_mecanica)};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.alert));

		// -1 means do not select anything
		builder.setSingleChoiceItems(items, -1, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				String alert_type = "";

				switch (which) {
				case 0:
					alert_type = "robo";
					break;
				case 1:
					alert_type = "falla_mecanica";
					break;
				//case 1:
				//	alert_type = "acidente";
					//break;
				}

				dialog.dismiss();


				//Get GPS Coordinate
				Location position = getCurrentPosition();

				if (position == null) {
					
					Toast.makeText(BiciMapaActivity.this, getString(R.string.no_position), Toast.LENGTH_LONG).show();
					
					return;
				}
				
				JSONObject json = new JSONObject();
				try {
					json.put("alert_type", alert_type);
					json.put("lat", position.getLatitude());
					json.put("lng", position.getLongitude());
					json.put("regid", regid);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}


				//Send alert
				new AsyncTask<String, Void, Void>() {

					@Override
					protected Void doInBackground(String... data) {

						String json = data[0];

						if (BuildConfig.DEBUG)
							Log.d("BiciMapa", "Sending alert: " + json);


						// Create a new HttpClient and Post Header
						HttpClient httpclient = new DefaultHttpClient();
						HttpPost httppost = new HttpPost("http://colazo-gcm.aws.af.cm/alert.php");

						try {
							// Add your data
							ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
							nameValuePairs.add(new BasicNameValuePair("data", json));
							httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

							// Execute HTTP Post Request
							ResponseHandler<String> responseHandler=new BasicResponseHandler();
							String response = httpclient.execute(httppost, responseHandler);

							if (BuildConfig.DEBUG)
								Log.d("BiciMapa", "Response: " + response);

						}
						catch (Exception e) {
							e.printStackTrace();
						}

						return null;
					}

				}.execute(json.toString());
			}
		});

		builder.setNegativeButton(getString(R.string.cancel), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();






	}

	protected Location getCurrentPosition() {

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (gps_enabled)
			return lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (network_enabled)
			return lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		//No providers
		return null;
	}

	public void callEmergencyContact() {

		BiciSeguraHelper helper = new BiciSeguraHelper(this);

		helper.openBdd();

		String emergency_contact_uri = helper.get_emergency_contact();	

		helper.closeBdd();

		boolean has_emergency_contact = !"".equalsIgnoreCase(emergency_contact_uri);


		//If emergency contact defined
		if (has_emergency_contact){

			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse(emergency_contact_uri));

			startActivity(intent);

		}
		else {//else

			Toast toast = Toast.makeText(this, getString(R.string.txt_define_emergency_contact), Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	public void rateAndComment(View view) {

		if (selected_item != -1) {
			
			String url = "http://www.bicimapa.com/sitio/"+selected_item;
			
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			startActivity(intent);
		}
		
		
	}

}
