package com.freeewaycoffee.ordermanager;

public class FCOrderManagerError 
{
	private Integer ErrorCodeMajor;
	private Integer ErrorCodeMinor;
	private String ErrorMachineText; // Gory
	private String ErrorDisplayText; // Human Readable
	
	public FCOrderManagerError()
	{
		ErrorCodeMajor=-1;
		ErrorCodeMinor=-1;
		ErrorMachineText=null;
		ErrorDisplayText=null;
	}
	
	public Integer GetErrorCodeMajor()
	{
		return ErrorCodeMajor;
	}
	
	public void SetErrorCodeMajor(Integer Code)
	{
		ErrorCodeMajor=Code;
	}
	
	public Integer GetErrorCodeMinor()
	{
		return ErrorCodeMinor;
	}
	
	public void SetErrorCodeMinor(Integer Code)
	{
		ErrorCodeMinor=Code;
	}
	
	public void SetErrorMachineText(String Text)
	{
		ErrorMachineText=Text;
	}
	
	public String GetErrorMachineText()
	{
		return ErrorMachineText;
	}
	
	public void SetErrorDisplayText(String Text)
	{
		ErrorDisplayText=Text;
	}
	
	public String GetErrorDisplayText()
	{
		return ErrorDisplayText;
	}
}
