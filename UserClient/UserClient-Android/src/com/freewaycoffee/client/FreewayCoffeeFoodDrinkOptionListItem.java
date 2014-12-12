package com.freewaycoffee.client;

import java.util.Comparator;

public class FreewayCoffeeFoodDrinkOptionListItem 
{
	static public final Integer OPTION_TYPE_FOOD_DRINK_OPTION=0;
	static public final Integer OPTION_TYPE_EXTRA_OPTIONS=1;
	
	public FreewayCoffeeFoodDrinkOptionGroup OptionGroupRef;
	//public Integer FoodDrinkOption;
	public String FoodDrinkOptionDescr;
	public String FoodDrinkOptionPrice;
	public FreewayCoffeeFoodDrinkOptionGroup.SelectionType PickType;
	public String OptionValue;
	public Integer OptionGroup;
	public boolean IsMandatory;
	public Integer SortOrder;
	public Integer EntryType;
	public boolean IsNone;
	
	public FreewayCoffeeFoodDrinkOptionListItem()
	{
		OptionGroupRef=null;
		//FoodDrinkOption="";
		FoodDrinkOptionDescr="";
		FoodDrinkOptionPrice="";
		PickType=FreewayCoffeeFoodDrinkOptionGroup.SelectionType.SelectOne;
		OptionValue="";
		OptionGroup=-1;
		IsMandatory=false;
		SortOrder=-1;
		EntryType=-1;
		IsNone=true;
	}
	
	
	// The idea here is that the web server has classified things into brackets based on Sort Order.
	// The primary sort key is SortOrder. However, if multiple options have the same sort order then we sort them alphabetically
	
	public static Comparator<FreewayCoffeeFoodDrinkOptionListItem> GetDisplayComparator()
	{
		return new Comparator<FreewayCoffeeFoodDrinkOptionListItem>()
		{

			@Override
			public int compare(FreewayCoffeeFoodDrinkOptionListItem object1, FreewayCoffeeFoodDrinkOptionListItem object2) 
			{
				if(object1.EntryType.compareTo(object2.EntryType)<0)
				{
					return -1;
				}
				else if(object1.EntryType.compareTo(object2.EntryType)>0)
				{
					return 1;
				}
				if(object1.SortOrder.equals(object2.SortOrder))
				{
					return object1.FoodDrinkOptionDescr.compareToIgnoreCase(object2.FoodDrinkOptionDescr);
					
				}
				else
				{
					return object1.SortOrder.compareTo(object2.SortOrder);
				}
			}
		};
	}
}
