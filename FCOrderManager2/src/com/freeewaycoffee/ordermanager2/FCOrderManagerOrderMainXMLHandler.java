package com.freeewaycoffee.ordermanager2;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.freewaycoffee.clientobjlib.FCLocation;
import com.freewaycoffee.clientobjlib.FCLocationAllowedArrivalMethod;
import com.freewaycoffee.clientobjlib.FCLocationAllowedPayMethod;
import com.freewaycoffee.clientobjlib.FCOrder;
import com.freewaycoffee.clientobjlib.FCOrderItem;
import com.freewaycoffee.clientobjlib.FCXMLHelper;

import android.util.Log;

public class FCOrderManagerOrderMainXMLHandler extends DefaultHandler 
{



	public FCXMLHelper.ResponseEnum Response;
	public FCXMLHelper.ResponseTypeEnum ResponseType;
	public String OrderUpdatedResponse;
	public boolean NetworkError;
	//public String m_HighestOrderTimestamp;
	public String m_LastGlobalOrderTag;
	boolean CommandSuccess;
	private FCOrderManagerApp appState;
	private FCOrder CurrentOrder;
	public ArrayList<FCOrderManagerCustomerHereItem> m_CustomerHereData;
	//public FCLocation m_Location;
	public ArrayList<FCOrder> OrderList;
	public FCLocation m_CurrentLocation;
	public FCOrderManagerOrderMainXMLHandler(FCOrderManagerApp state)
	{
		appState = state;
		Initialize();
	}

	private void Initialize()
	{
		NetworkError=false;
		
		Response=FCXMLHelper.ResponseEnum.NONE;
		ResponseType=FCXMLHelper.ResponseTypeEnum.UNKNOWN;
		OrderList=null;
		CurrentOrder=null;
		m_CustomerHereData=null;
		CommandSuccess=false;
	}
	@Override
	public void startDocument() throws SAXException
	{
		Initialize();
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
		if (localName.equals(FCXMLHelper.NETWORK_ERROR_TAG))
		{

			NetworkError=true;
		}
		else if (localName.equals(FCXMLHelper.SIGNON_RESPONSE_TAG))
		{
			ResponseType=FCXMLHelper.ResponseTypeEnum.SIGNON;
			
			Response = FCXMLHelper.ParseResultCodeFromAttributes(atts);
			
		}
		else if(localName.equals(FCLocation.LOCATION_UPDATED_TAG))
		{
			ResponseType=FCXMLHelper.ResponseTypeEnum.LOCATION_UPDATED;
			Response = FCXMLHelper.ParseResultCodeFromAttributes(atts);
			
		}
		else if(localName.equals(FCXMLHelper.ERROR_TAG))
		{
			// TODO FIXME ERROR NEED TO FIX
			//FCOrderManagerError Error;
			//FCOrderManagerXMLHelper.DoConvertAttrsToStringMap(atts,Error)
			//	appState.GetLastError());
		}
		else if(localName.equals(FCXMLHelper.ORDER_UPDATED_TAG))
		{
			ResponseType=FCXMLHelper.ResponseTypeEnum.ORDER_UPDATED;
			
			OrderUpdatedResponse = atts.getValue(FCXMLHelper.RESULT_ATTR);
			try
			{
				OrderUpdatedResponse=URLDecoder.decode(OrderUpdatedResponse,FCXMLHelper.URL_DECODE_TYPE);
			}
			catch(UnsupportedEncodingException e)
			{
				
			}
			
		}
		else if(localName.equals(FCXMLHelper.ORDER_LIST_TAG))
		{
			// Ugly because order_list is embedded inside ORDERS_SINCE so it overrides that always
			if(ResponseType!=FCXMLHelper.ResponseTypeEnum.ORDER_UPDATED)
			{
				ResponseType=FCXMLHelper.ResponseTypeEnum.ORDERS_SINCE_TAG;
			}
			OrderList = new ArrayList<FCOrder>();
			/*
			m_LastGlobalOrderTag = atts.getValue(FCXMLHelper.ORDER_GLOBAL_ORDER_TAG_ATTR);
			if(m_LastGlobalOrderTag!=null)
			{
				try
				{
					m_LastGlobalOrderTag= URLDecoder.decode(m_LastGlobalOrderTag,FCXMLHelper.URL_DECODE_TYPE);
					appState.SetHighestGlobalOrderTag(m_LastGlobalOrderTag);
				}
				catch(UnsupportedEncodingException e)
				{
					appState.ClearHighestGlobalOrderTag();
				}
			}*/
			
			// NOTHING -- Process each order
		}
		else if(localName.equals(FCXMLHelper.ORDER_TAG))
		{
			CurrentOrder = ProcessOrder(atts);
			if(ResponseType!=FCXMLHelper.ResponseTypeEnum.ORDER_UPDATED)
			{
				// Only update the tag if it was NOT a change in a single order
				// THis is because update a single order doesn't download all the other "orders since"
				// So say our Highest tag=9, but the DB has had 3 more orders (Tag=12)
				// We Refund and get an order back with 13. We must leave our max at 9 
				// Because Refund didnt send back 10-12 (Because we dont want to block the UI too long)
				appState.UpdateHighestGlobalOrderTag(CurrentOrder.GetOrderGlobalOrderTag());
			}
		}
		else if(localName.equals(FCXMLHelper.ORDER_CREDIT_CARD_TAG))
		{
			
		}
		else if(localName.equals(FCXMLHelper.OM_ORDER_ITEM_LIST_TAG))
		{
			// NOTHING
		}
		/*
		else if(localName.equals(FCOrderManagerXMLHelper.OM_ORDER_FOOD_ITEM_LIST_TAG))
		{
			// NOTHING
		}
		*/
		else if(localName.equals(FCXMLHelper.OM_ORDER_ITEM_TAG))
		{
			ProcessDrinkItem(atts);
		}
		else if(localName.equals(FCXMLHelper.OM_ORDER_FOOD_ITEM_TAG))
		{
			//ProcessFoodItem(atts);
		}
		else if(localName.equals(FCXMLHelper.OM_USER_TIME_HERE_TAG))
		{
			ProcessTimeHereItem(atts);
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
	
	private void ProcessTimeHereItem(Attributes atts)
	{
		FCOrderManagerCustomerHereItem CustTimeHere = new FCOrderManagerCustomerHereItem();
		
		for(int attrsIndex=0; attrsIndex<atts.getLength(); attrsIndex++)
	   	{	
	   		String AttrName = atts.getLocalName(attrsIndex);
	   		String AttrValue = atts.getValue(attrsIndex);
	   		
	   		try
	   		{
	   			AttrValue = URLDecoder.decode(AttrValue,FCXMLHelper.URL_DECODE_TYPE);
	   		}
	   		catch(UnsupportedEncodingException e)
	   		{
	   			// TODO
	   		}
	   		
	   		if(AttrName.equals(FCXMLHelper.OM_USER_TIME_HERE_LOC_ID_ATTR))
	   		{
	   			CustTimeHere.SetLocationID( FCXMLHelper.ParseIntStringOrMinusOne(AttrValue));
	   		}
	   		else if(AttrName.equals(FCXMLHelper.OM_USER_TIME_HERE_ORDER_ID_ATTR))
	   		{
	   			CustTimeHere.SetOrderID(FCXMLHelper.ParseIntStringOrMinusOne(AttrValue));
	   		}
	   		else if(AttrName.equals(FCXMLHelper.OM_USER_TIME_HERE_TIME_HERE_ATTR))
	   		{
	   			CustTimeHere.SetTimeHere(AttrValue);
	   		}	
	   	}
		//Log.w("OM", "Here for: " + CustTimeHere.GetOrderID());
   		m_CustomerHereData.add(CustTimeHere);
	}
	private void ProcessDrinkItem(Attributes atts)
	{
		FCOrderItem Item = new FCOrderItem();
		
		Item.SetItemTypeByEnum(FCOrderItem.OrderItemKind.ORDER_ITEM_TYPE_DRINK);
		
		for(int attrsIndex=0; attrsIndex<atts.getLength(); attrsIndex++)
	   	{	
	   		String AttrName = atts.getLocalName(attrsIndex);
	   		String AttrValue = atts.getValue(attrsIndex);
	   		
	   		
	   		
	   		try
	   		{
	   			AttrValue = URLDecoder.decode(AttrValue,FCXMLHelper.URL_DECODE_TYPE);
	   		}
	   		catch(UnsupportedEncodingException e)
	   		{
	   			// TODO
	   		}
	   		//Log.w("FCXML", "ProcessOrder: Index: " + attrsIndex + " Name: " + AttrName + " Value " + AttrValue );
	   		if(AttrName.equals(FCXMLHelper.ID_ATTR))
	   		{
	   			Item.SetItemID(FCXMLHelper.ParseIntStringOrMinusOne(AttrValue));
	   		}
	   		else if(AttrName.equals(FCXMLHelper.OM_ORDER_ITEM_DESCR_ATTR))
	   		{
	   			Item.SetItemDescription(AttrValue);
	   		}
	   		else if(AttrName.equals(FCXMLHelper.OM_ORDER_ITEM_COST_ATTR))
	   		{
	   			Item.SetItemCost(AttrValue);
	   		}
	   	}
		CurrentOrder.AddOrderItem(Item);
	}
	

	
	private FCOrder ProcessOrder(Attributes atts)
	{
		CurrentOrder = new FCOrder();
		for(int attrsIndex=0; attrsIndex<atts.getLength(); attrsIndex++)
	   	{	
	   		String AttrName = atts.getLocalName(attrsIndex);
	   		String AttrValue = atts.getValue(attrsIndex);
	   		
	   		try
	   		{
	   			AttrValue = URLDecoder.decode(AttrValue,FCXMLHelper.URL_DECODE_TYPE);
	   		}
	   		catch(UnsupportedEncodingException e)
	   		{
	   			// TODO
	   		}
	   		//Log.w("FCXML", "ProcessOrder: Index: " + attrsIndex + " Name: " + AttrName + " Value " + AttrValue );
	   		if(AttrName.equals(FCXMLHelper.ID_ATTR))
	   		{
	   			CurrentOrder.SetOrderID(FCXMLHelper.ParseIntStringOrMinusOne(AttrValue));
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_USER_ID_ATTR)) 
	   		{
	   			CurrentOrder.SetUserID(FCXMLHelper.ParseIntStringOrMinusOne(AttrValue));
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_START_TIME_ATTR))
	   		{
	   			CurrentOrder.SetTimeReceived(AttrValue);
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_TIME_TO_LOCATION_ATTR))
	   		{
	   			CurrentOrder.SetTimeToLocation(AttrValue);
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_END_TIME_ATTR))
	   		{
	   			CurrentOrder.SetTimeOrderEnded(AttrValue);
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_DISPOSITION_ATTR))
	   		{
	   			CurrentOrder.SetDispositionInt(FCXMLHelper.ParseIntStringOrMinusOne(AttrValue));
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_DISPOSITION_TEXT_ATTR))
	   		{
	   			CurrentOrder.SetDisposition(AttrValue);
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_TOTAL_COST_ATTR))
	   		{
	   			CurrentOrder.SetTotalCost(AttrValue);
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_USER_EMAIL_ATTR))
	   		{
	   			CurrentOrder.SetUserEmail(AttrValue);
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_USER_NAME_ATTR))
	   		{
	   			CurrentOrder.SetUserName(AttrValue);
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_USER_TIME_HERE_ATTR))
	   		{
	   			CurrentOrder.SetTimeUserHere(AttrValue);
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_LOCATION_ID_ATTR))
	   		{
	   			CurrentOrder.SetLocationID(FCXMLHelper.ParseIntStringOrMinusOne(AttrValue));
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_TIME_NEEDED_ATTR))
	   		{
	   			CurrentOrder.SetTimeNeeded(AttrValue);
	   		}
	   		else if(AttrName.equals(FCXMLHelper.INCARNATION_ATTR))
	   		{
	   			CurrentOrder.SetIncarnation(FCXMLHelper.ParseIntStringOrMinusOne(AttrValue));
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_USER_CAR_INFO))
	   		{
	   			CurrentOrder.SetUserCarMakeModel(AttrValue);
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_USER_TAG))
	   		{
	   			CurrentOrder.SetUserTag(AttrValue);
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_ARRIVE_MODE_ATTR))
	   		{
	   			if(AttrValue.equals("1"))
	   			{
	   				CurrentOrder.SetArriveMode(FCXMLHelper.ARRIVE_MODE_WALKUP_INT);
	   			}
	   			else
	   			{
	   				CurrentOrder.SetArriveMode(FCXMLHelper.ARRIVE_MODE_CAR_INT);
	   			}
	   			
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_USER_IS_DEMO_ATTR))
	   		{
	   			if(AttrValue.equals("1"))
	   			{
	   				CurrentOrder.SetDemoOrder(true);
	   			}
	   			else
	   			{
	   				CurrentOrder.SetDemoOrder(false);
	   			}
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_ITEMS_TOTAL_ATTR))
	   		{
	   			CurrentOrder.SetOrderItemsTotal(AttrValue);
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_DISCOUNT_ATTR))
	   		{
	   			CurrentOrder.SetOrderDiscount(AttrValue);
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_TIP_ATTR))
	   		{
	   			CurrentOrder.SetOrderTip(AttrValue);
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_PAY_METHOD_ATTR))
	   		{
	   			if(AttrValue.equals("1"))
	   			{
	   				CurrentOrder.SetOrderPayMethod(FCOrder.ORDER_PAY_METHOD_IN_APP);
	   			}
	   			else if(AttrValue.equals("2"))
	   			{
	   				CurrentOrder.SetOrderPayMethod(FCOrder.ORDER_PAY_METHOD_IN_STORE);
	   			}
	   		}
	   		else if(AttrName.equals(FCXMLHelper.ORDER_GLOBAL_ORDER_TAG_ATTR))
	   		{
	   			if( (AttrValue!=null) && (AttrValue.length()>0))
	   			{
	   				Long Tag = Long.valueOf(AttrValue);
	   				CurrentOrder.SetOrderGlobalOrderTag(Tag);
	   				
	   			}
	   			
	   		}
	   		
	   		
	   	}
		return CurrentOrder;
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
		if(localName.equals(FCXMLHelper.ORDER_TAG))
		{
			OrderList.add(CurrentOrder);
		}
	}


}
