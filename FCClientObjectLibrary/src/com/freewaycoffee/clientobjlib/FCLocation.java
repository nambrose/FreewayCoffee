package com.freewaycoffee.clientobjlib;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.apache.http.protocol.HTTP;
import org.xml.sax.Attributes;

public class FCLocation
{
	public static final Integer LOCATION_ID_NONE=0;
	
	public static final String LOCATION_UPDATED_TAG="loc_upd";
	public static final String USER_LOCATION_TAG="user_location";
	
	public static final int  DB_LOCATION_OPEN_MODE_FORCE_CLOSED = 0;
	public static final int  DB_LOCATION_OPEN_MODE_FORCE_OPEN = 1;
	public static final int  DB_LOCATION_OPEN_MODE_OBEY_HOURS =2;
	
	public static final String  DB_LOCATION_OPEN_MODE_FORCE_CLOSED_STR = "FORCE CLOSE";
	public static final String  DB_LOCATION_OPEN_MODE_FORCE_OPEN_STR = "FORCE OPEN";
	public static final String  DB_LOCATION_OPEN_MODE_OBEY_HOURS_STR ="OBEY HOURS";
	
	    
	public static final String  USER_LOCATION_PHONE_ATTR="l_p";
	public static final String  USER_LOCATION_EMAIL_ATTR="l_e";
	public static final String  USER_LOCATION_DESCRIPTION_ATTR="l_d";
	public static final String  USER_LOCATION_ADDRESS_ATTR="l_a";
	public static final String  USER_LOCATION_OPEN_MODE_ATTR="l_om";
	public static final String  USER_LOCATION_HOURS_ATTR="l_h";
	public static final String  USER_LOCATION_GPS_LAT_ATTR="lg_lat";
	public static final String  USER_LOCATION_GPS_LONG_ATTR="l_glon";
	public static final String  USER_LOCATION_TZ_ATTR="l_tz";
	public static final String  USER_LOCATION_INSTRUCTIONS_ATTR="l_i";
	public static final String  USER_LOCATION_LONG_DESCR_ATTR="l_lo_d";
	public static final String  USER_LOCATION_MENU_ID_ATTR="l_m_id";
	public static final String  USER_LOCATION_SHOW_PHONE_ATTR="l_sp";
	public static final String  USER_LOCATION_SHOW_EMAIL_ATTR="l_se";
	public static final String  USER_LOCATION_TTTM_ATTR="l_tttm";
	
	
	//public static final String USER_LOCATION_DESCR_ATTR="user_location_description";
	//public static final String USER_LOCATION_GPS_LAT_ATTR="user_location_gps_lat";
	//public static final String USER_LOCATION_GPS_LONG_ATTR="user_location_gps_long";
	//public static final String USER_LOCATION_ADDRESS="user_location_address";
	//public static final String USER_LOCATION_HOURS="user_location_hours";
	
	private Integer m_LocationID;
	private String m_LocationDescription;
	private String m_LocationAddress;
	private String m_LocationHours;
	private String m_LocationGPSLat;
	private String m_LocationGPSLon;
	private String m_LocationEmail;
	private String m_LocationTZ;
	private String m_LocationPhone;
	private Integer m_LocationOpenMode;
	private String m_LocationLongDescr;
	private String m_LocationInstructions;
	private Integer m_LocationMenuID;
	private boolean m_LocationIsActive;
	private Integer m_Incarnation;
	private ArrayList<FCLocationAllowedArrivalMethod> m_AllowedArrivalModes;
	private ArrayList<FCLocationAllowedPayMethod> m_AllowedPaymentMethods;
	private boolean m_ShowPhone;
	private boolean m_ShowEmail;
	private String  m_LocationTalkToTheManagerNumber;

	public FCLocation()
	{
		m_AllowedArrivalModes = new ArrayList<FCLocationAllowedArrivalMethod>();
		m_AllowedPaymentMethods = new ArrayList<FCLocationAllowedPayMethod>();
	}
	
	public String GetLocationOpenModeAsStr()
	{
		switch(m_LocationOpenMode)
		{
		case FCLocation.DB_LOCATION_OPEN_MODE_FORCE_CLOSED:
			return FCLocation.DB_LOCATION_OPEN_MODE_FORCE_CLOSED_STR;
		case FCLocation.DB_LOCATION_OPEN_MODE_FORCE_OPEN:
			return FCLocation.DB_LOCATION_OPEN_MODE_FORCE_OPEN_STR;
		case FCLocation.DB_LOCATION_OPEN_MODE_OBEY_HOURS:
			return FCLocation.DB_LOCATION_OPEN_MODE_OBEY_HOURS_STR;
		default:
			return "Unknown";
						
		}

	}
	public void SetLocationID(Integer LocationID)
	{
		m_LocationID=LocationID;
	}
	
	public Integer GetLocationID()
	{
		return m_LocationID;
	}
	
	public void SetLocationName(String LocationName)
	{
		m_LocationDescription = LocationName;
	}
	
	public String GetLocationName()
	{
		return m_LocationDescription;
	}
	
	public void SetLocationAddress(String Address)
	{
		m_LocationAddress = Address;
	}
	
	public String GetLocationAddress()
	{
		return m_LocationAddress;
	}
	
	public void SetLocationHours(String Hours)
	{
		m_LocationHours = Hours;
	}
	
	public String GetLocationHours()
	{
		return m_LocationHours;
	}
	
	public void SetLocationGPSLat(String Lat)
	{
		m_LocationGPSLat = Lat;
	}
	public String GetLocationGPSLat()
	{
		return m_LocationGPSLat;
	}
	
	public void SetLocationGPSLon(String Lon)
	{
		m_LocationGPSLon = Lon;
	}
	public String GetLocationGPSLon()
	{
		return m_LocationGPSLon;
	}
	
	public void SetLocationEmail(String Email)
	{
		m_LocationEmail = Email;
	}
	public String GetLocationEmail()
	{
		return m_LocationEmail;
	}
	
	public void SetLocationTZ(String TZ)
	{
		m_LocationTZ = TZ;
	}
	public String GetLocationTZ()
	{
		return m_LocationTZ;
	}
	
	public void SetLocationPhone(String Phone)
	{
		m_LocationPhone = Phone;
	}
	public String GetLocationPhone()
	{
		return m_LocationPhone;
	}
	
	public void SetLocationOpenMode(Integer Mode)
	{
		// Check range ...
		m_LocationOpenMode = Mode;
	}
	public Integer GetLocationOpenMode()
	{
		return m_LocationOpenMode;
	}
	
	public void SetLocationLongDescr(String LongDescr)
	{
		m_LocationLongDescr = LongDescr;
	}
	public String GetLocationLongDescr()
	{
		return m_LocationLongDescr;
	}
	
	public void SetLocationInstructions(String Instructions)
	{
		m_LocationInstructions = Instructions;
	}
	public String GetLocationInstructions()
	{
		return m_LocationInstructions;
	}
	
	public void SetLocationMenuID(Integer MenuID)
	{
		m_LocationMenuID = MenuID;
	}
	
	public Integer GetLocationMenuID()
	{
		return m_LocationMenuID;
	}
	
	public void SetLocationIsActive(boolean IsActive)
	{
		m_LocationIsActive = IsActive;
	}
	
	public boolean GetLocationIsActive()
	{
		return m_LocationIsActive;
	}
	
	public void SetLocationIncarnation(Integer Incarnation)
	{
		m_Incarnation = Incarnation;
	}
	
	public Integer GetLocationIncarnation()
	{
		return m_Incarnation;
	}
	
	// Arrival Modes/Methods
	public void ClearAllAllowedArrivalModes()
	{
		m_AllowedArrivalModes.clear();
	}
	
	public void AddAllowedArrivalMode(FCLocationAllowedArrivalMethod Mode)
	{
		m_AllowedArrivalModes.add(Mode);
	}
	
	public boolean IsArrivalMethodAllowed(Integer Method)
	{
		for(int index=0;index<m_AllowedArrivalModes.size();index++)
		{
			FCLocationAllowedArrivalMethod Arrival = m_AllowedArrivalModes.get(index);
			if(Arrival!=null)
			{
				if(Arrival.IsModeEqualTo(Method)==true)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public int GetCountOfArrivalMethods()
	{
		return m_AllowedArrivalModes.size();
	}
	
	public String MakeArrivalMethodStringForAllMethods()
	{
		boolean firstTime=false;
		String Result="";
		
		for(int index=0;index<m_AllowedArrivalModes.size();index++)
		{
			if(firstTime!=true)
			{
				Result += ",";
				
			}
			FCLocationAllowedArrivalMethod Arrival = m_AllowedArrivalModes.get(index);
			if(Arrival!=null)
			{
				Result += Arrival.GetModeName();
			}
			firstTime=false;
		}
		return Result;
		
	}
	
	// Payment Modes/Methods
	public void ClearAllAllowedPaymentMethods()
	{
		m_AllowedPaymentMethods.clear();
	}
	
	public void AddAllowedPaymentMethods(FCLocationAllowedPayMethod Method)
	{
		m_AllowedPaymentMethods.add(Method);
	}
	
	public boolean IsPaymentMethodAllowed(Integer Method)
	{
		for(int index=0;index<m_AllowedPaymentMethods.size();index++)
		{
			FCLocationAllowedPayMethod PayMethod = m_AllowedPaymentMethods.get(index);
			if(PayMethod!=null)
			{
				if(PayMethod.DoesMatchMode(Method)==true)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public int GetCountOfPaymentMethods()
	{
		return m_AllowedPaymentMethods.size();
	}
	
	public String MakePaymentMethodStringForAllMethods()
	{
		boolean firstTime=false;
		String Result="";
		
		for(int index=0;index<m_AllowedPaymentMethods.size();index++)
		{
			if(firstTime!=true)
			{
				Result += ",";
				
			}
			FCLocationAllowedPayMethod Arrival = m_AllowedPaymentMethods.get(index);
			if(Arrival!=null)
			{
				Result += Arrival.GetPaymentMethodText();
			}
			firstTime=false;
		}
		return Result;
		
	}
	
	
	public void SetShowPhone(boolean ShowPhone)
	{
		m_ShowPhone = ShowPhone;
	}
	
	public boolean GetShowPhone()
	{
		return m_ShowPhone;
	}
	
	public void SetShowEmail(boolean ShowEmail)
	{
		m_ShowEmail = ShowEmail;
	}
	
	public boolean GetShowEmail()
	{
		return m_ShowEmail;
	}
	
	public void SetTalkToTheManagerNumber(String TTTM)
	{
		m_LocationTalkToTheManagerNumber = TTTM;
	}
	
	public String GetTalkToTheManagerNumber()
	{
		return m_LocationTalkToTheManagerNumber;
	}
	
	
	public static FCLocation ParseFromXMLAttributes(Attributes atts)
	{
		FCLocation Location = new FCLocation();
			
		String ParsedAttr;
		
		// ID
		ParsedAttr = atts.getValue(FCXMLHelper.ID_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			return null;
		}
		
		try
		{
			ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			if((ParsedAttr!=null) && (ParsedAttr.length()>0))
			{
				Location.SetLocationID(Integer.parseInt(ParsedAttr));
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
		
		// Phone
		ParsedAttr = atts.getValue(FCLocation.USER_LOCATION_PHONE_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			
		}
		else
		{
			try
			{
				ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			}
			catch(UnsupportedEncodingException enc_ex)
			{
				return null;
			}
			Location.SetLocationPhone(ParsedAttr);
		}
		
		
		
		// Email
		ParsedAttr = atts.getValue(FCLocation.USER_LOCATION_EMAIL_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			
		}
		else
		{
			try
			{
				ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			}
			catch(UnsupportedEncodingException enc_ex)
			{
				return null;
			}
			Location.SetLocationEmail(ParsedAttr);
		}
		
		// Description/Name
		ParsedAttr = atts.getValue(FCLocation.USER_LOCATION_DESCRIPTION_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
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
		Location.SetLocationName(ParsedAttr);
		
		// Address
		ParsedAttr = atts.getValue(FCLocation.USER_LOCATION_ADDRESS_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
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
		Location.SetLocationAddress(ParsedAttr);
		
		
		// Open Mode
		ParsedAttr = atts.getValue(FCLocation.USER_LOCATION_OPEN_MODE_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			return null;
		}
		
		try
		{
			ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			if((ParsedAttr!=null) && (ParsedAttr.length()>0))
			{
				Location.SetLocationOpenMode(Integer.parseInt(ParsedAttr));
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
		// HOURS
		ParsedAttr = atts.getValue(FCLocation.USER_LOCATION_HOURS_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
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
		Location.SetLocationHours(ParsedAttr);
		
		// GPS Lat
		ParsedAttr = atts.getValue(FCLocation.USER_LOCATION_GPS_LAT_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
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
		Location.SetLocationGPSLat(ParsedAttr);
		
		// GPS Lon
		ParsedAttr = atts.getValue(FCLocation.USER_LOCATION_GPS_LONG_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
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
		Location.SetLocationGPSLon(ParsedAttr);
		
		// TZ
		ParsedAttr = atts.getValue(FCLocation.USER_LOCATION_TZ_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
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
		Location.SetLocationTZ(ParsedAttr);
		
		// Instructions
		ParsedAttr = atts.getValue(FCLocation.USER_LOCATION_INSTRUCTIONS_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			
		}
		else
		{
			try
			{
				ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			}
			catch(UnsupportedEncodingException enc_ex)
			{
				return null;
			}
			Location.SetLocationInstructions(ParsedAttr);
		}
		
		
		// Long Descr
		
		ParsedAttr = atts.getValue(FCLocation.USER_LOCATION_LONG_DESCR_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			Location.SetLocationLongDescr("");
		}
		else
		{
			try
			{
				ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			}
			catch(UnsupportedEncodingException enc_ex)
			{
				return null;
			}
			Location.SetLocationLongDescr(ParsedAttr);
		}
		// MENU ID
		
		ParsedAttr = atts.getValue(FCLocation.USER_LOCATION_MENU_ID_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			return null;
		}
		
		try
		{
			ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			if((ParsedAttr!=null) && (ParsedAttr.length()>0))
			{
				Location.SetLocationMenuID(Integer.parseInt(ParsedAttr));
			}
		}
		catch(UnsupportedEncodingException enc_ex)
		{
			return null;
		}
		catch(NumberFormatException e)
		{
			return null;
			
		}
		
		// Show Phone
		ParsedAttr = atts.getValue(FCLocation.USER_LOCATION_SHOW_PHONE_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			Location.SetShowPhone(true);
		}
		else
		{
			try
			{
				ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			}
			catch(UnsupportedEncodingException enc_ex)
			{
				return null;
			}
			Location.SetShowPhone(FCXMLHelper.parseStringValueAsBoolean(ParsedAttr));
		}
		
		// Show Email
		ParsedAttr = atts.getValue(FCLocation.USER_LOCATION_SHOW_EMAIL_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			Location.SetShowEmail(true);
		}
		else
		{
			try
			{
				ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			}
			catch(UnsupportedEncodingException enc_ex)
			{
				return null;
			}
			Location.SetShowEmail(FCXMLHelper.parseStringValueAsBoolean(ParsedAttr));
		}
		
		// Talk To The Manager
		ParsedAttr = atts.getValue(FCLocation.USER_LOCATION_TTTM_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			
		}
		else
		{
			try
			{
				ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			}
			catch(UnsupportedEncodingException enc_ex)
			{
				return null;
			}
			Location.SetTalkToTheManagerNumber(ParsedAttr);
		}
		// Incarnation
		ParsedAttr = atts.getValue(FCXMLHelper.INCARNATION_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			return null;
		}
		
		try
		{
		
			ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			if((ParsedAttr!=null) && (ParsedAttr.length()>0))
			{
				Location.SetLocationIncarnation(Integer.parseInt(ParsedAttr));
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
		
		return Location;
	}	
	
	public static boolean IsLocationIDNone(Integer LocationID)
	{
		return LocationID.equals(LOCATION_ID_NONE);
	}
	public boolean IsLocationNone()
	{
		return FCLocation.IsLocationIDNone(m_LocationID);
		
	}
/*
	-
	-(BOOL) isLongDescrPopulated;
	- (NSString*) makeDetailText;
	- (NSString*) makeLocationMapsURL;
	
*/

}
