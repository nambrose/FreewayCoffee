package com.freewaycoffee.clientobjlib;

import java.util.ArrayList;

public class FCOrderItemList
{
	private ArrayList<FCOrderItem> ItemList;
	
	FCOrderItemList()
	{
		Initialize();
	}
	
	private void Initialize()
	{
		ItemList=new ArrayList<FCOrderItem> ();
	}
	
	public void AddOrderItem(FCOrderItem TheItem)
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
	
	public FCOrderItem GetItemAtIndex(int Index)
	{
		return ItemList.get(Index);
	}

}
