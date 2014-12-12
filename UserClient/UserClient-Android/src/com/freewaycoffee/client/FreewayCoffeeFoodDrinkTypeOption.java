package com.freewaycoffee.client;

public class FreewayCoffeeFoodDrinkTypeOption 
{
	private Integer m_FoodDrinkTypeOptionID;
	private Integer m_FoodDrinkTypeID; // Food or Drink Type
	private Integer m_FoodDrinkOptionID; // The actual Final linked Option
	private Integer m_FoodDrinkOptionGroupID; // Option Group
	private Integer m_MinCount;
	private Integer m_MaxCount;
	private String m_CostPer;
	private Integer m_ChargeEach; // Could be a BOOL
	public FreewayCoffeeFoodDrinkTypeOption()
	{
		// NOTE: These are (obviously) the defaults. 
		// To save bandwith (cue danger warnings of really hard bug to diagnose)
		// They are NOT sent by the server if they (the vast majority) are already 0,1,0.00 only "overrides" sent.
		// I sure hope these are good defaults :)
		m_MinCount=0;
		m_MaxCount=1;
		m_CostPer="0.00";
		m_ChargeEach=1; // Default is to charge for each option*count so server will not send if ==1
	}
	
	public Integer GetFoodDrinkTypeOptionID()
	{
		return m_FoodDrinkTypeOptionID;
	}
	
	public void SetFoodDrinkTypeOptionID(Integer FoodDrinkTypeOptionID)
	{
		m_FoodDrinkTypeOptionID = FoodDrinkTypeOptionID;
	}
	
	
	public Integer GetFoodDrinkTypeID()
	{
		return m_FoodDrinkTypeID;
	}
	
	public void SetFoodDrinkTypeID(Integer FoodDrinkTypeID)
	{
		m_FoodDrinkTypeID = FoodDrinkTypeID;
	}
	
	public Integer GetFoodDrinkOptionID()
	{
		return m_FoodDrinkOptionID;
	}
	
	public void SetFoodDrinkOptionID(Integer FoodDrinkOptionID)
	{
		m_FoodDrinkOptionID = FoodDrinkOptionID;
	}
	
	public Integer GetFoodDrinkOptionGroupID()
	{
		return m_FoodDrinkOptionGroupID;
	}
	
	public void SetFoodDrinkOptionGroupID(Integer FoodDrinkOptionGroupID)
	{
		m_FoodDrinkOptionGroupID = FoodDrinkOptionGroupID;
	}
	
	public Integer GetMinCount()
	{
		return m_MinCount;
	}
	
	public void SetMinCount(Integer Count)
	{
		m_MinCount = Count;
	}
	public Integer GetMaxCount()
	{
		return m_MaxCount;
	}
	public void SetMaxCount(Integer Count)
	{
		m_MaxCount = Count;
	}
	
	public String GetCostPer()
	{
		return m_CostPer;
	}
	
	public void SetCostPer(String Cost)
	{
		m_CostPer = Cost;
	}
	
	public void SetChargeEach(Integer ChargeEach)
	{
		m_ChargeEach = ChargeEach;
	}
	public Integer GetChargeEach()
	{
		return m_ChargeEach;
	}
	
	
}
