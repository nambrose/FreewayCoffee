package com.freewaycoffee.client;

public class FreewayCoffeeFoodDrinkTypeDefaultOption 
{
	//private Integer ItemID;
	private Integer Type;
	private Integer OptionGroup;
	private Integer OptionValue;
	private Integer OptionCount;
	
	public FreewayCoffeeFoodDrinkTypeDefaultOption()
	{
		// Server does not send if =1
		OptionCount=1;
	}
	/*
	public Integer GetID()
	{
		return ItemID;
	}
	*/
	public Integer GetType()
	{
		return Type;
	}
	
	public Integer GetOptionGroup()
	{
		return OptionGroup;
	}
	
	public Integer GetOptionValue()
	{
		return OptionValue;
	}
	
	public Integer GetOptionCount()
	{
		return OptionCount;
	}
	
	// Set
	/*
	public void SetID(Integer ID)
	{
		ItemID=ID;
	}
	*/
	
	public void SetDrinkType(Integer theType)
	{
		Type = theType;
	}
	
	public void SetOptionGroup(Integer Group)
	{
		OptionGroup = Group;
	}
	
	public void SetOptionValue(Integer Value)
	{
		OptionValue = Value;
	}
	
	public void SetOptionCount(Integer Count)
	{
		OptionCount=Count;
	}

}
