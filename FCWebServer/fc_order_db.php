<?php
/**
 * fc_order_db.php
 * (C) Copyright Freeway Coffee, 2012
 * Database queries relating to orders
 */
    

require_once("fc_constants.php");
require_once("fc_order_objects.php");
require_once("fc_credit_card.php");

class DatabaseOrders
{
	function __construct()
	{
		global $session;
	}
	
	function GetOrdersBaseQueryString($order_type,$location_id)
	{
		global $database;
		
		
		return "SELECT orders_id, orders_userid, orders_start_time, orders_time_to_location, orders_end_time, " .
				" orders_time_needed, orders_disposition, orders_total_cost, orders_user_email, orders_location_id, " .
				" orders_user_here_time, orders_user_name, orders_incarnation,orders_user_car_make_model, user_tag,  " .
				" orders_authorize_net_customer_profile_id, orders_user_is_demo, last_modified, orders_client_type, " .
				" orders_arrive_mode, orders_item_total, orders_discount, orders_tip, orders_pay_method, global_order_tag, " .
				" orders_taxable, orders_taxable_amount, orders_tax, orders_tax_rate, orders_convenience_fee " . 
				" FROM " . $database->GetOrdersTableName($order_type,$location_id);
		/*
		return "SELECT orders_id, orders_userid, orders_start_time, orders_time_to_location, orders_end_time, " .
				" orders_time_needed, orders_disposition, orders_total_cost, orders_user_email, orders_location_id, " .
				" orders_user_here_time, orders_user_name, orders_incarnation,orders_user_car_make_model, user_tag,  " .
				" orders_arrive_mode, orders_item_total, orders_discount, orders_tip, orders_pay_method, global_order_tag " .
				" FROM " . $database->GetOrdersTableName($order_type,$location_id); ??? JUNK ??? */
		
	}
	
	function OpenGetItemsOptionsForOrder($order_id,$item_id)
	{
		global $session;
		global $database;
	
		$stmt = mysqli_stmt_init($database->getConnection());
		$query_string = "SELECT orders_item_options_id, orders_item_options_order_id, orders_item_options_order_item_id,
							orders_item_options_drink_type_option_id, orders_item_options_option_count, orders_item_options_orig_cost_per drink_option_groups, "
							. "drink_options.do_option_name, drink_option_groups.dog_long_name "
						." FROM orders_user_items_options, drink_options, drink_option_groups, drink_types_options "
						." WHERE orders_user_items_options.orders_item_options_order_item_id=? AND "
						." orders_user_items_options.orders_item_options_order_id=? AND "
						." orders_user_items_options.orders_item_options_drink_type_option_id=drink_types_options.dto_id AND "
						." drink_types_options.dto_drink_option_id = drink_options.do_id AND "
						." drink_types_options.dto_drink_option_group_id = drink_option_groups.dog_id "
						." ORDER BY orders_item_options_order_id ASC, orders_item_options_order_item_id ASC, orders_item_options_id ASC";
	
		if(!mysqli_stmt_prepare($stmt,$query_string))
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"OpenGetItemsOptionsForOrder: Prepare Failed Order: {$order_id} (" . mysqli_stmt_error($stmt) . ")");
			return NULL;
		}
	
		if(!mysqli_stmt_bind_param($stmt,'ii',$item_id,$order_id))
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"OpenGetItemsOptionsForOrder: BIND failed Order: {$order_id}(" . mysqli_stmt_error($stmt) . ")");
			return NULL;
		}
	
		if(mysqli_stmt_execute($stmt))
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"OpenGetItemsOptionsForOrder: Could not store result from SELECT Order: {$order_id}(" . mysqli_stmt_error($stmt) . ")");
			return NULL;
		}
	
		return $stmt;
	
	}
	
	function GetNextItemsOptionsForOrder($stmt)
	{
		global $session;
		global $database;
	
		if($stmt==NULL)
		{
			return NULL;
	
		}
			
		$order_item_option =  new OrderItemOption();
		if(!mysqli_stmt_bind_result($stmt,	$order_item_option->orders_item_options_id,
				$order_item_option->orders_item_options_order_id,
				$order_item_option->orders_item_options_order_item_id,
				$order_item_option->orders_item_options_drink_type_option_id,
				$order_item_option->orders_item_options_option_count,
				$order_item_option->orders_item_options_orig_cost_per,
				$order_item_option->option_name,
				$order_item_option->option_group
		))
		{
			$database->GetNextItemsOptionsForOrder($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"GetNextItemsForOrderFull: Could not bind result (" . mysqli_stmt_error($stmt) . ")");
			return NULL;
		}
	
		if( mysqli_stmt_fetch($stmt)==true)
		{
			return $order_item_option;
		}
		else
		{
			return NULL;
		}
	
		return NULL;
	
	}
	
	function CloseGetItemsOptionsForOrder($stmt)
	{
		if(!is_null($stmt))
		{
			mysqli_stmt_close($stmt);
		}
	}
	
	function OpenGetItemsForOrderFull($order_id)
	{
		global $session;
		global $database;
	
		$stmt = mysqli_stmt_init($database->getConnection());
		$query_string = "SELECT orders_user_items_id, orders_user_items_order_id, orders_user_items_user_id,"
						. " orders_user_items_drink_type_id, orders_user_items_drink_cost, orders_user_items_extra, "
						." orders_user_items_name, orders_user_items_description, orders_user_items_class, drink_types.drink_type_long_descr "
						." FROM orders_user_items, drink_types WHERE orders_user_items_order_id=? AND "
						." orders_user_items.orders_user_items_drink_type_id = drink_types.drink_type_id ORDER BY orders_user_items_order_id ASC ";
							

		if(!mysqli_stmt_prepare($stmt,$query_string))
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"OpenGetItemsForOrderFull: Prepare Failed Order: {$order_id} (" . mysqli_stmt_error($stmt) . ")");
			return NULL;
		}
	
		if(!mysqli_stmt_bind_param($stmt,'i',$order_id))
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"OpenGetItemsForOrderFull: BIND failed Order: {$order_id}(" . mysqli_stmt_error($stmt) . ")");
			return NULL;
		}
	
		if(mysqli_stmt_execute($stmt))
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"OpenGetItemsForOrderFull: Could not store result from SELECT Order: {$order_id}(" . mysqli_stmt_error($stmt) . ")");
			return NULL;
		}
	
		return $stmt;
	}
	
	function GetNextItemsForOrderFull($stmt)
	{
		global $session;
		global $database;
	
		if($stmt==NULL)
		{
			return NULL;
	
		}
			
		
		$order_item =  new OrderItem();
		if(!mysqli_stmt_bind_result($stmt,$order_item->order_item_id, $order_item->order_id, $order_item->user_id,
						$order_item->drink_type_id, $order_item->order_item_cost, $order_item->item_extra, $order_item->item_name,
						$order_item->order_item_descr, $order_item->item_type, $order_item->drink_type_name))
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"GetNextItemsForOrderFull: Could not bind result (" . mysqli_stmt_error($stmt) . ")");
			return NULL;
		}
	
		if( mysqli_stmt_fetch($stmt)==true)
		{
			//$order_item->SetDispositionTextFromCurrentID();
			//$order_item->ConvertUTCTimesToUserTZ();
			return $order_item;
		}
		else
		{
			return NULL;
		}
	
		return NULL;
	
	}
	
	function OpenGetItemsForOrderShort($order_id)
	{
		global $session;
		global $database;
		
		$stmt = mysqli_stmt_init($database->getConnection());
		$query_string = "SELECT orders_user_items_id, orders_user_items_drink_cost, orders_user_items_description FROM orders_user_items WHERE orders_user_items_order_id=? ORDER BY orders_user_items_class ASC";
		
		if(!mysqli_stmt_prepare($stmt,$query_string))
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"OpenGetItemsForOrderShort: Prepare Failed Order: {$order_id} (" . mysqli_stmt_error($stmt) . ")");
			return NULL;
		}
		
		if(!mysqli_stmt_bind_param($stmt,'i',$order_id))
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"OpenGetItemsForOrderShort: BIND failed Order: {$order_id}(" . mysqli_stmt_error($stmt) . ")");
			return NULL;
		}
		
		if(mysqli_stmt_execute($stmt))
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"OpenGetItemsForOrderShort: Could not store result from SELECT Order: {$order_id}(" . mysqli_stmt_error($stmt) . ")");
			return NULL;
		}
		
		return $stmt;
	
	}
	
	function GetNextItemsForOrderShort($stmt)
	{
		global $session;
		global $database;
	
		if($stmt==NULL)
		{
			return NULL;
	
		}
		 
		$order_item =  new OrderItem();
		if(!mysqli_stmt_bind_result($stmt,$order_item->order_item_id, $order_item->order_item_cost,$order_item->order_item_descr))
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"GetNextItemsForOrderShort: Could not bind result (" . mysqli_stmt_error($stmt) . ")");
			return NULL;
		}
	
		if( mysqli_stmt_fetch($stmt)==true)
		{
			//$order_item->SetDispositionTextFromCurrentID();
			//$order_item->ConvertUTCTimesToUserTZ();
			return $order_item;
		}
		else
		{
			return NULL;
		}
	
		return NULL;
	
	}
	
	function CloseGetItemsForOrder($stmt)
	{
		mysqli_stmt_close($stmt);
	}
	
	// Returns an array of any open orders for the user
	// $limit==0 means all orders
	function GetOpenInProgressOrdersForUserAndLocation($user_id,$limit,$location_id)
	{
		global $session,$database;
		
		//AND orders_user_here_time IS NULL
		$query_string = $this->GetOrdersBaseQueryString(ORDER_TYPE_IN_PROGRESS, $location_id)
						." WHERE orders_userid=?  AND (orders_disposition=" . ORDER_RECEIVED 
						." OR orders_disposition=" . ORDER_INPROGRESS . ") ORDER BY orders_id DESC";

		if (!isset($limit) || is_null($limit) || ($limit<0) )
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"GetOrdersForNonArrivedAndOpenOrders: Location [{$location_id}] Limit must be valid positive number");
			return NULL;
		}
		
		if($limit>0)
		{
			$query_string .=" LIMIT ?";
		}
	
		$stmt = mysqli_stmt_init($database->getConnection());
		if(!mysqli_stmt_prepare($stmt,$query_string))
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"GetOrderIDsForNonArrivedOpenOrders: Location [{$location_id}] Prepare Failed (" . mysqli_stmt_error($stmt) . ")");
			return NULL;
		}
		
		if($limit>0)
		{
			$bind_result = mysqli_stmt_bind_param($stmt,'ii',$user_id,$limit);
		}
		else
		{
			$bind_result = mysqli_stmt_bind_param($stmt,'i',$user_id);
		}
		
		if($bind_result!=true)
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"GetOrderIDsForNonArrivedOpenOrders: Location [{$location_id}] BIND failed (" . mysqli_stmt_error($stmt) . ")");
			$this->CloseGetOrderBy($stmt);
			return NULL;
		}
		
		if(mysqli_stmt_execute($stmt))
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"GetOrderIDsForNonArrivedOpenOrders: Location [{$location_id}] Could not store result from SELECT (" . mysqli_stmt_error($stmt) . ")");
			$this->CloseGetOrderBy($stmt);
			return NULL;
		}
		
		$result=NULL;
		while($order=$this->GetNextOrderByStatement($stmt))
		{
			$result[]=$order;
		}
		$this->CloseGetOrderBy($stmt);
		return $result;
		
	}
	
	// Now its hard to get an order from *anywhere* -- do we need a mapping here ? Ugh that sucks. Later I hope
	function GetOrderByOrderIDForLocation($order_id,$location_id)
	{
		$order = $this->GetOrderByOrderIDAndTypeForLocation(ORDER_TYPE_IN_PROGRESS,$order_id,$location_id);
		if($order==NULL)
		{
			return $this->GetOrderByOrderIDAndTypeForLocation(ORDER_TYPE_PROCESSED,$order_id,$location_id);
		}
		return $order;
	}
	
	function GetOrderByOrderIDAndTypeForLocation($order_type,$order_id,$location_id)
	{
		global $session,$database;
		
		$query_string = $this->GetOrdersBaseQueryString($order_type, $location_id) . " WHERE orders_id=? ";
		
		$stmt = mysqli_stmt_init($database->getConnection());
		if(!mysqli_stmt_prepare($stmt,$query_string))
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"GetOrderByOrderIDAndTypeForLocation: Type: [{$order_type}] Location [{$location_id}] Prepare Failed OrderID: {$order_id} (" 
						. mysqli_stmt_error($stmt) . ")");
			return NULL;
		}
	
		$bind_result = mysqli_stmt_bind_param($stmt,'i',$order_id);
		
		if($bind_result!=true)
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"GetOrderByOrderIDAndTypeForLocation: Type: [{$order_type}] Location [{$location_id}] BIND failed OrderID: {$order_id}("
					 . mysqli_stmt_error($stmt) . ")");
			$this->CloseGetOrderBy($stmt);
			return NULL;
		}
		
		if(mysqli_stmt_execute($stmt))
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"GetOrderByOrderIDAndTypeForLocation: Type: [{$order_type}] Location [{$location_id}] Could not store result from SELECT OrderID: {$order_id} (" 
					. mysqli_stmt_error($stmt) . ")");
			$this->CloseGetOrderBy($stmt);
			return NULL;
		}
		
		
		$order=$this->GetNextOrderByStatement($stmt);
		$this->CloseGetOrderBy($stmt);
		return $order;
		
	}

	
	// num_days of 1 = today, 2 = yesterday and today ...
	function CalculateDatesForDayRange($num_days,&$day_start_string,&$day_start_date_time,&$day_start_date_time_user,
												&$day_end_string,&$day_end_date_time,&$day_end_date_time_user)
	{
		global $session;
		
		$UserTZ = new DateTimeZone($session->user_data->UserTZ);
		$UTCTZ = new DateTimeZone("UTC");
		
		$day_start = mktime(0,0,0); // Hour, min, second=0
		
		$day_start_date_time = new DateTime("@" . $day_start);
		$day_start_date_time->setTimezone($UTCTZ);
		
		$day_start_date_time_user = new DateTime("@". $day_start);
		$day_start_date_time_user->setTimezone($UserTZ);
		
		//print("Loc: {$location_id}");
		
		//print("Num Days: {$num_days}");
		if($num_days>1)
		{
			$date_past_str =  $num_days-1 . " day";
				
				
			if(($num_days-1)>1)
			{
				$date_past_str.="s";
			}
			$date_past_str_final = "-" . $date_past_str;
			//print("DatePast: {$date_past_str_final}");
				
			date_modify($day_start_date_time,$date_past_str_final);
			date_modify($day_start_date_time_user,$date_past_str_final);
		}
		
		$day_start_string = $day_start_date_time->format(DEFAULT_DATE_TIME_FORMAT);
		
		$day_end_date_time=new DateTime("@" . $day_start);
		$day_end_date_time->setTimezone($UTCTZ);
		
		$day_end_date_time_user=new DateTime("@" . $day_start);
		$day_end_date_time_user->setTimezone($UserTZ);
		
		date_modify($day_end_date_time, '+1 day'); // Always +1 as we can only go up to midnight tonight
		date_modify($day_end_date_time_user, '+1 day'); // Always +1 as we can only go up to midnight tonight
		
		$day_end_string = $day_end_date_time->format(DEFAULT_DATE_TIME_FORMAT);
		
	}
	// NOTE: SHARE WITH TODAY EVENTUAALLY
	
	function OpenGetOrdersForDateTimeRangeForLocation($order_type,$location_id,$day_start_string,$day_end_string)
	{
		global $database;
		global $session;
		
		$query_string = $this->GetOrdersBaseQueryString($order_type, $location_id) . "WHERE orders_start_time BETWEEN ? AND ? ORDER BY orders_id ASC" ;
		
	
		$stmt = mysqli_stmt_init($database->getConnection());
		
		if(!mysqli_stmt_prepare($stmt,$query_string))
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
							"OpenGetOrdersForTodayForLocation: OrderType [{$order_type} ] LocationID [{$location_id}] Prepare Failed (" 
							. mysqli_stmt_error($stmt) . ")");
			return NULL;
		}
		
		if(!mysqli_stmt_bind_param($stmt,'iss',$location_id,$day_start_string,$day_end_string))
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"OpenGetOrdersForTodayForLocation: OrderType [{$order_type} ] LocationID [{$location_id}] BIND failed (" 
							. mysqli_stmt_error($stmt) . ")");
			return NULL;
		}
		
		if(mysqli_stmt_execute($stmt))
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"OpenGetOrdersForTodayForLocation: OrderType [{$order_type} ] LocationID [{$location_id}] Could not store result from SELECT (" 
					. mysqli_stmt_error($stmt) . ")");
			return NULL;
		}
		
		return $stmt;
	}
	
	function OpenGetInProcessOrdersForTodayForLocation($location_id)
	{
		global $session;
		global $database;
       
		
		if(is_null($session->user_data->UserTZ) || strlen($session->user_data->UserTZ)==0)
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,
					"OpenGetInProcessOrdersForTodayForLocation::TZ EMPTY. - FAILING Location {$location_id} UserInfo. : " . var_export($session->user_data,true));
			return NULL;	
		}
		
		$UserTZ = new DateTimeZone($session->user_data->UserTZ);
		$UTCTZ = new DateTimeZone("UTC");
		
		$day_start = mktime(0,0,0); // Hour, min, second=0
		
		$day_start_date_time = new DateTime("@" . $day_start,$UTCTZ);
		$day_start_string = $day_start_date_time->format(DEFAULT_DATE_TIME_FORMAT);
		
		$day_end_time=new DateTime("@" . $day_start,$UTCTZ);
		date_modify($day_end_time, '+1 day');
		//date_add($day_end_time, date_interval_create_from_date_string('1 day'));
		$day_end_string = $day_end_time->format(DEFAULT_DATE_TIME_FORMAT);
		
		$query_string = $this->GetOrdersBaseQueryString(ORDER_TYPE_IN_PROGRESS, $location_id) ." where orders_start_time BETWEEN ? AND ?";
		
		//print("XX: NowDate: {$day_start_string}, TomorrowDate: {$day_end_string}");
		
		//print("YYY: {$location_id}");
		//print $query_string;
		//print $query_string;
		
        $stmt = mysqli_stmt_init($database->getConnection());
        
        if(!mysqli_stmt_prepare($stmt,$query_string))
        {
        	$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        					"OpenGetInProcessOrdersForTodayForLocation: Prepare Failed (" . mysqli_stmt_error($stmt) . ")");
        	return NULL;
        }
        
        if(!mysqli_stmt_bind_param($stmt,'ss',$day_start_string,$day_end_string))
        {
        	$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        				"OpenGetInProcessOrdersForTodayForLocation: BIND failed (" . mysqli_stmt_error($stmt) . ")");
        	return NULL;
        }
        
        if(mysqli_stmt_execute($stmt))
        {
        	mysqli_stmt_store_result($stmt);
        }
        else
        {
        	$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        			"OpenGetInProcessOrdersForTodayForLocation: Could not store result from SELECT (" . mysqli_stmt_error($stmt) . ")");
        	return NULL;
        }
        
        return $stmt;
        }
        
        
       	function OpenGetInProcessOrdersAfterOrderIDForLocation($location_id,$order_id)
        {
        	global $session;
        	global $database;
        	 
        	$query_string = $this->GetOrdersBaseQueryString(ORDER_TYPE_IN_PROGRESS, $location_id) . " where orders_id > ?";
        	//print($query_string);
        	
        	$stmt = mysqli_stmt_init($database->getConnection());
        
        	if(!mysqli_stmt_prepare($stmt,$query_string))
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        					"OpenGetInProcessOrdersAfterOrderIDForLocation: Location [{$location_id}] Prepare Failed (" 
        				. mysqli_stmt_error($stmt) . ")");
        		return NULL;
        	}
        
        	if(!mysqli_stmt_bind_param($stmt,'ii',$location_id,$order_id))
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        					"OpenGetInProcessOrdersAfterOrderIDForLocation: Location [{$location_id}] BIND failed (" 
        				. mysqli_stmt_error($stmt) . ")");
        		return NULL;
        	}
        
        	if(mysqli_stmt_execute($stmt))
        	{
        		mysqli_stmt_store_result($stmt);
        	}
        	else
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        					"OpenGetInProcessOrdersAfterOrderIDForLocation: Location [{$location_id}] Could not store result from SELECT (" 
        				. mysqli_stmt_error($stmt) . ")");
        		return NULL;
        	}
        
        	return $stmt;
        }
        
        /*
        function OpenGetInProgressOrdersModifiedAfterTimestampForLocation($location_id,$timestamp)
        {
        	global $session;
        	global $database;
        
        	// We order DESC because I want to sneak a peek at the first order in the calling function and need it to have the highest timestamp
        	// Then we send that to the client so he can send it to us again for the next query and only get the most recent updated
        	$query_string = $this->GetOrdersBaseQueryString($order_type, $location_id) . " where last_modified >= ? ORDER BY last_modified DESC";
        	//print($query_string);
        	 
        	$stmt = mysqli_stmt_init($database->getConnection());
        
        	if(!mysqli_stmt_prepare($stmt,$query_string))
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        				"OpenGetInProgressOrdersModifiedAfterTimestampForLocation: Location [{$location_id}] Prepare Failed ("
        				 . mysqli_stmt_error($stmt) . ")");
        		return NULL;
        	}
        	
        	if(!mysqli_stmt_bind_param($stmt,'s',$timestamp))
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        				"OpenGetInProgressOrdersModifiedAfterTimestampForLocation: Location [{$location_id}] BIND failed (" 
        				. mysqli_stmt_error($stmt) . ")");
        		return NULL;
        	}
        
        	if(mysqli_stmt_execute($stmt))
        	{
        		mysqli_stmt_store_result($stmt);
        	}
        	else
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        				"OpenGetInProgressOrdersModifiedAfterTimestampForLocation: Location [{$location_id}] Could not store result from SELECT (" 
        		. mysqli_stmt_error($stmt) . ")");
        		return NULL;
        	}
        
        	return $stmt;
        }
        */
        function OpenGetInProgressOrdersModifiedAfterTagForLocation($location_id,$order_tag)
        {
        	global $session;
        	global $database;
        
        	// We order DESC because I want to sneak a peek at the first order in the calling function and need it to have the highest timestamp
        	// Then we send that to the client so he can send it to us again for the next query and only get the most recent updated
        	$query_string = $this->GetOrdersBaseQueryString($order_type, $location_id) . " where global_order_tag > ? ";
        	//print($query_string);
        
        	$stmt = mysqli_stmt_init($database->getConnection());
        
        	if(!mysqli_stmt_prepare($stmt,$query_string))
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        				"OpenGetInProgressOrdersModifiedAfterTimestampForLocation: Location [{$location_id}] Prepare Failed ("
        				. mysqli_stmt_error($stmt) . ")");
        		return NULL;
        	}
        	 
        	if(!mysqli_stmt_bind_param($stmt,'s',$order_tag))
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        				"OpenGetInProgressOrdersModifiedAfterTimestampForLocation: Location [{$location_id}] BIND failed ("
        				. mysqli_stmt_error($stmt) . ")");
        		return NULL;
        	}
        
        	if(mysqli_stmt_execute($stmt))
        	{
        		mysqli_stmt_store_result($stmt);
        	}
        	else
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        				"OpenGetInProgressOrdersModifiedAfterTimestampForLocation: Location [{$location_id}] Could not store result from SELECT ("
        				. mysqli_stmt_error($stmt) . ")");
        		return NULL;
        	}
        
        	return $stmt;
        }
        
        function GetNextOrderByStatement($stmt)
        {
        	global $session;
        	global $database;
        
        	if($stmt==NULL)
        	{
        		return NULL;
        		
        	}
        			
        	$order =  new Order();
        	if(!mysqli_stmt_bind_result($stmt,
        			$order->orders_id, $order->orders_user_id, $order->orders_start_time_utc, $order->orders_time_to_location,
        			$order->orders_end_time_utc,$order->orders_time_needed_utc, $order->orders_disposition, $order->orders_total_cost, 
        			$order->orders_user_email, $order->orders_location_id, $order->orders_user_time_here, $order->orders_user_name,
        			$order->incarnation, $order->user_car_info, $order->user_tag, $order->orders_authorize_net_customer_profile_id,
        			$order->orders_user_is_demo, 
        			$order->last_modified, $order->orders_client_type,$order->orders_arrive_mode,
        			$order->orders_items_cost, $order->orders_discount, $order->orders_tip_amount,
        			$order->orders_pay_method,$order->global_order_tag,
        			$order->order_is_taxable, $order->order_taxable_amount, $order->order_total_tax,$order->order_tax_rate, $order->order_convenience_fee ))
	       	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        				"GetNextOrderByStatement: Could not bind result (" . mysqli_stmt_error($stmt) . ")");
        		return NULL;
        	}
        
        	if( mysqli_stmt_fetch($stmt)==true)
        	{
        		$order->SetDispositionTextFromCurrentID();
        		$order->ConvertUTCTimesToUserTZ();
        		return $order;
        	}
        	else
        	{
        		return NULL;
        	}
        
        	return NULL;
        
        }
        
        function CloseGetOrderBy($stmt)
        {
        	if(isset($stmt))
        	{
        		if(!is_null($stmt))
        		{
        			mysqli_stmt_close($stmt);
        		}
        	}
        }
        
        // A hack for now as I am not going to mess with multiple entries. Lets just assume one only
        function GetFirstOrderCreditCard($order_id,$for_update)
        {
        	global $session;
        	global $database;
        	
        	$stmt = mysqli_stmt_init($database->getConnection());
        	
        	$query_string = "select orders_credit_cards_id, orders_credit_cards_profile_id, orders_credit_cards_provider_id, orders_credit_cards_trans_id,
        						orders_credit_cards_auth, orders_credit_cards_refund_auth, orders_credit_cards_type, orders_credit_cards_last4,
        						orders_credit_cards_card_descr FROM orders_credit_cards where orders_credit_cards_order_id=? LIMIT 1  ";
        	if($for_update==true)
        	{
        		$query_string .= " FOR UPDATE";
        	}
        	mysqli_stmt_prepare($stmt,$query_string);
        	
        	if(!mysqli_stmt_bind_param($stmt,"i",$order_id))

        	{
        			
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        				"GetFirstOrderCreditCard: Bind Failed Order: {$order_id} (" . mysqli_stmt_error($stmt) . ")");
        		mysqli_stmt_close($stmt);
        		return NULL;
        	}
        	
        	$query_result = mysqli_stmt_execute($stmt);
        	if($query_result!=TRUE)
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        					"GetFirstOrderCreditCard: Execute Failed Order: {$order_id} (" . mysqli_stmt_error($stmt) . ")");
        		mysqli_stmt_close($stmt);
        		return NULL;
        	}
        	
        	mysqli_stmt_store_result($stmt);
        	 
        	$order_card = new OrderCreditCard();
        	if(!mysqli_stmt_bind_result($stmt,
        				$order_card->card_id, $order_card->profile_id, $order_card->provider_id, $order_card->trans_id,$order_card->auth_code,
        				$order_card->refund_auth_code, $order_card->card_type, $order_card->card_last4, $order_card->card_descr))
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        					"GetFirstOrderCreditCard: Could not bind result Order: {$order_id}  (" . mysqli_stmt_error($stmt) . ")");
        		mysqli_stmt_close($stmt);
        		return NULL;
        	}
        	if( mysqli_stmt_fetch($stmt)==true)
        	{
        		
        		mysqli_stmt_close($stmt);
        		return $order_card;
        	}
        	else
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        					"GetFirstOrderCreditCard: Could not execute Order: {$order_id}  (" . mysqli_stmt_error($stmt) . ")");
        		mysqli_stmt_close($stmt);
        	}
        	
        	
        	
        }
               
        function RefundInProgressOrder(Order $order,$location_id,$incarnation,&$error_object)
        {
        	global $database;
        	
        	global $session;
        	if (!mysqli_query($database->getConnection(),"START TRANSACTION"))
        	{
        	
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        				"RefundOrder: START_TRANSACTION failed (" .
        				mysqli_error($database->getConnection() . ")" ));
        		$err_str = "Refund Order, Could not Start a transaction for Order ID: {$order->orders_id}";
        		$error_object->DisplayText = "";
        		$error_object->ErrorCodeMajor = ERROR_MAJOR_INTERNAL;
        		$error_object->ErrorCodeMinor = ERROR_MINOR_STATE_NOT_FOUND;
        		$error_object->DisplayText=$err_str;
        		$error_object->LongText=$err_str;
        		return false;
        	}
        	
        	// true => FOR UPDATE
        	//print("OID: " . $order->orders_id)
        	$order_credit_card = $this->GetFirstOrderCreditCard($order->orders_id, true);
        	if(is_null($order_credit_card))
        	{
        		$err_str = "RefundOrder: No Order Credit Card For Order : " . $order->orders_id;
        		$error_object->DisplayText = "";
        		$error_object->ErrorCodeMajor = ERROR_MAJOR_INTERNAL;
        		$error_object->ErrorCodeMinor = ERROR_MINOR_STATE_NOT_FOUND;
        		$error_object->DisplayText=$err_str;
        		$error_object->LongText=$err_str;
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        				$err_str );
        		return false;
        	}
        	$stmt=mysqli_stmt_init($database->getConnection());
        	
        	$order_tag = $database->GetNextGlobalOrderTag();
        	
        	mysqli_stmt_prepare($stmt,"UPDATE " . $database->GetOrdersTableName(ORDER_TYPE_IN_PROGRESS, $location_id) .
        					" SET orders_disposition=" .  ORDER_REFUNDED . ", global_order_tag={$order_tag}  WHERE orders_id=? ");
        	
        	if(!mysqli_stmt_bind_param($stmt,"i",$order->orders_id))
        	{
        	
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),
        				LOG_CODE_ERROR,"RefundInProgressOrder: UPDATE Bind Failed ORDERID={$order->orders_id}(" 
        				. mysqli_stmt_error($stmt) . ")");
        		mysqli_query($database->getConnection(),"ROLLBACK");
        		
        		return false;
        		
        	}
        	$query_result = mysqli_stmt_execute($stmt);
        	
        	if(!$query_result)
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        				"RefundInProgressOrder: Execute Failed ORDERID={$order->orders_id}(" 
        				. mysqli_stmt_error($stmt) . ")"
        				. mysqli_stmt_error($stmt) . ")");
        		
        		mysqli_query($database->getConnection(),"ROLLBACK");
         	  	return false;
        	}
        	$refund_tran_id=-1;
        	if($database->VoidOrRefundTransaction($order_credit_card,$order,$refund_tran_id,$order->orders_total_cost,
        								$order->orders_authorize_net_customer_profile_id,$error_object)!=true)
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        				"RefundInProgressOrder: ReverseTransaction Failed ORDERID={$order->orders_id} (" . mysqli_stmt_error($stmt) . ")". mysqli_stmt_error($stmt) . ")");
        		
        		mysqli_query($database->getConnection(),"ROLLBACK");
        		return false;
        	}
        	mysqli_stmt_close($stmt);
        	
        	// Now we are comitted to COMMIT as we cannot undo the Reverse. Any DB errors from here on, we just have to live with
        	$this->UpdateCreditCardRefundAuth($order_credit_card->card_id,$refund_tran_id);
        	mysqli_query($database->getConnection(),"COMMIT");
        	
        	return true;
        }
        
        function UpdateCreditCardRefundAuth($card_id, $refund_code)
        {
        	global $database;
        	$stmt=mysqli_stmt_init($database->getConnection());
        	 
        	mysqli_stmt_prepare($stmt,"UPDATE orders_credit_cards SET orders_credit_cards_refund_auth=? WHERE orders_credit_cards_id=? ");
        	 
        	if(!mysqli_stmt_bind_param($stmt,"si",$refund_code,$card_id))
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),
        				LOG_CODE_ERROR,"UpdateCreditCardRefundAuth: UPDATE Bind Failed CardID={$card_id}(" . mysqli_stmt_error($stmt) . ")");
        		
        		mysqli_stmt_close($stmt);
        		return false;
        	
        	}
        	$query_result = mysqli_stmt_execute($stmt);
        	 
        	if(!$query_result)
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        				"UpdateCreditCardRefundAuth: Execute Failed CardID={$card_id}(" . mysqli_stmt_error($stmt) . ")");
        	
        		mysqli_stmt_close($stmt);
        		return false;
        	}
        	mysqli_stmt_close($stmt);
        	return true;
        }
        
        function UpdateInProgressOrderDisposition($order_id,$location_id,$incarnation,$disposition)
        {
        	global $session;
        	global $database;
        	
        	// For now we ignore incarnation. Technically, SetTimeHere should increment it, then we need to send the
        	// incarnation (or whole order) down to OrderManager blah blah which we dont do yet. Maybe we should ?
        	$stmt=mysqli_stmt_init($database->getConnection());
        	$order_tag = $database->GetNextGlobalOrderTag();
        	$query = "UPDATE " . $database->GetOrdersTableName(ORDER_TYPE_IN_PROGRESS, $location_id)
        					. " SET orders_disposition=?, global_order_tag={$order_tag} WHERE orders_id=? ";
        	mysqli_stmt_prepare($stmt, $query);
        					
        	
        	//print("Q: {$query} OID: {$order_id}");
        	
        	if(!mysqli_stmt_bind_param($stmt,"ii",$disposition,$order_id))
        	{
        	
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),
        				LOG_CODE_ERROR,"UpdateInProgressOrderDisposition: UPDATE Bind Failed ORDERID={$order_id} (" 
        					. mysqli_stmt_error($stmt) . ")");
        		return false;
        	}
        	$query_result = mysqli_stmt_execute($stmt);
        	
        	if(!$query_result)
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        				"UpdateInProgressOrderDisposition: Execute Failed ORDERID={$order_id}(" . mysqli_stmt_error($stmt) . ")"
        				. mysqli_stmt_error($stmt) . ")");
        		mysqli_stmt_close($stmt);
        		return false;
        	}
        	
        	mysqli_stmt_close($stmt);
        	return true;
        	
        }
}
?>