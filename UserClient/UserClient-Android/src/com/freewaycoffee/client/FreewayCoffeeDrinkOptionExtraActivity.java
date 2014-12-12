package com.freewaycoffee.client;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;


public class FreewayCoffeeDrinkOptionExtraActivity extends Activity 
{
	private FreewayCoffeeApp appState;
	private FreewayCoffeeCarMakeModelColorTagHolder CarData;
	private EditText TextBox;
	@Override
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	 	appState = ((FreewayCoffeeApp)getApplicationContext());        
	   	setContentView(R.layout.fc_user_drink_extra_edit);
	   	CarData = appState.GetCarDataBeingEdited();
	    
	   	TextBox = (EditText)findViewById(R.id.fc_user_drink_extra_edit);
	   	if(TextBox!=null)
	   	{
	   		//FILT InputFilter[] ExtraOptionsFilts = new InputFilter[]{appState.GetEditTextFreeformInputFilter()};
	   		//FILT TextBox.setFilters(ExtraOptionsFilts); 
	   	}
	   	
	   	String Extra = getIntent().getStringExtra(FreewayCoffeeDrinkAddEditActivity.SUB_ACTIVITY_EXTRA_OPTIONS_TEXT);
	   	if(TextBox!=null)
	   	{
	   		TextBox.setText(Extra);
	   	}
	   	else
	   	{
	   		TextBox.setText("ooops, Internal issues");
	   	}
	}
		
	public void DoUpdateExtra(View V)
	{
		String OptionData = TextBox.getText().toString();
		// For some reason, this always has a \n at the end.
		OptionData = OptionData.replaceAll("\n", "");
		
		Intent intent = new Intent();
		Integer ParentPos = getIntent().getIntExtra(FreewayCoffeeItemListView.PARENT_LIST_POSITION, -1);
		
		intent.putExtra(FreewayCoffeeItemListView.PARENT_LIST_POSITION,ParentPos);
		intent.putExtra(FreewayCoffeeDrinkAddEditActivity.SUB_ACTIVITY_EXTRA_OPTIONS_TEXT,OptionData);
		
		
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

