package uk.org.openhealthcare.guidelines;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper is used for versioning the database, and also for providing a connection
 * to the app's database.
 * 
 * Versions are managed in the onUpgrade() method 
 *  
 * @author Ross Jones (ross.jones@openhealthcare.org.uk)
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
	public DatabaseHelper(Context context, String name, int version) {
	    super(context, name, null, version);
	 }
	
	/* 
	 * Create a new database and tables for the default types.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Category.CreateTable(db);
		Guideline.CreateTable(db);
	}

	/* 
	 * Upgrade the database for the specific version. We currently only have version
	 * 1 and so this will do nothing currently
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Category.UpgradeTable(db, oldVersion, newVersion);
		Guideline.UpgradeTable(db, oldVersion, newVersion);		
	}

}
