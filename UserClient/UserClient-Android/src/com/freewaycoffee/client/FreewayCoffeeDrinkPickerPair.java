package com.freewaycoffee.client;

import java.util.Comparator;

public class FreewayCoffeeDrinkPickerPair 
{
	// TODO -- Oops, not a pair anymore!
	public FreewayCoffeeApp.FoodDrink Type;
	public Integer ID;
	public String Descr;
	public Integer SortOrder;
	public FreewayCoffeeFoodDrinkType DrinkType;
	FreewayCoffeeDrinkPickerPair()
	{
		ID=-1;
		Descr="";
		SortOrder=-1;
	}
	
	public static Comparator<FreewayCoffeeDrinkPickerPair> GetDisplayComparator()
	{
		return new Comparator<FreewayCoffeeDrinkPickerPair>()
		{

			@Override
			public int compare(FreewayCoffeeDrinkPickerPair object1, FreewayCoffeeDrinkPickerPair object2) 
			{
				if(object1.Type.equals(object2.Type))
				{
					if(object1.SortOrder.equals(object2.SortOrder))
					{
						return object1.Descr.compareToIgnoreCase(object2.Descr);
						
					}
					else
					{
						return object1.SortOrder.compareTo(object2.SortOrder);
					}
				}
				else if((object1.Type.equals(FreewayCoffeeApp.FoodDrink.Food)) && (object2.Type.equals(FreewayCoffeeApp.FoodDrink.Drink)))
				{
					return -1;
				}
				else if((object1.Type.equals(FreewayCoffeeApp.FoodDrink.Drink)) && (object2.Type.equals(FreewayCoffeeApp.FoodDrink.Food)))
				{
					return 1;
				}
				else
				{
					return 1;
				}
				
			}
		};
	}
	

}
