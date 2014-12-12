package com.freeewaycoffee.ordermanager2;

import org.apache.http.client.methods.HttpPost;
import com.freewaycoffee.clientobjlib.FCXMLHelper;

import android.os.AsyncTask;

public class FCOrderManagerSignonAsync extends
		AsyncTask<HttpPost,Integer,String> 
		{
			/*
			 * Executes a URL GET in background. Retrieves XML string, and passes back to UI thread.
			 * Does not attempt to interpret anything. Just to keep the UI thread running without "not responding"
			 */
				
			private FCOrderManagerSignonActivity myActivity;
			private FCOrderManagerApp myApp;
			private boolean Completed;
			private String Response;
			public String EmailAddressSaved;
			public String PasswordSaved;
			public boolean LoginAutomaticallySaved;
				
			public FCOrderManagerSignonAsync(FCOrderManagerSignonActivity activity, FCOrderManagerApp app)
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
			
			protected String doInBackground(HttpPost... Post)
			{
					
				// Even though we potentially accept a list of strings (I think), we only want the first one for now. Baby steps. 
					
				try
				{
					FCOrderManagerHTTP HTTPRef = myApp.GetHTTP();
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
					
					 return FCXMLHelper.NETWORK_ERROR_XML;
					//Log. e ("Input Output Exception", e.toString ());
				}
			}
			
			public void UnlinkActivity()
			{
				myActivity=null;
			}
			
			public void SetActivity(FCOrderManagerSignonActivity activity)
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


