package com.freewaycoffee.client;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;


import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

import com.freewaycoffee.clientobjlib.FCAppSetting;
import com.freewaycoffee.clientobjlib.FCUserData;
import com.freewaycoffee.clientobjlib.FCXMLHelper;

import android.util.Log;

public class FreewayCoffeeXMLHandler extends DefaultHandler 
{
	// Sent by Signin and Signup
	public static final Integer MAIN_COMPAT_SCHEMA_REV=10;
	
	// What kind of response did we get.
	public String signupResponse;
	public String signonResponse;
	public String orderResponse;
	public String orderHereResponse;
	
	
	public String UserItemsCompatLevel;
	public boolean NetworkError;
	public boolean MainSchemaCompatibilityError;
	public String CompatReleaseRequired;
	public HashMap<String,String> TheOrderLocation;
	public HashMap<String,String> TheOrder;
	public HashMap<String,String> m_OrderCreditCard;
	FreewayCoffeeUserDrink m_CurrentDrink;
	public Integer m_OrderID;
	private FreewayCoffeeApp appState;
	public ArrayList<FreewayCoffeeOrderItem> m_OrderItems;
	
	
	public FreewayCoffeeXMLHandler(FreewayCoffeeApp state)
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
		signupResponse=null;
		signonResponse=null;
		orderResponse=null;
		orderHereResponse=null;
		TheOrderLocation=null;
		m_CurrentDrink=null;
		m_OrderID=-1;
		TheOrder=null;
		m_OrderItems=null;
		m_OrderCreditCard=null;
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
    	
    	if (localName.equals(FreewayCoffeeXMLHelper.NETWORK_ERROR_TAG))
        {
           
    		NetworkError=true;
        }
    	else if (localName.equals(FreewayCoffeeXMLHelper.REGISTER_RESPONSE_TAG))
        {
           
        	signupResponse = atts.getValue("result");
        	signupResponse = URLDecoder.decode(signupResponse);
        	CheckSchema(atts);
        	
        	
        }
        else if (localName.equals(FreewayCoffeeXMLHelper.SIGNON_RESPONSE_TAG))
        {
           CheckSchema(atts);
        	signonResponse = atts.getValue("result");
        	signonResponse=URLDecoder.decode(signonResponse);
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.ERROR_TAG))
        {
        	FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,appState.GetLastError());
        }
    	
    	
    	else if(localName.equals(FreewayCoffeeXMLHelper.USER_ORDER_RESPONSE_TAG))
        {
        	orderResponse = atts.getValue("result");
        	orderResponse=URLDecoder.decode(orderResponse);
        	String Attr = atts.getValue(FreewayCoffeeXMLHelper.ORDER_ID_ATTR);
        	m_OrderID=-1;
        	try
    		{
        		if(Attr!=null)
        		{
        			m_OrderID = Integer.parseInt(Attr);
        		}
    		}
    		catch(NumberFormatException e)
    		{
    			// TODO Log
    			
    		}
   
        	String ParseValue = atts.getValue(FCUserData.USER_INFO_USER_FREE_DRINKS);
        	if(ParseValue!=null)
        	{
        		ParseValue = URLDecoder.decode(ParseValue);
        		appState.GetUserInfoData().put(FCUserData.USER_INFO_USER_FREE_DRINKS, ParseValue);
        		Log.w("FC","Order Response, Updating Free Drinks to:" + ParseValue);
        	}
        			
        }
    	
    	else if(localName.equals(FreewayCoffeeXMLHelper.OM_ORDER_ITEM_TAG))
        {
    		if(m_OrderItems==null)
    		{
    			m_OrderItems = new ArrayList<FreewayCoffeeOrderItem>();
    		}
    		FreewayCoffeeOrderItem Item = new FreewayCoffeeOrderItem();
    		if(ProcessOrderItem(atts,Item))
    		{
    			m_OrderItems.add(Item);
    		}
    		
        	
        }
    	else if(localName.equals(FreewayCoffeeXMLHelper.ORDER_CREDIT_CARD_TAG))
        {
    		m_OrderCreditCard = new HashMap<String,String>();
        	
        	FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,m_OrderCreditCard);
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.ORDER_LOCATION_TAG))
        {

        	TheOrderLocation = new HashMap<String,String>();
        	FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,TheOrderLocation);
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.ORDER_TAG))
        {
        	TheOrder = new HashMap<String,String>();
        	FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,TheOrder);
        }
    	
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_ORDER_HERE_RESPONSE_TAG))
        {
        	orderHereResponse = atts.getValue("result");
        	orderHereResponse=URLDecoder.decode(orderHereResponse);
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_ITEMS_TAG))
        {
        	
        	UserItemsCompatLevel = atts.getValue(FreewayCoffeeXMLHelper.USER_ITEMS_COMPAT_ATTR);
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_INFO_TAG))
        {
        	ProcessUserInfoItems(atts);
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_LOCATION_TAG))
        {
        	ProcessLocationInfoItems(atts);
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_CREDIT_CARDS_TAG))
        {
        	FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,appState.GetCreditCardsData());	
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_FOODS_TAG))
        {
        	// Nothing
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_FOOD_TAG))
        {
        	FreewayCoffeeXMLHelper.ProcessListOfItems(atts,appState.GetFoodData());
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_DRINKS_TAG))
        {
        	// Nothing (all is in sub-elements)
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_DRINK_TAG))
        {
        	//FreewayCoffeeXMLHelper.ProcessListOfItems(atts,appState.GetDrinksData());
        	m_CurrentDrink = new FreewayCoffeeUserDrink();
        	if(FreewayCoffeeXMLHelper.ParseUserDrinkAttrs(m_CurrentDrink,atts)==false)
        	{
        		m_CurrentDrink=null;
        	}
        	
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_DRINK_OPTION_TAG))
        {
        	if(m_CurrentDrink!=null)
        	{
        		FreewayCoffeeFoodDrinkUserOption Option = new FreewayCoffeeFoodDrinkUserOption();
        		if(FreewayCoffeeXMLHelper.ParseUserDrinkOptions( appState,Option,atts))
        		{
        			m_CurrentDrink.AddUserDrinkOption(Option);
        		}
        	}
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_CAR_DATA_TAG))
        {
        	FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,appState.GetUserCarData());
        }
        else if(localName.equals(FCXMLHelper.APP_SETTING_TAG))
        {
        	FCAppSetting Setting = new FCAppSetting();
        	if(FCXMLHelper.ParseAppSetting(atts,Setting))
        	{
        		appState.GetAppSettingsTable().addAppSetting(Setting);
        	}
        	FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,appState.GetUserCarData());
        }
    	
    	
    	
    } 
    
    private void CheckSchema(Attributes atts)
    {
    	String Schema = atts.getValue(FreewayCoffeeXMLHelper.ATTR_MAIN_SCHEMA_COMPAT_LEVEL);
	
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
	
    	CompatReleaseRequired = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.ATTR_MAIN_SCHEMA_COMPAT_RELEASE_NEEDED));
    	
    }
    
    public boolean IsSchemaError()
    {
    	return MainSchemaCompatibilityError;
    }
    
    private void ProcessUserInfoItems(Attributes atts)
    {
    	 FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,appState.GetUserInfoData());
    }
    
    private void ProcessLocationInfoItems(Attributes atts)
    {
    
    	 FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,appState.GetLocationData());
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
    
    private boolean ProcessOrderItem(Attributes atts, FreewayCoffeeOrderItem Item)
    {
    	String ParsedAttr;
		
		// DRINK TYPE
		ParsedAttr = atts.getValue("id");
		Item.m_OrderItemID=ParsedAttr = URLDecoder.decode(ParsedAttr);
		if(Item.m_OrderItemID==null)
		{
			return false;
		}
		ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.OM_ORDER_ITEM_DESCR_ATTR);
		Item.m_OrderItemDescription=ParsedAttr = URLDecoder.decode(ParsedAttr);
		if(Item.m_OrderItemDescription==null)
		{
			return false;
		}
		ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.OM_ORDER_ITEM_COST_ATTR);
		Item.m_OrderItemCost=ParsedAttr = URLDecoder.decode(ParsedAttr);
		if(Item.m_OrderItemCost==null)
		{
			return false;
		}
		return true;
    }
	
    @Override
    public void endElement(String namespaceURI, String localName, String qName) 
    throws SAXException
    {
    	if(IsSchemaError())
    	{
    		return;
    	}
        if (localName.equals(FreewayCoffeeXMLHelper.REGISTER_RESPONSE_TAG))
        {
           
        }
        else if (localName.equals(FreewayCoffeeXMLHelper.SIGNON_RESPONSE_TAG))
        {
           
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_DRINKS_TAG))
        {
        	// At the end of the Drinks data, generate the default order.
        	appState.GenerateDefaultDrinksOrder(); // Copy the DrinksData DrinkIDs to the order array;
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_DRINK_TAG))
        {
        	//FreewayCoffeeXMLHelper.ProcessListOfItems(atts,appState.GetDrinksData());
        	m_CurrentDrink.SortUserDrinkOptions();
        	appState.AddUserDrink(m_CurrentDrink);
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_FOODS_TAG))
        {
        	// At the end of the Food data, generate the default order.
        	appState.GenerateDefaultFoodOrder(); // Copy the FoodData FoodIDs to the order array;
        }
        
    }
}
