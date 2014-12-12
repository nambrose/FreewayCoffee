<?php
	
    /* Nick Ambrose
     * Location
     * (C) Copyright Freeway Coffee, 2011,2012,2013
     */

class LocationArriveMode
{
	// in fc_contsants static $ARRIVE_TYPE_CAR=0;
	//in fc_contsants static $ARRIVE_TYPE_WALKUP=1;
	
	
	static $LOCATION_ARRIVE_MODE_TAG="l_a_m";
	static $LOCATION_ARRIVE_MODE_ATTR="l_a_m_am";
	static $LOCATION_ARRIVE_MODE_STRING_ATTR="l_a_m_sam";
	
	static $ARRIVE_MODE_CAR_ATTR_VALUE="Vehicle";
	static $ARRIVE_MODE_WALKUP_ATTR_VALUE="Walkup";
	
	static $GET_ARRIVE_MODES_QUERY = "SELECT arrival_type from  locations_allowed_arrival_modes ";
	var $arrive_mode;
	
	static function DB_GetAllowedArrivalTypesForLocationID($database, $location_id)
	{
		//print("DB_GetAllowedArrivalTypesForLocationID: {$location_id}");
		$result=array();
		
		$stmt = mysqli_stmt_init($database->getConnection());
		
		$query = LocationArriveMode::$GET_ARRIVE_MODES_QUERY . " WHERE location_id=?";
			
		mysqli_stmt_prepare($stmt,$query);
		 
		mysqli_stmt_bind_param($stmt,'i',  $location_id);
		if(mysqli_stmt_execute($stmt))
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"DB_GetAllowedArrivalTypesForLocationID: Could not store result from SELECT (" . mysqli_stmt_error($stmt) . ")" );
			return NULL;
		}
		
		while(true)
		{
			$arrive = new LocationArriveMode();
			if(!mysqli_stmt_bind_result($stmt,$arrive->arrive_mode))
			{
				$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
						"DB_GetAllowedArrivalTypesForLocationID: Could not bind result (" . mysqli_stmt_error($stmt) . ")" );
				return result;
			}
			if( mysqli_stmt_fetch($stmt)!=true)
			{
			
				return $result;
			}
			else
			{	
				$result[]=$arrive;
			}
		}
		return $result; // Unreachable
	}
	
	function PrintAsXML()
	{
		print("<" . LocationArriveMode::$LOCATION_ARRIVE_MODE_TAG  . " " . 
				LocationArriveMode::$LOCATION_ARRIVE_MODE_ATTR . "=\"" . rawurlencode($this->arrive_mode) . "\" " );
		if($this->arrive_mode==ARRIVE_MODE_CAR)
		{
			print(LocationArriveMode::$LOCATION_ARRIVE_MODE_STRING_ATTR . "=\"" . rawurlencode(LocationArriveMode::$ARRIVE_MODE_CAR_ATTR_VALUE) . "\" ");
		}
		else if($this->arrive_mode==ARRIVE_MODE_WALKUP)
		{
			print(LocationArriveMode::$LOCATION_ARRIVE_MODE_STRING_ATTR . "=\"" . rawurlencode(LocationArriveMode::$ARRIVE_MODE_WALKUP_ATTR_VALUE) . "\" ");
		}
		print("></" . LocationArriveMode::$LOCATION_ARRIVE_MODE_TAG . ">");
		
	}
	
	static function MakeArriveModeStringFromID($arrive_mode_id)
	{
		switch($arrive_mode_id)
		{
			case ARRIVE_MODE_CAR:
				return ARRIVE_MODE_CAR_STR;
			case ARRIVE_MODE_WALKUP:
				return ARRIVE_MODE_WALKUP_STR;
			default:
				return NONE_TEXT;
		}
	}
	
	function DoesArriveModeMatch($arrive_mode)
	{
		return $this->arrive_mode==$arrive_mode;
	}
}

class LocationPayMethod
{
	// in fc_contsants static $ARRIVE_TYPE_CAR=0;
	//in fc_contsants static $ARRIVE_TYPE_WALKUP=1;

	static $LOCATION_PAY_METHOD_IN_APP=1;
	static $LOCATION_PAY_METHOD_IN_STORE=2;
	
	
	static $LOCATION_PAY_METHOD_TAG="l_p_m";
	static $LOCATION_PAY_METHOD_ATTR="l_p_m_pm";
	static $LOCATION_PAY_METHOD_DESCR_ATTR="l_p_m_pmd";
	
	static $GET_PAY_METHODS_QUERY = "SELECT pay_type,pay_type_descr from  locations_allowed_pay_methods ";
	var $pay_type;
	var $pay_type_descr;

	function DoesPayMethodMatch($pay_method)
	{
		return $this->pay_type==$pay_method;	
	}
	
	static function ConvertPayMethodToString($pay_method)
	{
		switch($pay_method)
		{
			case LocationPayMethod::$LOCATION_PAY_METHOD_IN_APP:
				return "In-App"; // THis is going to need to go in the DB later
			case LocationPayMethod::$LOCATION_PAY_METHOD_IN_STORE:
				return "At Merchant";
			default:
				return "Unknown";
		}
	}
	
	static function s_IsPayAtLocation($method)
	{		
		return $method==LocationPayMethod::$LOCATION_PAY_METHOD_IN_STORE;
	}

	static function IsValidPaymentMethod($method)
	{
		if( ($method!=LocationPayMethod::$LOCATION_PAY_METHOD_IN_APP) && ($method!=LocationPayMethod::$LOCATION_PAY_METHOD_IN_STORE))
		{
			return false;
		}
		return true;
	}
	
	static function DB_GetAllowedPayTypesForLocationID($database, $location_id)
	{
		//print("DB_GetAllowedPayTypesForLocationID: {$location_id}");
		$result=array();

		$stmt = mysqli_stmt_init($database->getConnection());

		$query = LocationPayMethod::$GET_PAY_METHODS_QUERY . " WHERE location_id=?";
			
		mysqli_stmt_prepare($stmt,$query);
			
		mysqli_stmt_bind_param($stmt,'i',  $location_id);
		if(mysqli_stmt_execute($stmt))
		{
			mysqli_stmt_store_result($stmt);
		}
		else
		{
			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
					"DB_GetAllowedPayTypesForLocationID: Could not store result from SELECT (" . mysqli_stmt_error($stmt) . ")" );
			return NULL;
		}

		while(true)
		{
			$pay_type = new LocationPayMethod();
			if(!mysqli_stmt_bind_result($stmt,$pay_type->pay_type,$pay_type->pay_type_descr))
			{
				$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
						"DB_GetAllowedPayTypesForLocationID: Could not bind result (" . mysqli_stmt_error($stmt) . ")" );
				return result;
			}
			if( mysqli_stmt_fetch($stmt)!=true)
			{
					
				return $result;
			}
			else
			{
				//print("ADDING A PAY TYPE");
				
				$result[]=$pay_type;
			}
		}
		return $result; // Unreachable
	}

	function PrintAsXML()
	{
		print("<" . LocationPayMethod::$LOCATION_PAY_METHOD_TAG . " " . 
				LocationPayMethod::$LOCATION_PAY_METHOD_ATTR . "=\"" . rawurlencode($this->pay_type) . "\" " .
				LocationPayMethod::$LOCATION_PAY_METHOD_DESCR_ATTR . "=\"" . rawurlencode($this->pay_type_descr) . "\" " .
				"></" . LocationPayMethod::$LOCATION_PAY_METHOD_TAG . ">");
	}
}

class Location
	{
		var $valid;
		var $LocationID;
		var $LocationDescription;
		var $LocationAddress;
		var $LocationHours;
		var $LocationGPSLat;
		var $LocationGPSLong;
		var $LocationEmail;
		// Admin Email does not go to client. Maybe OM eventually
		var $LocationAdminEmail; // Where the invoices, receipts and payment info go (or do they go to the Group admin -- need a flag?)
		var $LocationTZ;
		var $LocationPhone;
		var $LocationOpenMode;
		var $LocationInstructions;
		var $LocationLongDescr;
		var $LocationMenuID;
		var $LocationShowPhone;
		var $LocationShowEmail;
		var $incarnation;
		var $LocationTTMNumber; // Talk To the Manager Address if any
		var $LocationSalesTaxRate; // Divided by 100
		var $LocationConvenienceFee; // For paying online (not for walkup orders)
		var $is_active;
		
		var $allowed_arrival_modes;
		var $allowed_pay_methods; // Pay In Store, In App etc.
		
		static $GET_LOCATION_QUERY = "SELECT location_id, location_descr, location_address, location_hours,location_gps_lat, location_gps_long, 
        						location_email, location_admin_email, location_tz, location_phone, location_open_mode, location_instructions, 
        						location_long_descr,location_menu_id,is_active, incarnation,location_show_phone, location_show_email, TTTMPhone, 
								location_sales_tax_rate, location_convenience_fee FROM locations ";
		
		// Location Attrs
		//static $USER_LOCATION_ID="location_id";
		static $LOCATION_UPDATED_TAG="loc_upd";
		static $USER_LOCATION_TAG="user_location";
		
		static $USER_LOCATION_PHONE_ATTR="l_p";
		static $USER_LOCATION_EMAIL_ATTR="l_e";
		static $USER_LOCATION_DESCRIPTION_ATTR="l_d";
		static $USER_LOCATION_ADDRESS_ATTR="l_a";
		static $USER_LOCATION_OPEN_MODE_ATTR="l_om";
		static $USER_LOCATION_HOURS_ATTR="l_h";
		static $USER_LOCATION_GPS_LAT_ATTR="lg_lat";
		static $USER_LOCATION_GPS_LONG_ATTR="l_glon";
		static $USER_LOCATION_TZ_ATTR="l_tz";
		static $USER_LOCATION_INSTRUCTIONS_ATTR="l_i";
		static $USER_LOCATION_LONG_DESCR_ATTR="l_lo_d";
		static $USER_LOCATION_MENU_ID_ATTR="l_m_id";
		static $USER_LOCATION_SHOW_PHONE_ATTR="l_sp";
		static $USER_LOCATION_SHOW_EMAIL_ATTR="l_se";
		static $USER_LOCATION_TTTM_ATTR="l_tttm";
		static $USER_LOCATION_SALES_TAX_RATE_ATTR="l_str";
		static $USER_LOCATION_CONV_FEE="l_cf";
		
		// ORDER LOCATION
		static $ORDER_LOCATION_TAG="o_location";
		static $ORDER_LOCATION_DESCRIPTION_ATTR="order_location_description";
		static $ORDER_LOCATION_ADDRESS_ATTR="order_location_address";
		static $ORDER_LOCATION_HOURS_ATTR="order_location_hours";
		static $ORDER_LOCATION_GPS_LAT_ATTR="order_location_gps_lat";
		static $ORDER_LOCATION_GPS_LONG_ATTR="order_location_gps_long";
		static $ORDER_LOCATION_PHONE_ATTR="order_location_phone";
		static $ORDER_LOCATION_EMAIL_ATTR="order_location_email";
		static $ORDER_LOCATION_INSTRUCTIONS_ATTR="order_location_instructions";
		static $ORDER_LOCATION_LONG_DESCR_ATTR="order_location_long_descr";
		static $ORDER_LOCATION_MENU_ID_ATTR="ol_m_id";
		static $ORDER_LOCATION_TZ_ATTR="ol_tz";
		static $ORDER_LOCATION_OPEN_MODE_ATTR="ol_om";
		
		
		static $NONE_LOCATION_ID=0;
		
		public static function DB_GetLocationInfo($database,$location_id)
		{
			$stmt = mysqli_stmt_init($database->getConnection());
        
			$query = Location::$GET_LOCATION_QUERY . " where location_id=?";
			
			//print("Q: {$query}");
			
        	mysqli_stmt_prepare($stmt,$query);
        	
        	mysqli_stmt_bind_param($stmt,'i',  $location_id);
        	if(mysqli_stmt_execute($stmt))
			{
				mysqli_stmt_store_result($stmt);
			}
			else
			{
        	    $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
        	    		"DB_GetLocationInfo: Could not store result from GetLocationInfo SELECT (" . mysqli_stmt_error($stmt) . ")" );
           	 	return NULL;
        	}
		
        	$location_data = $database->GetNextLocations($stmt);
        	
        	
       		mysqli_stmt_close($stmt);
       		
        	return $location_data;              
    	}
                             
    	
    	function Location()
    	{
    		$this->valid=false;
    		$this->allowed_arrival_modes=array();
    		$this->allowed_pay_methods=array();
    	}
		
    	static function isNoneLocationID($location_id)
    	{
    		return $location_id==Location::$NONE_LOCATION_ID;
    	}
    	
    	function isNoneLocation()
    	{
    		if($this->LocationID==Location::$NONE_LOCATION_ID)
    		{
    			return true;
    		}
    		return false;
    	}
    	
    	function IsArrivalModeValid($arrive_mode)
    	{
    		foreach($this->allowed_arrival_modes as $mode)
    		{
    			if($mode->DoesArriveModeMatch($arrive_mode)==true)
    			{
    				return true;
    			}
    		}
    		return false;
    	}
    	
    	function IsPaymentMethodValid($pay_method)
    	{
    		foreach($this->allowed_pay_methods as $meth)
    		{
    			if($meth->DoesPayMethodMatch($pay_method)==true)
    			{
    				return true;
    			}
    		}
    		return false;
    	}
    	
    	function IsOrderTaxable($arrive_mode)
    	{
    		return true;
    	}
    	
    	function GetConvenienceFeeForPayMethod($method)
    	{
    		if($method == LocationPayMethod::$LOCATION_PAY_METHOD_IN_STORE)
    		{
    			return "0.00";
    		}
    		else
    		{
    			return $this->LocationConvenienceFee; // In-App
    		}
    	}
    	function CalculateSalesTax($amount,$arrive_mode)
    	{
    		//global $database;
    		if($this->IsOrderTaxable($arrive_mode)!=true)
    		{
    			return "0.00";
    		}
    		if(bccomp($this->LocationSalesTaxRate,"0.000000")==0)
    		{
    			return "0.00";
    		}
    		
    		$tax = bcmul($amount,$this->LocationSalesTaxRate,10);
    		
    		// NICKA There has to be a better way. We know both a +ve nums
    		// Say we have 1.23112345 for tax. This should round to 1.24
    		// Take trunc_three (1.231) - trunc_two (1.23) and if its non-zero then add 0.01 !!
    		$tax_trunc_three = bcadd($tax,"0.000000",3);
    		$tax_trunc_two = bcadd($tax,"0.000000",2);
    		
    		//print("Amount:" . $amount ."\n");
    		
    		//print("Tax Before:" . $tax . "\n");
    		//print("Trunc3: " . $tax_trunc_three . "\n");
    		//print("Trunc2: " . $tax_trunc_two . "\n");
    		$compare_amount = bcsub($tax_trunc_three,$tax_trunc_two,3);
    		
    		if(bccomp($compare_amount,"0.000")==1)
    		{
    			bcadd($tax,"0.01",2);
    			//print("Tax After:" . $tax . "\n");
    		}
    		else
    		{
    			$tax = bcadd($tax,"0.00",2); // Make sure its just 2 decimal points
    		}
    		return $tax;
    		
    	}
		function PrintAsXML()
		{
			
			print("<" . Location::$USER_LOCATION_TAG ." id=\"" . rawurlencode($this->LocationID) . "\" " .
					Location::$USER_LOCATION_DESCRIPTION_ATTR . "=\"" . rawurlencode($this->LocationDescription) . "\" " .
					Location::$USER_LOCATION_ADDRESS_ATTR ."=\"" . rawurlencode($this->LocationAddress) . "\" " .
					Location::$USER_LOCATION_HOURS_ATTR ."=\"" . rawurlencode($this->LocationHours) . "\" " .
					Location::$USER_LOCATION_GPS_LAT_ATTR ."=\"" . rawurlencode($this->LocationGPSLat) . "\" " .
					Location::$USER_LOCATION_GPS_LONG_ATTR ."=\"" . rawurlencode($this->LocationGPSLong) . "\" " .
					Location::$USER_LOCATION_PHONE_ATTR . "=\"" . rawurlencode($this->LocationPhone) . "\" " .
					Location::$USER_LOCATION_EMAIL_ATTR . "=\"" . rawurlencode($this->LocationEmail) . "\" " .
					Location::$USER_LOCATION_INSTRUCTIONS_ATTR . "=\"" . rawurlencode($this->LocationInstructions) . "\" " .
					Location::$USER_LOCATION_LONG_DESCR_ATTR . "=\"" . rawurlencode($this->LocationLongDescr) . "\" " .
					Location::$USER_LOCATION_TZ_ATTR . "=\"" . rawurlencode($this->LocationTZ) . "\" " .
					Location::$USER_LOCATION_OPEN_MODE_ATTR . "=\"" . rawurlencode($this->LocationOpenMode) . "\" " .
					Location::$USER_LOCATION_MENU_ID_ATTR . "=\"" . rawurlencode($this->LocationMenuID) . "\" " .
					Location::$USER_LOCATION_TTTM_ATTR . "=\"" . rawurlencode($this->LocationTTMNumber) . "\" " .
					IS_ACTIVE_ATTR . "=\"" . rawurlencode($this->is_active) . "\" " .
					INCARNATION_ATTR . "=\"" . rawurlencode($this->incarnation) . "\" " .
					Location::$USER_LOCATION_SALES_TAX_RATE_ATTR . "=\"" . rawurlencode($this->LocationSalesTaxRate) . "\" " .
					Location::$USER_LOCATION_CONV_FEE . "=\"" . rawurlencode($this->LocationConvenienceFee) . "\" " );
			
			
			
			if($this->LocationShowPhone==0)
			{
				print(Location::$USER_LOCATION_SHOW_PHONE_ATTR . "=\"" . rawurlencode($this->LocationShowPhone) . "\" " );
			}
			if($this->LocationShowEmail==0)
			{
				print(Location::$USER_LOCATION_SHOW_EMAIL_ATTR . "=\"" . rawurlencode($this->LocationShowEmail) . "\" " );
			}
			
			print(">"); // Close the attributes section. All tags now ...
			foreach($this->allowed_arrival_modes as $arrival)
			{
				$arrival->PrintAsXML();
			}
			foreach($this->allowed_pay_methods as $pay_method)
			{
				$pay_method->PrintAsXML();
			}		
			print("</" . Location::$USER_LOCATION_TAG .">");
		}
		
		function PrintAsOrderLocationXML()
		{
			
			print("<" . Location::$ORDER_LOCATION_TAG .  " id=\"" . rawurlencode($this->LocationID) . "\" " .
					Location::$ORDER_LOCATION_DESCRIPTION_ATTR . "=\"" . rawurlencode($this->LocationDescription) . "\" " .
					Location::$ORDER_LOCATION_ADDRESS_ATTR . "=\"" . rawurlencode($this->LocationAddress) . "\" " .
					Location::$ORDER_LOCATION_HOURS_ATTR . "=\"" . rawurlencode($this->LocationHours) . "\" " .
					Location::$ORDER_LOCATION_GPS_LAT_ATTR . "=\"" . rawurlencode($this->LocationGPSLat) . "\" " .
					Location::$ORDER_LOCATION_GPS_LONG_ATTR . "=\"" . rawurlencode($this->LocationGPSLong) . "\" " .
					Location::$ORDER_LOCATION_PHONE_ATTR . "=\"" . rawurlencode($this->LocationPhone) . "\" " .
					Location::$ORDER_LOCATION_EMAIL_ATTR . "=\"" . rawurlencode($this->LocationEmail) . "\" " .
					Location::$ORDER_LOCATION_INSTRUCTIONS_ATTR . "=\"" . rawurlencode($this->LocationInstructions) . "\" " .
					Location::$ORDER_LOCATION_LONG_DESCR_ATTR . "=\"" . rawurlencode($this->LocationLongDescr) . "\" " .
					Location::$ORDER_LOCATION_TZ_ATTR . "=\"" . rawurlencode($this->LocationTZ) . "\" " .
					Location::$ORDER_LOCATION_OPEN_MODE_ATTR . "=\"" . rawurlencode($this->LocationOpenMode) . "\" " .
					Location::$ORDER_LOCATION_MENU_ID_ATTR . "=\"" . rawurlencode($this->LocationMenuID). "\" " .
					IS_ACTIVE_ATTR . "=\"" . rawurlencode($this->is_active) . "\" " .
					INCARNATION_ATTR . "=\"" . rawurlencode($this->incarnation) . "\" " .
					">" . "</"  . Location::$ORDER_LOCATION_TAG .">");
			
		}
	}
?>
