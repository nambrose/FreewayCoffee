package com.freewaycoffee.clientobjlib;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.http.protocol.HTTP;
import org.xml.sax.Attributes;

public class FCLocationAllowedPayMethod 
{
	static final public Integer LOCATION_PAY_METHOD_UNKNOWN =0;

	static final public Integer  LOCATION_PAY_METHOD_IN_APP =1;
	static final public Integer  LOCATION_PAY_METHOD_IN_STORE =2;

	// TODO HORRID FIXME NOW THIS NEEDS TO COME FROM SERVER OR SOMEWHERE ELSE AT LEAST
	static final public String LOCATION_PAY_IN_STORE_STRING ="Pay at Location";
	static final public String LOCATION_PAY_IN_APP_STRING ="In-App";
	// XML

	static final public String LOCATION_PAY_METHOD_TAG ="l_p_m";
	static final public String LOCATION_PAY_METHOD_ATTR ="l_p_m_pm";
	static final public String  LOCATION_PAY_METHOD_DESCR_ATTR ="l_p_m_pmd";
	// END XML

	private Integer m_PaymentMethod;
	private String m_PaymentMethodText;
	
	public void SetPaymentMethod(Integer Method)
	{
		m_PaymentMethod = Method;
	}
	
	public Integer GetPaymentMethod()
	{
		return m_PaymentMethod;
	}
	
	public void SetPaymentMethodText(String Text)
	{
		m_PaymentMethodText = Text;
	}
	
	public String GetPaymentMethodText()
	{
		return m_PaymentMethodText;
	}
	public boolean IsPayInStore(Integer Method)
	{
		return m_PaymentMethod.equals(LOCATION_PAY_METHOD_IN_APP);
	}

	public boolean DoesMatchMode(Integer Method)
	{
		return Method.equals(m_PaymentMethod);

	}

	public static FCLocationAllowedPayMethod ParseFromXMLAttributes(Attributes atts)
	{
		FCLocationAllowedPayMethod Method = new FCLocationAllowedPayMethod();
		
		String ParsedAttr;
		
		// ID
		ParsedAttr = atts.getValue(FCLocationAllowedPayMethod.LOCATION_PAY_METHOD_ATTR);
		if(( ParsedAttr==null) || (ParsedAttr.length()==0) )
		{
			return null;
		}
		
		try
		{
			ParsedAttr = URLDecoder.decode(ParsedAttr,HTTP.UTF_8);
			if((ParsedAttr!=null) && (ParsedAttr.length()>0))
			{
				Method.SetPaymentMethod(Integer.parseInt(ParsedAttr));
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
		ParsedAttr = atts.getValue(FCLocationAllowedPayMethod.LOCATION_PAY_METHOD_DESCR_ATTR);
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
		Method.SetPaymentMethodText(ParsedAttr);
		
		return Method;
	}

}
