package uk.org.openhealthcare.guidelines;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

	/**************************************************************************** Category helpers */
	
	/*
	 *
	 */
	public void AddCategory(SQLiteDatabase db, Category c) {
		ContentValues insertValues = new ContentValues();
		insertValues.put("name", c.Name);
		if ( c.Parent != null ) {
			insertValues.put("parent_id", c.Parent.Id);
		} //else {
		//	insertValues.put("parent_id", null)
		//}
		
		c.Id = db.insert(Category.TABLE_NAME, null, insertValues);
	}

	public Category LoadCategoryFromDb(SQLiteDatabase db, long id) {
	    Cursor cursor = db.rawQuery("SELECT _id, name, parent_id FROM " + Category.TABLE_NAME  + " WHERE _id = ?;", 
	    	new String[]{Long.toString(id)});
	    cursor.moveToFirst();
	    
	    Category cat = loadCategoryFromCursor(db, cursor);
	    cursor.close();
	    return cat;
	}

	private Category loadCategoryFromCursor(SQLiteDatabase db,Cursor cursor) {
		Category cat = new Category();
	    cat.Id = cursor.getLong(0);
	    cat.Name = cursor.getString(1);
	    try {
	    	long parent_id = cursor.getLong(2);
	    	cat.Parent = LoadCategoryFromDb(db, parent_id);
	    } catch (Exception e ) {
	    	// Nothing to do, there's no parent_id or category
	    }
		return cat;
	}

	public List<Category> GetAllTopLevelCategories(SQLiteDatabase db) {
		List<Category> categories = new ArrayList<Category>();

	    Cursor cursor = db.rawQuery("SELECT _id, name, parent_id FROM " + Category.TABLE_NAME  + 
	    		" WHERE parent_id IS NULL ORDER BY name;", null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Category cat = loadCategoryFromCursor(db, cursor);
	      categories.add(cat);
	      cursor.moveToNext();
	    }

	    cursor.close();
	    return categories;
	}
	

	
	public static List<Category> GetCategoryChildren(SQLiteDatabase db, Category top) {
		return null;
	}	
	
	public static long GetCategoryChildrenCount(SQLiteDatabase db, Category top) {
		long count = 0;
		Cursor cursor = db.rawQuery("SELECT count(_id) FROM " + Category.TABLE_NAME  + " WHERE parent_id = ?;", 
				new String[]{Long.toString(top.Id)});
	    cursor.moveToFirst();
	    
	    count = cursor.getLong(0);
	    cursor.close();
	    return count;
	}		

	/************************************************************************** Guideline helpers */
	
	public Guideline LoadGuidelineFromDb(SQLiteDatabase db, long id) {
	    Cursor cursor = db.rawQuery("SELECT _id, name, url, onDisk, category_id FROM " + Guideline.TABLE_NAME  + " WHERE _id = ?;", 
	    	new String[]{Long.toString(id)});
	    cursor.moveToFirst();
	    
	    Guideline g = loadGuidelineFromCursor(db, cursor);
	    cursor.close();
	    return g;
	}
	
	private Guideline loadGuidelineFromCursor(SQLiteDatabase db,Cursor cursor) {
		Guideline g = new Guideline();
	    g.Id = cursor.getLong(0);
	    g.Name = cursor.getString(1);
	    g.Url = cursor.getString(2);
	    try {
	    	g.OnDisk = cursor.getString(3);
	    } catch (Exception e) {}
	    
	    try {
	    	g.Category= LoadCategoryFromDb(db, cursor.getLong(4));
	    } catch (Exception e ) {
	    	// Nothing to do, there's no parent_id or category
	    }
		return g;
	}
	
	public List<Guideline> GetForCategory(SQLiteDatabase db, Category category) {
		if ( category == null ) {
			return null;
		}
		
		List<Guideline> guidelines = new ArrayList<Guideline>();		
	    Cursor cursor = db.rawQuery( "SELECT _id,name,url,on_disk,category_id FROM " + Guideline.TABLE_NAME +
		    	" WHERE category_id=? ORDER BY name DESC;", new String[]{Long.toString(category.Id)});
	    
	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Guideline g = loadGuidelineFromCursor(db, cursor);
	      guidelines.add(g);
	      cursor.moveToNext();
	    }

	    cursor.close();
	    return guidelines;
	}
			

}
