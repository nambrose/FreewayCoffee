package com.freewaycoffee.client;

public class FreewayCoffeeFoodDrinkOption 
{
	private Integer m_OptionID;
	private Integer m_OptionGroupID;
	private String m_OptionName;
	private Integer m_SortOrder;

	public Integer GetOptionID()
	{
		return m_OptionID;
	}
	
	public void SetOptionID(Integer OptionID)
	{
		m_OptionID = OptionID;
	}
	
	public Integer GetOptionGroupID()
	{
		return m_OptionGroupID;
	}
	
	public void SetGroupOptionID(Integer GroupID)
	{
		m_OptionGroupID = GroupID;
	}
	
	public String GetOptionName()
	{
		return m_OptionName;
	}
	
	public void SetOptionName(String OptionName)
	{
		m_OptionName = OptionName;
	}
	
	public Integer GetSortOrder()
	{
		return m_SortOrder;
	}
	
	public void SetSortOrder(Integer SortOrder)
	{
		m_SortOrder = SortOrder;
	}
}
