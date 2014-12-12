package com.freewaycoffee.client;

import java.util.Comparator;

public class FreewayCoffeeFoodDrinkOptionDataListItem 
{
	public Integer OptionID;
	public String OptionText;
	//public String OptionCostPer;
	public FreewayCoffeeFoodDrinkOption Option;
	public FreewayCoffeeFoodDrinkTypeOption DrinkTypeOption;
	public FreewayCoffeeFoodDrinkOptionGroup OptionGroup;
	public FreewayCoffeeFoodDrinkUserOption UserOption;
	//public Integer OptionType;
	public Integer OptionCount;
	public boolean IsNone;
	public Integer OptionSortOrder;
	
	// The idea here is that the web server has classified things into brackets based on Sort Order.
	// The primary sort key is SortOrder. However, if multiple options have the same sort order then we sort them alphabetically
	
	public static Comparator<FreewayCoffeeFoodDrinkOptionDataListItem> GetDisplayComparator()
	{
		return new Comparator<FreewayCoffeeFoodDrinkOptionDataListItem>()
		{

			@Override
			public int compare(FreewayCoffeeFoodDrinkOptionDataListItem object1, FreewayCoffeeFoodDrinkOptionDataListItem object2) 
			{
				
				
				if(object1.OptionSortOrder.equals(object2.OptionSortOrder))
				{
					return object1.OptionText.compareToIgnoreCase(object2.OptionText);
					
				}
				else
				{
					return object1.OptionSortOrder.compareTo(object2.OptionSortOrder);
				}
			}
		};
	}
	
	public FreewayCoffeeFoodDrinkUserOption CreateUserDrinkOption()
	{
		FreewayCoffeeFoodDrinkUserOption TheUserOption = new FreewayCoffeeFoodDrinkUserOption();
		TheUserOption.SetDrinkOptionGroupID(Option.GetOptionGroupID());
		// Not in the table -- bit scary, should we add it ? UserOption.SetDrinkOptionID(Option.GetOptionValue());
		TheUserOption.SetOptionCount(OptionCount);
		TheUserOption.SetDrinkOptionID(Option.GetOptionID()); // Default Option points just to FoodDrinkOption, NOT FoodDrinkTypeOption
		
		
		TheUserOption.SetDrinkTypesOptionID(DrinkTypeOption.GetFoodDrinkTypeOptionID());			
		//FreewayCoffeeFoodDrinkOption Op= FindDrinkOption(Option.GetOptionGroup(),Option.GetOptionValue());
		TheUserOption.SetSortOrder(OptionSortOrder);
		
		return TheUserOption;
	}
}
