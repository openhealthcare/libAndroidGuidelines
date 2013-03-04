/**
 * 
 */
package uk.org.openhealthcare.guidelines;

import java.util.List;

import android.database.Cursor;
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
	
	public static Guideline LoadFromDb(SQLiteDatabase db, long id) {
	    Cursor cursor = db.rawQuery("SELECT _id, name, url, onDisk, category_id FROM category WHERE _id = ?;", 
	    	new String[]{Long.toString(id)});
	    cursor.moveToFirst();
	    
	    Guideline g = loadFromCursor(db, cursor);
	    cursor.close();
	    return g;
	}
	
	private static Guideline loadFromCursor(SQLiteDatabase db,Cursor cursor) {
		Guideline g = new Guideline();
	    g.Id = cursor.getLong(0);
	    g.name = cursor.getString(1);
	    g.url = cursor.getString(2);
	    try {
	    	g.onDisk = cursor.getString(3);
	    } catch (Exception e) {}
	    
	    try {
	    	g.category= Category.LoadFromDb(db, cursor.getLong(4));
	    } catch (Exception e ) {
	    	// Nothing to do, there's no parent_id or category
	    }
		return g;
	}
	
	public static List<Guideline> GetForCategory(Category parent) {
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
