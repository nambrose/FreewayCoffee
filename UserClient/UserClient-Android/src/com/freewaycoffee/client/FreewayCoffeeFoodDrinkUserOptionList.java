package com.freewaycoffee.client;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;



public class FreewayCoffeeFoodDrinkUserOptionList 
{
	private ArrayList<FreewayCoffeeFoodDrinkUserOption> m_UserOptions;
	
	public static FreewayCoffeeFoodDrinkUserOptionList CloneUserOptions(FreewayCoffeeFoodDrinkUserOptionList theList)
	{
		FreewayCoffeeFoodDrinkUserOptionList newList = new FreewayCoffeeFoodDrinkUserOptionList();
		
		for(FreewayCoffeeFoodDrinkUserOption UserOption : theList.m_UserOptions)
		{
			FreewayCoffeeFoodDrinkUserOption NewUserOption = FreewayCoffeeFoodDrinkUserOption.CloneUserOption(UserOption);
			newList.AddUserDrinkOption(NewUserOption);
		}
		return newList;
	}
	public FreewayCoffeeFoodDrinkUserOptionList()
	{
		m_UserOptions = new ArrayList<FreewayCoffeeFoodDrinkUserOption>();
	}
	
	public ArrayList<FreewayCoffeeFoodDrinkUserOption> GetUserOptions()
	{
		return m_UserOptions;
	}
	
	public void AddUserDrinkOption(FreewayCoffeeFoodDrinkUserOption theOption)
	{
		if(theOption.GetOptionCount()==0)
		{
			return; // Dont add zeroes!
		}
		m_UserOptions.add(theOption);
	}
	
	
	public String MakeOptionValueStringForOptionGroup(FreewayCoffeeApp appState,Integer GroupID,Integer DrinkTypeID)
	{
		
		// NOTE: We have to obey sort order here on the options, that's going to be a thorn as it's not included. LATER FIXME
		String Result="";
		boolean FirstTime=true;
		for(FreewayCoffeeFoodDrinkUserOption Option : m_UserOptions)
		{
			String SubResult="";
			if(FirstTime!=true)
			{
				SubResult += ", ";
			}
			if(Option.GetDrinkOptionGroupID().equals(GroupID) )
			{
				SubResult += Option.MakeOptionValueString(appState,DrinkTypeID);
				if(SubResult!=null)
				{
					Result+=SubResult;
					FirstTime=false;
				}
			}
			
		}
		
		return Result;
	}
	
	public String MakeOptionCostForOptionGroup(FreewayCoffeeApp appState,Integer GroupID, Integer DrinkTypeID)
	{
		
		// NOTE: We have to obey sort order here on the options, that's going to be a thorn as it's not included. LATER FIXME
		
		BigDecimal CurrentCost = new BigDecimal("0.00");

		for(FreewayCoffeeFoodDrinkUserOption Option : m_UserOptions)
		{
			if(Option.GetDrinkOptionGroupID().equals(GroupID) )
			{
				BigDecimal SubCost = Option.GetTotalCost(appState,DrinkTypeID);
				if(SubCost!=null)
				{
					CurrentCost = CurrentCost.add(SubCost);
				}
			}
		}
		CurrentCost.setScale(2);
		return  CurrentCost.toPlainString();
	}
	
	public void Clear()
	{
	
		m_UserOptions.clear();
	}
	
	public void Sort()
	{
	
		Collections.sort(m_UserOptions,FreewayCoffeeFoodDrinkUserOption.GetDisplayComparator());
	}
	
	public FreewayCoffeeFoodDrinkUserOption FindUserDrinkOptionByDrinkOptionID(Integer DrinkOptionID)
	{
		
		for(FreewayCoffeeFoodDrinkUserOption Option : m_UserOptions)
		{
			if(Option.GetDrinkOptionID().equals(DrinkOptionID))
			{
				return Option;
			}				
		}
		return null;
	}
	
	public void RemoveOptionByOptionID(Integer DrinkOptionId)
	{
		for(int Index=0;Index<m_UserOptions.size();Index++)
		{
			if(m_UserOptions.get(Index).GetDrinkOptionID().equals(DrinkOptionId))
			{
				m_UserOptions.remove(Index);
				return; // Can only be one. If not, then we need to traverse backwards.
			}
		}
	}
	
	public void RemoveAllOptionsForOptionGroup(Integer OptionGroupID)
	{
		if(m_UserOptions.size()==0)
		{
			return;
		}

		for(int Index=m_UserOptions.size();Index>0;Index--)
		{
			if(m_UserOptions.get(Index-1).GetDrinkOptionGroupID().equals(OptionGroupID))
			{
				m_UserOptions.remove(Index-1);
			}
		}
	}
	
	public void RemoveAllOptionsForAnyOtherOptionInGroup(Integer OptionGroupID,Integer OptionID)
	{
		if(m_UserOptions.size()==0)
		{
			return;
		}

		for(int Index=m_UserOptions.size();Index>0;Index--)
		{
			if(m_UserOptions.get(Index-1).GetDrinkOptionGroupID().equals(OptionGroupID))
			{
				if ((m_UserOptions.get(Index-1).GetDrinkOptionID().equals(OptionID) )==false)
				{
					m_UserOptions.remove(Index-1);
				}
			}
		}
	}
	
	public void AddOptionsListToPost(List<NameValuePair> nameValuePairs ) throws UnsupportedEncodingException
	{
		Integer Index=0;
		for(FreewayCoffeeFoodDrinkUserOption Option : m_UserOptions)
		{
			String OptionString = Option.GetDrinkOptionGroupID() + "*" + Option.GetDrinkOptionID() + "*" + Option.GetDrinkTypesOptionID() + "*" +
							Option.GetDrinkTypesOptionID() + "*" + Option.GetOptionCount();
			String ParamName = FreewayCoffeeApp.S_USER_DRINK_OPTIONS  + Index;
			nameValuePairs.add(new BasicNameValuePair(ParamName,OptionString));
			Index++;
		}
				
	}
	
}
