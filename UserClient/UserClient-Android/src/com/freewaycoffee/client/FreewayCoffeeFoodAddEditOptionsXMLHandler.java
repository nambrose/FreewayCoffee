
package com.freewaycoffee.client;

import java.net.URLDecoder;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FreewayCoffeeFoodAddEditOptionsXMLHandler extends DefaultHandler 
{
	public String signonResponse;
	public String addFoodResponse;
	public String editFoodResponse;
	public HashMap<String,String> TheFood;
	private FreewayCoffeeApp appState;
	public boolean NetworkError;
	
	
	public FreewayCoffeeFoodAddEditOptionsXMLHandler(FreewayCoffeeApp state)
	{
		appState = state;
		Reset();
	}
	
	private void Reset()
	{
		TheFood=null;
		NetworkError=false;
		editFoodResponse=null;
		addFoodResponse=null;
	}
	@Override
    public void startDocument() throws SAXException
    {
        Reset();
    } 
    
    @Override
    public void endDocument() throws SAXException
    {
        // Some sort of finishing up work
    } 
    
    // NOTE! WHen updating this parser, also update the DrinkPicker parser as it shares some of this data (FIXME )
    @Override
    public void startElement(String namespaceURI, String localName, String qName, 
            					Attributes atts) throws SAXException
    {
    	if (localName.equals(FreewayCoffeeXMLHelper.NETWORK_ERROR_TAG))
        {
           
    		NetworkError=true;
        }
    	
    	else if (localName.equals(FreewayCoffeeXMLHelper.USER_ADD_FOOD_TAG))
        {
        	addFoodResponse = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.RESULT_ATTR));
        }
    	else if(localName.equals(FreewayCoffeeXMLHelper.USER_EDIT_FOOD_TAG))
    	{
    		editFoodResponse = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.RESULT_ATTR));
    	}
    	
        else if (localName.equals(FreewayCoffeeXMLHelper.SIGNON_RESPONSE_TAG))
        {
           //buff = new StringBuffer("");
            //buffering = true;
        	signonResponse = URLDecoder.decode(atts.getValue("result"));
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_FOOD_TAG))
        {
        	TheFood=new HashMap<String,String>();
        	
        	FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,TheFood);
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
    	if (localName.equals(FreewayCoffeeXMLHelper.SIGNON_RESPONSE_TAG))
        {
           // buffering = false; 
           // signupResponse = buff.toString();
        }
    }
}

