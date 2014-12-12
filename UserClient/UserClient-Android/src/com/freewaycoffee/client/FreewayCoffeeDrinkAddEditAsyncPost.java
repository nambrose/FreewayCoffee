package com.freewaycoffee.client;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;

import android.os.AsyncTask;


public class FreewayCoffeeDrinkAddEditAsyncPost extends AsyncTask<HttpPost,Integer,String>
{
	
	private FreewayCoffeeDrinkAddEditActivity myActivity;
	private FreewayCoffeeApp myApp;
	private boolean Completed;
	private String Response;
	public ArrayList <FreewayCoffeeFoodDrinkOptionListItem> SavedListIndex;
	private String ProgressMessage;
	
	public FreewayCoffeeDrinkAddEditAsyncPost(FreewayCoffeeDrinkAddEditActivity activity, FreewayCoffeeApp app, String Message)
	{
		myActivity = activity;
		myApp = app;
		Response=null;
		Completed=false;
		SavedListIndex=null;
		ProgressMessage = Message;
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
	
	public String GetProgressMessage()
	{
		return ProgressMessage;
	}
	public void UnlinkActivity()
	{
		myActivity=null;
		
	}
	
	
	@Override
	protected void onPreExecute()
	{
		Completed=false;
		myActivity.showProgressDialog(GetProgressMessage());
	}
	
	
	public void SetActivity(FreewayCoffeeDrinkAddEditActivity activity)
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
