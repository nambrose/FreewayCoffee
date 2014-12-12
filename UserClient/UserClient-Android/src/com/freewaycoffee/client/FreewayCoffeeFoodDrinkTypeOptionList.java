package com.freewaycoffee.client;

import java.util.ArrayList;

public class FreewayCoffeeFoodDrinkTypeOptionList 
{
	private ArrayList<FreewayCoffeeFoodDrinkTypeOption> m_Options;
	
	public FreewayCoffeeFoodDrinkTypeOptionList()
	{
		m_Options = new ArrayList<FreewayCoffeeFoodDrinkTypeOption>();
	}
	
	public void AddOption(FreewayCoffeeFoodDrinkTypeOption Option)
	{
		m_Options.add(Option);
	}
	
	public void Clear()
	{
		m_Options.clear();
	}

	public Integer Length()
	{
		return m_Options.size();
	}
	
	FreewayCoffeeFoodDrinkTypeOption FindOptionByDrinkTypeID(Integer DrinkTypeOption)
	{
		for(FreewayCoffeeFoodDrinkTypeOption Option : m_Options)
		{
			if(Option.GetFoodDrinkOptionID().equals(DrinkTypeOption))
			{
				return Option;
			}
		}
		return null;
	}
	
}
