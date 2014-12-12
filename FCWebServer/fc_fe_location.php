<?php
	
    /* Nick Ambrose
     * Location Front End code
     * (C) Copyright Freeway Coffee, 2011, 2012
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
    require_once("fc_location_logic.php");
    require_once("fc_order_db.php");
    
    $xml_helper = new XMLHelper();
	$session = new Session;
	
    $mailer = new Mailer();
	$user = new UserLogic();
    
	$location_logic = new LocationLogic();
    
    
    $error_text="";
    $start_micro_time = microtime(true);
    $start_date_time = $database->GetNowAsUTC();
    $order_database = new DatabaseOrders();
    
    if(!$session->checkLogin())
    {
        $xml_helper->GenerateSigonResponse(SIGNON_RESPONSE_FAIL,null,null);
        $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,"User Signin Failed");
        die();
    }
    else
    {
        $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,USER_SIGNIN_OK);
        if( (strcmp($_POST['user_command'],LocationLogic::$GET_ALL_LOCATIONS_CMD)==0) || (strcmp($_GET['user_command'],LocationLogic::$GET_ALL_LOCATIONS_CMD)==0))
        {
        	$location_logic->GetAllLocations();
            $end_micro_time = microtime(true);
            $database->StatsAddGetAllLocations($_SESSION['user_id'],
            		$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
            
        }
        else if( (strcmp($_POST['user_command'],LocationLogic::$SET_USER_LOCATION_CMD)==0))
        {
        	$location_logic->SetAndPrintUserLocation($_SESSION['user_id'], $_POST[LOCATION_ID_ATTR]);
        }
        
    }
    
    ?>
