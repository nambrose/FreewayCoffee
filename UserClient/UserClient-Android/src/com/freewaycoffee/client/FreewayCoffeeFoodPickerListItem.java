package com.freewaycoffee.client;
import java.util.Comparator;

public class FreewayCoffeeFoodPickerListItem 
{
	// TODO -- Oops, not a pair anymore!
	public Integer FoodID;
	public String FoodDescr;
	public Integer SortOrder;
	
	FreewayCoffeeFoodPickerListItem()
	{
		FoodID=-1;
		FoodDescr="";
		SortOrder=-1;
	}
	
	public static Comparator<FreewayCoffeeFoodPickerListItem> GetDisplayComparator()
	{
		return new Comparator<FreewayCoffeeFoodPickerListItem>()
		{

			@Override
			public int compare(FreewayCoffeeFoodPickerListItem object1, FreewayCoffeeFoodPickerListItem object2) 
			{
				if(object1.SortOrder.equals(object2.SortOrder))
				{
					return object1.FoodDescr.compareToIgnoreCase(object2.FoodDescr);
					
				}
				else
				{
					return object1.SortOrder.compareTo(object2.SortOrder);
				}
			}
		};
	}
	

}
