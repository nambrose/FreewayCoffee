package com.freewaycoffee.client;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;


public class FreewayCoffeeTimeToLocationAsyncGet extends AsyncTask<String,Integer,String>
{
	
	private FreewayCoffeeTimeToLocationActivity myActivity;
	private FreewayCoffeeApp myApp;
	private boolean Completed;
	private String Response;
	public Integer CurrentTimeToLocationSaved;
	public Integer OriginalTimeToLocationSaved;
	
	
	public FreewayCoffeeTimeToLocationAsyncGet(FreewayCoffeeTimeToLocationActivity activity, FreewayCoffeeApp app)
	{
		myActivity = activity;
		myApp = app;
		Response=null;
		Completed=false;
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
	
	
	@Override
	protected void onPreExecute()
	{
		Completed=false;
		myActivity.showProgressDialog();
	}
	
	
	public void SetActivity(FreewayCoffeeTimeToLocationActivity activity)
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