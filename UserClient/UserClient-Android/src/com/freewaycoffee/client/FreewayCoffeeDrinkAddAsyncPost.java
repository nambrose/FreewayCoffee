package com.freewaycoffee.client;

import java.util.ArrayList;

import org.apache.http.client.methods.HttpPost;

import android.os.AsyncTask;

public class FreewayCoffeeDrinkAddAsyncPost extends AsyncTask<HttpPost,Integer,String>
{
	
	private FreewayCoffeeDrinkAddActivity myActivity;
	private FreewayCoffeeApp myApp;
	private boolean Completed;
	private String Response;
	public ArrayList <FreewayCoffeeFoodDrinkOptionListItem> SavedListIndex;
	private String ProgressMessage;
	
	public FreewayCoffeeDrinkAddAsyncPost(FreewayCoffeeDrinkAddActivity activity, FreewayCoffeeApp app)
	{
		myActivity = activity;
		myApp = app;
		Response=null;
		Completed=false;
		SavedListIndex=null;
	}
	
	
	
	protected String doInBackground(HttpPost... Post)
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
			Response = HTTPRef.ExecuteHTTPPost(Post[0]);
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
	
	
	public void SetActivity(FreewayCoffeeDrinkAddActivity activity)
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
