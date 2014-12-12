package com.freewaycoffee.client;
import java.io.IOException;

import android.os.AsyncTask;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.*;



public class FreewayCoffeeSignonAsyncGet extends AsyncTask<String,Integer,String> 
{
	/*
	 * Executes a URL GET in background. Retrieves XML string, and passes back to UI thread.
	 * Does not attempt to interpret anything. Just to keep the UI thread running without "not responding"
	 */
		
	private FreewayCoffeeSignonActivity myActivity;
	private FreewayCoffeeApp myApp;
	private boolean Completed;
	private String Response;
	public String EmailAddressSaved;
	public String PasswordSaved;
	public boolean LoginAutomaticallySaved;
		
	public FreewayCoffeeSignonAsyncGet(FreewayCoffeeSignonActivity activity, FreewayCoffeeApp app)
	{
		
		myActivity = activity;
		myApp = app;
		Response=null;
		Completed=false;
	}
		
	@Override
	protected void onPreExecute()
	{
		Completed=false;
		myActivity.showProgressDialog();
	}
	
	protected String doInBackground(String... URLGet)
	{
			
		// Even though we potentially accept a list of strings (I think), we only want the first one for now. Baby steps. 
			
		try
		{
			FreewayCoffeeHTTP HTTPRef = myApp.GetHTTP();
			if (isCancelled())
			{
				UnlinkActivity();
				return null;
			}
			
			Response = HTTPRef.ExecuteHTTPGet(URLGet[0]);
			
			return Response;
		}
		
		catch (Exception e) 
		{
			
			 return FreewayCoffeeXMLHelper.NETWORK_ERROR_XML;
			//Log. e ("Input Output Exception", e.toString ());
		}
	}
	
	public void UnlinkActivity()
	{
		myActivity=null;
	}
	
	public void SetActivity(FreewayCoffeeSignonActivity activity)
	{
		// This case is where the async completed but the user rotated, and we now need to notify a new activity.
		myActivity = activity;
		if(IsCompleted())
		{
			
			myActivity.ProcessXMLResult(Response);
		}
	}
	
	public boolean IsCompleted()
	{
		return Completed;
	}
	
	
	protected void onPostExecute(String result)
    {
		if((myActivity!=null) && !isCancelled())
		{
			Completed=true;
			myActivity.ProcessXMLResult(result);
		}
    }


}


