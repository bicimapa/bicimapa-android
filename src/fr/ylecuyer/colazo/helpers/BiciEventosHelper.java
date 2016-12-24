package fr.ylecuyer.colazo.helpers;

import java.util.ArrayList;
import java.util.Date;

import fr.ylecuyer.colazo.bdd.BiciEventosOpenHelper;
import fr.ylecuyer.colazo.bdd.BiciSeguraOpenHelper;
import fr.ylecuyer.colazo.world.Event;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author ylecuyer
 *
 * Helper in order to manage the BiciSegura SQLite database
 */
public class BiciEventosHelper {
	
	/**
	 * SQLite databse
	 */
	BiciEventosOpenHelper openHelper = null;
	
	/**
	 * {@link BiciSeguraOpenHelper}
	 */
	private SQLiteDatabase bdd = null;
	
	/**
	 * Create a new instance of the {@link BiciEventosHelper}
	 * 
	 * @param context @see {@link Context}
	 */
	public BiciEventosHelper(Context context) {
	
		openHelper = new BiciEventosOpenHelper(context);
		
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
		
		Cursor cursor = bdd.query(BiciEventosOpenHelper.BICIEVENTOS_DATA_TABLE, null, null, null, null, null, null);
	
		Date date = null;
		
		if (cursor.getCount() > 0) {

			
			cursor.moveToFirst(); 
		
			date = new Date(cursor.getLong(cursor.getColumnIndex(BiciEventosOpenHelper.LAST_UPDATE_TIMESTAMP)));
			
		}
		
		cursor.close();
		
		return date;
	}
	
	public void setLastUpdate(Date date) {
		
		bdd.delete(BiciEventosOpenHelper.BICIEVENTOS_DATA_TABLE, null, null);
		
		ContentValues values = new ContentValues();
		
		values.put(BiciEventosOpenHelper.LAST_UPDATE_TIMESTAMP, date.getTime());
		
		bdd.insert(BiciEventosOpenHelper.BICIEVENTOS_DATA_TABLE, null, values);
		
	}
	
	/**
	 * Close the SQLite database
	 * 
	 * <b>Must be called when the database is not needed anymore</b>
	 */
	public void closeBdd() {
		
		bdd.close();
	}

	public ArrayList<Event> getEvents() {
		
		ArrayList<Event> events = new ArrayList<Event>();
		
		Cursor cursor = bdd.query(BiciEventosOpenHelper.BICIEVENTOS_EVENTS_TABLE, null, null, null, null, null, null);
		
		while (cursor.moveToNext()) {
			
			Event event = new Event();
			
			event.setName(cursor.getString(cursor.getColumnIndex(BiciEventosOpenHelper.NAME)));
			event.setLongDescription(cursor.getString(cursor.getColumnIndex(BiciEventosOpenHelper.DESCRIPTION)));
			
			String short_description = event.getLongDescription();
			
			if (short_description.length() > 120)
				short_description = short_description.substring(0, 120) + "...";
			
			event.setShortDescription(short_description);
			
			event.setWhen(cursor.getLong(cursor.getColumnIndex(BiciEventosOpenHelper.WHEN)));
			event.setWhere(cursor.getString(cursor.getColumnIndex(BiciEventosOpenHelper.WHERE)));
			
			events.add(event);
		}
		
		cursor.close();
		
		
		return events;
	}
	
	public void setEvents(ArrayList<Event> events) {
		
		bdd.delete(BiciEventosOpenHelper.BICIEVENTOS_EVENTS_TABLE, null, null);
		
		for (Event event: events) {
			
			
			ContentValues values = new ContentValues();
			
			values.put(BiciEventosOpenHelper.NAME, event.getName());
			values.put(BiciEventosOpenHelper.DESCRIPTION, event.getLongDescription());
			values.put(BiciEventosOpenHelper.WHEN, event.getWhen());
			values.put(BiciEventosOpenHelper.WHERE, event.getWhere());
			
			bdd.insert(BiciEventosOpenHelper.BICIEVENTOS_EVENTS_TABLE, null, values);
			
		}
		
	}
}
