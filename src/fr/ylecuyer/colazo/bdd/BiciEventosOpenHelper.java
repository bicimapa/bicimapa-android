package fr.ylecuyer.colazo.bdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BiciEventosOpenHelper extends SQLiteOpenHelper {

	public static final String DBNAME = "BICIEVENTOS";
	public static final int VERSION = 1;
	public static final String BICIEVENTOS_DATA_TABLE = "BICIEVENTOS_DATA";
	public static final String LAST_UPDATE_TIMESTAMP = "LAST_UPDATE_TIMESTAMP";
	public static final String ID = "_ID";
	public static final String BICIEVENTOS_EVENTS_TABLE = "BICIEVENTOS_EVENTS";
	public static final String NAME = "NAME";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String WHEN = "WHEN_DATE";
	public static final String WHERE = "WHERE_PLACE";	
	

	public BiciEventosOpenHelper(Context context) {

		super(context, DBNAME, null, VERSION);
		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		
		db.execSQL("CREATE TABLE " + BICIEVENTOS_DATA_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + 
		
				LAST_UPDATE_TIMESTAMP + " INTEGER" +
				");"
				);
		
		db.execSQL("CREATE TABLE " + BICIEVENTOS_EVENTS_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + 
				
				NAME + " STRING," +
				DESCRIPTION + " TEXT," +
				WHEN + " INTEGER," +
				WHERE + " STRING" +
				");"
				);

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
