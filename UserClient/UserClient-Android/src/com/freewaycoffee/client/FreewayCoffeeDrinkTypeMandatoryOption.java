package com.freewaycoffee.client;

public class FreewayCoffeeDrinkTypeMandatoryOption 
{
	private Integer ItemID;
	private Integer DrinkType;
	private Integer OptionGroup;
	
	public Integer GetItemID()
	{
		return ItemID;
	}
	
	public Integer GetDrinkType()
	{
		return DrinkType;
	}
	
	public Integer GetOptionGroup()
	{
		return OptionGroup;
	}
	
	
	// SET
	public void SetItemID(Integer ID)
	{
		ItemID = ID;
	}
	
	public void SetDrinkType(Integer Type)
	{
		DrinkType = Type;
	}
	
	public void SetOptionGroup(Integer Group)
	{
		OptionGroup = Group;
	}
}
