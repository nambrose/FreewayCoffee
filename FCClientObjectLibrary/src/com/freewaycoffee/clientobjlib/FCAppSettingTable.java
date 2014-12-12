package com.freewaycoffee.clientobjlib;

import java.util.HashMap;
import java.util.Map;

public class FCAppSettingTable 
{
	private Map<String,FCAppSetting> m_AppSettings;
	
	public FCAppSettingTable()
	{
		m_AppSettings = new HashMap<String,FCAppSetting>();
	}
	
	public void Clear()
	{
		m_AppSettings.clear();
	}
	
	public void addAppSetting (FCAppSetting Setting)
	{
		m_AppSettings.put(Setting.GetSettingName(), Setting);
	}
	
	public FCAppSetting GetSettingByName(String SettingName)
	{
		return m_AppSettings.get(SettingName);
	}
	boolean tryGetSettingValueAsBOOL(String SettingName)
	{
		FCAppSetting Setting = GetSettingByName(SettingName);
		if(Setting==null)
		{
			return false;
		}
		return Setting.tryGetValueAsBoolean();
	}
	
	public String tryGetSettingValueAsString(String SettingName)
	{
		FCAppSetting Setting = GetSettingByName(SettingName);
		if(Setting!=null)
		{
			return Setting.GetSettingValue();
		}
		return null;
	}

}
