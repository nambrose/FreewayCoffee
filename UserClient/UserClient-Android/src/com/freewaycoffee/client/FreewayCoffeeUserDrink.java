package com.freewaycoffee.client;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;


public class FreewayCoffeeUserDrink 
{
	private Integer m_UserDrinkID;
	private Integer m_DrinkTypeID;
	
	private String m_UserDrinkDrinkTypeLongDescr;
	private String m_UserDrinkCost;
	private String m_UserDrinkName;
	private String m_UserDrinkExtra;
	private boolean m_IncludeDefault;
	// Problem arises if we add a drink option. Then this becomes invalid. Can't really happen as we "reget" the drink on add/edit
	private String m_UserDrinkOptionsText; 
	
	//private ArrayList<FreewayCoffeeFoodDrinkUserOption> m_UserDrinkOptionsList;
	private FreewayCoffeeFoodDrinkUserOptionList m_UserOptions;
	
	public static FreewayCoffeeUserDrink CloneUserDrink(FreewayCoffeeUserDrink TheDrink)
	{
		FreewayCoffeeUserDrink NewDrink = new FreewayCoffeeUserDrink();
		if(TheDrink.m_UserDrinkID!=null)
		{
			NewDrink.m_UserDrinkID = new Integer(TheDrink.m_UserDrinkID);
		}
		NewDrink.m_DrinkTypeID = new Integer(TheDrink.m_DrinkTypeID);
		NewDrink.m_UserDrinkDrinkTypeLongDescr = TheDrink.m_UserDrinkDrinkTypeLongDescr;
		NewDrink.m_UserDrinkCost = TheDrink.m_UserDrinkCost;
		NewDrink.m_UserDrinkName = TheDrink.m_UserDrinkName;
		NewDrink.m_UserDrinkExtra = TheDrink.m_UserDrinkExtra;
		if(TheDrink.m_IncludeDefault==true)
		{
			NewDrink.m_IncludeDefault=true;
		}
		else
		{
			NewDrink.m_IncludeDefault = false;
		}
		
		NewDrink.m_UserOptions = FreewayCoffeeFoodDrinkUserOptionList.CloneUserOptions(TheDrink.m_UserOptions);
		
		
		return NewDrink;
	}
	
	public String MakeOptionValueStringForOptionGroup(FreewayCoffeeApp appState,Integer GroupID)											
	{
		return m_UserOptions.MakeOptionValueStringForOptionGroup(appState,GroupID,m_DrinkTypeID);
	}
	public String MakeOptionCostForOptionGroup(FreewayCoffeeApp appState,Integer GroupID)
	{
		return m_UserOptions.MakeOptionCostForOptionGroup(appState,GroupID,m_DrinkTypeID);
	}

	public FreewayCoffeeUserDrink()
	{
		m_UserOptions = new FreewayCoffeeFoodDrinkUserOptionList();
	}
	public Integer GetUserDrinkID()
	{
		return m_UserDrinkID;
	}
	
	public void SetUserDrinkID(Integer UserDrinkID)
	{
		m_UserDrinkID = UserDrinkID;
	}
	
	public Integer GetDrinkTypeID()
	{
		return m_DrinkTypeID;
	}
	
	public void SetDrinkTypeID(Integer DrinkTypeID)
	{
		m_DrinkTypeID=DrinkTypeID;
	}
	
	public String GetDrinkTypeLongDescr()
	{
		return m_UserDrinkDrinkTypeLongDescr;
	}
	
	public void SetDrinkTypeLongDescr(String UserDrinkDrinkTypeLongDescr)
	{
		m_UserDrinkDrinkTypeLongDescr = UserDrinkDrinkTypeLongDescr;
	}
	
	public boolean GetIncludeDefault()
	{
		return m_IncludeDefault;
	}
	
	public void SetIncludeDefault(boolean IncludeDefault)
	{
		m_IncludeDefault = IncludeDefault;
	}
	
	public String GetUserDrinkCost()
	{
		return m_UserDrinkCost;
	}
	
	public void SetUserDrinkCost(String UserDrinkCost)
	{
		m_UserDrinkCost = UserDrinkCost;
	}

	public String GetUserDrinkName()
	{
		return m_UserDrinkName;
	}
	
	public void SetUserDrinkName(String UserDrinkName)
	{
		m_UserDrinkName = UserDrinkName;
	}
	
	public String GetUserDrinkExtra()
	{
		return m_UserDrinkExtra;
	}
	
	public void SetUserDrinkExtra(String UserDrinkExtra)
	{
		m_UserDrinkExtra = UserDrinkExtra;
	}
	
	public String GetUserDrinkOptionsText()
	{
		return m_UserDrinkOptionsText;
	}
	
	public void SetUserDrinkOptionsText(String UserDrinkOptionsText)
	{
		m_UserDrinkOptionsText = UserDrinkOptionsText;
	}
	
	public void ClearUserDrinkOptions()
	{
		m_UserOptions.Clear();
		
	}
	
	public FreewayCoffeeFoodDrinkUserOptionList GetUserDrinkOptions()
	{
		return m_UserOptions;
	}
	
	public void AddUserDrinkOption(FreewayCoffeeFoodDrinkUserOption Option)
	{
		
		 m_UserOptions.AddUserDrinkOption(Option);
		SortUserDrinkOptions(); // Slow but safe!
	}
	
	public void SortUserDrinkOptions()
	{
		m_UserOptions.Sort();
	}
	
	public FreewayCoffeeFoodDrinkUserOption FindUserDrinkOptionByDrinkOptionID(Integer DrinkOptionID)
	{
		return m_UserOptions.FindUserDrinkOptionByDrinkOptionID(DrinkOptionID);
	}
	
	public void RemoveOptionByOptionID(Integer DrinkOptionId)
	{
		m_UserOptions.RemoveOptionByOptionID(DrinkOptionId);
	}
	
	public void RemoveAllOptionsForOptionGroup(Integer OptionGroupID)
	{
		m_UserOptions.RemoveAllOptionsForOptionGroup(OptionGroupID);
	}
	
	public void RemoveAllOptionsForAnyOtherOptionInGroup(Integer OptionGroupID,Integer OptionID)
	{
		m_UserOptions.RemoveAllOptionsForAnyOtherOptionInGroup(OptionGroupID,OptionID);
	}
	
	public void ReplaceUserOptions(FreewayCoffeeFoodDrinkUserOptionList NewOptions)
	{
		m_UserOptions.Clear();
		m_UserOptions = FreewayCoffeeFoodDrinkUserOptionList.CloneUserOptions(NewOptions); // I think I can just assign here but safer .... TODO
	}
	
	public void AddOptionsListToPost(List<NameValuePair> nameValuePairs ) throws UnsupportedEncodingException
	{
		 m_UserOptions.AddOptionsListToPost(nameValuePairs);
	}
}
