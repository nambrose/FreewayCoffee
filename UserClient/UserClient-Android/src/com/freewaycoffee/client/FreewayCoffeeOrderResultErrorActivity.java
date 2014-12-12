package com.freewaycoffee.client;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class FreewayCoffeeOrderResultErrorActivity extends Activity 
{
	private FreewayCoffeeApp appState;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
		 super.onCreate(savedInstanceState);
		 appState = ((FreewayCoffeeApp)getApplicationContext());        
     
		 setContentView(R.layout.fc_order_response_error);
		 
		 //TextView UsernameView = (TextView)findViewById(R.id.fc_banner_text);
         
         //String ResponseString = getString(R.string.fc_order_response);
         // TODO move USER_INFO_NAME_KEY to a better place
         //UsernameView.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + ", " + ResponseString);
         
		 TextView MainText = (TextView)findViewById(R.id.fc_order_response_text);
		 
		 //Button ArrivedButton = (Button)findViewById(R.id.fc_order_response_here_button);
		 /*
		 if(appState.WasLastOrderSuccessful()==false)
		 {
			 ArrivedButton.setVisibility(Button.INVISIBLE);
		 }
		 */
		 
		 String OrderErrorResponse = getIntent().getStringExtra(FreewayCoffeeItemListView.ERROR_ORDER_INTENT_ID);
		 MainText.setText(OrderErrorResponse);
	 }

	 
	 public void DoBack(View v)
	 {
		 setResult(RESULT_OK);
		 finish();
	 }
	  
	 @Override
   	public void onConfigurationChanged(Configuration newConfig) 
   	{
   	    super.onConfigurationChanged(newConfig);
   	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   	}

}

