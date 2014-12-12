package com.freewaycoffee.client;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.freewaycoffee.clientobjlib.FCXMLHelper;

import android.text.Html;
import android.text.Spanned;

public class FreewayCoffeeLastOrder 
{
	public enum OrderSubmittedStatus
	{
		ORDER_FAILED,
		ORDER_SUBMITTED,
		ORDER_HERE_SENT,
		ORDER_HERE_OK
		
	};
	
	private OrderSubmittedStatus m_Status;
	
	private Map<String,String> m_LastOrder;
	private Map<String,String> m_LastOrderLocation;
	//boolean LastOrderSentImHere; // NOTE TODO FIXME. Getting nervous here.
	//Date LastOrderTime;
	private Integer m_OrderID; // This one comes in the Order Submitted. We also get an OrderID in m_LastOrder later. Irksome.
	private FreewayCoffeeApp appState;
	private ArrayList<FreewayCoffeeOrderItem> m_OrderItems;
	private Map<String,String> m_OrderCreditCard;
	
	public FreewayCoffeeLastOrder(FreewayCoffeeApp State)
	{
		appState = State;
		
	}
	
	
	public boolean OrderExists()
	{
		// Not too scientific
		if(m_LastOrder==null)
		{
			return false;
		}
		return true;
	}
	public void Clear()
	{
		m_OrderID=-1;
		if(m_LastOrder!=null)
		{
			m_LastOrder.clear();
		}
		if(m_LastOrderLocation!=null)
		{
			m_LastOrderLocation.clear();
		}
		if(m_OrderItems!=null)
		{
			m_OrderItems.clear();
		}
		if(m_OrderCreditCard!=null)
		{
			m_OrderCreditCard.clear();
		}
	}
	
	public void SetImHereStatus()
	{
		if(m_LastOrder!=null)
		{
			String UserTimeHere = m_LastOrder.get(FreewayCoffeeXMLHelper.ORDER_USER_TIME_HERE_ATTR);
			if( (UserTimeHere!=null) && (UserTimeHere.equals("")!=true))
			{
				m_Status=OrderSubmittedStatus.ORDER_HERE_OK;
			}
				
		}
	}
	public void SetOrderCreditCard(Map<String,String> Card)
	{
		m_OrderCreditCard=Card;
	}
	public void AddOrderItem(FreewayCoffeeOrderItem Item)
	{
		if(m_OrderItems==null)
		{
			m_OrderItems=new ArrayList<FreewayCoffeeOrderItem>();
		}
		m_OrderItems.add(Item);
	}
	
	public void SetOrderItems(ArrayList<FreewayCoffeeOrderItem> OrderItems)
	{
		m_OrderItems = OrderItems;
	}
	
	public void SetOrderID(Integer ID)
	{
		m_OrderID=ID;
		if(m_LastOrder==null)
		{
			return;
		}
		if ((m_OrderID==null) || (m_OrderID==-1))
		{
			String IDStr = m_LastOrder.get("id");
			if(IDStr==null)
			{
				m_OrderID=-1;
				return;
			}
			try
			{
				m_OrderID = Integer.parseInt(IDStr);
			}
			catch(NumberFormatException e)
			{
				m_OrderID=-1;
			}
		}
	}
	public Integer GetOrderID()
	{
		return m_OrderID;
	}
	
	public OrderSubmittedStatus GetOrderStatus()
	{
		return m_Status;
	}
	
	public void SetOrderStatus(OrderSubmittedStatus Status)
	{
		m_Status=Status;
	}
	
	public void SetOrderData(Map<String,String> OrderData)
	{
		m_LastOrder = OrderData;
		if(m_LastOrder==null)
		{
			return;
		}
		if(  (m_OrderID==null) || (m_OrderID==-1))
		{
			String IDStr = m_LastOrder.get("id");
			if(IDStr==null)
			{
				m_OrderID=-1;
				return;
			}
			try
			{
				m_OrderID = Integer.parseInt(IDStr);
			}
			catch(NumberFormatException e)
			{
				m_OrderID=-1;
			}
		}
	}
	
	public void SetOrderLocation(Map<String,String> OrderLocationData)
	{
		m_LastOrderLocation=OrderLocationData;
	}
	
	public Spanned MakeOrderSubmittedText()
	{
		String Result = appState.getString(R.string.fc_congratulations) + ", " + appState.getLoginNickname() + "<br><br>";
		Result += appState.getString(R.string.fc_your_card_was_charged) + "<br>";
		
		if(m_LastOrderLocation!=null)
		{
			Result += appState.getString(R.string.fc_order_id) + " " + m_OrderID + "<br><br>";
			
			Result += appState.getString(R.string.fc_order_location) + " " + m_LastOrderLocation.get(FreewayCoffeeXMLHelper.ORDER_LOCATION_DESCRIPTION_ATTR) + "<br>";
			Result += appState.getString(R.string.fc_email) + " " + m_LastOrderLocation.get(FreewayCoffeeXMLHelper.ORDER_LOCATION_EMAIL_ATTR) +"<br>";
			
			Result += "<br>";
			Result += m_LastOrderLocation.get(FreewayCoffeeXMLHelper.ORDER_LOCATION_INSTRUCTIONS_ATTR);
			

			
		}
		Spanned marked_up = Html.fromHtml(Result);
		return marked_up;
	}
	public String MakeImHereURL(boolean Walkup)
	{
		String UserArriveMode = FCXMLHelper.ARRIVE_MODE_CAR_STR;
		if(Walkup)
		{
			UserArriveMode = FCXMLHelper.ARRIVE_MODE_WALKUP_STR;
		}
		
		
		String URL = new String(FreewayCoffeeApp.BASE_URL + FreewayCoffeeApp.ORDER_PAGE + "?" + FreewayCoffeeApp.COMMAND_STRING + "=" 
		+ FreewayCoffeeApp.ORDER_TIME_HERE_COMMAND + "&" + 
		FCXMLHelper.USER_ARRIVE_MODE_CMD_ARG  +"=" + UserArriveMode +"&" +
			FreewayCoffeeApp.ORDER_ID + "=" + m_OrderID );
		return URL;
		
	}
	//Uses LastError in App (good idea ? I doubt it)
	public String MakeFailedOrderResponseText()
	{
		String ErrorText = appState.MakeLastErrorText();
		
		String Result = appState.getString(R.string.fc_sorry) + ", " + appState.getLoginNickname() + "\n" +  appState.getString(R.string.fc_order_failed) + "\n\n";
		Result += appState.getString(R.string.fc_reason) +  "\n" + ErrorText;
		return Result;
	}
	
	public String GetTimeDateAsHoursAndMins(String TimeDate)
	{
		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
			Date TimeNeededAsDate = curFormater.parse(TimeDate);
			DateFormat time = DateFormat.getTimeInstance(DateFormat.SHORT);
			return time.format(TimeNeededAsDate);
		}
		catch (ParseException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return TimeDate; // IF we cant parse it, just return the original
	}
	
	    
	public Spanned MakeOrderResponseText()
	{
		String Result = appState.getString(R.string.fc_congratulations) + ", " + appState.getLoginNickname() + "<br><br>";
		Result += appState.getString(R.string.fc_order_was_successful) + "<br>";
		Result += appState.getString(R.string.fc_order_id) + " " + m_OrderID + "<br><br>";
		
		Result += appState.getString(R.string.fc_total_cost) + " " + m_LastOrder.get(FreewayCoffeeXMLHelper.ORDER_TOTAL_COST_ATTR) +"<br>"; 
		Result += appState.getString(R.string.fc_order_credit_card) + " " + m_OrderCreditCard.get(FreewayCoffeeXMLHelper.ORDER_CREDIT_CARD_DESCR);
		Result += "(..."  + m_OrderCreditCard.get(FreewayCoffeeXMLHelper.ORDER_CREDIT_CARD_CARD_LAST4) + ") was charged.<br><br>";
			
		/* Removed Ontime: D-155
		Result += appState.getString(R.string.fc_order_ready_at) + " ";
		if(m_LastOrder.get(FreewayCoffeeXMLHelper.ORDER_TIME_NEEDED_ATTR) !=null)
		{
			Result += GetTimeDateAsHoursAndMins(m_LastOrder.get(FreewayCoffeeXMLHelper.ORDER_TIME_NEEDED_ATTR) );
		}
		else
		{
			Result += "N/A";
		}
				
		Result += "<br>";
			
			*/
		Result += "Order Summary:<br><br>";
		
		for (FreewayCoffeeOrderItem Item : m_OrderItems)
		{
			Result += Item.m_OrderItemDescription + "($" + Item.m_OrderItemCost + ")<br><br>";
		}
		Spanned marked_up = Html.fromHtml(Result);
		return marked_up;

	}
}
