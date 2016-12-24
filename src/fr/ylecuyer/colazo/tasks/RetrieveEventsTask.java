package fr.ylecuyer.colazo.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.ylecuyer.colazo.BuildConfig;
import fr.ylecuyer.colazo.adapters.BiciEventosAdapter;
import fr.ylecuyer.colazo.helpers.BiciEventosHelper;
import fr.ylecuyer.colazo.world.Event;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

public class RetrieveEventsTask extends AsyncTask<String, Void, String> {

	private Context context;
	private ProgressDialog dialog;
	private ListView listView;

	public RetrieveEventsTask(Context context, ListView listView, ProgressDialog dialog) {

		this.context = context;
		this.listView = listView;
		this.dialog = dialog;
	}

	@Override
	protected String doInBackground(String... params) {

		URL website = null;
		StringBuilder response = new StringBuilder();
		try {
			website = new URL(params[0]);
			URLConnection connection = null;
			connection = website.openConnection();

			BufferedReader in = null;
			in = new BufferedReader(
					new InputStreamReader(
							connection.getInputStream()));


			String inputLine;

			while ((inputLine = in.readLine()) != null) 
				response.append(inputLine);

			in.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




		return response.toString();
	}

	@Override
	protected void onPostExecute(String result) {

		if (BuildConfig.DEBUG)
			Log.d("BiciEventos", result);

		ArrayList<Event> events = new ArrayList<Event>();

		JSONObject root;
		try {

			root = new JSONObject(result);
			JSONArray items = root.getJSONArray("items");

			if(BuildConfig.DEBUG)
				Log.d("BiciEventos", root.getString("summary") + " " + items.length());

			for (int i = 0; i < items.length(); i++) {

				JSONObject json = items.getJSONObject(i);



				Event event = new Event();
				if (json.has("summary"))
					event.setName(json.getString("summary"));
				else 
					event.setName("No summary");
				// TODO ^change to R.string

				if (json.has("description")) {
				
					
					String description = json.getString("description");
				
					event.setLongDescription(description);
					
					if (description.length() > 120)
						description = description.substring(0, 120) + "...";
					
					event.setShortDescription(description);
				
				}
				else {
					event.setShortDescription("No description");
					event.setLongDescription("No description");
				// TODO ^ change to R.string
				}
				
				if (json.has("location"))
					event.setWhere(json.getString("location"));
				else 
					event.setWhere("Unknown");
				//TODO ^ change to R.string


				if (json.has("start")) {
					JSONObject start = json.getJSONObject("start");

					if (start.has("date")) {

						//parse "date": "2013-10-09"
						SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
						Date date = new Date();
						try {
							date = parser.parse(start.getString("date"));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						event.setWhen(date.getTime());
					}
					else if (start.has("dateTime")) {

						//parse "dateTime": "2013-10-12T07:00:00-05:00"

						SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
						Date date = new Date();
						try {
							date = parser.parse(start.getString("dateTime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						event.setWhen(date.getTime());


					}
					else 
						event.setWhen(0);
					// TODO ^display 0 as unknown
				}
				else 
					event.setWhen(0);
				// TODO ^display 0 as unknown

				events.add(event);
			}




		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		
		//Save events
		BiciEventosHelper helper = new BiciEventosHelper(context);

		helper.openBdd();

		helper.setEvents(events);

		helper.closeBdd();

		listView.setAdapter(new BiciEventosAdapter(context, events));
		
		dialog.dismiss();

	}

}
