package com.freewaycoffee.client;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class FreewayCoffeeReportsActivity extends Activity 
{
	//static final private int ALERT_DIALOG_NO_EMAIL=1;
	//static final private int ALERT_DIALOG_NO_COMMENT=2;
		
	private FreewayCoffeeApp appState;
//	private RadioGroup MainRadio;
	private EditText NumberOfDays;
//	private EditText EmailText;
		
	@Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
		 super.onCreate(savedInstanceState);
		 appState = ((FreewayCoffeeApp)getApplicationContext());        
			      
		 setContentView(R.layout.fc_reports);
			  
		 NumberOfDays = (EditText)findViewById(R.id.fc_reports_edit);			 
	}
		
	public void DoCancel(View v)
	{
		finish();
	}
		
	
				
		
	public void DoReport(View v)
	{
	
		if(NumberOfDays==null)
		{
			return;
		}
		
		CharSequence NumDaysSeq = NumberOfDays.getText();
		String NumDays=null;
		if(NumDaysSeq!=null)
		{
			NumDays = NumDaysSeq.toString();
		}
			
		try
		{
			HttpPost Cmd = appState.MakeReportPost(NumDays);
			FreewayCoffeeReportsAsyncPost Async= new FreewayCoffeeReportsAsyncPost(this,appState);
			Async.execute(Cmd);
			Toast Msg = Toast.makeText(appState.getApplicationContext(),
					"Your Report Link Will be emailed to You",
					Toast.LENGTH_SHORT);
			Msg.show();
			finish();
		}
		catch ( UnsupportedEncodingException e)
		{
			
		}
	}

}
