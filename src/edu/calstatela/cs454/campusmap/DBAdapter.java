package edu.calstatela.cs454.campusmap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * DBAdapter is a helper class to make database operations. 
 * There are 2 tables and 1 view in campus database.
 * Tables:
 * locations (id, latitude, longitude, title, phone, web, type).
 * departments (id, college, department, web, email, phone, room, locationID).
 * 
 * @author Rascal
 * 
 */
public class DBAdapter {
	private static final String DATABASE_NAME = "campus.db";
	private static final int DATABASE_VERSION = 1;
	private static final String LOCATION_TABLE_NAME = "locations";
	private static final String LOCATION_ID = "id";
	private static final String LOCATION_LATITUDE = "latitude";
	private static final String LOCATION_LONGITUDE = "longitude";
	private static final String LOCATION_TITLE = "title";
	private static final String LOCATION_PHONE = "phone";
	private static final String LOCATION_WEB = "web";
	private static final String LOCATION_TYPE = "type";
	private static final String LOCATION_CREATE = 
			"CREATE TABLE IF NOT EXISTS "+LOCATION_TABLE_NAME+"("
	        +LOCATION_ID+" INTEGER PRIMARY KEY NOT NULL, "
			+LOCATION_LATITUDE+" REAL NOT NULL, "
	        +LOCATION_LONGITUDE+" REAL NOT NULL, "
	        +LOCATION_TITLE+" TEXT NOT NULL, "
	        +LOCATION_PHONE+" TEXT, "
	        +LOCATION_WEB+" TEXT, "
	        +LOCATION_TYPE+" TEXT);";	
	
	private static final String DEPARTMENT_TABLE_NAME = "departments";
	private static final String DEPARTMENT_ID = "id";
	private static final String DEPARTMENT_COLLEGE = "college";
	private static final String DEPARTMENT_DEPARTMENT = "department";
	private static final String DEPARTMENT_WEB = "web";
	private static final String DEPARTMENT_EMAIL = "email";
	private static final String DEPARTMENT_PHONE = "phone";
	private static final String DEPARTMENT_ROOM = "room";
	private static final String DEPARTMENT_LOCATION_ID = "locationId";	
	private static final String DEPARTMENT_CREATE = 
			"CREATE TABLE IF NOT EXISTS "+DEPARTMENT_TABLE_NAME+"("
			+DEPARTMENT_ID+" INTEGER PRIMARY KEY NOT NULL, "
			+DEPARTMENT_COLLEGE+" TEXT NOT NULL, "
			+DEPARTMENT_DEPARTMENT+" TEXT NOT NULL, "
			+DEPARTMENT_WEB+" TEXT NOT NULL, "
			+DEPARTMENT_EMAIL+" TEXT NOT NULL, "
			+DEPARTMENT_PHONE+" TEXT NOT NULL, "
			+DEPARTMENT_ROOM+" TEXT, "
			+DEPARTMENT_LOCATION_ID+" INTEGER NOT NULL, " +
		    "FOREIGN KEY("+DEPARTMENT_LOCATION_ID+") REFERENCES "
			+LOCATION_TABLE_NAME+"("+LOCATION_ID+"));";
	
	private static final String VIEW_NAME = "departmentDetail";
	private static final String VIEW_CREATE = 
			"CREATE VIEW IF NOT EXISTS "+VIEW_NAME+
			" AS SELECT "+DEPARTMENT_TABLE_NAME+"."+DEPARTMENT_ID+" AS dep_id, "+
			DEPARTMENT_TABLE_NAME+"."+DEPARTMENT_COLLEGE+" AS dep_college, "+
			DEPARTMENT_TABLE_NAME+"."+DEPARTMENT_DEPARTMENT+" AS dep_department, "+
			DEPARTMENT_TABLE_NAME+"."+DEPARTMENT_WEB+" AS dep_Web, "+
			DEPARTMENT_TABLE_NAME+"."+DEPARTMENT_EMAIL+" AS dep_Email, "+
			DEPARTMENT_TABLE_NAME+"."+DEPARTMENT_PHONE+" AS dep_Phone, "+
			DEPARTMENT_TABLE_NAME+"."+DEPARTMENT_ROOM+" AS dep_Room, "+
			LOCATION_TABLE_NAME+"."+LOCATION_LATITUDE+" AS loc_Latitude, "+
			LOCATION_TABLE_NAME+"."+LOCATION_LONGITUDE+" AS loc_Longitude, "+
			LOCATION_TABLE_NAME+"."+LOCATION_TITLE+" AS loc_Title, "+
			LOCATION_TABLE_NAME+"."+LOCATION_PHONE+" AS loc_Phone, "+
			LOCATION_TABLE_NAME+"."+LOCATION_WEB+" AS loc_Web, "+
			LOCATION_TABLE_NAME+"."+LOCATION_TYPE+" AS loc_Type"+
			" FROM "+DEPARTMENT_TABLE_NAME+", "+LOCATION_TABLE_NAME+
			" WHERE "+DEPARTMENT_TABLE_NAME+"."+DEPARTMENT_LOCATION_ID+" = "+
			LOCATION_TABLE_NAME+"."+LOCATION_ID;
	
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
	
	public DBAdapter(Context context) {
        this.DBHelper = new DatabaseHelper(context);
	}
	
	public DBAdapter openWrite() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	public DBAdapter openRead() throws SQLException {
		db = DBHelper.getReadableDatabase();
		return this;
	}
	
	public void close() {
		DBHelper.close();
	}
	
	
	/**
	 * insert a row of data into locations table on db.
	 * 
	 * @param id
	 * @param latitude
	 * @param longitude
	 * @param title
	 * @param phone
	 * @param web
	 * @param type
	 * @return the row ID of the newly inserted row, or -1 if an error occurred.
	 */
	public long insertLocation(String id, String latitude, String longitude, String title, 
			String phone, String web, String type) {
	     ContentValues values = new ContentValues();
	     values.put(LOCATION_ID, id);
	     values.put(LOCATION_LATITUDE, latitude);
	     values.put(LOCATION_LONGITUDE, longitude);
	     values.put(LOCATION_TITLE, title);
	     values.put(LOCATION_PHONE, phone);
	     values.put(LOCATION_WEB, web);
	     values.put(LOCATION_TYPE, type);
	     return db.insert(LOCATION_TABLE_NAME, null, values);
	}
	
	
	/**
	 * insert a row of data into departments table on db.
	 * 
	 * @param id
	 * @param college
	 * @param department
	 * @param web
	 * @param email
	 * @param phone
	 * @param room
	 * @param locationId
	 * @return the row ID of the newly inserted row, or -1 if an error occurred.
	 */
	public long insertDepartment(String id, String college, String department, String web, 
			String email, String phone, String room, String locationId) {
	     ContentValues values = new ContentValues();
	     values.put(DEPARTMENT_ID, id);
	     values.put(DEPARTMENT_COLLEGE, college);
	     values.put(DEPARTMENT_DEPARTMENT, department);
	     values.put(DEPARTMENT_WEB, web);
	     values.put(DEPARTMENT_EMAIL, email);
	     values.put(DEPARTMENT_PHONE, phone);
	     values.put(DEPARTMENT_ROOM, room);
	     values.put(DEPARTMENT_LOCATION_ID, locationId);
	     return db.insert(DEPARTMENT_TABLE_NAME, null, values);
	}
	
	
	/**
	 * get a cursor of view by matching department name.
	 * 
	 * @param department
	 * @return the cursor of view with matching department name.
	 * @throws SQLException
	 */
	public Cursor getViewByDepartment(String department) throws SQLException {		
		Cursor mc = db.query(true, VIEW_NAME, 
			       new String[] {"dep_id", "dep_college", "dep_department", "dep_Web", "dep_Email", 
				   "dep_Phone", "dep_Room", "loc_Latitude", "loc_Longitude", "loc_Title", "loc_Phone", 
				   "loc_Web", "loc_Type"}, "dep_department"+"="+"'"+department+"'", null,
			       null, null, null, null);
		if (mc != null) mc.moveToFirst();
       
	    return mc;	
	}

	
	/**
	 * get a cursor of locations table by matching location id.
	 * 
	 * @param id
	 * @return the cursor of locations table with matching location id.
	 * @throws SQLException
	 */
	public Cursor getLocationById(int id) throws SQLException {
		Cursor mc = db.query(true, LOCATION_TABLE_NAME, 
				       new String[] {LOCATION_ID, LOCATION_LATITUDE, LOCATION_LONGITUDE, LOCATION_TITLE,
				       LOCATION_PHONE, LOCATION_WEB, LOCATION_TYPE}, LOCATION_ID + "=" + id, null,
				       null, null, null, null);
		if (mc != null) mc.moveToFirst();
		
		return mc;
	}
	
	
	/**
	 * get a cursor of locations table by matching location title.
	 * 
	 * @param title
	 * @return the cursor of locations table with matching location title.
	 * @throws SQLException
	 */
	public Cursor getLocationByTitle(String title) throws SQLException {
		Cursor mc = db.query(true, LOCATION_TABLE_NAME, 
				       new String[] {LOCATION_ID, LOCATION_LATITUDE, LOCATION_LONGITUDE, LOCATION_TITLE,
				       LOCATION_PHONE, LOCATION_WEB, LOCATION_TYPE}, LOCATION_TITLE+"="+"'"+title+"'", null,
				       null, null, null, null);
		if (mc != null) mc.moveToFirst();
		
		return mc;
	}	
	
	
	/**
	 * get a cursor of locations table by matching location type.
	 * 
	 * @param type
	 * @return the cursor of locations table with matching location type.
	 * @throws SQLException
	 */
	public Cursor getLocationByType(String type) throws SQLException {
		Cursor mc = db.query(LOCATION_TABLE_NAME, 
				       new String[] {LOCATION_ID, LOCATION_LATITUDE, LOCATION_LONGITUDE, LOCATION_TITLE,
				       LOCATION_PHONE, LOCATION_WEB, LOCATION_TYPE}, LOCATION_TYPE+"="+"'"+type+"'", null, 
				       null, null, null);
		return mc;
	}
	
	
	/**
	 * get a cursor of departments table by matching department id.
	 * 
	 * @param id
	 * @return the cursor of departments table with matching department id.
	 * @throws SQLException
	 */
	public Cursor getDepartmentById(int id) throws SQLException {
		Cursor mc = db.query(true, DEPARTMENT_TABLE_NAME, 
				       new String[] {DEPARTMENT_ID, DEPARTMENT_COLLEGE, DEPARTMENT_DEPARTMENT, DEPARTMENT_WEB,
				       DEPARTMENT_EMAIL, DEPARTMENT_PHONE, DEPARTMENT_ROOM, DEPARTMENT_LOCATION_ID}, 
				       DEPARTMENT_ID + "=" + id, null, null, null, null, null);
		if (mc != null) mc.moveToFirst();
		
		return mc;
	}		
	
	
	/**
	 * get a cursor of departments table by matching department name.
	 * 
	 * @param department
	 * @return the cursor of departments table with matching department name.
	 * @throws SQLException
	 */
	public Cursor getDepartmentByDepartment(String department) throws SQLException {
		Cursor mc = db.query(true, DEPARTMENT_TABLE_NAME, 
				       new String[] {DEPARTMENT_ID, DEPARTMENT_COLLEGE, DEPARTMENT_DEPARTMENT, DEPARTMENT_WEB,
				       DEPARTMENT_EMAIL, DEPARTMENT_PHONE, DEPARTMENT_ROOM, DEPARTMENT_LOCATION_ID}, 
				       DEPARTMENT_DEPARTMENT + "=" +"'"+department+"'", null, null, null, null, null);
		if (mc != null) mc.moveToFirst();
		
		return mc;
	}
	
	
	/**
	 * get a cursor of departments table by matching college.
	 * 
	 * @param college
	 * @return the cursor of departments table with matching college.
	 * @throws SQLException
	 */
	public Cursor getDepartmentByCollege(String college) throws SQLException {
		Cursor mc = db.query(DEPARTMENT_TABLE_NAME, 
				       new String[] {DEPARTMENT_ID, DEPARTMENT_COLLEGE, DEPARTMENT_DEPARTMENT, DEPARTMENT_WEB,
				       DEPARTMENT_EMAIL, DEPARTMENT_PHONE, DEPARTMENT_ROOM, DEPARTMENT_LOCATION_ID}, 
				       DEPARTMENT_COLLEGE + "=" +"'"+college+"'", null, null, null, null);
		
		return mc;
	}
	
	
	/**
	 * get all department names from departments table.
	 * 
	 * @return an array of department names in strings.
	 */
	public String[] getAlldepartments()
    {
        Cursor mc = db.query(DEPARTMENT_TABLE_NAME, new String[] {DEPARTMENT_DEPARTMENT}, null, null, null, null, null);
        if(mc.getCount() >0)
        {
            String[] str = new String[mc.getCount()];
            int i = 0;
            while (mc.moveToNext())
            {
                 str[i] = mc.getString(mc.getColumnIndex(DEPARTMENT_DEPARTMENT));
                 i++;
            }
            mc.close();
            return str;
        }
        else
        { 
        	mc.close();
            return new String[] {};
        }
    }
	
	
    /**
     * DatabaseHelper is a helper class to make database schema creation.
     * 
     * @author Rascal
     *
     */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

	    @Override
	    public void onCreate(SQLiteDatabase db) {
	    	try {
			    db.execSQL(LOCATION_CREATE);
			    db.execSQL(DEPARTMENT_CREATE);
			    db.execSQL(VIEW_CREATE);
	    	} catch (SQLException e) {
	    		e.printStackTrace();
			}
	    }

		@Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		    Log.w("DBAdapter", "Upgrading database from version " + oldVersion + " to " +
	              newVersion + ", which will destroy all old data");
		    db.execSQL("DROP VIEW IF EXISTS" + VIEW_NAME);
		    db.execSQL("DROP TABLE IF EXISTS " + DEPARTMENT_TABLE_NAME);
		    db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME);
		    onCreate(db);
	    }
	}	
}