
package com.freewaycoffee.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.freewaycoffee.client.FreewayCoffeeItemListView.SubActivityTypes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FreewayCoffeeFoodOptionValuePickerActivity extends Activity 
{
	private FreewayCoffeeApp appState;
	
	private  ArrayList<FreewayCoffeeFoodDrinkOptionDataListItem> ListIndex;
	private ListView listView;
	private String FoodOptionType;
	FreewayCoffeeFoodDrinkOption Option;
	private Integer ParentListPosition;
	private FreewayCoffeeFoodDrinkType FoodTypeInfo;
	private Integer FoodTypeID;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		 super.onCreate(savedInstanceState);
	        
	     appState = ((FreewayCoffeeApp)getApplicationContext());
	     
	     setContentView(R.layout.fc_food_option_value_picker_list);
	     
	     TextView UsernameView = (TextView)findViewById(R.id.fc_banner_text);
	     String AddStr = getString(R.string.select_a_food_option);
	     
	     UsernameView.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + ", " + AddStr);
	     
	     // Get this just so's we can set it back again later. TODO must be a better way.
	      ParentListPosition = getIntent().getIntExtra(FreewayCoffeeItemListView.PARENT_LIST_POSITION, -1);
	     
	     // TODO Error check
	     FoodOptionType = getIntent().getStringExtra(FreewayCoffeeItemListView.USER_SELECT_FOOD_OPTION_ID);
	     Option = appState.GetFoodOptions().get(FoodOptionType);
	     
	     
	     FoodTypeID = getIntent().getIntExtra(FreewayCoffeeItemListView.FOOD_TYPE,-1);
	     FoodTypeInfo = appState.GetFoodTypes().get(FoodTypeID);
	     
	     CreateListIndex();
	     listView = (ListView)findViewById(R.id.fc_food_option_value_picker_list);
	     
	     View footerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fc_food_option_value_picker_list_footer, null, false);
	     listView.addFooterView(footerView);

	     
	     listView.setAdapter(new FCFoodOptionPickerAdapter(ListIndex,this));
         listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	     
	}
	 
	@Override
   	public void onConfigurationChanged(Configuration newConfig) 
   	{
   	    super.onConfigurationChanged(newConfig);
   	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   	}
	
	
	private void CreateListIndex()
	{
		ListIndex = new ArrayList<FreewayCoffeeFoodDrinkOptionDataListItem>();
		FreewayCoffeeFoodDrinkOptionDataListItem IndexEntry=null;
		/*
		for (HashMap<String,String> OptionData : Option.GetOptionData().values())
		{
			String OptionID = OptionData.get("id");
			Integer OptionAsInt = Integer.parseInt(OptionID);
			
			// This will include Mandatory check
			if(FoodTypeInfo.IsOptionValidForFoodDrinkType(FoodOptionType,OptionAsInt))
			{
				// FIXME TODO cannot hardcode zeroes everywhere
				
				// OptionAsInt==0 is NONE, so if its NONE, and the option is not mandatory, we can have "None"
				// If its NONE AND the option is mandatory, then we dont allow the user to pick that
				// Unfortunately, Option.GetType is badly named, and means the Type of the option (sizes, milks etc)
				if( (OptionAsInt==0) && (appState.IsOptionMandatoryForFoodType(FoodTypeID,Option.GetType())==true) )
				{
					// Skip this one
					continue;
				}
				else
				{
					// Add it
					IndexEntry=new FreewayCoffeeFoodDrinkOptionDataListItem();
					IndexEntry.OptionID = OptionID;
					IndexEntry.OptionText = OptionData.get(FreewayCoffeeXMLHelper.DRINK_OPTION_DESCR);
					try
					{
						IndexEntry.OptionSortOrder = Integer.parseInt(OptionData.get(FreewayCoffeeXMLHelper.SORT_ORDER_ATTR));
					}
					catch(NumberFormatException e)
					{
						IndexEntry.OptionSortOrder = -1;
					}
					
					
				
					IndexEntry.OptionCost=FoodTypeInfo.GetCostFromOptionTypeAndID(FoodOptionType,OptionAsInt);
				
					
					ListIndex.add(IndexEntry);
				}
			}
		}	
		Collections.sort(ListIndex,FreewayCoffeeFoodDrinkOptionDataListItem.GetDisplayComparator());
		*/
 		
	}

	public void UserCanceled(View v)
    {
            setResult(RESULT_CANCELED, null);
            finish();
    }

	
	private class FCFoodOptionPickerAdapter extends BaseAdapter
	  {
			private ArrayList<FreewayCoffeeFoodDrinkOptionDataListItem> Items;
			private LayoutInflater mInflater;
			
			public FCFoodOptionPickerAdapter(ArrayList<FreewayCoffeeFoodDrinkOptionDataListItem> items, Context context)
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
					Intent intent = new Intent();	
					intent.putExtra(FreewayCoffeeItemListView.USER_SELECTED_FOOD_OPTION_DATA_ID,ListIndex.get(mPosition).OptionID);
					intent.putExtra(FreewayCoffeeItemListView.PARENT_LIST_POSITION,ParentListPosition);
					setResult(RESULT_OK,intent);
					finish();   	
			        }               
			    }
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
			  
				// A FCListViewHolder keeps references to children views to avoid unneccessary calls
				// to findViewById() on each row.
				FCFoodOptionDataPickerHolder holder;
				  
				// When convertView is not null, we can reuse it directly, there is no need
				// to reinflate it. We only inflate a new View when the convertView supplied
				// by ListView is null
				if (convertView == null)
				{
					convertView = mInflater.inflate(R.layout.fc_drink_option_picker_list_row, parent,false);
					// Creates a ViewHolder and store references to the two children views
					// we want to bind data to.
					holder = new FCFoodOptionDataPickerHolder();
					holder.item_descr = (TextView) convertView.findViewById(R.id.fc_drink_option_picker_row_text);
					holder.item_cost = (TextView) convertView.findViewById(R.id.fc_option_picker_row_cost);
					
					//holder.item_icon = (ImageView) convertView.findViewById(R.id.fc_drink_option_picker_row_img);
					  
					//holder.item_small_descr = (TextView) convertView.findViewById(R.id.fc_drink_option_picker_row_small_text);
					convertView.setOnClickListener(new OnItemClickListener(position));
					convertView.setTag(holder);
				                     
				}
				else
				{
					// Get the ViewHolder back to get fast access to the TextView
					// and the ImageView.
					holder = (FCFoodOptionDataPickerHolder) convertView.getTag();
				}
				  	              
				UpdateItem(position,holder);
				 
				  
				return convertView;

			  }
			  
			  private void UpdateItem(Integer position,FCFoodOptionDataPickerHolder holder)
			  {
				  
				  //holder.item_descr.setText(Items.get(position).OptionText);
				  //holder.item_small_descr.setText(appState.GetDrinkSmallTextFromID(Items.get(position).DrinkID));
				  /*
				  if( (Items.get(position).OptionCost!=null) &&
					  (!Items.get(position).OptionCost.equals("")) &&
					  (!Items.get(position).OptionCost.equals("0.00")))
				  {
					  holder.item_cost.setText(Items.get(position).OptionCost);
				  }
				  else
				  {
					  holder.item_cost.setText("");
				  }
				  //holder.item_icon.setImageResource(R.drawable.fc_drink);
				   * */
				   
			  }
			  
			  class FCFoodOptionDataPickerHolder
			  {
				  TextView item_descr;
				  TextView item_small_descr;
				  TextView item_cost;
				  //ImageView item_icon;
				 
			  }
	}
}

