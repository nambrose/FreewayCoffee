package com.freewaycoffee.client;

public class FreewayCoffeeCarColor
{
	static public final Integer CAR_COLOR_NONE=0;

	private Integer ColorID;
	private String ColorLongDescr;
	private String ColorShortDescr;
	private Integer SortOrder;
	
	public  FreewayCoffeeCarColor()
	{
		ColorID=0;
		ColorLongDescr="";
		ColorShortDescr="";
		SortOrder=-1;
	}
	
	public Integer GetColorID()
	{
		return ColorID;
	}
	public String GetColorLongDescr()
	{
		return ColorLongDescr;
	}
	public String GetColorShortDescr()
	{
		return ColorShortDescr;
	}
	
	public void SetColorID(Integer ID)
	{
		ColorID=ID;
	}
	public void SetColorLongDescr(String Descr)
	{
		ColorLongDescr = Descr;
	}
	public void SetColorShortDescr(String Descr)
	{
		ColorShortDescr=Descr;
	}
	
	public void SetSortOrder(Integer Order)
	{
		SortOrder=Order;
	}
	
	public Integer GetSortOrder()
	{
		return SortOrder;
	}
	
	boolean IsNone()
	{
		if(ColorID==0)
		{
			return true;
		}
		return false;
	}
	
}