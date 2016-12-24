package fr.ylecuyer.colazo.helpers;

import fr.ylecuyer.colazo.bdd.BiciSeguraOpenHelper;
import fr.ylecuyer.colazo.world.PersonalData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author ylecuyer
 *
 * Helper in order to manage the BiciSegura SQLite database
 */
public class BiciSeguraHelper {
	
	/**
	 * SQLite databse
	 */
	BiciSeguraOpenHelper openHelper = null;
	
	/**
	 * {@link BiciSeguraOpenHelper}
	 */
	private SQLiteDatabase bdd = null;
	
	/**
	 * Create a new instance of the {@link BiciSeguraHelper}
	 * 
	 * @param context @see {@link Context}
	 */
	public BiciSeguraHelper(Context context) {
	
		openHelper = new BiciSeguraOpenHelper(context);
		
	}

	/**
	 * Open the SQLite databse
	 * 
	 * <b>Must be called before any other function which uses the database</b>
	 */
	public void openBdd() {
		
		bdd  = openHelper.getWritableDatabase();
		
	}
	
	/**
	 * Saves personal data into the database
	 *  
	 * @param last_names User last names
	 * @param first_names User first names
	 * @param cedula User cedula number
	 * @param RH User RH (A+, A-, B+, B-, AB+, AB-, O+, O-)
	 * @param emergency_contact_URI User emergency contact URI
	 */
	public void setPersonalData(String last_names, String first_names, String cedula, String RH, String emergency_contact_URI) {
		
		Cursor cursor = bdd.rawQuery("SELECT COUNT(*) FROM " + BiciSeguraOpenHelper.TABLE_NAME, null);
		
		cursor.moveToFirst();
		
		int nb = cursor.getInt(0);
		
		cursor.close();
		
		ContentValues values = new ContentValues();
		
		values.put(BiciSeguraOpenHelper.LAST_NAMES, last_names);
		values.put(BiciSeguraOpenHelper.FIRST_NAMES, first_names);
		values.put(BiciSeguraOpenHelper.CEDULA, cedula);
		values.put(BiciSeguraOpenHelper.RH, RH);
		values.put(BiciSeguraOpenHelper.EMERGENCY_CONTACT_URI, emergency_contact_URI);
		
		if (nb == 0) { //INSERT
			
			bdd.insert(BiciSeguraOpenHelper.TABLE_NAME, null, values);
			
		}
		else { //UPDATE
					
			bdd.update(BiciSeguraOpenHelper.TABLE_NAME, values, null, null);
			
		}
		
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
	 * Retrieve user personal data from database
	 * 
	 * @return User personal data
	 */
	public PersonalData getPersonalData() {

		PersonalData data = new PersonalData();
		
		Cursor cursor = bdd.rawQuery("SELECT * FROM " + BiciSeguraOpenHelper.TABLE_NAME + " LIMIT 1", null);
		
		if (cursor.getCount() == 0) {
			
			cursor.close();
			
			return null;
			
		}
		else {
			
			cursor.moveToFirst();
			
			data.setLast_names(cursor.getString(cursor.getColumnIndex(BiciSeguraOpenHelper.LAST_NAMES)));
			data.setFirst_names(cursor.getString(cursor.getColumnIndex(BiciSeguraOpenHelper.FIRST_NAMES)));
			data.setCedula(cursor.getString(cursor.getColumnIndex(BiciSeguraOpenHelper.CEDULA)));
			data.setRH(cursor.getString(cursor.getColumnIndex(BiciSeguraOpenHelper.RH)));
			data.setEmergency_contact_URI(cursor.getString(cursor.getColumnIndex(BiciSeguraOpenHelper.EMERGENCY_CONTACT_URI)));
		
			cursor.close();
		}
		
		return data;
	}

	/**
	 * Retrieve user emergency contact
	 * 
	 * @return User emergency contact URI
	 */
	public String get_emergency_contact() {

		PersonalData data = getPersonalData();
		
		if (data != null)
			return data.getEmergency_contact_URI();
		else 
			return "";
		
	}
}
