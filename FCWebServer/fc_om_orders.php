<?php
	
    /* Nick Ambrose
     * Order Manager Orders
     * (C) Copyright Freeway Coffee, 2011,2012,2013
     */
	
    require_once("fc_constants.php");
    require_once("fc_user_auth_db.php");
    
    require_once("fc_database_connection.php");
	require_once("fc_order_db.php");
    //date_default_timezone_set("America/Los_Angeles");

    $database=new DatabaseConnection();
    $user_auth_db = new UserAuthDB();
    require_once("fc_session.php");
    require_once("fc_mailer.php");
    require_once("fc_xml_helper.php");
    require_once("fc_om_orders_logic.php");
    
    $xml_helper = new XMLHelper();
	$session = new Session;
	
    $mailer = new Mailer();
    $error_text="";
    $order_database = new DatabaseOrders();
    $order_logic = new OM_OrdersLogic();
	
    $start_micro_time = microtime(true);
    $start_date_time = $database->GetNowAsUTC();
    
    
    if(!$session->checkLoginAdmin())
    {
    	$xml_helper->GenerateSigonResponse(SIGNON_RESPONSE_FAIL,null,null);
    
    	$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,
    			"User checkLoginAdmin Failed in fc_om_orders POST: " . var_export($_POST,true));
    	$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,
    			"User checkLoginAdmin Failed in fc_om_orders GET: " . var_export($_GET,true));
    	$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,
    			"User checkLoginAdmin Failed in fc_om_orders SESSION: " . var_export($_SESSION,true));
    	$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,
    			"User checkLoginAdmin Failed in fc_om_orders SERVER: " . var_export($_SERVER,true));
    	
    	die();
    }
    if(strcmp($_POST[OM_USER_COMMAND],USER_COMMAND_UPDATE_LOCATION_OPEN_MODE)==0)
    {
    	$order_logic->UpdateLocationOpenMode($session->user_data->user_location_id,$_POST[Location::$USER_LOCATION_OPEN_MODE_ATTR]);
    }
    if(strcmp($_POST[OM_USER_COMMAND],USER_COMMAND_OM_GET_TODAYS_ORDERS_FOR_LOC)==0)
    {
    	// Important that we only use users location ID even though one may come up from the client,.
    	// Later when we have real roles and profiles maybe we can allow a selection
    	$order_logic->GetTodaysOrdersForLocation($session->user_data->user_location_id);
   	} 
    else if(strcmp($_POST[OM_USER_COMMAND],USER_COMMAND_OM_GET_ORDERS_SINCE_TIMESTAMP)==0)
    {
    	$order_logic->GetOrdersAfterTimeStampForLocation($session->user_data->user_location_id,$_POST[OM_TIMESTAMP_CMD_PARAM]);
    }
    else if(strcmp($_POST[OM_USER_COMMAND],USER_COMMAND_OM_GET_ORDERS_SINCE_TAG)==0)
    {
    	$order_logic->GetOrdersAfterTagForLocation($session->user_data->user_location_id,$_POST[OM_GLOBAL_ORDER_TAG_CMD_PARAM]);
    }
    else if(strcmp($_POST[OM_USER_COMMAND],OM_ORDER_DELIVER_CMD_PARAM)==0)
    {
    	if($order_logic->DoDeliverInProgressOrder($_POST[OM_ORDER_ID_CMD_PARAM],
    									$session->user_data->user_location_id,
    									$_POST[OM_INCARNATION_CMD_PARAM])==true)
    	{
    		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
    				"[DeliverOrder]: Updated Order Disposition: OrderID: {$_POST[OM_ORDER_ID_CMD_PARAM]} " .
    				"LocationID: {$session->user_data->user_location_id} Disposition: DELIVERED");
    		
    		$end_micro_time = microtime(true);
    		$database->StatsAddChangeOrderDisposition("stats_deliver_order",$_SESSION['user_id'],$session->user_data->user_location_id,
    				$_POST[OM_ORDER_ID_CMD_PARAM],
    				$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
    		
    	}
    	else
    	{
    		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
    				"[DeliverOrder]: Failed to Update Order Disposition: OrderID: {$_POST[OM_ORDER_ID_CMD_PARAM]} ".
    				" LocationID: {$session->user_data->user_location_id}");
    	}
    				
    }
    
    else if(strcmp($_POST[OM_USER_COMMAND],OM_ORDER_NOSHOW_CMD_PARAM)==0)
    {
    	
    	if($order_logic->DoNoShowInProgressOrder($_POST[OM_ORDER_ID_CMD_PARAM],
    							$_POST[OM_INCARNATION_CMD_PARAM],
    							$session->user_data->user_location_id)==true)
    	{
    		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
    				"[NoShowOrder]: Updated Order Disposition: OrderID: {$_POST[OM_ORDER_ID_CMD_PARAM]} ".
    				" LocationID: {$session->user_data->user_location_id} ".
    				" Disposition: NOSHOW");
    		$end_micro_time = microtime(true);
    		$database->StatsAddChangeOrderDisposition("stats_noshow_order",$_SESSION['user_id'],$session->user_data->user_location_id,
    				$_POST[OM_ORDER_ID_CMD_PARAM],
    				$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
    	}
    	else
    	{
    		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
    				"[NoShowOrder]: Failed to Update Order Disposition: OrderID: {$_POST[OM_ORDER_ID_CMD_PARAM]} " .
    				" LocationID: {$session->user_data->user_location_id}");
    	}
    }
    else if(strcmp($_POST[OM_USER_COMMAND],OM_ORDER_REFUND_CMD_PARAM)==0)
    {
    	if($order_logic->DoRefundInProgressOrder($_POST[OM_ORDER_ID_CMD_PARAM],
    							$session->user_data->user_location_id,
    							$_POST[OM_INCARNATION_CMD_PARAM])==true)
    	{
    		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
    				"[RefundOrder]: Updated Order Disposition: OrderID: {$_POST[OM_ORDER_ID_CMD_PARAM]} " .
    				"LocationID: {$session->user_data->user_location_id} " .
    				"Disposition: REFUNDED");
    				
    		$end_micro_time = microtime(true);
    		$database->StatsAddChangeOrderDisposition("stats_refund_order",$_SESSION['user_id'],$session->user_data->user_location_id,
    					$_POST[OM_ORDER_ID_CMD_PARAM],
    				$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
    	}
    	else
    	{
    		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
    				"[RefundOrder]: Failed to Update Order Disposition: OrderID: {$_POST[OM_ORDER_ID_CMD_PARAM]} " .
    				"LocationID: {$session->user_data->user_location_id}");   	
    	}
    	
    }
    

    
?>