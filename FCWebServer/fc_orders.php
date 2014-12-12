<?php
	
    /* Nick Ambrose
     * Signon
     * (C) Copyright Freeway Coffee, 2011,2012
     */
	
    require_once ("fc_constants.php");
    
    require_once("fc_user_auth_db.php");
    
    require_once("fc_database_connection.php");

    $database=new DatabaseConnection();
    $user_auth_db = new UserAuthDB();
    
    
    require_once("fc_session.php");
    require_once("fc_mailer.php");
    
    require_once("fc_xml_helper.php");
    require_once("fc_user_logic.php");
    require_once("fc_item_objects.php");
    require_once("fc_credit_card.php");
    
    require_once("fpdf.php");
    require_once("fc_pdf_helper.php");
  	require_once("fc_order_db.php");
    require_once("fc_om_orders_logic.php");
    
    
    $pdf_helper = new PDFHelper();
    
    $xml_helper = new XMLHelper();
	$session = new Session;
	
    $mailer = new Mailer();
	$user = new UserLogic();
    
    $error_text="";
	
    $order_database = new DatabaseOrders();
    $orders_logic = new OM_OrdersLogic();
   
    /*
     print("Session Login email (" . $_SESSION['user_email'] .") uid: " . $_SESSION['user_id'] . "uname:" . $_SESSION['user_name'] . "id: " . session_id() );
     */
    $start_micro_time = microtime(true);
    $start_date_time = $database->GetNowAsUTC();
     
    if(!$session->checkLogin())
    {
        $xml_helper->GenerateSigonResponse(SIGNON_RESPONSE_FAIL,null,null);
        $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,"User Signin Failed");
        die();
    }
    else
    {

        if( strcmp($_GET['user_command'],"make_order")==0)
        {
        	$client_type = APP_CLIENT_OS_VALUE_UNKNOWN_NUMBER;
        	if(  strcmp($_GET[APP_CLIENT_TYPE],APP_CLIENT_VALUE_ANDROID)==0)
        	{
        		$client_type = APP_CLIENT_OS_VALUE_ANDROID_NUMBER;
        	}
        	else if(strcmp($_GET[APP_CLIENT_TYPE],APP_CLIENT_VALUE_IOS)==0)
        	{
        		$client_type = APP_CLIENT_OS_VALUE_IOS_NUMBER;
        	}
        	
        	$arrive_mode = ARRIVE_MODE_CAR;
        	
        	if(isset($_GET[USER_ARRIVE_MODE_CMD_ARG]))
        	{
        		$arrive_mode = $_GET[USER_ARRIVE_MODE_CMD_ARG];
        		//print ("XXX: " .$_GET[USER_ARRIVE_MODE_CMD_ARG] );
        	}
        	$order_id=0;
        	$user_incarnation=$_GET[USER_INCARNATION_CMD_ARG];
        	$location_incarnation=$_GET[USER_LOCATION_INCARNATION_CMD_ARG];
        	
            if($user->MakeOrder($_SESSION['user_id'],$_GET['drinks_list'],$order_id,$client_type,$arrive_mode,$user_incarnation,$location_incarnation)==true)
            {
            	
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,"User Order Completed.");
                $end_micro_time = microtime(true);
                
                $database->StatsAddMakeOrder($_SESSION['user_id'],$session->user_data->user_location_id,$order_id,
                		$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time),1);
            }
            else
            {
            	
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,"User Order Failed.");
                $database->StatsAddMakeOrder($_SESSION['user_id'],$session->user_data->user_location_id,$order_id,
                		$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time),0);
            }
        }
        else if(strcmp($_GET['user_command'],"time_here")==0)
        {
        	$arrive_mode = ARRIVE_MODE_CAR;
        	 
        	if(isset($_GET[USER_ARRIVE_MODE_CMD_ARG]))
        	{
        		$arrive_mode = $_GET[USER_ARRIVE_MODE_CMD_ARG];
        	}
            if($user->UpdateTimeHere($_GET['order_id'],$arrive_mode)==true)
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
                		"User Order Time Here Updated: ORDER={$_GET['order_id']}");
            }
            else
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,
                		"User Order Time Here Update failed: ORDER={$_GET['order_id']}");
            }
        }
    }
    
?>
