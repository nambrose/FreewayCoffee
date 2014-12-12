package com.freewaycoffee.client;

import java.net.URLDecoder;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class FreewayCoffeeUserTagXMLHandler extends DefaultHandler 
{
	public String signonResponse;
	public String updateTagResponse ;
	public String Tag;
	private FreewayCoffeeApp appState;
	public boolean NetworkError;
	private FreewayCoffeeCarColor CarColor;
	private FreewayCoffeeCarMake CarMake;
	public boolean MakeModelDataResponse;
	private FreewayCoffeeCarModel CarModel;
	private FreewayCoffeeCarMake CachedCarMake; // Hopefully speeds up adding models. Keep the previous make. Compare new Make ID for each model
	                                            // If its the same, no need to do a lookup.
	
	
	public FreewayCoffeeUserTagXMLHandler(FreewayCoffeeApp state)
	{
		appState = state;
		Tag=null;
		NetworkError=false;
		updateTagResponse=null;
		CarColor=null;
		MakeModelDataResponse=false;
		CarMake=null;
		CarModel=null;
		CachedCarMake=null;
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
    	else if(localName.equals(FreewayCoffeeXMLHelper.USER_UPDATE_TAG_TAG))
    	{
    		updateTagResponse = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.USER_TAG_UPDATE_RESULT_ATTR));
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
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_TAG_TAG))
        {
        	Tag=URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.USER_TAG_ATTR));
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.ALL_CAR_MAKES_AND_MODELS_TAG))
        {
        	MakeModelDataResponse=true;
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.USER_CAR_DATA_TAG))
        {
        	// Since we are getting all of it, zap out the old stuff.
        	appState.ClearUserCarData();
        	FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,appState.GetUserCarData());
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.CAR_COLOR_TAG))
        {
        	CarColor=new FreewayCoffeeCarColor();
        	
        	try
        	{
        		Integer ID  = Integer.parseInt(atts.getValue(FreewayCoffeeXMLHelper.CAR_COLOR_ID_ATTR));
        		CarColor.SetColorID(ID);
        	}
        	catch(NumberFormatException e)
        	{
        		
        	}
        	
        	// Long Descr
        	String Att =atts.getValue(FreewayCoffeeXMLHelper.CAR_COLOR_LONG_DESCR_ATTR);
        	CarColor.SetColorLongDescr(URLDecoder.decode(Att));
        	
        	// Short Descr
        	Att = atts.getValue(FreewayCoffeeXMLHelper.CAR_COLOR_SHORT_DESCR_ATTR);
        	if(Att!=null)
        	{
        		CarColor.SetColorShortDescr(URLDecoder.decode(Att));
        	}
        	//SORT ORDER
        	try
        	{
        		String SortOrderStr = atts.getValue(FreewayCoffeeXMLHelper.SORT_ORDER_SHORT_ATTR);
        		if(SortOrderStr!=null)
        		{
        			SortOrderStr=URLDecoder.decode(SortOrderStr);
        			Integer SortOrder = Integer.parseInt(SortOrderStr);
        			CarColor.SetSortOrder(SortOrder);
        		}
        		else
        		{
        			CarColor.SetSortOrder(-1);
        		}
        	}
        	catch(NumberFormatException e)
        	{
        		// TODO LOG FIXME
        		CarColor.SetSortOrder(-1);
        	}
        	
        	appState.GetCarMakeModelData().AddCarColor(CarColor.GetColorID(),CarColor);
        	//Log.w("FCXML","CarColor: ID=" + CarColor.GetColorID() + "Long: " + CarColor.GetColorLongDescr() + "Short: " + CarColor.GetColorShortDescr());
        	
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.CAR_MAKE_TAG))
        {
        	CarMake = new FreewayCoffeeCarMake();
        	try
        	{
        		Integer ID = Integer.parseInt(atts.getValue(FreewayCoffeeXMLHelper.CAR_MAKE_ID_ATTR));
        		CarMake.SetMakeID(ID);
        	}
        	catch(NumberFormatException e)
        	{
        		//??
        	}
        	String Att =atts.getValue(FreewayCoffeeXMLHelper.CAR_MAKE_LONG_DESCR_ATTR);			
        	CarMake.SetMakeLongDescr(URLDecoder.decode(Att));
        	
        	Att = atts.getValue(FreewayCoffeeXMLHelper.CAR_MAKE_SHORT_DESCR_ATTR);
        	if(Att!=null)
        	{
        		CarMake.SetMakeShortDescr(URLDecoder.decode(Att));
        	}
        	String CanHaveModelsString = atts.getValue(FreewayCoffeeXMLHelper.CAR_MAKE_CAN_HAVE_MODELS);
        	
        	if( (CanHaveModelsString!=null) && (CanHaveModelsString.equals("1")))
        	{
        		CarMake.SetMakeCanHaveModels(true);
        	}
        	else
        	{
        		CarMake.SetMakeCanHaveModels(false);
        	}
        	
        	
        	//SORT ORDER
        	try
        	{
        		String SortOrderStr = atts.getValue(FreewayCoffeeXMLHelper.SORT_ORDER_SHORT_ATTR);
        		if(SortOrderStr!=null)
        		{
        			SortOrderStr=URLDecoder.decode(SortOrderStr);
        			Integer SortOrder = Integer.parseInt(SortOrderStr);
        			CarMake.SetSortOrder(SortOrder);
        		}
        		else
        		{
        			CarMake.SetSortOrder(-1);
        		}
        	}
        	catch(NumberFormatException e)
        	{
        		// TODO LOG FIXME
        		CarMake.SetSortOrder(-1);
        	}
        	/*Log.w("FCXML","Make ID: " + CarMake.GetMakeID() + " Make Long: " + CarMake.GetMakeLongDescr() + " Short Desc: " + CarMake.GetMakeShortDescr()
        			+ " Can Have Models: " + CarMake.GetMakeCanHaveModels()); */
        	appState.GetCarMakeModelData().AddCarMake(CarMake.GetMakeID(),CarMake);
        	
        }
        else if(localName.equals(FreewayCoffeeXMLHelper.CAR_MODEL_TAG))
        {
        	CarModel = new FreewayCoffeeCarModel();
        	try
        	{
        		Integer ID = Integer.parseInt(atts.getValue(FreewayCoffeeXMLHelper.CAR_MODEL_ID_ATTR));
        		CarModel.SetModelID(ID);
        	}
        	catch(NumberFormatException e)
        	{
        		//??
        	}
        	
        	Integer MakeID=0;
        	try
        	{
        		MakeID = Integer.parseInt(atts.getValue(FreewayCoffeeXMLHelper.CAR_MODEL_MAKE_ID_ATTR));
        	}
        	catch(NumberFormatException e)
        	{
        		//??
        	}
        	
        	String Att = atts.getValue(FreewayCoffeeXMLHelper.CAR_MODEL_LONG_DESCR);
        	CarModel.SetModelLongDescr(URLDecoder.decode(Att));
        	
        	Att = atts.getValue(FreewayCoffeeXMLHelper.CAR_MODEL_SHORT_DESCR);
        	if(Att!=null)
        	{
        		CarModel.SetModelShortDescr(URLDecoder.decode(Att));
        	}
        	//SORT ORDER
        	try
        	{
        		String SortOrderStr = atts.getValue(FreewayCoffeeXMLHelper.SORT_ORDER_SHORT_ATTR);
        		if(SortOrderStr!=null)
        		{
        			SortOrderStr=URLDecoder.decode(SortOrderStr);
        			Integer SortOrder = Integer.parseInt(SortOrderStr);
        			CarModel.SetSortOrder(SortOrder);
        		}
        		else
        		{
        			CarModel.SetSortOrder(-1);
        		}
        	}
        	catch(NumberFormatException e)
        	{
        		// TODO LOG FIXME
        		CarModel.SetSortOrder(-1);
        	}
        	
        	// If we had no cached Make. OR the MakeID of the Cached Make is not the same as this make
        	// Cache this one. This (hopefully) speeds up getting car data as all the models for each make should come grouped
        	// This will save us a map lookup for each model (after the first) for a given make
        	// OF course I am optimizing w/out really measuring here.
        	if ( (CachedCarMake == null ) || (MakeID!=CachedCarMake.GetMakeID()))
        	{		
        		CachedCarMake = appState.GetCarMakeModelData().GetCarMake(MakeID);
        	}
        	/*
        	Log.w("FCXML","Model: " + CarModel.GetModelID() + " Model Make: " + MakeID + " Long: " + CarModel.GetModelLongDescr() +
        			"Short: " + CarModel.GetModelShortDescr());*/
        	  
        	if(CachedCarMake==null)
        	{
        		// Problamo !!!
        	}
        	else
        	{
        		//Log.w("FCXML","Found Car Make For Model: " + CarModel.GetModelID());
        		CachedCarMake.AddModel(CarModel);
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
        
    }
}

