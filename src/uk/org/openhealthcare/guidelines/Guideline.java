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
		for (int i=0; i<PATCHES.length; i++) {
		    PATCHES[i].apply(db);
		  }		
	}

	/**
	 * Upgrade the category table in the database.
	 * @return 
	 */
	public static void UpgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (int i=oldVersion; i>newVersion; i++) {
			PATCHES[i-1].apply(db);
		}
	}

	private static final DBPatch[] PATCHES = new DBPatch[] {
		   new DBPatch() {
		      public void apply(SQLiteDatabase db) {
		  		String q = "CREATE TABLE guideline ( " +
		  			"_id integer primary key autoincrement, " +
					"name text NOT NULL, " + 
					"url text NOT NULL, " +						
					"on_disk text, " +						
					"category_id integer" +
				"); ";
				db.execSQL(q);
		      }
		 
		      public void revert(SQLiteDatabase db) {
		         db.execSQL("drop table guideline;");
		      }
		   }
		};
	
}
