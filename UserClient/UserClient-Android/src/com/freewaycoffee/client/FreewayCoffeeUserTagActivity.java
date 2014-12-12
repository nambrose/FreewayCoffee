package com.freewaycoffee.client;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class FreewayCoffeeUserTagActivity extends Activity 
{
	// For the Sub-activity to pick car options
	static public final String OPTION_PICK_TYPE="pick_car_option_type";

	private FreewayCoffeeApp appState;
	
	private ProgressDialog TagProgress;
	private FreewayCoffeeUserTagAsyncGet AsyncGet;
	private FreewayCoffeeUserTagXMLHandler TagXMLHandler;
	private FreewayCoffeeCarMakeModelColorTagHolder CarData;
	private ArrayList<FCUserTagIndexPair> ListIndex;
	
	static public final Integer LIST_ENTRY_MAKE=0;
	static public final Integer LIST_ENTRY_MODEL=1;
	static public final Integer LIST_ENTRY_COLOR=2;
	static public final Integer LIST_ENTRY_TAG=3;
	private ListView MainList;
	private FCUserTagAdapter ListAdapter;
	static final private int ERROR_DIALOG=1;
	private CheckBox m_WalkupCheck;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		CarData=null;
		ListAdapter=null;
		
	 	appState = ((FreewayCoffeeApp)getApplicationContext());        
    	setContentView(R.layout.fc_user_tag_list);
    	
    	TextView Banner = (TextView)findViewById(R.id.fc_banner_text);
    	Banner.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + ", " + getString(R.string.fc_set_your_vehicle));
    	
    	ListIndex = new ArrayList<FCUserTagIndexPair>();
    	
    	MainList = (ListView)findViewById(R.id.fc_user_tag_list);
        // listView.setOnItemClickListener(new OnItemClickListener()
           
        View footerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fc_user_tag_list_footer, null, false);
        MainList.addFooterView(footerView);
           
        View headerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fc_user_tag_list_header, null, false);
        MainList.addHeaderView(headerView);
        
        ListAdapter = new FCUserTagAdapter(ListIndex,this);
        MainList.setAdapter(ListAdapter);
        MainList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        m_WalkupCheck = (CheckBox)findViewById(R.id.fc_user_tag_header_walkup);
        
        if(m_WalkupCheck!=null)
        {
        	if(appState.IsUserWalkup())
        	{
        		m_WalkupCheck.setChecked(true);
        	}
        	else
        	{
        		m_WalkupCheck.setChecked(false);
        	}
        	m_WalkupCheck.setOnCheckedChangeListener(new OnCheckedChangeListener()
        	{
        		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        		{
        			CreateListIndex();
        		}
        	});
        }
        
    	Object retained = getLastNonConfigurationInstance(); 
    	if(retained instanceof FreewayCoffeeUserTagAsyncGet)
		{
    		AsyncGet = (FreewayCoffeeUserTagAsyncGet)retained;
    		showProgressDialog(getString(R.string.fc_updating_tag_info));// This must be before setActivity as that calls ProcessXML which destroys the dialog --- so we dont want to just re-create it again
    		AsyncGet.SetActivity(this);
    		
		}
    	else
    	{
    		AsyncGet = null;
    		if(NeedDownloadCarData()==true)
    		{
    			DownloadCarData();
    		}
    		else
    		{
    			ShowGUI();
    		}
    	}
	}
	
	protected Dialog onCreateDialog (int id, Bundle args)
	{
		switch(id)
		{
		case ERROR_DIALOG:
			return new AlertDialog.Builder(this)
            .setTitle("User Tag")
            .setMessage(appState.GetLastErrorDisplayString())
            .setPositiveButton(R.string.fc_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    /* User clicked OK so do some stuff */
                	dismissDialog(ERROR_DIALOG);
                }
            })
            .create();
		}
		return null;
	}
	private void ShowGUI()
	{
		CarData=appState.GetCarDataBeingEdited();
		if(CarData==null)
		{
			CarData=new FreewayCoffeeCarMakeModelColorTagHolder();
			
			String DataStr = appState.GetUserCarData().get(FreewayCoffeeXMLHelper.USER_CAR_COLOR_ID_ATTR);
			try
			{
				CarData.ColorID=Integer.parseInt(DataStr);
			}
			catch(NumberFormatException e)
			{
				CarData.ColorID=0;
			}
			DataStr = appState.GetUserCarData().get(FreewayCoffeeXMLHelper.USER_CAR_MAKE_ID_ATTR);
			try
			{
				CarData.MakeID=Integer.parseInt(DataStr);
			}
			catch(NumberFormatException e)
			{
				CarData.MakeID=0;
			}
			DataStr = appState.GetUserCarData().get(FreewayCoffeeXMLHelper.USER_CAR_MODEL_ID_ATTR);
			try
			{
				CarData.ModelID=Integer.parseInt(DataStr);
			}
			catch(NumberFormatException e)
			{
				CarData.ModelID=0;
			}
			
			
			CarData.Tag=appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_TAG_ATTR);
			
			appState.SetCarDataBeingEdited(CarData);
			
		}
		CreateListIndex();
		
    	//TextView Banner = (TextView)findViewById(R.id.fc_banner_text);
    	//Banner.setText(DisplayStr);
    	
		
		//UserTagEdit = (EditText)findViewById(R.id.fc_user_tag_edit);
		//UserTagEdit.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_TAG_ATTR));
	}
	
	private void CreateListIndex()
	{
		
		ListIndex.clear();
		
		if(m_WalkupCheck.isChecked())
		{
			ListAdapter.notifyDataSetChanged();
			return; // No list, just the Walkup
		}
		
		// Make
		FCUserTagIndexPair Item = new FCUserTagIndexPair();
		Item.EntryType=LIST_ENTRY_MAKE;
		Item.EntryID = CarData.MakeID;
		ListIndex.add(Item);
		
		// Model
		Item = new FCUserTagIndexPair();
		Item.EntryType=LIST_ENTRY_MODEL;
		Item.EntryID = CarData.ModelID;
		ListIndex.add(Item);
		
		// Color
		Item = new FCUserTagIndexPair();
		Item.EntryType=LIST_ENTRY_COLOR;
		Item.EntryID = CarData.ColorID;
		ListIndex.add(Item);
		
		// License
		Item = new FCUserTagIndexPair();
		Item.EntryType=LIST_ENTRY_TAG;
		Item.EntryID = 0;
		ListIndex.add(Item);
		ListAdapter.notifyDataSetChanged();
	}
	
	private boolean NeedDownloadCarData()
	{
		if(appState.GetCarMakeModelData().IsDataPopulated()==true)
		{
			return false;
		}
		return true;
	}
	
	private void DownloadCarData()
	{
		// TODO Add a message ID to constructor because we have more than one thing we can do on the network now.
		AsyncGet = new FreewayCoffeeUserTagAsyncGet(this,appState,getString(R.string.fc_downloading_car_make_model_data));
		String CommandStr = appState.MakeGetCarDataURL();	
		
		AsyncGet.execute(CommandStr);
	}
	
	protected void onDestroy ()
	{
		 super.onDestroy();
		 //AsyncGet.cancel(true);
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
			 return AsyncGet;
		 }
		 return null;
	 }
	 
	 public void showProgressDialog(String Message)
	 {
		 TagProgress = ProgressDialog.show(this, "",Message,true);
				 			
	 }
	 
	public void UserCanceled(View V)
	{
		setResult(RESULT_CANCELED);
		finish();
	}
	
	private void DisplayError()
	{
		Toast Err = Toast.makeText(appState.getApplicationContext(),
				getString(R.string.fc_tag_error),
				Toast.LENGTH_SHORT);
    	Err.show();
	}
	
	public void DoUpdateUserTag(View V)
	{
		/* License is for now optional
		if(TagStr.length()==0)
		{
			DisplayError();
			return;
			
		}
		*/
		try
		{
			AsyncGet = new FreewayCoffeeUserTagAsyncGet(this,appState,getString(R.string.fc_updating_tag_info));
			String CommandStr = appState.MakeUpdateTagAndCarDataURL(m_WalkupCheck.isChecked());
		
			AsyncGet.execute(CommandStr);
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO
			DisplayNetworkError();
			return;
		}
	}
	
	 
	@Override
   	public void onConfigurationChanged(Configuration newConfig) 
   	{
   	    super.onConfigurationChanged(newConfig);
   	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   	}
	
	public void ProcessXMLResult(String XML)
    {
		int len=XML.length();

		//Log.w("FreewayCoffeeDB",XML);
		/* Get a SAXParser from the SAXPArserFactory. */
		SAXParserFactory spf = SAXParserFactory.newInstance();
		AsyncGet=null;
		try
		{
			SAXParser sp = spf.newSAXParser();

            /* Get the XMLReader of the SAXParser we created. */
			XMLReader xr = sp.getXMLReader();
            /* Create a new ContentHandler and apply it to the XML-Reader*/
			TagXMLHandler = new FreewayCoffeeUserTagXMLHandler(appState);
			xr.setContentHandler(TagXMLHandler);

           // Parse the xml-data from our URL.
			xr.parse(new InputSource(new StringReader(XML)));
           /* Parsing has finished. */
			java.util.Date ParseTime = new java.util.Date();
			
			
			if(TagProgress!=null)
			{
				TagProgress.dismiss();
				TagProgress=null;
			}
			if(TagXMLHandler.NetworkError==true)
			{
				DisplayNetworkError();
				return;
			}
			if(TagXMLHandler.signonResponse!=null)
			{
				// TODO -=- in general, what do we do for these sub-views if we get say a SIGNON_OK.
				// Cannot just reload indefinitely. Can I even call reload here ?
				// Return to the parent view and try to let that one refresh ? Lets punt it for now.

				// Tell the parent view that some critical login-type thing has happened!
				setResult(FreewayCoffeeItemListView.RESULT_CODE_NOT_LOGGED_IN);
				
				this.finish();
			}
			else if(TagXMLHandler.MakeModelDataResponse==true)
			{
				
				ShowGUI();
				
			}
			else if(TagXMLHandler.updateTagResponse.equals("ok"))
			{
				/*
        	  Toast SuccessToast = Toast.makeText(appState.getApplicationContext(),
        			  							 getString(R.string.fc_tag_updated), 
        			  							 Toast.LENGTH_SHORT);
        	  SuccessToast.show();
        	  */
        	  try
        	  {
        		  if(m_WalkupCheck.isChecked())
        		  {
        			  appState.SetUserArriveMode(FCXMLHelper.ARRIVE_MODE_WALKUP_STR);
        		  }
        		  else
        		  {
        			  appState.SetUserArriveMode(FCXMLHelper.ARRIVE_MODE_CAR_STR);
        		  }
        		  // TODO -- make a real API or class or something for gods sakes
        		  appState.GetUserInfoData().put(FreewayCoffeeItemListView.USER_TAG_ATTR,TagXMLHandler.Tag);
        		  EndActivity(RESULT_OK);
        	  }
        	  catch (NumberFormatException e)
        	  {
        		  // TODO sub optional
        		  //Toast ErrorToast = Toast.makeText(appState.getApplicationContext(),"I have absolutely no idea if your Tag was put in the database or not",Toast.LENGTH_SHORT);
            	  //ErrorToast.show();
            	  appState.SetLastError(FreewayCoffeeXMLHelper.APP_ERROR_MAJOR_INTERNAL,FreewayCoffeeXMLHelper.APP_ERROR_INTERNAL_MINOR_NUMBER_EXCEPTION,
            			                "AC: UserTag: ProcessXML: Number Format Exception Occurred","A Number conversion failed. Please send Feedback");
            	  showDialog(ERROR_DIALOG);
            	  return;
        	  }
        	  
        	  
			}
			else
			{
				// TODO Obviously could do better here.
				//Toast ErrorToast = Toast.makeText(appState.getApplicationContext(),"Something Terrible Happened When Adding Your Tag But I am unable to tell you what",Toast.LENGTH_SHORT);
				//ErrorToast.show();
				appState.SetLastError(FreewayCoffeeXMLHelper.APP_ERROR_MAJOR_INTERNAL,FreewayCoffeeXMLHelper.APP_ERROR_NETWORK_RESULT_NOT_OK,
						              "AC: UserTag: ProcessXML: result=" + TagXMLHandler.updateTagResponse,"A network error occurred: " + TagXMLHandler.updateTagResponse);
				showDialog(ERROR_DIALOG);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		//CreateAndDisplay();
		if(resultCode==FreewayCoffeeItemListView.RESULT_CODE_NOT_LOGGED_IN)
		{
			// Forced logout by admin, or some other weird error
			setResult(FreewayCoffeeItemListView.RESULT_CODE_NOT_LOGGED_IN);
			finish();	
		}
		else if(resultCode==RESULT_OK)
		{
			// Update our local state from the global
			CarData=appState.GetCarDataBeingEdited();
			CreateListIndex();
			ListAdapter.notifyDataSetChanged();
		}
	}
	
	private void DisplayNetworkError()
    {
		if(TagProgress!=null)
		{
			TagProgress.dismiss();
			TagProgress=null;
		}
    	Toast Err = Toast.makeText(appState.getApplicationContext(),
				getString(R.string.fc_network_error),
				Toast.LENGTH_SHORT);
    	Err.show();
    }
	
	private void EndActivity(Integer Result)
	{
		setResult(Result);
		finish();
	}
	 private class FCUserTagAdapter extends BaseAdapter
	  {
			private ArrayList<FCUserTagIndexPair> Items;
			private LayoutInflater mInflater;
			
			public FCUserTagAdapter(ArrayList<FCUserTagIndexPair> items, Context context)
			{
					             
				Items = items;
				mInflater = LayoutInflater.from(context);
			}
			
			@Override
			public int getCount()
			{
				return Items.size();
			}
			  	 
			@Override
			public Object getItem(int position)
			{
				return Items.get(position);
			}
			  
			@Override
			public long getItemId(int position)
			{
				return position;
			}
			  
			private class OnItemClickListener implements View.OnClickListener
			{           	  
				private int mPosition;
				OnItemClickListener(int position)
				{
					mPosition = position;
				}
				@Override
				public void onClick(View arg0)
				{
					// Log.v(TAG, "onItemClick at position" + mPosition);     
					Intent intent = new Intent();
					FCUserTagIndexPair Item = ListIndex.get(mPosition);
					
			        if(Item.EntryType==LIST_ENTRY_MAKE  ||  Item.EntryType==LIST_ENTRY_COLOR)
			        {
			        	intent.putExtra(OPTION_PICK_TYPE,Item.EntryType);
			        	
          				intent.setClassName(FreewayCoffeeUserTagActivity.this, FreewayCoffeeCarOptionActivity.class.getName());
          				startActivityForResult(intent,Item.EntryType);
			        }
			        else if(Item.EntryType == LIST_ENTRY_MODEL)
			        {
			        	
			        	FreewayCoffeeCarMake Make = appState.GetCarMakeModelData().GetCarMake(CarData.MakeID);
			        	if((Make!=null) && (Make.DoesMakeHaveModels()==true))
			        	{
			        		intent.putExtra(OPTION_PICK_TYPE,Item.EntryType);
				        	
	          				intent.setClassName(FreewayCoffeeUserTagActivity.this, FreewayCoffeeCarOptionActivity.class.getName());
	          				startActivityForResult(intent,Item.EntryType);
			        	}
			        }
			        else if(Item.EntryType==LIST_ENTRY_TAG)
			        {
			        	
			        	intent.putExtra(OPTION_PICK_TYPE,Item.EntryType);
			        	intent.setClassName(FreewayCoffeeUserTagActivity.this, FreewayCoffeeLicensePlateActivity.class.getName());
          				startActivityForResult(intent,Item.EntryType);
 	
			        }               
			    }
			}
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
			  
				// A FCListViewHolder keeps references to children views to avoid unneccessary calls
				// to findViewById() on each row.
				FCuserTagHolder holder;
				  
				// When convertView is not null, we can reuse it directly, there is no need
				// to reinflate it. We only inflate a new View when the convertView supplied
				// by ListView is null
				if (convertView == null)
				{
					convertView = mInflater.inflate(R.layout.fc_user_tag_list_row, null);
					// Creates a ViewHolder and store references to the two children views
					// we want to bind data to.
					holder = new FCuserTagHolder();
					holder.MainText = (TextView) convertView.findViewById(R.id.fc_user_tag_row_main_text);
					holder.RightImage = (ImageView)convertView.findViewById(R.id.fc_user_tag_row_right_img);
					//holder.Position=position;
					
					convertView.setOnClickListener(new OnItemClickListener(position));
					convertView.setTag(holder);
				                     
				}
				else
				{
					// Get the ViewHolder back to get fast access to the TextView
					// and the ImageView.
					holder = (FCuserTagHolder) convertView.getTag();
					// Make sure we set On-click here because when a view is re-used when removing, it may have a different Position so we need to update that.
					convertView.setOnClickListener(new OnItemClickListener(position));
					//holder.Position=position;
					//holder.EditButton.setTag(holder); // So we can tell what row for later
					//holder.RemoveButton.setTag(holder); 
				}
				  	              
				UpdateItem(position,holder);
				 
				  
				return convertView;

			  }
			  
			  private void UpdateItem(Integer position,FCuserTagHolder holder)
			  {
				  FCUserTagIndexPair ListItem= ListIndex.get(position);
				  if(ListItem.EntryType==LIST_ENTRY_MAKE)
				  {
					  String MakePrefix = getString(R.string.make_prefix);
					  
					  FreewayCoffeeCarMake Make = appState.GetCarMakeModelData().GetCarMake(ListItem.EntryID);
					  if(Make==null || (Make.IsNone()==true) )
					  {
						  holder.MainText.setTextColor(getResources().getColor(R.color.Black));
						  String SelectText = getString(R.string.fc_user_tag_select_a_make);
						  holder.MainText.setText(MakePrefix + SelectText);
						  //holder.MainText.setTextColor(getResources().getColor(R.color.DarkGray));
						  holder.RightImage.setVisibility(ImageView.VISIBLE);
						  holder.RightImage.setImageResource(R.drawable.menuright);
					  }
					  else
					  {
						  holder.MainText.setText(MakePrefix + Make.GetMakeLongDescr());
						  holder.MainText.setTextColor(getResources().getColor(R.color.Black));
						  holder.RightImage.setVisibility(ImageView.VISIBLE);
						  holder.RightImage.setImageResource(R.drawable.menuright);
					  }
				  }
				  else if(ListItem.EntryType==LIST_ENTRY_MODEL)
				  {
					  FreewayCoffeeCarMake Make = appState.GetCarMakeModelData().GetCarMake(CarData.MakeID);
					  String ModelPrefix = getString(R.string.model_prefix);
					  holder.MainText.setTextColor(getResources().getColor(R.color.Black));
					  if(Make==null || (Make.IsNone()==true) )
					  {
						  String SelectText = getString(R.string.fc_user_tag_select_a_make_first);
						  holder.MainText.setText(ModelPrefix + SelectText);
						  //holder.MainText.setTextColor(getResources().getColor(R.color.DarkGray));
						  holder.RightImage.setVisibility(ImageView.INVISIBLE);
					  }
					  else
					  {
						  if(Make.GetMakeCanHaveModels()==false)
						  {
							  String SelectText = getString(R.string.fc_user_tag_make_cannot_have_models);
							  holder.MainText.setText(ModelPrefix + SelectText);
							  holder.RightImage.setVisibility(ImageView.INVISIBLE);
						  }
						  else if((Make.GetMakeCanHaveModels()==true) && (Make.GetNumberOfModels()==0))
						  {
							  String SelectText = getString(R.string.fc_user_tag_make_does_not_have_models);
							  holder.MainText.setText(ModelPrefix + SelectText);
							  holder.RightImage.setVisibility(ImageView.INVISIBLE);
						  }
						  else
						  {
							  holder.MainText.setTextColor(getResources().getColor(R.color.Black));
							  FreewayCoffeeCarModel Model = Make.GetModel(ListItem.EntryID);
							  if(Model==null || (Model.IsNone()))
							  {
								  String SelectText = getString(R.string.fc_user_tag_select_a_model);
								  holder.MainText.setText(ModelPrefix + SelectText);
								  //holder.MainText.setTextColor(getResources().getColor(R.color.DarkGray));
								  holder.RightImage.setVisibility(ImageView.VISIBLE);
								  holder.RightImage.setImageResource(R.drawable.menuright);
							  }
							  else
							  {
								  holder.MainText.setText(ModelPrefix + Model.GetModelLongDescr());
								  holder.MainText.setTextColor(getResources().getColor(R.color.Black));
								  holder.RightImage.setVisibility(ImageView.VISIBLE);
								  holder.RightImage.setImageResource(R.drawable.menuright);
							  }
						  }
					  }
					  
				  }
				  else if(ListItem.EntryType==LIST_ENTRY_COLOR)
				  {
					  String ColorPrefix = getString(R.string.color_prefix);
					  
					  holder.RightImage.setVisibility(ImageView.VISIBLE);
					  holder.RightImage.setImageResource(R.drawable.menuright);
					  
					  FreewayCoffeeCarColor Color = appState.GetCarMakeModelData().GetCarColor(ListItem.EntryID);
					  holder.MainText.setTextColor(getResources().getColor(R.color.Black));
					  if(Color==null || (Color.IsNone()))
					  {
						  String SelectText = getString(R.string.user_tag_select_a_color);
						  
						  holder.MainText.setText(ColorPrefix + SelectText);
						  //holder.MainText.setTextColor(getResources().getColor(R.color.DarkGray));
					  }
					  else
					  {
						  holder.MainText.setText(ColorPrefix + Color.GetColorLongDescr());
						  holder.MainText.setTextColor(getResources().getColor(R.color.Black));
					  }
				  }
				  else if(ListItem.EntryType==LIST_ENTRY_TAG)
				  {
					  String LicensePrefix = getString(R.string.license_prefix);
					  holder.RightImage.setVisibility(ImageView.VISIBLE);
					  holder.RightImage.setImageResource(R.drawable.menuright);
					  
					  holder.MainText.setCompoundDrawables(appState.getResources().getDrawable(R.drawable.menuright),null,null,null);
					  holder.MainText.setTextColor(getResources().getColor(R.color.Black));
					  if(CarData.Tag==null || (CarData.Tag.equals("")) || CarData.Tag.equals("NONE"))
					  {
						  String SelectText = getString(R.string.user_tag_enter_a_license_plate);
						  holder.MainText.setText(LicensePrefix + SelectText);
						 // holder.MainText.setTextColor(getResources().getColor(R.color.DarkGray));
					  }
					  else
					  {
						  holder.MainText.setText(LicensePrefix + CarData.Tag);
						  holder.MainText.setTextColor(getResources().getColor(R.color.Black));
					  }
				  }
				  			  
				  
				 // holder.SelectButton.setText("Select");
			  }
			  
	}
	 private class FCuserTagHolder
	 {
		 TextView MainText;
		 ImageView RightImage;
	 }
	 
	private class FCUserTagIndexPair
	{
		Integer EntryType;
		Integer EntryID;
	}
}
