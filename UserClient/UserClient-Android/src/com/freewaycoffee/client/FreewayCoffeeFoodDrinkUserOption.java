package com.freewaycoffee.client;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class FreewayCoffeeFoodDrinkUserOption 
{
	private Integer m_UserDrinkOptionID;
	private Integer m_DrinkOptionID;
	private Integer m_DrinkTypesOptionID;
	private Integer m_DrinkOptionGroupID;
	private Integer m_OptionCount;
	
	private Integer m_SortOrder; // Set in App, not downloaded ?
	
	public static FreewayCoffeeFoodDrinkUserOption CloneUserOption(FreewayCoffeeFoodDrinkUserOption TheOption)
	{
		FreewayCoffeeFoodDrinkUserOption NewOption = new FreewayCoffeeFoodDrinkUserOption();
		
		// This one is allowed to be NULL because it may be a Local option and not come from the DB yet.
		if(TheOption.m_UserDrinkOptionID!=null)
		{
			NewOption.m_UserDrinkOptionID = new Integer(TheOption.m_UserDrinkOptionID);
		}
		else
		{
			NewOption.m_UserDrinkOptionID =null;
		}
		NewOption.m_DrinkOptionID = new Integer(TheOption.m_DrinkOptionID);
		NewOption.m_DrinkTypesOptionID = new Integer(TheOption.m_DrinkTypesOptionID);
		NewOption.m_DrinkOptionGroupID = new Integer(TheOption.m_DrinkOptionGroupID);
		NewOption.m_OptionCount = new Integer(TheOption.m_OptionCount);
		NewOption.m_SortOrder = new Integer(TheOption.m_SortOrder);
		return NewOption;
	}
	
	public static Comparator<FreewayCoffeeFoodDrinkUserOption> GetDisplayComparator()
	{
		return new Comparator<FreewayCoffeeFoodDrinkUserOption>()
		{

			@Override
			public int compare(FreewayCoffeeFoodDrinkUserOption object1, FreewayCoffeeFoodDrinkUserOption object2) 
			{
				return object1.m_SortOrder.compareTo(object2.m_SortOrder);
			}
		};
	}
	
	
	public FreewayCoffeeFoodDrinkUserOption()
	{
		m_OptionCount=1; // Web server wont send it if its '1' so default it
	}
	public Integer GetID()
	{
		return m_UserDrinkOptionID;
	}
	
	public void SetID(Integer ID)
	{
		m_UserDrinkOptionID=ID;
	}
	
	public Integer GetDrinkOptionID()
	{
		return m_DrinkOptionID;
	}
	
	public void SetDrinkOptionID(Integer DrinkOptionID)
	{
		m_DrinkOptionID = DrinkOptionID;
	}
	
	public Integer GetDrinkTypesOptionID()
	{
		return m_DrinkTypesOptionID;
	}
	
	public void SetDrinkTypesOptionID(Integer DrinkTypesOptionID)
	{
		m_DrinkTypesOptionID = DrinkTypesOptionID;
	}
	
	public Integer GetDrinkOptionGroupID()
	{
		return m_DrinkOptionGroupID;
	}
	
	public void SetDrinkOptionGroupID(Integer OptionGroupID)
	{
		m_DrinkOptionGroupID = OptionGroupID;
	}
	
	public Integer GetOptionCount()
	{
		return m_OptionCount;
	}
	
	public void SetOptionCount(Integer OptionCount)
	{
		m_OptionCount = OptionCount;
	}
	
	public Integer GetSortOrder()
	{
		return m_SortOrder;
	}
	
	public void SetSortOrder(Integer Order)
	{
		m_SortOrder = Order;
	}
	
	public void IncrementCount()
	{
		m_OptionCount++;
	}
	
	public void DecrementCount()
	{
		m_OptionCount--;
	}
	public String MakeOptionValueString(FreewayCoffeeApp appState,Integer DrinkType)
	{
		FreewayCoffeeFoodDrinkOption Option = appState.FindDrinkOption(m_DrinkOptionGroupID,m_DrinkOptionID);
		FreewayCoffeeFoodDrinkOptionGroup Group =appState.GetDrinkOptionGroups().get(m_DrinkOptionGroupID);
		// Get FoodDrinkOptionType, check if Max>1 and then add (1), (2) if count >0
		if(Option==null)
		{
			return null;
		}
		String Result = Option.GetOptionName();
		
		FreewayCoffeeFoodDrinkTypeOption DrinkTypeOption = appState.FindDrinkTypeOption(m_DrinkOptionGroupID,DrinkType,m_DrinkOptionID);
		
		if(Group.GetSelectionType()==FreewayCoffeeFoodDrinkOptionGroup.SelectionType.SelectMulti)
		{
			if(m_OptionCount>0)
			{
				Result += " (" + m_OptionCount + ")";
			}
		}
		else if( (m_OptionCount>0) && (DrinkTypeOption.GetMaxCount()>1) )
		{
			Result += " (" + m_OptionCount + ")";
		}
		
		return Result;
			
	}
	public BigDecimal GetTotalCost(FreewayCoffeeApp appState,Integer DrinkType)
	{
		// NOTE:Could also use a method here like FindDrinkTypeOptionByDrinkTypeOptionID as I think both are unique in that list.
		FreewayCoffeeFoodDrinkTypeOption Option = appState.FindDrinkTypeOption(m_DrinkOptionGroupID,DrinkType,m_DrinkOptionID);
		if(Option==null)
		{
			return null;
		}
		BigDecimal Result = new BigDecimal(Option.GetCostPer());
		
		// Only multiple count*cost if "Charge per item" == 1, otherwise we just charge for 1
		if(Option.GetChargeEach().equals(1))
		{
			Result = Result.multiply(new BigDecimal(m_OptionCount));
		}
		return Result;
		
	}
	
	
}
