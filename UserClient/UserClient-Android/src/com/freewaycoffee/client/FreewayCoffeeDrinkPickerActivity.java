package com.freewaycoffee.client;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.methods.HttpPost;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.freewaycoffee.client.FreewayCoffeeItemListView.SubActivityTypes;

//import com.freewaycoffee.client.FreewayCoffeeItemListView.FCListAdapter;
//import com.freewaycoffee.client.FreewayCoffeeItemListView.FCListAdapter.FCListViewHolder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

//public class FreewayCoffeeDrinkPickerActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener
public class FreewayCoffeeDrinkPickerActivity extends Activity implements View.OnClickListener
{
	
	private FreewayCoffeeApp appState;
	private ArrayList<String> DrinkListSpinnerArray = new ArrayList<String>();
	
	// This maps The index in the main spinner to a user drink ID (-1 for none -2 for new ... TODO need to fix the -2 nonsense!)
	private Vector<Integer> DrinkListSpinnerIndexMapper; 
	
	// Database ID that comes from the parent activity. Can be -1 to mean NONE, and default is -1 if it was not set by par
	private Integer UserSelectedDrinkID;
	private LinearLayout layout;
	
	private Button CancelButton;
    private Button AddDrinkButton;
	
    private ArrayList<FreewayCoffeeDrinkPickerPair> ListIndex;
    private FreewayCoffeeAddEditDrinkXMLHandler DrinksXMLHandler;
    
    private Integer ParentSelectedPosition; // The position in the list of the parent that was selected. // TODO should I keep this as a member in parent?
    
	private ListView listView;
	private FCDrinkPickAdapter myAdapter;
	private FreewayCoffeeDrinkPickerAsyncPost AsyncGet;
	private FreewayCoffeeDrinkPickerDeleteDrinkAsyncGet DeleteAsyncGet;
	
	private ProgressDialog DrinkListProgress;
	private ProgressDialog DeleteDrinkProgress;
	
	private static final int MENU_ADD =Menu.FIRST;
	private static final int MENU_DELETE =Menu.FIRST+1;
	private static final int MENU_EDIT =Menu.FIRST+2;
	
	public class FCDrinkPickerHolder
	  {
		  Integer Position;
		  TextView item_descr;
		  TextView item_small_descr;
		  ImageView item_icon;
		  TextView item_cost;
		 // Button SelectButton;
		  //FreewayCoffeeNoParentPressImageButtonView EditButton;
		  //FreewayCoffeeNoParentPressImageButtonView RemoveButton;
		  //FreewayCoffeeNoParentPressImageButtonView AddButton;
		  FreewayCoffeeNoParentPressButtonView ActionButton;
		  
	  }
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		 super.onCreate(savedInstanceState);
	        
	     appState = ((FreewayCoffeeApp)getApplicationContext());
		//RESULT_CANCELLED
	     
	     setContentView(R.layout.fc_drink_picker_list);
	     DrinkListProgress=null;
	     DeleteDrinkProgress=null;
	     listView = (ListView)findViewById(R.id.fc_drink_picker_list);
         // listView.setOnItemClickListener(new OnItemClickListener()
            
         View footerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fc_drink_picker_list_footer, null, false);
         listView.addFooterView(footerView);
	     Object retained = getLastNonConfigurationInstance();
		
		if(retained instanceof FreewayCoffeeDrinkPickerAsyncPost)
		{
			// Get the saved state -- the Async Get with the XML and the ListIndex.
			// WHen we set the new activity, it will call Process XML for us
			AsyncGet = (FreewayCoffeeDrinkPickerAsyncPost)retained;
			showProgressDialog(AsyncGet.GetProgressMessage());// This must be before setActivity as that calls ProcessXML which destroys the dialog --- so we dont want to just re-create it again
			DeleteDrinkProgress=null;
			DeleteAsyncGet=null;
			AsyncGet.SetActivity(this);
		}
		else if(retained instanceof FreewayCoffeeDrinkPickerDeleteDrinkAsyncGet)
		{
			DeleteAsyncGet = (FreewayCoffeeDrinkPickerDeleteDrinkAsyncGet)retained;
			showDeleteDrinkProgressDialog();
			DeleteAsyncGet.SetActivity(this);
			AsyncGet=null;
			DrinkListProgress=null;
		}
		else
		{
			AsyncGet=null;
			DeleteAsyncGet=null;
			ListIndex=null;
			GetOrUpdate();
		}
		
	     
	     UserSelectedDrinkID = getIntent().getIntExtra(FreewayCoffeeItemListView.USER_ITEM_SELECTED_DRINK_ID,-1);
	     ParentSelectedPosition = getIntent().getIntExtra(FreewayCoffeeItemListView.USER_ITEM_SELECTED_FOOD_DRINK_POS,-1);
	     
	     TextView UsernameView = (TextView)findViewById(R.id.fc_banner_text);
         
         String DrinksString = getString(R.string.fc_user_favorites);
         // TODO move USER_INFO_NAME_KEY to a better place
         UsernameView.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + "'s " + DrinksString);
         
         
	}
	
	private void GetOrUpdate()
	{
		// Checks if we already got the network info (DringTypes and DrinkOptions). IF we did, no need to get again since
		// we are under the possibly bad assumption they wont change on the server w/out basically kicking the user out
		if( (appState.IsDrinksMenuLoaded()==true) && (appState.IsFoodMenuLoaded()==true))
		{
			// Both loaded
			ResetList();
			return;
		}
		//if( (appState.IsDrinksMenuLoaded()==false) && (appState.IsFoodMenuLoaded()==false))
		//{
			// Neither Loaded
		//	ExecuteGet(appState.MakeGetFoodDrinkPickListURL(),getString(R.string.fc_load_food_drink_menu));
			//appState.MakeGetDrinkPickListURL()
		//}
		if(appState.IsDrinksMenuLoaded()==false)
		{
			// Load Drinks
			ExecuteGet(appState.MakeGetDrinkPickListURL(),getString(R.string.fc_load_drink_menu));
		}
		else if(appState.IsFoodMenuLoaded()==false)
		{
			// Load Food
			//ExecuteGet(appState.MakeGetFoodPickListURL(),getString(R.string.fc_load_food_menu));
		}
		
	}
	private void ResetList()
	{
		CreateListIndex();
        myAdapter = new FCDrinkPickAdapter(ListIndex,this);
        listView.setAdapter(myAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // listView.setOnItemClickListener(this);
        
	}
	private void ExecuteGet(HttpPost Command, String Message)
    {
		// Make a Get
        AsyncGet = new FreewayCoffeeDrinkPickerAsyncPost(this,appState,Message);
        AsyncGet.execute(Command);
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
		
	public void ProcessXMLResult(String XML)
    {
		UnlinkAllAsync();
		DismissAllProgress();

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
			else if(DrinksXMLHandler.DrinkDeletedResponse!=null)
			{
				if(DrinksXMLHandler.DrinkDeletedResponse.equals("ok"))
				{
					try
					{
						Toast Err = Toast.makeText(appState.getApplicationContext(),
								getString(R.string.fc_delete_item_ok),
								Toast.LENGTH_SHORT);
				    	Err.show();
						Integer DrinkID = Integer.parseInt(DrinksXMLHandler.DrinkDeletedID);
						DoDeleteFoodDrink(FreewayCoffeeApp.FoodDrink.Drink,DrinkID);
						
					}
					catch(NumberFormatException e)
					{
						 // TODO
					}
				}
				else
				{
					Toast Err = Toast.makeText(appState.getApplicationContext(),
							getString(R.string.fc_delete_drink_error),
							Toast.LENGTH_SHORT);
			    	Err.show();
				}
				
			}
			else if(DrinksXMLHandler.FoodDeletedResponse!=null)
			{
				if(DrinksXMLHandler.FoodDeletedResponse.equals("ok"))
				{
					try
					{
						Toast Err = Toast.makeText(appState.getApplicationContext(),
								getString(R.string.fc_delete_food_ok),
								Toast.LENGTH_SHORT);
				    	Err.show();
						Integer FoodID = Integer.parseInt(DrinksXMLHandler.FoodDeletedID);
						DoDeleteFoodDrink(FreewayCoffeeApp.FoodDrink.Food,FoodID);
						
					}
					catch(NumberFormatException e)
					{
						 // TODO
					}
				}
				else
				{
					Toast Err = Toast.makeText(appState.getApplicationContext(),
							getString(R.string.fc_delete_food_error),
							Toast.LENGTH_SHORT);
			    	Err.show();
				}
				
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
	
	
	   protected void onDestroy ()
		{
			 super.onDestroy();
			 UnlinkAllAsync();
		}
		 
		 @Override
		 public Object onRetainNonConfigurationInstance()
		 {
			 if(AsyncGet!=null)
			 {
				 AsyncGet.UnlinkActivity();				 
				 AsyncGet.SavedListIndex=ListIndex;
				 return AsyncGet;
			 }
			 return null;
		 }
		 
		 private void UnlinkAllAsync()
			{
				if(AsyncGet!=null)
				{
					 AsyncGet.UnlinkActivity();
					 AsyncGet=null;
				}
				if(DeleteAsyncGet!=null)
				{
					DeleteAsyncGet.UnlinkActivity();
					DeleteAsyncGet=null;
				}
				
			}

		 @Override
		public void onClick(View v)
		{
			 
			 registerForContextMenu(v);
			 openContextMenu(v);
		}
		 
		 @Override
		 public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo Info)
		 {
			 FCDrinkPickerHolder holder = (FCDrinkPickerHolder)v.getTag();
			 FreewayCoffeeDrinkPickerPair ItemInfo = ListIndex.get(holder.Position);

			 // Context Menus dont support Icons !
			 menu.setHeaderTitle("Actions");
			 // This is simply disgusting. Not sure I can use AdapterContextMenuInfo as v is a button
			 // Set the group to the Index I want. I hate myself. FIXME TODO
			 MenuItem AddItem = menu.add(holder.Position,MENU_ADD,Menu.NONE,R.string.fc_add);
			 //AddItem.setIcon(R.drawable.fc_add);
			 
			 //item.setIcon(R.drawable.menu_about);
			 MenuItem DeleteItem = menu.add(holder.Position,MENU_DELETE,Menu.NONE,R.string.fc_delete);
			//DeleteItem.setIcon(R.drawable.fc_remove);
			 //item.setIcon(R.drawable.menu_send_diag);

			 // Edit for Drink Only
			 if(ItemInfo.Type.equals(FreewayCoffeeApp.FoodDrink.Drink))
			 {  
				 MenuItem EditItem = menu.add(holder.Position,MENU_EDIT,Menu.NONE,R.string.fc_edit);
				 //EditItem.setIcon(R.drawable.fc_edit);
			 }
		 }
		 
		 @Override 
		 public boolean onContextItemSelected(MenuItem item)
		 {
			 super.onContextItemSelected(item);
			 Integer Position = item.getGroupId();
			 if(ListIndex.size()==0)
			 {
				 return true;
			 }
			 if( (Position<0) || (Position>ListIndex.size()-1))
			 {
				 return false;
			 }
			
			 FreewayCoffeeDrinkPickerPair ItemInfo = ListIndex.get(Position);
			 switch(item.getItemId())
			 {
			 case MENU_ADD:
				 DoSelectAddAndFinish(Position);
				 return true;
				
			 case MENU_DELETE:
				 DeleteAsyncGet = new FreewayCoffeeDrinkPickerDeleteDrinkAsyncGet(this,appState);
					try
					{
						String URL = appState.MakeDeleteFoodDrinkURL(ItemInfo.Type,ItemInfo.ID);
						DeleteAsyncGet.execute(URL);
					}
					catch (UnsupportedEncodingException e)
					{
						// TODO What ???
					}
					return true;
				 
			 case MENU_EDIT:
				 if(ItemInfo.Type.equals(FreewayCoffeeApp.FoodDrink.Drink))
					{
						Intent intent = new Intent();
				
						intent.putExtra(FreewayCoffeeItemListView.USER_EDIT_DRINK_ID,ItemInfo.ID);
				
						intent.setClassName(this, FreewayCoffeeDrinkAddEditActivity.class.getName());
						startActivityForResult(intent,SubActivityTypes.FC_SUB_ACTIVITY_EDIT_DRINK.ordinal());
					}
					else if(ItemInfo.Type.equals(FreewayCoffeeApp.FoodDrink.Food))
					{
					// Cannot yet edit food.
					}
				 return true;
				 
			 }
			 
			 return false;
		 }
		 /*
		@Override
		public void onClick(View v)
		{
			
			FCDrinkPickerHolder holder = (FCDrinkPickerHolder)v.getTag();
			FreewayCoffeeDrinkPickerPair ItemInfo = ListIndex.get(holder.Position);
			
			if(v==holder.EditButton)
			{
				if(ItemInfo.Type.equals(FreewayCoffeeApp.FoodDrink.Drink))
				{
					Intent intent = new Intent();
			
					intent.putExtra(FreewayCoffeeItemListView.USER_EDIT_DRINK_ID,ItemInfo.ID);
			
					intent.setClassName(this, FreewayCoffeeDrinkAddEditActivity.class.getName());
					startActivityForResult(intent,SubActivityTypes.FC_SUB_ACTIVITY_EDIT_DRINK.ordinal());
				}
				else if(ItemInfo.Type.equals(FreewayCoffeeApp.FoodDrink.Food))
				{
				// Cannot yet edit food.
				}
				
			}
			else if(v==holder.RemoveButton)
			{
				// TODO. Issue Remove to server and then XML handler will deal with it. BUT what happens in the meantime ?
				// TODO -- do we make a non-dismissable popup ? this is getting ugly. Really dont want the list / activity to move on
				// because then we will get into all sorts of issues. Yuck.
				// 1) Issue XML. 
				// 2) Block GUI with progress
				// 3) on XML, if success, remove from ListIndex AND save the ID to remove so we can remove from ItemList
				// 4) on Fail, do nothing except Toast. Pretty ugly
				DeleteAsyncGet = new FreewayCoffeeDrinkPickerDeleteDrinkAsyncGet(this,appState);
				try
				{
					String URL = appState.MakeDeleteFoodDrinkURL(ItemInfo.Type,ItemInfo.ID);
					DeleteAsyncGet.execute(URL);
				}
				catch (UnsupportedEncodingException e)
				{
					// TODO What ???
				}
			}
			else if(v==holder.AddButton)
			{
				DoSelectAddAndFinish(holder.Position);
			}
		}
		*/
		   
		@Override
	    public void onConfigurationChanged(Configuration newConfig) 
	    {
	        super.onConfigurationChanged(newConfig);
	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    }
		
		protected void onActivityResult(int requestCode, int resultCode, Intent data)
		{
			//CreateAndDisplay();
			
			
			if(resultCode==RESULT_OK)
			{
				// If we added a drink, then just add it to the order to prevent the user having to add it, find it in the list then select it again
				// If its an edit, then either its already int the main ItemList and will show up there, or I think they can handle finding it
				if(requestCode == SubActivityTypes.FC_SUB_ACTIVITY_ADD_DRINK.ordinal())
				{
					Integer DrinkID = data.getIntExtra(FreewayCoffeeItemListView.USER_CHOSE_DRINK_ID,-1);
					Intent intent=new Intent();
					intent.putExtra(FreewayCoffeeItemListView.USER_CHOSE_DRINK_ID, DrinkID);
					intent.putExtra(FreewayCoffeeItemListView.USER_ITEM_SELECTED_FOOD_DRINK_POS, ParentSelectedPosition);
					setResult(RESULT_OK,intent);
					finish();
				}
				if(requestCode == SubActivityTypes.FC_SUB_ACTIVITY_ADD_FOOD.ordinal())
				{
					Integer FoodID = data.getIntExtra(FreewayCoffeeItemListView.USER_CHOSE_FOOD_ID,-1);
					Intent intent=new Intent();
					intent.putExtra(FreewayCoffeeItemListView.USER_CHOSE_FOOD_ID, FoodID);
					intent.putExtra(FreewayCoffeeItemListView.USER_ITEM_SELECTED_FOOD_DRINK_POS, ParentSelectedPosition);
					setResult(RESULT_OK,intent);
					finish();
				}
				else if( (requestCode == SubActivityTypes.FC_SUB_ACTIVITY_EDIT_DRINK.ordinal()) ||
						 (requestCode == SubActivityTypes.FC_SUB_ACTIVITY_EDIT_FOOD.ordinal()) )
				{
					// Edit
					CreateListIndex();
					// Gets copied in the constructor ?
					myAdapter.notifyDataSetChanged();	
				}
				
			
				
			}
			else if(resultCode==FreewayCoffeeItemListView.RESULT_CODE_NOT_LOGGED_IN)
			{
				setResult(FreewayCoffeeItemListView.RESULT_CODE_NOT_LOGGED_IN);
				finish();
			}
		}
		
		private void DismissAllProgress()
		{
			if(DeleteDrinkProgress!=null)
			{
				DeleteDrinkProgress.dismiss();
				DeleteDrinkProgress=null;
			}
			if(DrinkListProgress!=null)
			{
				DrinkListProgress.dismiss();
				DrinkListProgress=null;
			}
			
		}
		public void showDeleteDrinkProgressDialog()
		{
			DeleteDrinkProgress = DrinkListProgress = ProgressDialog.show(this, "",
					 getString(R.string.fc_deleting_your_item),
					 true);
		}
		
		public void showProgressDialog(String Message)
		{
			 DrinkListProgress = ProgressDialog.show(this, "",
					 Message,
					 true);
		
		}
	private void CreateListIndex()
	{
		// Since we set this pointer into the Adaptor (by reference) we dont want to change it between updates because then we just have to re-set all the data again
		
		if(ListIndex==null)
		{
			if(AsyncGet!=null)
			{
				if(AsyncGet.SavedListIndex!=null)
				{
					ListIndex = new ArrayList<FreewayCoffeeDrinkPickerPair>(AsyncGet.SavedListIndex);				 
					return; // Dont re-create the index.
				}
			}
			else
			 {
				 ListIndex = new ArrayList<FreewayCoffeeDrinkPickerPair>();
			 }
		}
		else
		{
			ListIndex.clear();
		}
		
		FreewayCoffeeDrinkPickerPair IndexPair;
		
		// Populate drinks
 		for (FreewayCoffeeUserDrink CurrentDrink : appState.GetUserDrinksData().values())
 		{
 			
 			IndexPair=new FreewayCoffeeDrinkPickerPair();
 	
 			try
 			{
 				IndexPair.Type = FreewayCoffeeApp.FoodDrink.Drink;
 				IndexPair.ID = CurrentDrink.GetUserDrinkID();
 				IndexPair.Descr = "Check This"; // FIXME ???
 			}
 			catch (NumberFormatException e)
 			{
 				// Pair values already set to "-1"// TODO we need to do better
 			}
 			ListIndex.add(IndexPair);
 		}
 		
 		// Populate Foods
 		for (HashMap<String, String > Value : appState.GetFoodData().values())
 		{
 			
 			IndexPair=new FreewayCoffeeDrinkPickerPair();
 	
 			try
 			{
 				IndexPair.Type = FreewayCoffeeApp.FoodDrink.Food;
 				IndexPair.ID = Integer.parseInt(Value.get("id"));
 				IndexPair.Descr = Value.get(FreewayCoffeeXMLHelper.USER_FOOD_LONG_DESCR);
 			}
 			catch (NumberFormatException e)
 			{
 				// Pair values already set to "-1"// TODO we need to do better
 			}
 			
 			ListIndex.add(IndexPair);
 		}
	}
	
	private void DoDeleteFoodDrink(FreewayCoffeeApp.FoodDrink Type,Integer ID)
	{
		appState.DeleteFoodDrinkID(Type,ID); // Also deletes all from CurrentDrinkOrder
	
		myAdapter.notifyDataSetChanged();
		
		for(Iterator<FreewayCoffeeDrinkPickerPair> it = ListIndex.iterator();it.hasNext();)
		{
			FreewayCoffeeDrinkPickerPair Item = it.next();
			if( (Item.Type==Type) && (Item.ID==ID))
			{
				it.remove();
			}
		}
	}
	
	public void UserCanceled(View v)
    {
            setResult(RESULT_CANCELED, null);
            if(AsyncGet!=null)
            {
            	AsyncGet.cancel(true);
            	AsyncGet.UnlinkActivity();
            	
            }
            finish();
    }

	 public void DrinkAdd(View v)
	 {
		Intent intent = new Intent();
		intent.setClassName(this, FreewayCoffeeDrinkAddActivity.class.getName());
		
		startActivityForResult(intent,SubActivityTypes.FC_SUB_ACTIVITY_ADD_DRINK.ordinal());		 
	 }
	 
	 public void FoodAdd(View v)
	 {
		Intent intent = new Intent();
		intent.setClassName(this, FreewayCoffeeFoodAddActivity.class.getName());
		
		startActivityForResult(intent,SubActivityTypes.FC_SUB_ACTIVITY_ADD_FOOD.ordinal());		 
	 }

	 public void DoSelectAddAndFinish(Integer Position)
	 {
		    
		Intent intent = new Intent();
     		
		if(ListIndex.get(Position).Type.equals(FreewayCoffeeApp.FoodDrink.Drink))
		{
			intent.putExtra(FreewayCoffeeItemListView.USER_CHOSE_DRINK_ID,ListIndex.get(Position).ID);
		}
		else if(ListIndex.get(Position).Type.equals(FreewayCoffeeApp.FoodDrink.Food))
		{
			intent.putExtra(FreewayCoffeeItemListView.USER_CHOSE_FOOD_ID,ListIndex.get(Position).ID);
		}
    	intent.putExtra(FreewayCoffeeItemListView.USER_ITEM_SELECTED_FOOD_DRINK_POS,ParentSelectedPosition);
    	setResult(RESULT_OK,intent);
    	
    	finish();
    	
	 }
	  private class FCDrinkPickAdapter extends BaseAdapter
	  {
			private ArrayList<FreewayCoffeeDrinkPickerPair> Items;
			private LayoutInflater mInflater;
			
			public FCDrinkPickAdapter(ArrayList<FreewayCoffeeDrinkPickerPair> items, Context context)
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
					if(AsyncGet!=null)
			       	{
			       		// Cancel all the Async stuff as we really dont care at this point. we will re-get if we have to
			       		AsyncGet.cancel(true);
			           	AsyncGet.UnlinkActivity();
			       		AsyncGet=null;
			       	}
					FreewayCoffeeDrinkPickerActivity.this.DoSelectAddAndFinish(mPosition);
					
					
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
					convertView = mInflater.inflate(R.layout.fc_drink_picker_row, null);
					// Creates a ViewHolder and store references to the two children views
					// we want to bind data to.
					holder = new FCDrinkPickerHolder();
					holder.item_descr = (TextView) convertView.findViewById(R.id.fc_drink_picker_row_text);
					holder.Position=position;
					holder.item_icon = (ImageView) convertView.findViewById(R.id.fc_drink_picker_row_img);
					holder.item_cost=(TextView) convertView.findViewById(R.id.fc_drink_picker_row_cost);
					holder.item_small_descr = (TextView) convertView.findViewById(R.id.fc_drink_picker_row_small_text);
					
					holder.ActionButton = (FreewayCoffeeNoParentPressButtonView) convertView.findViewById(R.id.fc_drink_picker_action_but);
					holder.ActionButton.setTag(holder); // So we can tell what row for later
					holder.ActionButton.setOnClickListener(FreewayCoffeeDrinkPickerActivity.this);
				//	holder.SelectButton = (Button) convertView.findViewById(R.id.fc_drink_picker_row_select_button);
					/*
					holder.EditButton = (FreewayCoffeeNoParentPressImageButtonView) convertView.findViewById(R.id.fc_drink_picker_row_edit_button);
					holder.EditButton.setTag(holder); // So we can tell what row for later
					holder.EditButton.setOnClickListener(FreewayCoffeeDrinkPickerActivity.this);
					
					holder.RemoveButton = (FreewayCoffeeNoParentPressImageButtonView) convertView.findViewById(R.id.fc_drink_picker_row_remove_button);
					holder.RemoveButton.setTag(holder); // So we can tell what row for later
					holder.RemoveButton.setOnClickListener(FreewayCoffeeDrinkPickerActivity.this);
					
					holder.AddButton = (FreewayCoffeeNoParentPressImageButtonView) convertView.findViewById(R.id.fc_drink_picker_row_add_button);
					holder.AddButton.setTag(holder); // So we can tell what row for later
					holder.AddButton.setOnClickListener(FreewayCoffeeDrinkPickerActivity.this);
					*/
					convertView.setOnClickListener(new OnItemClickListener(position));
					convertView.setTag(holder);
				                     
				}
				else
				{
					// Get the ViewHolder back to get fast access to the TextView
					// and the ImageView.
					holder = (FCDrinkPickerHolder) convertView.getTag();
					// Make sure we set On-click here because when a view is re-used when removing, it may have a different Position so we need to update that.
					convertView.setOnClickListener(new OnItemClickListener(position));
					holder.Position=position;
					holder.ActionButton.setTag(holder); // So we can tell what row for later
					/*
					holder.EditButton.setTag(holder); // So we can tell what row for later
					holder.RemoveButton.setTag(holder); 
					holder.AddButton.setTag(holder);
					*/
				}
				  	              
				UpdateItem(position,holder);
				 
				  
				return convertView;

			  }
			  
			  private void UpdateItem(Integer position,FCDrinkPickerHolder holder)
			  {
				  
				  
				  Integer ItemID = Items.get(position).ID;
				  
				  // Dont show the dirty laundry NONE drink.
				  if(Items.get(position).ID!=0)
				  {
					  //HashMap<String,String> Drink = appState.GetDrinksData().get(Items.get(position).DrinkID);
					  //Integer DrinkTypeID = Integer.parseInt(Drink.get(FreewayCoffeeXMLHelper.USER_DRINK_TYPE_ID));
					  
					  FreewayCoffeeApp.FoodDrink Type= Items.get(position).Type;
					  
					  if(Type.equals(FreewayCoffeeApp.FoodDrink.Drink))
					  {
						  FreewayCoffeeUserDrink TheDrink = appState.GetUserDrink(ItemID);
						  if(TheDrink==null)
						  {
							  holder.item_small_descr.setText("Internal Error. No DrinkID(" + ItemID + " Found");
							  return;
						  }
						  
						  String ItemDescr = TheDrink.GetDrinkTypeLongDescr();
						  
						  if(!TheDrink.GetUserDrinkName().equals(""))
						  {
							  ItemDescr +=" (" + appState.GetDrinkNameFromDrinkID(Items.get(position).ID) + ")";
						  }
					  
						  holder.item_descr.setText(ItemDescr);
					  
						  holder.item_cost.setText("$" + TheDrink.GetUserDrinkCost() );
					  
						  
						  holder.item_small_descr.setText(TheDrink.GetUserDrinkOptionsText());
						  //holder.EditButton.setVisibility(Button.VISIBLE);
						  
						  //holder.item_small_descr.setText(appState.GetDrinkSmallTextFromID(Items.get(position).DrinkID));
						  holder.item_icon.setImageResource(R.drawable.fc_drink);
						  
					  }
					  else if(Type.equals(FreewayCoffeeApp.FoodDrink.Food)==true)
					  {
						  String ItemDescr = (appState.GetFoodData().get(ItemID).get(FreewayCoffeeXMLHelper.USER_FOOD_LONG_DESCR_ATTR));
						  holder.item_descr.setText(ItemDescr);
						  holder.item_small_descr.setText(appState.GetFoodData().get(ItemID).get(FreewayCoffeeXMLHelper.USER_FOOD_OPTIONS_TEXT));
						  holder.item_cost.setText("$" + appState.GetFoodData().get(ItemID).get(FreewayCoffeeXMLHelper.USER_FOOD_COST_ATTR));
						  
						  holder.item_icon.setImageResource(R.drawable.fc_food);
						  //holder.EditButton.setVisibility(Button.INVISIBLE);
					  }
				  }
				  
				  
				 // holder.SelectButton.setText("Select");
			  }
			  
	}
	
}
