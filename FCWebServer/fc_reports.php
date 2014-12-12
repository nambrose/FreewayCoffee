<?php
	
    /* Nick Ambrose
     * Reports
     * (C) Copyright Freeway Coffee, 2011,2012
     */
	
    require_once("fc_constants.php");
    require_once("fc_user_auth_db.php");
    
    require_once("fc_database_connection.php");
	
	$database=new DatabaseConnection();
    $user_auth_db = new UserAuthDB();
    require_once("fc_session.php");
    require_once("fc_mailer.php");
    require_once("fc_xml_helper.php");
    require_once("fc_user_logic.php");
    require_once("fc_om_orders_logic.php");
    require_once("fc_order_db.php");
    $xml_helper = new XMLHelper();
	$session = new Session;
	
	$user_logic = new UserLogic();
	
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
    			"User checkLoginAdmin Failed in fc_reports SERVER: " . var_export($_SERVER,true) );
    	die();
    }
    else if(strcmp($_POST[USER_COMMAND],COMMAND_REPORT==0))
    {
    	if($session->IsUserSuper()!=true)
    	{
    		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,
    				"User Super Check Failed in fc_reports");
    		die();
    	}
    	$order_logic->MakeReport($_POST[REPORT_NUM_DAYS]);
    	// UCK Location Hardcoded
    	// FIXME NOW PLEASE PLEASE PLEASE
    	$end_micro_time = microtime(true);
    	$database->StatsAddMakeReport($_SESSION['user_id'],1,
    			$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));

    	
    }

    
?>