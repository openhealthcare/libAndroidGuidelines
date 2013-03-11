package uk.org.openhealthcare.guidelines;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * DatabaseHelper is used for versioning the database, and also for providing a connection
 * to the app's database.
 * 
 * Versions are managed in the onUpgrade() method 
 *  
 * @author Ross Jones (ross.jones@openhealthcare.org.uk)
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
	SQLiteDatabase database = null;
	
	public DatabaseHelper(Context context, String name, int version) {
	    super(context, name, null, version);
		Log.i("Helper", "Created");
		this.database = this.getWritableDatabase();		
	 }
	
	public void close() {
		this.database.close();
		super.close();
	}
	
	/* 
	 * Create a new database and tables for the default types.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i("Test","Creating tables");
		Category.CreateTable(db);
		Guideline.CreateTable(db);
		ManifestFile.CreateTable(db);		
	}

	/* 
	 * Upgrade the database for the specific version. We currently only have version
	 * 1 and so this will do nothing currently
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("Test","Upgrading tables");		
		Category.UpgradeTable(db, oldVersion, newVersion);
		Guideline.UpgradeTable(db, oldVersion, newVersion);		
		ManifestFile.UpgradeTable(db, oldVersion, newVersion);		
	}

	/**************************************************************************** Category helpers */
	
	/*
	 *
	 */
	public boolean AddCategory(Category c) {
		if ( this.GetCategoryCount(c.Name, c.Parent) > 0 ) {
			Log.i("Helper", "Not adding duplicate category");
			return false;
		}
		
		this.database.beginTransaction();
		try{
			ContentValues insertValues = new ContentValues();
			insertValues.put("name", c.Name);
			if ( c.Parent != null ) {
				Log.i("Helper", "Adding a parent");
				insertValues.put("parent_id", c.Parent.Id);
			}
			c.Id = this.database.insert(Category.TABLE_NAME, null, insertValues);
			Log.i("Helper", "Inserted row ID is " + Long.toString(c.Id));
			this.database.setTransactionSuccessful();
		}
		finally {
			this.database.endTransaction();
		}
		return true;
	}

	public Category LoadCategoryFromDb(long id) {
	    Cursor cursor = this.database.rawQuery("SELECT _id, name, parent_id FROM " + Category.TABLE_NAME  + " WHERE _id = ?;", 
	    	new String[]{Long.toString(id)});
	    cursor.moveToFirst();
	    
	    Category cat = loadCategoryFromCursor(cursor);
	    cursor.close();
	    return cat;
	}

	private Category loadCategoryFromCursor(Cursor cursor) {
		Category cat = new Category();
	    cat.Id = cursor.getLong(0);
	    cat.Name = cursor.getString(1);
	    try {
	    	long parent_id = cursor.getLong(2);
	    	cat.Parent = LoadCategoryFromDb(parent_id);
	    } catch (Exception e ) {
	    	// Nothing to do, there's no parent_id or category
	    }
		return cat;
	}

	public List<Category> GetAllTopLevelCategories() {
		List<Category> categories = new ArrayList<Category>();

	    Cursor cursor = this.database.rawQuery("SELECT _id, name, parent_id FROM " + Category.TABLE_NAME  + 
	    		" WHERE parent_id is NULL ORDER BY name", null);
	    cursor.moveToFirst();
	    Log.i("Helper/GetAllTopLevelCategories", "Moving to first");
	    while (!cursor.isAfterLast()) {
	      Log.i("Helper/GetAllTopLevelCategories", "Checking row");
	      Category cat = loadCategoryFromCursor(cursor);
	      categories.add(cat);
	      cursor.moveToNext();
	    }

	    cursor.close();
	    return categories;
	}
	
	
	public List<Category> GetCategoryChildren(Category top) {
		List<Category> categories = new ArrayList<Category>();

	    Cursor cursor = this.database.rawQuery("SELECT _id, name, parent_id FROM " + Category.TABLE_NAME  + 
	    		" WHERE parent_id is ? ORDER BY name", new String[]{Long.toString(top.Id)});
	    cursor.moveToFirst();
	    Log.i("Helper/GetCategoryChildren", "Moving to first");
	    while (!cursor.isAfterLast()) {
	      Log.i("Helper/GetCategoryChildren", "Checking row");
	      Category cat = loadCategoryFromCursor(cursor);
	      categories.add(cat);
	      cursor.moveToNext();
	    }

	    cursor.close();
	    return categories;

	}	

	public long GetCategoryCount(String name, Category parent) {
		
		long count = -1;
		if (parent == null ) {
			count = DatabaseUtils.queryNumEntries(this.database, Category.TABLE_NAME,
					"name=?", new String[] {name});					
		} else {
			count = DatabaseUtils.queryNumEntries(this.database, Category.TABLE_NAME,
					"name=? AND parent_id=?", new String[] {name, Long.toString(parent.Id)});								
		}
		return count;
	}		

	
	public long GetCategoryChildrenCount(Category top) {
		return DatabaseUtils.queryNumEntries(this.database, Category.TABLE_NAME,
				"parent_id=?", new String[] {Long.toString(top.Id)});					
		
	}		

	/************************************************************************** Guideline helpers */
	
	public Guideline LoadGuidelineFromDb(long id) {
	    Cursor cursor = this.database.rawQuery("SELECT _id, name, url, onDisk, category_id FROM " + Guideline.TABLE_NAME  + " WHERE _id = ?;", 
	    	new String[]{Long.toString(id)});
	    cursor.moveToFirst();
	    
	    Guideline g = loadGuidelineFromCursor(cursor);
	    cursor.close();
	    return g;
	}
	
	private Guideline loadGuidelineFromCursor(Cursor cursor) {
		Guideline g = new Guideline();
	    g.Id = cursor.getLong(0);
	    g.Name = cursor.getString(1);
	    g.Url = cursor.getString(2);
	    try {
	    	g.OnDisk = cursor.getString(3);
	    } catch (Exception e) {}
	    
	    try {
	    	g.Category= LoadCategoryFromDb(cursor.getLong(4));
	    } catch (Exception e ) {
	    	// Nothing to do, there's no parent_id or category
	    }
		return g;
	}
	
	public List<Guideline> GetForCategory(Category category) {
		if ( category == null ) {
			return null;
		}
		
		List<Guideline> guidelines = new ArrayList<Guideline>();		
	    Cursor cursor = this.database.rawQuery( "SELECT _id,name,url,on_disk,category_id FROM " + Guideline.TABLE_NAME +
		    	" WHERE category_id=? ORDER BY name DESC;", new String[]{Long.toString(category.Id)});
	    
	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Guideline g = loadGuidelineFromCursor(cursor);
	      guidelines.add(g);
	      cursor.moveToNext();
	    }

	    cursor.close();
	    return guidelines;
	}
			

}
