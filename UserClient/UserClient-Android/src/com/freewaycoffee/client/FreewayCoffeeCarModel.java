package com.freewaycoffee.client;

public class FreewayCoffeeCarModel
{
	static public final Integer CAR_MODEL_NONE=0;
	private Integer ModelID;
	private String ModelLongDescr;
	private String ModelShortDescr;
	private Integer SortOrder;
	
	
	public void FreewayCoffeeCarModel()
	{
		ModelID=0;
		ModelLongDescr="";
		ModelShortDescr="";
		SortOrder=-1;
	}
	
	public void SetModelID(Integer ID)
	{
		ModelID=ID;
	}
	public void SetModelLongDescr(String Descr)
	{
		ModelLongDescr=Descr;
	}
	public void SetModelShortDescr(String Descr)
	{
		ModelShortDescr=Descr;
	}
	public Integer GetModelID()
	{
		return ModelID;
	}
	public String GetModelLongDescr()
	{
		return ModelLongDescr;
	}
	public String GetModelShortDescr()
	{
		return ModelShortDescr;
	}
	public void SetSortOrder(Integer Order)
	{
		SortOrder=Order;
	}
	
	public Integer GetSortOrder()
	{
		return SortOrder;
	}
	// TODO we have to do better here.
	public boolean IsNone()
	{
		if(ModelID==0)
		{
			return true;
		}
		return false;
	}
}