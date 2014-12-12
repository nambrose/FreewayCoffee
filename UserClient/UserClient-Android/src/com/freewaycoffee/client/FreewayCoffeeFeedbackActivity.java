package com.freewaycoffee.client;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

public class FreewayCoffeeFeedbackActivity extends Activity 
{
	static final private int ALERT_DIALOG_NO_EMAIL=1;
	static final private int ALERT_DIALOG_NO_COMMENT=2;
	
	private FreewayCoffeeApp appState;
	private RadioGroup MainRadio;
	private EditText CommentsText;
	private EditText EmailText;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
		
		 super.onCreate(savedInstanceState);
		 appState = ((FreewayCoffeeApp)getApplicationContext());        
		      
		 setContentView(R.layout.fc_feedback_dialog);
		 
		 
		 
		 MainRadio = (RadioGroup) findViewById(R.id.fc_feedback_happy_group);
		 if(MainRadio!=null)
		 {
			 MainRadio.setOnCheckedChangeListener(ToggleListener); 
		 }
		 
		 CommentsText = (EditText)findViewById(R.id.fc_feedback_edit);
		 if(CommentsText!=null)
		 {
			//FILT InputFilter[] Filts = new InputFilter[]{appState.GetEditTextFreeformInputFilter()};
			 //FILTCommentsText.setFilters(Filts); 
		 }
				
		 EmailText = (EditText)findViewById(R.id.fc_feedback_email_edit);
		 if(EmailText!=null)
		 {
			 //FILTInputFilter[] Filts = new InputFilter[]{appState.GetEditTextEmailInputFilter()};
			 //FILTEmailText.setFilters(Filts);
			 //FILT
			 EmailText.setText(appState.getLoginEmail());
		 }
				 
				 
	 }
	
	public void DoCancel(View v)
	{
		finish();
	}
	
	protected Dialog onCreateDialog (int id, Bundle args)
	{
		AlertDialog.Builder AlertBuilder;
		switch(id)
		{
		case ALERT_DIALOG_NO_EMAIL:
			AlertBuilder = new AlertDialog.Builder(this);
			AlertBuilder.setMessage(R.string.fc_must_enter_email)
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) 
				{
					dismissDialog(ALERT_DIALOG_NO_EMAIL);

				}
			});
			return AlertBuilder.create();
		case ALERT_DIALOG_NO_COMMENT:
			AlertBuilder = new AlertDialog.Builder(this);
			AlertBuilder.setMessage(R.string.fc_must_enter_feedback)
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) 
				{
					dismissDialog(ALERT_DIALOG_NO_EMAIL);

				}
			});
			return AlertBuilder.create();
		}
		return null;
	}
			
	
	public void DoSendFeedback(View v)
	{
		
		// Get the checked Feedback icon
		int HappinessIndicator=-1;
		/*
		for (int j = 0; j < MainRadio.getChildCount(); j++)
    	{
            final ToggleButton view_check = (ToggleButton) MainRadio.getChildAt(j);
            if(view_check.isChecked())
            {
            	HappinessIndicator=j;
            	break;
            }
    	}
		*/
		CharSequence CommentsSeq = CommentsText.getText();
		String Comments=null;
		if(CommentsSeq!=null)
		{
			Comments = CommentsSeq.toString();
		}
		
		if( (CommentsSeq==null) || (Comments==null) || (Comments.length()==0))
		{
			// LOOKS WEIRD> FIXME TODO
			//showDialog(ALERT_DIALOG_NO_COMMENT);
			//return;
		}
				
			
		CharSequence EmailSeq = EmailText.getText();
		String UserEmail=null;
		if(EmailSeq!=null)
		{
			UserEmail = EmailSeq.toString();
		}
		
		if( (EmailSeq==null) || (UserEmail==null) || (UserEmail.length()==0))
		{
			// LOOKS WEIRD> FIXME TODO
			//showDialog(ALERT_DIALOG_NO_EMAIL);
			//return;
		}
		try
		{
			String CommandStr = appState.MakeSendFeedbackURL(HappinessIndicator, Comments, UserEmail );
			FreewayCoffeeFeedbackAsync Async= new FreewayCoffeeFeedbackAsync(this,appState);
			Async.execute(CommandStr);
			Toast Msg = Toast.makeText(appState.getApplicationContext(),
					getString(R.string.fc_feedback_sent),
					Toast.LENGTH_SHORT);
			Msg.show();
			finish();
		}
		catch ( UnsupportedEncodingException e)
		{
			
		}
	}
	
	static final RadioGroup.OnCheckedChangeListener ToggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
                view.setChecked(view.getId() == i);
            }
        }
    };

    public void onToggle(View view) 
    {
    	int Checked = view.getId();
    	for (int j = 0; j < MainRadio.getChildCount(); j++)
    	{
            final ToggleButton view_check = (ToggleButton) MainRadio.getChildAt(j);
            if(view_check.getId() == Checked)
            {
            	view_check.setChecked(true);
            }
            else
            {
            	view_check.setChecked(false);
            }
    	}
    }

    
}
