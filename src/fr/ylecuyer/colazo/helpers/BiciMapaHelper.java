package fr.ylecuyer.colazo.helpers;

import java.util.ArrayList;
import java.util.Date;

import fr.ylecuyer.colazo.bdd.BiciMapaOpenHelper;
import fr.ylecuyer.colazo.bdd.BiciSeguraOpenHelper;
import fr.ylecuyer.colazo.world.Place;
import fr.ylecuyer.colazo.world.Point;
import fr.ylecuyer.colazo.world.Route;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/**
 * @author ylecuyer
 *
 * Helper in order to manage the BiciSegura SQLite database
 */
public class BiciMapaHelper {
	
	/**
	 * SQLite databse
	 */
	BiciMapaOpenHelper openHelper = null;
	
	/**
	 * {@link BiciSeguraOpenHelper}
	 */
	private SQLiteDatabase bdd = null;
	
	/**
	 * Create a new instance of the {@link BiciMapaHelper}
	 * 
	 * @param context @see {@link Context}
	 */
	public BiciMapaHelper(Context context) {
	
		openHelper = new BiciMapaOpenHelper(context);
		
	}

	/**
	 * Open the SQLite databse
	 * 
	 * <b>Must be called before any other function which uses the database</b>
	 */
	public void openBdd() {
		
		bdd  = openHelper.getWritableDatabase();
		
	}
	
	public Date getLastUpdate() {
		
		Cursor cursor = bdd.query(BiciMapaOpenHelper.BICIMAPA_DATA_TABLE, null, null, null, null, null, null);
	
		Date date = null;
		
		if (cursor.getCount() > 0) {

			
			cursor.moveToFirst(); 
		
			date = new Date(cursor.getLong(cursor.getColumnIndex(BiciMapaOpenHelper.LAST_UPDATE_TIMESTAMP)));
			
		}
		
		cursor.close();
		
		return date;
	}
	
	public void setLastUpdate(Date date) {
		
		bdd.delete(BiciMapaOpenHelper.BICIMAPA_DATA_TABLE, null, null);
		
		ContentValues values = new ContentValues();
		
		values.put(BiciMapaOpenHelper.LAST_UPDATE_TIMESTAMP, date.getTime());
		
		bdd.insert(BiciMapaOpenHelper.BICIMAPA_DATA_TABLE, null, values);
		
	}
	
	/**
	 * Close the SQLite database
	 * 
	 * <b>Must be called when the database is not needed anymore</b>
	 */
	public void closeBdd() {
		
		bdd.close();
	}

	public ArrayList<Place> getPlaces() {
		
		ArrayList<Place> places = new ArrayList<Place>();
		
		Cursor cursor = bdd.query(BiciMapaOpenHelper.BICIMAPA_PLACES_TABLE, null, null, null, null, null, null);
		
		while (cursor.moveToNext()) {
			
			Place place = new Place();
			
			place.setSitio_id(cursor.getLong(cursor.getColumnIndex(BiciMapaOpenHelper.SITIO_ID)));
			place.setName(cursor.getString(cursor.getColumnIndex(BiciMapaOpenHelper.NAME)));
			place.setDescription(cursor.getString(cursor.getColumnIndex(BiciMapaOpenHelper.DESCRIPTION)));
			place.setLongitude(cursor.getDouble(cursor.getColumnIndex(BiciMapaOpenHelper.LONGITUDE)));
			place.setLatitude(cursor.getDouble(cursor.getColumnIndex(BiciMapaOpenHelper.LATITUDE)));
			place.setCategory(cursor.getString(cursor.getColumnIndex(BiciMapaOpenHelper.CATEGORY)));
			place.setRoute(cursor.getString(cursor.getColumnIndex(BiciMapaOpenHelper.ROUTE)));
			
			places.add(place);
		}
		
		cursor.close();
		
		return places;
	}
	
	public void setPlaces(ArrayList<Place> places) {
		
		bdd.beginTransaction();
		
		bdd.delete(BiciMapaOpenHelper.BICIMAPA_PLACES_TABLE, null, null);
		
		SQLiteStatement statement = bdd.compileStatement("INSERT INTO " + BiciMapaOpenHelper.BICIMAPA_PLACES_TABLE 
				+ "(" + BiciMapaOpenHelper.NAME
				+ "," + BiciMapaOpenHelper.DESCRIPTION
				+ "," + BiciMapaOpenHelper.SITIO_ID
				+ "," + BiciMapaOpenHelper.LONGITUDE
				+ "," + BiciMapaOpenHelper.LATITUDE
				+ "," + BiciMapaOpenHelper.CATEGORY
				+ "," + BiciMapaOpenHelper.ROUTE
				+ ")" + "VALUES"
				+ "(?, ?, ?, ?, ?, ?, ?);");
		
		for (Place place: places) {
						
            statement.clearBindings();

			
			statement.bindString(1, place.getName());
			statement.bindString(2, place.getDescription());
			statement.bindLong(3, place.getSitio_id());
			statement.bindDouble(4, place.getLongitude());
			statement.bindDouble(5, place.getLatitude());
			statement.bindString(6, place.getCategory());
			statement.bindString(7, place.getRoute());
			
			
			statement.execute();
						
		}
		
		bdd.setTransactionSuccessful();
		bdd.endTransaction();
		
	}

	public void setRoutes(ArrayList<Route> routes) {

		bdd.beginTransaction();

		
		bdd.delete(BiciMapaOpenHelper.BICIMAPA_ROUTES_TABLE, null, null);
		bdd.delete(BiciMapaOpenHelper.BICIMAPA_ROUTES_POINTS_TABLE, null, null);
		
		SQLiteStatement statement_route = bdd.compileStatement("INSERT INTO " + BiciMapaOpenHelper.BICIMAPA_ROUTES_TABLE
				+ "(" + BiciMapaOpenHelper.NAME
				+ "," + BiciMapaOpenHelper.DESCRIPTION
				+ "," + BiciMapaOpenHelper.ID
				+ ")" + "VALUES"
				+ "(?, ?, ?);");
		
		SQLiteStatement statement_points = bdd.compileStatement("INSERT INTO " + BiciMapaOpenHelper.BICIMAPA_ROUTES_POINTS_TABLE 
				+ "(" + BiciMapaOpenHelper.LATITUDE
				+ "," + BiciMapaOpenHelper.LONGITUDE
				+ "," + BiciMapaOpenHelper.ROUTE_ID
				+ ")" + "VALUES"
				+ "(?, ?, ?);");
		
		for (Route route: routes) {
						
			statement_route.clearBindings();
			
			statement_route.bindString(1, route.getName());
			statement_route.bindString(2, route.getDescription());
			statement_route.bindLong(3, route.getID());
			
			statement_route.execute();
			
			for (Point point: route.getPoints()) {
								
				statement_points.clearBindings();
				
				statement_points.bindDouble(1, point.getLatitude());
				statement_points.bindDouble(2, point.getLongitude());
				statement_points.bindLong(3, point.getRoute_ID());
				
				statement_points.execute();

			}
			
		}
		
		bdd.setTransactionSuccessful();
		bdd.endTransaction();
		
		
	}

	public ArrayList<Route> getRoutes() {
		
		ArrayList<Route> routes = new ArrayList<Route>();
		
		Cursor cursor = bdd.query(BiciMapaOpenHelper.BICIMAPA_ROUTES_TABLE, null, null, null, null, null, null);
		
		while (cursor.moveToNext()) {
			
			Route route = new Route();
			
			route.setName(cursor.getString(cursor.getColumnIndex(BiciMapaOpenHelper.NAME)));
			route.setDescription(cursor.getString(cursor.getColumnIndex(BiciMapaOpenHelper.DESCRIPTION)));
			route.setID(cursor.getLong(cursor.getColumnIndex(BiciMapaOpenHelper.ID)));
			
			Cursor cursor2 = bdd.query(BiciMapaOpenHelper.BICIMAPA_ROUTES_POINTS_TABLE, null, BiciMapaOpenHelper.ROUTE_ID + " = ?", new String[]{""+route.getID()}, null, null, null);
			
			
			ArrayList<Point> points = new ArrayList<Point>();
			
			while (cursor2.moveToNext()) {
				
				Point point = new Point();
				
				point.setID(cursor2.getLong(cursor2.getColumnIndex(BiciMapaOpenHelper.ID)));
				point.setLatitude(cursor2.getDouble(cursor2.getColumnIndex(BiciMapaOpenHelper.LATITUDE)));
				point.setLongitude(cursor2.getDouble(cursor2.getColumnIndex(BiciMapaOpenHelper.LONGITUDE)));
				point.setRoute_ID(cursor2.getInt(cursor2.getColumnIndex(BiciMapaOpenHelper.ROUTE_ID)));
				
				points.add(point);
			}
			
			route.setPoints(points);
			
			cursor2.close();
			
			routes.add(route);
		}
		
		cursor.close();
		
		return routes;
		
	}

	public void setCiclovias(ArrayList<Route> ciclovias) {

		bdd.beginTransaction();

		
		bdd.delete(BiciMapaOpenHelper.BICIMAPA_CICLOVIAS_TABLE, null, null);
		bdd.delete(BiciMapaOpenHelper.BICIMAPA_CICLOVIAS_POINTS_TABLE, null, null);
		
		SQLiteStatement statement_ciclovia = bdd.compileStatement("INSERT INTO " + BiciMapaOpenHelper.BICIMAPA_CICLOVIAS_TABLE
				+ "(" + BiciMapaOpenHelper.NAME
				+ "," + BiciMapaOpenHelper.DESCRIPTION
				+ "," + BiciMapaOpenHelper.ID
				+ ")" + "VALUES"
				+ "(?, ?, ?);");
		
		SQLiteStatement statement_points = bdd.compileStatement("INSERT INTO " + BiciMapaOpenHelper.BICIMAPA_CICLOVIAS_POINTS_TABLE 
				+ "(" + BiciMapaOpenHelper.LATITUDE
				+ "," + BiciMapaOpenHelper.LONGITUDE
				+ "," + BiciMapaOpenHelper.CICLOVIAS_ID
				+ ")" + "VALUES"
				+ "(?, ?, ?);");
		
		for (Route ciclovia: ciclovias) {
						
			statement_ciclovia.clearBindings();
			
			statement_ciclovia.bindString(1, ciclovia.getName());
			statement_ciclovia.bindString(2, ciclovia.getDescription());
			statement_ciclovia.bindLong(3, ciclovia.getID());
			
			statement_ciclovia.execute();
			
			for (Point point: ciclovia.getPoints()) {
								
				statement_points.clearBindings();
				
				statement_points.bindDouble(1, point.getLatitude());
				statement_points.bindDouble(2, point.getLongitude());
				statement_points.bindLong(3, point.getRoute_ID());
				
				statement_points.execute();

			}
			
		}
		
		bdd.setTransactionSuccessful();
		bdd.endTransaction();
		
		
	}

	public ArrayList<Route> getCiclovias() {
		
		ArrayList<Route> ciclovias = new ArrayList<Route>();
		
		Cursor cursor = bdd.query(BiciMapaOpenHelper.BICIMAPA_CICLOVIAS_TABLE, null, null, null, null, null, null);
		
		while (cursor.moveToNext()) {
			
			Route ciclovia = new Route();
			
			ciclovia.setName(cursor.getString(cursor.getColumnIndex(BiciMapaOpenHelper.NAME)));
			ciclovia.setDescription(cursor.getString(cursor.getColumnIndex(BiciMapaOpenHelper.DESCRIPTION)));
			ciclovia.setID(cursor.getLong(cursor.getColumnIndex(BiciMapaOpenHelper.ID)));
			
			Cursor cursor2 = bdd.query(BiciMapaOpenHelper.BICIMAPA_CICLOVIAS_POINTS_TABLE, null, BiciMapaOpenHelper.CICLOVIAS_ID + " = ?", new String[]{""+ciclovia.getID()}, null, null, null);
			
			
			ArrayList<Point> points = new ArrayList<Point>();
			
			while (cursor2.moveToNext()) {
				
				Point point = new Point();
				
				point.setID(cursor2.getLong(cursor2.getColumnIndex(BiciMapaOpenHelper.ID)));
				point.setLatitude(cursor2.getDouble(cursor2.getColumnIndex(BiciMapaOpenHelper.LATITUDE)));
				point.setLongitude(cursor2.getDouble(cursor2.getColumnIndex(BiciMapaOpenHelper.LONGITUDE)));
				point.setRoute_ID(cursor2.getInt(cursor2.getColumnIndex(BiciMapaOpenHelper.CICLOVIAS_ID)));
				
				points.add(point);
			}
			
			ciclovia.setPoints(points);
			
			cursor2.close();
			
			ciclovias.add(ciclovia);
		}
		
		cursor.close();
		
		return ciclovias;
		
	}
}
