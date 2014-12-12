<?php
/**
 * user_auth_db.php
 * 
 * The Database class is meant to simplify the task of accessing
 * user authentication information from the website's database.
 *
 *
 * (C) Copyright Freeway Coffee, 2011,2012,2013
 */
require_once("fc_constants.php");
require_once("ph.php");
	
class UserAuthDB
{

   /* Class constructor */
   function UserAuthDB()
   {

   }

   /**
    * confirmUserPass - Checks whether or not the given
    * username/pw is in the database
	* 
    */
	
	function confirmUserPass(&$hasher, $user_email, $password)
    {
		global $database;

		$retcode=false;
		
		if (strlen($password) > PW_MAX_LEN) 
		{
			//die("Password must be 72 characters or less");
			return false;
		}
		
		// Just in case the hash isn't found
		$stored_hash = "*";
		
		//Verify that user is in database 
		$stmt = mysqli_stmt_init($database->getConnection());
		
		
		if(!mysqli_stmt_prepare($stmt,"SELECT user_pw FROM users WHERE user_email_upper=?"))
		{
			//die ("XX" . mysqli_error($database->getConnection()));
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"confirmUserPass: Prepare Failed: User: {$user_email} (". mysqli_stmt_error($stmt) . ")");
			return false;
		}
		
		$user_email_upper = strtoupper($user_email);
		
		mysqli_stmt_bind_param($stmt,"s",$user_email_upper);	

		
		$query_result = mysqli_stmt_execute($stmt);
		
		if($query_result)
		{
			mysqli_stmt_store_result($stmt);
		}		
		else
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
						"confirmUserPass: Could not Execute: User: {$user_email} (" . mysqli_stmt_error($stmt) . ")" );
			mysqli_stmt_close($stmt);
			return false;
		}
		
	
		if(!mysqli_stmt_bind_result($stmt,$stored_hash))
		{
		
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
						"confirmUserPass: Could not bind result : User: {$user_email}(" . mysqli_stmt_error($stmt) . ")");
			mysqli_stmt_close($stmt);
			return false;
		
		}
		mysqli_stmt_fetch($stmt);
		mysqli_stmt_close($stmt);
		
		// Check that the password is correct, returns a boolean
		$check = $hasher->CheckPassword($password, $stored_hash);
		
		if ($check)
		{	
			// passwords matched! show account dashboard or something
			return true;
		} 
		else
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"confirmUserPass: Password does not match for User: {$user_email}");
			// No match
			return false;		
		}
		
		return $retcode;
		
	}
	
   
   /**
    * confirmUserID - Checks whether or not the given
    * username is in the database, if so it checks if the
    * given userid is the same userid in the database
    * for that user. If the user doesn't exist or if the
    * userids don't match up, it returns an error code
    * (1 or 2). On success it returns 0.
    */
	function confirmUserID($user_email, $userid)
	{
		global $database;
		
		$retcode=false;

		/* Verify that user is in database */
		
		$stmt=mysqli_stmt_init($database->getConnection());
		
		mysqli_stmt_prepare($stmt,"SELECT user_id FROM users WHERE user_email_upper = ? AND user_id = ?");
		
		$user_email_upper = strtoupper($user_email);
		
		mysqli_stmt_bind_param($stmt,'sd',$user_email_upper,$userid);
		
		$result = mysqli_stmt_execute($stmt);
		
		if($result)
		{
			mysqli_stmt_store_result($stmt);
		
		}
		if(!$result || (mysqli_stmt_num_rows($stmt) !=1))
		{
			$retcode=false; //Indicates username failure
		}
		
		$retcode=true;
		mysqli_stmt_close($stmt);
		
		return $retcode;
   }
   
   /**
    * usernameTaken - Returns true if the username has
    * been taken by another user, false otherwise.
    */
	/*
	function isUsernameTaken($user_email)
	{
		global $database;
		
		$retval=true; // Assume the worst I guess!
		
		$stmt=mysqli_stmt_init($database->getConnection());
		
		if(!mysqli_stmt_prepare($stmt,"SELECT user_email FROM users WHERE user_email = ?"))
		{
			mysqli_stmt_close($stmt);
			return true;
		}
		
		if(!mysqli_stmt_bind_param($stmt,'s',$user_email))
		{
			mysqli_stmt_close($stmt);
			return true;
		}
      
		$result = mysqli_stmt_execute($stmt);
		
		if($result)
		{
			mysqli_stmt_store_result($stmt);
			
		}
		if(!$result || (mysqli_stmt_num_rows($stmt) ==1))
		{
			$retcode=true; //Indicates username failure
		}
		else
		{
			$retcode=false;
		}
		mysqli_stmt_close($stmt);
		
		return $retcode;
   }
   */
	
     
	function addNewUser($user_email, $password, $user_name,$user_num_free_drinks,$user_num_free_food,$user_tag,$is_admin,$auth_customer_profile_id)
	{
		global $database;
		
		$time = time();
		$retcode=false;
		
		$stmt=mysqli_stmt_init($database->getConnection());
        $default_time_to_location=DEFAULT_TIME_TO_LOCATION;
		
        
        // Default is car.
        $arrive_mode = ARRIVE_MODE_CAR;
        
        $incarnation=1;
        
       	$user_pay_method=LocationPayMethod::$LOCATION_PAY_METHOD_IN_STORE;
       	
		if(!mysqli_stmt_prepare($stmt,"INSERT INTO users VALUES ('0', ?, ?, ?,?, ?,?, ?,?,0,?,?,0,0,0,0,0,
								{$arrive_mode},{$incarnation},{$user_pay_method},NULL,NULL)"))
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"addNewUser: Prepare Failed (". mysqli_stmt_error($stmt) . ")");
            //mysqli_stmt_close($stmt);
			return false;
		}
		
		$user_email_upper = strtoupper($user_email);
		
		if(!mysqli_stmt_bind_param($stmt,"sssssiiiii",$user_email,$user_email_upper,$user_name,$password,$user_tag,$user_num_free_drinks,$user_num_free_food,
                                   $is_admin,$default_time_to_location,$auth_customer_profile_id))		
		{
					
            $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"addNewUser: Bind Failed (". mysqli_stmt_error($stmt) . ")");
            mysqli_stmt_close($stmt);
			return false;
		}
		
		$query_result = mysqli_stmt_execute($stmt);
		if($query_result==true)
		{
            $retcode=true;
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			//print("Err:  ". mysqli_stmt_error($stmt));
            $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"addNewUser: Execute Failed (". mysqli_stmt_error($stmt) . ")");
            mysqli_stmt_close($stmt);
			return false;	
		}
		
		mysqli_stmt_close($stmt);
		
		return $retcode;
		 
   }
	
   
   /**
    * updateUserField - Updates a field, specified by the field
    * parameter, in the user's row of the database.
    */
	/*
	function updateUserField($username, $field, $value)
	{
		
      $q = "UPDATE ".TBL_USERS." SET ".$field." = '$value' WHERE username = '$username'";
      return mysql_query($q, $this->connection);
		 
		return true;
   }
   */
   
   
		
   /**
    * query - Performs the given query on the database and
    * returns the result, which may be false, true or a
    * resource identifier.
    */
	function query($query)
	{
		return mysqli_query($this->connection,$query);
	}
};

?>
