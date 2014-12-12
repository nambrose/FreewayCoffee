package com.freewaycoffee.client;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;
import android.util.Log;


public class FreewayCoffeeUserTagAsyncGet extends AsyncTask<String,Integer,String>
{
	
	private FreewayCoffeeUserTagActivity myActivity;
	private FreewayCoffeeApp myApp;
	private boolean Completed;
	private String Response;
	private String MessageToPrint;
	//public ArrayList <FreewayCoffeeDrinkOptionListItem> SavedListIndex;
	
	public FreewayCoffeeUserTagAsyncGet(FreewayCoffeeUserTagActivity activity, FreewayCoffeeApp app,String Message)
	{
		myActivity = activity;
		myApp = app;
		Response=null;
		Completed=false;
		MessageToPrint=Message;
		//SavedListIndex=null;
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
			String s = e.toString ();
			Log. e ("Input Output Exception", e.toString ());
			return FreewayCoffeeXMLHelper.NETWORK_ERROR_XML;
			//
			
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
		myActivity.showProgressDialog(MessageToPrint);
	}
	
	
	public void SetActivity(FreewayCoffeeUserTagActivity activity)
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
