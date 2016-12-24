/**
 * 
 */
package fr.ylecuyer.colazo.bdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author ylecuyer
 *
 */
public class BiciRevisionOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "BICIREVISION";

	public static final String BICYCLES_TABLE_NAME = "bicycles";

	public static final String COLUMN_ID = "_ID";
	public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
	public static final String COLUMN_PHOTO = "PHOTO";



	private static final String BICYCLES_TABLE_CREATE =
			"CREATE TABLE " + BICYCLES_TABLE_NAME + " (" +
					COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COLUMN_DESCRIPTION + " TEXT, " + 
					COLUMN_PHOTO + " BLOB"
					+ ");";

	public static final String PARTS_TABLE_NAME = "parts";
	public static final String COLUMN_NAME = "NAME";
	public static final String COLUMN_BICYCLE_ID = "BICYCLE_ID";

	private static final String PARTS_TABLE_CREATE = 
			"CREATE TABLE " + PARTS_TABLE_NAME + " (" +
					COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COLUMN_NAME + " TEXT, " +
					COLUMN_BICYCLE_ID + " INT"
					+ ");";

	public static final String LOGS_TABLE_NAME = "logs";
							
	public static final String COLUMN_REASON = "REASON";
	public static final String COLUMN_COST = "COST";
	public static final String COLUMN_TIMESTAMP = "TIMESTAMP";
	public static final String COLUMN_PART_ID = "PART_ID";

	private static final String LOGS_TABLE_CREATE = 
			"CREATE TABLE " + LOGS_TABLE_NAME + " (" +
					COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COLUMN_DESCRIPTION + " TEXT, " +
					COLUMN_REASON + " TEXT, " +
					COLUMN_COST + " DOUBLE, " +
					COLUMN_TIMESTAMP + " INTEGER, " +
					COLUMN_PART_ID + " INT"
					+ ");";

	public BiciRevisionOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(BICYCLES_TABLE_CREATE);
		db.execSQL(PARTS_TABLE_CREATE);
		db.execSQL(LOGS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {



	}

}
