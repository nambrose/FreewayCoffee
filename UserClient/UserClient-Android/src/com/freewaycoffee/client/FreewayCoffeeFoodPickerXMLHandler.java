package com.freewaycoffee.client;

import java.net.URLDecoder;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FreewayCoffeeFoodPickerXMLHandler extends DefaultHandler 
{

	public String signonResponse;
	public boolean signonResponseSeen;
	public boolean NetworkError;
	public String FoodPickerCompatLevel;
	private FreewayCoffeeApp appState;
	private FreewayCoffeeFoodDrinkOption CurrentFoodOption;
	private FreewayCoffeeFoodDrinkType CurrentFood;

	public FreewayCoffeeFoodPickerXMLHandler(FreewayCoffeeApp state)
	{
		Reset();
		appState = state;
		
	}
		
	public void Reset()
	{
		signonResponse=null;
		NetworkError=false;
		CurrentFood=null;
		CurrentFoodOption=null;
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
	
	
	/// NOTE. IF YOU CHANGE ANYTHING HERE, YOU ALSO HAVE TO UPDATE DrinkPICKER XML HANDLER AS I HAD TO PASTE THIS CODE THERE
	// TODO FIXME
	
	    
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
	        //FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,CurrentFood.GetTypeOptionData());
	        	
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
		 * {
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
	    	}
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
