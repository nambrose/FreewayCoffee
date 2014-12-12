package com.freewaycoffee.client;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.methods.HttpPost;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.freewaycoffee.client.FreewayCoffeeItemListView.SubActivityTypes;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


// Allows the customer to edit an existing drink, or add an entirely new one

public class FreewayCoffeeDrinkAddEditActivity extends Activity 
{
	private FreewayCoffeeApp appState;
	private FreewayCoffeeUserDrink DrinkInProgress;
	private ListView listView;
	
	private ArrayList <FreewayCoffeeFoodDrinkOptionListItem> ListIndex;
	private Integer DrinkType; // From DrinkAdd
	private Integer EditDrinkID; // From DrinkPicker
	private FreewayCoffeeFoodDrinkType DrinkTypeInfo;
	
	private FCDrinkAddEditdapter myAdapter;
	private EditText DrinkNameWidget;
	private CheckBox AlwaysIncludeWidget;
	FreewayCoffeeAddEditDrinkXMLHandler AddEditDrinkXMLHandler;
	FreewayCoffeeDrinkAddEditAsyncPost AsyncGet;
	ProgressDialog AddEditProgress;
	private Button AddEditButton;
	
	static public final String SUB_ACTIVITY_EXTRA_OPTIONS_TEXT="sub_activity_options_text";
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		 super.onCreate(savedInstanceState);
	        
		 AsyncGet=null;
	     appState = ((FreewayCoffeeApp)getApplicationContext());
	     AddEditProgress=null;
	     DrinkNameWidget=null;
	     AlwaysIncludeWidget=null;
	     
	     setContentView(R.layout.fc_drink_add_edit_list);
	  
         AlwaysIncludeWidget=null;
         
         DrinkType = getIntent().getIntExtra(FreewayCoffeeItemListView.USER_ADD_DRINK_TYPE,-1);
	     EditDrinkID = getIntent().getIntExtra(FreewayCoffeeItemListView.USER_EDIT_DRINK_ID,-1);
	     
	     
	     
	     if(EditDrinkID==-1)
	     {
	    	 DrinkInProgress = appState.MakeAndSetNewEmptyDrink(DrinkType); // Reference is stored in appState for later.
	    	 
	     }
	     else
	     {
	    	 DrinkInProgress = appState.SetDrinkInProgress(EditDrinkID);
	     }
	     
	     
	     if(DrinkType==-1 && EditDrinkID>0)
	     {
	    	 DrinkType=DrinkInProgress.GetDrinkTypeID(); 
	     }
	     
	     listView = (ListView)findViewById(R.id.fc_drink_add_edit_list_object);
	     
	     // List header and footer used to avoid trying to stuff ListView into a ScrollView (it doesn't like that like a cat doesn't like taking a shower)
	     View footerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fc_drink_add_edit_list_footer, null, false);
	     listView.addFooterView(footerView);
		     
	     //View headerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fc_drink_add_edit_list_header, null, false);
	     View headerView = getLayoutInflater().inflate(R.layout.fc_drink_add_edit_list_header, null);
	     listView.addHeaderView(headerView);
		  
	     TextView UsernameView = (TextView)findViewById(R.id.fc_banner_text);
	     String DrinksString = getString(R.string.fc_build_your_drink);
	      
	     // TODO move USER_INFO_NAME_KEY to a better place
	     UsernameView.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + ", " + DrinksString);
	         
	     AddEditButton = (Button)findViewById(R.id.fc_drink_add_edit_doit);
	     DrinkNameWidget = (EditText) findViewById(R.id.fc_add_edit_drink_name_edit);
	     
	     if(DrinkNameWidget!=null)
	     {
	    	 //FILT InputFilter[] DrinkNameFilts = new InputFilter[]{appState.GetEditTextInputFilter()};
	         //FILT DrinkNameWidget.setFilters(DrinkNameFilts);
	         //FILT DrinkNameWidget.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

	     }
	     AlwaysIncludeWidget = (CheckBox) findViewById(R.id.fc_add_edit_drink_always_include);
	         
	         
	     // TODO --- what if we have garbage DrinkID/Type here ? Exit ? Display some error ???
	     
        Object retained = getLastNonConfigurationInstance();
 		
 		if(retained instanceof FreewayCoffeeDrinkAddEditAsyncPost)
 		{
 			// Get the saved state -- the Async Get with the XML and the ListIndex.
 			// WHen we set the new activity, it will call Process XML for us
 			AsyncGet = (FreewayCoffeeDrinkAddEditAsyncPost)retained;
 			showProgressDialog(AsyncGet.GetProgressMessage());// This must be before setActivity as that calls ProcessXML which destroys the dialog --- so we dont want to just re-create it again
 			AsyncGet.SetActivity(this);
 		}
 		else
 		{
 			AsyncGet = null;
 			ListIndex=null;
 			GetOrUpdate();
 		}
     
	    
	}
	
	private void DisplayList()
	{
		DrinkTypeInfo = appState.GetDrinkTypes().get(DrinkType);
		CreateListIndex();
		TextView DrinkTypeView = (TextView)findViewById(R.id.fc_add_edit_drink_type_edit);
        DrinkTypeView.setText(appState.GetDrinkTypeNameFromDrinkTypeID(DrinkType));
        if(EditDrinkID==-1)
	    {
	    	 // Add
	    	 
	    	 AddEditButton.setText(getString(R.string.fc_add_drink));
	    	 AlwaysIncludeWidget.setChecked(true);
	    }
        else
        {
        	
       	 	AddEditButton.setText(getString(R.string.fc_save_drink));
       	 	if(DrinkInProgress.GetIncludeDefault()==true)
       	 	{
       	 		AlwaysIncludeWidget.setChecked(true);
	    	}
       	 	else
       	 	{
       	 		AlwaysIncludeWidget.setChecked(false);
       	 	}
       	 	String DrinkName = DrinkInProgress.GetUserDrinkName();
       	 	
       	 	if(DrinkName!=null)
       	 	{
       	 		DrinkNameWidget.setText(DrinkName);
       	 	}
       	 	
        }
        
        myAdapter = new FCDrinkAddEditdapter(ListIndex,this);
        listView.setAdapter(myAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
	}
	
	private void GetOrUpdate()
	{
		if(appState.IsDrinksMenuLoaded())
		{
			DisplayList();
		}
		else
		{
			ExecuteGet();
		}
	}
	
	private void ExecuteGet()
    {
		// Make a Get
        AsyncGet = new FreewayCoffeeDrinkAddEditAsyncPost(this,appState,getString(R.string.fc_retrieving_drinks_menu));
        AsyncGet.execute(appState.MakeGetDrinkPickListURL());
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
			//AsyncGet.SavedListIndex=ListIndex;
			return AsyncGet;
		}
		return null;
	}

	public void showProgressDialog(String Message)
	{
		 AddEditProgress = ProgressDialog.show(this, "",Message, true);
	
	}
	
	private Integer GetIndexOfFirstOptionToFix()
	{
		for(int index=0;index<ListIndex.size();index++)
		{
			FreewayCoffeeFoodDrinkOptionListItem IndexEntry=ListIndex.get(index);
			if( (IndexEntry.IsMandatory==true) && (IndexEntry.IsNone==true))
			{
				return index;
			}
			
		}
		return -1;
		
	}
	
	private boolean AreAllMandatoryOptionsSelected()
	{
		for(int index=0;index<ListIndex.size();index++)
		{
			FreewayCoffeeFoodDrinkOptionListItem IndexEntry=ListIndex.get(index);
			if( (IndexEntry.IsMandatory==true) && (IndexEntry.IsNone==true))
			{
				return false;
			}
			
		}
		return true;
	}
	
	private FreewayCoffeeFoodDrinkOptionListItem CreateIndexEntry(FreewayCoffeeFoodDrinkOptionGroup OptionGroup)
	{
		FreewayCoffeeFoodDrinkOptionListItem IndexEntry=new FreewayCoffeeFoodDrinkOptionListItem();
		
		IndexEntry.OptionGroupRef=OptionGroup;
		IndexEntry.OptionGroup=OptionGroup.GetGroupID();
		IndexEntry.FoodDrinkOptionDescr=OptionGroup.GetGroupName();
		IndexEntry.PickType=OptionGroup.GetSelectionType();
		IndexEntry.SortOrder = OptionGroup.GetSortOrder();
		IndexEntry.EntryType=FreewayCoffeeFoodDrinkOptionListItem.OPTION_TYPE_FOOD_DRINK_OPTION;
		
		IndexEntry.OptionValue = DrinkInProgress.MakeOptionValueStringForOptionGroup(appState,IndexEntry.OptionGroup);
		IndexEntry.FoodDrinkOptionPrice = DrinkInProgress.MakeOptionCostForOptionGroup(appState,IndexEntry.OptionGroup);
		if( (IndexEntry.OptionValue==null) || (IndexEntry.OptionValue.length()==0) )
		{
			IndexEntry.OptionValue=getString(R.string.none_text);
			IndexEntry.IsNone=true;
		}
		else
		{
			IndexEntry.IsNone=false;
		}
		
		if(IndexEntry.EntryType==FreewayCoffeeFoodDrinkOptionListItem.OPTION_TYPE_EXTRA_OPTIONS)
		{
			IndexEntry.IsMandatory=false;
		}
		else if(appState.IsOptionMandatoryForDrinkType(DrinkType, IndexEntry.OptionGroup))
		{
			IndexEntry.IsMandatory=true;
		}
		else
		{
			IndexEntry.IsMandatory=false;
		}
		
		
		return IndexEntry;
	}
	
	private void CreateListIndex()
	{
		if(ListIndex==null)
		{
			if(AsyncGet!=null)
			{		
				if(AsyncGet.SavedListIndex!=null)				
				{
					ListIndex = new ArrayList <FreewayCoffeeFoodDrinkOptionListItem>(AsyncGet.SavedListIndex);
					Collections.sort(ListIndex,FreewayCoffeeFoodDrinkOptionListItem.GetDisplayComparator()); // Sort here just in case.
			 
					return; // Dont re-create the index.
				}
			}
			else
			{
				ListIndex = new ArrayList<FreewayCoffeeFoodDrinkOptionListItem>();
			}
		}
		else
		{
			ListIndex.clear();
		}
		
		FreewayCoffeeFoodDrinkOptionListItem IndexEntry=null;
		
		for (Map.Entry<Integer,FreewayCoffeeFoodDrinkOptionGroup> Group : appState.GetDrinkOptionGroups().entrySet())
 		{
			// OptionType is badly named. Its milks, sizes etc
			Integer OptionGroup = Group.getKey();
			
			if(DrinkTypeInfo.AreAnyOptionsValidForDrinkOptionGroup(OptionGroup)==true)
			{
				IndexEntry = CreateIndexEntry(Group.getValue());		
				ListIndex.add(IndexEntry);
			}
 		}
		// Now add an entry for Extra Options
		IndexEntry = new FreewayCoffeeFoodDrinkOptionListItem();
		IndexEntry.EntryType = FreewayCoffeeFoodDrinkOptionListItem.OPTION_TYPE_EXTRA_OPTIONS;
		IndexEntry.FoodDrinkOptionDescr = DrinkInProgress.GetUserDrinkExtra();
		if(IndexEntry.FoodDrinkOptionDescr==null)
		{
			IndexEntry.FoodDrinkOptionDescr="";
		}
		ListIndex.add(IndexEntry);
		
		Collections.sort(ListIndex,FreewayCoffeeFoodDrinkOptionListItem.GetDisplayComparator());
		
	}

	@Override
   	public void onConfigurationChanged(Configuration newConfig) 
   	{
   	    super.onConfigurationChanged(newConfig);
   	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   	}
	   
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		//CreateAndDisplay();
		if(resultCode==FreewayCoffeeItemListView.RESULT_CODE_NOT_LOGGED_IN)
		{
			setResult(FreewayCoffeeItemListView.RESULT_CODE_NOT_LOGGED_IN);
			finish();
		}
		else if(resultCode==RESULT_OK)
		{
			Integer ListPosition = data.getIntExtra(FreewayCoffeeItemListView.PARENT_LIST_POSITION, -1); // TODO ERROR CHECK
			if( (ListPosition <0 ) || (ListIndex.size()==0) || (ListPosition>ListIndex.size()-1) )
			{
				// NOTE LOG TODO README FIXME
				return;
			}
			FreewayCoffeeFoodDrinkOptionListItem Item = ListIndex.get(ListPosition);
			
			if(requestCode == FreewayCoffeeFoodDrinkOptionListItem.OPTION_TYPE_FOOD_DRINK_OPTION)
			{
				//String OptionSelected = data.getStringExtra(FreewayCoffeeItemListView.USER_SELECTED_DRINK_OPTION_DATA_ID);
				// SHould be safest ? CreateListIndex();
				
				FreewayCoffeeFoodDrinkOptionListItem IndexEntry=CreateIndexEntry(Item.OptionGroupRef);
				if(IndexEntry!=null)
				{
					ListIndex.set(ListPosition,IndexEntry);
				}
				
				/*
				Item.OptionValueID=OptionSelected;
				
				Item.OptionValue=Option.GetOptionValueFromID(Item.OptionValueID);
				if(Item.OptionValue==null)
				{
					Item.OptionValue=getString(R.string.none_text);
				}
				 */
			}
			else if(requestCode == FreewayCoffeeFoodDrinkOptionListItem.OPTION_TYPE_EXTRA_OPTIONS)
			{
				String Extra = data.getStringExtra(SUB_ACTIVITY_EXTRA_OPTIONS_TEXT);
				DrinkInProgress.SetUserDrinkExtra(Extra);
				Item.FoodDrinkOptionDescr=Extra;
				
				
			}
			myAdapter.notifyDataSetChanged();
		}
	}
	
	
	public void DoAction (View v)
    {
		Integer OptionToFix=-1;
		
		if(AreAllMandatoryOptionsSelected()!=true)
		{
			OptionToFix = GetIndexOfFirstOptionToFix();
			
			String ErrorStr = getString(R.string.fc_please_select_a) + ": ";
			if( (OptionToFix>=0) && ((int)OptionToFix<ListIndex.size()) )
			{
				ErrorStr+= ListIndex.get(OptionToFix).FoodDrinkOptionDescr;
			}
			else
			{
				ErrorStr+="Internal Error(Bad Day)";
			}
			Toast ErrorToast = Toast.makeText(appState.getApplicationContext(),ErrorStr,
						Toast.LENGTH_SHORT);
			ErrorToast.show();
			
			return;
		}
		
		String DrinkName = DrinkNameWidget.getText().toString();
		
		DrinkInProgress.SetUserDrinkName(DrinkName);
		
		// Always Include
		if(AlwaysIncludeWidget.isChecked())
		{
			DrinkInProgress.SetIncludeDefault(true);
			
		}
		else
		{
			DrinkInProgress.SetIncludeDefault(false);
			
		}
		
		try
		{
			HttpPost Command = appState.MakeAddEditDrinkURL(DrinkInProgress);
			String ProgressMessage;
			
			// TODO, this is not the best way to determine if its an add or an edit. Oh well
			if(EditDrinkID==-1)
			{
				ProgressMessage = getString(R.string.fc_adding_your_item);
			}
			else
			{
				ProgressMessage = getString(R.string.fc_editing_your_item);
			}
			AsyncGet = new FreewayCoffeeDrinkAddEditAsyncPost(this,appState,ProgressMessage);
			AsyncGet.execute(Command);
		}
		
		catch (UnsupportedEncodingException e)
		{
			// TODO
			DisplayNetworkError();
			return;
		}
		
    }
	
	
	private void DisplayNetworkError()
	{
		 if(AddEditProgress!=null)
         {
       	  AddEditProgress.dismiss();
       	  AddEditProgress=null;
         }
		Toast Err = Toast.makeText(appState.getApplicationContext(),
					getString(R.string.fc_network_error),
					Toast.LENGTH_SHORT);
		Err.show();
	}
	  
	public void ProcessXMLResult(String XML)
    {
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
			AddEditDrinkXMLHandler = new FreewayCoffeeAddEditDrinkXMLHandler(appState);
           xr.setContentHandler(AddEditDrinkXMLHandler);

           // Parse the xml-data from our URL.
           InputSource is = new InputSource(new StringReader(XML));
			//is.setEncoding("UTF-8");
			
			xr.parse(is);
           /* Parsing has finished. */
          if(AddEditProgress!=null)
          {
        	  AddEditProgress.dismiss();
        	  AddEditProgress=null;
          }
          
          if(AddEditDrinkXMLHandler.NetworkError==true)
  		  {
        	  DisplayNetworkError();
        	  return;
  		  }
          if(AddEditDrinkXMLHandler.signonResponse!=null)
          {
        	   // TODO -=- in general, what do we do for these sub-views if we get say a SIGNON_OK.
        	   // Cannot just reload indefinitely. Can I even call reload here ?
        	   // Return to the parent view and try to let that one refresh ? Lets punt it for now.

               // Tell the parent view that some critical login-type thing has happened!
        	   setResult(FreewayCoffeeItemListView.RESULT_CODE_NOT_LOGGED_IN);
        	   if(AsyncGet!=null)
				{
					 AsyncGet.UnlinkActivity();
					 AsyncGet=null;
				}
        	   
        	   this.finish();
          }
          
          if(AddEditDrinkXMLHandler.addDrinkResponse!=null)
          {
        	  if(AddEditDrinkXMLHandler.addDrinkResponse.equals("ok"))        	
        	  {
        		  Toast SuccessToast = Toast.makeText(appState.getApplicationContext(),"Item Added Successfully.",
        			  							Toast.LENGTH_SHORT);
        		  SuccessToast.show();
        		  try
        		  {
        			  if(AddEditDrinkXMLHandler.m_TheDrink!=null)
        			  {
        				  appState.AddUserDrink(AddEditDrinkXMLHandler.m_TheDrink);
        				  
        				  EndActivity(AddEditDrinkXMLHandler.m_TheDrink.GetUserDrinkID(),
        						  FreewayCoffeeItemListView.SubActivityTypes.FC_SUB_ACTIVITY_ADD_DRINK.ordinal());
        			  }
        			  else
        			  {
        				  Toast ErrorToast = Toast.makeText(appState.getApplicationContext(),"An Error Occurred Adding Your Item. Please try again",Toast.LENGTH_SHORT);
                		  ErrorToast.show();
        				  EndActivity(-1,FreewayCoffeeItemListView.SubActivityTypes.FC_SUB_ACTIVITY_ADD_DRINK.ordinal());
        			  }
        		  }
        		  catch (NumberFormatException e)
        		  {
        			  // TODO sub optional
        			  Toast ErrorToast = Toast.makeText(appState.getApplicationContext(),"An Error Occurred Adding Your Item. Please Try Again",Toast.LENGTH_SHORT);
        			  ErrorToast.show();
        			  return;
        		  }
        	  }
        	  else
        	  {
        		  // TODO Obviously could do better here.
        		  Toast ErrorToast = Toast.makeText(appState.getApplicationContext(),"An Error Occurred Adding Your Item. Please try again",Toast.LENGTH_SHORT);
        		  ErrorToast.show();
        		  return;
        	  }
          }
          else if(AddEditDrinkXMLHandler.editDrinkResponse!=null)
          {
        	  if(AddEditDrinkXMLHandler.editDrinkResponse.equals("ok"))        	
        	  {
        		  Toast SuccessToast = Toast.makeText(appState.getApplicationContext(),"You Item Was Successfully Edited.",
        			  							Toast.LENGTH_SHORT);
        		  SuccessToast.show();
        		  try
        		  {
        			  if(AddEditDrinkXMLHandler.m_TheDrink!=null)
        			  {
        				  appState.AddUserDrink(AddEditDrinkXMLHandler.m_TheDrink);
        			  
        				  EndActivity(AddEditDrinkXMLHandler.m_TheDrink.GetUserDrinkID(),
        						  FreewayCoffeeItemListView.SubActivityTypes.FC_SUB_ACTIVITY_EDIT_DRINK.ordinal());
        			  }
        			  else
        			  {
        				  Toast ErrorToast = Toast.makeText(appState.getApplicationContext(),"An Error Occurred Editing Your Item. Please try again",Toast.LENGTH_SHORT);
                		  ErrorToast.show();
        				  EndActivity(-1,FreewayCoffeeItemListView.SubActivityTypes.FC_SUB_ACTIVITY_ADD_DRINK.ordinal());
        			  }
        	 	}
        		  catch (NumberFormatException e)
        		  {
        			  // TODO sub optional
        			  Toast ErrorToast = Toast.makeText(appState.getApplicationContext(),"An Error Occurred Editing Your Item. Please try again",Toast.LENGTH_SHORT);
        			  ErrorToast.show();
        			  return;
        		  }
        	  }
        	  else
        	  {
        		  // TODO Obviously could do better here.
        		  Toast ErrorToast = Toast.makeText(appState.getApplicationContext(),"An Error Occurred Editing Your Item Please Try Again",Toast.LENGTH_SHORT);
        		  ErrorToast.show();
        		  return;
        	  }
          }
          else
          {
        	  // This was a Load of the Menu
        	  DisplayList();
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
	
	private void EndActivity(Integer DrinkID,Integer AddOrEdit)
	{
		Intent intent = new Intent();
		
		if(AddOrEdit==FreewayCoffeeItemListView.SubActivityTypes.FC_SUB_ACTIVITY_EDIT_DRINK.ordinal())
		{
			
		}
		else if(AddOrEdit == FreewayCoffeeItemListView.SubActivityTypes.FC_SUB_ACTIVITY_ADD_DRINK.ordinal())
		{
			intent.putExtra(FreewayCoffeeItemListView.USER_CHOSE_DRINK_ID,DrinkID);
		}
		else
		{
			// ERK TODO
		}
		setResult(RESULT_OK,intent);
		finish();
	}
	
	public void UserCanceled(View v)
    {
            setResult(RESULT_CANCELED, null);
            if(AsyncGet!=null)
            {
            	AsyncGet.cancel(true);
            	AsyncGet.UnlinkActivity(); // NULL out activity pointer. TODO do we need thread safety here ?
            }
            finish();
    }

	private class FCDrinkAddEditdapter extends BaseAdapter
	  {
			private ArrayList<FreewayCoffeeFoodDrinkOptionListItem> Items;
			private LayoutInflater mInflater;
			
			public FCDrinkAddEditdapter(ArrayList<FreewayCoffeeFoodDrinkOptionListItem> items, Context context)
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
			        	
			        	FreewayCoffeeFoodDrinkOptionListItem Item = ListIndex.get(mPosition);
			        	if(Item.EntryType==FreewayCoffeeFoodDrinkOptionListItem.OPTION_TYPE_FOOD_DRINK_OPTION)
			        	{
			                Intent intent = new Intent();
			                
			                // FIXME NULL CHeck
			            	intent.putExtra(FreewayCoffeeItemListView.USER_SELECT_DRINK_OPTION_ID,ListIndex.get(mPosition).OptionGroup);
			            	intent.putExtra(FreewayCoffeeItemListView.PARENT_LIST_POSITION,mPosition);
			            	intent.putExtra(FreewayCoffeeItemListView.DRINK_TYPE,DrinkType);
			            	
			            	intent.setClassName(FreewayCoffeeDrinkAddEditActivity.this, FreewayCoffeeDrinkOptionPickerActivity.class.getName());
			            	startActivityForResult(intent,FreewayCoffeeFoodDrinkOptionListItem.OPTION_TYPE_FOOD_DRINK_OPTION);
			        	}
			        	else if(Item.EntryType==FreewayCoffeeFoodDrinkOptionListItem.OPTION_TYPE_EXTRA_OPTIONS)
			        	{
			        		Intent intent = new Intent();
			        		intent.putExtra(FreewayCoffeeItemListView.PARENT_LIST_POSITION,mPosition);
			        		intent.putExtra(FreewayCoffeeDrinkAddEditActivity.SUB_ACTIVITY_EXTRA_OPTIONS_TEXT, Item.FoodDrinkOptionDescr);
			        		intent.setClassName(FreewayCoffeeDrinkAddEditActivity.this, FreewayCoffeeDrinkOptionExtraActivity.class.getName());
			        		startActivityForResult(intent,FreewayCoffeeFoodDrinkOptionListItem.OPTION_TYPE_EXTRA_OPTIONS);
			        		
			        	}
			        }               
			    }
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
			  
				// A FCListViewHolder keeps references to children views to avoid unneccessary calls
				// to findViewById() on each row.
				FCDrinkPickerHolder holder;
				  
				// When convertView is not null, we can reuse it directly, there is no need
				// to reinflate it. We only inflate a new View when the convertView supplied
				// by ListView is null
				if (convertView == null)
				{
					convertView = mInflater.inflate(R.layout.fc_drink_add_edit_list_row, parent,false);
					// Creates a ViewHolder and store references to the two children views
					// we want to bind data to.
					holder = new FCDrinkPickerHolder();
					holder.item_descr = (TextView) convertView.findViewById(R.id.fc_drink_add_edit_row_text);
					  
					//holder.item_icon = (ImageView) convertView.findViewById(R.id.fc_drink_add_edit_row_img);
					holder.item_cost=(TextView) convertView.findViewById(R.id.fc_add_edit_row_cost);
					holder.item_small_descr = (TextView) convertView.findViewById(R.id.fc_drink_add_edit_row_small_text);
					convertView.setOnClickListener(new OnItemClickListener(position));
					convertView.setTag(holder);
				                     
				}
				else
				{
					// Get the ViewHolder back to get fast access to the TextView
					// and the ImageView.
					holder = (FCDrinkPickerHolder) convertView.getTag();
					convertView.setOnClickListener(new OnItemClickListener(position));
				}
				  	              
				UpdateItem(position,holder);
				 
				  
				return convertView;

			  }
			  
			  private void UpdateItem(Integer position,FCDrinkPickerHolder holder)
			  {
				  FreewayCoffeeFoodDrinkOptionListItem IndexEntry = Items.get(position);
				  if(IndexEntry==null)
				  {
					  return;
				  }
			  
				  if(IndexEntry.EntryType==FreewayCoffeeFoodDrinkOptionListItem.OPTION_TYPE_FOOD_DRINK_OPTION)
				  {
					  holder.item_descr.setText(Items.get(position).FoodDrinkOptionDescr + ": " + Items.get(position).OptionValue);
					  //holder.item_small_descr.setText(appState.GetDrinkSmallTextFromID(Items.get(position).DrinkID));
					  if( 	( Items.get(position).FoodDrinkOptionPrice!=null) &&
							  (!Items.get(position).FoodDrinkOptionPrice.equals("")) &&
							  (!Items.get(position).FoodDrinkOptionPrice.equals("0.00")))
					  {
						  holder.item_cost.setText("$" + Items.get(position).FoodDrinkOptionPrice);
					  }
					  else
					  {
						  holder.item_cost.setText("");
					  }
					  
					  if( (IndexEntry.IsMandatory==true) && (IndexEntry.IsNone==true))
					  {
						  // Mandatory and Set to none = bad
						  holder.item_descr.setTextColor(getResources().getColor(R.color.Red));
					  }
					  else
					  {
						  holder.item_descr.setTextColor(getResources().getColor(R.color.Black));
					  }
					  

				  }
				  else if(Items.get(position).EntryType==FreewayCoffeeFoodDrinkOptionListItem.OPTION_TYPE_EXTRA_OPTIONS)
				  {
					  String ExtraStr = getString(R.string.fc_extra);
					  ExtraStr += ": ";
					  if( (Items.get(position).FoodDrinkOptionDescr==null) || (Items.get(position).FoodDrinkOptionDescr.length()==0) )
					  {
						  holder.item_descr.setText(ExtraStr + getString(R.string.none_text));
					  }
					  else
					  {
						  holder.item_descr.setText(ExtraStr + Items.get(position).FoodDrinkOptionDescr);
						  
					  }
					  holder.item_descr.setTextColor(getResources().getColor(R.color.Black));  
					  holder.item_cost.setText("");
				  }
				  //holder.item_icon.setImageResource(R.drawable.fc_drink);
			  }

			  

			  
			  class FCDrinkPickerHolder
			  {
				  TextView item_descr;
				  TextView item_small_descr;
				  TextView item_cost;
				  //ImageView item_icon;
				 
			  }
	}
}
