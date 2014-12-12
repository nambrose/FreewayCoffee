package com.freeewaycoffee.ordermanager2;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.methods.HttpPost;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


public class FCOrderManagerOrderMainAsync extends AsyncTask<HttpPost,Integer,FCOrderManagerOrderMainXMLHandler> 
		{
			/*
			 * Executes a URL GET in background. Retrieves XML string, and passes back to UI thread.
			 * Does not attempt to interpret anything. Just to keep the UI thread running without "not responding"
			 */
				
			private FCOrderManagerOrderMainActivity myActivity;
			private FCOrderManagerApp myApp;
			private boolean Completed;
			private FCOrderManagerOrderMainXMLHandler ResponseObject;
			
				
			public FCOrderManagerOrderMainAsync(FCOrderManagerOrderMainActivity activity, FCOrderManagerApp app)
			{
				
				myActivity = activity;
				myApp = app;
				ResponseObject=null;
				Completed=false;
			}
				
			@Override
			protected void onPreExecute()
			{
				Completed=false;
				//myActivity.showProgressDialog();
			}
			
			protected FCOrderManagerOrderMainXMLHandler doInBackground(HttpPost... Post)
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
					String Response = HTTPRef.ExecuteHTTPPost(Post[0]);
					Log.w("OM","Response:" + Response);
					
					SAXParserFactory spf = SAXParserFactory.newInstance();
					
					SAXParser sp = spf.newSAXParser();

					/* Get the XMLReader of the SAXParser we created. */
					XMLReader xr = sp.getXMLReader();

					ResponseObject = new FCOrderManagerOrderMainXMLHandler(myApp);
					xr.setContentHandler(ResponseObject);

					/* Parse the xml-data from our URL. */

					InputSource is = new InputSource(new StringReader(Response));

					xr.parse(is);
						/* Parsing has finished. */
					return ResponseObject;
				}
				catch(SAXException e)
				{
					Log. e ("SAXException", e.toString ());
					//DisplayNetworkError();
					return null;
				}
				catch (ParserConfigurationException pe)
				{
					//DisplayNetworkError();
					Log. e ("ParseConfig Exception", pe.toString ());
					return null;
				}
				catch (IOException io)
				{
					//DisplayNetworkError();
					Log. e ("Input Output Exception", io.toString ());
					return null;
				}
				/*
				catch (Exception e) 
				{
					Log. e ("Exception", e.toString ());
					return null;
					
				}*/
			}
			
			public void UnlinkActivity()
			{
				myActivity=null;
			}
			
			public void SetActivity(FCOrderManagerOrderMainActivity activity)
			{
				// This case is where the async completed but the user rotated, and we now need to notify a new activity.
				myActivity = activity;
				if(IsCompleted())
				{
					
					myActivity.ProcessXMLResult(ResponseObject);
				}
			}
			
			public boolean IsCompleted()
			{
				return Completed;
			}
			
			
			protected void onPostExecute(FCOrderManagerOrderMainXMLHandler result)
		    {
				if((myActivity!=null) && !isCancelled())
				{
					Completed=true;
					myActivity.ProcessXMLResult(result);
				}
		    }


		}


