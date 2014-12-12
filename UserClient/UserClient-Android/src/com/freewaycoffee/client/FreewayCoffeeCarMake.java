package com.freewaycoffee.client;

public class FreewayCoffeeCarMake
{
	static public final Integer CAR_MAKE_NONE=0;

	private Integer MakeID;
	private String MakeLongDescr;
	private String MakeShortDescr;
	private boolean CanHaveModels;
	private FreewayCoffeeCarModelList Models;
	private Integer SortOrder;
	
	public FreewayCoffeeCarMake()
	{
		MakeID=0;
		MakeLongDescr="";
		MakeShortDescr="";
		CanHaveModels=false;
		SortOrder=-1;
		Models = new FreewayCoffeeCarModelList();
	}
	
	public void SetMakeID(Integer ID)
	{
		MakeID=ID;
	}
	public void SetMakeLongDescr(String Descr)
	{
		MakeLongDescr=Descr;
	}
	public void SetMakeShortDescr(String Descr)
	{
		MakeShortDescr=Descr;
	}
	public void SetMakeCanHaveModels(boolean CanHave)
	{
		CanHaveModels=CanHave;
	}
	
	public Integer GetMakeID()
	{
		return MakeID;
	}
	
	public String GetMakeLongDescr()
	{
		return MakeLongDescr;
	}
	public String GetMakeShortDescr()
	{
		return MakeShortDescr;
	}
	
	public void SetSortOrder(Integer Order)
	{
		SortOrder=Order;
	}
	
	public Integer GetSortOrder()
	{
		return SortOrder;
	}
	
	public boolean GetMakeCanHaveModels()
	{
		return CanHaveModels;
	}
	
	public boolean DoesMakeHaveModels()
	{
		if( (CanHaveModels==true ) && (Models.Size()>0))
		{
			return true;
		}
		return false;
	}
	
	public void AddModel(FreewayCoffeeCarModel Model)
	{
		Models.AddModel(Model);
	}
	
	public FreewayCoffeeCarModel GetModel(Integer ModelID)
	{
		return Models.GetModel(ModelID);
	}
	
	public int GetNumberOfModels()
	{
		return Models.Size();
	}
	
	public boolean IsNone()
	{
		// TODO, find a way to share this logic with server code common files
		if(MakeID==0)
		{
			return true;
		}
		return false;
	}
	
	public FreewayCoffeeCarModelList GetModelList()
	{
		return Models;
	}
}
