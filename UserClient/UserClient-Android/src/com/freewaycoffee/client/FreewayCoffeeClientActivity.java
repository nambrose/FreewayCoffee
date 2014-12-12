package com.freewaycoffee.client;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.app.Activity;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.CheckBox;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.http.HttpEntity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.util.Log;

import android.os.Bundle;

public class FreewayCoffeeClientActivity extends Activity {
	
	private FreewayCoffeeApp appState;
	  @Override
   	public void onConfigurationChanged(Configuration newConfig) 
   	{
   	    super.onConfigurationChanged(newConfig);
   	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   	}
	   
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        appState = ((FreewayCoffeeApp)getApplicationContext());
        
     //   setContentView(R.layout.signup);
      
        if((appState.getLoginEmail()!=null) && (appState.GetPassword()!=null))
        {
        	// If both PW and Login Email are set, then we assume the user has an account and goto signon
        	Intent intent = new Intent();
            intent.setClassName(this, FreewayCoffeeSignonActivity.class.getName());
            startActivity(intent);
            this.finish();
        }
        else
        {
        	// Must Register.
        	Intent intent = new Intent();
            intent.setClassName(this, FreewayCoffeeSignupActivity.class.getName());
            startActivity(intent);
            this.finish();
        }
     
    }
}

