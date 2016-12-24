package fr.ylecuyer.colazo.bdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BiciMapaOpenHelper extends SQLiteOpenHelper {

	public static final String DBNAME = "BICIMAPA";
	public static final int VERSION = 6;
	public static final String BICIMAPA_DATA_TABLE = "BICIMAPA_DATA";
	public static final String LAST_UPDATE_TIMESTAMP = "LAST_UPDATE_TIMESTAMP";
	public static final String ID = "_ID";
	public static final String BICIMAPA_PLACES_TABLE = "BICIMAPA_PLACES";
	public static final String NAME = "NAME";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String LATITUDE = "LATITUDE";
	public static final String LONGITUDE = "LONGITUDE";
	public static final String CATEGORY = "CATEGORY";
	public static final String ROUTE_ID = "ROUTE_ID";
	public static final String BICIMAPA_ROUTES_TABLE = "BICIMAPA_ROUTES";
	public static final String BICIMAPA_ROUTES_POINTS_TABLE = "BICIMAPA_ROUTES_POINTS";
	public static final String ROUTE = "ROUTE";
	public static final String SITIO_ID = "SITIO_ID";
	public static final String BICIMAPA_CICLOVIAS_TABLE = "BICIMAPA_CICLOVIAS";
	public static final String BICIMAPA_CICLOVIAS_POINTS_TABLE = "BICIMAPA_CICLOVIAS_POINTS";
	public static final String CICLOVIAS_ID = "CICLOVIAS_ID";
	
	

	public BiciMapaOpenHelper(Context context) {

		super(context, DBNAME, null, VERSION);
		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		
		db.execSQL("CREATE TABLE " + BICIMAPA_DATA_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + 
		
				LAST_UPDATE_TIMESTAMP + " INTEGER" +
				");"
				);
		
		db.execSQL("CREATE TABLE " + BICIMAPA_PLACES_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + 
				
				SITIO_ID + " INTEGER," +
				NAME + " STRING," +
				DESCRIPTION + " TEXT," +
				LATITUDE + " DOUBLE," +
				LONGITUDE + " DOUBLE," +
				CATEGORY + " STRING," +
				ROUTE + " STRING" +
				");"
				);
		
			db.execSQL("CREATE TABLE " + BICIMAPA_ROUTES_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + 
					
				NAME + " STRING," +
				DESCRIPTION + " TEXT" +
				");"
				);
			
			db.execSQL("CREATE TABLE " + BICIMAPA_ROUTES_POINTS_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + 
			
				LATITUDE + " DOUBLE," +
				LONGITUDE + " DOUBLE," +
				ROUTE_ID + " INTEGER" +
				");"
				);
			
			db.execSQL("CREATE TABLE " + BICIMAPA_CICLOVIAS_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + 
					
				NAME + " STRING," +
				DESCRIPTION + " TEXT" +
				");"
				);
			
			db.execSQL("CREATE TABLE " + BICIMAPA_CICLOVIAS_POINTS_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + 
			
				LATITUDE + " DOUBLE," +
				LONGITUDE + " DOUBLE," +
				CICLOVIAS_ID + " INTEGER" +
				");"
				);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		if (newVersion >= 2 && oldVersion < 2) {
			
			db.execSQL("DROP TABLE IF EXISTS " + BICIMAPA_DATA_TABLE);
			
		}

		if (newVersion >= 3 && oldVersion < 3) {
				
			db.execSQL("CREATE TABLE " + BICIMAPA_ROUTES_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + 
				
				NAME + " STRING," +
				DESCRIPTION + " TEXT" +
				");"
				);
			
			db.execSQL("CREATE TABLE " + BICIMAPA_ROUTES_POINTS_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + 
			
				LATITUDE + " DOUBLE," +
				LONGITUDE + " DOUBLE," +
				ROUTE_ID + " INTEGER" +
				");"
				);
			
		}
		
		if (newVersion >= 4 && oldVersion < 4) {
			
			db.execSQL("ALTER TABLE " + BICIMAPA_PLACES_TABLE + " ADD COLUMN " + ROUTE + " STRING;");
			
		}
		
		if (newVersion >= 5 && oldVersion < 5) {
			
			db.execSQL("ALTER TABLE " + BICIMAPA_PLACES_TABLE + " ADD COLUMN " + SITIO_ID + " INTEGER;");
			
		}
		
		if (newVersion >= 6 && oldVersion < 6) {
			
			db.execSQL("CREATE TABLE " + BICIMAPA_CICLOVIAS_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + 
				
				NAME + " STRING," +
				DESCRIPTION + " TEXT" +
				");"
				);
			
			db.execSQL("CREATE TABLE " + BICIMAPA_CICLOVIAS_POINTS_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + 
			
				LATITUDE + " DOUBLE," +
				LONGITUDE + " DOUBLE," +
				CICLOVIAS_ID + " INTEGER" +
				");"
				);
			
		}
		
	}

}
