package fr.ylecuyer.colazo.tasks;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import fr.ylecuyer.colazo.BuildConfig;
import android.os.AsyncTask;
import android.util.Log;

public class PostPlaceTask extends AsyncTask<String, Void, String> {

	private String name;
	private String description;
	private double longitude;
	private double latitude;
	private String category;

	public PostPlaceTask(String name, String description, double longitude,
			double latitude, String category) {

		this.name = name;
		this.description = description;
		this.longitude = longitude;
		this.latitude = latitude;
		this.category = category;
	}

	@Override
	protected String doInBackground(String... params) {

		URL url;
		try {
			url = new URL(params[0]);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setDoOutput(true);

			
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());

			//TODO add sanitization
			String post = "place[title]="+name+"&place[description]="+description+"&place[longitude]="+longitude+"&place[latitude]="+latitude+"&place[category]="+category;
			out.write(post);
			out.close();
			
			int result_code = conn.getResponseCode();

			if (BuildConfig.DEBUG) {
				Log.d("BiciMapa", "Post " + post);
				Log.d("BiciMapa", "Sent " + result_code);
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(String result) {



	}

}
