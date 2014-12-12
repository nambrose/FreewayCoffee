<?php
	
    /* Nick Ambrose
     * Signon
     * (C) Copyright Freeway Coffee, 2011
     */
	
    require_once("fc_constants.php");
    
    require_once("fc_user_auth_db.php");
    
    require_once("fc_database_connection.php");
	require_once("fc_om_orders_logic.php");
    
    $database=new DatabaseConnection();
    $user_auth_db = new UserAuthDB();
    
    
    require_once("fc_session.php");
    require_once("fc_mailer.php");
    
    require_once("fc_xml_helper.php");
    require_once("fc_user_logic.php");
    require_once("fc_item_objects.php");
    require_once("fc_credit_card.php");
    require_once("fc_order_db.php");
    require_once("fc_om_orders_logic.php");
  
    $xml_helper = new XMLHelper();
	$session = new Session;
	
    $mailer = new Mailer();
	$user = new UserLogic();
	$order_database = new DatabaseOrders();
    $error_text="";
    $orders_logic = new OM_OrdersLogic();
    
    $start_micro_time = microtime(true);
    $start_date_time = $database->GetNowAsUTC();
     
    
    if(!$session->checkLogin())
    {
        $xml_helper->GenerateSigonResponse(SIGNON_RESPONSE_FAIL,null,null);
        if( strcmp($_GET['user_command'],ADD_USER_FEEDBACK_COMMAND)==0)
        {
        	// We are allowed to do this without a login as the user may have forggoten their password etc.
        	// Fake user ID of NULL
        	$user->AddUserFeedback(NULL, $_GET['feedback_user_email'],$_GET['feedback_code'],$_GET['feedback'],
        							$_GET['feedback_happiness']);
        }
        else
        {
        	$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,"User Signin Failed");
        	die();
        }
    }
    else
    {
        //$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,USER_SIGNIN_OK);
        if( strcmp($_GET['user_command'],"get_user_items")==0)
        {
            $user->GetUserItems($_SESSION['user_id']);
            $end_micro_time = microtime(true);
            $database->StatsAddGetUserItems($_SESSION['user_id'],$session->user_data->user_location_id,
            		$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
        }
        else if( strcmp($_GET['user_command'],"update_time_to_location")==0)
        {
            if($user->UpdateTimeToLocation($_GET['update_time_to_location']))
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
                		"Updated Time To Location To: " .$_GET['update_time_to_location']);
                $end_micro_time = microtime(true);
            
                $database->StatsAddUpdateTimeToLoc($_SESSION['user_id'],$session->user_data->user_location_id,1,
                		$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
            }
            else
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,
                		"Failed to Update Time To Location");
                $end_micro_time = microtime(true);
                $database->StatsAddUpdateTimeToLoc($_SESSION['user_id'],$session->user_data->user_location_id,0,
                		$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
            }
        }
        else if( strcmp($_GET[USER_COMMAND],UPDATE_PAYMENT_METHOD_COMMAND)==0)
        {
        	if($user->UpdatePaymentMethod($_SESSION['user_id'],$_GET[LocationPayMethod::$LOCATION_PAY_METHOD_ATTR]))
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
        				"Updated Payment Method To: " . $_GET[LocationPayMethod::$LOCATION_PAY_METHOD_ATTR]);
        	}
        	else
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,
        				"Failed to Update Payment Method To: " . $_GET[LocationPayMethod::$LOCATION_PAY_METHOD_ATTR]);
        	}
        }
        
        
        else if(strcmp($_POST['user_command'],UPDATE_CREDIT_CARD_USE_FOR_PAY_CMD)==0)
        {
        	if($user->UpdateCreditCard($_POST['credit_card_number'], $_POST['credit_card_exp_month'], $_POST['credit_card_exp_year'], $_POST['credit_card_zip'],true)==true)
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,"Updated Credit Card");
        		$end_micro_time = microtime(true);
        		$database->StatsAddUpdateCreditCard($_SESSION['user_id'],$session->user_data->user_location_id,1,
        				$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
        	}
        	else
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,"Failed to Update Credit Card");
        		$end_micro_time = microtime(true);
        		$database->StatsAddUpdateCreditCard($_SESSION['user_id'],$session->user_data->user_location_id,0,
        				$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
        	}
        
        }
        else if(strcmp($_POST['user_command'],UPDATE_CREDIT_CARD_CMD)==0)
        {
            if($user->UpdateCreditCard($_POST['credit_card_number'], $_POST['credit_card_exp_month'], $_POST['credit_card_exp_year'], $_POST['credit_card_zip'],false)==true)
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,"Updated Credit Card");
                $end_micro_time = microtime(true);
                $database->StatsAddUpdateCreditCard($_SESSION['user_id'],$session->user_data->user_location_id,1,
                		$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
            }
            else
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,"Failed to Update Credit Card");
                $end_micro_time = microtime(true);
                $database->StatsAddUpdateCreditCard($_SESSION['user_id'],$session->user_data->user_location_id,0,
                		$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
            }
                                    
        }
        
        else if(strcmp($_POST['user_command'],DELETE_CREDIT_CARD)==0)
        {
        	if($user->DeleteCreditCardForThisUser($_POST[CREDIT_CARD_ID_CMD_PARAM])==true)
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,"Delete Credit Card ID: {$_POST['credit_card_id']} OK");
        		$end_micro_time = microtime(true);
        		$database->StatsAddDeleteCreditCard($_SESSION['user_id'],$session->user_data->user_location_id,1,
        				$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
        	}
        	else
        	{
        		$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,"Delete Credit Card ID: {$_POST['credit_card_id']} FAIL");
        		$database->StatsAddDeleteCreditCard($_SESSION['user_id'],$session->user_data->user_location_id,0,
        				$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
        	}
        }
        else if(strcmp($_GET['user_command'],"update_tag")==0)
        {
            
            if($user->UpdateTag($_GET['user_tag'])==true)
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,"Updated Tag OK {$_GET['user_tag']}");
            }
            else
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,"Updated Tag FAILED {$_GET['user_tag']}");
            }
        }
        else if(strcmp($_GET['user_command'],"update_tag_and_car")==0)
        {	
        	$arrive_mode = ARRIVE_MODE_CAR_STR;
        	if(isset($_GET[USER_ARRIVE_MODE_CMD_ARG]))
        	{
        		//print("Arrive: " . $_GET[USER_ARRIVE_MODE_CMD_ARG]);
        		
        		$arrive_mode = $_GET[USER_ARRIVE_MODE_CMD_ARG];
        	}
        	// TODO -- we really need to check these are numeric here (and a bazillion other places ... i hate stupid PHP and types)
            if($user->UpdateTagAndCar($_GET['user_tag'],$_GET['user_car_make_id'],$_GET['user_car_model_id'],$_GET['user_car_color_id'],$arrive_mode)==true)
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,"Updated Tag And Car OK {$_GET['user_tag']}");
            }
            else
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,"Updated Tag And Car FAILED {$_GET['user_tag']}");
            }
        }
        else if(strcmp($_POST[USER_COMMAND],SET_USER_TIP_CMD)==0)
        {
        	$user->SetTipForUserAndLocation($_SESSION['user_id'], $_POST[LOCATION_ID_ATTR], $_POST[USER_TIP_TYPE_ATTR], $_POST[USER_TIP_AMOUNT_ATTR],
        									$_POST[USER_TIP_ROUND_UP_ATTR]);	
        }
    	else if( strcmp($_GET['user_command'],ADD_USER_FEEDBACK_COMMAND)==0)
        {
        	// We are allowed to do this without a login as the user may have forggoten their password etc.
        	$user->AddUserFeedback($_SESSION['user_id'], $_GET['feedback_user_email'],$_GET['feedback_code'],$_GET['feedback'],
        							$_GET['feedback_happiness']);
        }
    }
     
?>
