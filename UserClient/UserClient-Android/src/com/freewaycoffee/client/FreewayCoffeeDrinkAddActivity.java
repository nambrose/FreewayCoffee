package com.freewaycoffee.client;

import java.io.IOException;
import java.io.StringReader;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FreewayCoffeeDrinkAddActivity extends Activity 
{
	private FreewayCoffeeApp appState;
	
	private  ArrayList<FreewayCoffeeDrinkPickerPair> ListIndex;
	private ListView listView;
	private Integer Command;
	private Integer ParentListPos;
	private FreewayCoffeeDrinkAddAsyncPost AsyncGet;
	private ProgressDialog DrinkListProgress;
	private FreewayCoffeeAddEditDrinkXMLHandler DrinksXMLHandler;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		 super.onCreate(savedInstanceState);
	        
	     appState = ((FreewayCoffeeApp)getApplicationContext());
	     
	     setContentView(R.layout.fc_drink_add_list);
	     
	     TextView UsernameView = (TextView)findViewById(R.id.fc_banner_text);
	     String AddStr = getString(R.string.select_a_drink_type);
	     
	     UsernameView.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + ", " + AddStr);
	     
	     // Check how we were called. Was it from ItemList(bypassing DrinkPicker) or was it from Drink Picker?
	     
	     Command = getIntent().getIntExtra(FreewayCoffeeItemListView.ACTIVITY_COMMAND_CODE,-1);
	     ParentListPos = getIntent().getIntExtra(FreewayCoffeeItemListView.USER_ITEM_SELECTED_FOOD_DRINK_POS,-1);
	     
	     
	     
	     Object retained = getLastNonConfigurationInstance();
			
		if(retained instanceof FreewayCoffeeDrinkAddAsyncPost)
		{
			// Get the saved state -- the Async Get with the XML and the ListIndex.
			// WHen we set the new activity, it will call Process XML for us
			AsyncGet = (FreewayCoffeeDrinkAddAsyncPost)retained;
			showProgressDialog();// This must be before setActivity as that calls ProcessXML which destroys the dialog --- so we dont want to just re-create it again
				
			AsyncGet.SetActivity(this);
		}
		else
		{
			AsyncGet=null;
			ListIndex=null;
			GetOrUpdate();
		}

	}
	
	public void ResetList()
	{
		listView = (ListView)findViewById(R.id.fc_drink_add_list);
		View footerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fc_drink_add_list_footer, null, false);
	    listView.addFooterView(footerView);
	    CreateListIndex();
		listView.setAdapter(new FCDrinkAddAdapter(ListIndex,this));
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	     
	}
	public void GetOrUpdate()
	{
		if(appState.IsDrinksMenuLoaded())
		{
			ResetList();
		}
		else
		{
			ExecuteGet();
		}
	}
	
	private void ExecuteGet()
    {
		// Make a Get
        AsyncGet = new FreewayCoffeeDrinkAddAsyncPost(this,appState);
        AsyncGet.execute(appState.MakeGetDrinkPickListURL());
    }
	
	@Override
   	public void onConfigurationChanged(Configuration newConfig) 
   	{
   	    super.onConfigurationChanged(newConfig);
   	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   	}
	private void CreateListIndex()
	{
		ListIndex = new ArrayList<FreewayCoffeeDrinkPickerPair>();
		FreewayCoffeeDrinkPickerPair IndexEntry=null;
		
		for (FreewayCoffeeFoodDrinkType DrinkType : appState.GetDrinkTypes().values())
 		{
			// DrinkID== NONE and we dont want to add that one. 
			if(DrinkType.GetTypeID()!=0)
			{
				IndexEntry=new FreewayCoffeeDrinkPickerPair();
				IndexEntry.Type=FreewayCoffeeApp.FoodDrink.Drink;
				IndexEntry.ID = DrinkType.GetTypeID();
				IndexEntry.Descr = DrinkType.GetTypeName();
				IndexEntry.SortOrder = DrinkType.GetSortOrder();
				IndexEntry.DrinkType = DrinkType;
				ListIndex.add(IndexEntry);
			}
 		}
		Collections.sort(ListIndex,FreewayCoffeeDrinkPickerPair.GetDisplayComparator());
		
	}

	public void showProgressDialog()
	{
		 DrinkListProgress = ProgressDialog.show(this, "",
				 getString(R.string.fc_retrieving_drinks_menu),
				 true);
	
	}
	
	private void DismissAllProgress()
	{
		
		if(DrinkListProgress!=null)
		{
			DrinkListProgress.dismiss();
			DrinkListProgress=null;
		}
		
	}

	private void UnlinkAllAsync()
	{
		if(AsyncGet!=null)
		{
			AsyncGet.UnlinkActivity();
			AsyncGet=null;
		}		
	}

	private void DisplayNetworkError()
	{
		if(DrinkListProgress!=null)
		{
			DrinkListProgress.dismiss();
			DrinkListProgress=null;
		}
		Toast Err = Toast.makeText(appState.getApplicationContext(),
					getString(R.string.fc_network_error),
					Toast.LENGTH_SHORT);
		Err.show();
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
		 
		 
	public void UserCanceled(View v)
    {
            setResult(RESULT_CANCELED, null);
            finish();
    }
	
	public void ProcessXMLResult(String XML)
    {
		UnlinkAllAsync();
		DismissAllProgress();

		//Log.w("FreewayCoffeeDB",XML);
		/* Get a SAXParser from the SAXPArserFactory. */
		SAXParserFactory spf = SAXParserFactory.newInstance();
		
		try
		{
			SAXParser sp = spf.newSAXParser();

			/* Get the XMLReader of the SAXParser we created. */
			XMLReader xr = sp.getXMLReader();
			/* Create a new ContentHandler and apply it to the XML-Reader*/
			DrinksXMLHandler = new FreewayCoffeeAddEditDrinkXMLHandler(appState);
			xr.setContentHandler(DrinksXMLHandler);

			/* Parse the xml-data from our URL. */
			InputSource is = new InputSource(new StringReader(XML));
			//is.setEncoding("UTF-8");
			
    		xr.parse(is);
			/* Parsing has finished. */
			
			
			if(DrinksXMLHandler.NetworkError==true)
			{
	        	  DisplayNetworkError();
	        	  return;
			}
			
			if(DrinksXMLHandler.signonResponse!=null)
			{
				// TODO -=- in general, what do we do for these sub-views if we get say a SIGNON_OK.
				// Cannot just reload indefinitely. Can I even call reload here ?
				// Return to the parent view and try to let that one refresh ? Lets punt it for now.

				// Tell the parent view that some critical login-type thing has happened!
				setResult(FreewayCoffeeItemListView.RESULT_CODE_NOT_LOGGED_IN);
				UnlinkAllAsync();
				
				this.finish();
			}
			else
			{
				ResetList();
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
        	//Log.w("FCItemListView",pe.getMessage());
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
		if(resultCode==RESULT_OK)
		{
			Integer DrinkID = data.getIntExtra(FreewayCoffeeItemListView.USER_CHOSE_DRINK_ID,-1);
			Intent intent=new Intent();
			intent.putExtra(FreewayCoffeeItemListView.USER_CHOSE_DRINK_ID, DrinkID);
			intent.putExtra(FreewayCoffeeItemListView.USER_ITEM_SELECTED_FOOD_DRINK_POS, ParentListPos);
			setResult(RESULT_OK,intent);
		}
		else if(resultCode==FreewayCoffeeItemListView.RESULT_CODE_NOT_LOGGED_IN)
		{
			setResult(FreewayCoffeeItemListView.RESULT_CODE_NOT_LOGGED_IN);
		}
		else
		{
			setResult(RESULT_CANCELED,null);
		}
		finish();
	}
	
	private class FCDrinkAddAdapter extends BaseAdapter
	  {
			private ArrayList<FreewayCoffeeDrinkPickerPair> Items;
			private LayoutInflater mInflater;
			
			public FCDrinkAddAdapter(ArrayList<FreewayCoffeeDrinkPickerPair> items, Context context)
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
		            		
					intent.putExtra(FreewayCoffeeItemListView.USER_ADD_DRINK_TYPE,ListIndex.get(mPosition).ID);			                
			            	
					intent.setClassName(FreewayCoffeeDrinkAddActivity.this, FreewayCoffeeDrinkAddEditActivity.class.getName());
			       	startActivityForResult(intent,0);  	
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
					convertView = mInflater.inflate(R.layout.fc_drink_add_row, parent,false);
					// Creates a ViewHolder and store references to the two children views
					// we want to bind data to.
					holder = new FCDrinkPickerHolder();
					holder.item_descr = (TextView) convertView.findViewById(R.id.fc_drink_add_row_text);
					  
					//holder.item_icon = (ImageView) convertView.findViewById(R.id.fc_drink_add_row_img);
					  
					holder.item_small_descr = (TextView) convertView.findViewById(R.id.fc_drink_add_row_small_text);
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
				  
				  holder.item_descr.setText(Items.get(position).Descr);
				  if(Items.get(position).DrinkType.GetFoodDrinkTypeText()!=null)
				  {
					  holder.item_small_descr.setText(Items.get(position).DrinkType.GetFoodDrinkTypeText());
				  }
				  //holder.item_small_descr.setText(appState.GetDrinkSmallTextFromID(Items.get(position).DrinkID));
				  
				  //holder.item_icon.setImageResource(R.drawable.fc_drink);
			  }

			  

			  
			  class FCDrinkPickerHolder
			  {
				  TextView item_descr;
				  TextView item_small_descr;
				  //ImageView item_icon;
				 
			  }
	}
}
