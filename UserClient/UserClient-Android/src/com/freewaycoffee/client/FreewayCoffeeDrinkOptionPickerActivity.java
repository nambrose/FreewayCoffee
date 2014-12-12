package com.freewaycoffee.client;

import java.math.BigDecimal;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FreewayCoffeeDrinkOptionPickerActivity extends Activity 
{
	private FreewayCoffeeApp appState;
	
	private  ArrayList<FreewayCoffeeFoodDrinkOptionDataListItem> ListIndex;
	private ListView listView;
	private Integer DrinkOptionGroupID;
	FreewayCoffeeFoodDrinkOptionGroup OptionGroup;
	private Integer ParentListPosition;
	private FreewayCoffeeFoodDrinkType DrinkTypeInfo;
	private Integer DrinkTypeID;
	private FCDrinkOptionPickerAdapter m_ListAdapter;
	private boolean m_DoesAnyOptionHaveMaxCountMoreThanOne; // For displaying the Done button on Single pick lists (I think it makes sense)
	private FreewayCoffeeFoodDrinkUserOptionList m_TheOptions;
	private FreewayCoffeeUserDrink m_ItemInProgress;
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		 super.onCreate(savedInstanceState);
	        
	     appState = ((FreewayCoffeeApp)getApplicationContext());
	     
	     m_DoesAnyOptionHaveMaxCountMoreThanOne = false;
	     setContentView(R.layout.fc_drink_option_picker_list);
	     
	     TextView UsernameView = (TextView)findViewById(R.id.fc_banner_text);
	     String AddStr = getString(R.string.select_a_drink_option);
	     
	     UsernameView.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + ", " + AddStr);
	     
	     // Get this just so's we can set it back again later. TODO must be a better way.
	     ParentListPosition = getIntent().getIntExtra(FreewayCoffeeItemListView.PARENT_LIST_POSITION, -1);
	     
	     // TODO Error check
	     DrinkOptionGroupID = getIntent().getIntExtra(FreewayCoffeeItemListView.USER_SELECT_DRINK_OPTION_ID,-1);
	     OptionGroup = appState.GetDrinkOptionGroups().get(DrinkOptionGroupID);
	     
	     // TODO ERROR CHECK BADLY NEEDED !!!!
	     DrinkTypeID = getIntent().getIntExtra(FreewayCoffeeItemListView.DRINK_TYPE,-1);
	     DrinkTypeInfo = appState.GetDrinkTypes().get(DrinkTypeID);
	     ListIndex = new ArrayList<FreewayCoffeeFoodDrinkOptionDataListItem>();
	     
	     m_ItemInProgress = appState.GetDrinkInProgress();
	     m_TheOptions = FreewayCoffeeFoodDrinkUserOptionList.CloneUserOptions(m_ItemInProgress.GetUserDrinkOptions());
	     CreateListIndex();
	     listView = (ListView)findViewById(R.id.fc_drink_option_picker_list);
	     
	     View footerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fc_drink_option_picker_list_footer, null, false);
	     listView.addFooterView(footerView);

	     if ( (OptionGroup.GetSelectionType()==FreewayCoffeeFoodDrinkOptionGroup.SelectionType.SelectOne) &&
	    		 (m_DoesAnyOptionHaveMaxCountMoreThanOne!=true))
	     {
	    	 // Done button is hidden for Single pick Options where all have a max of one or less. Primarily for sweetner where
	    	 // You can click the + button, it goes green but then you may not know to press the "sweetner" text to add.
	    	 // Done button will be a bit more obvious at least.
	    	 Button DoneButton  = (Button) findViewById(R.id.fc_drink_option_picker_done);
	    	 if(DoneButton!=null)
	    	 {
	    		 DoneButton.setVisibility(Button.INVISIBLE);
	    	 }
	     }
	     
	     m_ListAdapter = new FCDrinkOptionPickerAdapter(ListIndex,this);
	     listView.setAdapter(m_ListAdapter);
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
		
		ListIndex.clear();
		
		// Check to see if we have to add a NONE entry too
		
		FreewayCoffeeFoodDrinkOptionDataListItem IndexEntry=null;
		
		// If OptionGroup is not mandatory, add a "None" at the top so the user has a way to "deselect"
		// Right now, None is present for MultiSelect(as a shortcut to de-selecting all) and Pick one(needed). We will see if it looks odd on multiselect
		if(appState.IsOptionMandatoryForDrinkType(DrinkTypeID, DrinkOptionGroupID)==false)
		{
			IndexEntry = new FreewayCoffeeFoodDrinkOptionDataListItem();
			IndexEntry.OptionText = getString(R.string.none_text);
			IndexEntry.IsNone=true;
			IndexEntry.OptionSortOrder=0;
			ListIndex.add(IndexEntry);
		}
		for(FreewayCoffeeFoodDrinkOption Option : OptionGroup.GetFoodDrinkOptions())
		{
			
			if(DrinkTypeInfo.IsOptionValidForFoodDrinkType(DrinkOptionGroupID,Option.GetOptionID()))
			{
				IndexEntry = new FreewayCoffeeFoodDrinkOptionDataListItem();
			
				IndexEntry.IsNone=false;
				IndexEntry.OptionID = Option.GetOptionID();
				
				IndexEntry.OptionCount=0;
				
				IndexEntry.UserOption = m_TheOptions.FindUserDrinkOptionByDrinkOptionID(Option.GetOptionID());
				IndexEntry.Option = Option;
				if(IndexEntry.UserOption!=null)
				{
					IndexEntry.OptionText= IndexEntry.UserOption.MakeOptionValueString(appState, DrinkTypeID);
				}
				else
				{
					IndexEntry.OptionText= Option.GetOptionName();
				}
				IndexEntry.DrinkTypeOption=DrinkTypeInfo.FindDrinkTypeOptionByDrinkOptionID(Option.GetOptionGroupID(), Option.GetOptionID());
				IndexEntry.OptionGroup=appState.GetDrinkOptionGroups().get(Option.GetOptionGroupID());
				if(IndexEntry.UserOption!=null)
				{
					if(IndexEntry.UserOption.GetSortOrder()==null)
					{
						FreewayCoffeeFoodDrinkOption Op=  appState.FindDrinkOption(DrinkOptionGroupID,IndexEntry.UserOption.GetDrinkOptionID());
						if(Op!=null)
						{
							// May not have initially been present if menu was not downloaded
							IndexEntry.UserOption.SetSortOrder(Op.GetSortOrder());
						}
					}
					IndexEntry.OptionCount = IndexEntry.UserOption.GetOptionCount();
				}
				IndexEntry.OptionSortOrder=Option.GetSortOrder();
				if(IndexEntry.DrinkTypeOption.GetMaxCount()>1)
				{
					m_DoesAnyOptionHaveMaxCountMoreThanOne=true;
				}
				ListIndex.add(IndexEntry);
			
			}
		}	
		Collections.sort(ListIndex,FreewayCoffeeFoodDrinkOptionDataListItem.GetDisplayComparator());
		
 		
	}

	public void UserCanceled(View v)
    {
		setResult(RESULT_CANCELED, null);
		finish();
    }
	
	public void UserDone(View v)
    {
		FinishWithSuccess();
    }
	
	
	
	private void FinishWithSuccess()
	{
		Intent intent = new Intent();
		intent.putExtra(FreewayCoffeeItemListView.PARENT_LIST_POSITION,ParentListPosition);
		setResult(RESULT_OK,intent);
		m_ItemInProgress.ReplaceUserOptions(m_TheOptions); // Commit the options
		finish();  
	}
	
	private class FCDrinkOptionPickerAdapter extends BaseAdapter
	  {
			private ArrayList<FreewayCoffeeFoodDrinkOptionDataListItem> Items;
			private LayoutInflater mInflater;
			
			public FCDrinkOptionPickerAdapter(ArrayList<FreewayCoffeeFoodDrinkOptionDataListItem> items, Context context)
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
			  
			private class OnPlusClickListener implements View.OnClickListener
			{
				private int mPosition;
				OnPlusClickListener(int position)
				{
					mPosition = position;
				}
				@Override
				public void onClick(View arg0)
				{
					FreewayCoffeeFoodDrinkOptionDataListItem Item = Items.get(mPosition);
					if(Item==null)
					{
						return; // bad   	
			        }
					
					if(Item.OptionGroup.GetSelectionType()==FreewayCoffeeFoodDrinkOptionGroup.SelectionType.SelectOne)
					{
						m_TheOptions.RemoveAllOptionsForAnyOtherOptionInGroup(Item.OptionGroup.GetGroupID(),
								Item.OptionID); // Clear out any other Options because we can only have one
					}
					
					if(Item.OptionCount.compareTo(Item.DrinkTypeOption.GetMaxCount())<0)
					{
						// Count is smaller than Max
						Item.OptionCount+=1;
					}
					if(Item.UserOption==null)
					{
						Item.UserOption = Item.CreateUserDrinkOption();
						m_TheOptions.AddUserDrinkOption(Item.UserOption);

					}
					else
					{
						Item.UserOption.SetOptionCount(Item.OptionCount);
					}
					
					CreateListIndex();
					m_ListAdapter.notifyDataSetChanged();
					
							
			    }
			
			}
	private class OnMinusClickListener implements View.OnClickListener
	{
		private int mPosition;
		OnMinusClickListener(int position)
		{
			mPosition = position;
		}
		@Override
		public void onClick(View arg0)
		{
			FreewayCoffeeFoodDrinkOptionDataListItem Item = Items.get(mPosition);
			if(Item==null)
			{
				return; // bad   	
	        }               
			if(Item.OptionCount.compareTo(Item.DrinkTypeOption.GetMinCount())>0)
			{
				// Count is smaller than Max
				Item.OptionCount-=1;
				if(Item.UserOption!=null)
				{
					Item.UserOption.DecrementCount();
					if(Item.OptionCount==0)
					{
						m_TheOptions.RemoveOptionByOptionID(Item.OptionID);
					}
				}
				
			}
			CreateListIndex();
			m_ListAdapter.notifyDataSetChanged();
			 	
	      }               
	
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
					FreewayCoffeeFoodDrinkOptionDataListItem Item = Items.get(mPosition);
					if(Item==null)
					{
						return; // bad
					}
					if(Item.IsNone==true)
					{
						m_TheOptions.RemoveAllOptionsForOptionGroup(OptionGroup.GetGroupID());
						FinishWithSuccess();
						
						return;
					}
					// Must do this bit before create the UserItem !
					if(Item.OptionCount==0)
					{
						Item.OptionCount=1; // This way, we dont add it with a count of zero and selecting it, will add with a count of 1
					}
					FreewayCoffeeFoodDrinkUserOption UserOption = Item.CreateUserDrinkOption();
					
					
					if(ListIndex.get(mPosition).OptionGroup.GetSelectionType()==FreewayCoffeeFoodDrinkOptionGroup.SelectionType.SelectOne)
					{
						m_TheOptions.RemoveAllOptionsForOptionGroup(Item.OptionGroup.GetGroupID() ); // Clear out any other Options because we can only have one
						m_TheOptions.AddUserDrinkOption(UserOption);
						FinishWithSuccess();
						
						return;
			        }
					else if(Item.UserOption==null)
					{
						// Dont add again and again just by clicking it.
						m_TheOptions.AddUserDrinkOption(UserOption);
						CreateListIndex();
						m_ListAdapter.notifyDataSetChanged();
					}
					
			    }
			}
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
			  
				// A FCListViewHolder keeps references to children views to avoid unneccessary calls
				// to findViewById() on each row.
				FCDrinkOptionDataPickerHolder holder;
				  
				// When convertView is not null, we can reuse it directly, there is no need
				// to reinflate it. We only inflate a new View when the convertView supplied
				// by ListView is null
				if (convertView == null)
				{
					convertView = mInflater.inflate(R.layout.fc_drink_option_picker_list_row, parent,false);
					// Creates a ViewHolder and store references to the two children views
					// we want to bind data to.
					holder = new FCDrinkOptionDataPickerHolder();
					holder.item_descr = (TextView) convertView.findViewById(R.id.fc_drink_option_picker_row_text);
					holder.item_cost = (TextView) convertView.findViewById(R.id.fc_option_picker_row_cost);
					holder.MinusButton = (FreewayCoffeeNoParentPressImageButtonView) convertView.findViewById(R.id.fc_drink_option_picker_row_minus_but);
					holder.PlusButton = (FreewayCoffeeNoParentPressImageButtonView) convertView.findViewById(R.id.fc_drink_option_picker_row_plus_but);
					//holder.item_icon = (ImageView) convertView.findViewById(R.id.fc_drink_option_picker_row_img);
					  
					holder.MinusButton.setOnClickListener(new OnMinusClickListener(position));
					holder.PlusButton.setOnClickListener(new OnPlusClickListener(position));
					holder.item_small_descr = (TextView) convertView.findViewById(R.id.fc_drink_option_picker_row_small_text);
					convertView.setOnClickListener(new OnItemClickListener(position));
					convertView.setTag(holder);
				                     
				}
				else
				{
					// Get the ViewHolder back to get fast access to the TextView
					// and the ImageView.
					holder = (FCDrinkOptionDataPickerHolder) convertView.getTag();
					convertView.setOnClickListener(new OnItemClickListener(position));
					holder.MinusButton.setOnClickListener(new OnMinusClickListener(position));
					holder.PlusButton.setOnClickListener(new OnPlusClickListener(position));
				}
				  	              
				UpdateItem(position,holder);
				 
				  
				return convertView;

			  }
			  
			  private void UpdateItem(Integer position,FCDrinkOptionDataPickerHolder holder)
			  {
				  
				  
				  //holder.item_small_descr.setText(appState.GetDrinkSmallTextFromID(Items.get(position).DrinkID));
				  FreewayCoffeeFoodDrinkOptionDataListItem Item = Items.get(position);
				  if(Item==null)
				  {
					  return; // Not good.
				  }
				  if(Item.IsNone==true)
				  {
					  holder.item_descr.setTextColor(getResources().getColor(R.color.Black));
					  holder.item_small_descr.setText("");
					  holder.item_cost.setText("");
					  holder.item_descr.setText(Item.OptionText);
					  holder.PlusButton.setVisibility(Button.INVISIBLE);
					  holder.MinusButton.setVisibility(Button.INVISIBLE);
					  return;
				  }
				  String CostPer = Item.DrinkTypeOption.GetCostPer();
				  holder.item_small_descr.setText("");
				  
				  if( (CostPer!=null) &&
					  (!CostPer.equals("")) &&
					  (!CostPer.equals("0.00") ))
				  {
					  holder.item_cost.setText("$" + CostPer);
					  if(Item.OptionCount!=0)
					  {  
						  if( (Item.DrinkTypeOption.GetMaxCount()>1) && (Item.UserOption!=null))
						  {
							  // Multiplies (or not) as needed
							  BigDecimal Cost =Item.UserOption.GetTotalCost(appState,DrinkTypeID);
							  
							  holder.item_small_descr.setText("Total Cost: $" + Cost.toPlainString());
						  }
					  }
				  }
				  else
				  {
					  holder.item_cost.setText("");
				  }
				  
				  /*
				  if( (Item.OptionGroup.GetSelectionType()==FreewayCoffeeFoodDrinkOptionGroup.SelectionType.SelectOne)  &&
				    (Item.DrinkTypeOption.GetMaxCount()==1) )
				  {
					  holder.MinusButton.setVisibility(Button.INVISIBLE);
					  holder.PlusButton.setVisibility(Button.INVISIBLE);
					  
				  }
				  */
				  
				  /*if(Item.OptionCount>1)
				  {
					  holder.item_descr.setText(Items.get(position).OptionText + " (" + Item.OptionCount + ")");
				  }
				  */
				  //else
				  //{
					  holder.item_descr.setText(Items.get(position).OptionText);
				  //}
				  
				  if(Item.OptionCount.equals(Item.DrinkTypeOption.GetMinCount()))
				  {
					  holder.MinusButton.setVisibility(Button.INVISIBLE);
				  }
				  else
				  {
					  holder.MinusButton.setVisibility(Button.VISIBLE);
				  }
				  if(Item.OptionCount.equals(Item.DrinkTypeOption.GetMaxCount()))
				  {
					  holder.PlusButton.setVisibility(Button.INVISIBLE);
				  }
				  else
				  {
					  holder.PlusButton.setVisibility(Button.VISIBLE);
				  }
				  if ( (Item.OptionGroup.GetSelectionType()==FreewayCoffeeFoodDrinkOptionGroup.SelectionType.SelectOne) &&
						  (Item.DrinkTypeOption.GetMaxCount()==1) &&
						  (Item.DrinkTypeOption.GetMinCount()==0))
				  {
					 holder.PlusButton.setVisibility(Button.INVISIBLE);
					 holder.MinusButton.setVisibility(Button.INVISIBLE);
					  
				  }
				  if(Item.UserOption!=null)
				  {
					  holder.item_descr.setTextColor(getResources().getColor(R.color.Green));
				  }
				  else
				  {
					  holder.item_descr.setTextColor(getResources().getColor(R.color.Black));
				  }
				  //holder.item_icon.setImageResource(R.drawable.fc_drink);
			  }
			  
			  class FCDrinkOptionDataPickerHolder
			  {
				  TextView item_descr;
				  TextView item_small_descr;
				  TextView item_cost;
				  FreewayCoffeeNoParentPressImageButtonView MinusButton;
				  FreewayCoffeeNoParentPressImageButtonView PlusButton;
				  //ImageView item_icon;
				 
			  }
	}
}

