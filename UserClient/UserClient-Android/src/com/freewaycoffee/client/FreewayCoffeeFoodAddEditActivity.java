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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


// Allows the customer to edit an existing drink, or add an entirely new one

public class FreewayCoffeeFoodAddEditActivity extends Activity 
{
private FreewayCoffeeApp appState;
	
	private ArrayList<FreewayCoffeeFoodAddEditListItem> ListIndex;
	private ListView listView;
	private Integer Command;
	private Integer ParentListPos;
	private Integer FoodCategory;  // Restrict display of Food Types to just this.
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		 super.onCreate(savedInstanceState);
	        
	     appState = ((FreewayCoffeeApp)getApplicationContext());
	     
	     setContentView(R.layout.fc_food_add_edit_list);
	     FoodCategory = getIntent().getIntExtra(FreewayCoffeeItemListView.ACTIVITY_FOOD_CATEGORY,-1);
	     
	     TextView UsernameView = (TextView)findViewById(R.id.fc_banner_text);
	     String AddStr = getString(R.string.fc_select_food);
	     
	     UsernameView.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + ", " + AddStr);
	     
	     
	     // Check how we were called. Was it from ItemList(bypassing DrinkPicker) or was it from Drink Picker?
	     ParentListPos = getIntent().getIntExtra(FreewayCoffeeItemListView.USER_ITEM_SELECTED_FOOD_DRINK_POS,-1);
	     
	     ResetList();
	}
	
	public void ResetList()
	{
		listView = (ListView)findViewById(R.id.fc_food_add_edit_list);
		View footerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fc_food_add_edit_list_footer, null, false);
	    listView.addFooterView(footerView);
	    CreateListIndex();
		listView.setAdapter(new FCFoodAddEditAdapter(ListIndex,this));
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
		ListIndex = new ArrayList<FreewayCoffeeFoodAddEditListItem>();
		FreewayCoffeeFoodAddEditListItem IndexEntry=null;
		
		for (FreewayCoffeeFoodDrinkType FoodType : appState.GetFoodTypes().values())
 		{
			if(FoodType.GetCategoryID()!=FoodCategory)
			{
				continue; // Not the correct Food Category
			}
			
			IndexEntry = new FreewayCoffeeFoodAddEditListItem();
			IndexEntry.FoodID=FoodType.GetTypeID();
			IndexEntry.FoodName=FoodType.GetTypeName();
			IndexEntry.FoodCost=FoodType.GetBaseCost();
			
			IndexEntry.SortOrder=FoodType.GetSortOrder();
			
			ListIndex.add(IndexEntry);
 		}
		Collections.sort(ListIndex,FreewayCoffeeFoodAddEditListItem.GetDisplayComparator());
	}

	
	protected void onDestroy ()
	{
		super.onDestroy();
	}		 
		 
	public void UserCanceled(View v)
    {
            setResult(RESULT_CANCELED, null);
            finish();
    }
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		
		if(resultCode==RESULT_OK)
		{
			Integer FoodID = data.getIntExtra(FreewayCoffeeItemListView.USER_CHOSE_FOOD_ID,-1);
			Intent intent=new Intent();
			intent.putExtra(FreewayCoffeeItemListView.USER_CHOSE_FOOD_ID, FoodID);
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
	
	private class FCFoodAddEditAdapter extends BaseAdapter
	  {
			private ArrayList<FreewayCoffeeFoodAddEditListItem> Items;
			private LayoutInflater mInflater;
			
			public FCFoodAddEditAdapter(ArrayList<FreewayCoffeeFoodAddEditListItem> items, Context context)
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
		            		
					//intent.putExtra(FreewayCoffeeItemListView.USER_ADD_DRINK_TYPE,ListIndex.get(mPosition).DrinkID);	
					intent.putExtra(FreewayCoffeeItemListView.USER_ADD_FOOD_TYPE,ListIndex.get(mPosition).FoodID);
							                
			            	
					intent.setClassName(FreewayCoffeeFoodAddEditActivity.this, FreewayCoffeeFoodAddEditOptionsActivity.class.getName());
			       	startActivityForResult(intent,0);  	
			       	
					
				}               
			}
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
			  
				// A FCListViewHolder keeps references to children views to avoid unneccessary calls
				// to findViewById() on each row.
				FCFoodAddEditHolder holder;
				  
				// When convertView is not null, we can reuse it directly, there is no need
				// to reinflate it. We only inflate a new View when the convertView supplied
				// by ListView is null
				if (convertView == null)
				{
					convertView = mInflater.inflate(R.layout.fc_food_add_edit_list_row, parent,false);
					// Creates a ViewHolder and store references to the two children views
					// we want to bind data to.
					holder = new FCFoodAddEditHolder();
					holder.item_descr = (TextView) convertView.findViewById(R.id.fc_food_add_edit_row_text);
					  
					//holder.item_icon = (ImageView) convertView.findViewById(R.id.fc_food_add_edit_row_img);
					  
					holder.item_small_descr = (TextView) convertView.findViewById(R.id.fc_food_add_edit_row_small_text);
					
					holder.item_cost =(TextView) convertView.findViewById(R.id.fc_food_add_edit_row_cost);
					convertView.setOnClickListener(new OnItemClickListener(position));
					convertView.setTag(holder);
				                     
				}
				else
				{
					// Get the ViewHolder back to get fast access to the TextView
					// and the ImageView.
					holder = (FCFoodAddEditHolder) convertView.getTag();
					convertView.setOnClickListener(new OnItemClickListener(position));
				}
				  	              
				UpdateItem(position,holder);
				 
				  
				return convertView;

			  }
			  
			  private void UpdateItem(Integer position,FCFoodAddEditHolder holder)
			  {
				  
				  holder.item_descr.setText(Items.get(position).FoodName);
				  
				  holder.item_cost.setText("$" + Items.get(position).FoodCost);
				  //holder.item_small_descr.setText(appState.GetDrinkSmallTextFromID(Items.get(position).DrinkID));
				  //holder.item_icon.setImageResource(R.drawable.fc_food);
			  }
	  
			  class FCFoodAddEditHolder
			  {
				  TextView item_descr;
				  TextView item_small_descr;
				  //ImageView item_icon;
				  TextView item_cost;
				 
			  }
	}
}
