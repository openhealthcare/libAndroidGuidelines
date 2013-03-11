package uk.org.openhealthcare.guidelines;


import java.util.Date;

import org.w3c.dom.Element;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ManifestFile {

	private static String root_url = "http://openhealthcare.org.uk/";
	public static String TABLE_NAME = "manifest";
	
	public long Id;
	public Date LastCheck;
	public String MD5;
		
	private int daysBetweenDates(Date d1, Date d2) {
		int day = 24 * 3600 * 1000;
        int days = (int) ((d1.getTime() - d2.getTime()) / day);  
        return Math.abs(days);  
    }  
	
	@SuppressWarnings("deprecation")
	public String RequiresCheck(SQLiteDatabase db) {
		// If a check if required then it will return the MD5 to check against,
		// otherwise null.
	    Cursor cursor = db.rawQuery("SELECT last_check, md5 from "+ TABLE_NAME, null);
		cursor.moveToFirst();
		
		long check = cursor.getLong(0);
		String md5 = cursor.getString(1);
		
		Date now = new Date();
		Date lastDate = new Date( check );
		Log.i("Manifest", "Last check was :" + lastDate.toString());
		Log.i("Manifest", "Now is :" + now.toString());		
		
		int diff = daysBetweenDates(now, lastDate);
		Log.i("Manifest", "Num of days diff is :" + Integer.toString(diff));		
		if ( diff > 1 ) {
			return md5;
		}
		return null;
	}

	public boolean UpdateCheck(SQLiteDatabase db, String md5) {
		ContentValues values = new ContentValues();
		values.put("md5", md5);
		values.put("last_check", System.currentTimeMillis());
		db.update (TABLE_NAME, values, null, null);
		return true;
	}

	
	/**
	 * Create a new category table, only called when the DB is 
	 * created. 
	 */
	public static void CreateTable(SQLiteDatabase db) {
		Log.i("Manifest", "Request to create table ");
		for (int i=0; i<PATCHES.length; i++) {
		    PATCHES[i].apply(db);
		  }		
	}

	/**
	 * Upgrade the category table in the database.
	 * @return 
	 */
	public static void UpgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("Manifest", "Request to upgrade table ");		
		for (int i=oldVersion; i>newVersion; i++) {
			PATCHES[i].apply(db);
		}
	}

	private static final DBPatch[] PATCHES = new DBPatch[] {
		   new DBPatch() {
		      public void apply(SQLiteDatabase db) {
		  		String q = "CREATE TABLE " + TABLE_NAME  + " ( " +
		  			"_id integer primary key autoincrement, " +
					"last_check int NOT NULL, " + 
					"md5 text NOT NULL" +
				");";
		  		db.execSQL(q);
				q = "INSERT INTO " + TABLE_NAME + "(last_check, md5) VALUES(0, '');";
				db.execSQL(q);
		      }
		 
		      public void revert(SQLiteDatabase db) {
		         db.execSQL("drop table " + TABLE_NAME  + ";");
		      }
		   }
		};
}
