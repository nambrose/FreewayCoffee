package com.freewaycoffee.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FreewayCoffeeCarColorList
{
	private HashMap<Integer,FreewayCoffeeCarColor> CarColors;
	
	public FreewayCoffeeCarColorList()
	{
		CarColors = new HashMap<Integer,FreewayCoffeeCarColor>();
		
	}
	
	public Set<Map.Entry<Integer, FreewayCoffeeCarColor> >  GetColorsEntrySet()
	{
		return CarColors.entrySet();
		
	}
	
	public void Clear()
	{
		CarColors.clear();
	}
	
	public void AddCarColor(FreewayCoffeeCarColor Color)
	{
		CarColors.put(Color.GetColorID(),Color);
	}
	
	public int GetSize()
	{
		return CarColors.size();
	}
	
	public void AddCarColor(Integer ID, FreewayCoffeeCarColor Color)
	{
		CarColors.put(ID,Color);
	}
	
	public FreewayCoffeeCarColor GetCarColor(Integer ID)
	{
		return CarColors.get(ID);
	}
}