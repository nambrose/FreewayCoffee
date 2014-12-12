package com.freeewaycoffee.ordermanager2;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.freewaycoffee.clientobjlib.FCXMLHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class FCOrderManagerSignonActivity extends Activity 
{
	static final private int MENU_ABOUT = Menu.FIRST;
	static final private int MENU_FEEDBACK = Menu.FIRST+1;

	static final private int SCHEMA_ERROR_DIALOG=1;
	static final private int LOGIN_FAIL_DIALOG=2;
	static final private int NETWORK_ERROR_DIALOG=3;


	private FCOrderManagerApp appState;
	private String EmailAddress;
	private String Password;
	//private boolean LoginAutomatically;
	private  ProgressDialog SignonProgress;
	//private FreewayCoffeeSignonAsyncGet AsyncGet;
	private String SchemaError;

	private FCOrderManagerSignonAsync Async;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		appState = ((FCOrderManagerApp)getApplicationContext());        
		setContentView(R.layout.fc_om_signon);
		@SuppressWarnings("deprecation")
		Object retained = getLastNonConfigurationInstance(); 

		EditText EmailView = (EditText)findViewById(R.id.fc_om_signon_email_edit);
		/*
	     InputFilter[] filts = new InputFilter[]{appState.GetEditTextEmailInputFilter()};

	     EmailView.setFilters(filts); 
		 */


		//InputFilter[] Emailfilts = new InputFilter[]{appState.GetEditTextEmailInputFilter()};

		//EmailView.setFilters(Emailfilts); 


		EditText PasswordView = (EditText)findViewById(R.id.fc_om_signon_password_edit);

		if(retained instanceof FCOrderManagerSignonAsync)
		{

			Async = (FCOrderManagerSignonAsync)retained;


			EmailAddress=Async.EmailAddressSaved;
			Password=Async.PasswordSaved;
			//LoginAutomatically=AsyncPost.LoginAutomaticallySaved;

			// Get the saved state -- the Async Get with the XML and the ListIndex.
			// WHen we set the new activity, it will call Process XML for us
			EmailView.setText(EmailAddress);
			PasswordView.setText(Password);
			//AutoSigninView.setChecked(LoginAutomatically);
			showProgressDialog();// This must be before setActivity as that calls ProcessXML which destroys the dialog --- so we dont want to just re-create it again
			//AsyncGet.SetActivity(this);


			Async.SetActivity(this);
		}
		else
		{	     
			SignonProgress=null;
			Async=null;
			EmailView.setText(appState.GetUserLoginName());
			EmailAddress = appState.GetUserLoginName();
			PasswordView.setText(appState.GetUserPassword());
			Password=appState.GetUserPassword();
			//AutoSigninView.setChecked(appState.GetLoginAutomatically());
		}
		/*
		 * NO AUTO LOGIN FOR ADMIN APPS -- TOO DANGEROUS
	     if(appState.IsUserNameSet() && appState.IsPasswordSet())
    	 {
	    	 DoLogin();
    	 }
		 */
	}

	protected Dialog onCreateDialog (int id, Bundle args)
	{

		switch(id)
		{
		case SCHEMA_ERROR_DIALOG:

			return new AlertDialog.Builder(this)
			.setIcon(R.drawable.fc_om_error)
			.setTitle(R.string.fc_om_schema_error)
			.setMessage(SchemaError)
			.setPositiveButton(R.string.fc_om_ok, new DialogInterface.OnClickListener() {
				@SuppressWarnings("deprecation")
				public void onClick(DialogInterface dialog, int whichButton) {

					/* User clicked OK so do some stuff */
					dismissDialog(SCHEMA_ERROR_DIALOG);
				}
			})
			.create();


		case LOGIN_FAIL_DIALOG:

			return new AlertDialog.Builder(this)
			.setIcon(R.drawable.fc_om_error)
			.setTitle(R.string.fc_om_signon_error)
			.setMessage(R.string.fc_om_signon_not_successful)
			.setPositiveButton(R.string.fc_om_ok, new DialogInterface.OnClickListener() {
				@SuppressWarnings("deprecation")
				public void onClick(DialogInterface dialog, int whichButton) {

					/* User clicked OK so do some stuff */
					dismissDialog(LOGIN_FAIL_DIALOG);
				}

			})
			.create();

		case NETWORK_ERROR_DIALOG:

			return new AlertDialog.Builder(this)
			.setIcon(R.drawable.fc_om_error)
			.setTitle(R.string.fc_om_network_error)
			.setMessage(R.string.fc_om_network_error)
			.setPositiveButton(R.string.fc_om_ok, new DialogInterface.OnClickListener() {
				@SuppressWarnings("deprecation")
				public void onClick(DialogInterface dialog, int whichButton) {

					/* User clicked OK so do some stuff */
					dismissDialog(NETWORK_ERROR_DIALOG);
				}

			})
			.create();
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);


		MenuItem item = menu.add(0,MENU_ABOUT,Menu.NONE,R.string.fc_om_about);
		item.setIcon(R.drawable.fc_om_about);

		item = menu.add(0,MENU_FEEDBACK,Menu.NONE,R.string.fc_om_contact_us);
		item.setIcon(R.drawable.fc_om_contact_us);

		return true;
	}

	@Override 
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch(item.getItemId())
		{
		case MENU_ABOUT:
			DoAboutMenu(item);
			return true;

		case MENU_FEEDBACK:
			DoFeedbackMenu(item);
			return true;

		}
		return false;
	}

	public void DoAboutMenu(MenuItem item)
	{
		/*
			Intent intent = new Intent();
			intent.setClassName(this, FCOrderManagerAboutDialogActivity.class.getName());
			startActivity(intent);
		 */
	}

	public void DoFeedbackMenu(MenuItem item)
	{
		/*
			Intent intent = new Intent();
			intent.setClassName(this, FreewayCoffeeFeedbackActivity.class.getName());
			startActivity(intent);
		 */
	}

	public void showProgressDialog()
	{
		SignonProgress = ProgressDialog.show(this, "",
				getString(R.string.fc_om_signing_you_in),
				true);
	}
	private void DisplayError(Integer ErrorViewID, Integer ErrorStringID)
	{
		TextView EmailError = (TextView)findViewById(ErrorViewID);
		EmailError.setText(ErrorStringID);
		EmailError.invalidate();
	}

	private void ClearError(Integer ErrorViewID)
	{
		TextView EmailError = (TextView)findViewById(ErrorViewID);
		EmailError.setText("");
		EmailError.invalidate();
	}

	private void UnlinkAsync()
	{
		if(Async!=null)
		{
			Async.UnlinkActivity();
			Async=null;
		}
	}

	private void ClearOldErrors()
	{
		ClearError(R.id.fc_om_signon_email_error);	    	
		ClearError(R.id.fc_om_signon_password_error);  	
	}

	public void AttemptSignon(View v)
	{
		DoAttemptSignon(v);
	}


	protected void onDestroy ()
	{
		super.onDestroy();

		if(Async!=null)
		{
			Async.UnlinkActivity();
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance()
	{
		if(Async!=null)
		{
			Async.UnlinkActivity();
			Async.EmailAddressSaved =EmailAddress;
			Async.PasswordSaved=Password;
			//Async.LoginAutomaticallySaved=LoginAutomatically;
			return Async;
		}
		return null;
	}

	/*
		 public void GoToSignup(View v)
		 {
			 // Start the Signup activity
			 // We finish() here to avoid a trigger-happy user making an infinite number of activities
 			Intent intent = new Intent();
			intent.setClassName(this, FCoffeeSignupActivity.class.getName());
			startActivity(intent);
			finish();
		 }
	 */

	// Separate method so we can call from onCreate w/out necessarily worrying about passing a View
	private void DoAttemptSignon(View SignonButton)
	{

		ClearOldErrors();

		// Validate Email address
		EditText EmailView = (EditText)findViewById(R.id.fc_om_signon_email_edit);
		EmailAddress = EmailView.getText().toString();

		if(EmailAddress.length()==0)
		{
			DisplayError(R.id.fc_om_signon_email_error,R.string.fc_om_error_email_cannot_be_empty);
			return;
		}

		// Validate password non-empty
		EditText PasswordView = (EditText)findViewById(R.id.fc_om_signon_password_edit);
		Password = PasswordView.getText().toString();
		if(Password.length()==0)
		{
			DisplayError(R.id.fc_om_signon_password_error,R.string.fc_om_error_password_cannot_be_empty);
			return;
		}


		// Auto signin

		//LoginAutomatically = true;
		DoLogin();


	}

	private void DoLogin()
	{

		try
		{
			Async = new FCOrderManagerSignonAsync(this,appState);
			Async.execute(appState.MakeSignonHTTPPost(EmailAddress, Password));
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO
			DisplayNetworkError();
			return;
		}
	}
	@SuppressWarnings("deprecation")
	public void ProcessXMLResult(String XML)
	{
		//Log.w("FreewayCoffeeDB",XML);
		/* Get a SAXParser from the SAXPArserFactory. */
		//AsyncGet=null;
		UnlinkAsync();
		if(SignonProgress!=null)
		{
 			SignonProgress.dismiss();
			SignonProgress=null; // So we dont try to dismiss twice if we sign on, sign off then change orientation during next signon ?
		}

		SAXParserFactory spf = SAXParserFactory.newInstance();
		try
		{
			SAXParser sp = spf.newSAXParser();

			/* Get the XMLReader of the SAXParser we created. */
			XMLReader xr = sp.getXMLReader();

			FCOrderManagerXMLHandler Handler = new FCOrderManagerXMLHandler(appState);
			xr.setContentHandler(Handler);

			/* Parse the xml-data from our URL. */

			InputSource is = new InputSource(new StringReader(XML));

			xr.parse(is);
			/* Parsing has finished. */


			if(Handler.NetworkError==true)
			{

				DisplayNetworkError();
				return;
			}

			if(Handler.IsSchemaError())
			{
				DisplaySchemaError(Handler.CompatReleaseRequired);
				return;
			}
			if(Handler.ResponseType==FCXMLHelper.ResponseTypeEnum.SIGNON)
			{	
				if(Handler.Response==FCXMLHelper.ResponseEnum.OK)
				{

					
	    			Toast Err = Toast.makeText(appState.getApplicationContext(),
	    					"Signed on OK",
							Toast.LENGTH_SHORT);
	    			Err.show();
					 
					// Save the users login state to the database (in case they switched user)
					// Since we were having some issues here, only save them if they have some data ... sadly cannot do for auto login
					if(EmailAddress.length()>0)
					{
						appState.SetUserLoginName(EmailAddress);
					}
					if(Password.length()>0)
					{
						appState.SetUserPassword(Password);
					}

					//appState.SetLoginAutomatically(LoginAutomatically);
					appState.ClearAllDownloadedData(); // This is our chance to make sure a complete refresh is done

					if(Handler.m_UserData!=null)
					{
						appState.SetUserData(Handler.m_UserData);
					}
					if(Handler.m_CurrentLocation!=null)
					{
						appState.SetUserLocationData(Handler.m_CurrentLocation);
					}
					
	    			Intent intent = new Intent();
	    			intent.setClassName(this, FCOrderManagerOrderMainActivity.class.getName());
	    			startActivity(intent);
					
				}
				else
				{
					showDialog(LOGIN_FAIL_DIALOG);
				}
			}
			else
			{
				showDialog(LOGIN_FAIL_DIALOG);
				/*
	    			Toast Err = Toast.makeText(appState.getApplicationContext(),
	    										getString(R.string.fc_login_not_successful),	    							
	    										Toast.LENGTH_SHORT);
	    			Err.show();
				 */

			}
		}
		catch(SAXException e)
		{

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

	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	private void DisplaySchemaError(String CompatReleaseRequired)
	{
		String this_app_ver="";
		try
		{
			this_app_ver = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		}
		catch (NameNotFoundException e)
		{
			//Log.v(tag, e.getMessage());
		}
		SchemaError = getString(R.string.fc_om_version_error_occurred) + CompatReleaseRequired + " " +
				getString(R.string.fc_om_version_you_have) + this_app_ver + " " + getString(R.string.fc_om_please_download_version);

		showDialog(SCHEMA_ERROR_DIALOG);

	}

	private void DismissProgress()
	{
		if(SignonProgress!=null)
		{
			SignonProgress.dismiss();
			SignonProgress=null; // So we dont try to dismiss twice if we sign on, sign off then change orientation during next signon ?
		}
	}
	private void DisplayNetworkError()
	{
		DismissProgress();
		showDialog(NETWORK_ERROR_DIALOG);
		/*
	    	Toast Err = Toast.makeText(appState.getApplicationContext(),
					getString(R.string.fc_network_error),
					Toast.LENGTH_SHORT);
	    	Err.show();
		 */
	}
	
}
