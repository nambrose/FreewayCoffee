package com.freewaycoffee.clientobjlib;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.http.protocol.HTTP;
import org.xml.sax.Attributes;


public class FCUserData 
{
	public static final String USER_DEMO_VALUE_DEMO ="1";
	public static final String USER_DEMO_VALUE_NON_DEMO ="0";
	
	public static final String USER_INFO_TAG ="user_info";

	public static final String USER_INFO_USER_NAME ="user_name";
	public static final String USER_INFO_USER_EMAIL ="user_email";
	public static final String USER_INFO_USER_TAG ="user_tag";
	public static final String USER_INFO_USER_FREE_DRINKS ="user_free_drinks";
	public static final String USER_INFO_USER_IS_DEMO_ATTR ="user_is_demo";
	public static final String USER_INFO_USER_IS_LOCKED_ATTR ="user_locked";
	public static final String USER_INFO_USER_TYPE_ATTR ="user_type";
	public static final String USER_INFO_TIME_TO_LOCATION_ATTR ="user_time_to_location";
	//public static final String USER_INFO_TZ_ATTR ="user_tz";
	public static final String USER_INFO_ARRIVE_MODE_ATTR ="user_arrive_mode";
	public static final String USER_INFO_LOCATION_ID ="user_location_id";
	public static final String USER_INFO_PAY_METHOD_ATTR ="user_pay_method";

	public static final Integer USER_TYPE_NORMAL=0;
	public static final Integer USER_TYPE_ADMIN=1;
	public static final Integer USER_TYPE_SUPER=2;

	
	private Integer m_UserID;
	private String m_UserName;
	private String m_UserEmail;
	private String m_UserTag;
	private Integer m_UserFreeDrinksCount;
	private Integer m_UserTimeToLocation;
	private boolean m_UserIsDemo;
	private boolean m_UserIsLocked;
	private Integer m_UserType; // Admin, Super etc -- see enum
	private Integer m_UserArriveMode; // Walkup, Car etc.
	private Integer m_UserLocationID; // Current Location
	//private String m_UserTZ;
	private Integer m_UserPayMethod; // InStore,InApp etc
	private Integer m_UserIncarnation;
	
	
	public FCUserData()
	{
		
	}
	
	public void SetUserID(Integer UserID)
	{
		m_UserID=UserID;
	}
	
	public Integer GetUserID()
	{
		return m_UserID;
	}
	
	public void SetUserName(String UserName)
	{
		m_UserName = UserName;
	}
	
	public String GetUserName()
	{
		return m_UserName;
	}
	
	public void SetUserEmail(String UserEmail)
	{
		m_UserEmail=UserEmail;
	}
	
	public String GetUserEmail()
	{
		return m_UserEmail;
	}
	
	
	public void SetUserTag(String UserTag)
	{
		m_UserTag = UserTag;
	}
	
	public String GetUserTag()
	{
		return m_UserTag;
	}
	
	
	public void SetUserFreeDrinksCount(Integer FreeCount)
	{
		m_UserFreeDrinksCount=FreeCount;
	}
	
	public Integer GetUserFreeDrinksCount()
	{
		return m_UserFreeDrinksCount;
	}
	
	public void SetUserTimeToLocation(Integer Time)
	{
		m_UserTimeToLocation = Time;
	}
	
	public Integer GetUserTimeToLocation()
	{
		return m_UserTimeToLocation;
	}
	
	public void SetUserIsDemo(boolean IsDemo)
	{
		m_UserIsDemo = IsDemo;
	}
	
	public boolean GetUserIsDemo()
	{
		return m_UserIsDemo;
	}
	
	public void SetUserIsLocked(boolean IsLocked)
	{
		m_UserIsLocked = IsLocked;
	}
	
	public boolean GetUserIsLocked()
	{
		return m_UserIsLocked;
	}
	
	public void SetUserType(Integer UserType)
	{
		if( (UserType.equals(USER_TYPE_NORMAL)) ||
			(UserType.equals(USER_TYPE_ADMIN)) ||
			(UserType.equals(USER_TYPE_SUPER)) )
		{
			m_UserType = UserType;
		}
	}
	
	public Integer GetUserType()
	{
		return m_UserType;
	}
	
	public void SetUserArriveMode(Integer Mode)
	{
		if( (Mode.equals(FCXMLHelper.ARRIVE_MODE_CAR_INT)) ||
			(Mode.equals(FCXMLHelper.ARRIVE_MODE_WALKUP_INT)))
		{
			m_UserArriveMode = Mode;
		}
	}
	
	public Integer GetUserArriveMode()
	{
		return m_UserArriveMode;
	}
	
	public void SetUserLocationID(Integer Location)
	{
		m_UserLocationID = Location;
	}
	
	public Integer GetUserLocationID()
	{
		return m_UserLocationID;
	}
	
	/*
	public void SetUserTZ(String TZName)
	{
		m_UserTZ = TZName;
	}
	
	public String GetUserTZ()
	{
		return m_UserTZ;
	}
	*/
	
	public void SetUserPayMethod(Integer PayMethod)
	{
		m_UserPayMethod = PayMethod;
	}
	
	public Integer GetUserPayMethod()
	{
		return m_UserPayMethod;
	}
	
	public void SetUserIncarnation(Integer Incarnation)
	{
		m_UserIncarnation = Incarnation;
	}
	
	public Integer GetUserIncarnation()
	{
		return m_UserIncarnation;
	}
	
	
	public static FCUserData ParseFromXMLAttributes(Attributes atts)
	{
		FCUserData User = new FCUserData();
			
		
		String ParsedAttr;
		
		// DRINK TYPE
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
				User.SetUserID(Integer.parseInt(ParsedAttr));
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
		
		// User Name
		ParsedAttr = atts.getValue(FCUserData.USER_INFO_USER_NAME);
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
		
		User.SetUserName(ParsedAttr);
		
		// Email
		ParsedAttr = atts.getValue(FCUserData.USER_INFO_USER_EMAIL);
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
			
			
		User.SetUserEmail(ParsedAttr);
		
		// Tag (License Plate)
		ParsedAttr = atts.getValue(FCUserData.USER_INFO_USER_TAG);
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
		
			User.SetUserTag(ParsedAttr);
		}
		
		// Free Drinks
		ParsedAttr = atts.getValue(FCUserData.USER_INFO_USER_FREE_DRINKS);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			return null;
		}
			
		try
		{
			ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			if((ParsedAttr!=null) && (ParsedAttr.length()>0))
			{
				User.SetUserFreeDrinksCount(Integer.parseInt(ParsedAttr));
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
		
		// Time To Location
		ParsedAttr = atts.getValue(FCUserData.USER_INFO_TIME_TO_LOCATION_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			return null;
		}
		
		try
		{
			ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			if((ParsedAttr!=null) && (ParsedAttr.length()>0))
			{
				User.SetUserTimeToLocation(Integer.parseInt(ParsedAttr));
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
		
		// Is Demo
		ParsedAttr = atts.getValue(FCUserData.USER_INFO_USER_IS_DEMO_ATTR);
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
		User.SetUserIsDemo(FCXMLHelper.parseStringValueAsBoolean(ParsedAttr));
		
		// Is Locked
		ParsedAttr = atts.getValue(FCUserData.USER_INFO_USER_IS_LOCKED_ATTR);
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
		User.SetUserIsLocked(FCXMLHelper.parseStringValueAsBoolean(ParsedAttr));
		
		// User Type
		ParsedAttr = atts.getValue(FCUserData.USER_INFO_USER_TYPE_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			return null;
		}
		
		try
		{
			ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			if((ParsedAttr!=null) && (ParsedAttr.length()>0))
			{
				User.SetUserType(Integer.parseInt(ParsedAttr));
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
		
		
		// User TZ
		/*
		ParsedAttr = atts.getValue(FCUserData.USER_INFO_TZ_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			return null;
		}
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		User.SetUserTZ(ParsedAttr);
		
		*/
		
		// Arrive Mode
		ParsedAttr = atts.getValue(FCUserData.USER_INFO_ARRIVE_MODE_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			return null;
		}
		
		try
		{
			ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			if((ParsedAttr!=null) && (ParsedAttr.length()>0))
			{
				User.SetUserArriveMode(Integer.parseInt(ParsedAttr));
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
		
		// LocationID
		ParsedAttr = atts.getValue(FCUserData.USER_INFO_LOCATION_ID);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			
		}
		else
		{
		
			try
			{
				ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
				if((ParsedAttr!=null) && (ParsedAttr.length()>0))
				{
					User.SetUserLocationID(Integer.parseInt(ParsedAttr));
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
		}
		
		// Pay Method
		
		ParsedAttr = atts.getValue(FCUserData.USER_INFO_PAY_METHOD_ATTR);
		if( (ParsedAttr==null) || (ParsedAttr.length()==0))
		{
			return null;
		}
		
		try
		{
			ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			if((ParsedAttr!=null) && (ParsedAttr.length()>0))
			{
				User.SetUserPayMethod(Integer.parseInt(ParsedAttr));
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
		
		
		return User;
	}
		
}
