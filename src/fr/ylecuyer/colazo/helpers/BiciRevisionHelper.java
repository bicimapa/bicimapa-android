package fr.ylecuyer.colazo.helpers;

import java.util.ArrayList;

import fr.ylecuyer.colazo.bdd.BiciRevisionOpenHelper;
import fr.ylecuyer.colazo.world.Bicycle;
import fr.ylecuyer.colazo.world.BicyclePart;
import fr.ylecuyer.colazo.world.LogEntry;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author ylecuyer
 * 
 * Helper in order to manage the BiciRevision SQLite database
 */
public class BiciRevisionHelper {

	/**
	 * SQLite database
	 */
	private SQLiteDatabase bdd = null;

	/**
	 * {@link BiciRevisionOpenHelper}
	 */
	private BiciRevisionOpenHelper openHelper = null;

	/**
	 * Create a new instance of the {@link BiciRevisionHelper}
	 * 
	 * @param context @see {@link Context}
	 */
	public BiciRevisionHelper(Context context) {

		openHelper = new BiciRevisionOpenHelper(context);

	}

	/**
	 * Open the SQLite databse
	 * 
	 * <b>Must be called before any other function which uses the database</b>
	 */
	public void openBdd() {

		bdd = openHelper.getWritableDatabase();

	}

	/**
	 * Close the SQLite database
	 * 
	 * <b>Must be called when the database is not needed anymore</b>
	 */
	public void closeBdd() {

		bdd.close();

	}

	/**
	 * Return the number of bicycles defined in BiciRevision
	 * 
	 * @return Number of bicycles defined in BiciRevision
	 */
	public int getBicycleCount() {

		final String SQL_QUERY = "SELECT COUNT(*) FROM " + BiciRevisionOpenHelper.BICYCLES_TABLE_NAME;

		Cursor cursor = bdd.rawQuery(SQL_QUERY, null);

		int count = 0;

		cursor.moveToFirst();
		count= cursor.getInt(0);
		cursor.close();

		return count;
	}

	/**
	 * 
	 * Search if a bicycle with the given description has been defined in BiciRevision
	 * 
	 * @param description Bicycle's description
	 * @return true if such a bicycle exists false otherwise
	 */
	public boolean existsBicycleWithDescription(String description) {

		Cursor cursor = bdd.query(BiciRevisionOpenHelper.BICYCLES_TABLE_NAME, new String[] {BiciRevisionOpenHelper.COLUMN_DESCRIPTION}, BiciRevisionOpenHelper.COLUMN_DESCRIPTION + " = ?", new String[] {description} , null, null, null);

		boolean exists = false;
		
		if (cursor.getCount() != 0) {
			exists = true;
		}

		cursor.close();

		return exists;
	}

	/**
	 * Add a bicycle to BiciRevision
	 * 
	 * @param description Bicycle's description
	 * @param photo_URI 
	 * @return Newly added bicycle's ID
	 */
	public long addBicyle(String description, String photo_URI) {

		ContentValues values = new ContentValues();

		values.put(BiciRevisionOpenHelper.COLUMN_DESCRIPTION, description);
		values.put(BiciRevisionOpenHelper.COLUMN_PHOTO, photo_URI);
		
		return bdd.insert(BiciRevisionOpenHelper.BICYCLES_TABLE_NAME, null, values);

	}

	/**
	 * Returns all bicycles defined in BiciRevision
	 * 
	 * @return Rows of all bicycles defined in BiciRevision
	 */
	public ArrayList<Bicycle> getBicycles() {

		ArrayList<Bicycle> bicycles = new ArrayList<Bicycle>();
		
		
		Cursor cursor = bdd.query(BiciRevisionOpenHelper.BICYCLES_TABLE_NAME, null, null, null, null, null, null);

		while (cursor.moveToNext()) {
			
			Bicycle bicycle = new Bicycle();
			
			bicycle.setDescription(cursor.getString(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_DESCRIPTION)));
			bicycle.setPhoto_URI(cursor.getString(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_PHOTO)));
			bicycle.setID(cursor.getLong(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_ID)));
			
			
			bicycles.add(bicycle);
		}
		
		cursor.close();
		
		return bicycles;
	}
	
	/**
	 * Get bicycle information
	 * 
	 * @param id Bicycle's ID
	 * @return Bicycle information
	 */
	public Bicycle getBicycle(long id) {

		Bicycle bicycle = null;

		Cursor cursor = bdd.query(BiciRevisionOpenHelper.BICYCLES_TABLE_NAME, null, BiciRevisionOpenHelper.COLUMN_ID + " = ? ", new String[] {""+id}, null, null, null);

		if (cursor.getCount() != 0) {

			bicycle = new Bicycle();
			
			cursor.moveToFirst();

			bicycle.setDescription(cursor.getString(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_DESCRIPTION)));
			bicycle.setPhoto_URI(cursor.getString(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_PHOTO)));
			bicycle.setID(cursor.getLong(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_ID)));
		}
		
		cursor.close();

		return bicycle;
	}

	/**
	 * Add a new part to a bicycle
	 * 
	 * @param id Bicycle's ID
	 * @param name Part's name
	 * @return Newly added part's ID
	 */
	public long addPart(long id, String name) {

		ContentValues values = new ContentValues();

		values.put(BiciRevisionOpenHelper.COLUMN_NAME, name);
		values.put(BiciRevisionOpenHelper.COLUMN_BICYCLE_ID, id);

		return bdd.insert(BiciRevisionOpenHelper.PARTS_TABLE_NAME, null, values);
	}

	/**
	 * All parts for a given bicycle
	 * 
	 * @param id Bicycle's ID
	 * @return Rows of all associated parts
	 */
	public ArrayList<BicyclePart> getBicycleParts(long id) {

		ArrayList<BicyclePart> parts = new ArrayList<BicyclePart>();
		
		Cursor cursor = bdd.query(BiciRevisionOpenHelper.PARTS_TABLE_NAME, null, BiciRevisionOpenHelper.COLUMN_BICYCLE_ID + " = ?", new String[] {""+id}, null, null, null);
	
		while (cursor.moveToNext()) {
			
			BicyclePart part = new BicyclePart();
			
			part.setName(cursor.getString(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_NAME)));
			part.setID(cursor.getLong(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_ID)));
			
			parts.add(part);
		}
		
		cursor.close();
		
		return parts;
	}

	/**
	 * Search if a given part has already been defined for a given bicycle
	 * 
	 * @param id Bicycle's ID
	 * @param name Part's name
	 * @return true if a part with that name already exists for that bicycle, false otherwise
	 */
	public boolean existsBicyclePartWithName(long id, String name) {

		Cursor cursor = bdd.query(BiciRevisionOpenHelper.PARTS_TABLE_NAME, null, BiciRevisionOpenHelper.COLUMN_BICYCLE_ID + " = ? AND " + BiciRevisionOpenHelper.COLUMN_NAME + " = ?", new String[] {""+id,  name}, null, null, null);

		boolean exists = false;
		
		if (cursor.getCount() != 0)
			exists = true;

		cursor.close();
		
		return exists;
	}

	/**
	 * Get a given part
	 * 
	 * @param id Part id
	 * @return The given part
	 */
	public BicyclePart getPart(long id) {

		BicyclePart part = null;

		Cursor cursor = bdd.query(BiciRevisionOpenHelper.PARTS_TABLE_NAME, null, BiciRevisionOpenHelper.COLUMN_ID + " = ? ", new String[] {""+id}, null, null, null);

		if (cursor.getCount() != 0) {
	
			part = new BicyclePart();

			cursor.moveToFirst();

			part.setName(cursor.getString(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_NAME)));

		}

		cursor.close();
		
		return part;

	}

	/**
	 * Add a log to a given part
	 * 
	 * @param description Log description
	 * @param reason Reason description
	 * @param cost Cost
	 * @param timestamp Date
	 * @param id Part id
	 * @return Newly created log id
	 */
	public long addLog(String description, String reason, double cost, long timestamp, long id) {


		ContentValues values = new ContentValues();

		values.put(BiciRevisionOpenHelper.COLUMN_DESCRIPTION, description);
		values.put(BiciRevisionOpenHelper.COLUMN_REASON, reason);
		values.put(BiciRevisionOpenHelper.COLUMN_COST, cost);
		values.put(BiciRevisionOpenHelper.COLUMN_TIMESTAMP, timestamp);
		values.put(BiciRevisionOpenHelper.COLUMN_PART_ID, id);

		return bdd.insert(BiciRevisionOpenHelper.LOGS_TABLE_NAME, null, values);

	}
	
	/**
	 * Get logs that belongs to a given part
	 * 
	 * @param id Part id
	 * @return Row of logs associated to the given part
	 */
	public ArrayList<LogEntry> getPartLogs(long id) {

		ArrayList<LogEntry> logs = new ArrayList<LogEntry>();
		
		Cursor cursor = bdd.rawQuery("SELECT " 
				+ BiciRevisionOpenHelper.COLUMN_ID + ", "
				+ BiciRevisionOpenHelper.COLUMN_DESCRIPTION + ", "
				+ BiciRevisionOpenHelper.COLUMN_REASON + ", "
				+ BiciRevisionOpenHelper.COLUMN_COST + ", "
				+ BiciRevisionOpenHelper.COLUMN_TIMESTAMP
				+ " FROM " + BiciRevisionOpenHelper.LOGS_TABLE_NAME
				+ " WHERE " + BiciRevisionOpenHelper.COLUMN_PART_ID + " =  ?"
				, new String[] {""+id});
		
		while (cursor.moveToNext()) {
			
			LogEntry log = new LogEntry();
			
			log.setDescription(cursor.getString(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_DESCRIPTION)));
			log.setReason(cursor.getString(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_REASON)));
			log.setCost(cursor.getDouble(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_COST)));
			log.setTimestamp(cursor.getLong(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_TIMESTAMP)));
			log.setID(cursor.getLong(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_ID)));
			
			logs.add(log);
		}
		
		cursor.close();
		
		return logs;
	}

	/**
	 * Get all logs that belongs to a given bicycle ordered by dates ascending
	 * 
	 * @param id Bicycle id
	 * @return Row of logs associated to the given bicycle ordered by dates ascending
	 */
	public ArrayList<LogEntry> getHistory(long id) {

		ArrayList<LogEntry> logs = new ArrayList<LogEntry>();

		
		Cursor cursor = bdd.rawQuery("SELECT " 
				+ BiciRevisionOpenHelper.COLUMN_ID + ", "
				+ BiciRevisionOpenHelper.COLUMN_DESCRIPTION + ", "
				+ BiciRevisionOpenHelper.COLUMN_REASON + ", "
				+ BiciRevisionOpenHelper.COLUMN_COST + ", "
				+ BiciRevisionOpenHelper.COLUMN_TIMESTAMP
				+ " FROM " + BiciRevisionOpenHelper.LOGS_TABLE_NAME
				+ " WHERE " + BiciRevisionOpenHelper.COLUMN_PART_ID + " IN (SELECT " + BiciRevisionOpenHelper.COLUMN_ID + " FROM " + BiciRevisionOpenHelper.PARTS_TABLE_NAME + " WHERE " + BiciRevisionOpenHelper.COLUMN_BICYCLE_ID + " = ?)"
				+ " ORDER BY " + BiciRevisionOpenHelper.COLUMN_TIMESTAMP + " ASC"
				, new String[] {""+id});
		
		while (cursor.moveToNext()) {
			
			LogEntry log = new LogEntry();
			
			log.setDescription(cursor.getString(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_DESCRIPTION)));
			log.setReason(cursor.getString(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_REASON)));
			log.setCost(cursor.getDouble(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_COST)));
			log.setTimestamp(cursor.getLong(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_COST)));
			log.setID(cursor.getLong(cursor.getColumnIndex(BiciRevisionOpenHelper.COLUMN_ID)));
			
			logs.add(log);
		}
		
		cursor.close();

		return logs;
	}
	
	/**
	 * Delete a bicycle and all parts/logs associated to it
	 * 
	 * @param id Bicycle id
	 */
	public void deleteBicycle(long id) {
		
		ArrayList<BicyclePart> parts = getBicycleParts(id);
		
		for (BicyclePart part: parts) {
			deletePart(part.getID());
		}
				
		bdd.delete(BiciRevisionOpenHelper.BICYCLES_TABLE_NAME, BiciRevisionOpenHelper.COLUMN_ID + " = ?", new String[] {""+id});
	}

	/**
	 * Delete a part and all associated logs
	 * 
	 * @param id Part id
	 */
	public void deletePart(long id) {

		bdd.delete(BiciRevisionOpenHelper.LOGS_TABLE_NAME, BiciRevisionOpenHelper.COLUMN_PART_ID + " = ?", new String[] {""+id});
		bdd.delete(BiciRevisionOpenHelper.PARTS_TABLE_NAME, BiciRevisionOpenHelper.COLUMN_ID + " = ?", new String[] {""+id});
	}

	/**
	 * Delete a log
	 * 
	 * @param id Log id
	 */
	public void deleteLog(long id) {

		bdd.delete(BiciRevisionOpenHelper.LOGS_TABLE_NAME, BiciRevisionOpenHelper.COLUMN_ID + " = ?", new String[] {""+id});
	}

	public void editLog(long id, String description, String reason, double cost, long timestamp) {
		
		ContentValues values = new ContentValues();
		values.put(BiciRevisionOpenHelper.COLUMN_DESCRIPTION, description);
		values.put(BiciRevisionOpenHelper.COLUMN_REASON, reason);
		values.put(BiciRevisionOpenHelper.COLUMN_COST, cost);
		values.put(BiciRevisionOpenHelper.COLUMN_TIMESTAMP, timestamp);
		
		bdd.update(BiciRevisionOpenHelper.LOGS_TABLE_NAME, values, BiciRevisionOpenHelper.COLUMN_ID + " = ?", new String[] {""+id});
		
	}

	public void editPart(long part_id, String part_name) {
		
		ContentValues values = new ContentValues();
		values.put(BiciRevisionOpenHelper.COLUMN_NAME, part_name);
		
		bdd.update(BiciRevisionOpenHelper.PARTS_TABLE_NAME, values, BiciRevisionOpenHelper.COLUMN_ID + " = ?", new String[] {""+part_id});
	}

	public void editBicyle(long bicycle_id, String description, String photo_URI) {

		ContentValues values = new ContentValues();
		values.put(BiciRevisionOpenHelper.COLUMN_DESCRIPTION, description);
		values.put(BiciRevisionOpenHelper.COLUMN_PHOTO, photo_URI);
		
		bdd.update(BiciRevisionOpenHelper.BICYCLES_TABLE_NAME, values, BiciRevisionOpenHelper.COLUMN_ID + " = ?", new String[] {""+bicycle_id});
	}

	public Cursor getPartLog(long id) {
		
		return bdd.query(BiciRevisionOpenHelper.LOGS_TABLE_NAME, null, BiciRevisionOpenHelper.COLUMN_ID + " = ?", new String[] {""+id}, null, null, null);
	}
}
