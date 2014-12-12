package com.freewaycoffee.client;

import android.app.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class FreewayCoffeeAboutDialogActivity extends Activity 
{

	private FreewayCoffeeApp appState;
		 	
	 @Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
		 super.onCreate(savedInstanceState);
		 appState = ((FreewayCoffeeApp)getApplicationContext());        
		      
		 setContentView(R.layout.fc_about_dialog);
		 		 
		  //TextView UsernameView = (TextView)findViewById(R.id.fc_order_response_user_banner);
		          
		 //String ResponseString = getString(R.string.fc_order_response);
		 // TODO move USER_INFO_NAME_KEY to a better place
		 //UsernameView.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + ", " + ResponseString);
		          
		 TextView MainText = (TextView)findViewById(R.id.fc_about_text);
		 String this_app_ver = "unknown";
		 
		 
		 try
		 {
			 this_app_ver = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		 }
		 catch(PackageManager.NameNotFoundException e)
		 {
			 
		 }
		 
		 String AboutText = ("Freeway Coffee(Version: " + this_app_ver + ")<br><br>");
		 
		 AboutText += "Fresh Coffee Without Lines or Leaving Your Car!<br><br>";

		 AboutText += "Drinking your favorite coffee just became faster and easier.<br><br><b>Customize your coffee, place your order</b>, and pay for it all on your phone before you leave the house." +
		 "<br><br><b>Upon arrival<b>, your order will be <b>made fresh</b> and <b>delivered to your car</b>.<br>" +
		 "<br><br>Freeway Coffee is an Angel-funded start up, incorporated in California. Our team consists of coffee lovers who are also technologists with experience in developing online and mobile consumer products and applications." +
		 "<br><br>For more information visit <a href=\"http://www.www.freewaycoffee.com\">www.freewaycoffee.com</a>";
		 		 
		 
		 Spanned marked_up = Html.fromHtml(AboutText);
		MainText.setText(marked_up,BufferType.SPANNABLE);
				 		 
		 
		 /*
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
*/
	 }
		public void DoShowSite(View v)
		{
		
			String SiteURL = appState.MakeSiteURL();
		 		
		 	Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(SiteURL));
		 	startActivity(intent);
		 		
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

		 


