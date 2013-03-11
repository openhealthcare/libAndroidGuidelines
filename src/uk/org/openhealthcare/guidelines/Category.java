package uk.org.openhealthcare.guidelines;


import org.w3c.dom.Element;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author Ross Jones (ross.jones@openhealthcare.org.uk)
 */
public class Category extends Object {
	
	public static String TABLE_NAME = "category";
	
	public long Id;
	public String Name;
	public Category Parent;
	
	public static Category LoadFromXml(Element node) {
		return new Category();
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
			PATCHES[i].apply(db);
		}
	}

	private static final DBPatch[] PATCHES = new DBPatch[] {
		   new DBPatch() {
		      public void apply(SQLiteDatabase db) {
		  		String q = "CREATE TABLE " + TABLE_NAME  + " ( " +
		  			"_id integer primary key autoincrement, " +
					"name text NOT NULL, " + 
					"parent_id integer " +
				");";
				db.execSQL(q);
		      }
		 
		      public void revert(SQLiteDatabase db) {
		         db.execSQL("drop table " + TABLE_NAME  + ";");
		      }
		   }
		};
}
