package com.freeewaycoffee.ordermanager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class FCOrderManagerXMLHandler extends DefaultHandler 
{

	// Sent by Signin
	private static final Integer MAIN_COMPAT_SCHEMA_REV=5;
		
	
	
	public FCOrderManagerXMLHelper.SignonResponseEnum SignonResponse;
	public FCOrderManagerXMLHelper.ResponseTypeEnum ResponseType;
	public boolean NetworkError;
	public boolean MainSchemaCompatibilityError;
	public String CompatReleaseRequired;
		
		
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
		
		SignonResponse=FCOrderManagerXMLHelper.SignonResponseEnum.NONE;
		ResponseType=FCOrderManagerXMLHelper.ResponseTypeEnum.UNKNOWN;
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

		if (localName.equals(FCOrderManagerXMLHelper.NETWORK_ERROR_TAG))
		{

			NetworkError=true;
		}
		else if (localName.equals(FCOrderManagerXMLHelper.SIGNON_RESPONSE_TAG))
		{
			ResponseType=FCOrderManagerXMLHelper.ResponseTypeEnum.SIGNON;
			CheckSchema(atts);
			String SignonResponseStr = atts.getValue(FCOrderManagerXMLHelper.RESULT_ATTR);
			try
			{
				SignonResponseStr=URLDecoder.decode(SignonResponseStr,FCOrderManagerXMLHelper.URL_DECODE_TYPE);
				if(SignonResponseStr.equals(FCOrderManagerXMLHelper.SIGNON_RESPONSE_OK_ATTR))
				{
					SignonResponse=FCOrderManagerXMLHelper.SignonResponseEnum.OK;
				}
				else
				{
					SignonResponse=FCOrderManagerXMLHelper.SignonResponseEnum.ERROR;
				}
					
			}
			catch(UnsupportedEncodingException e)
			{
				SignonResponse=FCOrderManagerXMLHelper.SignonResponseEnum.ERROR;
			}
		}
		else if(localName.equals(FCOrderManagerXMLHelper.ERROR_TAG))
		{
			// TODO FIXME ERROR NEED TO FIX
			//FCOrderManagerError Error;
			//FCOrderManagerXMLHelper.DoConvertAttrsToStringMap(atts,Error)
				//	appState.GetLastError());
		}
		else if(localName.equals(FCOrderManagerXMLHelper.USER_INFO_TAG))
        {
        	ProcessUserInfoItems(atts);
        }
		else if(localName.equals(FCOrderManagerXMLHelper.USER_LOCATION_TAG))
        {
        	ProcessLocationItems(atts);
        }
	} 

	private void CheckSchema(Attributes atts)
	{
		String Schema = atts.getValue(FCOrderManagerXMLHelper.ATTR_MAIN_SCHEMA_COMPAT_LEVEL);
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

		CompatReleaseRequired = atts.getValue(FCOrderManagerXMLHelper.ATTR_MAIN_SCHEMA_COMPAT_RELEASE_NEEDED);
				
		if(CompatReleaseRequired!=null)
		{
			try
			{
				CompatReleaseRequired=URLDecoder.decode(CompatReleaseRequired,FCOrderManagerXMLHelper.URL_DECODE_TYPE);
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
		
	}

	private void ProcessUserInfoItems(Attributes atts)
    {
    	 FCOrderManagerXMLHelper.DoConvertAttrsToStringMap(atts,appState.GetUserInfoData());
    }
	private void ProcessLocationItems(Attributes atts)
    {
    	 FCOrderManagerXMLHelper.DoConvertAttrsToStringMap(atts,appState.GetUserLocationData());
    }

}
