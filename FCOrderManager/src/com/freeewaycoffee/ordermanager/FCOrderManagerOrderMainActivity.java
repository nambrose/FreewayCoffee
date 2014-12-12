package com.freeewaycoffee.ordermanager;


import java.util.ArrayList;
import java.util.Collections;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener; 
//import android.widget.Toast;



public class FCOrderManagerOrderMainActivity extends Activity 
{
	static private Integer PollInterval = 1000;
	private long m_LastDownloadComplete;
	private long m_LastGUIUpdate;
	
	private FCOrderManagerApp appState;
	private FCOMMainAdapter m_MainAdapter;
	private ExpandableListView listView;
	static final private int SCHEMA_ERROR_DIALOG=1;
	static final private int LOGIN_FAIL_DIALOG=2;
	static final private int NETWORK_ERROR_DIALOG=3;
	private String SchemaError;
	FCOrderManagerOrderMainAsync Async;
	private Handler m_Handler = new Handler();
	private long m_StartTime=0;
	//private long m_LastTime=0;
	private ArrayList<FCOrderManagerOrder> m_MainOrderListIndex;
	private ArrayList<FCOrderManagerOrder> m_ArrivingCustomersOrderListIndex;
	private ListView m_ArrivingList;
	private FCOOrderManagerArrivingListAdapter m_ArrivingAdapter;
	private CheckBox m_HideCompleted;
	
	
	private Runnable m_UpdateTimeTask = new Runnable() 
	{
		   public void run() 
		   {
		       //final long start = m_StartTime;
		       //long now = System.currentTimeMillis();
		       long now = SystemClock.uptimeMillis();
		       //long millis = now - m_LastTime;
		       //Log.e("OM","Update Running");
		       if((Async==null) && ((now-m_LastDownloadComplete) > appState.GetPreferenceOrderUpdateIntervalInMillisec()) )
		       {
		    	   //Log.e("OM","Order Download Running");
		    	   DoDownload(true); 
		       }

		       if( (now-m_LastGUIUpdate)>appState.GetPreferenceGUIUpdateIntervalInMillisec())
		       {
		    	   //Log.e("OM","Update GUI Running");
		    	   CreateListIndex();
		    	   m_LastGUIUpdate =SystemClock.uptimeMillis();
		       }
		    	   
		       m_Handler.postDelayed(m_UpdateTimeTask, PollInterval);
		      
		   }
		};
		
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		m_LastDownloadComplete=0;
		m_LastGUIUpdate=0;
		
		
		appState = ((FCOrderManagerApp)getApplicationContext());     
		setContentView(R.layout.fc_om_order_main);
		@SuppressWarnings("deprecation")
		Object retained = getLastNonConfigurationInstance(); 
		if(retained instanceof FCOrderManagerOrderMainAsync)
		{

			Async = (FCOrderManagerOrderMainAsync)retained;
			//showProgressDialog();// This must be before setActivity as that calls ProcessXML which destroys the dialog --- so we dont want to just re-create it again
			Async.SetActivity(this);
		}
		
		
		listView = (ExpandableListView)findViewById(R.id.fc_om_order_main_list);
		
		View headerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fc_om_order_main_list_header, null, false);
	    listView.addHeaderView(headerView);
	     
		m_MainOrderListIndex = new ArrayList<FCOrderManagerOrder>();
		m_ArrivingCustomersOrderListIndex= new ArrayList<FCOrderManagerOrder>();
		
		m_MainAdapter = new FCOMMainAdapter(this,m_MainOrderListIndex);
        listView.setAdapter(m_MainAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        m_ArrivingList = (ListView) findViewById(R.id.fc_om_order_arriving_list);
    	m_ArrivingAdapter = new FCOOrderManagerArrivingListAdapter(m_ArrivingCustomersOrderListIndex,this);
    	m_ArrivingList.setAdapter(m_ArrivingAdapter);
    	m_ArrivingList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    	
    	m_HideCompleted = (CheckBox)findViewById(R.id.fc_om_main_hide_completed_orders_check);
    	if(m_HideCompleted!=null)
    	{
    		m_HideCompleted.setOnCheckedChangeListener(new OnCheckedChangeListener()
    		{
    			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    			{
    				appState.SetPreferenceHideCompletedOrders(isChecked);
    				CreateListIndex();

    			}
    		});
    		m_HideCompleted.setChecked( appState.GetPreferenceHideCompletedOrders());
    	}
    	
        DoDownload(false);
        m_StartTime=SystemClock.uptimeMillis();
		//m_LastTime=m_StartTime;
		//mStartTime = System.currentTimeMillis();
		m_Handler.postDelayed(m_UpdateTimeTask, PollInterval);
	}

	private void DoDownload(boolean fromTimer)
	{
		String LocationID = appState.GetUserLocationID();
		
		if(appState.NeedDownloadAll()==true)
		{
			if(LocationID!=null)
			{
				//Log.e("OM","Download today Timer: " + fromTimer);
				Async = new FCOrderManagerOrderMainAsync(this,appState);
				Async.execute(appState.MakeGetTodaysOrdersForLocationAndHere(LocationID ));
			}
		}
		else
		{
			//Log.e("OM","Download after Order:" + appState.GetHighestOrderID() +" Timer: " + fromTimer);
			Async = new FCOrderManagerOrderMainAsync(this,appState);
			Async.execute(appState.MakeGetOrdersAfterOrderIDForLocationAndHere(LocationID,appState.GetHighestOrderID()));
		}
	}
	
	private void CreateListIndex()
	{
		m_MainOrderListIndex.clear();
		m_ArrivingCustomersOrderListIndex.clear();
		
		// Get it once because this one accesses SharedPrefs each time
		boolean HideCompletedOrders = appState.GetPreferenceHideCompletedOrders();
		
		// Main List first.
		for(Map.Entry<Integer,FCOrderManagerOrder> Entry : appState.GetOrderList().entrySet())
		{
			FCOrderManagerOrder CurrOrder = Entry.getValue();
			if( HideCompletedOrders==true) 
			{
				if(CurrOrder.IsOrderCompleted()==true)
				{
					continue;
				}
			}
			m_MainOrderListIndex.add(CurrOrder);
			
			if( (CurrOrder.IsOrderCompleted()!=true) && (CurrOrder.IsCustomerHere()))
			{
				m_ArrivingCustomersOrderListIndex.add(CurrOrder);
			}
		}
		
		// Then sort the suckers
		Collections.sort(m_MainOrderListIndex, FCOrderManagerOrder.ComparatorTimeReceived);
		Collections.sort(m_ArrivingCustomersOrderListIndex, FCOrderManagerOrder.ComparatorTimeHere);
		m_MainAdapter.notifyDataSetChanged();
		m_ArrivingAdapter.notifyDataSetChanged();
		m_MainAdapter.RestoreExpandedState();
	}
	
	public void DoMainListRefund(View v)
	{
		Integer Position = (Integer) v.getTag();
		FCOrderManagerOrder Order = m_MainOrderListIndex.get( (int)Position);
		if(Order!=null)
		{
			Order.SetDispositionIntAndString(FCOrderManagerOrder.ORDER_REFUNDED);
		}
		CreateListIndex();
	}
	
	
	public void DoMainListInProcess(View v)
	{
		Integer Position = (Integer) v.getTag();
		FCOrderManagerOrder Order = m_MainOrderListIndex.get( (int)Position);
		if(Order!=null)
		{
			Order.SetDispositionIntAndString(FCOrderManagerOrder.ORDER_INPROGRESS);
		}
		CreateListIndex();
	}
	
	
	public void DoMainListNoShow(View v)
	{
		Integer Position = (Integer) v.getTag();
		FCOrderManagerOrder Order = m_MainOrderListIndex.get( (int)Position);
		if(Order!=null)
		{
			Order.SetDispositionIntAndString(FCOrderManagerOrder.ORDER_NOSHOW);
		}
		CreateListIndex();
	}
	
	
	public void DoMainListDelivered(View v)
	{
		Integer Position = (Integer) v.getTag();
		FCOrderManagerOrder Order = m_MainOrderListIndex.get( (int)Position);
		if(Order!=null)
		{
			Order.SetDispositionIntAndString(FCOrderManagerOrder.ORDER_DELIVERED);
		}
		CreateListIndex();
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

	private void UnlinkAsync()
	{
		if(Async!=null)
		{
			Async.UnlinkActivity();
			Async=null;
		}
	}
	private void DisplayNetworkError()
	{
		showDialog(NETWORK_ERROR_DIALOG);
		/*
	    	Toast Err = Toast.makeText(appState.getApplicationContext(),
					getString(R.string.fc_network_error),
					Toast.LENGTH_SHORT);
	    	Err.show();
		 */
	}
	
	@SuppressWarnings("deprecation")
	public void ProcessXMLResult(FCOrderManagerOrderMainXMLHandler Handler)
	{
		//Log.w("FreewayCoffeeDB",XML);
		/* Get a SAXParser from the SAXPArserFactory. */
		//AsyncGet=null;
		UnlinkAsync();
		m_LastDownloadComplete = SystemClock.uptimeMillis();
		if(Handler==null)
		{
			//DisplayNetworkError();
			return;
		}
		if(Handler.NetworkError==true)
		{

			//DisplayNetworkError();
			return;
		}

		if(Handler.ResponseType==FCOrderManagerXMLHelper.ResponseTypeEnum.SIGNON)
		{	
			if(Handler.SignonResponse!=FCOrderManagerXMLHelper.SignonResponseEnum.OK)
			{
				showDialog(LOGIN_FAIL_DIALOG);
				finish();
			}
		}
		else if(Handler.ResponseType==FCOrderManagerXMLHelper.ResponseTypeEnum.ORDER_AND_TIME_HERE_DOWNLOAD)
		{
			AddDownloadedOrdersToMainList(Handler.OrderList);
			ProcessCustomerHereData(Handler.m_CustomerHereData);
			if( (Handler.OrderList.size()>0) || (Handler.m_CustomerHereData.size()>0))
			{
				CreateListIndex();
			}
			
		}
	}

	private void ProcessCustomerHereData(ArrayList<FCOrderManagerCustomerHereItem> CustomerHereData)
	{
		for(FCOrderManagerCustomerHereItem Item  : CustomerHereData)
		{
			appState.ProcessCustomerHereItem(Item);
		}
	}
	protected void onDestroy ()
	{
		//Log.e("OM","onDestroy");
		
		 super.onDestroy();
		 //AsyncGet.cancel(true);
		 if(Async!=null)
		 {
			 Async.UnlinkActivity();
		 }
		 m_Handler.removeCallbacks(m_UpdateTimeTask);
	}
	 
	 @Override
	 public Object onRetainNonConfigurationInstance()
	 {
		 if(Async!=null)
		 {
			 Async.UnlinkActivity();
			 return Async;
		 }
		 return null;
	 }
	private void AddDownloadedOrdersToMainList(ArrayList<FCOrderManagerOrder> List)
	{
		for(int Index=0;Index<List.size();Index++)
		{
			appState.AddOrder(List.get(Index));
		}
		
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

	public void DoArriveListDelivered(View v)
	{
		if(v==null)
		{
			return;
		}
		Integer Pos = (Integer)(v.getTag());
		int Position = (int)(Pos); // Get the row# that we put there in getView in the adapter.
		
		FCOrderManagerOrder Order = m_ArrivingCustomersOrderListIndex.get(Position);
		if(Order==null)
		{
			return;
		}
		Order.SetDispositionIntAndString(FCOrderManagerOrder.ORDER_DELIVERED);
		
		CreateListIndex();
	}
	

	private class FCOOrderManagerArrivingListAdapter extends BaseAdapter
	{
		
		private ArrayList<FCOrderManagerOrder> m_Items;
		private LayoutInflater mInflater;
		
		public FCOOrderManagerArrivingListAdapter(ArrayList<FCOrderManagerOrder> items, Context context)
		{
				             
			m_Items = items;
			mInflater = LayoutInflater.from(context);
		}
		
		//@Override
		public int getCount()
		{
			int count=m_Items.size();
			return m_Items.size();
		}
		  	 
		
		
		//@Override
		public Object getItem(int position)
		{
			return m_Items.get(position);
		}
		  
		//@Override
		public long getItemId(int position)
		{
			return position;
		}
		  
		//@Override
		
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// A FCListViewHolder keeps references to children views to avoid unneccessary calls
			// to findViewById() on each row.
			FCOMArrivingHolder holder;
			  
			// When convertView is not null, we can reuse it directly, there is no need
			// to reinflate it. We only inflate a new View when the convertView supplied
			// by ListView is null
			if (convertView == null)
			{
				
				convertView = mInflater.inflate(R.layout.fc_om_order_arriving_list_row, null);
				// Creates a ViewHolder and store references to the two children views
				// we want to bind data to.
				holder = new FCOMArrivingHolder();
				holder.OrderTitle=(TextView) convertView.findViewById(R.id.fc_om_order_arriving_title);
				holder.OrderCarInfo=(TextView) convertView.findViewById(R.id.fc_om_order_arriving_car);
				holder.OrderNumItems=(TextView) convertView.findViewById(R.id.fc_om_order_arriving_item_count);
				holder.OrderTotal=(TextView) convertView.findViewById(R.id.fc_om_order_arriving_item_cost);
				holder.ActionsBut = (FCOrderManagerNoParentPressButton) convertView.findViewById(R.id.fc_om_order_arriving_actions);
				  
				holder.ActionsBut.setTag(position);
				convertView.setTag(holder);
				//convertView.setOnClickListener(new OnItemClickListener(position)); 
				
			                     
			}
			else
			{
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (FCOMArrivingHolder) convertView.getTag();
				// Make sure we set On-click here because when a view is re-used when removing, it may have a different Position so we need to update that.
				//convertView.setOnClickListener(new OnItemClickListener(position)); 
				//holder.remove_button.setTag(position); // So we can get the offending line later when we want to 86 it
			}
			UpdateItem(position,holder);
		  return convertView;

		  }
		 
		private void UpdateItem(int Position, FCOMArrivingHolder Holder)
		{
			FCOrderManagerOrder Order = m_Items.get(Position);
			if(Order==null)
			{
				// Bad
				return;
			}
			
			String Title = "Order #" + Order.GetOrderID() + " | " + Order.GetUserName();
			Holder.OrderTitle.setText(Title);
			Holder.OrderCarInfo.setText(Order.GetUserCarInfoAndTagForDisplay());
			String ItemString = String.valueOf(Order.GetItemCount()) + " Items";
			
			Holder.OrderNumItems.setText(ItemString);
			Holder.OrderTotal.setText(Order.GetTotalCostForDisplay());
			
		}
			       
		
		  class FCOMArrivingHolder
		  {
			  TextView OrderTitle;
			  TextView OrderCarInfo;
			  TextView OrderNumItems;
			  TextView OrderTotal;
			  FCOrderManagerNoParentPressButton ActionsBut;
			  //FreewayCoffeeNoParentPressImageButtonView remove_button;
		  }
	}
		  
		  private class FCOMMainAdapter extends BaseExpandableListAdapter
			{
				private static final int VIEW_TYPE_CHILD_HEADER_BUTTON_ROW=0;
				private static final int VIEW_TYPE_CHILD_MAIN_ROW=1;
			
				private LayoutInflater m_Inflater;
				private FCOrderManagerApp appState;
				ArrayList<FCOrderManagerOrder> m_Items;
				private Set<Integer> m_MainOrderListExpandedOrderIDs;
				public FCOMMainAdapter(Context context,ArrayList<FCOrderManagerOrder> Items)
				{
					appState = ((FCOrderManagerApp)getApplicationContext()); 
					m_Inflater = LayoutInflater.from(context);
					m_Items=Items;
					m_MainOrderListExpandedOrderIDs = new HashSet<Integer>();
				}
				
				public long getChildId(int Group, int Child)
				{
					return Child;
				}
				
				public void RestoreExpandedState()
				{
					int groupIndex=0;
					for(FCOrderManagerOrder Order : m_Items)
					{
						if(m_MainOrderListExpandedOrderIDs.contains(Order.GetOrderID()))
						{
							FCOrderManagerOrderMainActivity.this.listView.expandGroup(groupIndex);
						}
						else
						{
							FCOrderManagerOrderMainActivity.this.listView.collapseGroup(groupIndex);
						}
						groupIndex++;
					}
				}
				
				public void onGroupCollapsed(int groupPosition)
				{
					
					FCOrderManagerOrder Order = m_Items.get(groupPosition);
					if(Order!=null)
					{
						m_MainOrderListExpandedOrderIDs.remove(Order.GetOrderID());
					}
				}

				
				public void onGroupExpanded(int groupPosition)
				{
					
					FCOrderManagerOrder Order = m_Items.get(groupPosition);
					if(Order!=null)
					{
						m_MainOrderListExpandedOrderIDs.add(Order.GetOrderID());
					}
				}
				
				
				public int getGroupCount() 
				{
				   
					return m_Items.size();
				}

				public boolean isChildSelectable(int Group, int Child)
				{
					return true;
				}
				public long getGroupId(int Group)
				{
					//FCOrderManagerOrder Order= appState.GetOrderList().get(Group);
					//return Order.GetOrderID();
					return Group;
				}
				public Object getChild(int Group,int Child)
				{
					return null;
				}
				public Object getGroup(int Group)
				{
					//return Group;
					return null;
				}
				
				public boolean hasStableIds()
				{
					return false;
				}
			
				public int getChildTypeCount() 
				{
				    return 2;
				}
				
				public int getChildType(int groupPosition, int childPosition)
				{
				
					if(childPosition==0)
					{
						return VIEW_TYPE_CHILD_HEADER_BUTTON_ROW;
					}
					else
					{
						return VIEW_TYPE_CHILD_MAIN_ROW;
					}
					
				}
				
				public View getGroupView(int groupPosition, boolean isExpanded,
						View convertView, ViewGroup parent)
				{
					MainAdapterGroupHolder holder;
					if (convertView == null)
					{
						
						convertView = m_Inflater.inflate(R.layout.fc_om_order_main_list_row, null);
						
						holder = new MainAdapterGroupHolder();
						
						holder.GroupIcon = (ImageView) convertView.findViewById(R.id.fc_om_order_main_list_group_icon);
						holder.OrderID = (TextView) convertView.findViewById(R.id.fc_om_order_main_list_order_id);
						holder.TimeReceived = (TextView) convertView.findViewById(R.id.fc_om_order_main_list_time_received);
						holder.TimeNeeded = (TextView) convertView.findViewById(R.id.fc_om_order_main_list_time_needed);
						holder.UserName = (TextView) convertView.findViewById(R.id.fc_om_order_main_list_user_name);
						holder.TotalItems = (TextView) convertView.findViewById(R.id.fc_om_order_main_list_item_count);
						holder.TotalCost = (TextView) convertView.findViewById(R.id.fc_om_order_main_list_total_cost);
						holder.Disposition = (TextView) convertView.findViewById(R.id.fc_om_order_main_list_disposition);
						holder.Here = (TextView) convertView.findViewById(R.id.fc_om_order_main_list_here);
						holder.MainLay = (LinearLayout) convertView.findViewById(R.id.fc_om_order_main_group_lay);
						convertView.setTag(holder);
						//convertView.setOnClickListener(new OnItemClickListener(position)); 
					                     
					}
					else
					{
						// Get the ViewHolder back to get fast access to the TextView
						// and the ImageView.
						holder = (MainAdapterGroupHolder) convertView.getTag();
						// Make sure we set On-click here because when a view is re-used when removing, it may have a different Position so we need to update that.
						//convertView.setOnClickListener(new OnItemClickListener(position)); 
						//holder.remove_button.setTag(position); // So we can get the offending line later when we want to 86 it
					}
					  	      
					
					if(isExpanded==true)
					{
						holder.GroupIcon.setImageResource(R.drawable.fc_om_list_expanded);
					}
					else
					{
						holder.GroupIcon.setImageResource(R.drawable.fc_om_list_closed);
					}
					UpdateGroupItem(groupPosition,holder);
					 

					return convertView;
				}
				
				
				private void UpdateGroupItem(int groupPosition,MainAdapterGroupHolder holder)
				{
					
					UpdateGroupData(groupPosition,holder);
					
				}
				
				private void UpdateChildHeaders(MainAdapterChildHolder holder)
				{
					holder.ItemType.setText("Type");
					holder.ItemDescription.setText("Description");
					holder.ItemCost.setText("Cost");
				}
				private void UpdateGroupData(int groupPosition,MainAdapterGroupHolder holder)
				{
					FCOrderManagerOrder Order = m_Items.get(groupPosition);
					holder.GroupIcon.setVisibility(ImageView.VISIBLE);
					holder.OrderID.setText(String.valueOf(Order.GetOrderID()));
					holder.TimeReceived.setText(Order.GetTimeReceivedDisplay());
					holder.TimeNeeded.setText(Order.GetTimeNeededJustTime());
					holder.UserName.setText(Order.GetDisplayName());
					holder.TotalItems.setText(String.valueOf(Order.GetItemCount()));
					holder.TotalCost.setText(Order.GetTotalCostForDisplay());
					holder.Disposition.setText(Order.GetDisposition());
					if(Order.GetTimeUserHere()==null || (Order.GetTimeUserHere().length()==0))
					{
						holder.Here.setText("No");
					}
					else
					{
						holder.Here.setText("Yes");
					}
					if(Order.GetItemCount()==0)
					{
						holder.GroupIcon.setVisibility(ImageView.INVISIBLE);
					}
					else
					{
						holder.GroupIcon.setVisibility(ImageView.VISIBLE);
					}
					
					if(Order.IsOrderLate()==true && (Order.IsOrderCompleted()==false))
					{
						Drawable GroupLate = getResources().getDrawable( R.drawable.fc_om_late_bg);
						holder.MainLay.setBackgroundDrawable(GroupLate);
					}
					else
					{
						holder.MainLay.setBackgroundDrawable(null);
					}
				}
				
				private void UpdateChildData(int groupPosition,int childPosition,MainAdapterChildHolder holder)
				{
					FCOrderManagerOrder Order = m_Items.get(groupPosition);
					
					// TODO CHECK
					// Subtract one as we have the header row ... that is always zero
					FCOrderManagerOrderItem OrderItem= Order.GetOrderItems().GetItemAtIndex(childPosition-2);
					
					holder.ItemType.setText(OrderItem.GetItemTypeAsString());
					holder.ItemDescription.setText(OrderItem.GetItemDescription());
					holder.ItemCost.setText(OrderItem.GetItemCost());
					
					
				}

				
				private void UpdateChildItem(int groupPosition,int childPosition, MainAdapterChildHolder holder)
				{
					if(childPosition==1)
					{
						UpdateChildHeaders(holder);
					}
					else
					{
						UpdateChildData(groupPosition,childPosition,holder);
					}
				}
				
				public View getChildView(int groupPosition, int childPosition,
						boolean isLastChild, View convertView, ViewGroup parent) 
				{
					if(childPosition==0)
					{
						return getChildViewButtonHeader(groupPosition,childPosition,isLastChild,convertView,parent);
					}
					else
					{
						return getChildViewMainRow(groupPosition,childPosition,isLastChild,convertView,parent);
					}
				}
				
				public View getChildViewButtonHeader(int groupPosition, int childPosition,
						boolean isLastChild, View convertView, ViewGroup parent) 
				{
					/*
					
					class MainAdapterButtonHeaderHolder
					{
						//TextView m_TitleText;
						FCOrderManagerNoParentPressButton m_RefundButton;
						FCOrderManagerNoParentPressButton m_NoShowButton;
						FCOrderManagerNoParentPressButton m_DeliveredButton;
						FCOrderManagerNoParentPressButton m_InProcessButton;
					}
					*/
					MainAdapterButtonHeaderHolder holder;
					if (convertView == null)
					{
						
						convertView = m_Inflater.inflate(R.layout.fc_om_order_main_list_child_but_header, null);
						
						holder = new MainAdapterButtonHeaderHolder();
				
						holder.m_RefundButton = (FCOrderManagerNoParentPressButton)
									convertView.findViewById(R.id.fc_om_order_main_list_child_header_refund_but);
						holder.m_NoShowButton = (FCOrderManagerNoParentPressButton) 
									convertView.findViewById(R.id.fc_om_order_main_list_child_header_noshow_but);
						holder.m_DeliveredButton = (FCOrderManagerNoParentPressButton) 
									convertView.findViewById(R.id.fc_om_order_main_list_child_header_delivered_but);
						holder.m_InProcessButton = (FCOrderManagerNoParentPressButton) 
								convertView.findViewById(R.id.fc_om_order_main_list_child_header_in_process_but);
						
						convertView.setTag(holder);
						SetChildButtonTags(holder,groupPosition);
						//convertView.setOnClickListener(new OnItemClickListener(position)); 
					                     
					}
					else
					{
						// Get the ViewHolder back to get fast access to the TextView
						// and the ImageView.
						holder = (MainAdapterButtonHeaderHolder) convertView.getTag();
						SetChildButtonTags(holder,groupPosition);
						// Make sure we set On-click here because when a view is re-used when removing, it may have a different Position so we need to update that.
						//convertView.setOnClickListener(new OnItemClickListener(position)); 
						//holder.remove_button.setTag(position); // So we can get the offending line later when we want to 86 it
					}
					return convertView;
					
				}
				
				private void SetChildButtonTags(MainAdapterButtonHeaderHolder holder, int Position)
				{
					Integer Pos = new Integer(Position);
					if(holder==null)
					{
						return;
					}
					holder.m_RefundButton.setTag(Pos);
					holder.m_NoShowButton.setTag(Pos);
					holder.m_InProcessButton.setTag(Pos);
					holder.m_DeliveredButton.setTag(Pos);
					
					
				}
				public View getChildViewMainRow(int groupPosition, int childPosition,
						boolean isLastChild, View convertView, ViewGroup parent) 
				{
					MainAdapterChildHolder holder;
					if (convertView == null)
					{
						
						convertView = m_Inflater.inflate(R.layout.fc_om_order_main_list_row_child, null);
						
						holder = new MainAdapterChildHolder();
				
						holder.ItemType = (TextView) convertView.findViewById(R.id.fc_om_order_main_list_child_item_type);
						holder.ItemDescription = (TextView) convertView.findViewById(R.id.fc_om_order_main_list_child_item_description);
						holder.ItemCost = (TextView) convertView.findViewById(R.id.fc_om_order_main_list_child_item_cost);
						holder.ChildLay = (LinearLayout) convertView.findViewById(R.id.fc_om_order_main_list_child_item_lay);
						holder.ChildMainLay = (LinearLayout) convertView.findViewById(R.id.fc_om_order_main_list_child_item_lay_main);
						convertView.setTag(holder);
						//convertView.setOnClickListener(new OnItemClickListener(position)); 
					                     
					}
					else
					{
						// Get the ViewHolder back to get fast access to the TextView
						// and the ImageView.
						holder = (MainAdapterChildHolder) convertView.getTag();
						// Make sure we set On-click here because when a view is re-used when removing, it may have a different Position so we need to update that.
						//convertView.setOnClickListener(new OnItemClickListener(position)); 
						//holder.remove_button.setTag(position); // So we can get the offending line later when we want to 86 it
					}
					  	     
					
					
						if(childPosition==1)
						{
							if(holder.ChildLay!=null)
							{
								Drawable ChildHeaderDraw = getResources().getDrawable( R.drawable.fc_om_list_wrapper);
								holder.ChildLay.setBackgroundDrawable(ChildHeaderDraw);
							}
							if(holder.ChildMainLay!=null)
							{
								//Drawable.ChildHeaderLay = getResources().getDraw
								holder.ChildMainLay.setBackgroundDrawable(null);
							}
						}	
						else if(childPosition>1)
						{
							if(holder.ChildLay!=null)
							{
								Drawable ChildDraw = getResources().getDrawable( R.drawable.fc_om_list_wrapper_child_gradient);
								holder.ChildLay.setBackgroundDrawable(ChildDraw);
							}
							if(holder.ChildMainLay!=null)
							{
								Drawable ChildMainLay = getResources().getDrawable( R.drawable.fc_om_list_wrapper_order_child);
								holder.ChildMainLay.setBackgroundDrawable(ChildMainLay);
							}
					}
							
					UpdateChildItem(groupPosition,childPosition,holder);
					 

					return convertView;
				
				}


				public int getChildrenCount(int groupPosition) 
				{
					
					FCOrderManagerOrder Order = m_Items.get(groupPosition);
					
					// TODO CHECK
					return Order.GetOrderItems().Size()+2; // One for button row, one for normal header
					
				}
				
				class MainAdapterGroupHolder
				{
					ImageView GroupIcon;
					TextView OrderID;
					TextView TimeReceived;
					TextView TimeNeeded;
					TextView UserName;
					TextView TotalItems;
					TextView TotalCost;
					TextView Disposition;
					TextView Here;
					LinearLayout MainLay;
					
				}
				
				class MainAdapterButtonHeaderHolder
				{
					//TextView m_TitleText;
					FCOrderManagerNoParentPressButton m_RefundButton;
					FCOrderManagerNoParentPressButton m_NoShowButton;
					FCOrderManagerNoParentPressButton m_DeliveredButton;
					FCOrderManagerNoParentPressButton m_InProcessButton;
				}
				
				class MainAdapterChildHolder
				{
					TextView ItemType;
					TextView ItemDescription;
					TextView ItemCost;
					LinearLayout ChildLay;
					LinearLayout ChildMainLay;
				}
			}
			
			
}


	