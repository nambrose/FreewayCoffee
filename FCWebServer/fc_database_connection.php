<?php
/**
 * fc_database_connection.php
 * (C) Copyright Freeway Coffee, 2011,2012,2013
 */
    
    /*
     Building inserts can be annoying. This helper function inserts an array into a table, using the key names as column names:
     
     <?php
     private function store_array (&$data, $table, $mysqli)
     {
     $cols = implode(',', array_keys($data));
     foreach (array_values($data) as $value)
     {
     isset($vals) ? $vals .= ',' : $vals = '';
     $vals .= '\''.$this->mysql->real_escape_string($value).'\'';
     }
     $mysqli->real_query('INSERT INTO '.$table.' ('.$cols.') VALUES ('.$vals.')');
     }
    ?>
*/

require_once("fc_constants.php");
require_once("AuthorizeNet.php");
require_once("auth_net.php");
require_once("fc_user_objects.php");
require_once("fc_global_objects.php");
require_once("fc_location_objects.php");

class DatabaseConnection
{
	var $connection;         //The MySQL database connection

	function DatabaseConnection()
	{
		//$this->connection = mysqli_connect('nickambr.ipowermysql.com', 'fc_server','test123',DB_NAME);
		$this->connection=mysqli_connect(DB_SERVER,DB_USER,DB_PASS,DB_NAME);
		if(!$this->connection)
		{
			print("Bad DB Connection");
			
		}
	}
	
	function GetGlobalSettings(&$global_settings)
	{
		$query = "SELECT * from global_settings WHERE 1";

		$result = mysqli_query($this->getConnection(),$query);
		
		if($result===FALSE)
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"GetGlobalSettings: Could not Query ");
			return false;
		}
		
		while ($row = mysqli_fetch_assoc($result)) 
		{
			$global_settings[$row['gs_name']]=$row['gs_value'];
		}
		return true;
		
	}

	function getConnection()
	{
		return $this->connection;
	}
	
	function getReadConnection()
	{
		return $this->connection;
	}
	
	function GetTimeAsUTCAndLocal($unix_time,&$time_UTC,&$time_Local)
	{
		$time_UTC=gmdate( DEFAULT_DATE_TIME_FORMAT,$unix_time);
		$time_Local=date( DEFAULT_DATE_TIME_FORMAT,$unix_time);
	}
	
	function GetNowAsUTC()
	{
		return gmdate( DEFAULT_DATE_TIME_FORMAT,time());
	}
	
	// inputs in microtime format
	function GetTimeDiffInUsec($end_time,$start_time)
	{
		$interval = ($end_time-$start_time);
		return $interval*1000;
	}
	
	// Should go in Order DB eventually
	function GetNextOrderID()
	{
		$query = "REPLACE INTO order_id_gen(stub) VALUES('a')";
		mysqli_query($this->getConnection(),$query);
		return mysqli_insert_id($this->getConnection());
		
	}
	
	function GetNextGlobalOrderTag()
	{
		$query = "REPLACE INTO global_order_tag_gen(stub) VALUES('a')";
		mysqli_query($this->getConnection(),$query);
		return mysqli_insert_id($this->getConnection());
	
	}

	function GetOrdersTableName($order_type, $location_id)
	{
		if($order_type == ORDER_TYPE_IN_PROGRESS)
		{
			return INPROGRESS_ORDERS_TABLE_NAME_PREFIX . "_" . $location_id;
		}
		else if($order_type== ORDER_TYPE_PROCESSED)
		{
			//return ORDERS_TABLE_NAME_PREFIX . "_" . $location_id;
			return ORDERS_TABLE_NAME_PREFIX; // JUst one table for now
		}
		else
		{
			return "GetOrdersTableName INVALID ARGUMENT";
		}
	}
			
	
	function IsStoreOpen(Location $location_info,$local_time_needed)
	{
		global $session;
		
		$day = date('D',$local_time_needed); // Just Mon Tue etc
		
		//print("Loc: " . $location_info->LocationID);
		//print("Day: " . $day);
		//print("Loc Time Needed: " . $local_time_needed);
		
		$stmt = mysqli_stmt_init($this->getConnection());
		mysqli_stmt_prepare($stmt,"SELECT loc_base_hours_time_start, loc_base_hours_time_end from location_base_hours where loc_base_hours_location_id=? AND loc_base_hours_day=?");
		
		
		mysqli_stmt_bind_param($stmt,'is',$location_info->LocationID,$day);
		if(mysqli_stmt_execute($stmt))
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"IsStoreOpen: Could not store result LocactionID: {$location_info->LocationID} (" . mysqli_stmt_error($stmt) . ")" );
			return false;
		}
		
		
		if(mysqli_stmt_bind_result($stmt,$range_start,$range_end)!=TRUE)
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"IsStoreOpen: Could not bind result LocationID: {$location_info->LocationID}(" . mysqli_stmt_error($stmt) . ")");
			mysqli_stmt_close($stmt);
			return false;
		}
		
		while(mysqli_stmt_fetch($stmt)==true)
		{
			//print("Comparing Range: {$range_start} / {$range_end}");
			$range_start_time = strtotime($range_start);
			$range_end_time = strtotime($range_end);
			if(($range_start_time===false) || ($range_end_time===false))
			{
				$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
						"IsStoreOpen: Start or End could not be converted to times: Start: {$range_start}, End: {$range_end}  Loc ID: {$location_info->LocationID}(" . mysqli_stmt_error($stmt) . ")");
				mysqli_stmt_close($stmt);
				return false;
			}
			if(($local_time_needed>=$range_start_time) && ($local_time_needed<=$range_end_time))
			{
				mysqli_stmt_close($stmt);
				return true;
			}
		}
		mysqli_stmt_close($stmt);
		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
				"IsStoreOpen: LocationID: {$location_info->LocationID} Couldnt find any matching Day/Time Ranges to compare. Assuming Store closed");
		return false;
		
	}
                             
    function IsUserHereSetForOrder($order_id)
    {
        global $session;
        
        $stmt = mysqli_stmt_init($this->getConnection());
        mysqli_stmt_prepare($stmt,"SELECT orders_user_here_time from orders where orders_id=? AND orders_userid=?");
        mysqli_stmt_bind_param($stmt,'ii',  $order_id,$_SESSION['user_id']);
        
        if(mysqli_stmt_execute($stmt))
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"IsUserHereSetForOrder: Could not store result from SELECT ORDERID={$order_id} (" . mysqli_stmt_error($stmt) . ")" );
            mysqli_stmt_close($stmt);
            return false;
        }
        
        
        
        if(!mysqli_stmt_bind_result($stmt,$user_here_time))
        {
            
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"IsUserHereSetForOrder: Could not bind result from  SELECT ORDERID={$order_id}(" . mysqli_stmt_error($stmt) . ")");
           mysqli_stmt_close($stmt);
           return false;
        
        }
        mysqli_stmt_fetch($stmt);
        mysqli_stmt_close($stmt);
        
        if($user_here_time!==NULL)
        {
           return true;
        }
        
        return false;
    }
    
    function UpdateLocationOpenMode($location_id,$new_mode)
    {
    	global $session;
    	global $database;
    	
    	if( ($new_mode!=DB_LOCATION_OPEN_MODE_FORCE_CLOSED) && 
    		($new_mode!=DB_LOCATION_OPEN_MODE_FORCE_OPEN) &&
    		($new_mode!=DB_LOCATION_OPEN_MODE_OBEY_HOURS))
    	{
    		return false;
    	}	
    	
    	$stmt=mysqli_stmt_init($this->getConnection());
    	
    	$query = "UPDATE locations SET location_open_mode=?, incarnation=incarnation+1 where location_id=?";
    	
    	if(!mysqli_stmt_prepare($stmt,$query))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"UpdateLocationOpenMode: UPDATE Prepare Failed LocationID={$location_id}");
    		return false;
    	}
    	if(!mysqli_stmt_bind_param($stmt,"ii",$new_mode,$location_id))
        {
		
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            		"UpdateLocationOpenMode: UPDATE Bind Failed LocationID={$location_id}");
			return false;
		}
        $query_result = mysqli_stmt_execute($stmt);
        
        if(!$query_result)
        {
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            		"UpdateLocationOpenMode: Execute Failed LocationID={$location_id}");
            mysqli_stmt_close($stmt);
            return false;
        }
        
        mysqli_stmt_close($stmt);
              
        return true;
    }
    
    
    function UpdateUserHereTimeForOrder($order_id,&$time_here,$arrive_mode)
    {
        global $session;
        global $database;
        
        $unix_time_here = time();
        $this->GetTimeAsUTCAndLocal($unix_time_here,$gm_time_here,$time_here);
        
        $stmt=mysqli_stmt_init($this->getConnection());
        
        $ip_orders_table = $this->GetOrdersTableName(ORDER_TYPE_IN_PROGRESS, $session->user_data->user_location_id);
        
        $order_tag = $database->GetNextGlobalOrderTag();
        
        mysqli_stmt_prepare($stmt,"UPDATE " . $ip_orders_table ." SET orders_user_here_time=?,  orders_arrive_mode=?, global_order_tag={$order_tag} WHERE orders_id=? AND orders_userid=?");
        
        if(!mysqli_stmt_bind_param($stmt,"siii",$gm_time_here,$arrive_mode,$order_id,$_SESSION['user_id']))
        {
		
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"UpdateUserHereTimeForOrder: UPDATE Bind Failed ORDERID={$order_id}(" . mysqli_stmt_error($stmt) . ")");
			return false;
		}
        $query_result = mysqli_stmt_execute($stmt);
        
        if(!$query_result)
        {
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"UpdateUserHereTimeForOrder: Execute Failed ORDERID={$order_id}(" . mysqli_stmt_error($stmt) . ")". mysqli_stmt_error($stmt) . ")");
            mysqli_stmt_close($stmt);
            return false;
        }
        
        mysqli_stmt_close($stmt);
              
        return true;
        
    }
    
    
	// Assumes users table has that many free drinks. Will Check against userdata (but not decrement it -- Make Order does that on success)
	function DecrementUserFreeDrinksCount($user_id,$reduce_count)
	{
		global $session;
		if($session->user_data->user_free_drinks_count<$reduce_count)
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"DecrementUserFreeDrinksCount: Failed. User Free Drinks Count is: " . $session->user_data->user_free_drinks_count .
					"But Asked To Decement By: {$reduce_count}" );
			return false;
		}
		
		$stmt = mysqli_stmt_init($this->getConnection());
		//$query = "UPDATE users set user_free_drinks_count = user_free_drinks_count - ?, incarnation=incarnation+1 WHERE user_id=?";
		$query = "UPDATE users set user_free_drinks_count = user_free_drinks_count - ? WHERE user_id=?";
		if(!mysqli_stmt_prepare($stmt,$query))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"DecrementUserFreeDrinksCount:  Prepare Failed (" . mysqli_stmt_error($stmt) . ")");
			return false;
		}
		
		if(!mysqli_stmt_bind_param($stmt,'ii',$reduce_count,$user_id))
		{
				
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"DecrementUserFreeDrinksCount:  Bind Failed (" . mysqli_stmt_error($stmt) . ")");
			return false;
		}
		$query_result = mysqli_stmt_execute($stmt);
		if(!$query_result)
		{
			$err_str = "DecrementUserFreeDrinksCount:  Execute Failed (" . mysqli_stmt_error($stmt) . ")";
			 
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,$err_str);
			 
			return false;
		}
		
		
		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
				"DecrementUserFreeDrinksCount: Decremented By: " . $reduce_count);
		
		mysqli_stmt_close($stmt);
		
		return true;
	}
    
    function GetUserDefaultCreditCard($user_id)
    {
        global $session;
        
        $credit_card=array();
        $credit_card['user_credit_card_id']="";
        $credit_card['user_credit_card_last4']="";
        $credit_card['user_credit_card_descr']="";
        $credit_card['user_credit_card_exp']="";
        $credit_card['user_credit_card_card_type']="";
        $credit_card['user_credit_card_payment_profile_id']=0;
        
        $stmt = mysqli_stmt_init($this->getConnection());

        mysqli_stmt_prepare($stmt,"SELECT user_credit_card_id,user_credit_card_last4, user_credit_card_descr,user_credit_card_exp,user_credit_card_card_type,user_credit_card_payment_profile_id  from user_credit_cards WHERE user_id=? AND user_credit_card_default=1");
        mysqli_stmt_bind_param($stmt,'i',$user_id);
        //print(mysqli_stmt_error($stmt));
               
        if(mysqli_stmt_execute($stmt))
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            		"GetUserDefaultCreditCard: User: {$user_id} Could not store result from GetUserDefaultCreditCard SELECT (" . mysqli_stmt_error($stmt) . ")");
            return NULL;
        }
        
        if(mysqli_stmt_num_rows($stmt) > 1)
		{
            AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            		"GetUserDefaultCreditCard: User: {$user_id} Num Rows =" . mysqli_stmt_num_rows($stmt) . "Should be 1 or zero (bad). Using first. (" . mysqli_stmt_error($stmt) . ")");
		}
        if(!mysqli_stmt_bind_result($stmt,$credit_card['user_credit_card_id'],$credit_card['user_credit_card_last4'],$credit_card['user_credit_card_descr'],$credit_card['user_credit_card_exp'],
                                    $credit_card['user_credit_card_card_type'],$credit_card['user_credit_card_payment_profile_id']))
        {
            AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"GetUserDefaultCreditCard: Could not bind result from SELECT (" . mysqli_stmt_error($stmt) . ")");
            return NULL;
        }
        mysqli_stmt_fetch($stmt);
        //print ("NUM ROWS: " . mysqli_stmt_num_rows($stmt));
        if(mysqli_stmt_num_rows($stmt)==0)
        {
            // No Card
            return NULL;
        }
        mysqli_stmt_close($stmt);
        return $credit_card;             
        
    }
    //////////////////////////////
    
    /////// Locations 
    function GetLocation($location_id, $is_active)
    {
    	global $session;
    	
    	$stmt = mysqli_stmt_init($this->getConnection());
    	
    	
    	$query = Location::$GET_LOCATION_QUERY . " WHERE location_id=? ";
    	$query .= $this->getIsActiveAndClause($active_mode);
    	
    	mysqli_stmt_prepare($stmt,$query);
    	mysqli_stmt_bind_param($stmt,'i',$location_id);
    	if(mysqli_stmt_execute($stmt))
    	{
    		mysqli_stmt_store_result($stmt);
    	}
    	else
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"GetLocation: Could not store result from SELECT  (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    	
    	$location=$this->GetNextLocations($stmt);
    	$this->CloseGetLocations($stmt);
    	return $location;
    	
    }
    function OpenGetLocations($is_active)
    {
    
    	global $session;
    	$stmt = mysqli_stmt_init($this->getConnection());
    
    
    	$query = Location::$GET_LOCATION_QUERY;
    	
    	$query = $query . $this->getIsActiveWhereClause($active_mode);
    	
    	mysqli_stmt_prepare($stmt,$query);
    	if(mysqli_stmt_execute($stmt))
    	{
    		mysqli_stmt_store_result($stmt);
    	}
    	else
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"OpenGetLocations: Could not store result from SELECT  (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    
    	return $stmt;
    }
    
   
    function GetNextLocations($stmt)
    {
    	$location = new Location();
    	
    	if(!mysqli_stmt_bind_result($stmt,$location->LocationID,$location->LocationDescription,
    			$location->LocationAddress,$location->LocationHours,$location->LocationGPSLat,$location->LocationGPSLong,
    			$location->LocationEmail, $location->LocationAdminEmail,
    			$location->LocationTZ,$location->LocationPhone,$location->LocationOpenMode,
    			$location->LocationInstructions,
    			$location->LocationLongDescr,$location->LocationMenuID, $location->is_active, $location->incarnation,
    			$location->LocationShowPhone,$location->LocationShowEmail, $location->LocationTTMNumber, $location->LocationSalesTaxRate, $location->LocationConvenienceFee
    			))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"GetNextLocations Could not bind result  (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    
    	if( mysqli_stmt_fetch($stmt)==true)
    	{
    		$location->allowed_arrival_modes = LocationArriveMode::DB_GetAllowedArrivalTypesForLocationID($this, $location->LocationID);
    		$location->allowed_pay_methods=LocationPayMethod::DB_GetAllowedPayTypesForLocationID($this, $location->LocationID);
    		$location->valid=true;
    		return $location;
    	}
    	return NULL;
    
    }
    
    function CloseGetLocations($stmt)
    {
    	mysqli_stmt_close($stmt);
    
    }
    
    
    //////////// END LOCATIONS
    /// APP Settings (Global to all)
    function OpenGetAppSettings()
    {
    
    	global $session;
    	$stmt = mysqli_stmt_init($this->getConnection());
    
    
    	$query = "SELECT app_setting_name, app_setting_value  FROM app_settings WHERE 1";
    	mysqli_stmt_prepare($stmt,$query);
    	if(mysqli_stmt_execute($stmt))
    	{
    		mysqli_stmt_store_result($stmt);
    	}
    	else
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"OpenGetAppSettings: Could not store result from SELECT  (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    
    	return $stmt;
    }
    function GetNextAppSetting($stmt)
    {
    	$setting = new AppSetting();
    
    	if(!mysqli_stmt_bind_result($stmt,$setting->setting_name,$setting->setting_value))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"GetNextAppSetting Could not bind result  (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    
    	if( mysqli_stmt_fetch($stmt)==true)
    	{
    
    		return $setting;
    	}
    	return NULL;
    
    }
    
    function CloseGetAppSettings($stmt)
    {
    	mysqli_stmt_close($stmt);
    
    }
    //////// END APP Settings
    
    ///////// USER TIPS //////////////////
    function OpenGetAllUserTips($user_id)
    {
    
    	global $session;
    	$stmt = mysqli_stmt_init($this->getConnection());
    
    
    	$query = "SELECT ut_user_id, ut_location_id, ut_tip_type, ut_tip_amount,ut_tip_round_up FROM user_tips where ut_user_id=? ";
    	mysqli_stmt_prepare($stmt,$query);
    	if(!mysqli_stmt_bind_param($stmt,"i",$user_id))
   		{
   			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
   					"OpenGetUserTips:  Bind User ID Failed (" . mysqli_stmt_error($stmt) . ")");
   			return NULL;
    	}
    	
    	if(mysqli_stmt_execute($stmt))
    	{
    		mysqli_stmt_store_result($stmt);
    	}
    	else
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"OpenGetUserTips: Could not store result from SELECT (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    
    	return $stmt;
    }
    
    function OpenGetUserTip($user_id,$location_id)
    {
    
    	global $session;
    	$stmt = mysqli_stmt_init($this->getConnection());
    
    
    	$query = "SELECT ut_user_id, ut_location_id, ut_tip_type, ut_tip_amount,ut_tip_round_up FROM user_tips where ut_user_id=? AND ut_location_id=?";
    	mysqli_stmt_prepare($stmt,$query);
    	if(!mysqli_stmt_bind_param($stmt,"ii",$user_id,$location_id))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"OpenGetUserTip: Bind User ID Failed Location: {$location_id} (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    	 
    	if(mysqli_stmt_execute($stmt))
    	{
    		mysqli_stmt_store_result($stmt);
    	}
    	else
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"OpenGetUserTip: Could not store result from SELECT Location: {$location_id} (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    
    	return $stmt;
    }
    
    function GetNextUserTips($stmt)
    {
    	$user_tip = new UserTip();
    	 
    	if(!mysqli_stmt_bind_result($stmt,$user_tip->user_id,$user_tip->location_id,$user_tip->tip_type, $user_tip->tip_amount, $user_tip->round_up))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"GetNextUserTips Could not bind result  (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    
    	if( mysqli_stmt_fetch($stmt)==true)
    	{
    
    		return $user_tip;
    	}
    	return NULL;
    
    }
    
    function CloseGetUserTips($stmt)
    {
    	mysqli_stmt_close($stmt);
    
    }
    function GetUserTip($user_id,$location_id)
    {
    	$stmt = $this->OpenGetUserTip($user_id,$location_id);
    	if($stmt==NULL)
    	{
    		return NULL;
    	}	
    	
    	$tip = $this->GetNextUserTips($stmt);
    	$this->CloseGetUserTips($stmt);
    	return $tip;
    }
    
    function AddUserTip (UserTip $tip)
    {
    	if(is_null($tip))
    	{
    		return false;
    	}
    	$stmt=mysqli_stmt_init($this->getConnection());
    	 
    	$query = "INSERT INTO user_tips (ut_id,ut_user_id,ut_location_id,ut_tip_type,ut_tip_amount,ut_tip_round_up) VALUES (NULL,?,?, ?, ?,?)";
    	
    	mysqli_stmt_prepare($stmt,$query);
    	 
    	if(!mysqli_stmt_bind_param($stmt,"iiisi",
    			$tip->user_id,$tip->location_id, $tip->tip_type,$tip->tip_amount,$tip->round_up))
    	{
    		 
    		$err_str = "AddUserTip: Failed to UPDATE  (" . mysqli_stmt_error($stmt) . ")";
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,$err_str);
    		return false;
    	}
    	$query_result = mysqli_stmt_execute($stmt);
    	if(!$query_result)
    	{
    		$err_str = "AddUserTip:  Execute Failed (" . mysqli_stmt_error($stmt) . ")";
    		 
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,$err_str);
    		 
    		return false;
    	}
    	 
    	 
    	$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
    			"AddUserTip: Added Tip, Location: " . $tip->location_id);
    	 
    	mysqli_stmt_close($stmt);
    	return true;
    }
    
    function UpdateUserTip(UserTip $tip)
    {
    	if(is_null($tip))
    	{
    		return false;
    	}
    	$stmt=mysqli_stmt_init($this->getConnection());
    	
    	$query = "UPDATE user_tips SET ut_user_id=?, ut_location_id=?, ut_tip_type=?, ut_tip_amount=?, ut_tip_round_up=? WHERE ut_user_id=? AND ut_location_id=?";
 
    	mysqli_stmt_prepare($stmt,$query);
    	
    	if(!mysqli_stmt_bind_param($stmt,"iiisiii",
    							   $tip->user_id,$tip->location_id, $tip->tip_type,$tip->tip_amount,$tip->round_up,$tip->user_id,$tip->location_id ))
    	{
    	
    		$err_str = "UpdateUserTip: Failed to UPDATE  (" . mysqli_stmt_error($stmt) . ")";
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,$err_str);
    		return false;
    	}
    	$query_result = mysqli_stmt_execute($stmt);
    	if(!$query_result)
    	{
    		$err_str = "UpdateUserTip:  Execute Failed (" . mysqli_stmt_error($stmt) . ")";
    		 
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,$err_str);
    	
    		return false;
    	}
    	
    	
    	$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
    			"UpdateUserTip: Updated Tip, Location: " . $tip->location_id);
    	
    	mysqli_stmt_close($stmt);
    	return true;
    	
    }
    
    ///////// END USER TIPS /////////////
    
    /////////////////////////////
    function OpenGetDrinkTypeOptionsForDrinkTypeID($drink_type_id, $active_mode)
    {
    
    	global $session;
    	$stmt = mysqli_stmt_init($this->getConnection());
    
    	 $query="SELECT dto_id,  dto_drink_type_id,  dto_drink_option_id, dto_drink_option_group_id, " .
    						"dto_min_val, dto_max_val, dto_price, dto_charge_each_count FROM drink_types_options WHERE dto_drink_type_id=? ";
    	
   		
   		$query .= $this->getIsActiveAndClause($active_mode);
    	 	 	 	 	 	 	
    	mysqli_stmt_prepare($stmt,$query);
    
    	if(!mysqli_stmt_bind_param($stmt,"i",$drink_type_id))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"OpenGetDrinkTypeOptionsForDrinkTypeID:  Bind Failed (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    	 
    	if(mysqli_stmt_execute($stmt))
    	{
    		mysqli_stmt_store_result($stmt);
    	}
    	else
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"OpenGetDrinkTypeOptionsForDrinkTypeID: Could not store result from SELECT (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    
    	return $stmt;
    }
    
    function GetNextDrinkTypeOptionsForDrinkTypeID($stmt,$menu_id)
    {
    	$drink_type_option = new DrinkTypesOption();
    	
    	if(!mysqli_stmt_bind_result($stmt,$drink_type_option->drink_types_option_id,
    								$drink_type_option->drink_types_option_drink_type_id,$drink_type_option->drink_types_drink_option_id,
    								$drink_type_option->drink_types_drink_option_group_id,$drink_type_option->drink_types_option_range_min,
    								$drink_type_option->drink_types_option_range_max, $drink_type_option->drink_types_option_cost_per,
    								$drink_type_option->dto_charge_each_count
    								))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"GetNextDrinkOptionForDrinkOptionsGroup Could not bind result  (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    
    	if( mysqli_stmt_fetch($stmt)==true)
    	{
    		// menu_id not stored in this table
    		$drink_type_option->menu_id = $menu_id;
    		return $drink_type_option;
    	}
    	return NULL;
    
    }
    
    function CloseGetDrinkTypeOptionsForDrinkTypeID($stmt)
    {
    	mysqli_stmt_close($stmt);
    
    }
    
    ////////////////////////////
    
    function OpenGetDrinkOptionsForDrinkOptionGroup($group_id, $active_mode)
    {
    	 
    	global $session;
    	$stmt = mysqli_stmt_init($this->getConnection());
   	
    	$query = "SELECT do_id,  do_option_group,  do_option_name, do_sort_order FROM drink_options WHERE do_option_group=? ";
    	
    	$query .= $this->getIsActiveAndClause($active_mode);
    	
    	mysqli_stmt_prepare($stmt, $query);
    	 
    	if(!mysqli_stmt_bind_param($stmt,"i",$group_id))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"OpenGetDrinkOptionsForDrinkOptionGroup:  Bind Failed (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    	
    	if(mysqli_stmt_execute($stmt))
    	{
    		mysqli_stmt_store_result($stmt);
    	}
    	else
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"OpenGetDrinkOptionsForDrinkOptionGroup: Could not store result from SELECT (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    	 
    	return $stmt;
    }
    

    function GetNextDrinkOptionsForDrinkOptionGroup($stmt,$menu_id)
    {
    	$drink_option = new DrinkOption();
    
    	if(!mysqli_stmt_bind_result($stmt,$drink_option->option_id,$drink_option->option_group_id,
    			$drink_option->option_name,$drink_option->sort_order))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"GetNextDrinkOptionForDrinkOptionsGroup Could not bind result  (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    
    	if( mysqli_stmt_fetch($stmt)==true)
    	{
    		// Not stored directly as its just gonna mess up the DB
    		$drink_option->menu_id=$menu_id;
    		return $drink_option;
    	}
    	return NULL;
    
    }
    
    function CloseGetDrinkOptionsForDrinkOptionsGroup($stmt)
    {
    	mysqli_stmt_close($stmt);
    
    }
    
    function getIsActiveAndClause($active_mode)
    {
    	if(active_only==EnumFetchActive::FETCH_ACTIVE_ONLY)
    	{
    		return " AND is_active=1 ";
    	}
    	else if($active_only==EnumFetchActive::FETCH_INACTIVE_ONLY)
    	{
    		return " AND is_active=0 ";
    	}
    	else if($active_mode==EnumFetchActive::FETCH_ACTIVE_AND_INACTIVE)
    	{
    		return " ";
    	}
    	else
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"getActiveWhereClause: INTERNAL Bad Active Mode Passed; [ {$active_mode}]");
    		
    		return " ";
    	}
    }
    
    function getIsActiveWhereClause($active_mode)
    {
    	if(active_only==EnumFetchActive::FETCH_ACTIVE_ONLY)
    	{
    		return " WHERE is_active=1 ";
    	}
    	else if($active_only==EnumFetchActive::FETCH_INACTIVE_ONLY)
    	{
    		return " WHERE is_active=0 ";
    	}
    	else if($active_mode==EnumFetchActive::FETCH_ACTIVE_AND_INACTIVE)
    	{
    		return " ";
    	}
    	else
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"getActiveWhereClause: INTERNAL Bad Active Mode Passed; [ {$active_mode}]");
    
    		return " ";
    	}
    }
    
    function OpenGetDrinkOptionGroups($menu_id,$active_only)
    {
    	
    	global $session;
    	$stmt = mysqli_stmt_init($this->getConnection());
    
    	$query = "SELECT dog_id, dog_name, dog_long_name, dog_multi, dog_sortorder, dog_menu_id,is_active FROM drink_option_groups WHERE dog_menu_id=? ";
    	
    	$query .= $this->getIsActiveAndClause($active_only);
    		
    	mysqli_stmt_prepare($stmt,$query);
    	
    	mysqli_stmt_bind_param($stmt,"i", $menu_id);

    	
    	if(mysqli_stmt_execute($stmt))    	
    	{
    		mysqli_stmt_store_result($stmt);
    	}
    	else
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"OpenGetDrinkOptionGroups: Menu:[{$menu_id}] Could not store result from SELECT (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    	
    	return $stmt;
    }
    
    function GetNextDrinkOptionGroups($stmt)
    {
    	$drink_option_group = new DrinkOptionGroup();
    
    	
    	if(!mysqli_stmt_bind_result($stmt,$drink_option_group->group_id,$drink_option_group->group_part_name,
    								$drink_option_group->group_long_name,
    								$drink_option_group->group_multi_select, $drink_option_group->sort_order,
    								$drink_option_group->dog_menu_id,
    								$drink_option_group->is_active))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"GetNextDrinkOptionGroups Menu:[{$menu_id}] Could not bind result  (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    
    	if( mysqli_stmt_fetch($stmt)==true)
    	{
    
    		return $drink_option_group;
    	}
    	return NULL;
    
    }
    
    function CloseGetDrinkOptionGroups($stmt)
    {
    	mysqli_stmt_close($stmt);
    
    }
    
    
    /////////// ITEM TYPE GROUPS
    function OpenGetItemTypeGroups($menu_id,$active_only)
    {
    	 
    	global $session;
    	$stmt = mysqli_stmt_init($this->getConnection());
    
    	$query = "SELECT itg_id, itg_menu_id, itg_name, sort_order, is_active FROM item_type_groups WHERE itg_menu_id=? ";
    	 
    	$query .= $this->getIsActiveAndClause($active_only);
    
    	mysqli_stmt_prepare($stmt,$query);
    	
    	mysqli_stmt_bind_param($stmt,"i", $menu_id);
    	 
    	if(mysqli_stmt_execute($stmt))
    	{
    		mysqli_stmt_store_result($stmt);
    	}
    	else
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"OpenGetItemTypeGroups: Menu:[{$menu_id}] Could not store result from SELECT (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    	 
    	return $stmt;
    }
    
    function GetNextItemTypeGroups($stmt)
    {
    	$item_type_group = new ItemTypeGroup();
    
    	 
    	if(!mysqli_stmt_bind_result($stmt,$item_type_group->id, $item_type_group->menu_id, $item_type_group->group_name,
    			$item_type_group->sort_order, $item_type_group->is_ative))
    			

    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"GetNextTypeGroups Menu:[{$menu_id}] Could not bind result  (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    
    	if( mysqli_stmt_fetch($stmt)==true)
    	{
    
    		return $item_type_group;
    	}
    	return NULL;
    
    }
    
    function CloseGetItemTypeGroups($stmt)
    {
    	mysqli_stmt_close($stmt);
    
    }
    
   
    // END ITEM TYPE GROUPS
    
    function OpenGetDrinkTypes($menu_id,$active_mode)
    {
        global $session;
        $stmt = mysqli_stmt_init($this->getConnection());
        
        $query = "SELECT drink_type_id,  drink_type_short_descr,  drink_type_long_descr, drink_type_text, sort_order, item_type_base_price, dr_types_menu_id, drtypes_item_group_id, item_type_type FROM drink_types WHERE dr_types_menu_id=?";
        
        $query .= $this->getIsActiveAndClause($active_mode);
        
        mysqli_stmt_prepare($stmt,$query);
                             
        mysqli_stmt_bind_param($stmt,'i',$menu_id);
        
        if(mysqli_stmt_execute($stmt))
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            		"OpenGetDrinkTypes: MenuID: [ {$menu_id} ] Could not store result from SELECT (" . mysqli_stmt_error($stmt) . ")");
            return NULL;
        }
        
        return $stmt;   
    }
    
    function GetNextDrinkTypes($stmt)
    { 
        $drink_type = new DrinkType();
        
        if(!mysqli_stmt_bind_result($stmt,$drink_type->drink_type_id,$drink_type->drink_type_short_descr,$drink_type->drink_type_long_descr,
                                    $drink_type->drink_type_text, $drink_type->sort_order, $drink_type->base_price,
        							$drink_type->menu_id,$drink_type->item_type_group, $drink_type->item_type_type))
        {
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"GetNextDrinkTypes Could not bind result  (" . mysqli_stmt_error($stmt) . ")");
            return NULL;
        }
        
        if( mysqli_stmt_fetch($stmt)==true)
        {
            
            return $drink_type;
        }
        return NULL;
        
    }
    
    function CloseGetDrinkTypes($stmt)
    {
        mysqli_stmt_close($stmt);
        
    }

      
    ///// MANDATORY DRINK OPTIONS ******************************************
    
    function OpenDrinkTypesMandatoryOptions($menu_id,$active_mode)
    {
        global $session;
        $stmt = mysqli_stmt_init($this->getConnection());
        
        $query = "SELECT mdo_id,mdo_drink_type_id, mdo_opt_group_id,menu_id from drink_types_mandatory_options WHERE menu_id=? ";
        
        $query .= $this->getIsActiveAndClause($active_mode);
        
        mysqli_stmt_prepare($stmt,$query);
        
        mysqli_stmt_bind_param($stmt,"i",$menu_id);
        
        if(mysqli_stmt_execute($stmt))
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"OpenMandatoryDrinkOptions: Could not store result (" . mysqli_stmt_error($stmt) . ")");
            return NULL;
        }
        
        return $stmt;      
        
    }
    
    function GetNextDrinkTypesMandatoryOptions($stmt)
    {
        $mand_opt = new MandatoryDrinkOption();
        
        
        if(!mysqli_stmt_bind_result($stmt,$mand_opt->mand_option_id,$mand_opt->drink_type_id,$mand_opt->drink_option_group,$mand_opt->menu_id))
        {
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"GetNextMandatoryDrinkOptions: Could not bind result (" . mysqli_stmt_error($stmt) . ")");
            return NULL;
        }
        
        if( mysqli_stmt_fetch($stmt)==true)
        {
            return $mand_opt;
        }
        return NULL;
        
    }
    
    function CloseDrinkTypesMandatoryOptions($stmt)
    {
        mysqli_stmt_close($stmt);
        
    }
    
    
    //// END MANDATORY DRINK OPTIONS ************************************
    
    
    //// DRINK DEFAULT OPTOPNS ******************************************
    
    function OpenDrinkTypesDefaultOptions($menu_id,$active_mode)
    {
        global $session;
        $stmt = mysqli_stmt_init($this->getConnection());
        
        $query = "SELECT drtypes_defopt_id, drtypes_defopt_drink_type_id, drtypes_defopt_option_group, 
        		drtypes_defopt_default_value, drtypes_defopt_quantity,menu_id from drink_types_default_options WHERE menu_id=? ";
        
        $query .= $this->getIsActiveAndClause($active_mode);
        mysqli_stmt_prepare($stmt,$query);
        
        mysqli_stmt_bind_param($stmt,"i",$menu_id);
           
        if(mysqli_stmt_execute($stmt))
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"OpenDrinkTypesDefaultOptions: Could not store result (" . mysqli_stmt_error($stmt) . ")");
            return NULL;
        }
        
        return $stmt;      
        
    }
    
    function GetNextDrinkTypesDefaultOptions($stmt)
    {
        $def_opt = new DrinkTypeDefaultOptions();
        
        
        if(!mysqli_stmt_bind_result($stmt,$def_opt->drtypes_defopt_id, $def_opt->drtypes_defopt_drink_type_id, 
                                    $def_opt->drtypes_defopt_option_group, $def_opt->drtypes_defopt_default_value,
        							$def_opt->drtypes_defoptions_quantity, $def_opt->menu_id
        							))
        {
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"GetNextDrinkTypesDefaultOptions: Could not bind result (" . mysqli_stmt_error($stmt) . ")");
            return NULL;
        }
        
        if( mysqli_stmt_fetch($stmt)==true)
        {
            return $def_opt;
        }
        return NULL;
        
    }
    
    function CloseDrinkTypesDefaultOptions($stmt)
    {
        mysqli_stmt_close($stmt);
        
    }
    
 
    //// END DRINK DEFAULT OPTOPNS ******************************************
    
    
    function DeleteUserDrinkOptionsForDrink($UserDrinkID)
    {
    	$stmt = mysqli_stmt_init($this->getConnection());
    	mysqli_stmt_prepare($stmt,"DELETE FROM user_drink_options WHERE udo_user_drink_id = ?");
    	if(!mysqli_stmt_bind_param($stmt,'i',$UserDrinkID))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"DeleteUserDrinkOptions: Could not Bind for Drink ID: {$UserDrinkID} (" .
    		mysqli_stmt_error($stmt) . ")" );
    		return false;
    	}
    	if(!mysqli_stmt_execute($stmt))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"DeleteUserDrinkOptions: Could not Execute for Drink ID: {$UserDrinkID} (" .
    		mysqli_stmt_error($stmt) . ")" );
    	
    		mysqli_stmt_close($stmt);
    		return false;
    	}
    	mysqli_stmt_close($stmt);
    	return true;
    		
    }
    
    function DeleteCreditCardForUser($card_id,$user_id)
    {
    	global $session;
    	if( ($card_id==null))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"DeleteCreditCardForUser: Card ID was NULL (" .
    				mysqli_error($this->getConnection() . ")" ));
    		return false;
    	}
    	if( ($user_id==null))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"DeleteCreditCardForUser: User ID was NULL (" .
    				mysqli_error($this->getConnection() . ")" ));
    		return false;
    	}
    	
    	$stmt = mysqli_stmt_init($this->getConnection());
    	if(!mysqli_stmt_prepare($stmt,"DELETE FROM user_credit_cards where user_credit_card_id=? AND user_id=?"))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"DeleteCreditCardForUser: Prepare failed Card ID: {$card_id} (" .
    				mysqli_error($this->getConnection() . ")" ));
    		
    		mysqli_stmt_close($stmt);
    		return false;
    	}
    	mysqli_stmt_bind_param($stmt,'ii',$card_id,$user_id);
    	
    	if(!mysqli_stmt_execute($stmt))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"DeleteCreditCardForUser: Could not DELETE Card ID: {$card_id} (" .
    		mysqli_stmt_error($stmt) . ")" );
    		mysqli_stmt_close($stmt);
    		return false;
    	}
    	mysqli_stmt_close($stmt);
    	    	
    	return true;
    }
    function DoDeleteDrink($drink_id)
    {
    	
        global $session;
        if (!mysqli_query($this->getConnection(),"START TRANSACTION")) 
        {
            
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"DoDeleteDrink: START_TRANSACTION failed (" . 
                               mysqli_error($this->getConnection() . ")" ));
            return;
        }    
        
         // First, delete the Options
        
        $this->DeleteUserDrinkOptionsForDrink($drink_id);

        $stmt = mysqli_stmt_init($this->getConnection());
        mysqli_stmt_prepare($stmt,"DELETE FROM user_drinks WHERE user_drink_id = ?");
        mysqli_stmt_bind_param($stmt,'i',$drink_id);
        
        
        if(!mysqli_stmt_execute($stmt))
		{
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"DoDeleteDrink: Could not DELETE Drink Data for drink ID: {$drink_id} (" .
                               mysqli_stmt_error($stmt) . ")" );
            
            mysqli_query($this->getConnection(),"ROLLBACK");
            mysqli_stmt_close($stmt);
            return false;
        }
        mysqli_stmt_close($stmt);
        
        
        mysqli_query($this->getConnection(),"COMMIT");
        
        return true;
        
    }
    
    // Returns NULL if fails.
    function BillCreditCard($credit_card_info, $amount,&$auth_code,&$error_object)
    {
        global $session;
        
        $transaction = new AuthorizeNetTransaction;
        $transaction->amount = $amount;
        
        $transaction->customerProfileId = $session->user_data->users_authorize_net_customer_profile_id;
        $transaction->customerPaymentProfileId = $credit_card_info['user_credit_card_payment_profile_id'];
        $request=new AuthorizeNetCIM(AUTH_NET_LOGIN,AUTH_NET_TRANS_KEY);
        
        $response = $request->createCustomerProfileTransaction("AuthCapture", $transaction,NULL);
        if($response->IsOk())
        {
            $transactionResponse = $response->getTransactionResponse();
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,"BillCreditCard Customer Profile: " 
            					. $transaction->customerProfileId 
                           		. " Payment Profile: " . $transaction->customerPaymentProfileId . " Amount: {$amount} Trans ID: "
            					. $transactionResponse->transaction_id);

            $auth_code = $transactionResponse->authorization_code;
            
            if($session->user_data->IsUserDemo()==true)
            {
            	$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
            			"BillCreditCard User Is Demo. VOIDING TRANSACTION!!!! [Trans ID: " . $transactionResponse->transaction_id . "]");
            	$this->VoidMakeOrderTransaction($transactionResponse->transaction_id,$session->user_data->users_authorize_net_customer_profile_id);
            }
            
            return $transactionResponse->transaction_id;
        }
        $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"BillCreditCard Customer Profile: " . $transaction->customerProfileId .
                           " Payment Profile: " . $transaction->customerPaymentProfileId . " Amount: {$amount} Reason: " . $response->getErrorMessage());
        
        $error_object->ErrorCodeMajor = ERROR_MAJOR_BILLING;
        $error_object->ErrorCodeMinor = $response->getMessageCode();
        $error_object->DisplayText=ERROR_MAJOR_BILLING_TEXT;
        $error_object->LongText = $response->getMessageText();
	
        return NULL;
    }
    
    function ReverseTransaction(Order $order,OrderCreditCard $card,$action,&$response_id,$amount,$customer_profile_id,&$error_object)
    {
    	global $session;
    	$response_id=-1;
    	$transaction = new AuthorizeNetTransaction;
    	$transaction->transId = $card->trans_id;
    	if(strcmp($action,"Refund")==0)
    	{
    		$transaction->amount=$amount;
    		$transaction->customerPaymentProfileId=$card->profile_id;
    		$transaction->customerProfileId=$customer_profile_id;
    	}
    	
    	$request=new AuthorizeNetCIM(AUTH_NET_LOGIN,AUTH_NET_TRANS_KEY);
    	$response = $request->createCustomerProfileTransaction($action, $transaction);
    	if($response->isOk())
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
    				"ReverseTransaction Success for Action {$action} TransID: {$transaction->transId}");
    		$response_id = $response->authorization_code;
    		return true;
    	}
    	$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    			"ReverseTransaction Error for Action: {$action} TransID: {$transaction->transId} Reason: " . $response->getErrorMessage());
    	if(!is_null($error_object))
    	{
    		$error_object->ErrorCodeMajor = ERROR_MAJOR_BILLING;
    		$error_object->ErrorCodeMinor = $response->getMessageCode();
    		$error_object->DisplayText=ERROR_MAJOR_BILLING_TEXT;
    		$error_object->LongText = $response->getMessageText();
    	}
    	return false;
    	 
    }
    
    function VoidOrRefundTransaction(OrderCreditCard $card,Order $order,&$response_id,$amount,$customer_profile_id,&$error_object)
    {
    	if($order->IsDemoOrder()==true)
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_INFO,
    				"VoidOrRefundTransaction Order [" . $order->orders_id . "] was Demo Order, Not reversing");
    		return true;
    	}
    	
    	if($order->IsPayAtLocation())
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_INFO,
    				"VoidOrRefundTransaction Order [" . $order->orders_id . "] was Pay At Location, Not reversing");
    		return true;
    	}
    	$order_cost_cmp = bccomp($order->orders_total_cost,"0.00");
    	if($order_cost_cmp==1)
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"VoidOrRefundTransaction: Order [" . $order->orders_id . "] total is $0.00 Not Reversing");
    		return true;
    	}
    	if( (is_null($card->trans_id) ) || (strlen($card->trans_id)==0))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"VoidOrRefundTransaction: Order [" . $order->orders_id . "] No Transaction Id. Not Reversing");
    		return true;
    	}
    		
    	if($this->ReverseTransaction($order,$card,"Void",$response_id,$amount,$customer_profile_id,$error_object)==true)
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
    				"VoidOrRefundTransaction Order [" . $order->orders_id . "] Success for Action: Void Input TransID: {$card->trans_id} ResponseID: {$response_id}");
    		return true;
    	}
    	else 
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"VoidOrRefundTransaction Order [" . $order->orders_id . "] Error for Action: Void TransID: {$card->trans_id} Attempting Refund");
    		return $this->ReverseTransaction($order,$card,"Refund",$response_id,$amount,$customer_profile_id,$error_object);
    	}
    }
    
    // This can never be used with an ORDER because it assumes the user is the current user (for user=demo etc)
    function VoidMakeOrderTransaction($trans_id,$customer_profile_id)
    {
    	global $session;
    	$response_id; // Dont care as we are not storing a record
    	// Can only Void whole thing so send in 0.00 	
    	if( (is_null($trans_id) ) || (strlen($trans_id)==0))
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_INFO,
    				"VoidTransaction Trans ID is Zero -- not Voiding. Was this a demo account?");
    		return true;
    	}
    	
    	// Since this is the "MakeOrder" Voider, it must be for the current user (cannot make orders on a 3rd party)
    	if($session->user_data->IsUserPayAtLocation())
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_INFO,
    				"VoidTransaction Called for pay at location user");
    		return true;
    	}
    	
    	$transaction = new AuthorizeNetTransaction;
    	$transaction->transId = $trans_id;
    	$transaction->customerProfileId=$customer_profile_id;
    	
    	$request=new AuthorizeNetCIM(AUTH_NET_LOGIN,AUTH_NET_TRANS_KEY);
    	$response = $request->createCustomerProfileTransaction("Void", $transaction);
    	if($response->isOk())
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
    				"VoidTransaction Success TransID: {$transaction->transId}");
    		return true;
    	}
    	$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    			"VoidTransaction Error for Action: {$action} TransID: {$transaction->transId} Reason: " . $response->getErrorMessage());
    	if(!is_null($error_object))
    	{
    		$error_object->ErrorCodeMajor = ERROR_MAJOR_BILLING;
    		$error_object->ErrorCodeMinor = $response->getMessageCode();
    		$error_object->DisplayText=ERROR_MAJOR_BILLING_TEXT;
    		$error_object->LongText = $response->getMessageText();
    	}
    	return false;
    	
    }
    
    function MakeOrder($item_data,$total_items_cost,$highest_item_cost,$highest_cost_item_index,
    					&$credit_data,&$order_id,&$time_needed,&$error_object,
    					$car_data, Location $location_info,$client_type,$arrive_mode,$tip)
    {
    	// ERROR FIX
        global $session;
        global $mailer;
        global $pdf_helper;
        global $order_database;
        
        $order_id=$this->GetNextOrderID();
        $order_tag = $this->GetNextGlobalOrderTag(); // Used in ip_orders only. Used like a "super" incarnation to make sure OM can always get modified orders
        
        if (!mysqli_query($this->getConnection(),"START TRANSACTION")) 
        {
            
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            			"MakeOrder: START_TRANSACTION failed");
            return false;
        }        
        $unix_time_now = time();
        $this->GetTimeAsUTCAndLocal($unix_time_now,$gm_date_time_now,$local_date_time_now);
        
        $unix_time_needed = strtotime("+" . $session->user_data->user_time_to_location . " minutes",$unix_time_now);
        $this->GetTimeAsUTCAndLocal($unix_time_needed, $gm_date_time_needed, $local_date_time_needed);
        
        $local_time_needed = date( 'g:i a',$unix_time_needed);
        $time_needed=$local_time_needed;
        
        if($location_info->LocationOpenMode==DB_LOCATION_OPEN_MODE_OBEY_HOURS)
        {
        	if($this->IsStoreOpen($location_info,$unix_time_needed)!=TRUE)
        	{
        		$err_str = "Sorry, The Store is not currently open, please check the opening times and try again later.";
        		$this->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,LOG_CAT_STORE_CLOSED,$err_str);
        		$error_object->ErrorCodeMajor = ERROR_MAJOR_LOCATION;
        		$error_object->ErrorCodeMinor = ERROR_MINOR_LOCATION_CLOSED;
        		$error_object->DisplayText=$err_str;
        		$error_object->LongText = $err_str;
        		return false;
        	}
        }
        else if($location_info->LocationOpenMode==DB_LOCATION_OPEN_MODE_FORCE_CLOSED)
        {
        	$err_str = "Sorry, The Store is not currently open, please check the opening times and try again later.";
        	$this->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,LOG_CAT_STORE_CLOSED,$err_str);
        	 
        	$error_object->ErrorCodeMajor = ERROR_MAJOR_LOCATION;
        	$error_object->ErrorCodeMinor = ERROR_MINOR_LOCATION_CLOSED;
        	$error_object->DisplayText=$err_str;
        	$error_object->LongText = $err_str;
        	return false;
        }
        
        
        $tip_amount="0.00";
        if(!is_null($tip))
        {
        	$tip_amount = $tip->CalculateDollarAmount($total_items_cost);
        }
        
        // Check for free drinks (Session is decremented later)
        if($session->DoesUserHaveFreeDrinks(1))
        {
        	if($this->DecrementUserFreeDrinksCount($_SESSION['user_id'], 1))
        	{
        		;
        		if(bccomp($total_items_cost,FREE_DRINK_ALT_AMOUNT)==-1)
        		{
        			//order item total is less than min
        			$total_order_cost ="0.00";
        			$order_discount = $total_items_cost;
        		}
        		else
        		{
        			$total_order_cost = bcsub($total_items_cost, FREE_DRINK_ALT_AMOUNT);
        			$order_discount = FREE_DRINK_ALT_AMOUNT;
        		}
        	}
        	else
        	{
        		// Log error later
        	}
        }
        else
        {
        	$total_order_cost = $total_items_cost;
        	$order_discount = "0.00";
        }
        // Bill card and insert a credit card record
        $input_credit_card=NULL;
        
        $order_cost_cmp = bccomp($total_order_cost,"0.00");
        if($order_cost_cmp==1)
        {
        	// Only bill if > 0.00 && need a credit card
        	
        	if($session->user_data->IsUserPayAtLocation())
        	{
        		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"MakeOrder: Pay At Location Order: {$order_id}");
        	}
        	else
        	{
        		$input_credit_card = $this->GetUserDefaultCreditCard($_SESSION['user_id']);
        		if($input_credit_card==NULL)
        		{
        			mysqli_query($this->getConnection(),"ROLLBACK");
        			$err_str = "Sorry, No credit card could be found. Order: {$order_id}";
        			$this->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,LOG_CAT_STORE_CLOSED,$err_str);
        		 
        			$error_object->ErrorCodeMajor = ERROR_MAJOR_BILLING;
        			$error_object->ErrorCodeMinor = ERROR_MINOR_BILLING_NO_CARD;
        			$error_object->DisplayText=$err_str;
        			$error_object->LongText = $err_str;
        			return false;
        		}
        		
        		// BILL BILL BILL
        		$auth_code="";
        		$transaction_id = $this->BillCreditCard($input_credit_card,$total_items_cost,$auth_code,$error_object);
        		if($transaction_id===NULL)
        		{
        			mysqli_query($this->getConnection(),"ROLLBACK");
         	  		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
         	  						"MakeOrder: BillCredit Card failed. Order: {$order_id}");
         	  		return false;
        		}
        	}
        }
        else if ($order_cost_cmp==0)
        {
        	$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_INFO,
        			"MakeOrder: Entire Order:{$order_id} FREE: Items Cost: {$total_items_cost} -- Pending Other failures. Check final log");
        	$auth_code="";
        	$transaction_id="";
        	
        }
        else if($order_cost_cmp==-1)
        {
        	mysqli_query($this->getConnection(),"ROLLBACK");
        	$err_str = "Somehow Order Total became negative. Order ID: {$order_id}";
        	$this->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,LOG_CAT_INTERNAL,$err_str);
        	 
        	
        	$error_object->ErrorCodeMajor = ERROR_MINOR_BILLING;
        	$error_object->ErrorCodeMinor = ERROR_MINOR_BILLING_NEGATIVE_COST;
        	$error_object->DisplayText=$err_str;
        	$error_object->LongText = $err_str;
        	return false;
        }
        
        $total_order_cost = bcadd($total_order_cost,$tip_amount);
        
        // Insert the main order record.
        $stmt = mysqli_stmt_init($this->getConnection());
        
        $car_make_model_color = NULL;
        if($car_data!=NULL)
        {
        	$car_make_model_color=$car_data->GenerateLongDisplayString();
        }
        else
        {
        	$car_data=NONE_TEXT;
        }
        
       // Bind Param cannot take a literal
        $disposition=ORDER_RECEIVED;
               
        $orders_table = $this->GetOrdersTableName(ORDER_TYPE_IN_PROGRESS,$location_info->LocationID);
        
        mysqli_stmt_prepare($stmt,"INSERT INTO {$orders_table} VALUES (?,?,?,?,?,NULL,?,?,?,?,?,NULL,?,0,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NULL,NULL)");
        
        $order_tag_str = (string)$order_tag;
        
        $order_taxable = $location_info->IsOrderTaxable($arrive_mode); // Maybe certain locations have some non-taxable based on this or even their state 
        
        															   // Especially for no sales-tax states! CA also in theory no tax for take out coffee.
        
        
        $taxable_amount = bcsub($total_items_cost,$order_discount);
        
        $convenience_fee = $location_info->GetConvenienceFeeForPayMethod($session->user_data->user_pay_method);
        
        $taxable_amount = bcadd($taxable_amount,$convenience_fee,10);
        $tax  = $location_info->CalculateSalesTax($taxable_amount,$arrive_mode);
        
        $total_order_cost = bcadd($total_order_cost,$convenience_fee,10);
        $total_order_cost = bcadd($total_order_cost,$tax);
        
        mysqli_stmt_bind_param($stmt,'iissisidsissssiiisssisddddd',
        					   $order_id,
                               $_SESSION['user_id'],
                               session_id(),
                               $gm_date_time_now,
                               $session->user_data->user_time_to_location,
                               $gm_date_time_needed,
                               $disposition,
                               $total_order_cost,
                               $_SESSION['user_email'],
                               $session->user_data->user_location_id,
        					   $_SESSION['user_name'],
        					   $car_make_model_color,
        					   $session->user_data->user_tag,
        					   $session->user_data->users_authorize_net_customer_profile_id,
        					   $session->user_data->user_is_demo,
        					   $client_type,
        					   $arrive_mode,
        					   $total_items_cost,
        					   $order_discount,
        					   $tip_amount,
        					   $session->user_data->user_pay_method,
        					   $order_tag_str,
        					   $order_taxable,
        					   $taxable_amount,
        					   $tax,
        					   $location_info->LocationSalesTaxRate,
        					   $location_info->LocationConvenienceFee
        		               ); // Created timestamp - now()
        
        if(!mysqli_stmt_execute($stmt))
		{
			mysqli_query($this->getConnection(),"ROLLBACK");
			$err_str = "MakeOrder: Could not insert Order data (" . mysqli_stmt_error($stmt) . ")";
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,$err_str);
            
            $this->VoidMakeOrderTransaction($transaction_id,$session->user_data->users_authorize_net_customer_profile_id);
            $error_object->ErrorCodeMajor = ERROR_MAJOR_DATABASE;
        	$error_object->ErrorCodeMinor = ERROR_MINOR_DATABASE_GENERAL;
        	$error_object->DisplayText=ERROR_DATABASE_GENERAL_TEXT;
        	$error_object->LongText = $err_str;
	
            return false;
        }
        mysqli_stmt_close($stmt);
        
        if($session->user_data->IsUserPayAtLocation())
        {
        	$credit_data=NULL;
        }
        else
        {
        	$credit_data->profile_id=$input_credit_card['user_credit_card_payment_profile_id'];
        	$credit_data->provider_id=1;
        	$credit_data->auth_code=$auth_code;
        	$credit_data->transaction_id=$transaction_id;
        	$credit_data->refund_auth_code="";
        
        	$credit_data->card_descr = $input_credit_card['user_credit_card_descr'];
        
        	$credit_data->card_type = $input_credit_card['user_credit_card_card_type'];
        	$credit_data->card_last4 = $input_credit_card['user_credit_card_last4'];
        
        	$stmt = mysqli_stmt_init($this->getConnection());
        
        	mysqli_stmt_prepare($stmt,"INSERT INTO orders_credit_cards VALUES ('0',?,?,?,?,?,?,?,?,?,NULL,NULL)");
        
        	//print ("T:" . $credit_data->card_descr);
        
        	mysqli_stmt_bind_param($stmt,'iiisssiss',
            	                $order_id,
                	            $credit_data->profile_id,
                    	        $credit_data->provider_id,
        						$credit_data->transaction_id,
                            	$credit_data->auth_code,
                               	$credit_data->refund_auth_code,
                               	$credit_data->card_type,
                               	$credit_data->card_last4,
                               	$credit_data->card_descr);
        
        	if(!mysqli_stmt_execute($stmt))
			{
				mysqli_query($this->getConnection(),"ROLLBACK");
				$err_str = "MakeOrder: Could not insert credit card data (" . mysqli_stmt_error($stmt) . ")";
            	$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,$err_str);
            
            	if($session->user_data->IsUserPayAtLocation()!=true)
            	{
            		$this->VoidMakeOrderTransaction($transaction_id,$session->user_data->users_authorize_net_customer_profile_id);
            	}
            	$error_object->ErrorCodeMajor = ERROR_MAJOR_DATABASE;
        		$error_object->ErrorCodeMinor = ERROR_MINOR_DATABASE_GENERAL;
        		$error_object->DisplayText=ERROR_DATABASE_GENERAL_TEXT;
        		$error_object->LongText = $err_str;
            	return false;
        	}
        	mysqli_stmt_close($stmt);
        
        	$credit_data->card_id=mysqli_insert_id($this->getConnection());
        }
        // Now try to preserve the drink info, and syrups since they are 1->M

        //$pdf_filenames = array();
        //$drink_options = array();
        
        //$pdf_mainfilename= ORDER_PDF_PATH_PREFIX . ORDER_PDF_FILE_PREFIX . $order_id . ".pdf";
        
        $index=0;
        
        //var_dump($drink_data);
        if($item_data!=NULL)
        {
	        foreach($item_data as $item)
	        {
	            if(!$this->DoAddOrderItem($item,$order_id))
	            {
	            	mysqli_query($this->getConnection(),"ROLLBACK");
	            	$err_str = "MakeOrder Could not Insert Item (Order: {$order_id}, DrinkID: " . $item->user_drink_id .")";
	                $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
	                                   $err_str);
	                if($session->user_data->IsUserPayAtLocation()!=true)
	                {
	                	$this->VoidMakeOrderTransaction($transaction_id,$session->user_data->users_authorize_net_customer_profile_id);
	                }
	                $error_object->ErrorCodeMajor = ERROR_MAJOR_DATABASE;
	        		$error_object->ErrorCodeMinor = ERROR_MINOR_DATABASE_GENERAL;
	        		$error_object->DisplayText=ERROR_DATABASE_GENERAL_TEXT;
	        		$error_object->LongText = $err_str;
	                return false;
	            }
	           
	            $index++;
	        }
        }
        
        
        // TODO FIX when we Fix PDFs and emails for Orders.
        if(mysqli_query($this->getConnection(),"COMMIT")===FALSE)
        {
        	//mysqli_query($this->getConnection(),"ROLLBACK");
        	$err_str = "COMMIT FAILED (Order: {$order_id})";
        	$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        			$err_str);
        	$error_object->ErrorCodeMajor = ERROR_MAJOR_DATABASE;
        	$error_object->ErrorCodeMinor = ERROR_MINOR_DATABASE_GENERAL;
        	$error_object->DisplayText=ERROR_DATABASE_GENERAL_TEXT;
        	$error_object->LongText=$err_str;
        	
        	if($session->user_data->IsUserPayAtLocation()!=true)
        	{
        		$this->VoidMakeOrderTransaction($transaction_id,$session->user_data->users_authorize_net_customer_profile_id);
        	}
        	return false;
        }
        
        
        
        $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
        		"MakeOrder: Success for OrderID: {$order_id}, Total Cost: " . number_format($total_order_cost) .
        					"Total Items Cost: " .  number_format($total_items_cost,2).
        					"Total Discount: " . number_format($order_discount) );
       
        
        $the_order = $order_database->GetOrderByOrderIDAndTypeForLocation(ORDER_TYPE_IN_PROGRESS,$order_id,$location_info->LocationID);
        
        if(!is_null($the_order))
        {
        	$mailer->SendUserOrderSuccessEmailNoPDF($session->user_data,
            	                           $order_id,
        			 					   number_format($total_order_cost,2),
        								   number_format($tip_amount,2),
        		 						   $credit_data,
        								   $local_time_needed,
        								   $item_data,
        								   $car_make_model_color,
        							   	$location_info,$the_order);
        
        	$mailer->SendStoreOrderEmailNoPDF($session->user_data,
        								$order_id,
        								number_format($total_order_cost,2),
        								number_format($total_items_cost,2),
        								number_format($order_discount,2),
        							    number_format($tip_amount,2),
        								$credit_card,
        								$local_time_needed,
                         	      		$item_data,
        								$car_make_model_color, 
        								$location_info,
        								$arrive_mode,
        								$the_order);
        }

        $order_cost_cmp = bccomp($total_order_cost,"0.00");
        if ($order_cost_cmp==0)
        {
        	$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_INFO,
        			"MakeOrder: Entire Order FREE: OrderID: {$order_id} Items Cost: {$total_items_cost} Confirmed");
        	
        }
        
        if($session->DoesUserHaveFreeDrinks(1))
        {
        	$session->ReduceUsersFreeDrinks(1);
        }
        return true;

    }

   function DoAddOrderItem(UserDrink $item,$order_id)
   {

   		global $session;
        
        $stmt=mysqli_stmt_init($this->getConnection());        
        mysqli_stmt_prepare($stmt,"INSERT INTO orders_user_items VALUES ('0',?,?,?,?,?,?,?,?,?,NULL,NULL)");
   
        $item_description = $item->MakeDrinkTextNoCost();
        // s means the database expects a string, d for decimal
        if(!mysqli_stmt_bind_param($stmt,"iiiidsssi",
                                   $order_id,
                                   $_SESSION['user_id'],
        						   $item->drink_type_id,
        						   $item->menu_id,
        						   $item->user_drink_cost,
        						   $item->user_drink_extra,
        						   $item->user_drink_name,
        						   $item_description,
        						   $item->item_type_class
        		
                                   ))
        {
        	$err_str = "DoAddOrderItem: Bind Failed User Item: {$item->user_drink_id} Order ID: {$order_id} (" . mysqli_stmt_error($stmt) . ")";
        	
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            		$err_str);
            //print $err_str;
            mysqli_stmt_close($stmt);
			return false;
		}
        
        $query_result = mysqli_stmt_execute($stmt);
        if($query_result!=TRUE)
        {
        	$err_str = "DoAddOrderItem: Execute Failed User Item: {$item->user_drink_id} Order ID: {$order_id} (" . mysqli_stmt_error($stmt) . ")";
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            			$err_str);
            //print $err_str;
            mysqli_stmt_close($stmt);
			return false;
        }
        
        mysqli_stmt_close($stmt);
        

        $order_item_id = mysqli_insert_id($this->getConnection());
        
        if($this->DoAddOrderItemOptions($item,$order_item_id,$order_id)!=true)
        {
        	$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        			"DoAddOrderItem: Execute DoAddOrderItemOptions Failed User Item: {$item->user_drink_id} Order ID: {$order_id}");
        	return false;
        }
        else
        {
        	return true;
        }
   }
   
   function DoAddOrderItemOptions(UserDrink $item,$order_item_id,$order_id)
   {
   	//print (" DoAddOrderItemOptions Order Item ID{$order_item_id} Order ID {$order_id}");
  		$query_string = "INSERT INTO orders_user_items_options VALUES ('0',?,?,?,?,?,NULL,NULL)";
  
   		for( $option_index = 0;$option_index< count($item->user_drink_options); $option_index++)
   		{
   			
   			$stmt=mysqli_stmt_init($this->getConnection());
   			
   			mysqli_stmt_prepare($stmt,$query_string);
   			
   			if(!mysqli_stmt_bind_param($stmt,"iiiid",
   									   $order_id,
   									   $order_item_id,
   									   $item->user_drink_options[$option_index]->drink_types_option_id,
   									   $item->user_drink_options[$option_index]->user_drink_option_count,
    								   $item->user_drink_options[$option_index]->cost_per
   									))
   			{
   				$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
   						"DoAddOrderItemOptions: Bind Failed {$item->user_drink_id} Order ID: {$order_id} Option Index: {$option_index} (" . 
   						mysqli_stmt_error($stmt) . ")");
   				return false;
   			}
   			$query_result = mysqli_stmt_execute($stmt);
   			if($query_result!=TRUE)
   			{
   				mysqli_stmt_close($stmt);
   				$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
   						"DoAddOrderItemOptions: Execute Failed {$item->user_drink_id} Order ID: {$order_id} Option Index: {$option_index} (" 
   						. mysqli_stmt_error($stmt) . ")");
   				return false;
   			}
   			mysqli_stmt_close($stmt);
   			
   		}
   		return true;
    }
    
    function GetItemsSummary($item_id_list,$menu_id,$check_menu,&$total_items_cost,&$highest_item_cost,&$highest_cost_item_index)
    {
    
    	// ERROR FIX
    	global $session;
    	
    	$item_id_list_safe = mysqli_real_escape_string($this->getConnection(),$item_id_list);
    	$item_id_array = explode(",",$item_id_list_safe);
    	$total_items_cost=0.00;
    	$highest_item_cost=0.00;
    	$highest_cost_item_index=0;
    	
    	if(count($item_id_array)==0)
    	{
    		return NULL;
    	}
    	
    	$item_data = array();
    	for($index=0;$index<count($item_id_array);$index++)
    	{
    		if($item_id_array[$index]==0)
    		{
    			continue; // Can happen if $drink_id_list is null or has some non-numeric junk in it.
    		}
    		
    		
    		if($item_id_array[$index]==NULL)
    		{
    			// ACK
    			
    			continue;
    		}
    		
    		$item = $this->GetUserItem($item_id_array[$index]);
    		if($item!=NULL)
    		{
    			if( ($item->menu_id==$menu_id) || ($check_menu==false))
    			{
    				$item_data[]=$item;
    				$total_items_cost = bcadd($total_items_cost,$item->user_drink_cost,10);
    			}
    			else
    			{
    				$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    						"GetItemsSummary: Item ID: " . $item_id_array[$index]
    						. "Not added to order because Item Menu ID of: [" . $item->menu_id . "]"
    						. "Does not match Required menu ID of: [" . $menu_id . "]");
    			}
    		}
    	}
    		
    	//var_dump($item_data);
    	
    	if( (count($item_data)==0) || ($item_data[0]==NULL))
    	{
    		return NULL;
    	}
    	
    	//Sort it
    	
    	
    	usort($item_data, "compare_user_item_by_type");
    	 
    	
    	// Then find the highest cost indexes .. cannot be found until sorted
    	for($index=0;$index<count($item_data);$index++)
    	{
    		
    		if($item_data[$index]->calc_cost > $highest_item_cost)
    		{
    			$highest_items_cost =  $item_data[$index]->cost;
    			$highest_cost_item_index=$index;
    		}
    	}
    	
    	return $item_data;
    }
    
    
    function GetUserDrink($user_drink_id)
    {
    	$stmt = $this->OpenGetUserDrinksForID($user_drink_id);
    	if($stmt!=null)
    	{
    		$drink = $this->GetNextUserDrink($stmt);
    		return $drink;
    	}
    	return NULL;
    }
    
    function GetUserItem($user_item_id)
    {
    	$stmt = $this->OpenGetUserItemsForID($user_item_id);
    	if($stmt!=null)
    	{
    		$item = $this->GetNextUserItem($stmt);
    		return $item;
    	}
    	return NULL;
    }
    
    function GetDrinkOptionsAndComputeCost($user_drink)
    {
    	global $session;
    	
    	$stmt = mysqli_stmt_init($this->getConnection());
    	//print()
    	mysqli_stmt_prepare($stmt,"SELECT user_drink_options.udo_id, user_drink_options.udo_user_drink_id, user_drink_options.udo_drink_type_option_id, "
    						. " user_drink_options.udo_option_count, drink_types_options.dto_price, drink_types_options.dto_charge_each_count, drink_options.do_option_name, "
    						. " drink_options.do_id, drink_option_groups.dog_id, drink_options.do_sort_order "
    						. " FROM user_drink_options, drink_types_options, drink_options, drink_option_groups "
    						. " WHERE "
    						. " user_drink_options.udo_user_drink_id=? AND "
    						. " drink_types_options.dto_id = user_drink_options.udo_drink_type_option_id AND "
    						. " drink_options.do_id = drink_types_options.dto_drink_option_id AND "
    						. " drink_option_groups.dog_id = drink_options.do_option_group AND "
    						. " drink_options.is_active=1 AND drink_option_groups.is_active=1 "
    						. " ORDER BY drink_option_groups.dog_sortorder ASC, drink_options.do_sort_order ASC");
    	
    	mysqli_stmt_bind_param($stmt,'i',$user_drink->user_drink_id);
    	
    	
    	if(mysqli_stmt_execute($stmt))
    	{
    		mysqli_stmt_store_result($stmt);
    	}
    	else
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"GetDrinkOptionsAndComputeCost: Could not store result from SELECT (" . mysqli_stmt_error($stmt) . ")");
    		return false;
    	}
    	
    	while($stmt!=null)
    	{
    		$user_drink_option = new UserDrinkOption();
    	
    		$charge_per_count=1;
    		
    		if(!mysqli_stmt_bind_result($stmt,$user_drink_option->user_drink_option_id,
    										  $user_drink_option->user_drink_id,
    										  $user_drink_option->drink_types_option_id,
    										  $user_drink_option->user_drink_option_count,
    										  $user_drink_option->cost_per,
    										  $charge_per_count,
    										  $user_drink_option->option_name,
    										  $user_drink_option->drink_option_id,
    										  $user_drink_option->drink_option_group_id,
    										  $user_drink_option->sort_order))
    		{
    			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"GetDrinkOptionsAndComputeCost: Could not bind result (" . mysqli_stmt_error($stmt) . ")");
    			mysqli_stmt_close($stmt);
    			return false;
    		}
    		$result = mysqli_stmt_fetch($stmt);
    		if( $result===NULL)
    		{
    			// END OF the rows
    			return true;
    		}
    		else if($result===false)
    		{
    			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"GetDrinkOptionsAndComputeCost: Fetch Failed (" . mysqli_stmt_error($stmt) . ")");
    			mysqli_stmt_close($stmt);
    			return false;
    		}
    		else
    		{
    			// Success
    			// Update total cost of the option
    			

    			if($charge_per_count==1)
    			{
    				$user_drink_option->calc_cost =bcmul($user_drink_option->cost_per,$user_drink_option->user_drink_option_count,10);
    			}
    			else
    			{
    				$user_drink_option->calc_cost =$user_drink_option->cost_per; // Only charge once no matter how many they choose.
    			}
    			//print("Compute Cost: OPTCost: {$user_drink_option->calc_cost}  DOID: {$user_drink_option->user_drink_option_id} DTOID: {$user_drink_option->drink_types_option_id} ".
    			//		" Cost Per: $user_drink_option->cost_per Count: $user_drink_option->user_drink_option_count");
    			$user_drink->AddDrinkOption($user_drink_option);
    		}
    	}
    	return true;
    	
    }
    
    function OpenGetUserItemsForID($user_item_id)
    {
    	return $this->OpenGetUserItemsForIDAndType($user_item_id,DB_USER_ITEM_CLASS_ALL);
    }
    
    function OpenGetUserDrinksForID($user_drink_id)
    {
    	return $this->OpenGetUserItemsForIDAndType($user_drink_id,DB_USER_ITEM_CLASS_DRINK);
    }
    
    function OpenGetUserItemsForIDAndType($user_item_id, $item_type_class)
    {
    	global $session;
    
    	$stmt = mysqli_stmt_init($this->getConnection());
    
    	// NOTE: SAME QUERY USED IN OpenGetUserItems
    	$query_string = UserDrink::$GET_USER_DRINK_BASE_QUERY . " WHERE user_drinks.user_id=? AND user_drinks.user_drink_id=? ";
    	if($item_type_class!=DB_USER_ITEM_CLASS_ALL)
    	{
    		$query_string .=" AND user_drinks.item_type_class={$item_type_class} ";
    	}
    	$query_string .=" AND drink_types.drink_type_id=user_drinks.drink_type_id ";
    	 
    	mysqli_stmt_prepare($stmt,$query_string);
    
    	mysqli_stmt_bind_param($stmt,'ii',$_SESSION['user_id'],$user_item_id);
    
    	if(mysqli_stmt_execute($stmt))
    	{
    		mysqli_stmt_store_result($stmt);
    	}
    	else
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"OpenGetUserItemsForIDAndType: Could not store result from SELECT Item ID: {$user_item_id} (" . mysqli_stmt_error($stmt) . ")");
    		return NULL;
    	}
    
    	return $stmt;
    }
    
    function OpenGetUserItems()
    {
        global $session;
        
        $stmt = mysqli_stmt_init($this->getConnection());
        
        $query_string = UserDrink::$GET_USER_DRINK_BASE_QUERY . " WHERE user_drinks.user_id=? AND drink_types.drink_type_id=user_drinks.drink_type_id";
        
        // NOTE: SAME QUERY USED IN OpenGetUserItemsForIDAndType
        mysqli_stmt_prepare($stmt,$query_string);
        
                
        mysqli_stmt_bind_param($stmt,'i',$_SESSION['user_id']);
        
        if(mysqli_stmt_execute($stmt))
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            		"OpenGetUserItems: Could not store result from SELECT (" . mysqli_stmt_error($stmt) . ")");
            return NULL;
        }
        
        return $stmt;           
    }
    
    function GetNextUserDrink($stmt)
    { 
    	return $this->GetNextUserItem($stmt);
    }
   
    function GetNextUserItem($stmt)
    {
   
    	//$this->user_drink_cost=0.00;
    	
    	
        global $session;
        $drink = new UserDrink();
        if(!mysqli_stmt_bind_result($stmt,$drink->user_drink_id, $drink->user_drink_name, $drink->user_drink_user_id, $drink->drink_type_id,
        		$drink->drink_include_default, $drink->user_drink_extra,$drink->item_type_class, $drink->menu_id, $drink->drink_type_long_descr ))
        {
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"GetNextUserDrink: Could not bind result (" . mysqli_stmt_error($stmt) . ")");
            return NULL;
        }
        
        if( mysqli_stmt_fetch($stmt)==true)
        {
            
        	if($this->GetDrinkOptionsAndComputeCost($drink)==true)
        	{
        		return $drink;
        	}
            else
            {
            	return NULL;
            }
        }
        
        return NULL;
     }
    
     function CloseGetUserItems($stmt)
     {
     	mysqli_stmt_close($stmt);
     }
     
    function CloseGetUserDrinks($stmt)
    {
    	$this->CloseGetUserItems($stmt); 
    }
    
    function UpdateCreditCardInfo($card,$use_for_pay,$error_object)
    {
        global $session;
        global $database;
        
        $credit_card = $this->GetUserDefaultCreditCard($_SESSION['user_id']);
        
        mysqli_query($this->getConnection(),"START TRANSACTION");
        
        if($credit_card==NULL)
        {
            // No Old card to worry about
            $result = $this->InsertCreditCard($card,$error_object);
        }
        else
        {
            $result = $this->UpdateCreditCard($card,$credit_card,$error_object);
        }
        
        if($result==true)
        {
        	if($use_for_pay==true)
        	{
        		$result=$this->UpdatePaymentMethod($_SESSION['user_id'], LocationPayMethod::$LOCATION_PAY_METHOD_IN_APP, $error_object);
        	}
        }
        if($result==true)
        {
        	mysqli_query($this->getConnection(),"COMMIT");
        }
        else
        {
        	mysqli_query($this->getConnection(),"ROLLBACK");
        }
        return $result;
    }
    
    function InsertCreditCard(CreditCard $card,$error_object)
    {
        // First delete any existing credit cards.
        global $session;
        global $mailer;
        
        $last_four = $card->GetLastFour();
        $card_descr = $card->getCardTypeString();
        
        $card_exp = $card->GetExpirationForAuthNet();
        
        

        // Send it first because we want to tell the user someone tried to change it, so we send this even if a failure happens.
        //$mailer->SendCreditCardInfoChanged($_SESSION['user_email'], $_SESSION['user_name'], $card_descr, $last_four);
        $error_object->ErrorCodeMajor = ERROR_MAJOR_INTERNAL;
        $error_object->ErrorCodeMinor = ERROR_MINOR_STATE_NOT_FOUND;
        $error_object->DisplayText="An Error Occurred trying to update your credit card information, Please try again, Or contact customer support if this persists";
        $error_object->LongText=$error_object->DisplayText;

        
        $stmt=mysqli_stmt_init($this->getConnection());
        
        if( ! $session->user_data->IsBillingProfilePopulated())
        {
        	$err_str = "InsertCreditCard Error: No CustomerProfileID for user " . $_SESSION['user_id'] . "(" . mysqli_stmt_error($stmt) . ")";
        	
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            		 $err_str);
            $error_object->LongText .= " - " . $err_str;
            return false;
        }
        
        $paymentProfile = new AuthorizeNetPaymentProfile;
        $paymentProfile->customerType = "individual";
        
        // If we submit it, it is checked. 
        if($card->IsZipValidFormat())
        {
        	$paymentProfile->billTo->zip=$card->card_billing_zip;
        }
        $paymentProfile->payment->creditCard->cardNumber=$card->card_number;
        $paymentProfile->payment->creditCard->expirationDate= $card_exp ;      
        
        $request=new AuthorizeNetCIM(AUTH_NET_LOGIN,AUTH_NET_TRANS_KEY);
        
        $auth_response = $request->createCustomerPaymentProfile($session->user_data->users_authorize_net_customer_profile_id,
        						 $paymentProfile,AUTH_NET_VALIDATION_MODE);
        
        if(!$auth_response->isOk())
        {
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"InsertCreditCard Error: Could not add Payment Profile " . $_SESSION['user_id'] .
                            "(" . $auth_response->getErrorMessage() . ")" );
            
            $error_object->ErrorCodeMajor = ERROR_MAJOR_BILLING;
            $error_object->ErrorCodeMinor = $auth_response->getMessageCode();
            $error_object->DisplayText=ERROR_MAJOR_BILLING_TEXT;
            $error_object->LongText = $auth_response->getMessageText();
            return false;

        }
        
        $stmt=mysqli_stmt_init($this->getConnection());    
        mysqli_stmt_prepare($stmt,"INSERT INTO user_credit_cards VALUES ('0',?,'XXX',?,?,?,'1',?,?,NULL,NULL)");
        
        if(!mysqli_stmt_bind_param($stmt,"isssii",$_SESSION['user_id'],$last_four,$card_descr,$card_exp,$card->card_type,
        		$auth_response->getPaymentProfileId()))
        {
			
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"INSERT New card Bind Failed (" . mysqli_stmt_error($stmt) . ")" );
            
			return false;
		}
        
        $query_result = mysqli_stmt_execute($stmt);
        if(!$query_result)
        {
        	$err_str = "INSERT New card Execute Failed -- Attempting to delete PaymentProfile [" 
        				. $auth_response->getPaymentProfileId() ."] to cleanup (" . mysqli_stmt_error($stmt) . ")";
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            		 $err_str);
            $error_object->LongText .= " - " . $err_str;
            
            // Try to delete the payment profile at least ...
            $request=new AuthorizeNetCIM(AUTH_NET_LOGIN,AUTH_NET_TRANS_KEY);
        
       		$auth_response = $request->deleteCustomerPaymentProfile($session->user_data->users_authorize_net_customer_profile_id,
        														$auth_response->getPaymentProfileId());
			return false;
        }
        
        $card_id=mysqli_insert_id($this->getConnection());
        $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,"Updated Credit Card Info ID: " . $card_id);
        
        mysqli_stmt_close($stmt);
        
        return true;
        
    }
    
    function UpdateCreditCard(CreditCard $new_card_info,$existing_card)
    {
        //mysqli_stmt_prepare($stmt,"DELETE FROM user_credit_cards where user_id=?");
        
        global $session;
        global $mailer;
        
        $last_four = $new_card_info->GetLastFour();
        $card_descr = $new_card_info->getCardTypeString();
        
        $card_exp = $new_card_info->GetExpirationForAuthNet();
        

        // Send it first because we want to tell the user someone tried to change it, so we send this even if a failure happens.
        //$mailer->SendCreditCardInfoChanged($_SESSION['user_email'], $_SESSION['user_name'], $card_descr, $last_four);
        $error_object->ErrorCodeMajor = ERROR_MAJOR_INTERNAL;
        $error_object->ErrorCodeMinor = ERROR_MINOR_STATE_NOT_FOUND;
        $error_object->DisplayText="An Error Occurred trying to update your credit card information, Please try again, Or contact customer support if this persists";
        $error_object->LongText=$error_object->DisplayText;
        
        
        $paymentProfile = new AuthorizeNetPaymentProfile;
        $paymentProfile->payment->creditCard->cardNumber=$new_card_info->card_number;
        $paymentProfile->payment->creditCard->expirationDate= $card_exp;
        
        $request=new AuthorizeNetCIM(AUTH_NET_LOGIN,AUTH_NET_TRANS_KEY);
        
        $auth_response = $request->createCustomerPaymentProfile($session->user_data->users_authorize_net_customer_profile_id,
        						$paymentProfile,AUTH_NET_VALIDATION_MODE);
        
        if(!$auth_response->isOk())
        {
        	$err_str = "UpdateCreditCard Error: Could not update Payment Profile User: " . $_SESSION['user_id'] .
                               "Customer Profile ID: " . $session->user_data->users_authorize_net_customer_profile_id . " Payment Profile ID: " . 
                               		$existing_card['user_credit_card_payment_profile_id'] .
                               "(" . $auth_response->getErrorMessage() .":" . $auth_response->getMessageText() . ")";
        	
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,$err_str);
            
            $error_object->ErrorCodeMajor = ERROR_MAJOR_BILLING;
            $error_object->ErrorCodeMinor = $auth_response->getMessageCode();
            $error_object->DisplayText=ERROR_MAJOR_BILLING_TEXT;
            $error_object->LongText = $err_str;
            
            return false;
            
        }
                
        //print("CID: " . $existing_card['user_credit_card_id']);
        
        $stmt=mysqli_stmt_init($this->getConnection());
        mysqli_stmt_prepare($stmt,"UPDATE user_credit_cards SET user_credit_card_last4=?, user_credit_card_descr=?, user_credit_card_exp=?, user_credit_card_card_type=? where user_credit_card_id=?");
        
        if(!mysqli_stmt_bind_param($stmt,"sssii",$last_four,$card_descr,$card_exp,$new_card_info->card_type,$existing_card['user_credit_card_id'] ))
        {
        
            $err_str = "UpdateCreditCard: Failed to UPDATE with new Card Info (" . mysqli_stmt_error($stmt) . ")";
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,$err_str);
            		
            $error_object->LongText .= " - " . $err_str;
            // Try to delete the payment profile at least ...
            $request=new AuthorizeNetCIM(AUTH_NET_LOGIN,AUTH_NET_TRANS_KEY);
            
            $auth_response = $request->deleteCustomerPaymentProfile($session->user_data->users_authorize_net_customer_profile_id,
            		$auth_response->getPaymentProfileId());
            return false;
        }
        $query_result = mysqli_stmt_execute($stmt);
        if(!$query_result)
        {
        	$err_str = "UpdateCreditCard:  Execute Failed (" . mysqli_stmt_error($stmt) . ")";
        	
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,$err_str);
            $error_object->LongText = $err_str;
            //print("SQL Err: " . mysqli_stmt_error($stmt));
            
            $error_object->LongText .= " - " . $err_str;
            // Try to delete the payment profile at least ...
            $request=new AuthorizeNetCIM(AUTH_NET_LOGIN,AUTH_NET_TRANS_KEY);
            
            $auth_response = $request->deleteCustomerPaymentProfile($session->user_data->users_authorize_net_customer_profile_id,
            		$auth_response->getPaymentProfileId());
            
			return false;
        }

        
        $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,"UpdateCreditCard: Updated Credit Card Info ID: " . $existing_card['user_credit_card_id']);
        
        mysqli_stmt_close($stmt);
        return true;
    }
    
    
    function UpdateTimeToLocation($update_time_to_location)
    {        
        global $database;
        global $session;
        
        $stmt=mysqli_stmt_init($database->getConnection());
        
        //mysqli_stmt_prepare($stmt,"UPDATE users SET user_time_to_location=?, incarnation=incarnation+1 WHERE user_id=?");
        mysqli_stmt_prepare($stmt,"UPDATE users SET user_time_to_location=? WHERE user_id=?");
        
        if(!mysqli_stmt_bind_param($stmt,"ii",$update_time_to_location,$_SESSION['user_id']))
        {
			
            //print("SQL Err: " . mysqli_stmt_error($stmt));
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"UpdateTimeToLocation: UPDATE Bind Failed (" . mysqli_stmt_error($stmt) . ")");
			return false;
		}
        $query_result = mysqli_stmt_execute($stmt);
        //print("SQL Err: " . mysqli_stmt_error($stmt));
        if(!$query_result)
        {
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"UpdateTimeToLocation: Execute Failed (" . mysqli_stmt_error($stmt) . ")". mysqli_stmt_error($stmt) . ")");
			return false;
        }
        
        mysqli_stmt_close($stmt);
        return true;
        
    }

    function UpdateUserLocation($user_id,$location_id)
    {
    	global $database;
    	global $session;
    
    	$stmt=mysqli_stmt_init($database->getConnection());
    
    	//mysqli_stmt_prepare($stmt,"UPDATE users SET user_location_id=?, incarnation=incarnation+1 WHERE user_id=?");
    	mysqli_stmt_prepare($stmt,"UPDATE users SET user_location_id=? WHERE user_id=?");
    	if(!mysqli_stmt_bind_param($stmt,"ii",$location_id,$user_id))
    	{

    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"UpdateUserLocation: UPDATE Bind Failed (" . mysqli_stmt_error($stmt) . ")");
    		return false;
    	}
    	$query_result = mysqli_stmt_execute($stmt);
    	
    	if(!$query_result)
    	{
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"UpdateUserLocation: Execute Failed (" . mysqli_stmt_error($stmt) . ")". mysqli_stmt_error($stmt) . ")");
    		return false;
    	}
    
    	mysqli_stmt_close($stmt);
    	return true;
    
    }
    
    function UpdateUserTag($user_tag)
    {
        global $database;
        global $session;
        
        $stmt=mysqli_stmt_init($database->getConnection());
        
        //mysqli_stmt_prepare($stmt,"UPDATE users SET user_tag=?, incarnation=incarnation+1 where user_id=?");
        mysqli_stmt_prepare($stmt,"UPDATE users SET user_tag=? where user_id=?");
        
        if(!mysqli_stmt_bind_param($stmt,"si",$user_tag,$_SESSION['user_id']))
        {
			
            //print("SQL Err: " . mysqli_stmt_error($stmt));
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"UpdateUserTag: UPDATE Bind Failed (" . mysqli_stmt_error($stmt) . ")");
            mysqli_stmt_close($stmt);
			return false;
		}
        $query_result = mysqli_stmt_execute($stmt);
        //print("SQL Err: " . mysqli_stmt_error($stmt));
        if(!$query_result)
        {
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"UpdateUserTag: Execute Failed (" . mysqli_stmt_error($stmt) . ")");
            mysqli_stmt_close($stmt);
			return false;
        }
        
        mysqli_stmt_close($stmt);
        return true;
    }

    function UpdatePaymentMethod($user_id,$pay_method,FCError &$error)
    {
    	global $database;
    	global $session;
    
    	$stmt=mysqli_stmt_init($database->getConnection());
    
    	//mysqli_stmt_prepare($stmt,"UPDATE users SET user_pay_method=?, incarnation=incarnation+1 where user_id=? ");
    	mysqli_stmt_prepare($stmt,"UPDATE users SET user_pay_method=? where user_id=? ");
    
    	if(!mysqli_stmt_bind_param($stmt,"ii",$pay_method,$user_id))
    	{
    		$err_str = "UpdatePaymentMethod: UPDATE Bind Failed (" . mysqli_stmt_error($stmt) . ")";
    		
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR, $err_str);
    		mysqli_stmt_close($stmt);
    		$error->ErrorCodeMajor=ERROR_MAJOR_DATABASE;
    		$error->ErrorCodeMinor=ERROR_MINOR_DATABASE_GENERAL;
    		$error->LongText = $err_str;
    		$error->DisplayText = "Database Error";
    		
    		return false;
    	}
    	$query_result = mysqli_stmt_execute($stmt);
    	//print("SQL Err: " . mysqli_stmt_error($stmt));
    	if(!$query_result)
    	{
    		$err_str = "UpdatePaymentMethod: Execute Failed (" . mysqli_stmt_error($stmt) . ")";
    		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,$err_str);
    		mysqli_stmt_close($stmt);
    		$error->ErrorCodeMajor=ERROR_MAJOR_DATABASE;
    		$error->ErrorCodeMinor=ERROR_MINOR_DATABASE_GENERAL;
    		$error->LongText = $err_str;
    		$error->DisplayText = "Database Error";
    		return false;
    	}
    
    	mysqli_stmt_close($stmt);
    	return true;
    
    }
    
    function UpdateTagAndCar($user_tag,$user_car_make_id,$user_car_model_id,$user_car_color_id,$arrive_mode)
    {
        global $database;
        global $session;
        
        $stmt=mysqli_stmt_init($database->getConnection());
        
        //mysqli_stmt_prepare($stmt,"UPDATE users SET user_tag=?, user_car_make_id=?, user_car_model_id=?, user_car_color_id=?, user_arrive_mode=?, incarnation=incarnation+1 where user_id=?  ");
        mysqli_stmt_prepare($stmt,"UPDATE users SET user_tag=?, user_car_make_id=?, user_car_model_id=?, user_car_color_id=?, user_arrive_mode=? where user_id=?  ");
        
        
        if(!mysqli_stmt_bind_param($stmt,"siiiii",$user_tag,$user_car_make_id,$user_car_model_id,$user_car_color_id,$arrive_mode,$_SESSION['user_id']))
        {
			
            //print("SQL Err: " . mysqli_stmt_error($stmt));
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"UpdateTagAndCar: UPDATE Bind Failed (" . mysqli_stmt_error($stmt) . ")" );
            mysqli_stmt_close($stmt);
			return false;
		}
        $query_result = mysqli_stmt_execute($stmt);
        //print("SQL Err: " . mysqli_stmt_error($stmt));
        if(!$query_result)
        {
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"UpdateTagAndCar: Execute Failed (" . mysqli_stmt_error($stmt) . ")");
            mysqli_stmt_close($stmt);
			return false;
        }
        
        mysqli_stmt_close($stmt);
        return true;

    }
    
    function DoAddNewItem($drink_type_id,$user_drink_name,$user_drink_include_default,$menu_id,$user_drink_extra_options,$item_type)
    {
        global $database;
        global $session;
            
        $user_drink_extra_options_safe = mysqli_real_escape_string($this->getConnection(),$user_drink_extra_options);
        
        if (!mysqli_query($this->getConnection(),"START TRANSACTION")) 
        {   
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            		"DoAddNewItem: START_TRANSACTION FAILED ("  . 
                               mysqli_stmt_error($stmt . ")" ));
            mysqli_stmt_close($stmt);
            return -1;
        }
        $stmt=mysqli_stmt_init($database->getConnection());        
        mysqli_stmt_prepare($stmt,"INSERT INTO user_drinks (user_drink_id,user_drink_name, user_id, drink_type_id, drink_include_default, menu_id, user_drink_extra, item_type_class,created,last_modified) VALUES ('0',?,?,?,?,?,?,?,NULL,NULL)");
        
        // TODO check that $stmt creation succeeded
         
        if(!mysqli_stmt_bind_param($stmt,"siiiisi",$user_drink_name,$_SESSION['user_id'],$drink_type_id,
                                   $user_drink_include_default,$menu_id,$user_drink_extra_options_safe,$item_type))
        {
        	mysqli_query($this->getConnection(),"ROLLBACK");
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            		"DoAddNewItem: Bind Failed (" . mysqli_stmt_error($stmt) . ")");
			return -1;
		}
        
        $query_result = mysqli_stmt_execute($stmt);
        if($query_result!=TRUE)
        {
        	mysqli_query($this->getConnection(),"ROLLBACK");
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            		"DoAddNewItem: Execute Failed (" . mysqli_stmt_error($stmt) . ")");
  
            mysqli_stmt_close($stmt);
			return -1;
        }
        $DrinkID = mysqli_insert_id($this->getConnection());
        mysqli_stmt_close($stmt);

        
        if($this->AddDrinkOptions($DrinkID)!=true)
        {
        	mysqli_query($this->getConnection(),"ROLLBACK");
        	$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"DoAddNewItem: AddDrinkOptions Failed");
        	return -1;
        }
        mysqli_query($this->getConnection(),"COMMIT");
        return $DrinkID;
        
    }
       
    
    	
   
       
    function AddDrinkOptions($DrinkID)
    {
    	return $this->AddItemOptions($DrinkID);
    }
    
    function AddItemOptions($item_id)
    {
        $index=0;

        while(1)
        {

        	$drink_option_var = SER_USER_DRINK_OPTIONS . (string)$index;
	      	if(!isset($_POST[$drink_option_var]))
        	{
        		// Done
        		break;
        	}
        	//print("OPT: {$drink_option_var} SET");
        	
        	$drink_opt_array= explode ( '*', $_POST[$drink_option_var]);
        	
        	$stmt=mysqli_stmt_init($this->getConnection());
        	mysqli_stmt_prepare($stmt,"INSERT INTO user_drink_options VALUES ('0',?,?,?,NULL,NULL)");
        	
        	if(!mysqli_stmt_bind_param($stmt,"iii",$item_id,$drink_opt_array[2],$drink_opt_array[4]))
        	{
        	
        		$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        				"AddItemOptions: Add Options Bind Failed Item ID: {$item_id} (" . mysqli_stmt_error($stmt) . ")");
        		mysqli_query($this->getConnection(),"ROLLBACK");
        		return false;
        	}
        	
            $query_result = mysqli_stmt_execute($stmt);
            if(!$query_result)
            {        
            	$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            			"AddItemOptions: Add Options Execute Failed Item ID: {item_id} (" . mysqli_stmt_error($stmt) . ")");
				 mysqli_query($this->getConnection(),"ROLLBACK");
				 return false;
            }

            $index++;
            mysqli_stmt_close($stmt);
        }
        
       return true;
        
    }
    
    
    function DoEditUpdateDrink($user_drink_id,$user_drink_drink_type_id,$user_drink_name,$user_drink_include_default,$user_drink_extra_options)
    {
    	return $this->DoEditUpdateItem($user_drink_id,$user_drink_drink_type_id,$user_drink_name,
    									$user_drink_include_default,$user_drink_extra_options);
    }
    
    function DoEditUpdateItem($item_id,$user_drink_drink_type_id,$user_drink_name,$user_drink_include_default,$user_drink_extra_options)
    {
        global $session;
        global $xml_helper;

        
        
  		if($item_type == DB_USER_ITEM_CLASS_ALL)
        {
        	$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        			"DoEditUpdateItem: ItemID: {$item_id} ItemType cannot be ALL");
        	return false;
        }
        if (!mysqli_query($this->getConnection(),"START TRANSACTION")) 
        {
            
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            		"DoEditUpdateItem: START_TRANSACTION failed ItemID: {$item_id} (" . 
                               mysqli_error($this->getConnection() . ")" ));
            return false;
        }      
        // First, delete the Options (have to re-add them all anyway)
        
        $this->DeleteUserDrinkOptionsForDrink($item_id);
        
        
        //print("Stuff : " . $user_drink_drink_type_id . " " );
        $user_drink_extra_options_safe = mysqli_real_escape_string($this->getConnection(),$user_drink_extra_options);
        
       // print("EXTRA: {$user_drink_extra_options} / {$user_drink_extra_options_safe}");
        
        $stmt=mysqli_stmt_init($this->getConnection());        
        mysqli_stmt_prepare($stmt,"UPDATE user_drinks SET user_drink_name=?, drink_type_id=?, drink_include_default=?, user_drink_extra=?".
                            " WHERE user_drink_id=?");
        
        // TODO check that $stmt creation succeeded
        
        // s means the database expects a string
        if(!mysqli_stmt_bind_param($stmt,"siisi",$user_drink_name,$user_drink_drink_type_id,
                                   $user_drink_include_default, 
                                   $user_drink_extra_options_safe,
                                   $item_id
        						   ))
        {
			//die("SQL Err: " . mysqli_error());
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            		"DoEditUpdateItem: Bind Failed ItemID: {$item_id} (" . mysqli_stmt_error($stmt) . ")");
            mysqli_query($this->getConnection(),"ROLLBACK");
            mysqli_stmt_close($stmt);
			return false;
		}
        
        $query_result = mysqli_stmt_execute($stmt);
        if($query_result!=TRUE)
        {
            $this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
            		"DoEditUpdateItem: Execute Failed ItemID: {$item_id} (" . mysqli_stmt_error($stmt) . ")");
            
            mysqli_query($this->getConnection(),"ROLLBACK");
            mysqli_stmt_close($stmt);
			return false;
        }
        
        mysqli_stmt_close($stmt);
        
        if($this->AddDrinkOptions($item_id)!=true)
        {
        	$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        			"DoEditUpdateItem ItemID: {$item_id} AddDrinkOptions Failed");
        	 
        	mysqli_query($this->getConnection(),"ROLLBACK");
        	 
        	return false;
        }
        mysqli_query($this->getConnection(),"COMMIT");
        return true;
        
    }

	function AddUserFeedback($user_id,$email_to_use,$feedback_code,$feedback,$feedback_happiness,$date_time)
	{
		$stmt=mysqli_stmt_init($this->getConnection());
		
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO user_feedback VALUES ('0', ?, ?, ?, ?,?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"AddUserFeedback: Prepare Failed (" . mysqli_stmt_error($stmt) . ")");
			return false;
		}
		
		if(!mysqli_stmt_bind_param($stmt,"sisiiss",session_id(),$user_id,$email_to_use,$feedback_code,$feedback_happiness,$feedback,$date_time))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"AddUserFeedback: Bind Failed (" . mysqli_stmt_error($stmt) . ")");
			return false;
		}
		
		$query_result = mysqli_stmt_execute($stmt);
		
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"AddUserFeedback: Execute Failed (" . mysqli_stmt_error($stmt) . ")");
			return false;	
		}
		
		mysqli_stmt_close($stmt);
		return true;
	}
	
	function AddLogEntry($user_id,$user_email,$session_id,$code,$action)
	{
		$this->AddLogEntryReal($user_id,$user_email,$session_id,$code,LOG_CAT_UNKNOWN,$action);
	}
	
	function AddLogEntryReal($user_id,$user_email,$session_id,$code,$cat,$action)
	{
		global $database;
		
		
		$fc_date_now = gmdate( 'Y-m-d H:i:s');
		
		$stmt=mysqli_stmt_init($database->getConnection());
		
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO freeway_coffee_log VALUES ('0', ?, ?, ?, ?,?,?,?)"))
		{
			die("SQL Err: " . mysqli_error($this->getConnection()));
			return;
		}
		
		if(!mysqli_stmt_bind_param($stmt,"ississi",$user_id,$user_email,$session_id,$code,$action,$fc_date_now,$cat))
		{
			die("SQL Err: " . mysqli_error($this->getConnection()));
			return ;
		}
		
		$query_result = mysqli_stmt_execute($stmt);
		
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			return;	
		}
		
		mysqli_stmt_close($stmt);
		
		
		
	}
	
	
	
	
	/////////////////////// STATS /////////////////////////////////
	
	////////////////////// Adding /////////////////////////////////
	
	
	
	function StatsAddChangeOrderDisposition($table,$user_id,$location_id,$order_id,$start_date_time,$interval)
	{
		$stmt=mysqli_stmt_init($this->getConnection());
	
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO {$table} (user_id,location_id,order_id,start_time,duration_usec) VALUES (?,?,?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddChangeOrderDisposition({$table}): Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return;
		}
	
		if(!mysqli_stmt_bind_param($stmt,"iiisi",$user_id,$location_id,$order_id,$start_date_time,$interval))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddChangeOrderDisposition({$table}): Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return ;
		}
	
		$query_result = mysqli_stmt_execute($stmt);
	
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddChangeOrderDisposition({$table}): Execute Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			return;
		}
	
		mysqli_stmt_close($stmt);
	}
	
	function StatsAddMakeReport($user_id,$location_id,$start_date_time,$interval)
	{
		$stmt=mysqli_stmt_init($this->getConnection());
	
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO stats_make_reports (user_id,location_id,start_time,duration_usec) VALUES (?,?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddMakeReport: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return;
		}
	
		if(!mysqli_stmt_bind_param($stmt,"iisi",$user_id,$location_id,$start_date_time,$interval))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddMakeReport: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return ;
		}
	
		$query_result = mysqli_stmt_execute($stmt);
	
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			return;
		}
	
		mysqli_stmt_close($stmt);
	}
	
	function StatsAddMakeOrder($user_id,$location_id,$order_id,$start_date_time,$interval,$success)
	{
		$stmt=mysqli_stmt_init($this->getConnection());
	
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO stats_make_order (user_id,location_id,order_id,start_time,duration_usec,success) VALUES (?,?,?,?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddMakeOrder: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return;
		}
	
		if(!mysqli_stmt_bind_param($stmt,"iiisii",$user_id,$location_id,$order_id,$start_date_time,$interval,$success))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddMakeOrder: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return ;
		}
	
		$query_result = mysqli_stmt_execute($stmt);
	
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			return;
		}
	
		mysqli_stmt_close($stmt);
	}
	
	function StatsAddDeleteItem($user_id,$location_id,$start_date_time,$interval)
	{
		$stmt=mysqli_stmt_init($this->getConnection());
	
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO stats_delete_item (user_id,location_id,start_time,duration_usec) VALUES (?,?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddDeleteItem: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return;
		}
	
		if(!mysqli_stmt_bind_param($stmt,"iisi",$user_id,$location_id,$start_date_time,$interval))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddDeleteItem: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return ;
		}
	
		$query_result = mysqli_stmt_execute($stmt);
	
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			return;
		}
	
		mysqli_stmt_close($stmt);
	}
	
	function StatsAddEditItem($user_id,$location_id,$start_date_time,$interval)
	{
		$stmt=mysqli_stmt_init($this->getConnection());
	
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO stats_edit_item (user_id,location_id,start_time,duration_usec) VALUES (?,?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddAddEditItem: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return;
		}
	
		if(!mysqli_stmt_bind_param($stmt,"iisi",$user_id,$location_id,$start_date_time,$interval))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddAddEditItem: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return ;
		}
	
		$query_result = mysqli_stmt_execute($stmt);
	
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			return;
		}
	
		mysqli_stmt_close($stmt);
	}
	
	
	function StatsAddAddNewItem($user_id,$location_id,$start_date_time,$interval)
	{
		$stmt=mysqli_stmt_init($this->getConnection());
	
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO stats_add_new_item (user_id,location_id,start_time,duration_usec) VALUES (?,?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddAddNewItem: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return;
		}
	
		if(!mysqli_stmt_bind_param($stmt,"iisi",$user_id,$location_id,$start_date_time,$interval))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddAddNewItem: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
				
			return ;
		}
	
		$query_result = mysqli_stmt_execute($stmt);
	
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			return;
		}
	
		mysqli_stmt_close($stmt);
	}
	
	function StatsAddGetMenuItems($user_id,$location_id,$menu_id,$req_if_needed,$start_date_time,$interval)
	{
		$stmt=mysqli_stmt_init($this->getConnection());
	
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO stats_get_menu_items (user_id,location_id,menu_id,req_if_needed,start_time,duration_usec) VALUES (?,?,?,?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddGetMenuItems: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return;
		}
	
		if(!mysqli_stmt_bind_param($stmt,"iiiisi",$user_id,$location_id,$menu_id,$req_if_needed,$start_date_time,$interval))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddGetMenuItems: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			
			return ;
		}
	
		$query_result = mysqli_stmt_execute($stmt);
	
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			return;
		}
	
		mysqli_stmt_close($stmt);
	}
	
	function StatsAddDeleteCreditCard($user_id,$location_id,$success_code,$start_date_time,$interval)
	{
		$stmt=mysqli_stmt_init($this->getConnection());
	
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO stats_delete_credit_card (user_id,location_id,success_code,start_time,duration_usec) VALUES (?,?,?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddUpdateCreditCard: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return;
		}
	
		if(!mysqli_stmt_bind_param($stmt,"iisi",$user_id,$location_id,$success_code,$start_date_time,$interval))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddUpdateCreditCard: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			//die("SQL Err: " . mysqli_error($this->getConnection()));
			return ;
		}
	
		$query_result = mysqli_stmt_execute($stmt);
	
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			return;
		}
	
		mysqli_stmt_close($stmt);
	}
	function StatsAddUpdateCreditCard($user_id,$location_id,$success_code,$start_date_time,$interval)
	{
		$stmt=mysqli_stmt_init($this->getConnection());
	
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO stats_update_credit_card (user_id,location_id,success_code,start_time,duration_usec) VALUES (?,?,?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddUpdateCreditCard: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return;
		}
	
		if(!mysqli_stmt_bind_param($stmt,"iiisi",$user_id,$location_id,$success_code,$start_date_time,$interval))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddUpdateCreditCard: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			//die("SQL Err: " . mysqli_error($this->getConnection()));
			return ;
		}
	
		$query_result = mysqli_stmt_execute($stmt);
	
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			return;
		}
	
		mysqli_stmt_close($stmt);
	}
	
	function StatsAddUpdateTimeToLoc($user_id,$location_id,$success_code,$start_date_time,$interval)
	{
		$stmt=mysqli_stmt_init($this->getConnection());
	
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO stats_update_time_to_loc (user_id,location_id,success_code,start_time,duration_usec) VALUES (?,?,?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddUpdateTimeToLoc: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return;
		}
	
		if(!mysqli_stmt_bind_param($stmt,"iiisi",$user_id,$location_id,$success_code,$start_date_time,$interval))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddUpdateTimeToLoc: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			//die("SQL Err: " . mysqli_error($this->getConnection()));
			return ;
		}
	
		$query_result = mysqli_stmt_execute($stmt);
	
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			return;
		}
	
		mysqli_stmt_close($stmt);
	}
	
	function StatsAddGetUserItems($user_id,$location_id,$start_date_time,$interval)
	{
		$stmt=mysqli_stmt_init($this->getConnection());
	
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO stats_get_user_items (user_id,location_id,start_time,duration_usec) VALUES (?,?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddGetUserItems: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return;
		}
	
		if(!mysqli_stmt_bind_param($stmt,"iisi",$user_id,$location_id,$start_date_time,$interval))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddGetUserItems: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			//die("SQL Err: " . mysqli_error($this->getConnection()));
			return ;
		}
	
		$query_result = mysqli_stmt_execute($stmt);
	
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			return;
		}
	
		mysqli_stmt_close($stmt);
	}
	
	function StatsAddGetVehicleData($user_id,$location_id,$start_date_time,$interval)
	{
		$stmt=mysqli_stmt_init($this->getConnection());
	
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO stats_get_vehicle_data (user_id,location_id,start_time,duration_usec) VALUES (?,?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddGetVehicleData: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return;
		}
	
		if(!mysqli_stmt_bind_param($stmt,"iisi",$user_id,$location_id,$start_date_time,$interval))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddGetVehicleData: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			
			return ;
		}
	
		$query_result = mysqli_stmt_execute($stmt);
	
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			return;
		}
	
		mysqli_stmt_close($stmt);
	}
	
	function StatsAddCheckLogin($user_id,$admin_value,$start_date_time,$interval)
	{
		$stmt=mysqli_stmt_init($this->getConnection());
	
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO stats_check_login (user_id,admin_value,start_time,duration_usec) VALUES (?,?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddCheckLogin: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return;
		}
	
		if(!mysqli_stmt_bind_param($stmt,"iisi",$user_id,$admin_value,$start_date_time,$interval))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddCheckLogin: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			return ;
		}
	
		$query_result = mysqli_stmt_execute($stmt);
	
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			return;
		}
	
		mysqli_stmt_close($stmt);
	}
	
	function StatsAddSignon($user_id,$admin_value,$success_code,$start_date_time,$interval)
	{
		$stmt=mysqli_stmt_init($this->getConnection());
	
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO stats_signon (user_id,admin_value,success_code,start_time,duration_usec) VALUES (?,?,?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddSignon: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
				
			return;
		}
	
		if(!mysqli_stmt_bind_param($stmt,"iiisi",$user_id,$admin_value,$success_code,$start_date_time,$interval))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddSignon: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			//die("SQL Err: " . mysqli_error($this->getConnection()));
			return ;
		}
	
		$query_result = mysqli_stmt_execute($stmt);
	
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			return;
		}
	
		mysqli_stmt_close($stmt);
	}
	
	function StatsAddGetAllLocations($user_id,$start_date_time,$interval)
	{
		$stmt=mysqli_stmt_init($this->getConnection());
		
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO stats_get_all_locations (user_id,start_time,duration_usec) VALUES (?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddGetAllLocations: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
				
			return;
		}
		
		if(!mysqli_stmt_bind_param($stmt,"isi",$user_id,$start_date_time,$interval))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddGetAllLocations: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			
			return ;
		}
		
		$query_result = mysqli_stmt_execute($stmt);
		
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			return;
		}
		
		mysqli_stmt_close($stmt);
	}
	
	function StatsAddSignup($user_id,$start_date_time,$interval)
	{
		$stmt=mysqli_stmt_init($this->getConnection());
		
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO stats_signup (user_id,start_time,duration_usec) VALUES (?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddSignup: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			
			return;
		}
		
		if(!mysqli_stmt_bind_param($stmt,"isi",$user_id,$start_date_time,$interval))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"StatsAddSignup: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			//die("SQL Err: " . mysqli_error($this->getConnection()));
			return ;
		}
		
		$query_result = mysqli_stmt_execute($stmt);
		
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			return;
		}
		
		mysqli_stmt_close($stmt);
	}		

	
	function AddUserPhoneDataIOS($user_id,$device_name,$device_sys_name,$device_sys_ver,$device_model)
	{
	
		$stmt=mysqli_stmt_init($this->getConnection());
	
		$query="INSERT INTO user_ios_client_device_info (user_id,ios_device_name,ios_device_sys_name,ios_device_sys_ver,ios_device_model) VALUES (?,?, ?, ?, ?)";
		
		if(!mysqli_stmt_prepare($stmt,$query))
				 
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"AddUserPhoneDataIOS: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return;
		}
	
		if(!mysqli_stmt_bind_param($stmt,"issss",$user_id,$device_name,$device_sys_name,$device_sys_ver,$device_model))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"AddUserPhoneDataIOS: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			
			return ;
		}
	
		$query_result = mysqli_stmt_execute($stmt);
	
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"AddUserPhoneDataIOS: Exec Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			return;
		}
	
		mysqli_stmt_close($stmt);
	}
	
	function AddUserPhoneDataAndroid($user_id,$phone_model,$android_version,$phone_manuf,$phone_product)
	{
		
		$stmt=mysqli_stmt_init($this->getConnection());
		
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO user_android_phone_info VALUES ('0', ?,?, ?, ?, ?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"AddUserPhoneData: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
				
			return;
		}
		
		if(!mysqli_stmt_bind_param($stmt,"issss",$user_id,$phone_model,$android_version,$phone_manuf,$phone_product))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"AddUserPhoneData: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			die("SQL Err: " . mysqli_error($this->getConnection()));
			return ;
		}
		
		$query_result = mysqli_stmt_execute($stmt);
		
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"AddUserPhoneData: Exec Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			return;
		}
		
		mysqli_stmt_close($stmt);
	}
	
	function AddAndroidClientVersionData($user_id,$app_vername,$app_version_num)
	{
		if(is_null($app_vername))
		{
			$app_vername="Unknown";
		}
		if(is_null($app_version_num))
		{
			$app_version_num=0;
		}
		$stmt=mysqli_stmt_init($this->getConnection());
	
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO user_android_client_info VALUES ('0', ?,?, ?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"AddAndroidClientVersionData: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return;
		}
	
		if(!mysqli_stmt_bind_param($stmt,"iss",$user_id,$app_vername,$app_version_num))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"AddAndroidClientVersionData: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			die("SQL Err: " . mysqli_error($this->getConnection()));
			return ;
		}
	
		$query_result = mysqli_stmt_execute($stmt);
	
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"AddAndroidClientVersionData: Exec Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			return;
		}
	
		mysqli_stmt_close($stmt);
	}
	
	function AddIOSClientVersionData($user_id,$app_name_string,$app_build)
	{
		if(is_null($app_name_string))
		{
			$app_name_string="Unknown";
		}
		if(is_null($app_build))
		{
			$app_build="Unknown";
		}
		$stmt=mysqli_stmt_init($this->getConnection());
	
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO user_ios_app_info VALUES ('0', ?,?,?)"))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"AddIOSClientVersionData: Prepare Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
	
			return;
		}
	
		if(!mysqli_stmt_bind_param($stmt,"iss",$user_id,$app_name_string,$app_build))
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"AddIOSClientVersionData: Bind Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			return ;
		}
	
		$query_result = mysqli_stmt_execute($stmt);
	
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"AddIOSClientVersionData: Exec Failed UserID: {$user_id} (" . mysqli_stmt_error($stmt) . ")");
			
			return;
		}
	
		mysqli_stmt_close($stmt);
	}
};
 

?>