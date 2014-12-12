package com.freewaycoffee.client;
import java.util.ArrayList;

import org.apache.http.client.methods.HttpPost;

import android.os.AsyncTask;


public class FreewayCoffeeDrinkPickerAsyncPost extends AsyncTask<HttpPost,Integer,String>
{
	
	private FreewayCoffeeDrinkPickerActivity myActivity;
	private FreewayCoffeeApp myApp;
	private boolean Completed;
	private String Response;
	public ArrayList<FreewayCoffeeDrinkPickerPair> SavedListIndex;
	String ProgressMessage;
	
	public FreewayCoffeeDrinkPickerAsyncPost(FreewayCoffeeDrinkPickerActivity activity, FreewayCoffeeApp app,String Message)
	{
		myActivity = activity;
		myApp = app;
		Response=null;
		Completed=false;
		ProgressMessage=Message;
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
	
	@Override
	protected void onPreExecute()
	{
		Completed=false;
		myActivity.showProgressDialog(ProgressMessage);
	}
	
	public void UnlinkActivity()
	{
		myActivity=null;
	}
	
	public String GetProgressMessage()
	{
		return ProgressMessage;
	}
	
	public void SetActivity(FreewayCoffeeDrinkPickerActivity activity)
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
