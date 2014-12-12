package com.freeewaycoffee.ordermanager;

import java.util.ArrayList;

public class FCOrderManagerOrderItemList
{
	private ArrayList<FCOrderManagerOrderItem> ItemList;
	
	FCOrderManagerOrderItemList()
	{
		Initialize();
	}
	
	private void Initialize()
	{
		ItemList=new ArrayList<FCOrderManagerOrderItem> ();
	}
	
	public void AddOrderItem(FCOrderManagerOrderItem TheItem)
	{
		ItemList.add(TheItem);
	}
	
	public void ClearItems()
	{
		ItemList.clear();
	}
	
	public Integer Size()
	{
		return ItemList.size();
	}
	
	public FCOrderManagerOrderItem GetItemAtIndex(int Index)
	{
		return ItemList.get(Index);
	}

}
