package fr.ylecuyer.colazo.tasks;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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

import fr.ylecuyer.colazo.BuildConfig;
import fr.ylecuyer.colazo.R;
import fr.ylecuyer.colazo.activities.bicimapa.BiciMapaActivity;
import fr.ylecuyer.colazo.api.ApiHelper;
import fr.ylecuyer.colazo.helpers.BiciMapaHelper;
import fr.ylecuyer.colazo.world.Place;
import fr.ylecuyer.colazo.world.Point;
import fr.ylecuyer.colazo.world.Route;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class RetrievePlacesTask extends AsyncTask<String, Integer, Void> {


	private String api_key;
	private BiciMapaActivity activity;
	private ArrayList<Place> places;
	private ProgressDialog dialog;
	private ArrayList<Route> routes;
	private ArrayList<Route> ciclovias;


	public RetrievePlacesTask(BiciMapaActivity activity) {

		this.activity = activity;
		this.api_key = ApiHelper.getPass();

	}


	@Override
	protected Void doInBackground(String... params) {

		if (BuildConfig.DEBUG)
			Log.d("BiciMapa", "Launching request");

		publishProgress(-1);

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(params[0]);

		try {
			// Add your data
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("pass", api_key));
			nameValuePairs.add(new BasicNameValuePair("api", "lugares"));
			nameValuePairs.add(new BasicNameValuePair("1", "on"));
			nameValuePairs.add(new BasicNameValuePair("2", "on"));
			nameValuePairs.add(new BasicNameValuePair("3", "on"));
			nameValuePairs.add(new BasicNameValuePair("4", "on"));
			nameValuePairs.add(new BasicNameValuePair("5", "on"));
			nameValuePairs.add(new BasicNameValuePair("6", "on"));
			nameValuePairs.add(new BasicNameValuePair("7", "on"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			ResponseHandler<String> responseHandler=new BasicResponseHandler();
			String response = httpclient.execute(httppost, responseHandler);

			if (BuildConfig.DEBUG)
				Log.d("BiciMapa", response);



			places = new ArrayList<Place>();

			//Load places
			JSONArray jsonArray;
			try {
				jsonArray = new JSONArray(response);

				publishProgress(-2, jsonArray.length());

				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject jsonObject = jsonArray.getJSONObject(i);

					Place place = new Place();

					place.setSitio_id(Long.parseLong(jsonObject.getString("id")));
					place.setName(jsonObject.getString("nombrelimpio"));
					place.setDescription(jsonObject.getString("descripcion"));
					place.setLongitude(jsonObject.getDouble("latitud"));
					place.setLatitude(jsonObject.getDouble("longitud"));

					int tipo = jsonObject.getInt("tipo");

					String category = null;

					switch (tipo) {

					case 1:
						category = "Tienda";
						break;
					case 2:
						category = "Parqueadero";
						break;
					case 3:
						category = "Taller";
						break;
					case 4:
						category = "Ruta";
						break;
					case 5:
						category = "Atencion";
						break;
					case 6:
						category = "ElBicitante";
						break;
					case 7:
						category = "Biciamigo";
						break;

					}

					place.setCategory(category);
					place.setRoute(jsonObject.getString("ruta"));

					places.add(place);

					publishProgress(i+1);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			publishProgress(-1);

			// Create a new HttpClient and Post Header
			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(params[0]);

			// Add your data
			nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("pass", api_key));
			nameValuePairs.add(new BasicNameValuePair("api", "ciclorrutas"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			responseHandler=new BasicResponseHandler();
			response = httpclient.execute(httppost, responseHandler);

			if (BuildConfig.DEBUG)
				Log.d("BiciMapa", response);

			routes = new ArrayList<Route>();

			//Load places
			try {
				jsonArray = new JSONArray(response);

				publishProgress(-2, jsonArray.length());


				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject jsonObject = jsonArray.getJSONObject(i);

					Route route = new Route();


					route.setID(Long.parseLong(jsonObject.getString("id")));
					route.setName(jsonObject.getString("nombre"));
					route.setDescription(jsonObject.getString("descripcion"));

					ArrayList<Point> points = new ArrayList<Point>();

					String point_list = jsonObject.getString("ruta");

					String[] coordinates = point_list.split(";");

					for(String coordinate: coordinates) {

						String[] latlng = coordinate.split(",");

						if (latlng.length == 2) {

							Point point = new Point();

							point.setLatitude(Double.parseDouble(latlng[0]));
							point.setLongitude(Double.parseDouble(latlng[1]));
							point.setRoute_ID(route.getID());

							points.add(point);
						}
					}

					route.setPoints(points);

					routes.add(route);

					publishProgress(i+1);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			publishProgress(-1);

			// Create a new HttpClient and Post Header
			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(params[0]);

			// Add your data
			nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("pass", api_key));
			nameValuePairs.add(new BasicNameValuePair("api", "ciclovias"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			responseHandler=new BasicResponseHandler();
			response = httpclient.execute(httppost, responseHandler);

			if (BuildConfig.DEBUG)
				Log.d("BiciMapa", response);

			ciclovias = new ArrayList<Route>();

			//Load places
			try {
				jsonArray = new JSONArray(response);

				publishProgress(-2, jsonArray.length());


				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject jsonObject = jsonArray.getJSONObject(i);

					Route route = new Route();


					route.setID(Long.parseLong(jsonObject.getString("id")));
					route.setName(jsonObject.getString("nombre"));
					route.setDescription(jsonObject.getString("descripcion"));

					ArrayList<Point> points = new ArrayList<Point>();

					String point_list = jsonObject.getString("ruta");

					String[] coordinates = point_list.split(";");

					for(String coordinate: coordinates) {

						String[] latlng = coordinate.split(",");

						if (latlng.length == 2) {

							Point point = new Point();

							point.setLatitude(Double.parseDouble(latlng[0]));
							point.setLongitude(Double.parseDouble(latlng[1]));
							point.setRoute_ID(route.getID());

							points.add(point);
						}
					}

					route.setPoints(points);

					ciclovias.add(route);

					publishProgress(i+1);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			publishProgress(-3, 3);

			//Save places
			BiciMapaHelper helper = new BiciMapaHelper(activity);

			helper.openBdd();


			helper.setPlaces(places);
			publishProgress(1);
			helper.setRoutes(routes);
			publishProgress(2);
			helper.setCiclovias(ciclovias);
			publishProgress(3);

			helper.closeBdd();


		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);


		if (values[0] == -1) {

			if (dialog != null) dialog.dismiss();

			dialog = new ProgressDialog(activity);
			dialog.setIndeterminate(true);
			dialog.setTitle(activity.getString(R.string.downloading));
			dialog.setMessage(activity.getString(R.string.please_wait));
			dialog.show();
		}
		else if (values[0] == -2) {

			if (dialog != null) dialog.dismiss();

			dialog = new ProgressDialog(activity);
			dialog.setIndeterminate(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setMax(values[1]);
			dialog.setTitle(activity.getString(R.string.extracting_data));
			dialog.setMessage(activity.getString(R.string.please_wait));
			dialog.show();

		}
		else if (values[0] == -3) {

			if (dialog != null) dialog.dismiss();

			dialog = new ProgressDialog(activity);
			dialog.setIndeterminate(true);
			dialog.setTitle(activity.getString(R.string.saving_into_db));
			dialog.setMessage(activity.getString(R.string.please_wait));
			dialog.show();

		}
		else {
			dialog.setProgress(values[0]);
		}
	}

	@Override
	protected void onPostExecute(Void result) {

		activity.setPlaces(places);
		activity.setCiclovias(ciclovias);
		activity.setRoutes(routes);

		dialog.dismiss();

		if (BuildConfig.DEBUG)
			Log.d("BiciMapa", "API: DONE");

	}

}
