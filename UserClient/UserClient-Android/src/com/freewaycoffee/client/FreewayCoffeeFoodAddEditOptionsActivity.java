package com.freewaycoffee.client;

import android.app.Activity;

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

public class FreewayCoffeeFoodAddEditOptionsActivity extends Activity 
{
	private FreewayCoffeeApp appState;
	private HashMap<String, String> FoodInProgress;
	private ListView listView;
	
	private ArrayList <FreewayCoffeeFoodDrinkOptionListItem> ListIndex;
	private FreewayCoffeeFoodDrinkType FoodType; 
	private Integer FoodTypeID;
	private Integer EditFoodID;
	
	private FCFoodAddEditOptionsAdapter myAdapter;
	//private EditText FoodNameWidget;
	//private CheckBox AlwaysIncludeWidget;
	FreewayCoffeeFoodAddEditOptionsXMLHandler AddEditFoodOptionsXMLHandler;
	FreewayCoffeeFoodAddEditOptionsAsyncGet AsyncGet;
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
	     //FoodNameWidget=null;
	     //AlwaysIncludeWidget=null;
	     
	     setContentView(R.layout.fc_food_add_edit_options_list);
	     TextView UsernameView = (TextView)findViewById(R.id.fc_banner_text);
	     String AddStr = getString(R.string.select_a_food_option);
	     UsernameView.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + ", " + AddStr);
	     
         //AlwaysIncludeWidget=null;
         
	     FoodTypeID = getIntent().getIntExtra(FreewayCoffeeItemListView.USER_ADD_FOOD_TYPE,-1);
	     EditFoodID = getIntent().getIntExtra(FreewayCoffeeItemListView.USER_EDIT_FOOD_ID,-1);
	     FoodType = appState.GetFoodTypes().get(FoodTypeID);
	     if(FoodType==null)
	     {
			return; // Oh crapola !!! FIXME TODO
	     }
			
	     if(EditFoodID==-1)
	     {
	    	 // Add
	    	 FoodInProgress = appState.MakeNewEmptyFood(String.valueOf(FoodType.GetCategoryID()), String.valueOf(FoodType.GetTypeID()));
	     }
	     else
	     {
	    	 // Edit
	    	 FoodInProgress = new HashMap<String,String>(appState.GetFoodData().get(EditFoodID)); 	 
	     }
	     
	     if(FoodTypeID==-1 && EditFoodID>0)
	     {
	    	 try
	    	 {
	    		 FoodTypeID=Integer.parseInt(FoodInProgress.get(FreewayCoffeeXMLHelper.FOOD_TYPE_ID_ATTR));
	    	 }
	    	 catch(NumberFormatException e)
	    	 {
	    		 FoodTypeID=-1;
	    	 }
	     }
	     
	     listView = (ListView)findViewById(R.id.fc_food_add_edit_options_list_object);
	     
	     // List header and footer used to avoid trying to stuff ListView into a ScrollView (it doesn't like that like a cat doesn't like taking a shower)
	     View footerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fc_food_add_edit_options_list_footer, null, false);
	     listView.addFooterView(footerView);
		     
	     View headerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fc_food_add_edit_options_list_header, null, false);
	     listView.addHeaderView(headerView);
		  
	     //TextView UsernameView = (TextView)findViewById(R.id.fc_food_add_edit_label);
	     //String FoodString = getString(R.string.fc_build_your_food);
	      
	     // TODO move USER_INFO_NAME_KEY to a better place
	     //UsernameView.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + ", " + FoodString);
	     AddEditButton = (Button)findViewById(R.id.fc_food_add_edit_options_doit);
	     
         
	     // TODO --- what if we have garbage DrinkID/Type here ? Exit ? Display some error ???
	     
        Object retained = getLastNonConfigurationInstance();
 		
 		if(retained instanceof FreewayCoffeeFoodAddEditOptionsAsyncGet)
 		{
 			// Get the saved state -- the Async Get with the XML and the ListIndex.
 			// WHen we set the new activity, it will call Process XML for us
 			AsyncGet = (FreewayCoffeeFoodAddEditOptionsAsyncGet)retained;
 			showProgressDialog(AsyncGet.GetProgressMessage());// This must be before setActivity as that calls ProcessXML which destroys the dialog --- so we dont want to just re-create it again
 			AsyncGet.SetActivity(this);
 		}
 		else
 		{
 			AsyncGet = null;
 			ListIndex=null;
 			DisplayList();
 		}
     
	    
	}
	
	private void DisplayList()
	{
		
		CreateListIndex();
		TextView FoodTypeView = (TextView)findViewById(R.id.fc_add_edit_food_type);
		
		String FoodTypeStr= FoodType.GetTypeName();
		if(FoodType.HasZeroBaseCost()==false)
		{
			FoodTypeStr = FoodTypeStr + "($" + FoodType.GetBaseCost() + ")";
		}
		
        FoodTypeView.setText(FoodTypeStr);
        
        if(EditFoodID==-1)
	    {
	    	 // Add
	    	 //DrinkInProgress = appState.MakeNewEmptyDrink(String.valueOf(DrinkType));
	    	 AddEditButton.setText(getString(R.string.fc_add_food));
	    }
        else
        {
        	AddEditButton.setText(getString(R.string.fc_edit_food));
        }
        
        myAdapter = new FCFoodAddEditOptionsAdapter(ListIndex,this);
        listView.setAdapter(myAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
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
	
	private FreewayCoffeeFoodDrinkOptionListItem CreateIndexEntry(String OptionType,FreewayCoffeeFoodDrinkOption Option)
	{
		FreewayCoffeeFoodDrinkOptionListItem IndexEntry=new FreewayCoffeeFoodDrinkOptionListItem();
		/*
		IndexEntry.FoodDrinkOption=OptionType;
		IndexEntry.FoodDrinkOptionDescr=Option.GetLabel();
		IndexEntry.PickType=Option.GetType();
		IndexEntry.SortOrder = Option.GetSortOrder();
		IndexEntry.EntryType=FreewayCoffeeFoodDrinkOptionListItem.OPTION_TYPE_FOOD_DRINK_OPTION;
		
		IndexEntry.OptionValueID=FoodInProgress.get(FreewayCoffeeXMLHelper.PART_ID_PREFIX + IndexEntry.FoodDrinkOption);
		
		IndexEntry.OptionValue=Option.GetOptionValueFromID(IndexEntry.OptionValueID);
		
		try
		{
			Integer OptionAsInt = Integer.parseInt(IndexEntry.OptionValueID);
			IndexEntry.FoodDrinkOptionPrice=FoodType.GetCostFromOptionTypeAndID(OptionType,OptionAsInt);
		}
		catch (NumberFormatException e)
		{
			// TODO what do I do now ?
		}
		if(IndexEntry.OptionValue==null)
		{
			IndexEntry.OptionValue=getString(R.string.none_text);
		}
		*/
		return IndexEntry;
	}
	
	private void CreateListIndex()
	{
		
		ListIndex = new ArrayList<FreewayCoffeeFoodDrinkOptionListItem>();
		FreewayCoffeeFoodDrinkOptionListItem IndexEntry=null;
		
		for (Map.Entry<String,FreewayCoffeeFoodDrinkOption> entry : appState.GetFoodOptions().entrySet())
 		{
			// OptionType is badly named. Its milks, sizes etc
			String OptionType = entry.getKey();
			/*
			if(FoodType.AreAnyOptionsValidForType(OptionType)==true)
			{
				IndexEntry = CreateIndexEntry(OptionType,entry.getValue());		
				ListIndex.add(IndexEntry);
			}
			*/
 		}
		
		
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
				String OptionSelected = data.getStringExtra(FreewayCoffeeItemListView.USER_SELECTED_FOOD_OPTION_DATA_ID);
			
				//FreewayCoffeeFoodDrinkOption Option = appState.GetFoodOptions().get(Item.FoodDrinkOption);
				// Update the current drink
				//FoodInProgress.put(FreewayCoffeeXMLHelper.PART_ID_PREFIX + Item.FoodDrinkOption,OptionSelected);
			
				FreewayCoffeeFoodDrinkOptionListItem IndexEntry=null;
				//IndexEntry = CreateIndexEntry(Item.FoodDrinkOption,Option);		
				ListIndex.set(ListPosition,IndexEntry);
			
				/*
				Item.OptionValueID=OptionSelected;
				
				Item.OptionValue=Option.GetOptionValueFromID(Item.OptionValueID);
				if(Item.OptionValue==null)
				{
					Item.OptionValue=getString(R.string.none_text);
				}
				 */
			}
			
			myAdapter.notifyDataSetChanged();
		}
	}
	
	
	public void DoAction (View v)
    {
		String ProgressMessage;
			
		// TODO, this is not the best way to determine if its an add or an edit. Oh well
		//if(FoodInProgress.get("id")==null)
		//{
		//	ProgressMessage = getString(R.string.fc_adding_your_item);
		//}
		//else
		//{
		//	ProgressMessage = getString(R.string.fc_editing_your_item);			}
			//AsyncGet = new FreewayCoffeeFoodAddEditOptionsAsyncGet(this,appState,ProgressMessage);
			//AsyncGet.execute(CommandStr);
	//	}
		
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
		Log.w("FreewayCoffeeDB",XML);
		/* Get a SAXParser from the SAXPArserFactory. */
		SAXParserFactory spf = SAXParserFactory.newInstance();
		AsyncGet=null;
		try
		{
			SAXParser sp = spf.newSAXParser();

            /* Get the XMLReader of the SAXParser we created. */
			XMLReader xr = sp.getXMLReader();
            /* Create a new ContentHandler and apply it to the XML-Reader*/
			AddEditFoodOptionsXMLHandler = new FreewayCoffeeFoodAddEditOptionsXMLHandler(appState);
           xr.setContentHandler(AddEditFoodOptionsXMLHandler);

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
          
          if(AddEditFoodOptionsXMLHandler.NetworkError==true)
  		  {
        	  DisplayNetworkError();
        	  return;
  		  }
          if(AddEditFoodOptionsXMLHandler.signonResponse!=null)
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
          
          if(AddEditFoodOptionsXMLHandler.addFoodResponse!=null)
          {
        	  if(AddEditFoodOptionsXMLHandler.addFoodResponse.equals("ok"))        	
        	  {
        		  Toast SuccessToast = Toast.makeText(appState.getApplicationContext(),"Your Food Was successfully added.",
        			  							Toast.LENGTH_SHORT);
        		  SuccessToast.show();
        		  try
        		  {
        			  Integer FoodID = Integer.parseInt(AddEditFoodOptionsXMLHandler.TheFood.get("id"));
        			  appState.GetFoodData().put(FoodID,AddEditFoodOptionsXMLHandler.TheFood);
        			  EndActivity(FoodID,FreewayCoffeeItemListView.SubActivityTypes.FC_SUB_ACTIVITY_ADD_FOOD.ordinal());
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
        		  Toast ErrorToast = Toast.makeText(appState.getApplicationContext(),"An Error Occurred Adding Your Item. Please Try Again",Toast.LENGTH_SHORT);
        		  ErrorToast.show();
        		  return;
        	  }
          }
          else if(AddEditFoodOptionsXMLHandler.editFoodResponse!=null)
          {
        	  if(AddEditFoodOptionsXMLHandler.editFoodResponse.equals("ok"))        	
        	  {
        		  
        		  Toast SuccessToast = Toast.makeText(appState.getApplicationContext(),"Your Item Was Successfully Edited.",
        			  							Toast.LENGTH_SHORT);
        		  SuccessToast.show();
        		  
        		  try
        		  {
        			  Integer FoodID = Integer.parseInt(AddEditFoodOptionsXMLHandler.TheFood.get("id"));
        			  appState.GetFoodData().put(FoodID,AddEditFoodOptionsXMLHandler.TheFood);
        			  EndActivity(FoodID,FreewayCoffeeItemListView.SubActivityTypes.FC_SUB_ACTIVITY_EDIT_FOOD.ordinal());
        	 	}
        		  catch (NumberFormatException e)
        		  {
        			  // TODO sub optional
        			  Toast ErrorToast = Toast.makeText(appState.getApplicationContext(),"An Error Occurred Editing Your Food. Please Try Again",Toast.LENGTH_SHORT);
        			  ErrorToast.show();
        			  return;
        		  }
        	  }
        	  else
        	  {
        		  // TODO Obviously could do better here.
        		  Toast ErrorToast = Toast.makeText(appState.getApplicationContext(),"An Error Occurred Editing Your Food. Please Try Again",Toast.LENGTH_SHORT);
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
	
	private void EndActivity(Integer FoodID,Integer AddOrEdit)
	{
		Intent intent = new Intent();
		
		if(AddOrEdit==FreewayCoffeeItemListView.SubActivityTypes.FC_SUB_ACTIVITY_EDIT_FOOD.ordinal())
		{
			intent.putExtra(FreewayCoffeeItemListView.USER_CHOSE_FOOD_ID,FoodID);
		}
		else if(AddOrEdit == FreewayCoffeeItemListView.SubActivityTypes.FC_SUB_ACTIVITY_ADD_FOOD.ordinal())
		{
			intent.putExtra(FreewayCoffeeItemListView.USER_CHOSE_FOOD_ID,FoodID);
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

	private class FCFoodAddEditOptionsAdapter extends BaseAdapter
	  {
			private ArrayList<FreewayCoffeeFoodDrinkOptionListItem> Items;
			private LayoutInflater mInflater;
			
			public FCFoodAddEditOptionsAdapter(ArrayList<FreewayCoffeeFoodDrinkOptionListItem> items, Context context)
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
			                
			            	//intent.putExtra(FreewayCoffeeItemListView.USER_SELECT_FOOD_OPTION_ID,ListIndex.get(mPosition).FoodDrinkOption);
			            	intent.putExtra(FreewayCoffeeItemListView.PARENT_LIST_POSITION,mPosition);
			            	intent.putExtra(FreewayCoffeeItemListView.FOOD_TYPE,FoodTypeID);
			            	
			            	intent.setClassName(FreewayCoffeeFoodAddEditOptionsActivity.this, FreewayCoffeeFoodOptionValuePickerActivity.class.getName());
			            	startActivityForResult(intent,FreewayCoffeeFoodDrinkOptionListItem.OPTION_TYPE_FOOD_DRINK_OPTION);
			        	}
			        }               
			    }
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
			  
				// A FCListViewHolder keeps references to children views to avoid unneccessary calls
				// to findViewById() on each row.
				FCFoodAddEditOptionsHolder holder;
				  
				// When convertView is not null, we can reuse it directly, there is no need
				// to reinflate it. We only inflate a new View when the convertView supplied
				// by ListView is null
				if (convertView == null)
				{
					convertView = mInflater.inflate(R.layout.fc_food_add_edit_options_list_row, parent,false);
					// Creates a ViewHolder and store references to the two children views
					// we want to bind data to.
					holder = new FCFoodAddEditOptionsHolder();
					holder.item_descr = (TextView) convertView.findViewById(R.id.fc_food_add_edit_options_row_text);
					  
					//holder.item_icon = (ImageView) convertView.findViewById(R.id.fc_food_add_edit_options_row_img);
					holder.item_cost=(TextView) convertView.findViewById(R.id.fc_food_add_edit_options_row_cost);
					holder.item_small_descr = (TextView) convertView.findViewById(R.id.fc_drink_add_edit_options_row_small_text);
					convertView.setOnClickListener(new OnItemClickListener(position));
					convertView.setTag(holder);
				                     
				}
				else
				{
					// Get the ViewHolder back to get fast access to the TextView
					// and the ImageView.
					holder = (FCFoodAddEditOptionsHolder) convertView.getTag();
					convertView.setOnClickListener(new OnItemClickListener(position));
				}
				  	              
				UpdateItem(position,holder);
				 
				  
				return convertView;

			  }
			  
			  private void UpdateItem(Integer position,FCFoodAddEditOptionsHolder holder)
			  {
				  if(Items.get(position).EntryType==FreewayCoffeeFoodDrinkOptionListItem.OPTION_TYPE_FOOD_DRINK_OPTION)
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
				  }
				  
				//  holder.item_icon.setImageResource(R.drawable.fc_food);
			  }

			  class FCFoodAddEditOptionsHolder
			  {
				  TextView item_descr;
				  TextView item_small_descr;
				  TextView item_cost;
				  //ImageView item_icon;
				 
			  }
	}
}
