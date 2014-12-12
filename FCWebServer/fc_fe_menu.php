<?php
	
    /* Nick Ambrose
     * Drink Pick
     * (C) Copyright Freeway Coffee, 2011
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
    require_once("fc_item_objects.php");
    require_once("fc_menu_logic.php");
    
    $xml_helper = new XMLHelper();
	$session = new Session;
	
    $mailer = new Mailer();
	$user = new UserLogic();
    
	$menu_logic = new MenuLogic();
    
    $error_text="";
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
        $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,USER_SIGNIN_OK);
        if( (strcmp($_POST[USER_COMMAND],GET_MENU_FOR_USER_CMD)==0))
        {
            $menu_logic->PrintMenuForUser($_SESSION['user_id']);
            $end_micro_time = microtime(true);
            $database->StatsAddGetMenuItems($_SESSION['user_id'],$session->user_data->user_location_id,$_POST[MENU_ID_CMD_PARAM],0,
            		$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
            
        }
        else if( (strcmp($_POST[USER_COMMAND],1,GET_MENU_FOR_USER_IF_NEED)==0))
        {	
            $menu_logic->PrintMenuForUserIfNeeded($_SESSION['user_id'],$_POST[MENU_HAVE_VERSION_CMD_ARG]);
            $end_micro_time = microtime(true);
            $database->StatsAddGetMenuItems($_SESSION['user_id'],$session->user_data->user_location_id,$_POST[MENU_ID_CMD_PARAM],TRUE,
            		$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
            
        }
    }
    
    ?>
