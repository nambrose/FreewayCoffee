package com.freeewaycoffee.ordermanager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class FCOrderManagerOrderMainXMLHandler extends DefaultHandler 
{



	public FCOrderManagerXMLHelper.SignonResponseEnum SignonResponse;
	public FCOrderManagerXMLHelper.ResponseTypeEnum ResponseType;
	public boolean NetworkError;

	private FCOrderManagerApp appState;
	private FCOrderManagerOrder CurrentOrder;
	public ArrayList<FCOrderManagerCustomerHereItem> m_CustomerHereData;
	
	public ArrayList<FCOrderManagerOrder> OrderList;
	public FCOrderManagerOrderMainXMLHandler(FCOrderManagerApp state)
	{
		appState = state;
		Initialize();
	}

	private void Initialize()
	{
		NetworkError=false;
		
		SignonResponse=FCOrderManagerXMLHelper.SignonResponseEnum.NONE;
		ResponseType=FCOrderManagerXMLHelper.ResponseTypeEnum.UNKNOWN;
		OrderList=null;
		CurrentOrder=null;
		m_CustomerHereData=null;
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
		if (localName.equals(FCOrderManagerXMLHelper.NETWORK_ERROR_TAG))
		{

			NetworkError=true;
		}
		else if (localName.equals(FCOrderManagerXMLHelper.SIGNON_RESPONSE_TAG))
		{
			ResponseType=FCOrderManagerXMLHelper.ResponseTypeEnum.SIGNON;
			//CheckSchema(atts);
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
			FCOrderManagerError Error;
			//FCOrderManagerXMLHelper.DoConvertAttrsToStringMap(atts,Error)
			//	appState.GetLastError());
		}
		else if(localName.equals(FCOrderManagerXMLHelper.OM_ORDER_AND_TIME_HERE_LIST_TAG))
		{
			ResponseType=FCOrderManagerXMLHelper.ResponseTypeEnum.ORDER_AND_TIME_HERE_DOWNLOAD;
		}
		else if(localName.equals(FCOrderManagerXMLHelper.ORDER_LIST_TAG))
		{
			
			OrderList = new ArrayList<FCOrderManagerOrder>();
			// NOTHING -- Process each order
		}
		else if(localName.equals(FCOrderManagerXMLHelper.ORDER_TAG))
		{
			ProcessOrder(atts);
		}
		else if(localName.equals(FCOrderManagerXMLHelper.ORDER_CREDIT_CARD_TAG))
		{
			
		}
		else if(localName.equals(FCOrderManagerXMLHelper.OM_ORDER_DRINK_ITEM_LIST_TAG))
		{
			// NOTHING
		}
		else if(localName.equals(FCOrderManagerXMLHelper.OM_ORDER_FOOD_ITEM_LIST_TAG))
		{
			// NOTHING
		}
		else if(localName.equals(FCOrderManagerXMLHelper.OM_ORDER_DRINK_ITEM_TAG))
		{
			ProcessDrinkItem(atts);
		}
		else if(localName.equals(FCOrderManagerXMLHelper.OM_ORDER_FOOD_ITEM_TAG))
		{
			ProcessFoodItem(atts);
		}
		else if(localName.equals(FCOrderManagerXMLHelper.OM_USER_TIME_HERE_LIST_TAG))
		{
			m_CustomerHereData = new ArrayList<FCOrderManagerCustomerHereItem> ();
		}
		else if(localName.equals(FCOrderManagerXMLHelper.OM_USER_TIME_HERE_TAG))
		{
			ProcessTimeHereItem(atts);
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
	   			AttrValue = URLDecoder.decode(AttrValue,FCOrderManagerXMLHelper.URL_DECODE_TYPE);
	   		}
	   		catch(UnsupportedEncodingException e)
	   		{
	   			// TODO
	   		}
	   		//static public final String ="o_th_l";
	   		//static public final String OM_USER_TIME_HERE_ORDER_ID_ATTR="o_th_o";
	   		//static public final String OM_USER_TIME_HERE_TIME_HERE_ATTR="o_th_t";		
	   		//Log.w("FCXML", "ProcessOrder: Index: " + attrsIndex + " Name: " + AttrName + " Value " + AttrValue );
	   		if(AttrName.equals(FCOrderManagerXMLHelper.OM_USER_TIME_HERE_LOC_ID_ATTR))
	   		{
	   			CustTimeHere.SetLocationID( FCOrderManagerXMLHelper.ParseIntStringOrMinusOne(AttrValue));
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.OM_USER_TIME_HERE_ORDER_ID_ATTR))
	   		{
	   			CustTimeHere.SetOrderID(FCOrderManagerXMLHelper.ParseIntStringOrMinusOne(AttrValue));
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.OM_USER_TIME_HERE_TIME_HERE_ATTR))
	   		{
	   			CustTimeHere.SetTimeHere(AttrValue);
	   		}	
	   	}
		Log.w("OM", "Here for: " + CustTimeHere.GetOrderID());
   		m_CustomerHereData.add(CustTimeHere);
	}
	private void ProcessDrinkItem(Attributes atts)
	{
		FCOrderManagerOrderItem Item = new FCOrderManagerOrderItem();
		
		Item.SetItemTypeByEnum(FCOrderManagerOrderItem.OrderItemKind.ORDER_ITEM_TYPE_DRINK);
		
		for(int attrsIndex=0; attrsIndex<atts.getLength(); attrsIndex++)
	   	{	
	   		String AttrName = atts.getLocalName(attrsIndex);
	   		String AttrValue = atts.getValue(attrsIndex);
	   		
	   		
	   		
	   		try
	   		{
	   			AttrValue = URLDecoder.decode(AttrValue,FCOrderManagerXMLHelper.URL_DECODE_TYPE);
	   		}
	   		catch(UnsupportedEncodingException e)
	   		{
	   			// TODO
	   		}
	   		//Log.w("FCXML", "ProcessOrder: Index: " + attrsIndex + " Name: " + AttrName + " Value " + AttrValue );
	   		if(AttrName.equals(FCOrderManagerXMLHelper.ID_ATTR))
	   		{
	   			Item.SetItemID(FCOrderManagerXMLHelper.ParseIntStringOrMinusOne(AttrValue));
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.OM_ORDER_DRINK_ITEM_DESCR_ATTR))
	   		{
	   			Item.SetItemDescription(AttrValue);
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.OM_ORDER_DRINK_ITEM_COST_ATTR))
	   		{
	   			Item.SetItemCost(AttrValue);
	   		}
	   	}
		CurrentOrder.AddOrderItem(Item);
	}
	
	
	
	private void ProcessFoodItem(Attributes atts)
	{
		
		FCOrderManagerOrderItem Item = new FCOrderManagerOrderItem();

		Item.SetItemTypeByEnum(FCOrderManagerOrderItem.OrderItemKind.ORDER_ITEM_TYPE_FOOD);

		for(int attrsIndex=0; attrsIndex<atts.getLength(); attrsIndex++)
		{	
			String AttrName = atts.getLocalName(attrsIndex);
			String AttrValue = atts.getValue(attrsIndex);
			


			try
			{
				AttrValue = URLDecoder.decode(AttrValue,FCOrderManagerXMLHelper.URL_DECODE_TYPE);
			}
			catch(UnsupportedEncodingException e)
			{
				// TODO
			}
			//Log.w("FCXML", "ProcessOrder: Index: " + attrsIndex + " Name: " + AttrName + " Value " + AttrValue );
			if(AttrName.equals(FCOrderManagerXMLHelper.ID_ATTR))
			{
				Item.SetItemID(FCOrderManagerXMLHelper.ParseIntStringOrMinusOne(AttrValue));
			}
			else if(AttrName.equals(FCOrderManagerXMLHelper.OM_ORDER_FOOD_ITEM_DESCR_ATTR))
			{
				Item.SetItemDescription(AttrValue);
			}
			else if(AttrName.equals(FCOrderManagerXMLHelper.OM_ORDER_FOOD_ITEM_COST_ATTR))
			{
				Item.SetItemCost(AttrValue);
			}
		}
		CurrentOrder.AddOrderItem(Item);
		
	}

	//FCOrderManagerOrderItem
	
	private void ProcessOrder(Attributes atts)
	{
		CurrentOrder = new FCOrderManagerOrder();
		for(int attrsIndex=0; attrsIndex<atts.getLength(); attrsIndex++)
	   	{	
	   		String AttrName = atts.getLocalName(attrsIndex);
	   		String AttrValue = atts.getValue(attrsIndex);
	   		
	   		try
	   		{
	   			AttrValue = URLDecoder.decode(AttrValue,FCOrderManagerXMLHelper.URL_DECODE_TYPE);
	   		}
	   		catch(UnsupportedEncodingException e)
	   		{
	   			// TODO
	   		}
	   		//Log.w("FCXML", "ProcessOrder: Index: " + attrsIndex + " Name: " + AttrName + " Value " + AttrValue );
	   		if(AttrName.equals(FCOrderManagerXMLHelper.ID_ATTR))
	   		{
	   			CurrentOrder.SetOrderID(FCOrderManagerXMLHelper.ParseIntStringOrMinusOne(AttrValue));
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.ORDER_USER_ID_ATTR)) 
	   		{
	   			CurrentOrder.SetUserID(FCOrderManagerXMLHelper.ParseIntStringOrMinusOne(AttrValue));
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.ORDER_START_TIME_ATTR))
	   		{
	   			CurrentOrder.SetTimeReceived(AttrValue);
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.ORDER_TIME_TO_LOCATION_ATTR))
	   		{
	   			CurrentOrder.SetTimeToLocation(AttrValue);
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.ORDER_END_TIME_ATTR))
	   		{
	   			CurrentOrder.SetTimeOrderEnded(AttrValue);
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.ORDER_DISPOSITION_ATTR))
	   		{
	   			CurrentOrder.SetDispositionInt(FCOrderManagerXMLHelper.ParseIntStringOrMinusOne(AttrValue));
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.ORDER_DISPOSITION_TEXT_ATTR))
	   		{
	   			CurrentOrder.SetDisposition(AttrValue);
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.ORDER_TOTAL_COST_ATTR))
	   		{
	   			CurrentOrder.SetTotalCost(AttrValue);
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.ORDER_USER_EMAIL_ATTR))
	   		{
	   			CurrentOrder.SetUserEmail(AttrValue);
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.ORDER_USER_NAME_ATTR))
	   		{
	   			CurrentOrder.SetUserName(AttrValue);
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.ORDER_USER_TIME_HERE_ATTR))
	   		{
	   			CurrentOrder.SetTimeUserHere(AttrValue);
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.ORDER_LOCATION_ID_ATTR))
	   		{
	   			CurrentOrder.SetLocationID(FCOrderManagerXMLHelper.ParseIntStringOrMinusOne(AttrValue));
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.ORDER_TIME_NEEDED_ATTR))
	   		{
	   			CurrentOrder.SetTimeNeeded(AttrValue);
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.INCARNATION_ATTR))
	   		{
	   			CurrentOrder.SetIncarnation(FCOrderManagerXMLHelper.ParseIntStringOrMinusOne(AttrValue));
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.ORDER_USER_CAR_INFO))
	   		{
	   			CurrentOrder.SetUserCarMakeModel(AttrValue);
	   		}
	   		else if(AttrName.equals(FCOrderManagerXMLHelper.ORDER_USER_TAG))
	   		{
	   			CurrentOrder.SetUserTag(AttrValue);
	   		}
	   		
	   	}
		OrderList.add(CurrentOrder);
		
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
		if(localName.equals(FCOrderManagerXMLHelper.ORDER_TAG))
		{
			
		}
	}


}
