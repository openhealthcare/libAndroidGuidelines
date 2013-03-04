/**
 * 
 */
package uk.org.openhealthcare.guidelines;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * @author Ross Jones (ross.jones@openhealthcare.org.uk)
 */
public class Guideline {

	public long Id;
	public String name;
	public String url;
	public String onDisk;
	public Category category;
	
	public void LoadFromXml() {
		
	}
	
	public void LoadFromDb() {
		
	}
	
	public static List<Guideline> GetForCategeory(Category parent) {
		return null;
	}
			
	public static void Clear() {
		
	}	
	
	
	/**
	 * Create a new category table, only called when the DB is 
	 * created. 
	 */
	public static void CreateTable(SQLiteDatabase db) {
		String q = "CREATE TABLE guideline ( " +
						"_id integer primary key autoincrement, " +
						"name text NOT NULL, " + 
						"url text NOT NULL, " +						
						"on_disk text, " +						
						"category_id integer" +
					"); ";
		db.execSQL(q);
	}

	/**
	 * Upgrade the category table in the database.
	 * @return 
	 */
	public static void UpgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
		// we'll probably do this by maintaining a map of version against the SQL to run
		// to upgrade, running all of them from oldVersion to newVersion.
	}

	
}
