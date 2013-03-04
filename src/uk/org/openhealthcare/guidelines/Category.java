package uk.org.openhealthcare.guidelines;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Ross Jones (ross.jones@openhealthcare.org.uk)
 */
public class Category extends Object {

	private static final String[] allColumns = { "_id", "name", "parent_id"};	
	
	public long Id;
	public String name;
	public Category parent;
	
	public void LoadFromXml() {
		
	}
	
	public void LoadFromDb() {
		
	}
	
	
	public static List<Category> GetAllTopLevel(SQLiteDatabase db) {
		List<Category> categories = new ArrayList<Category>();

	    Cursor cursor = db.rawQuery("SELECT _id, name, parent_id FROM category WHERE parent_id IS NULL;", null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Category cat = new Category();
	      cat.Id = cursor.getLong(0);
	      cat.name = cursor.getString(1);
	      categories.add(cat);
	      cursor.moveToNext();
	    }

	    cursor.close();
	    return categories;
	}
	
	public static List<Category> GetChildren(SQLiteDatabase db, Category top) {
		return null;
	}	
	
	public static long GetChildrenCount(SQLiteDatabase db, Category top) {
		long count = 0;
		Cursor cursor = db.rawQuery("SELECT count(_id) FROM category WHERE parent_id = ?;", 
				new String[]{Long.toString(top.Id)});
	    cursor.moveToFirst();
	    
	    count = cursor.getLong(0);
	    cursor.close();
	    return count;
	}		
	
	public static void AddCategory(Category c) {
		
	}
	
	public static void Clear() {
		
	}	
	
	/**
	 * Create a new category table, only called when the DB is 
	 * created. 
	 */
	public static void CreateTable(SQLiteDatabase db) {
		String q = "CREATE TABLE category ( " +
						"_id integer primary key autoincrement, " +
						"name text NOT NULL, " + 
						"parent_id integer" +
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
