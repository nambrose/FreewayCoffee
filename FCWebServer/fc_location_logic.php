<?php
	
    /* Nick Ambrose
     * Location
     * (C) Copyright Freeway Coffee, 2011,2012,2013
     */
    
	class LocationLogic
	{
		// Commands
		static $GET_ALL_LOCATIONS_CMD="get_all_locations";
		static $SET_USER_LOCATION_CMD="set_user_location";
		
		// Responses
		static $UPDATED_USER_LOCATION_TAG="updated_location";
		static $LOCATIONS_LIST_TAG="locations";
		
		
		
		function GetAllLocations()
		{

			global $database;
			
			
			print("<" . LocationLogic::$LOCATIONS_LIST_TAG . ">");
			
			$stmt = $database->OpenGetLocations(EnumFetchActive::FETCH_ACTIVE_ONLY);
			$location = $database->GetNextLocations($stmt);
			while($location!=null)
			{
				$location->PrintAsXML();
				$location = $database->GetNextLocations($stmt);
			}
			
			print("</" . LocationLogic::$LOCATIONS_LIST_TAG . ">");
			$database->CloseGetLocations($stmt);
		}
		
		function SetAndPrintUserLocation($user_id,$location_id)
		{
			global $order_database;
			global $user;
			
			$location = $this->UpdateUserLocation($user_id,$location_id);
			
			print("<" . LocationLogic::$UPDATED_USER_LOCATION_TAG ." " . 
					LOCATION_ID_ATTR . "=\"" . $location_id . "\" " .
					USER_ID_ATTR . "=\"" . $user_id . "\" ");
			
			if($location==NULL)
			{
				print("result=\"failed\" >");
			}
			else
			{
				print("result=\"ok\" >");
				$location->PrintAsXML();
			}
			
			// IF they have an InProgress order for this location, then send it for "Last Order"
			if(Location::isNoneLocationID($session->user_data->user_location_id)!=TRUE)
			{
				$order_arr = $order_database->GetOpenInProgressOrdersForUserAndLocation($user_id,1,$location_id);
				if(!is_null($order_arr) && count($order_arr)>0)
				{
					$user->GenerateOrderAndLocation($order_arr[0],null);
				}
			}
			
			print("</" . LocationLogic::$UPDATED_USER_LOCATION_TAG . ">");
			
		}
		
		function UpdateUserLocation($user_id,$location_id)
		{
			global $database;
			
			$location = $database->GetLocation($location_id,EnumFetchActive::FETCH_ACTIVE_ONLY);
			
			if($location==NULL)
			{
				$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    				"UpdateUserLocation: Updating Location for UserID: [ {$user_id} ]  CheckLocation Failed for LocationID: [ {$location_id} ]");
				return NULL;
			}
			if($database->UpdateUserLocation($user_id,$location_id)!=true)
			{
				$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
						"UpdateUserLocation: Updating Location for UserID: [ {$user_id} ]  Update of User Record Failed for LocationID: [ {$location_id} ]");
				return NULL;
			}
			return $location;
		}
	}
	
	
?>