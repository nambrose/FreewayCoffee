package com.freewaycoffee.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FreewayCoffeeFoodDrinkOptionGroup 
{
	public enum SelectionType
	{
		SelectOne,
		SelectMulti
	}
	
	private Integer m_GroupID;
	private String m_GroupName;
	private Integer m_SortOrder;
	private String m_PartName;
	private SelectionType m_SelectionType;
	
	private ArrayList<FreewayCoffeeFoodDrinkOption> m_DrinkGroupDrinkOptions;
	
	public FreewayCoffeeFoodDrinkOptionGroup()
	{
		m_DrinkGroupDrinkOptions = new ArrayList<FreewayCoffeeFoodDrinkOption>();
	}
	public Integer GetGroupID()
	{
		return m_GroupID;
	}
	
	public void SetGroupID(Integer GroupID)
	{
		m_GroupID = GroupID;
	}
	
	public String GetGroupName()
	{
		return m_GroupName;
	}
	
	public void SetGroupName(String GroupName)
	{
		m_GroupName = GroupName;
	}
	
	public Integer GetSortOrder()
	{
		return m_SortOrder;
	}
	
	public void SetSortOrder(Integer SortOrder)
	{
		m_SortOrder = SortOrder;
	}
	
	public String GetPartName()
	{
		return m_PartName;
	}
	
	public void SetPartName(String PartName)
	{
		m_PartName = PartName;
	}
	
	public SelectionType GetSelectionType()
	{
		return m_SelectionType;
	}
	
	public void SetSelectionType(SelectionType Type)
	{
		m_SelectionType = Type;
	}

	public void ClearAllDrinkOptions()
	{
		m_DrinkGroupDrinkOptions.clear();
	}
	
	public void AddDrinkOption(FreewayCoffeeFoodDrinkOption Option)
	{
		m_DrinkGroupDrinkOptions.add(Option);
	}
	
	public FreewayCoffeeFoodDrinkOption FindDrinkOption(Integer DrinkOptionID)
	{
		for( FreewayCoffeeFoodDrinkOption Option : m_DrinkGroupDrinkOptions )
		{
			if(Option.GetOptionID().equals(DrinkOptionID))
			{
				return Option;
			}
		}
		return null;
	}
	
	public ArrayList<FreewayCoffeeFoodDrinkOption> GetFoodDrinkOptions()
	{
		return m_DrinkGroupDrinkOptions;
	}
}
