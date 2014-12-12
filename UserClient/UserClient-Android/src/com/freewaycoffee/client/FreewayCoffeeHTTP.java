package com.freewaycoffee.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class FreewayCoffeeHTTP
{
	private FreewayCoffeeApp appState;
	
	public FreewayCoffeeHTTP(FreewayCoffeeApp state)
	{
		appState = state;
	}
	
	public String ExecuteHTTPPost(HttpPost Post)
	{
		String Result="";
		try
		{
			FreewayCoffeeHTTPClient client =appState.GetHttpClient();
			HttpResponse response = client.execute(Post);
			//HttpEntity entity = response.getEntity();
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			
			while ((line = rd.readLine()) != null)
			{
				//Log.w("FreewayCoffeeDB",line);
				Result+=line;
			}
			
			
		}
		catch (ClientProtocolException e)
		{
			
		}
		catch (IOException e)
		{
			Result=e.toString();
		}
		return Result;

	}
	
	public String ExecuteHTTPGet(String URLString)
	{
		String Result="";
		try
		{
			// Execute HTTP Get Request  
			FreewayCoffeeHTTPClient client =appState.GetHttpClient();
			HttpGet request = new HttpGet(URLString);
			HttpResponse response = (client.execute(request));  

			//HttpEntity entity = response.getEntity();
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			
			while ((line = rd.readLine()) != null)
			{
				//Log.w("FreewayCoffeeDB",line);
				Result+=line;
			}
	
		}
		
		//POST section
		//catch (UnsupportedEncodingException e)  
		//{
			
		//}
		//POST & GET section
		catch (NullPointerException e)
		{
			return FreewayCoffeeXMLHelper.NETWORK_ERROR_XML;
		}
		//POST section
		//catch (ClientProtocolException e)
		//{
			
		//}
		catch (ConnectTimeoutException e)
		{
			return FreewayCoffeeXMLHelper.NETWORK_ERROR_XML;
		}
		catch(IOException e)
		
		{
			//e.printStackTrace();
			return FreewayCoffeeXMLHelper.NETWORK_ERROR_XML;
		}
		// POST & GET section
		return Result;

	}
}