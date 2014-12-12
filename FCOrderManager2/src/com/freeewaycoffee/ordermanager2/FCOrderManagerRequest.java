package com.freeewaycoffee.ordermanager2;

import org.apache.http.client.methods.HttpPost;

public class FCOrderManagerRequest
{
	private HttpPost m_RequestObject;
	private boolean m_ShowProgressDialog;
	private String m_ProgressMessage;
	private boolean m_InProgress;
	
	public FCOrderManagerRequest(HttpPost Request, boolean ShowProgress, String ProgressMessage)
	{
		m_RequestObject = Request;
		m_ShowProgressDialog= ShowProgress;
		m_ProgressMessage = ProgressMessage;
		m_InProgress=false;
	}
	
	public void SetInProgress()
	{
		m_InProgress=true;
	}
	
	public HttpPost GetRequest()
	{
		return m_RequestObject;
	}
	
	public boolean GetShowProgress()
	{
		return m_ShowProgressDialog;
	}
	
	public String GetProgressMessage()
	{
		return m_ProgressMessage;
	}
	
	public boolean IsInProgress()
	{
		return m_InProgress;
		
	}
}
