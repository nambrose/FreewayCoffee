package com.freewaycoffee.client;
import java.util.Comparator;

public class FreewayCoffeeFoodCategoryPickerListItem
{
	// TODO -- Oops, not a pair anymore!
	public Integer FoodCategoryID;
	public String FoodCategoryDescr;
	public Integer SortOrder;
	
	FreewayCoffeeFoodCategoryPickerListItem()
	{
		FoodCategoryID=-1;
		FoodCategoryDescr="";
		SortOrder=-1;
	}
	
	public static Comparator<FreewayCoffeeFoodCategoryPickerListItem> GetDisplayComparator()
	{
		return new Comparator<FreewayCoffeeFoodCategoryPickerListItem>()
		{

			@Override
			public int compare(FreewayCoffeeFoodCategoryPickerListItem object1, FreewayCoffeeFoodCategoryPickerListItem object2) 
			{
				if(object1.SortOrder.equals(object2.SortOrder))
				{
					return object1.FoodCategoryDescr.compareToIgnoreCase(object2.FoodCategoryDescr);
					
				}
				else
				{
					return object1.SortOrder.compareTo(object2.SortOrder);
				}
			}
		};
	}
}
