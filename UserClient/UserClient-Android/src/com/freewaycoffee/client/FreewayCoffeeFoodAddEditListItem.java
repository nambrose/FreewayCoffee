package com.freewaycoffee.client;

import java.util.Comparator;

public class FreewayCoffeeFoodAddEditListItem 
{
	public Integer FoodID;
	public String FoodName;
	public String FoodCost;
	
	public Integer SortOrder;
	
	// The idea here is that the web server has classified things into brackets based on Sort Order.
	// The primary sort key is SortOrder. However, if multiple options have the same sort order then we sort them alphabetically
	
	public static Comparator<FreewayCoffeeFoodAddEditListItem> GetDisplayComparator()
	{
		return new Comparator<FreewayCoffeeFoodAddEditListItem>()
		{

			@Override
			public int compare(FreewayCoffeeFoodAddEditListItem object1, FreewayCoffeeFoodAddEditListItem object2) 
			{
				
				
				if(object1.SortOrder.equals(object2.SortOrder))
				{
					return object1.FoodName.compareToIgnoreCase(object2.FoodName);
					
				}
				else
				{
					return object1.SortOrder.compareTo(object2.SortOrder);
				}
			}
		};
	}
}
