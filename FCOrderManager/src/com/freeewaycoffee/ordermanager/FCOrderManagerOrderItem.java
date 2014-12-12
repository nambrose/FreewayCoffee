package com.freeewaycoffee.ordermanager;

public class FCOrderManagerOrderItem
{
	enum OrderItemKind
	{
		ORDER_ITEM_TYPE_UNKNOWN,
		ORDER_ITEM_TYPE_FOOD,
		ORDER_ITEM_TYPE_DRINK
	}
	
	private Integer m_ItemID;
	private OrderItemKind m_ItemType;
	private String m_ItemDescription;
	private String m_ItemCost;
	
	public FCOrderManagerOrderItem()
	{
		Initialize();
	}
	
	public void Initialize()
	{
		m_ItemID=-1;
		m_ItemType=OrderItemKind.ORDER_ITEM_TYPE_UNKNOWN;
		m_ItemDescription="";
		m_ItemCost="";
	}
	
	public void SetItemID(Integer ID)
	{
		m_ItemID=ID;
	}
	
	public Integer GetItemID()
	{
		return m_ItemID;
	}
	
	public void SetItemTypeByEnum(OrderItemKind Type)
	{
		m_ItemType = Type;
	}
	
	public boolean SetItemType(String Type)
	{
		if(Type.equalsIgnoreCase("food"))
		{
			m_ItemType = OrderItemKind.ORDER_ITEM_TYPE_FOOD;
			return true;
		}
		else if(Type.equalsIgnoreCase("drink"))
		{
			m_ItemType = OrderItemKind.ORDER_ITEM_TYPE_DRINK;
			return true;
		}
		m_ItemType = OrderItemKind.ORDER_ITEM_TYPE_UNKNOWN;
		return false;
	}
	
	public String GetItemTypeAsString()
	{
		switch(m_ItemType)
		{
		
		case ORDER_ITEM_TYPE_FOOD:
			return "Food";
		case ORDER_ITEM_TYPE_DRINK:
			return "Drink";
		case ORDER_ITEM_TYPE_UNKNOWN:
		default:
			return "Unknown";
			
		}
	}
	
	public OrderItemKind GetItemType()
	{
		return m_ItemType;
	}
	
	public void SetItemDescription(String ItemDescription)
	{
		m_ItemDescription=ItemDescription;
	}
	
	public String GetItemDescription()
	{
		return m_ItemDescription;
	}
	
	public void SetItemCost(String ItemCost)
	{
		m_ItemCost=ItemCost;
	}
	
	public String GetItemCost()
	{
		return "$" + m_ItemCost;
	}
}
