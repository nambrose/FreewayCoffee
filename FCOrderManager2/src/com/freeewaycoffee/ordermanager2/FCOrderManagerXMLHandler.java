package com.freeewaycoffee.ordermanager2;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.freewaycoffee.clientobjlib.FCLocation;
import com.freewaycoffee.clientobjlib.FCLocationAllowedArrivalMethod;
import com.freewaycoffee.clientobjlib.FCLocationAllowedPayMethod;
import com.freewaycoffee.clientobjlib.FCUserData;
import com.freewaycoffee.clientobjlib.FCXMLHelper;


public class FCOrderManagerXMLHandler extends DefaultHandler 
{

	// Sent by Signin
	public static final Integer MAIN_COMPAT_SCHEMA_REV=10;
		
	
	
	public FCXMLHelper.ResponseEnum Response;
	public FCXMLHelper.ResponseTypeEnum ResponseType;
	public boolean NetworkError;
	public boolean MainSchemaCompatibilityError;
	public String CompatReleaseRequired;
		
	public FCUserData m_UserData;
	public FCLocation m_CurrentLocation;
		
	private FCOrderManagerApp appState;

	public FCOrderManagerXMLHandler(FCOrderManagerApp state)
	{
		NetworkError=false;
		appState = state;
	}

	@Override
	public void startDocument() throws SAXException
	{
		// Some sort of setting up work
		NetworkError=false;
		MainSchemaCompatibilityError=false;
		
		Response=FCXMLHelper.ResponseEnum.NONE;
		ResponseType=FCXMLHelper.ResponseTypeEnum.UNKNOWN;
	} 

	@Override
	public void endDocument() throws SAXException
	{
		// Some sort of finishing up work
	} 

	@Override
	public void startElement(String namespaceURI, String localName, String qName, 
			Attributes atts) throws SAXException
	{
		if(IsSchemaError())
		{
			return;
		}

		if (localName.equals(FCXMLHelper.NETWORK_ERROR_TAG))
		{

			NetworkError=true;
		}
		else if (localName.equals(FCXMLHelper.SIGNON_RESPONSE_TAG))
		{
			ResponseType=FCXMLHelper.ResponseTypeEnum.SIGNON;
			CheckSchema(atts);
			
			Response = FCXMLHelper.ParseResultCodeFromAttributes(atts);
		}
		else if(localName.equals(FCXMLHelper.ERROR_TAG))
		{
			// TODO FIXME ERROR NEED TO FIX
			//FCOrderManagerError Error;
			//FCOrderManagerXMLHelper.DoConvertAttrsToStringMap(atts,Error)
				//	appState.GetLastError());
		}
		else if(localName.equals(FCUserData.USER_INFO_TAG))
        {
        	ProcessUserInfoItems(atts);
        }
		else if(localName.equals(FCXMLHelper.USER_LOCATION_TAG))
        {
			m_CurrentLocation = FCLocation.ParseFromXMLAttributes(atts);
        }
		else if(localName.equals(FCLocationAllowedArrivalMethod.LOCATION_ARRIVE_MODE_TAG))
        {
			if(m_CurrentLocation!=null)
			{
				FCLocationAllowedArrivalMethod Method = FCLocationAllowedArrivalMethod.ParseFromXMLAttributes(atts);
				if(Method!=null)
				{
					m_CurrentLocation.AddAllowedArrivalMode(Method);
				}
			}
        }
		else if(localName.equals(FCLocationAllowedPayMethod.LOCATION_PAY_METHOD_TAG))
        {
			if(m_CurrentLocation!=null)
			{
				FCLocationAllowedPayMethod Pay = FCLocationAllowedPayMethod.ParseFromXMLAttributes(atts);
				if(Pay!=null)
				{
					m_CurrentLocation.AddAllowedPaymentMethods(Pay);
				}
			}
        }
		
	} 

	private void CheckSchema(Attributes atts)
	{
		String Schema = atts.getValue(FCXMLHelper.ATTR_MAIN_SCHEMA_COMPAT_LEVEL);
		try
		{
			Integer SchemaRev = Integer.parseInt(Schema);
			if(SchemaRev!=MAIN_COMPAT_SCHEMA_REV)
			{
				MainSchemaCompatibilityError=true;
			}
		}
		catch(NumberFormatException e)
		{
			MainSchemaCompatibilityError=true;
		}

		CompatReleaseRequired = atts.getValue(FCXMLHelper.ATTR_MAIN_SCHEMA_COMPAT_RELEASE_NEEDED);
				
		if(CompatReleaseRequired!=null)
		{
			try
			{
				CompatReleaseRequired=URLDecoder.decode(CompatReleaseRequired,FCXMLHelper.URL_DECODE_TYPE);
			}
			catch (UnsupportedEncodingException e)
			{
				// TODO FIXME but really its just a hardcoded UTF-8 string
				CompatReleaseRequired=appState.getString(R.string.fc_om_unknown_release);
			}
		}
		else
		{
			CompatReleaseRequired=appState.getString(R.string.fc_om_unknown_release);
		}
	}

	public boolean IsSchemaError()
	{
		return MainSchemaCompatibilityError;
	}


	@Override
	public void characters(char ch[], int start, int length)
	{
		/* if(buffering)
	        {
	            buff.append(ch, start, length);
	        }
		 */
	} 

	@Override
	public void endElement(String namespaceURI, String localName, String qName) 
			throws SAXException
	{
		if(IsSchemaError())
		{
			return;
		}
		if(localName.equals(FCXMLHelper.USER_LOCATION_TAG))
        {
        	
        }
		
	}

	private void ProcessUserInfoItems(Attributes atts)
    {
		m_UserData = FCUserData.ParseFromXMLAttributes(atts);
    }

}
