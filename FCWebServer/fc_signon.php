<?php
	
    /* Nick Ambrose
     * Signon
     * (C) Copyright Freeway Coffee, 2011,2012,2013
     */
	
    
    require_once("fc_constants.php");
    require_once("fc_user_auth_db.php");
    require_once("fc_database_connection.php");
	require_once("fc_error.php");
    $database=new DatabaseConnection();
    $user_auth_db = new UserAuthDB();
    require_once("fc_session.php");
    require_once("fc_mailer.php");
    require_once("fc_xml_helper.php");
    
    $xml_helper = new XMLHelper();
	$session = new Session;
	
    $mailer = new Mailer();
	
    
    $error_text="";
	
    $start_micro_time = microtime(true);
    $start_date_time = $database->GetNowAsUTC();

    // TODO -- make this shared (signup & signon use it)
    $client_type=APP_CLIENT_OS_VALUE_UNKNOWN_NUMBER;
    if(isset($_POST[APP_CLIENT_TYPE]))
    {
    	 
    	if(strcmp($_POST[APP_CLIENT_TYPE],APP_CLIENT_VALUE_ANDROID)==0)
    	{
    		$client_type=APP_CLIENT_OS_VALUE_ANDROID_NUMBER;
    		
    	}
    	else if(strcmp($_POST[APP_CLIENT_TYPE],APP_CLIENT_VALUE_IOS)==0)
    	{
    		$client_type = APP_CLIENT_OS_VALUE_IOS_NUMBER;
    	}
    }

    $error = new FCError();
    $error->ErrorCodeMajor=ERROR_MAJOR_SIGNON;
    $error->ErrorCodeMinor=ERROR_MINOR_SIGNON_INTERNAL;
    $error->DisplayText="Database Error";
    
    $incoming_compat_level = $_POST[COMPAT_LEVEL_ATTR];
    
    if($incoming_compat_level!=MAIN_COMPAT_LEVEL)
    {
    	$error->ErrorCodeMinor=ERROR_MINOR_SIGNUP_VERSION;
    	$error->DisplayText="Version Error";
    	$error->LongText = "Please download ver: " . Session::GetCompatReleaseStringForClientType($client_type);
    
    	$xml_helper->GenerateSignupFailResponse($client_type,$error);
    	die(0);
    }
    
    if($session->login($_POST['signon_user_email'],$_POST['signon_user_password']))    
    {
        // Signed In
 		if($client_type==APP_CLIENT_OS_VALUE_ANDROID_NUMBER)
 		{
 			$session->AddUserPhoneDataAndroid($_SESSION['user_id'],
 					$_POST[ANDROID_PHONE_MODEL],
 					$_POST[ANDROID_VERSION],
 					$_POST[ANDROID_PHONE_MANUF],
 					$_POST[ANDROID_PHONE_PRODUCT],
 					$_POST[FC_VERSION_NAME],
 					$_POST[FC_VERSION_NUM]);
 		}
 		else if ($client_type==APP_CLIENT_OS_VALUE_IOS_NUMBER)
 		{
 			$session->AddUserDataIOS($_SESSION['user_id'],
 					$_POST[IOS_DEVICE_NAME],
 					$_POST[IOS_DEVICE_SYS_NAME],
 					$_POST[IOS_DEVICE_SYS_VER],
 					$_POST[IOS_DEVICE_MODEL],
 					$_POST[IOS_APP_VER_NAME_STRING],
 					$_POST[IOS_APP_BUILD_STRING]
 			);
 		}
        $database->AddLogEntry($_SESSION['user_id'],$_POST['signon_user_email'],session_id(),LOG_CODE_SUCCESS,USER_SIGNIN_OK,"User Signin OK");
        $xml_helper->GenerateSigonResponse(SIGNON_RESPONSE_OK,$client_type,NULL);
        //print("Session Login email (" . $_SESSION['user_email'] .") uid: " . $_SESSION['user_id'] . "uname:" . $_SESSION['user_name'] . "id: " . session_id() );
        $end_micro_time = microtime(true);
        $database->StatsAddSignon($_SESSION['user_id'],$session->user_data->user_type,1,
        							$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
    }
    else
    {
    	$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,
    			"User Signin Failed SERVER: " . var_export($_SERVER,true));
    	// FIXME to have a real FCError here
        $xml_helper->GenerateSigonResponse(SIGNON_RESPONSE_FAIL,$client_type,NULL);
        $database->AddLogEntry(0,$_POST['signon_user_email'],session_id(),LOG_CODE_ERROR,USER_SIGNIN_FAIL,"User Signin Failed");
        $end_micro_time = microtime(true);
        $database->StatsAddSignon($_SESSION['user_id'],$session->user_data->user_type,0,
        						$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
    }

    
?>