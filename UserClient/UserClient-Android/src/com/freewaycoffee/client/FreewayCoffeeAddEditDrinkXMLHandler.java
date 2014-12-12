package com.freewaycoffee.client;

import java.net.URLDecoder;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FreewayCoffeeAddEditDrinkXMLHandler extends DefaultHandler 
{
	public String DrinkDeletedResponse;
	public String FoodDeletedResponse;
	public String DrinkDeletedID;
	public String FoodDeletedID;
	public String signonResponse;
	public String addDrinkResponse;
	public String editDrinkResponse;
	private FreewayCoffeeApp appState;
	public boolean NetworkError;
	private FreewayCoffeeFoodDrinkOption CurrentDrinkOption;
	private FreewayCoffeeFoodDrinkType CurrentDrinkType;
	public FreewayCoffeeFoodDrinkOptionGroup m_DrinkOptionGroup; 
	FreewayCoffeeUserDrink m_TheDrink;
	
	
	public FreewayCoffeeAddEditDrinkXMLHandler(FreewayCoffeeApp state)
	{
		appState = state;
		m_TheDrink=null;
		NetworkError=false;
		editDrinkResponse=null;
		addDrinkResponse=null;
		m_DrinkOptionGroup=null;
		DrinkDeletedResponse=null;
		FoodDeletedResponse=null;
		DrinkDeletedID=null;
		FoodDeletedID=null;
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
    
    // NOTE! WHen updating this parser, also update the DrinkPicker parser as it shares some of this data (FIXME )
    @Override
    public void startElement(String namespaceURI, String localName, String qName, 
            					Attributes atts) throws SAXException
    {
    	if (localName.equals(FreewayCoffeeXMLHelper.NETWORK_ERROR_TAG))
        {
           
    		NetworkError=true;
        }
    	else if (localName.equals(FreewayCoffeeXMLHelper.USER_ADD_DRINK_TAG))
        {
        	addDrinkResponse = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.ADD_DRINK_RESULT_ATTR));
        }
    	else if(localName.equals(FreewayCoffeeXMLHelper.USER_EDIT_DRINK_TAG))
    	{
    		editDrinkResponse = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.RESULT_ATTR));
    	}
       	else if(localName.equals(FreewayCoffeeXMLHelper.DRINK_DELETED_TAG))
        {
    		DrinkDeletedResponse = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.RESULT_ATTR));
    		DrinkDeletedID = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.DELETED_DRINK_ID_ATTR));
        }
    	else if(localName.equals(FreewayCoffeeXMLHelper.USER_FOOD_DELETE_RESPONSE_TAG))
        {
    		FoodDeletedResponse = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.RESULT_ATTR));
    		FoodDeletedID = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.DELETED_FOOD_ID_ATTR));
        }
        else if (localName.equals(FreewayCoffeeXMLHelper.SIGNON_RESPONSE_TAG))
        {
        	signonResponse = URLDecoder.decode(atts.getValue("result"));
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_DRINK_TAG))
        {
        	m_TheDrink=new FreewayCoffeeUserDrink();
        	
        	if(FreewayCoffeeXMLHelper.ParseUserDrinkAttrs(m_TheDrink,atts)==false)
        	{
        		m_TheDrink=null;
        	}
        	
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_DRINK_OPTION_TAG))
        {
        	if(m_TheDrink!=null)
        	{
        		FreewayCoffeeFoodDrinkUserOption Option = new FreewayCoffeeFoodDrinkUserOption();
        		if(FreewayCoffeeXMLHelper.ParseUserDrinkOptions(appState, Option,atts))
        		{
        			m_TheDrink.AddUserDrinkOption(Option);
        		}
        	}
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.DRINK_TYPE_TAG))
        {
        	CurrentDrinkType = new FreewayCoffeeFoodDrinkType();
        	FreewayCoffeeXMLHelper.ParseDrinkType(atts,CurrentDrinkType);
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.DRINK_TYPE_OPTION_TAG))
        {
        	if(CurrentDrinkType!=null)
        	{
        		FreewayCoffeeFoodDrinkTypeOption CurrentDTOption = new FreewayCoffeeFoodDrinkTypeOption();
        		if(FreewayCoffeeXMLHelper.ParseDrinkTypeOption(atts,CurrentDTOption)==true)
        		{
        			CurrentDrinkType.AddDrinkTypeOption(CurrentDTOption);
        		}
        	}
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.DRINK_OPTION_GROUP_TAG))
        {
        	m_DrinkOptionGroup = new  FreewayCoffeeFoodDrinkOptionGroup(); ; 
        	FreewayCoffeeXMLHelper.ParseDrinkOptionGroup(atts,m_DrinkOptionGroup);
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.DRINK_OPTION_TAG))
        {
        	if(m_DrinkOptionGroup!=null)
        	{
        		FreewayCoffeeFoodDrinkOption Option = new FreewayCoffeeFoodDrinkOption();
        		if(FreewayCoffeeXMLHelper.ParseDrinkOption(atts,Option))
        		{
        			m_DrinkOptionGroup.AddDrinkOption(Option);
        		}
        	}
        	 
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.DRINK_TYPES_MAND_OPTION))
        {
        	FreewayCoffeeDrinkTypeMandatoryOption Option;
        	
        	// TODO -- make this a generic thing ? right now, its tied to DrinkTypePart too much.
        	Option = FreewayCoffeeXMLHelper.ParseDrinkTypesMandatoryOption(atts);
        	if(Option!=null)
        	{
        		appState.GetDrinkTypesMandatoryOptionsData().add(Option);
        	}
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.DRINK_TYPES_DEFAULT_OPTION_TAG))
        {
        	// TODO -- make this a generic thing ? right now, its tied to DrinkTypePart too much.
        	FreewayCoffeeFoodDrinkTypeDefaultOption Option;
        	Option = FreewayCoffeeXMLHelper.ParseDrinkTypesDefaultOption(atts);
        	if(Option!=null)
        	{
        		appState.GetDrinkTypesDefaultOptionsData().add(Option);
        	}
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
    	else if(localName.equals(FreewayCoffeeXMLHelper.DRINK_OPTION_GROUP_TAG))
        {
        	appState.AddDrinkOptionGroup(m_DrinkOptionGroup);
        	m_DrinkOptionGroup=null;
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.DRINK_TYPE_TAG))
        {
        	appState.GetDrinkTypes().put(CurrentDrinkType.GetTypeID(), CurrentDrinkType);
        	CurrentDrinkType=null;
        }
    }
}

