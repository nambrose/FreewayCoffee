package com.freewaycoffee.clientobjlib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import java.text.DateFormat;

import android.text.Html;
import android.text.Spannable;


public class FCOrder 
{
	public static final int ORDER_RECEIVED=1;
	public static final int ORDER_INPROGRESS=2;
	public static final int ORDER_DELIVERED=3;
	public static final int ORDER_REFUNDED=4;
	public static final int ORDER_NOSHOW=5;
	    
	public static final int ORDER_PAY_METHOD_UNKNOWN=0;
	public static final int ORDER_PAY_METHOD_IN_APP=1;
	public static final int ORDER_PAY_METHOD_IN_STORE=2;
	
	public static final String ORDER_PAY_METHOD_IN_APP_STR = "PAID: In App";
	public static final String ORDER_PAY_METHOD_IN_STORE_STR = "NOT PAID: Pay In Store";
	public static final String ORDER_PAY_METHOD_UNKNOWN_STR = "Unknown (Call Support)";
	
	private Integer m_OrderID;
	private Date m_TimeReceivedAsDate;
	private String m_TimeReceived;
	private String m_TimeReceivedDisplayTime;
	private String m_TimeNeeded;
	private String m_TimeNeededDisplayTime; // Just the Time, excluding date for display 
	private String m_TimeToLocation; // How long the user said they would take to get there
	private String m_TimeOrderEnd; // Time we went into a final state (Refund, DELIVERED, NOSHOW)
	private String m_TimeOrderEndDisplayTime;
	
	private String m_UserName;
	private String m_UserEmail;
	private String m_TotalCost;
	private String m_Disposition;
	private Integer m_DispositionInt;
	private Integer m_LocationID;
	private String m_TimeUserHere;
	private String m_TimeUserHereDisplayTime;
	private Date m_TimeHereAsDate;
	private Date m_TimeNeededAsDate;
	
	private String m_UserCarMakeModelInfo;
	private String m_UserTag;
	
	private Integer m_ArriveMode; // 0 - In Car, 1 - Walkup
	private Integer m_UserID;
	
	// To be added
	private String m_CreditCardType;
	private String m_CreditCardLast4;
	// Need credit card Auth data too ?
	private Integer m_RecordIncarnation; // Eventually used to detect if someone else made an edit in the meantime
	private FCOrderItemList m_OrderItems;
	private boolean m_IsDemoOrder;
	
	private String m_OrderItemsTotal; // Actual cost before any discount and not inc. tip or tax etc.
	private String m_OrderDiscount; // Discount (TBD if this is Location provided or FC -- gross)
	private String m_OrderTip; // Tip (if any)
	private int m_PayMethod; // InApp or InStore(NEED TO PAY AT LOCATION)
	private Long m_GlobalOrderTag;// Just for completeness. Not totally useful here
	
	
	
	
	public static Comparator<FCOrder> ComparatorTimeReceived=
	new Comparator<FCOrder>()
		{
		//@Override
			public int compare(FCOrder object1, FCOrder object2) 
			{
				return (object1.m_TimeReceivedAsDate.compareTo(object2.m_TimeReceivedAsDate));
			}
		};
	
	public final static Comparator<FCOrder> ComparatorTimeHere=
			new Comparator<FCOrder>()
			{
				//@Override
				public int compare(FCOrder object1, FCOrder object2) 
				{
					if( (object1.IsCustomerHere() ==true) && (object2.IsCustomerHere()==true) )
					{
						return (object1.m_TimeHereAsDate.compareTo(object2.m_TimeHereAsDate));
					}
					if( (object1.IsCustomerHere()==true) && (object2.IsCustomerHere()!=true) )
					{
						return -1;
					}
					if( (object1.IsCustomerHere()!=true) && (object2.IsCustomerHere()==true) )
					{
						return 1;
					}
					return 0; // Both not here 
				}
			};

	
	public FCOrder()
	{
		Initialize();
	}
	
	private void Initialize()
	{
		m_OrderID=-1;
		m_TimeReceived="";
		m_TimeReceivedDisplayTime="";
		m_TimeNeeded="";
		m_UserName="";
		m_UserEmail="";
		m_TotalCost="";
		m_Disposition="";
		m_UserID=-1;
		m_TimeUserHere="";
		m_TimeToLocation="";
		m_DispositionInt=-1;
		m_CreditCardType="";
		m_CreditCardLast4="";
		m_RecordIncarnation=-1;
		m_TimeOrderEnd="";
		m_OrderItems = new FCOrderItemList();
		m_ArriveMode=-1;
		m_TimeOrderEndDisplayTime="";
		m_TimeUserHereDisplayTime="";
		m_TimeNeededDisplayTime="";
		m_TimeHereAsDate=null;
		m_TimeReceivedAsDate=null;
		m_TimeNeededAsDate=null;
		m_UserCarMakeModelInfo="";
		m_UserTag="";
		m_OrderItemsTotal="";
		m_OrderDiscount=""; 
		m_OrderTip=""; // Tip (if any)
		m_PayMethod=ORDER_PAY_METHOD_UNKNOWN; 
		m_GlobalOrderTag=Long.valueOf(0);
		
		
	}

	public void SetDemoOrder(boolean DemoFlag)
	{
		m_IsDemoOrder = DemoFlag;
	}
	
	public boolean IsDemoOrder()
	{
		if(m_IsDemoOrder==true)
		{
			return true;
		}
		return false;
	}
	public void SetOrderID(Integer ID)
	{
		m_OrderID=ID;
	}
	
	public Integer GetOrderID()
	{
		return m_OrderID;
	}
	
	public void SetTimeReceived(String TimeReceived)
	{
		m_TimeReceived=TimeReceived;
		if( (TimeReceived==null) || (TimeReceived.length()==0))	
		{
			m_TimeReceivedDisplayTime="";
			m_TimeReceivedAsDate=null;
			return;
			
		}
		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US); 
		try {
			m_TimeReceivedAsDate = curFormater.parse(m_TimeReceived);
			DateFormat time = DateFormat.getTimeInstance(DateFormat.SHORT);
			m_TimeReceivedDisplayTime=time.format(m_TimeReceivedAsDate);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String GetTimeReceivedDisplay()
	{
		return m_TimeReceivedDisplayTime;
	}
	public String GetTimeReceived()
	{
		return m_TimeReceived;
	}
	
	public boolean IsOrderForToday()
	{
		// If time received OR needed is today, we keep it around for now
		
		Calendar c = Calendar.getInstance();

		// set the calendar to start of today
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		// and get that as a Date
		Date today = c.getTime();
		if( m_TimeReceivedAsDate.before(today) && (m_TimeNeededAsDate.before(today)))
		{
			return false;
		}
		
		return true;
		
	}
	public void SetTimeNeeded(String TimeNeeded)
	{
		m_TimeNeeded=TimeNeeded;
		
		if( (m_TimeNeeded==null) || (m_TimeNeeded.length()==0))	
		{
			m_TimeNeededDisplayTime="";
			m_TimeNeededAsDate=null;
			return;
			
		}
		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US); 
		try {
			m_TimeNeededAsDate = curFormater.parse(m_TimeNeeded);
			DateFormat time = DateFormat.getTimeInstance(DateFormat.SHORT);
			m_TimeNeededDisplayTime=time.format(m_TimeNeededAsDate);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	boolean IsOrderLate()
	{
		Date Now = new Date();
		
		if(m_TimeNeededAsDate.before(Now)==true)
		{
			return true;
		}
		return false;
	}
	public String GetTimeNeededJustTime()
	{
		return m_TimeNeededDisplayTime;
	}
	public String GetTimeNeeded()
	{
		return m_TimeNeeded;
	}
	
	public void SetUserName(String UserName)
	{
		m_UserName=UserName;
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
	
	
	public Integer GetItemCount()
	{
		return m_OrderItems.Size();
	}
	
	public void SetTotalCost(String TotalCost)
	{
		m_TotalCost=TotalCost;
	}
	
	public String GetTotalCost()
	{
		return m_TotalCost;
	}
	
	public String GetTotalCostForDisplay()
	{
		return "$" + m_TotalCost;
	}
	
	public void SetDisposition(String Disposition)
	{
		m_Disposition=Disposition;
	}
	
	public String GetDisposition()
	{
		return m_Disposition;
	}
	
	public void SetUserID(Integer UserID)
	{
		m_UserID=UserID;
	}
	
	public Integer GetUserID()
	{
		return m_UserID;
	}
	
	public String GetArriveMode()
	{
		// Note: Fix this !
		if(m_ArriveMode==FCXMLHelper.ARRIVE_MODE_WALKUP_INT)
		{
			return ("Walkup");
		}
		else
		{
			return ("In-Car");
		}
	}
	
	public String GetDisplayName()
	{
		return m_UserName + "(" + m_UserEmail + ")";
	}
	
	// NA this is a bit sloppy. Should I just export add/remove ? Going to need some Locking here for sure !
	public FCOrderItemList GetOrderItems()
	{
		return m_OrderItems;
	}
	
	public void SetTimeToLocation(String TimeToLocation)
	{
		m_TimeToLocation = TimeToLocation;
	}
	
	public String GetTimeToLocation()
	{
		return m_TimeToLocation;
	}
	
	public void SetDispositionIntAndString(Integer Disposition)
	{
		SetDispositionInt(Disposition);
		SetDisposition(ConvertDispositionIntToText((int)Disposition));
	}
	
	public String ConvertDispositionIntToText(Integer Disposition)
	{
		switch(Disposition)
		{
		case ORDER_RECEIVED:
				return "Received";
		case ORDER_INPROGRESS:
				return "In Progress";
		case ORDER_DELIVERED:
				return "Delivered";
		case ORDER_REFUNDED:
				return "Refunded";
		case ORDER_NOSHOW:
				return "No Show";
		default:
				return "Unknown";
		}
	}
	
	public void SetDispositionInt(Integer Disposition)
	{
		m_DispositionInt = Disposition;
	}
	
	public Integer GetDispositionInt()
	{
		return m_DispositionInt;
	}
	
	public void SetLocationID(Integer Location)
	{
		m_LocationID = Location;
	}
	
	public Integer GetLocation()
	{
		return m_LocationID;
	}
	
	public void SetTimeUserHere(String TimeUserHere)
	{
		m_TimeUserHere = TimeUserHere;
		if( (TimeUserHere==null) || (TimeUserHere.length()==0))	
		{
			m_TimeUserHereDisplayTime="";
			m_TimeHereAsDate=null;
			return;
			
		}
		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US); 
		try {
			m_TimeHereAsDate = curFormater.parse(m_TimeUserHere);
			DateFormat time = DateFormat.getTimeInstance(DateFormat.SHORT);
			m_TimeUserHereDisplayTime=time.format(m_TimeHereAsDate);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	
	public String GetTimeUserHereDisplay()
	{
		return m_TimeUserHereDisplayTime;
	}
	
	public String GetTimeUserHere()
	{
		return m_TimeUserHere;
	}
	
	public void SetCreditCardType(String CreditCardType)
	{
		m_CreditCardType = CreditCardType;
	}
	
	public String GetCreditCardType()
	{
		return m_CreditCardType;
	}
	
	public void SetCreditCardLast4(String CreditCardLast4)
	{
		m_CreditCardLast4=CreditCardLast4;
	}
	
	public String GetCreditCardLast4()
	{
		return m_CreditCardLast4;
	}
	
	public void SetIncarnation(Integer Incarnation)
	{
		m_RecordIncarnation=Incarnation;
	}
	
	public Integer GetIncarnation()
	{
		return m_RecordIncarnation;
	}
	
	public String GetOrderItemsTotal()
	{
		return m_OrderItemsTotal;
	}
	
	public void SetOrderItemsTotal(String OrderItemsTotal)
	{
		m_OrderItemsTotal = OrderItemsTotal;
	}
	
	public String GetOrderDiscount()
	{
		return m_OrderDiscount;
	}
	
	public void SetOrderDiscount(String Discount)
	{
		m_OrderDiscount = Discount;
	}
	
	public String GetOrderTip()
	{
		return m_OrderTip;
	}
	
	public void SetOrderTip(String TipAmount)
	{
		m_OrderTip = TipAmount;
	}
	
	public String GetOrderPayMethodAsString()
	{
		switch (m_PayMethod)
		{
		case ORDER_PAY_METHOD_IN_APP:
			return ORDER_PAY_METHOD_IN_APP_STR;
		case ORDER_PAY_METHOD_IN_STORE:
			return ORDER_PAY_METHOD_IN_STORE_STR;
		case ORDER_PAY_METHOD_UNKNOWN:
		default:
			return ORDER_PAY_METHOD_UNKNOWN_STR;
		}
	}
	
	public String GetOrderPayMethodAsStringForDisplay()
	{
		if(GetOrderPayMethod()==ORDER_PAY_METHOD_IN_APP)
		{
			return GetOrderPayMethodAsString();
		}
		
		else
		{
			return "<b>" + GetOrderPayMethodAsString() + "</b>";
		}
	}
	public int GetOrderPayMethod()
	{
		return m_PayMethod;
	}
	
	public void SetOrderPayMethod(int Method)
	{
		if( (Method == ORDER_PAY_METHOD_IN_APP ) || (Method==ORDER_PAY_METHOD_IN_STORE))
		{
			m_PayMethod = Method;
		}
	}
	
	public Long GetOrderGlobalOrderTag()
	{
		return m_GlobalOrderTag;
	}
	
	public void SetOrderGlobalOrderTag(Long Tag)
	{
		m_GlobalOrderTag = Tag;
	}
	
	public void SetTimeOrderEnded(String OrderEnded)
	{
		m_TimeOrderEnd = OrderEnded;
		if( (OrderEnded==null) || (OrderEnded.length()==0))	
		{
			m_TimeOrderEndDisplayTime="";
			return;
			
		}
		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US); 
		try {
			Date dateObj = curFormater.parse(m_TimeOrderEnd);
			DateFormat time = DateFormat.getTimeInstance(DateFormat.SHORT);
			m_TimeOrderEndDisplayTime=time.format(dateObj);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String GetTimeOrderEndedDisplay()
	{
		return m_TimeOrderEndDisplayTime;
	}
	
	public String GetTimeOrderEnded()
	{
		return m_TimeOrderEnd;
	}
	
	public String GetCreditCardDisplay()
	{
		return m_CreditCardType + "(" + m_CreditCardLast4 + ")";
	}
	
	public void AddOrderItem(FCOrderItem Item)
	{
		m_OrderItems.AddOrderItem(Item);
	}
	
	public boolean IsCustomerHere()
	{
		if( (m_TimeUserHere!=null) && (m_TimeUserHere.length()>0)) 
		{
			return true;
		}
		return false;
			
	}
	
	public boolean IsOrderCompleted()
	{
		if( (m_DispositionInt.equals(ORDER_RECEIVED)) || (m_DispositionInt.equals(ORDER_INPROGRESS)) )
		{
			return false;
		}
		return true;
	}
	
	
	public void SetUserCarMakeModel(String CarMakeModel)
	{
		m_UserCarMakeModelInfo=CarMakeModel;
	}
	
	public String GetUserCarMakeModel()
	{
		return m_UserCarMakeModelInfo;
	}
	
	public void SetUserTag(String UserTag)
	{
		m_UserTag = UserTag;
	}
	
	public void SetArriveMode(Integer Mode)
	{
		m_ArriveMode = Mode;
	}
	
	public String GetUserTag()
	{
		return m_UserTag;
	}
	
	public String GetUserCarInfoAndTagForDisplay()
	{
		return GetUserCarMakeModel() + " | " + GetUserTag();
	}
	
	public String GetTipForDisplay()
	{
		if(m_OrderTip==null)
		{
			return "";
		}
		if(m_OrderTip.equals(""))
		{
			return "";
		}
		if(m_OrderTip.equals("0.00"))
		{
			return "";
		}
	
		return " (Tip: $" + m_OrderTip + ")";
	}
	
	public Spannable GetNameAndCarDetails()
	{
		String Result = GetOrderPayMethodAsStringForDisplay() + " ";
		
		Result += "Name: " + GetUserName() + " ";
		
		Result += " (Arrive: "+ GetArriveMode() + " ) ";
		if(IsDemoOrder())
		{
			Result+= " (TEST ORDER) ";
		}
		
		Result += GetTipForDisplay();
		
		//Result += " (" + GetUserEmail() + ")" + "<br>" + GetUserCarInfoAndTagForDisplay();
				
		Result += "<br>" + "Car: " + GetUserCarInfoAndTagForDisplay();
				
		return (Spannable)Html.fromHtml(Result);
	}
}





