package com.freewaycoffee.client;

import android.content.Context;
import android.database.*;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.content.ContentValues;
import android.util.Log;



public class FreewayCoffeeDB 
{

	private static final String DATABASE_NAME = "FreewayCoffee.db";
	private static final String MAIN_TABLE="FCUserInfo";
	private final Context context;
	private static final int DATABASE_VERSION=1;
	
	private SQLiteDatabase db;
	private FreewayCoffeeDBHelper DBHelper;
	
	
	
	// Constructor
	public FreewayCoffeeDB(Context _context)
	{
		context = _context;
		DBHelper = new FreewayCoffeeDBHelper(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	public FreewayCoffeeDB open() throws SQLException
	{
		// Just for now, lets create it each time ?
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
		db.close();
	}
	
	public String getLoginEmail()
	{
		Cursor cursor = db.query(MAIN_TABLE, new String[]{"login_email"}, null, null, null, null, null);
		if(cursor.getCount()==0 || !cursor.moveToFirst())
		{
			return null;
		}
		
		// Really bad. Try to recreate DB and hope for best.
		if(cursor.getCount()>1)
		{
			Log.w("FreewayCoffeeDB","getLoginEmail returnd " + cursor.getCount() +" Which is obviously too many. Recreating Database");
			DBHelper.onCreate(db);
			return null;
		}
		return cursor.getString(0);
	}
	
	
	public String getLoginNickname()
	{
		Cursor cursor = db.query(MAIN_TABLE, new String[]{"login_nickname"}, null, null, null, null, null);
		if(cursor.getCount()==0 || !cursor.moveToFirst())
		{
			return null;
		}
		
		// Really bad. Try to recreate DB and hope for best.
		if(cursor.getCount()>1)
		{
			Log.w("FreewayCoffeeDB","getLoginNickname returnd " + cursor.getCount() +" Rows. Which is obviously too many. Recreating Database");
			DBHelper.onCreate(db);
			return null;
		}
		return cursor.getString(0);
	}
	
	public String getPassword()
	{
		Cursor cursor = db.query(MAIN_TABLE, new String[]{"login_password"}, null, null, null, null, null);
		if(cursor.getCount()==0 || !cursor.moveToFirst())
		{
			return null;
		}
		
		// Really bad. Try to recreate DB and hope for best.
		if(cursor.getCount()>1)
		{
			Log.w("FreewayCoffeeDB","getPassword returnd " + cursor.getCount() +" Rows. Which is obviously too many. Recreating Database");
			DBHelper.onCreate(db);
			return null;
		}
		return cursor.getString(0);
	}
	
	public boolean getAutoLogin()
	{
		Cursor cursor = db.query(MAIN_TABLE, new String[]{"autoLogin"}, null, null, null, null, null);
		if(cursor.getCount()==0 || !cursor.moveToFirst())
		{
			return false;
		}
		
		// Really bad. Try to recreate DB and hope for best.
		if(cursor.getCount()>1)
		{
			Log.w("FreewayCoffeeDB","getAutoLogin returnd " + cursor.getCount() +" Rows. Which is obviously too many. Recreating Database");
			DBHelper.onCreate(db);
			return false;
		}
		
		if(cursor.getInt(0)==0)
		{
			return false;
		}
		else
		{
			return true;
		}
		
		
	}
	
	public boolean UpdateLoginEmail(String newEmail)
	{
		ContentValues contents = new ContentValues();
		contents.put("login_email",newEmail);
		
		//Log.w("FC","UpdateLoginEmail: " + newEmail);
		return DoUpdateOrInsert(contents);
	}
	
	
	
	public boolean UpdateLoginNickname(String newNickname)
	{
		ContentValues contents = new ContentValues();
		contents.put("login_nickname",newNickname);
		//Log.w("FC","UpdateLoginNickname:" + newNickname);
		return DoUpdateOrInsert(contents);
	}
	
	public boolean UpdatePassword(String newPassword)
	{
		ContentValues contents = new ContentValues();
		contents.put("login_password",newPassword);
		//Log.w("FC","UpdatePassword: " + newPassword);
		
		return DoUpdateOrInsert(contents);
	}
	
	public boolean UpdateAutoLogin(boolean newAutoLogin)
	{
		ContentValues contents = new ContentValues();
		if(newAutoLogin)
		{
			contents.put("autoLogin",1);
		}
		else
		{
			contents.put("autoLogin",0);
		}
		
		return DoUpdateOrInsert(contents);
	}
	
	
	private boolean DoUpdateOrInsert(ContentValues contents)
	{
		if(IsDatabaseEmpty()==true)
		{
			//Log.w("FC","DoUpdateOrInsert: Database Empty");
			if(db.insert(MAIN_TABLE,null,contents)==1)
			{
				//Log.w("FC","DoUpdateOrInsert: Insert OK");
				return true;
			}
			else
			{
				Log.w("FC","DoUpdateOrInsert: Insert Fail");
				return false;
			}
		}
		else
		{
			//Log.w("FC","DoUpdateOrInsert: Database Not Empty");
			// Obviously a bit brittle. Relies on us maintaining constraint of zero or 1 rows in code.
			if(db.update(MAIN_TABLE,contents,null,null)==1)
			{
				//Log.w("FC","DoUpdateOrInsert: Update OK");
				return true;
			}
			else
			{
				Log.w("FC","DoUpdateOrInsert: Update Fail");
				return false;
			}
		}
	}
	
	
	private boolean IsDatabaseEmpty()
	{
		// Almost certainly a better way to do this!
		Cursor cursor = db.query(MAIN_TABLE, null, null, null, null, null, null);
		if(cursor.getCount()==1)
		{
			return false;
		}
		
		// Really bad. Try to recreate DB and hope for best.
		if(cursor.getCount()>1)
		{
			Log.w("FreewayCoffeeDB","IsDatabaseEmpty had  " + cursor.getCount() +" Rows. Which is obviously too many. Recreating Database");
			DBHelper.onCreate(db);
			return true;
		}
		
		return true;
	}
	
	private static class FreewayCoffeeDBHelper extends SQLiteOpenHelper
	{
		
		private String DATABASE_CREATE = "create table " + MAIN_TABLE + " ( login_email TEXT PRIMARY KEY, login_nickname TEXT, login_password TEXT, autoLogin INTEGER)";
		// Constructor
		public FreewayCoffeeDBHelper(Context context, String name, CursorFactory cursorFac, int Version)
		{
				super(context,name,cursorFac,Version);
		}
		
		// Called when the database does not exist and needs to be created
		@Override
		public void onCreate(SQLiteDatabase _db)
		{
			_db.execSQL(DATABASE_CREATE);
			
		}
		
		// Upgrade the DB
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion)
		{
			_db.execSQL("DROP TABLE IF EXISTS " + MAIN_TABLE);
			onCreate(_db);
		}
	}
}
