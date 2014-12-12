package com.freewaycoffee.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FreewayCoffeeCarMakeModelData 
{
	private FreewayCoffeeCarColorList CarColors;
	private HashMap<Integer,FreewayCoffeeCarMake> CarMakeAndModels;
	
	public FreewayCoffeeCarMakeModelData()
	{
		CarColors=new FreewayCoffeeCarColorList();
		CarMakeAndModels = new HashMap<Integer,FreewayCoffeeCarMake>();
	}
	
	public Set<Map.Entry<Integer, FreewayCoffeeCarMake> >  GetMakeEntrySet()
	{
		return CarMakeAndModels.entrySet();
		
	}
	
	public FreewayCoffeeCarColorList GetCarColorList()
	{
		return CarColors;
	}
	
	public Iterator GetMakeIterator()
	{
		return CarMakeAndModels.entrySet().iterator();
	}
	
	public void Clear()
	{
		CarMakeAndModels.clear();
		CarColors.Clear();
	}
	
	public boolean IsDataPopulated()
	{
		if( (CarColors.GetSize()>0) && CarMakeAndModels.size()>0)
		{
			return true;
		}
		else
		{
			// Since we only want all the data, if one was empty and one populated, something went wrong so we just clear it and re-get it.
			CarColors.Clear();
			CarMakeAndModels.clear();
			return false;
		}
	}
	
	public void AddCarColor(Integer ID,FreewayCoffeeCarColor Color)
	{
		CarColors.AddCarColor(ID,Color);
	}
	
	public void AddCarMake(Integer ID,FreewayCoffeeCarMake CarMake)
	{
		CarMakeAndModels.put(ID,CarMake);
	}
	
	public FreewayCoffeeCarMake GetCarMake(Integer MakeID)
	{
		return CarMakeAndModels.get(MakeID);
	}
	
	public FreewayCoffeeCarColor GetCarColor(Integer ID)
	{
		return CarColors.GetCarColor(ID);
	}
}

