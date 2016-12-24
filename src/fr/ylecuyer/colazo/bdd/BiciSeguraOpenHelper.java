package fr.ylecuyer.colazo.bdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BiciSeguraOpenHelper extends SQLiteOpenHelper {

	public static final String DBNAME = "BICISEGURA";
	public static final int VERSION = 1;
	public static final String TABLE_NAME = "PERSONAL_DATA";
	public static final String ID = "_ID";
	public static final String LAST_NAMES = "LAST_NAMES";
	public static final String FIRST_NAMES = "FIRS_NAMES";
	public static final String CEDULA = "CEDULA";
	public static final String RH = "RH";
	public static final String EMERGENCY_CONTACT_URI = "EMERGENCY_CONTACT_URI";
	
	

	public BiciSeguraOpenHelper(Context context) {

		super(context, DBNAME, null, VERSION);
		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		
		db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + 
		
				LAST_NAMES + " TEXT," +
				FIRST_NAMES + " TEXT," +
				CEDULA + " TEXT," +
				RH + " TEXT," + 
				EMERGENCY_CONTACT_URI + ", TEXT);"
				);

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
