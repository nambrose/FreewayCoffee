package com.freewaycoffee.client;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class FreewayCoffeeFoodDrinkType
{
	private Integer TypeID;
	
	// Since we kind of need to know about some of these at least, consider parsing into real member vars (later) TODO
	
	private String TypeName;
	private Integer SortOrder;
	private Map<Integer,FreewayCoffeeFoodDrinkTypeOptionList> m_DrinkTypeOptions;
	
	private Integer CategoryID; // Currently Foods only. Whattamess
	private String BaseCost; // Foods only!
	private String FoodDrinkTypeText;
	
	public FreewayCoffeeFoodDrinkType()
	{
		TypeID=-1;
		CategoryID=-1;
		FoodDrinkTypeText="";
		m_DrinkTypeOptions = new HashMap<Integer,FreewayCoffeeFoodDrinkTypeOptionList>();
	}
	
	public static Comparator<FreewayCoffeeFoodDrinkType> GetDisplayComparator()
	{
		return new Comparator<FreewayCoffeeFoodDrinkType>()
		{

			@Override
			public int compare(FreewayCoffeeFoodDrinkType object1, FreewayCoffeeFoodDrinkType object2) 
			{
				// Add Category ID here somewhere !!! TODO FIXME
				if(object1.SortOrder.equals(object2.SortOrder))
				{
					return object1.TypeName.compareToIgnoreCase(object2.TypeName);
					
				}
				else
				{
					return object1.SortOrder.compareTo(object2.SortOrder);
				}
			}
		};
	}

	public boolean HasZeroBaseCost()
	{
		if(BaseCost.equals("0.00"))
		{
			return true;
		}
		return false;
				
	}
	public String GetBaseCost()
	{
		return BaseCost;
	}
	
	public void SetBaseCost(String Cost)
	{
		BaseCost=Cost;	
	}
	
	public Integer GetCategoryID()
	{
		return CategoryID;
	}
	
	public void SetCategoryID(Integer ID)
	{
		CategoryID=ID;
	}
	
	public Integer GetTypeID()
	{
		return TypeID;
	}
	
	public void SetTypeID(Integer typeID)
	{
		TypeID=typeID;
	}
	
	public void SetTypeName(String name)
	{
		TypeName = name;
	}
	
	public String GetTypeName()
	{
		return TypeName;
	}
	
	public Integer GetSortOrder()
	{
		return SortOrder;
	}
	
	
	public void SetSortOrder(Integer Order)
	{
		SortOrder=Order;
	}
	
	public String GetFoodDrinkTypeText()
	{
		return FoodDrinkTypeText;
	}
	
	public void SetFoodDrinkTypeText(String Text)
	{
		FoodDrinkTypeText=Text;
	}
	// Use this to determine if ANY sizes/milks/caffeines are ever valid for a DrinkType (Based on Drink Option Group ID)
	// Used for instance to eliminate this option from the OptionPicker list entirely if none are applicable (say a drink that
	// does not have syrups or milk etc)
	public boolean AreAnyOptionsValidForDrinkOptionGroup(Integer DrinkOptionGroup)
	{
		FreewayCoffeeFoodDrinkTypeOptionList OptList = m_DrinkTypeOptions.get(DrinkOptionGroup);
		if(OptList==null)
		{
			return false;
		}
		if(OptList.Length()==0)
		{
			return false; // This should never really happen
		}
		
		return true;
		
	}
	
	public boolean IsOptionValidForFoodDrinkType(Integer GroupID,Integer DrinkOptionID)
	{
		if(FindDrinkTypeOptionByDrinkOptionID(GroupID,DrinkOptionID)!=null)
		{
			return true;
		}
		return false;
		
	}
	
	
	public void ClearAllOptions()
	{
		m_DrinkTypeOptions.clear();
	}
	
	public void AddDrinkTypeOption(FreewayCoffeeFoodDrinkTypeOption Option)
	{
		FreewayCoffeeFoodDrinkTypeOptionList OptList = m_DrinkTypeOptions.get(Option.GetFoodDrinkOptionGroupID());
		if(OptList==null)
		{
			OptList = new FreewayCoffeeFoodDrinkTypeOptionList();
			m_DrinkTypeOptions.put(Option.GetFoodDrinkOptionGroupID(), OptList);
		}
		OptList.AddOption(Option);
		
	}
			
	public FreewayCoffeeFoodDrinkTypeOption FindDrinkTypeOptionByDrinkOptionID(Integer DrinkOptionGroup, Integer DrinkOptionID)
	{
		FreewayCoffeeFoodDrinkTypeOptionList OptList = m_DrinkTypeOptions.get(DrinkOptionGroup);
		if(OptList==null)
		{
			return null;
		}
		return OptList.FindOptionByDrinkTypeID(DrinkOptionID);
	}
}
