package com.freeewaycoffee.ordermanager2;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

public class FCOrderManagerActivity extends Activity 
{
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fc_om_signon);
        Intent intent = new Intent();
        intent.setClassName(this, FCOrderManagerSignonActivity.class.getName());
        startActivity(intent);
        this.finish();
    }
    @Override
   	public void onConfigurationChanged(Configuration newConfig) 
   	{
   	    super.onConfigurationChanged(newConfig);
   	    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   	 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
   	}
}