<?php
/**
 * Session.php
 * 
 * The Session class is meant to simplify the task of keeping
 * track of logged in users and also guests.
 *
 * (C) Copyright Freeway Coffee, 2011
 */
//include("fc_constants.php");
	
/*
 *
 * $_SESSION Variables are:
 * $_SESSION['user_email']
 * $_SESSION['user_id']
 * $_SESSION['user_name']
 */
    
    require_once("AuthorizeNet.php");
    require_once("auth_net.php");
    require_once("ph.php");
    require_once("fc_location_objects.php");
    require_once("fc_user_data.php");
   
    class Session
    {
	
	
   var $logged_in;    //True if user is logged in, false otherwise
   
   var $user_data;
   var $sessionid; // PHP Session ID
   var $hasher;
   var $global_settings;
   var $user_location;

	function Session()
	{
		bcscale(10); // Erk !!!
		$this->hasher = new PasswordHash(8, false);
  	    $this->time = time();
  	    $this->startSession();
		$this->sessionid = session_id();
		$this->user_data=null;
		$this->global_settings=array();
		$this->user_location = null;
		
	}


	function doUnsetAll()
	{
		$this->doUnsetSession();
		$this->user_data= NULL;
		$this->user_location = null;
		$this->global_settings = (array)NULL;
	}
 
 
	function doUnsetSession()
	{
		unset($_SESSION['user_email']);
		unset($_SESSION['user_id']);
		unset($_SESSION['user_name']);
		
	}

	function GetNowAsUTC()
	{
		return gmdate( DEFAULT_DATE_TIME_FORMAT,time());
	}
	
   /**
    * startSession - Performs all the actions necessary to 
    * initialize this session object. Tries to determine if the
    * the user has logged in already, and sets the variables 
    * accordingly. Also takes advantage of this page load to
    * update the active visitors tables.
    */
	
	
	function startSession()
	{
		global $user_auth_db;
		
		if(!session_start())   //Tell PHP to start the session
        {
            print("Session_start bleh");
        }

		/// Determine if user is logged in 
		//$this->logged_in = $this->checkLogin();
	}
	

	static function GetCompatReleaseStringForClientType($client_type)
	{
		if($client_type==APP_CLIENT_OS_VALUE_ANDROID_NUMBER)
		{
			return  MAIN_COMPAT_RELEASE_ANDROID;
		}
		else if($client_type==APP_CLIENT_OS_VALUE_IOS_NUMBER)
		{
			return MAIN_COMPAT_RELEASE_ANDROID ;
		}
		else
		{
			return("compat_release_needed=\"Unknown\" ");
		}
		 
		 
	}
	
   /**
    * checkLogin - Checks if the user has already previously
    * logged in, and a session with the user has already been
    * established. Also checks to see if user has been remembered.
    * If so, the database is queried to make sure of the user's 
    * authenticity. Returns true if the user has logged in.
    */
	
	function checkLogin()
	{

		global $user_auth_db;
		global $database;
	
		$start_micro_time = microtime(true);
		$start_date_time = $this->GetNowAsUTC();
		if(isset($_SESSION['user_email']) && isset($_SESSION['user_id']))
		{
			// Confirm that username and userid are valid 
			if(!$user_auth_db->confirmUserID($_SESSION['user_email'], $_SESSION['user_id']))
			{
				// Variables are incorrect, user not logged in
				$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
						"checkLogin: confirmUserID Failed");
				  $this->logout();			  
				  return false;
			}
			else
			{             
				// User is logged in, set class variables 
				// Also sets user_location
				$this->user_data  = UserData::DB_GetUserInfoByLogin($database,$_SESSION['user_email']);
				
				if($database->GetGlobalSettings($this->global_settings)!=true)
				{
					$this->logout();
					return false;
				}
				
				
				if(strcmp($this->global_settings[GS_ALLOW_SIGNIN],"0")==0)
				{
					$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
							"checkLogin: Database Is not accepting Signins");
					$this->logout();
					return false;			
				}
				if($this->user_data->IsUserLocked())
				{
					$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
							"checkLogin: User Account Locked");
					$this->logout();
					return false;
				}
				
				$end_micro_time = microtime(true);
				$database->StatsAddCheckLogin($_SESSION['user_id'],$this->user_data->user_type,
						$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
				return true;
			}
			
		}
		// User not logged in 
		else
		{
			$this->logout();
			return false;
		}
		return false;
		 
	}
	
	
	function checkLoginAdmin()
	{
	
		global $user_auth_db;
		global $database;
	
		$start_micro_time = microtime(true);
		$start_date_time = $this->GetNowAsUTC();

		
		
		if(isset($_SESSION['user_email']) && isset($_SESSION['user_id']))
		{
			// Confirm that username and userid are valid
			if(!$user_auth_db->confirmUserID($_SESSION['user_email'], $_SESSION['user_id']))
			{
				// Variables are incorrect, user not logged in
				$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
						"checkLoginAdmin: confirmUserID Failed");
				$this->logout();
				return false;
			}
			else
			{
				// User is logged in, set class variables
				$this->user_data  = UserData::DB_GetUserInfoByLogin($database,$_SESSION['user_email']);
				if($database->GetGlobalSettings($this->global_settings)!=true)
				{
					$this->logout();
					return false;
				}
				
				if($this->user_data->IsUserAdminOrMore() !=true) 
				{
					// Not an admin
					$database->AddLogEntry(0,$subuser,session_id(),LOG_CODE_ERROR,USER_SIGNIN_FAIL,"Need Admin rights but not an admin");
					$this->logout();
					return false;
						
				}
				if(strcmp($this->global_settings[GS_ALLOW_SIGNIN],"0")==0)
				{
					$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
							"checkLoginAdmin: Database Is not accepting Signins ");
					$this->logout();
					return false;
				}
				if($this->user_data->IsUserLocked())
				{
					$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
							"checkLoginAdmin: User Account Locked");
					$this->logout();
					return false;
				}
				$database->StatsAddCheckLogin($_SESSION['user_id'],$this->user_data->user_type,
						$start_date_time,$database->GetTimeDiffInUsec($end_micro_time,$start_micro_time));
				return true;
			}
				
		}
		// User not logged in
		else
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"checkLoginAdmin: Session Vars not set. SESSION: " . var_export($_SESSION,true) );
			$this->logout();
			return false;
		}
		return false;
			
	}
	
	/**
	 * logout - Gets called when the user wants to be logged out of the
	 * website. It deletes any cookies that were stored on the users
	 * computer as a result of him wanting to be remembered, and also
	 * unsets session variables and demotes his user level to guest.
	 */
	
	
	
	function logout()
	{
		global $user_auth_db;  //The database connection
		
		$this->doUnsetAll();
		$this->logged_in = false;
		
	}
	
	
	/**
	 * login - The user has submitted his username and password
	 * 
	 */
	function login($subuser, $subpass)
	{
		global $user_auth_db,$database;
		
		if($user_auth_db->confirmUserPass($this->hasher,$subuser, $subpass)!=true)
		{
			$this->logout();
			return false;
		}
		
		
		// Username and password correct, register session variables 
		$this->user_data  = UserData::DB_GetUserInfoByLogin($database,$subuser);
		if(is_null($this->user_data))
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"login: Could not get UserByLogin -- even though confirmPass was good. Internal error");
			return false;
		}
		
		if($database->GetGlobalSettings($this->global_settings)!=true)
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"login: Could not get Global Settings");
			$this->logout();
			return false;
		}
		
		
		if(strcmp($this->global_settings[GS_ALLOW_SIGNIN],"0")==0)
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"login: Database Is not accepting Signins");
			$this->logout();
			return false;
		}
		if($this->user_data->IsUserLocked())
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"login: User Account Locked");
			$this->logout();
			return false;
		}

		$_SESSION['user_email']=$subuser;
		$_SESSION['user_id']=$this->user_data->user_id;
		$_SESSION['user_name']=$this->user_data->user_name;
		
  		$this->logged_in=true;
  		
		return true;
	}
	
	// Requires that the user be an administrator
	function login_admin($subuser, $subpass)
	{
		global $user_auth_db,$database;
		
		if($user_auth_db->confirmUserPass($this->hasher,$subuser,$subpass)!=true)
		{
			$this->logout();
			return false;
		}
	
		
		// Username and password correct, register session variables
		$this->user_data  = UserData::DB_GetUserInfoByLogin($database,$subuser);
		if(is_null($this->user_data))
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"login_admin: Could not get UserByLogin -- even though confirmPass was good. Internal error");
			return false;
		}
		
		if($database->GetGlobalSettings($this->global_settings)!=true)
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"login_admin: Could not get Global Settings");
			$this->logout();
			return false;
		}
		
		if(strcmp($this->global_settings[GS_ALLOW_SIGNIN],"0")==0)
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"login_admin: Database Is not accepting Signins");
			$this->logout();
			return false;
		}
		if($this->user_data->IsUserAdminOrMore()!=true)
		{
			// Not an admin
			
			$database->AddLogEntry(0,$subuser,session_id(),LOG_CODE_ERROR,USER_SIGNIN_FAIL,"Need Admin rights but not an admin");
			$this->logout();
			return false;
			
		}
		if($this->user_data->IsUserLocked())
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"login_admin: User Account Locked");
			$this->logout();
			return false;
		}
		$_SESSION['user_email']=$subuser;
		$_SESSION['user_id']=$this->user_data->user_id;
		$_SESSION['user_name']=$this->user_data->user_name;
	
		$this->logged_in=true;
	
		return true;
	}
	
	
	
	/**
	 * register - Gets called when the user has just submitted the
	 * registration form. Determines if there were any errors with
	 * the entry fields, if so, it records the errors and returns
	 * 
	 */
	
	function register($subuser_email, $subpass, $user_name,$user_tag,$client_type,&$error)
	{
		global $user_auth_db, $mailer, $database;  //The database, and mailer object
		
		if($database->GetGlobalSettings($this->global_settings)!=true)
		{
			$error->LongText="App Settings Could not be read";
			return false;
		}
		
		
		if(strcmp($this->global_settings[GS_ALLOW_SIGNIN],"0")==0)
		{
			$error->LongText="Global Settings Could not be read";
			
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"register: Database Is not accepting Signins");
			return false;
		}
        // Attempt to create an Authorize.net CustomerProfileID.
        $customerProfile = new AuthorizeNetCustomer;
        $customerProfile->email=$subuser_email;
        //$customerProfile->description=rawurlencode($user_name);
        //$customerProfile->merchantCustomerId=1;
        
        $request=new AuthorizeNetCIM(AUTH_NET_LOGIN,AUTH_NET_TRANS_KEY);
        $response = $request->createCustomerProfile($customerProfile);
        
        //print("Email: {$customerProfile->email}, Descr: {$customerProfile->description}, LOGIN:" .AUTH_NET_LOGIN . "KEY:" . AUTH_NET_TRANS_KEY  );
        if($response->isOk())
        {
            $new_customer_id = $response->getCustomerProfileId();
            $database->AddLogEntry(NULL,$subuser_email,session_id(),
            		LOG_CODE_SUCCESS,"Created Customer Profile {$new_customer_id} for User: " . $subuser_email);
        }
        else
        {
        	$error->LongText="Could not create billing profile";
        	
            $database->AddLogEntry(NULL,$subuser_email,session_id(),LOG_CODE_FAIL,
            		"Failed to create Customer Profile for User: " . $subuser_email . "Mesage: {$response->getMessageText()}");
            $new_customer_id=NULL;
            return false;
        }
        
        if (strlen($subpass) > PW_MAX_LEN)
        {
        	// Try to delete the damage we just did in Auth.net so they can at least sign up again
        	$request=new AuthorizeNetCIM(AUTH_NET_LOGIN,AUTH_NET_TRANS_KEY);
        	$request->deleteCustomerProfile($new_customer_id);
        	$error->LongText="Authorization Failed";
        	$error->DisplayText="Authorization Failed";
        	$error->ErrorCodeMinor=ERROR_MINOR_SIGNUP_AUTH_FAIL;
        	
        	return false;
        }
        
        $hash = $this->hasher->HashPassword($subpass);
        
        if (strlen($hash) < PW_MIN_LEN) 
        {      
        	// Password hash too short. Fail
        	// Try to delete the damage we just did in Auth.net so they can at least sign up again
        	$request=new AuthorizeNetCIM(AUTH_NET_LOGIN,AUTH_NET_TRANS_KEY);
        	$request->deleteCustomerProfile($new_customer_id);
        	$error->LongText="Authorization Failed";
        	$error->DisplayText="Your password is too short";
        	$error->ErrorCodeMinor=ERROR_MINOR_SIGNUP_AUTH_FAIL;
        	return false;   
        } 

		if($user_auth_db->addNewUser($subuser_email, $hash, $user_name,INITIAL_FREE_DRINKS,INITIAL_FREE_FOOD,$user_tag,0,$new_customer_id))
		{
			if($this->login($subuser_email,$subpass)!=true)
			{
				// Try to delete the damage we just did in Auth.net so they can at least sign up again
				$request=new AuthorizeNetCIM(AUTH_NET_LOGIN,AUTH_NET_TRANS_KEY);
				$request->deleteCustomerProfile($new_customer_id);
			}
			
			if(EMAIL_WELCOME)
			{
				$mailer->sendWelcome($subuser_email,$user_name);
			}
			return true;
		}
		else
		{
			//$database->AddLogEntry(NULL,$subuser_email,session_id(),LOG_CODE_FAIL,
				//	"Failed to create Customer Profile for User: " . $subuser_email . "Mesage: {$response->getMessageText()}");
			// Try to delete the damage we just did in Auth.net so they can at least sign up again
			$request=new AuthorizeNetCIM(AUTH_NET_LOGIN,AUTH_NET_TRANS_KEY);
			$request->deleteCustomerProfile($new_customer_id);
			$error->LongText="Add User Failed";
			
			return false;  //Registration attempt failed
		}
		
		// Try to delete the damage we just did in Auth.net so they can at least sign up again
		$request=new AuthorizeNetCIM(AUTH_NET_LOGIN,AUTH_NET_TRANS_KEY);
		$request->deleteCustomerProfile($new_customer_id);
		return false;
	}
	 
	function ReduceUsersFreeDrinks($reduce_count)
	{
		$this->user_data->user_free_drinks_count = intval($this->user_data->user_free_drinks_count,10) - $reduce_count;
	}
	
	function DoesUserHaveFreeDrinks($amount)
	{
		return $this->user_data->DoesUserHaveFreeDrinks($amount);
	}
	
	function AddUserPhoneDataAndroid($user_id,$phone_model,$android_version,$phone_manuf,$phone_product,$app_version_string,$app_version_num)
	{
		global $database;
		$database->AddUserPhoneDataAndroid($user_id,$phone_model,$android_version,$phone_manuf,$phone_product);
		$database->AddAndroidClientVersionData($user_id,$app_version_string,$app_version_num);
	}
	
	function AddUserDataIOS($user_id,$ios_device_name,$ios_device_sys_name,$ios_device_sys_ver,$ios_device_model,$ios_app_ver_name,$ios_app_build)
	{
		global $database;
		$database->AddUserPhoneDataIOS($user_id, $ios_device_name, $ios_device_sys_name, $ios_device_sys_ver, $ios_device_model);
		$database->AddIOSClientVersionData($user_id, $ios_app_ver_name, $ios_app_build);
	}
	 
};


?>
