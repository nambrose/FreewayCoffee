package com.freewaycoffee.client;

import java.net.URLDecoder;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FreewayCoffeeTimeToLocationXMLHandler extends DefaultHandler 
{
	public String signonResponse;
	
	private FreewayCoffeeApp appState;
	public boolean NetworkError;
	public boolean UpdateTimeToLocationFlag;
	
	
	public FreewayCoffeeTimeToLocationXMLHandler(FreewayCoffeeApp state)
	{
		appState = state;
		NetworkError=false;
		UpdateTimeToLocationFlag=false;
	}
	
	@Override
    public void startDocument() throws SAXException
    {
        // Some sort of setting up work
		NetworkError=false;
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
    	if (localName.equals(FreewayCoffeeXMLHelper.NETWORK_ERROR_TAG))
        {
           
    		NetworkError=true;
        }
    	else if (localName.equals(FreewayCoffeeXMLHelper.UPDATE_TIME_TO_LOCATION_TAG))
        {
        	String ResponseStr = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.UPDATE_TIME_TO_LOCATION_RESULT_ATTR));
        	if(ResponseStr.equals("ok"))
        	{
        		UpdateTimeToLocationFlag=true;
        	}
        }
        else if (localName.equals(FreewayCoffeeXMLHelper.SIGNON_RESPONSE_TAG))
        {
           //buff = new StringBuffer("");
            //buffering = true;
        	signonResponse = URLDecoder.decode(atts.getValue("result"));
        }
        
        
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
        
    }
}

