<?php
	
    /* Nick Ambrose
     * Add or Edit a drink (and send new drink back to client since only the server can create some of the info)
     * (C) Copyright Freeway Coffee, 2011
     */
	
    require_once("fc_constants.php");
    require_once("fc_user_auth_db.php");
    require_once("fc_database_connection.php");
    
    //date_default_timezone_set("America/Los_Angeles");
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
    
    $user_drink = new UserDrink();
    
    $start_micro_time = microtime(true);
    $start_date_time = $database->GetNowAsUTC();
     
    $error_text="";
    
    if(!$session->checkLogin())
    {
        $xml_helper->GenerateSigonResponse(SIGNON_RESPONSE_FAIL,null,null);
        $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,"User Signin Failed");
        die();
    }
    else
    {
    	
    	$user_menu_ver = $_POST[MENU_HAVE_VERSION_CMD_ARG];
    	
        if(strcmp($_POST['user_command'],"user_add_drink")==0)
        {	
        	// Check the menu version
        	if(!MenuLogic::CheckMenuVersion($session->user_location->LocationMenuID,$user_menu_ver))
        	{
        		$error_object = new FCError ();
        		// User incarnation does not match
        		$err_str = "App information out of sync with server (Menu) Please Try again";
        		$error_object->DisplayText = "";
        		$error_object->ErrorCodeMajor = ERROR_MAJOR_OUT_OF_SYNC;
        		$error_object->ErrorCodeMinor = ERROR_MINOR_USER_OUT_OF_SYNC;
        		$error_object->DisplayText=$err_str;
        		$error_object->LongText=$err_str;
        		XMLHelper::GenerateUserAddDrinkFailedNeedUpdate($error_object);
        		die();
        	}
        	
            $drink_id=$database->DoAddNewItem($_POST[USER_DRINK_DRINK_TYPE_ATTR],
            									$_POST[USER_DRINK_NAME_ATTR],
            									$_POST[USER_DRINK_INCLUDE_DEFAULT_ATTR], 
            									$_POST[MENU_ID_CMD_PARAM],
            									$_POST[USER_DRINK_EXTRA_ATTR],
            									$_POST[DRINK_TYPE_TYPE_ID_CMD_PARAM],
            									$user_menu_ver);
               
            if($drink_id!=-1)
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,"User Added Drink ID: {$drink_id}");
                $user->GenerateUserAddDrinkSuccess($drink_id);
                $end_micro_time = microtime(true);
                $database->StatsAddAddNewItem($_SESSION['user_id'],$session->user_data->user_location_id,
                		$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
            }
            else
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,"User Add Drink Failed.");
                XMLHelper::GenerateUserAddDrinkFailed();
                
            }
            
        }
        else if(strcmp($_POST['user_command'],"user_edit_drink")==0)
        {
        	// Check the menu version
        	if(!MenuLogic::CheckMenuVersion($session->user_location->LocationMenuID,$user_menu_ver))
        	{
        		$error_object = new FCError ();
        		// User incarnation does not match
        		$err_str = "App information out of sync with server (Menu) Please Try again";
        		$error_object->DisplayText = "";
        		$error_object->ErrorCodeMajor = ERROR_MAJOR_OUT_OF_SYNC;
        		$error_object->ErrorCodeMinor = ERROR_MINOR_USER_OUT_OF_SYNC;
        		$error_object->DisplayText=$err_str;
        		$error_object->LongText=$err_str;
        		XMLHelper::GenerateUserEditDrinkFailedNeedUpdate($error_object);
        		die();
        	}
        	
            $result = $database->DoEditUpdateDrink($_POST['user_drink_id'],$_POST[USER_DRINK_DRINK_TYPE_ATTR],$_POST[USER_DRINK_NAME_ATTR],
            						$_POST[USER_DRINK_INCLUDE_DEFAULT_ATTR] , $_POST[USER_DRINK_EXTRA_ATTR]);
            if($result==true)
            {
            	// Get drink can fail inside here. This is bad as now client and server state are now out of sync
                if($user->GenerateUserEditDrinkSuccess($_POST['user_drink_id'])==true)
                {
                	$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
                			"Drink ID: {$_POST['user_drink_id']} Edit SUCCESS");
                	$end_micro_time = microtime(true);
                	$database->StatsAddEditItem($_SESSION['user_id'],$session->user_data->user_location_id,
                			$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
                }
                else
                {
                	$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,LOG_CAT_INTERNAL,
                			"Drink ID: {$_POST['user_drink_id']} EDIT Succeeded then GET Failed!! DB MAY NOT BE CONSISTENT WITH APP");
                	$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,LOG_CAT_DB_APP_SYNC,
                			"Drink ID: {$_POST['user_drink_id']} EDIT Succeeded then GET Failed!! DB MAY NOT BE CONSISTENT WITH APP");
                	
                }
            }
            else
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,
                			"Drink ID: {$_POST['user_drink_id']} EDIT Failed");
                XMLHelper::GenerateUserEditDrinkFailed();
            }
        }
        else if(strcmp($_GET['user_command'],"delete_drink")==0)
        {
            $result = $database->DoDeleteDrink($_GET["drink_id"]);
            
            if($result==true)
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
                		"Drink ID: {$_POST['user_drink_id']} Deleted");
                $end_micro_time = microtime(true);
                $database->StatsAddDeleteItem($_SESSION['user_id'],$session->user_data->user_location_id,
                		$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
            }
            else
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,
                		"Drink ID: {$_POST['user_drink_id']} Delete Failed");
            }
            $xml_helper->GenerateDrinkDeleteResponse($result,$_GET["drink_id"]);
            
        }
        
    }
    
    
    ?>
