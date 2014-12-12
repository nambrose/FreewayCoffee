package com.freeewaycoffee.ordermanager;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import android.os.AsyncTask;
import org.apache.http.client.methods.HttpPost;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


public class FCOrderManagerOrderMainAsync extends AsyncTask<String,Integer,FCOrderManagerOrderMainXMLHandler> 
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
			
			protected FCOrderManagerOrderMainXMLHandler doInBackground(String... URLGet)
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
					String Response = HTTPRef.ExecuteHTTPGet(URLGet[0]);
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

					//DisplayNetworkError();
					return null;
				}
				catch (ParserConfigurationException pe)
				{
					//DisplayNetworkError();
					return null;
				}
				catch (IOException io)
				{
					//DisplayNetworkError();
					return null;
				}
				catch (Exception e) 
				{
					return null;
					//Log. e ("Input Output Exception", e.toString ());
				}
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


