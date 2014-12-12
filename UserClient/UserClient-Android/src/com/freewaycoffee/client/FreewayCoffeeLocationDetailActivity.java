package com.freewaycoffee.client;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FreewayCoffeeLocationDetailActivity extends Activity 
{
	 	private FreewayCoffeeApp appState;
	 	
	 	 @Override
	 	 public void onCreate(Bundle savedInstanceState) 
	 	 {
	 		 super.onCreate(savedInstanceState);
	 		 appState = ((FreewayCoffeeApp)getApplicationContext());        
	      
	 		 setContentView(R.layout.fc_location_detail);
	 		 
	 		TextView UsernameView = (TextView)findViewById(R.id.fc_banner_text);
	    	 String AddStr = getString(R.string.fc_your_location);
	  	     
	  	     UsernameView.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + ", " + AddStr);
	  	     
	  	     
	 		 //TextView UsernameView = (TextView)findViewById(R.id.fc_order_response_user_banner);
	          
	          //String ResponseString = getString(R.string.fc_order_response);
	          // TODO move USER_INFO_NAME_KEY to a better place
	          //UsernameView.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + ", " + ResponseString);
	          
	 		 TextView MainText = (TextView)findViewById(R.id.fc_location_detail_text);
	 		 
	 		 MainText.setText(appState.MakeLoctionDetailText());
	 		 
	 		if(appState.isAppInstalled("com.google.android.apps.maps")!=true)
	 		{
	 			Button MapBut = (Button)findViewById(R.id.fc_location_detail_map_button);
	 			{
	 				if(MapBut!=null)
	 				{
	 					MapBut.setVisibility(Button.INVISIBLE);
	 				}
	 			}
	 		}
	 		 
	 	 }

	 	public void DoShowMap(View v)
	 	{
	 		try
	 		{
	 			String MapURL = appState.MakeLocationGoogleMapsURL();
	 		
	 			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(MapURL));
	 		
	 			if(appState.isAppInstalled("com.google.android.apps.maps")==true)
	 			{
	 				intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
	 				startActivity(intent);
	 			}
	 			
	 		}
	 		catch(ActivityNotFoundException e)
	 		{
	 			
	 		}
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

	 


