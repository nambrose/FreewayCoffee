package com.freewaycoffee.client;
import java.net.URLDecoder;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class FreewayCoffeeDrinkPickerXMLHandler extends DefaultHandler
{

	public String signonResponse;
	public String DrinkDeletedResponse;
	public String DrinkDeletedID;
	public boolean signonResponseSeen;
	public boolean NetworkError;
	public String DrinksPickerCompatLevel;
	public String FoodPickerCompatLevel;
	private FreewayCoffeeApp appState;
	private FreewayCoffeeFoodDrinkOption CurrentFoodOption;
	private FreewayCoffeeFoodDrinkType CurrentFood;
	public String FoodDeletedResponse;
	public String FoodDeletedID;
	public FreewayCoffeeFoodDrinkOptionGroup m_DrinkOptionGroup; 
	private FreewayCoffeeFoodDrinkType CurrentDrinkType;

	// Keep the code around for a while until we can get rid of it but make sure not to use.
	private FreewayCoffeeDrinkPickerXMLHandler(FreewayCoffeeApp state)
	{
		appState = state;
		Reset();
	}
	
	public void Reset()
	{
		DrinkDeletedID=null;
		
		CurrentDrinkType=null;
		signonResponse=null;
		DrinkDeletedResponse=null;
		NetworkError=false;
		CurrentFood=null;
		CurrentFoodOption=null;
		DrinkDeletedResponse=null;
		FoodDeletedResponse=null;
		FoodDeletedID=null;
		DrinkDeletedID=null;
		m_DrinkOptionGroup = null;
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
    
    
    // NOTE!!! When updating this parser, you also have to update DrinkAddEditXMLHandler because it also needs to parse some of the same data
    @Override
    public void startElement(String namespaceURI, String localName, String qName, 
            					Attributes atts) throws SAXException
    {
    	if (localName.equals(FreewayCoffeeXMLHelper.NETWORK_ERROR_TAG))
        {
           
    		NetworkError=true;
        }
    	else if (localName.equals(FreewayCoffeeXMLHelper.SIGNON_RESPONSE_TAG))
        {
        	signonResponse = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.RESULT_ATTR));
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
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_DRINKS_TAG))
        {
        	// Nothing (all is in sub-elements)
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_DRINK_TAG))
        {
        	// Already got this on the previos. Maybe check the rev # ? for now keep it stupid, simple.
        //	FreewayCoffeeXMLHelper.ProcessListOfItems(atts,DrinksData);
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.DRINK_OPTION_GROUP_TAG))
        {
        	m_DrinkOptionGroup = new FreewayCoffeeFoodDrinkOptionGroup();
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
    	// FOOD
        else if(localName.equals(FreewayCoffeeXMLHelper.FOOD_OPTIONS_TAG))
	    {
	    	// NOTHING
	    }
	    else if(localName.equals(FreewayCoffeeXMLHelper.FOOD_TYPE_TAG))
	    {
	    	HashMap<String,String> FoodTypeData = new HashMap<String,String>();
	    	FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,FoodTypeData);
	    	try
	       	{
	       		String IDStr = URLDecoder.decode(atts.getValue("id"));
	       		Integer ID = Integer.parseInt(IDStr);
	       		appState.AddFoodCategory(ID,FoodTypeData);
        	}
	       	catch(NumberFormatException e)
	       	{
	       		// TODO LOG FIXME
	      		
        	}    
	    	
	    }
	    else if(localName.equals(FreewayCoffeeXMLHelper.FOOD_DEFAULT_OPTIONS))
	    {
	    	// NOTHING
	    }
	    else if(localName.equals(FreewayCoffeeXMLHelper.FOOD_DEFAULT_OPTION))
	    {
	    	// TODO -- make this a generic thing ? right now, its tied to DrinkTypePart too much.
        	FreewayCoffeeFoodDrinkTypeDefaultOption Option;
        	Option = FreewayCoffeeXMLHelper.ParseFoodTypesDefaultOption(atts);
        	if(Option!=null)
        	{
        		appState.GetFoodTypesDefaultOptionsData().add(Option);
        	}
	    }
	    else if(localName.equals(FreewayCoffeeXMLHelper.FOOD_TYPES_TAG))
	    {
	    	// NOTHING
	    }
		else if(localName.equals(FreewayCoffeeXMLHelper.FOOD_OPTION_TAG))
		{
			/*
	        CurrentFoodOption=new FreewayCoffeeFoodDrinkOption();
	       	CurrentFoodOption.SetType(URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.FOOD_OPTION_TYPE)));
	       	CurrentFoodOption.SetPickType(URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.FOOD_OPTION_PICK_TYPE)));
	       	CurrentFoodOption.SetLabel(URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.FOOD_OPTION_LABEL)));
	       	try
	       	{
	       		String SortOrderStr = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.SORT_ORDER_ATTR));
	       		Integer SortOrder = Integer.parseInt(SortOrderStr);
	       		CurrentFoodOption.SetSortOrder(SortOrder);
        	}
	       	catch(NumberFormatException e)
	       	{
	       		// TODO LOG FIXME
	      		CurrentFoodOption.SetSortOrder(-1);
        	}   
        	*/ 
		}
		else if(localName.equals(FreewayCoffeeXMLHelper.FOODS_TAG))
	    {
	    	// NOTHING
	    }
		else if(localName.equals(FreewayCoffeeXMLHelper.FOOD_TAG))
	    {
	        CurrentFood = new FreewayCoffeeFoodDrinkType();
	        
	        // Category (Food Type) ID
	        try
	        {
	        	Integer ID = Integer.parseInt(URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.FOOD_TYPE_ID_ATTR)));
	        	//CurrentFood.SetTypeID(ID);
	        	CurrentFood.SetCategoryID(ID);
	        }
	        catch (NumberFormatException ne)
	        {
	        	CurrentFood.SetCategoryID(-1);
	        }
	        
	        // Food ID
	        try
	        {
	        	Integer ID = Integer.parseInt(URLDecoder.decode(atts.getValue("id")));
	        	//CurrentFood.SetTypeID(ID);
	        	CurrentFood.SetTypeID(ID);
	        }
	        catch (NumberFormatException ne)
	        {
	        	CurrentFood.SetTypeID(-1);
	        }
	        	
	        // Now set all attrs as String types. This is top-level (descr etc)
	       // FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,CurrentFood.GetTypeOptionData());
	        	
	        // TODO -- should probably trap some kind of exception here ? TODO
	        CurrentFood.SetTypeName(URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.FOODS_LONG_DESCR)));
	        try
	       	{
	       		String SortOrderStr = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.SORT_ORDER_ATTR));
	       		Integer SortOrder = Integer.parseInt(SortOrderStr);
	       		CurrentFood.SetSortOrder(SortOrder);
	        }
	       	catch(NumberFormatException e)
	       	{
	        		// TODO LOG FIXME
	       		CurrentFood.SetSortOrder(-1);
	       	}
	        // BaseCost
	       	String BaseCost = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.FOODS_COST));
	       	CurrentFood.SetBaseCost(BaseCost);
    	}
		else if(localName.equals(FreewayCoffeeXMLHelper.FOOD_OPTION_INFO_TAG))
		{
			// TODO -- make this a generic thing ? right now, its tied to DrinkTypePart too much.
			//FreewayCoffeeXMLHelper.ParseIDAndCostList(atts,CurrentFood.GetTypePartData());
		}
		else if(localName.equals(FreewayCoffeeXMLHelper.FOOD_OPTION_DATA))
	    {
	       	HashMap<String,String> OptionData = new HashMap<String,String>();
        	
	       	FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,OptionData);
	       	//CurrentFoodOption.AddOptionData(URLDecoder.decode(atts.getValue("id")),OptionData);
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
        // FOOD
        else if(localName.equals(FreewayCoffeeXMLHelper.FOOD_OPTION_TAG))
        {
        	// ENd of a top-level drink option. Store it in the main map
        	//appState.GetFoodOptions().put(CurrentFoodOption.GetType(), CurrentFoodOption);
        	CurrentFoodOption=null;
        }
    	else if(localName.equals(FreewayCoffeeXMLHelper.FOOD_TAG))
        {
    		
        	appState.GetFoodTypes().put(CurrentFood.GetTypeID(), CurrentFood);
        	CurrentFood=null;
        	
        }
    }
}
