package com.freewaycoffee.client;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class FreewayCoffeeTimeToLocationActivity extends Activity 
{
	private FreewayCoffeeApp appState;
	private SeekBar TimeSeeker;
	private TextView TimeToLocationText;
	private Integer CurrentTimeToLocation;
	private Integer OriginalTimeToLocation;
	private ProgressDialog TimeToLocProgress;
	private FreewayCoffeeTimeToLocationXMLHandler TimeToLocationXMLHandler;
	private FreewayCoffeeTimeToLocationAsyncGet AsyncGet;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
		super.onCreate(savedInstanceState);
	 	appState = ((FreewayCoffeeApp)getApplicationContext());        
     	setContentView(R.layout.fc_time_to_location);
     	
     	
     	Object retained = getLastNonConfigurationInstance();
 		
 		if(retained instanceof FreewayCoffeeTimeToLocationAsyncGet)
 		{
 			
 			AsyncGet=(FreewayCoffeeTimeToLocationAsyncGet)retained;
 			CurrentTimeToLocation = AsyncGet.CurrentTimeToLocationSaved;
 	     	OriginalTimeToLocation = AsyncGet.OriginalTimeToLocationSaved;
 	     	showProgressDialog();// This must be before setActivity as that calls ProcessXML which destroys the dialog --- so we dont want to just re-create it again
    		AsyncGet.SetActivity(this);
 		}
 		else
 		{
 			AsyncGet =null;
 			CurrentTimeToLocation = getIntent().getIntExtra(FreewayCoffeeItemListView.INTENT_KEY_TIME_TO_LOCATION,0);
 	     	OriginalTimeToLocation = getIntent().getIntExtra(FreewayCoffeeItemListView.INTENT_KEY_TIME_TO_LOCATION,0);
 		}
 		
     	TextView UserBanner = (TextView)findViewById(R.id.fc_banner_text);
     	UserBanner.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) +", " + getString(R.string.fc_set_time_to));
     	
     	String Location = getIntent().getStringExtra(FreewayCoffeeItemListView.INTENT_KEY_USER_LOCATION_STRING);
     	
     	//getString(R.string.
     	TextView LocationNameView = (TextView)findViewById(R.id.fc_time_to_location_location_label);
     	LocationNameView.setText(getString(R.string.fc_location) + " " + Location);
     	
     	TimeToLocationText= (TextView)findViewById(R.id.fc_time_to_location_update_label);
     	UpdateCurrentTimeToLocationDisplay();
     	
     	SetSliderMinMaxLabels();
     	
     	TimeSeeker = (SeekBar)findViewById(R.id.fc_time_to_location_seeker);
     	
     
     	TimeSeeker.setMax(appState.GetPreferenceMaxTimeToLocation()+1);
     	
     	UpdateTimeToLocationSlider();
     	
     	TimeSeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
     	{

            @Override
            public void onProgressChanged(SeekBar seekBar,
                    int progress, boolean fromUser)
            {
            	CurrentTimeToLocation=progress;
            	UpdateCurrentTimeToLocationDisplay();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
     	});

     	
     	
	 }
	   protected void onDestroy ()
		{
			 super.onDestroy();
			 if(AsyncGet!=null)
			 {
				 AsyncGet.UnlinkActivity();
			 }
		}
		 
		 @Override
		 public Object onRetainNonConfigurationInstance()
		 {
			 if(AsyncGet!=null)
			 {
				 AsyncGet.UnlinkActivity();
				 AsyncGet.CurrentTimeToLocationSaved=CurrentTimeToLocation;
				 AsyncGet.OriginalTimeToLocationSaved=OriginalTimeToLocation;
				 return AsyncGet;
			 }
			 return null;
		 }
		 

	public void showProgressDialog()
	{
		 TimeToLocProgress = ProgressDialog.show(this, "",
				 							   getString(R.string.fc_updating_location_arrival),
				 							   true); 
	}
	
	
	public void UpdateTimeToLocationSlider()
	{
     	TimeSeeker.setProgress(CurrentTimeToLocation);
	}
	
	public void DoCancel(View V)
	{
		setResult(RESULT_CANCELED);
		finish();
		
	}
	public void DoReset(View V)
	{
		CurrentTimeToLocation=OriginalTimeToLocation;
		UpdateTimeToLocationSlider();
		UpdateCurrentTimeToLocationDisplay();
		
		
	}
	
	public void DoUpdate(View V)
	{
		try
		{
			AsyncGet = new FreewayCoffeeTimeToLocationAsyncGet(this,appState);
			String CommandStr = appState.MakeUpdateTimeToLocationURL(CurrentTimeToLocation);
			AsyncGet.execute(CommandStr);
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO
			DisplayNetworkError();
			return;
		}
	}
	
	
	public void UpdateCurrentTimeToLocationDisplay()
	{
		if(TimeToLocationText==null)
		{
			// TODO Track this
			TimeToLocationText.setText(getString(R.string.fc_oops));
			return;
		}
		// = "Time: x mins"
		TimeToLocationText.setText(getString(R.string.fc_time) + " " +
								   String.valueOf(CurrentTimeToLocation) +  " " +
								   getString(R.string.fc_minutes));
		
		
	}
	  
	@Override
   	public void onConfigurationChanged(Configuration newConfig) 
   	{
   	    super.onConfigurationChanged(newConfig);
   	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   	}
	
	public void ProcessXMLResult(String XML)
    {
		/* Get a SAXParser from the SAXPArserFactory. */
		SAXParserFactory spf = SAXParserFactory.newInstance();
		AsyncGet=null;
		try
		{
			SAXParser sp = spf.newSAXParser();

            /* Get the XMLReader of the SAXParser we created. */
			XMLReader xr = sp.getXMLReader();
            /* Create a new ContentHandler and apply it to the XML-Reader*/
			TimeToLocationXMLHandler = new FreewayCoffeeTimeToLocationXMLHandler(appState);
			xr.setContentHandler(TimeToLocationXMLHandler);

           // Parse the xml-data from our URL.
			
			InputSource is = new InputSource(new StringReader(XML));
			//is.setEncoding("UTF-8");
			xr.parse(is);
           /* Parsing has finished. */
          if(TimeToLocProgress!=null)
          {
        	  TimeToLocProgress.dismiss();
        	  TimeToLocProgress=null;
          }
          
          if(TimeToLocationXMLHandler.NetworkError==true)
  		  {
        	  DisplayNetworkError();
        	  return;
  		  }
          if(TimeToLocationXMLHandler.signonResponse!=null)
          {
        	   // TODO -=- in general, what do we do for these sub-views if we get say a SIGNON_OK.
        	   // Cannot just reload indefinitely. Can I even call reload here ?
        	   // Return to the parent view and try to let that one refresh ? Lets punt it for now.

               // Tell the parent view that some critical login-type thing has happened!
        	  setResult(FreewayCoffeeItemListView.RESULT_CODE_NOT_LOGGED_IN);
        	   
        	   this.finish();
           }
          
          
          if(TimeToLocationXMLHandler.UpdateTimeToLocationFlag==true)
          {
        	  Toast SuccessToast = Toast.makeText(appState.getApplicationContext(),
        			  							  getString(R.string.fc_time_to_location_updated),
        			  							  Toast.LENGTH_SHORT);
        	  SuccessToast.show();
        	 
        		  appState.GetUserInfoData().put(FreewayCoffeeItemListView.USER_TIME_TO_LOCATION,String.valueOf(CurrentTimeToLocation));
        		  EndActivity();
        	  
        	  
          }
          else
          {
        	  // TODO Obviously could do better here.
        	  Toast ErrorToast = Toast.makeText(appState.getApplicationContext(),"Time to location could not be updated. Please try again",Toast.LENGTH_SHORT);
        	  ErrorToast.show();
        	  return;
          }
          
		}
		catch(SAXException e)
    	{
    		//Log.w("FCItemListView",e.getMessage());
			 DisplayNetworkError();
       	  	return;
    	}
    	catch (ParserConfigurationException pe)
    	{
    		 DisplayNetworkError();
       	  	return;
    	}
    	catch (IOException io)
    	{
    		 DisplayNetworkError();
    		 return;
    	}	
		
		
    }
	
	private void EndActivity()
	{	
       	setResult(RESULT_OK);
       	finish();
	}
	
	 private void DisplayNetworkError()
	 {
		 if(TimeToLocProgress!=null)
         {
			 TimeToLocProgress.dismiss();
			 TimeToLocProgress=null;
         }
	    
		 Toast Err = Toast.makeText(appState.getApplicationContext(),
					getString(R.string.fc_network_error),
					Toast.LENGTH_SHORT);
		 Err.show();
	 }
	 
	public void SetSliderMinMaxLabels()
 	{
		TextView TimeToLocationMaxLabel = (TextView)findViewById(R.id.fc_time_to_location_seeker_text_max);
		if(TimeToLocationMaxLabel==null)
		{
			// TODO this doesn't look good
		}
		else
		{
			TimeToLocationMaxLabel.setText(String.valueOf(appState.GetPreferenceMaxTimeToLocation()));	
		}
 	
		TextView TimeToLocationMinLabel = (TextView)findViewById(R.id.fc_time_to_location_seeker_text_min);
		if(TimeToLocationMinLabel==null)
		{
			// TODO this doesn't look good
		}
		else
		{
 			TimeToLocationMinLabel.setText(String.valueOf(0));	
		}
 	}
}

// WONT USE BUT LIKE IT FOR SOMETHING ELSE ?
/*
seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

@Override
public void onStopTrackingTouch(SeekBar seekBar) {

    newProgressValue = seekBar.getProgress();
    currentProgress = newProgressValue ;
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putInt(Key_PROGRESS, newProgressValue);
    editor.commit();

}
*/