package com.freewaycoffee.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FreewayCoffeeCarModelList
{
	private HashMap<Integer,FreewayCoffeeCarModel> Models;
	
	public FreewayCoffeeCarModelList()
	{
		Models = new HashMap<Integer,FreewayCoffeeCarModel>();
	}
	
	public void AddModel(FreewayCoffeeCarModel Model)
	{
		Models.put(Model.GetModelID(),Model);
	}
	
	public void Clear()
	{
		Models.clear();
	}
	public FreewayCoffeeCarModel GetModel(Integer ModelID)
	{
		return Models.get(ModelID);
	}
	
	public int Size()
	{
		return Models.size();
	}
	
	public Set<Map.Entry<Integer,FreewayCoffeeCarModel>> GetModelEntrySet()
	{
		return Models.entrySet();
	}
}