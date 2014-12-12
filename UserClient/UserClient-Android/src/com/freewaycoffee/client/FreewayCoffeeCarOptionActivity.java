package com.freewaycoffee.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;


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

public class FreewayCoffeeCarOptionActivity extends Activity 
{
	private FreewayCoffeeApp appState;
	private ArrayList<FCCarOptionIndexHolder> ListIndex;
	// Car Make, Model or Color
	private Integer OptionListType;
	private ListView MainList;
	private FCCarOptionAdapter ListAdapter;
	private FreewayCoffeeCarMakeModelColorTagHolder CarData;
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	 	appState = ((FreewayCoffeeApp)getApplicationContext());        
    	setContentView(R.layout.fc_car_option_list);
    	CarData = appState.GetCarDataBeingEdited();
    	
    	String OptionName;
    	
    	OptionListType = getIntent().getIntExtra(FreewayCoffeeUserTagActivity.OPTION_PICK_TYPE,-1);
    	
    	if(OptionListType==FreewayCoffeeUserTagActivity.LIST_ENTRY_MAKE)
    	{
    		OptionName = getString(R.string.fc_make);
    	}
    	else if(OptionListType==FreewayCoffeeUserTagActivity.LIST_ENTRY_MODEL)
    	{
    		OptionName = getString(R.string.fc_model);
    	}
    	else if(OptionListType==FreewayCoffeeUserTagActivity.LIST_ENTRY_COLOR)
    	{
    		OptionName = getString(R.string.fc_color);
    	}
    	else
    	{
    		OptionName="Oooops";
    	}
    	
    	 TextView UsernameView = (TextView)findViewById(R.id.fc_banner_text);
    	 String AddStr = getString(R.string.fc_select_your) + " " + OptionName;
  	     
  	     UsernameView.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + ", " + AddStr);
  	     
  	     
    	MainList = (ListView)findViewById(R.id.fc_car_option_list);
        // listView.setOnItemClickListener(new OnItemClickListener()
           
        View headerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fc_car_option_list_header, null, false);
        MainList.addHeaderView(headerView);
        
    	TextView HeaderText = (TextView)findViewById(R.id.fc_car_option_header_text);
    	
    	String Select = getString(R.string.fc_select);
    	HeaderText.setText(Select + OptionName);
         
    	CreateListIndex();
    	ListAdapter = new FCCarOptionAdapter(ListIndex,this);
        MainList.setAdapter(ListAdapter);
        MainList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
			// Forced logout by admin, or some other weird error
			setResult(FreewayCoffeeItemListView.RESULT_CODE_NOT_LOGGED_IN);
			finish();	
		}
		else if(resultCode==RESULT_OK)
		{
			// Just set OK and exit. The sub-activity (of this same Activity type) will have updated the global data, so we
			// are really just unraveling the chain of activities back to the main Tag Activity
			Intent intent = new Intent();
			setResult(RESULT_OK,intent);
			finish();
		}
	}
	
	private void CreateListIndex()
	{
		ListIndex = new ArrayList<FCCarOptionIndexHolder>();
		
		if(OptionListType==FreewayCoffeeUserTagActivity.LIST_ENTRY_MAKE)
		{
			FreewayCoffeeCarMakeModelData MakeData = appState.GetCarMakeModelData();
			
			
			for(Map.Entry<Integer, FreewayCoffeeCarMake> Entry : MakeData.GetMakeEntrySet())
			{
				if(Entry.getValue().IsNone()!=true)
				{
					FCCarOptionIndexHolder IndexEntry = new FCCarOptionIndexHolder();
					IndexEntry.OptionID = Entry.getKey();
					IndexEntry.OptionDescr = Entry.getValue().GetMakeLongDescr();
					IndexEntry.ShowMoreArrow = Entry.getValue().DoesMakeHaveModels();
					IndexEntry.SortOrder = Entry.getValue().GetSortOrder();
					ListIndex.add(IndexEntry);
				}
				
			}
		}
		else if(OptionListType==FreewayCoffeeUserTagActivity.LIST_ENTRY_MODEL)
		{
			FreewayCoffeeCarMake Make = appState.GetCarMakeModelData().GetCarMake(CarData.MakeID);
			FreewayCoffeeCarModelList Models = Make.GetModelList();
			for(Map.Entry<Integer,FreewayCoffeeCarModel> Entry: Models.GetModelEntrySet())
			{
				if(Entry.getValue().IsNone()!=true)
				{
					FCCarOptionIndexHolder IndexEntry = new FCCarOptionIndexHolder();
					IndexEntry.OptionID = Entry.getKey();
					IndexEntry.OptionDescr = Entry.getValue().GetModelLongDescr();
					IndexEntry.ShowMoreArrow = false;
					IndexEntry.SortOrder = Entry.getValue().GetSortOrder();
					ListIndex.add(IndexEntry);
				}
			}
		}
		else if(OptionListType==FreewayCoffeeUserTagActivity.LIST_ENTRY_COLOR)
		{
			FreewayCoffeeCarColorList ColorList = appState.GetCarMakeModelData().GetCarColorList();
			
			for(Map.Entry<Integer,FreewayCoffeeCarColor> Entry: ColorList.GetColorsEntrySet())
			{
				if(Entry.getValue().IsNone()!=true)
				{
					FCCarOptionIndexHolder IndexEntry = new FCCarOptionIndexHolder();
					IndexEntry.OptionID = Entry.getKey();
					IndexEntry.OptionDescr = Entry.getValue().GetColorLongDescr();
					IndexEntry.ShowMoreArrow = false;
					IndexEntry.SortOrder = Entry.getValue().GetSortOrder();
					ListIndex.add(IndexEntry);
				}
			}
		}
		
		// Gross. Apparently sub-classes for some reason cannot have statics
		// We create a temporary instance and just use that TODO FIXME GROSS
		FCCarOptionIndexHolder DummyIndexEntry = new FCCarOptionIndexHolder();
		Collections.sort(ListIndex,DummyIndexEntry.GetDisplayComparator());
	}
	
	private class FCCarOptionAdapter extends BaseAdapter
	{
		private ArrayList<FCCarOptionIndexHolder> Items;
		private LayoutInflater mInflater;
			
		public FCCarOptionAdapter(ArrayList<FCCarOptionIndexHolder> items, Context context)
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
				FCCarOptionIndexHolder Item = ListIndex.get(mPosition);
				
				if(OptionListType==FreewayCoffeeUserTagActivity.LIST_ENTRY_MAKE)
		    	{
					// IF we change the Make, lets reset the model because it wont make sense otherwise.
					if(appState.GetCarDataBeingEdited().MakeID!=Item.OptionID)
					{
						// TODO remove this hardcoded stuff.
						appState.GetCarDataBeingEdited().ModelID=FreewayCoffeeCarModel.CAR_MODEL_NONE;
					}
					appState.GetCarDataBeingEdited().MakeID=Item.OptionID;
					CarData.MakeID = Item.OptionID;
					//FreewayCoffeeCarMake Make = appState.GetCarMakeModelData().GetCarMake(CarData.MakeID);
					Intent intent = new Intent();
					if(Item.ShowMoreArrow==true)
					{
						// We have some models to select from
						intent.putExtra(FreewayCoffeeUserTagActivity.OPTION_PICK_TYPE,FreewayCoffeeUserTagActivity.LIST_ENTRY_MODEL);
			        	
          				intent.setClassName(FreewayCoffeeCarOptionActivity.this, FreewayCoffeeCarOptionActivity.class.getName());
          				startActivityForResult(intent,FreewayCoffeeUserTagActivity.LIST_ENTRY_MODEL);
						
					}
					else
					{
						setResult(RESULT_OK,intent);
						finish();
					}
					
		    	}
				else if(OptionListType==FreewayCoffeeUserTagActivity.LIST_ENTRY_MODEL)
				{
					Intent intent = new Intent();
					appState.GetCarDataBeingEdited().ModelID = Item.OptionID;
					CarData.ModelID=Item.OptionID;
					setResult(RESULT_OK,intent);
					finish();
				}
				else if(OptionListType==FreewayCoffeeUserTagActivity.LIST_ENTRY_COLOR)
				{
					Intent intent = new Intent();
					appState.GetCarDataBeingEdited().ColorID = Item.OptionID;
					CarData.ColorID=Item.OptionID;
					setResult(RESULT_OK,intent);
					finish();
				}
			}
		}
			    
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
			  
				// A FCListViewHolder keeps references to children views to avoid unneccessary calls
				// to findViewById() on each row.
				FCCarOptionHolder holder;
				  
				// When convertView is not null, we can reuse it directly, there is no need
				// to reinflate it. We only inflate a new View when the convertView supplied
				// by ListView is null
				if (convertView == null)
				{
					convertView = mInflater.inflate(R.layout.fc_car_option_list_row, null);
					// Creates a ViewHolder and store references to the two children views
					// we want to bind data to.
					holder = new FCCarOptionHolder();
					holder.MainText = (TextView) convertView.findViewById(R.id.fc_car_option_row_main_text);
					holder.RightImage = (ImageView)convertView.findViewById(R.id.fc_car_option_row_right_img);
					//holder.Position=position;
					
					convertView.setOnClickListener(new OnItemClickListener(position));
					convertView.setTag(holder);
				                     
				}
				else
				{
					// Get the ViewHolder back to get fast access to the TextView
					// and the ImageView.
					holder = (FCCarOptionHolder) convertView.getTag();
					// Make sure we set On-click here because when a view is re-used when removing, it may have a different Position so we need to update that.
					convertView.setOnClickListener(new OnItemClickListener(position));
					//holder.Position=position;
					//holder.EditButton.setTag(holder); // So we can tell what row for later
					//holder.RemoveButton.setTag(holder); 
				}
				  	              
				UpdateItem(position,holder);
				 
				  
				return convertView;

			  }
			  
			  private void UpdateItem(Integer position,FCCarOptionHolder holder)
			  {
				  FCCarOptionIndexHolder ListItem= ListIndex.get(position);
				  
				  holder.MainText.setText(ListItem.OptionDescr);
				  if(ListItem.ShowMoreArrow==true)
				  {
					  holder.RightImage.setVisibility(ImageView.VISIBLE);
					  holder.RightImage.setImageResource(R.drawable.menuright);
				  }
				  else
				  {
					  holder.RightImage.setVisibility(ImageView.INVISIBLE);
				  }
			  }
	  }
			  
	
	 private class FCCarOptionHolder
	 {
		 TextView MainText;
		 ImageView RightImage;
	 }
	 
	private class FCCarOptionIndexHolder
	{
		public Integer OptionID;
		public String OptionDescr;
		boolean ShowMoreArrow;
		public Integer SortOrder;
		
		public  Comparator<FCCarOptionIndexHolder> GetDisplayComparator()
		{
			return new Comparator<FCCarOptionIndexHolder>()
			{

				@Override
				public int compare(FCCarOptionIndexHolder object1, FCCarOptionIndexHolder object2) 
				{
					if(object1.SortOrder.equals(object2.SortOrder))
					{
						return object1.OptionDescr.compareToIgnoreCase(object2.OptionDescr);
						
					}
					else
					{
						return object1.SortOrder.compareTo(object2.SortOrder);
					}
				}
			};
		}
	}

}
