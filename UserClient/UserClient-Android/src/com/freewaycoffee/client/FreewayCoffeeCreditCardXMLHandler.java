package com.freewaycoffee.client;

import java.net.URLDecoder;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FreewayCoffeeCreditCardXMLHandler extends DefaultHandler 
{
	public String signonResponse;
	public String updateCardResponse;
	public HashMap<String,String> TheCard;
	private FreewayCoffeeApp appState;
	public boolean NetworkError;
	
	
	public FreewayCoffeeCreditCardXMLHandler(FreewayCoffeeApp state)
	{
		appState = state;
		TheCard=null;
		NetworkError=false;
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
    	else if (localName.equals(FreewayCoffeeXMLHelper.CREDIT_CARD_UPDATE_RESPONSE_TAG))
        {
    		updateCardResponse = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.UPDATE_CREDIT_CARD_RESULT_ATTR));
        }
        else if (localName.equals(FreewayCoffeeXMLHelper.SIGNON_RESPONSE_TAG))
        {
           //buff = new StringBuffer("");
            //buffering = true;
        	signonResponse = URLDecoder.decode(atts.getValue("result"));
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.ERROR_TAG))
        {
        	FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,appState.GetLastError());
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_CREDIT_CARDS_TAG))
        {
        	
        	TheCard=new HashMap<String,String>();
        	FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,TheCard);
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

