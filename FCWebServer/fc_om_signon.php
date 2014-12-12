<?php
	
    /* Nick Ambrose
     * Signon
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
    $xml_helper = new XMLHelper();
	$session = new Session;
	
	$user_logic = new UserLogic();
	
    $mailer = new Mailer();
    $error_text="";
	
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
    if($session->login_admin($_POST['signon_user_email'],$_POST['signon_user_password']))    
    {
        // Signed In
			
		if($client_type==APP_CLIENT_VALUE_ANDROID_NUMBER)
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

        $database->AddLogEntry($_SESSION['user_id'],$_POST['signon_user_email'],session_id(),LOG_CODE_SUCCESS,USER_SIGNIN_OK,
        		"User Signin OK (Admin)");
        $user_logic->GenerateOrderManagerSignonOK(SIGNON_RESPONSE_OK,$client_type);
        
    }
    else
    {
    	$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,
    			"fc_om_signon failed SERVER: " . var_export($_SERVER,true));
    	$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,
    			"fc_om_signon failed POST: " . var_export($_POST,true));
        $xml_helper->GenerateSigonResponse(SIGNON_RESPONSE_FAIL,$client_type,null);
        $database->AddLogEntry(0,$_POST['login_email'],session_id(),LOG_CODE_ERROR,USER_SIGNIN_FAIL,"User Signin Failed (Admin)");
    }

    
?>