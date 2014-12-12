package com.freewaycoffee.client;

import java.util.ArrayList;

import android.os.AsyncTask;

public class FreewayCoffeeFeedbackAsync extends AsyncTask<String,Integer,String> 
{
	/*
	 * Executes a URL GET in background. Retrieves XML string, and passes back to UI thread.
	 * Does not attempt to interpret anything. Just to keep the UI thread running without "not responding"
	 */

	private FreewayCoffeeFeedbackActivity myActivity;
	private FreewayCoffeeApp myApp;
	//private boolean Completed;

	// TODO. I dont like this much. When we rotate, we save the async task (which may be in progress) and the List Inded and Android gives both back to us
	// in onCreate. This is kind of making my head spin a little. This is only valid through the rotate process.
	public ArrayList<FreewayCoffeeItemListIndexPair> SavedListIndex;
	String Response;

	public FreewayCoffeeFeedbackAsync(FreewayCoffeeFeedbackActivity activity, FreewayCoffeeApp app)
	{
		//Completed=false;
		myActivity = activity;
		myApp = app;
		Response=null;
		SavedListIndex=null;
	}


	protected String doInBackground(String... URLGet)
	{

		// Even though we potentially accept a list of strings (I think), we only want the first one for now. Baby steps. 

		try
		{
			FreewayCoffeeHTTP HTTPRef = myApp.GetHTTP();
			if (isCancelled())
			{
				//UnlinkActivity();
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


}
