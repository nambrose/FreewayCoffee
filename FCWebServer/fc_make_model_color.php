<?php
	
    /* Nick Ambrose
     * Car Make/Model/Color
     * (C) Copyright Freeway Coffee, 2011,2013
     */
	
    require_once("fc_constants.php");
    
    require_once("fc_user_auth_db.php");
    require_once("fc_database_connection.php");
    
    $database=new DatabaseConnection();
    $user_auth_db = new UserAuthDB();
    
    require_once("fc_make_model_color_logic.php");
    require_once("fc_session.php");
 
    
    require_once("fc_xml_helper.php");
    require_once("fc_user_logic.php");
 
    
    $xml_helper = new XMLHelper();
	$session = new Session;
	
	$user = new UserLogic();
    $make_model_color = new CarMakeModelInfo();
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
        if( strcmp($_GET['user_command'],"get_all_car_make_models")==0)
        {
            if($make_model_color->DB_PrintAllCarmakeModelColorAsXML()!=true)
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,"DB_PrintAllCarmakeModelColorAsXML FAILED");
            }
            else
            {
           	 	$end_micro_time = microtime(true);
           	 	$database->StatsAddGetVehicleData($_SESSION['user_id'],$session->user_data->user_location_id,
            				$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
            }
            
        }
    }
?>
    