package com.freewaycoffee.clientobjlib;

public class FCAppSetting 
{
	private String m_SettingName;
	private String m_SettingValue;
	
	public void SetSettingName(String SettingName)
	{
		m_SettingName = SettingName;
	}
	
	public void SetSettingValue(String SettingValue)
	{
		m_SettingValue = SettingValue;
	}
	
	public String GetSettingName()
	{
		return m_SettingName;
	}
	
	public String GetSettingValue()
	{
		return m_SettingValue;
	}
	
	public boolean tryGetValueAsBoolean()
	{
		return FCXMLHelper.parseStringValueAsBoolean(m_SettingValue);
		
	}
}
