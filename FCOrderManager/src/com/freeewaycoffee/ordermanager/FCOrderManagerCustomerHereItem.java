package com.freeewaycoffee.ordermanager;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FCOrderManagerCustomerHereItem
{
	private Integer m_LocationID;
	private Integer m_OrderID;
	private String m_TimeHereAsString;
	
	FCOrderManagerCustomerHereItem()
	{
		Initialize();
	}
	
	private void Initialize()
	{
		m_LocationID=-1;
		m_OrderID=-1;
		m_TimeHereAsString="";
		
	}
	
	public void SetLocationID(Integer LocationID)
	{
		m_LocationID=LocationID;
	}
	
	public Integer IntegerGetLocationID()
	{
		return m_LocationID;
	}
	
	public void SetOrderID(Integer OrderID)
	{
		m_OrderID = OrderID;
	}
	
	public Integer GetOrderID()
	{
		return m_OrderID;
	}
	
	public void SetTimeHere(String TimeHere)
	{
		m_TimeHereAsString = TimeHere;
		
	}
	
	public String GetTimeHere()
	{
		return m_TimeHereAsString;
	}
	
}
