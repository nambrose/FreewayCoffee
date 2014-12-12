package com.freewaycoffee.client;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class FreewayCoffeeLicensePlateActivity extends Activity 
{
	private FreewayCoffeeApp appState;
	private FreewayCoffeeCarMakeModelColorTagHolder CarData;
	private EditText TextBox;
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	 	appState = ((FreewayCoffeeApp)getApplicationContext());        
    	setContentView(R.layout.fc_license_plate);
    	CarData = appState.GetCarDataBeingEdited();
    
    	TextView UsernameView = (TextView)findViewById(R.id.fc_banner_text);
   	    String AddStr = getString(R.string.fc_your_licence);
 	     
 	     UsernameView.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + ", " + AddStr);
 	     
    	TextBox = (EditText)findViewById(R.id.fc_license_plate_edit);
    	TextBox.setText(CarData.Tag);
	}
	
	public void DoUpdateLicense(View V)
	{
		CarData.Tag=TextBox.getText().toString();
		
		// For some reason, this always has a \n at the end.
		CarData.Tag = CarData.Tag.replaceAll("\n", "");
		
		appState.GetCarDataBeingEdited().Tag=CarData.Tag;
		Intent intent = new Intent();
		setResult(RESULT_OK,intent);
		finish();
	}
	
	public void UserCanceled(View V)
	{
		Intent intent = new Intent();
		setResult(RESULT_CANCELED,intent);
		finish();
	}
	 
	@Override
   	public void onConfigurationChanged(Configuration newConfig) 
   	{
   	    super.onConfigurationChanged(newConfig);
   	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   	}

}
