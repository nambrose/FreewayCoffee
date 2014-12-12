package com.freewaycoffee.clientobjlib;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.http.protocol.HTTP;
import org.xml.sax.Attributes;

public class FCLocationAllowedArrivalMethod
{
	// XML
	public static final String LOCATION_ARRIVE_MODE_TAG= "l_a_m";
	public static final String LOCATION_ARRIVE_MODE_ATTR ="l_a_m_am";
	public static final String LOCATION_ARRIVE_MODE_STRING_ATTR ="l_a_m_sam";
	
	// END XML


	// See XML Helper for constants ARRIVE_MODE_CAR_INT etc
	public static final String ARRIVAL_MODE_NOT_ALLOWED_STRING ="Not Allowed At Location";

	

	private Integer m_ModeAllowed;
	private String m_ModeName;

	
	public static FCLocationAllowedArrivalMethod ParseFromXMLAttributes(Attributes atts)
	{
		FCLocationAllowedArrivalMethod Method = new FCLocationAllowedArrivalMethod();
		
		String ParsedAttr;
		
		// ID
		ParsedAttr = atts.getValue(FCLocationAllowedArrivalMethod.LOCATION_ARRIVE_MODE_ATTR);
		if(( ParsedAttr==null) || (ParsedAttr.length()==0) )
		{
			return null;
		}
	
		
		try
		{
			ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			if((ParsedAttr!=null) && (ParsedAttr.length()>0))
			{
				Method.SetMode(Integer.parseInt(ParsedAttr));
			}
		}
		catch(UnsupportedEncodingException enc_ex)
		{
			return null;
		}
		catch(NumberFormatException e)
		{
			// TODO Log
			return null;
		}
		ParsedAttr = atts.getValue(FCLocationAllowedArrivalMethod.LOCATION_ARRIVE_MODE_STRING_ATTR);
		if(( ParsedAttr==null) || (ParsedAttr.length()==0) )
		{
			return null;
		}
		try
		{
			ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
		}
		catch(UnsupportedEncodingException enc_ex)
		{
			return null;
		}
		Method.SetModeName(ParsedAttr);
		
		return Method;
	}

	public boolean IsModeEqualTo(Integer Mode)
	{
		return m_ModeAllowed.equals(Mode);
	}
	
	public void SetMode(Integer Mode)
	{
		if(( Mode.equals(FCXMLHelper.ARRIVE_MODE_CAR_INT)) || (Mode.equals(FCXMLHelper.ARRIVE_MODE_WALKUP_INT)) )
		{
			m_ModeAllowed = Mode;
		}
	}
	
	public Integer GetMode()
	{
		return m_ModeAllowed;
	}
	
	public void SetModeName(String Name)
	{
		m_ModeName = Name;
	}
	
	public String GetModeName()
	{
		return m_ModeName;
	}

}
