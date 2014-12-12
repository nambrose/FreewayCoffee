<?php
	
    /* Nick Ambrose
     * User Data
     * (C) Copyright Freeway Coffee, 2011,2012,2013
     */
    
	class UserData
	{
		var $user_id;
		var $user_email;
		var $user_name;
		
		// Access Control
		var $user_type;
		var $user_locked;
		var $user_is_demo;
		
		// Arrive Data
		var $user_tag;
		var $user_arrive_mode;
		var $user_car_make_id;
		var $user_car_model_id;
		var $user_car_color_id;
		
		// Billing
		var $users_authorize_net_customer_profile_id;
		
		// Free
		var $user_free_drinks_count;
		var $user_free_food_count;
		
		// Location
		var $user_location_id;
		var $user_time_to_location;
		
		var $UserTZ;
		var $user_pay_method;
		
		var $incarnation;
		
		// Queries
		static $GET_USER_BASE_QUERY ="SELECT user_is_admin,user_email,user_id,user_name,user_tag,user_free_drinks_count,
                               user_free_food_count,user_location_id,user_time_to_location,users_authorize_net_customer_profile_id,
                               user_car_make_id,user_car_model_id,user_car_color_id,user_locked,user_is_demo,user_arrive_mode,user_pay_method,incarnation FROM users ";
		
		// Tags & Attrs
		// Users Table
		
		static $USER_DATA_TAG="user_info";
		
		static $USER_EMAIL_ATTR="user_email";
		static $USER_NAME_ATTR="user_name";
		
		static $USER_TYPE_ATTR="user_type";
		static $USER_IS_LOCKED_ATTR="user_locked";
		static $USER_IS_DEMO_ATTR="user_is_demo";
		
		static $USER_TAG_ATTR="user_tag";
		static $USER_ARRIVE_MODE_ATTR="user_arrive_mode";
		//static $USER_CAR_MAKE_ID_ATTR;
		//static $USER_CAR_MODEL_ID_ATTR;
		//static $USER_CAR_COLOR_ID_ATTR;
		
		// Dont print this ons !
		//static $USER_AUTHORIZE_NET_CUSTOMER_PROFILE_ID_ATTR;
		
		static $USER_FREE_DRINKS_COUNT_ATTR="user_free_drinks";
		static $USER_FREE_FOOD_COUNT_ATTR="user_free_food";
		
		static $USER_LOCATION_ID_ATTR="user_location_id";
		static $USER_TIME_TO_LOCATION="user_time_to_location";
		
		static $USER_TZ_ATTR="user_tz";
		
		static $USER_PAY_METHOD_ATTR="user_pay_method";
		
		function PrintAsXML()
		{
			print("<" . UserData::$USER_DATA_TAG . " id=\"" . rawurlencode($this->user_id) ."\" " .
				 	UserData::$USER_NAME_ATTR. "=\"" . rawurlencode($this->user_name) ."\" " .
					UserData::$USER_EMAIL_ATTR . "=\"" .  rawurlencode($this->user_email) ."\" " .
					UserData::$USER_TAG_ATTR . "=\"" . rawurlencode($this->user_tag) ."\" " .
					UserData::$USER_FREE_DRINKS_COUNT_ATTR . "=\"" . rawurlencode($this->user_free_drinks_count) ."\" " .
					UserData::$USER_FREE_FOOD_COUNT_ATTR ."=\"" . rawurlencode($this->user_free_food_count) ."\" " .
					UserData::$USER_TIME_TO_LOCATION ."=\"" . rawurlencode($this->user_time_to_location) . "\" " .
					UserData::$USER_IS_DEMO_ATTR . "=\"" . rawurlencode($this->user_is_demo) . "\" " .
					UserData::$USER_IS_LOCKED_ATTR . "=\"" . rawurlencode($this->user_locked) . "\" " .
					UserData::$USER_TYPE_ATTR . "=\"" . rawurlencode($this->user_type) . "\" " .
					UserData::$USER_ARRIVE_MODE_ATTR . "=\"" . rawurlencode($this->user_arrive_mode) . "\" " .
					UserData::$USER_LOCATION_ID_ATTR . "=\"" . rawurlencode($this->user_location_id) . "\" " .
					UserData::$USER_PAY_METHOD_ATTR . "=\"" . rawurlencode($this->user_pay_method) . "\" " .
					INCARNATION_ATTR . "=\"" . rawurlencode($this->incarnation) . "\" " .
					">" . "</" . UserData::$USER_DATA_TAG .">");
		}
		
		static function DB_GetUserInfoByLogin($database,$user_email)
		{
		
			$user_email_upper = strtoupper($user_email);
			$stmt = mysqli_stmt_init($database->getConnection());
		
			if(!mysqli_stmt_prepare($stmt,UserData::GetQueryByUserEmail()))
			{
				$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
						"UserData::DB_GetUserInfoByLogin Prepare Error: (" . mysqli_stmt_error($stmt) . ")" );
			}
			 
			mysqli_stmt_bind_param($stmt,'s',$user_email_upper);
		
			return UserData::DB_GetUserInfoByQuery($database,$stmt);
		}
		
		 
		static function DB_GetUserInfoByUserID($database,$userid)
		{
		
			$stmt = mysqli_stmt_init($database->getConnection());
			if(!mysqli_stmt_prepare($stmt,UserData::GetQueryByUserID()))
			{
				$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
						"UserData::DB_GetUserInfoByUserID Prepare Error: (" . mysqli_stmt_error($stmt) . ")" );
			}
		
			mysqli_stmt_bind_param($stmt,'i',$userid);
		
			return UserData::DB_GetUserInfoByQuery($database,$stmt);
		}
		
		static function DB_GetUserInfoByQuery($database,$stmt)
		{
			global $session;
			
			if(mysqli_stmt_execute($stmt))
			{
				mysqli_stmt_store_result($stmt);
			}
			else
			{
				$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
						"UserData::DB_GetUserInfoByQuery Error: (" . mysqli_stmt_error($stmt) . ")" );
		
				return NULL;
			}
		
			if(mysqli_stmt_num_rows($stmt) != 1)
			{
				$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
						"UserData::DB_GetUserInfoByQuery Error: NumRows not 1" );
		
				return NULL;
			}
		
			$user_data = new UserData();
			
			// prepare the assoc array
			if(!mysqli_stmt_bind_result($stmt,$user_data->user_type, $user_data->user_email,
					$user_data->user_id,$user_data->user_name,$user_data->user_tag,$user_data->user_free_drinks_count,
					$user_data->user_free_food_count,$user_data->user_location_id,$user_data->user_time_to_location,
					$user_data->users_authorize_net_customer_profile_id,
					$user_data->user_car_make_id,$user_data->user_car_model_id,$user_data->user_car_color_id,
					$user_data->user_locked, $user_data->user_is_demo,$user_data->user_arrive_mode,$user_data->user_pay_method,
					$user_data->incarnation))
			{
				$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
							"UserData::DB_GetUserInfoByQuery Error: (" . mysqli_stmt_error($stmt) . ")" );
				return NULL;
			}
		
			 
			mysqli_stmt_fetch($stmt);
			mysqli_stmt_close($stmt);
			 	
			$session->user_location = Location::DB_GetLocationInfo($database, $user_data->user_location_id);
		
			if(is_null($session->user_location))
			{
				$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_INFO,
						"UserData::DB_GetUserInfoByQuery: INFO, Could not find any location information(Provisioning Error) Using Default:" .DEFAULT_TZ );
				date_default_timezone_set(DEFAULT_TZ);
				$user_data->UserTZ=DEFAULT_TZ;
				return $user_data;
			}
			else
			{
				if(strlen($session->user_location->LocationTZ)==0)
				{
					$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
							"UserData::DB_GetUserInfoByQuery Error Location TZ empty Loc Info:" . var_export($session->user_location,true) . "User Info: " . var_export($dbarray,true));
					 
				}
				else
				{
					if($session->user_location->isNoneLocation())
					{
						$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_INFO,
								"UserData::DB_GetUserInfoByQuery: INFO, Found None Location, using its TZ( - Fix this):" .$session->user_location->LocationTZ );
					}
					$user_data->UserTZ=$session->user_location->LocationTZ;
					date_default_timezone_set($user_data->UserTZ);
				}
			}
			return $user_data;
		}
		
		
		static function GetQueryByUserID()
		{
			return UserData::$GET_USER_BASE_QUERY . " WHERE user_id = ?";
		}
		
		static function GetQueryByUserEmail()
		{
			return UserData::$GET_USER_BASE_QUERY . " WHERE user_email_upper = ?";
		}

		function IsUserLocked()
		{
			if($this->user_locked==0)
			{
				return false;
			}
			return true;
		}
	
		function IsUserDemo()
		{
			if($this->user_id_demo==0)
			{
				return false;
			}
			return true;
		}
	
		function IsUserAdminOrMore()
		{
			if($this->user_type==USER_ADMIN)
			{
				return true;
			}
			if($this->IsUserSuper())
			{
				return true;
			}
			return false;
		}
	
		function IsUserSuper()
		{
			if($this->user_type==USER_SUPER)
			{
				return true;
			}
			return false;
		}
	
		function DoesUserHaveFreeDrinks($amount)
		{
			if(intval($this->user_free_drinks_count,10) >= $amount)
			{
				return true;
			}
			return false;
		}

		function IsBillingProfilePopulated()
		{
			if(is_null($this->users_authorize_net_customer_profile_id))
			{
				return false;
			}

			if(strlen($this->users_authorize_net_customer_profile_id)==0)
			{
				return false;
			}
			return true;	
		}
		
		function IsUserPayAtLocation()
		{
			
			if(LocationPayMethod::s_IsPayAtLocation($this->user_pay_method)==true)
			{
				return true;
			}
			return false;
		}
		

	}
?>