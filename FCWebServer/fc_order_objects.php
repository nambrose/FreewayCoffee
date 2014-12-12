<?php
	
    /* Nick Ambrose
     * 
     * (C) Copyright Freeway Coffee, 2011,2012
     */

	require_once ("fc_constants.php");
	
	class OrderArrivedData
	{
		var $location_id;
		var $order_id;
		var $time_here;
		
		function PrintAsXML()
		{
			print("<" . OM_USER_TIME_HERE_TAG . " " .
					OM_USER_TIME_HERE_LOC_ID_ATTR . "=\"" . rawurlencode($this->location_id)  . "\" " .
					OM_USER_TIME_HERE_ORDER_ID_ATTR . "=\"" . rawurlencode($this->order_id) . "\" " .
					OM_USER_TIME_HERE_TIME_HERE_ATTR . "=\"" . rawurlencode($this->time_here) . "\" " );
				
			print ("></" . OM_USER_TIME_HERE_TAG . ">");
		}
	}
	
	class OrderItemOption
	{
		var $orders_item_options_id;
		var $orders_item_options_order_id;
		var $orders_item_options_order_item_id;
		var $orders_item_options_drink_type_option_id;
		var $orders_item_options_option_count;
		var $orders_item_options_orig_cost_per;
		
		var $option_name; // From Join
		var $option_group; // From Join
		static $csv_columns = array("Order Item Option ID", "Order ID","Order Item ID","Drink Type Option ID","Option Count", "Option Cost Per","Option Name",
				"Option Group");
		
		static function PrintCSVHeaders($output_file)
		{
			return fputcsv($output_file,OrderItemOption::$csv_columns);
		}
		function PrintAsCSV($output_file)
		{
			$csv_data=array();
			$csv_data[]=$this->orders_item_options_id;
			$csv_data[]=$this->orders_item_options_order_id;
			$csv_data[]=$this->orders_item_options_order_item_id;
			$csv_data[]=$this->orders_item_options_drink_type_option_id;
			$csv_data[]=$this->orders_item_options_option_count;
			$csv_data[]=$this->orders_item_options_orig_cost_per;
			$csv_data[]=$this->option_name;
			$csv_data[]=$this->option_group;
			fputcsv($output_file,$csv_data);
				
		}
		
	}
	class OrderItem
	{
		var $order_item_id;
		var $order_id;
		var $user_id;

		var $drink_type_id;
		var $drink_type_name; // Computed via join
		
		var $item_extra;
		var $item_name;
		
		var $order_item_descr;
		var $order_item_cost;
		var $item_type;
		
		
		
		static $csv_columns = array("Order Item ID", "Order ID","User ID","Item Type ID","Item Type Name", "Item Extra", "Item Name",
									"Order Item Descr","Order Item Cost","Item Type");
				
		static function PrintCSVHeaders($output_file)
		{
			return fputcsv($output_file,OrderItem::$csv_columns);
		}
		function PrintAsCSV($output_file)
		{
			$csv_data=array();
			$csv_data[]=$this->order_item_id;
			$csv_data[]=$this->order_id;
			$csv_data[]=$this->user_id;
			$csv_data[]=$this->drink_type_id;
			$csv_data[]=$this->drink_type_name;
			$csv_data[]=$this->item_extra;
			$csv_data[]=$this->item_name;
			$csv_data[]=$this->order_item_descr;
			$csv_data[]=$this->order_item_cost;
			$csv_data[]=$this->item_type;
			fputcsv($output_file,$csv_data);
			
		}
		
		function PrintAsXML()
		{
			print("<" . OM_ORDER_ITEM_TAG . " id=\"" . rawurlencode($this->order_item_id)  . "\" " .
					OM_ORDER_ITEM_DESCR_ATTR . "=\"" . rawurlencode($this->order_item_descr) . "\" " .
					OM_ORDER_ITEM_COST_ATTR . "=\"" . rawurlencode($this->order_item_cost) . "\" " );
					
			print ("></" . OM_ORDER_ITEM_TAG . ">");
		}
		
		
	}
	
	
	class Order
	{
		var $orders_id;
		var $orders_user_id;
		var $orders_start_time;
		
		var $orders_time_to_location;
		var $orders_end_time; // If in a "final" state (Refunded, Delivered, NoShow)
		var $orders_time_needed;
		
		// UTC from the DB
		var $orders_start_time_utc;
		var $orders_end_time_utc; // If in a "final" state (Refunded, Delivered, NoShow)
		var $orders_time_needed_utc;
		
		var $orders_disposition; // (Received, InProgress, Delivered, Refunded, NoShow)
		var $orders_disposition_text;
		
		var $orders_total_cost;
		var $orders_user_email;
		var $orders_user_name;
		
		var $orders_user_time_here;
		var $orders_location_id;
		
		var $incarnation;
		var $user_car_info;
		var $user_tag;
		
		var $orders_authorize_net_customer_profile_id; // May not be sent to client .. sensitive.
		var $last_modified;
		var $orders_start_date; // Not sent to client (maybe I should?) used to determine if an order is today or not.
								// Very irksome when server and client are in sep TZ's and your MySQL seems not to support them
		var $orders_user_is_demo;
		var $orders_client_type;
		var $orders_arrive_mode;
		
		var $orders_items_cost; // The basic items
		var $orders_discount; // Any Discounts for free drinks, 10% off etc 
		var $orders_tip_amount; // The tip !!!!
		var $orders_pay_method;
		
		var $global_order_tag; // IP Orders only
		
		var $order_is_taxable;
		var $order_taxable_amount;
		var $order_total_tax;
		var $order_tax_rate;
		var $order_convenience_fee;
		
		static $csv_columns = array("Order ID","User ID","Start Time","Time To Location","End Time",
									"Time Needed","Disposition ID", "Disposition Text","Total Cost",
									"User Email","User Name","User Time Here","Order Location ID","Car Info",
									"User Tag","Is Demo User","Client Type","Arrive Mode","Items Cost","Order Discount","Order Tip","Pay Method",
									"Taxable","Taxable Amount","Tax","Tax Rate","Convenience Fee");
		
		static function PrintCSVHeaders($output_file)
		{
			return fputcsv($output_file,Order::$csv_columns);
		}
		
		function PrintAsCSV($output_file)
		{
			$csv_data = array();
			$csv_data[]=$this->orders_id;
			$csv_data[]=$this->orders_user_id;
			$csv_data[]=$this->orders_start_time;
			$csv_data[]=$this->orders_time_to_location;
			$csv_data[]=$this->orders_end_time;
			$csv_data[]=$this->orders_time_needed;
			$csv_data[]=$this->orders_disposition;
			$csv_data[]=$this->orders_disposition_text;
			$csv_data[]=$this->orders_total_cost;
			$csv_data[]=$this->orders_user_email;
			$csv_data[]=$this->orders_user_name;
			$csv_data[]=$this->orders_user_time_here;
			$csv_data[]=$this->orders_location_id;
			$csv_data[]=$this->user_car_info;
			$csv_data[]=$this->user_tag;
			$csv_data[]=$this->orders_user_is_demo;
			$csv_data[]=$this->orders_client_type;
			$csv_data[]=$this->orders_arrive_mode;
			$csv_data[]=$this->orders_items_cost;
			$csv_data[]=$this->orders_discount;
			$csv_data[]=$this->orders_tip_amount;
			$csv_data[]=$this->orders_pay_method;
			$csv_data[]=$this->order_is_taxable;
			$csv_data[]=$this->order_taxable_amount;
			$csv_data[]=$this->order_total_tax;
			$csv_data[]=$this->order_tax_rate;
			$csv_data[]=$this->order_convenience_fee;
			fputcsv($output_file,$csv_data);
			
		}
		function PrintAsXML_NoClosingTag()
		{
			print("<" . ORDER_TAG . " id=\"" . rawurlencode($this->orders_id)  . "\" " .
					ORDER_USER_ID_ATTR . "=\"" . rawurlencode($this->orders_user_id) . "\" " .
					ORDER_START_TIME_ATTR . "=\"" . rawurlencode($this->orders_start_time) . "\" " .
					ORDER_TIME_TO_LOCATION_ATTR . "=\"" . rawurlencode($this->orders_time_to_location) . "\" " .
					ORDER_END_TIME_ATTR . "=\"" . rawurlencode($this->orders_end_time) . "\" " .
					ORDER_TIME_NEEDED_ATTR . "=\"" . rawurlencode($this->orders_time_needed) . "\" " .
					ORDER_DISPOSITION_ATTR  . "=\"" . rawurlencode($this->orders_disposition). "\" " .
					ORDER_DISPOSITION_TEXT_ATTR . "=\"" . rawurlencode($this->orders_disposition_text) . "\" " .
					ORDER_TOTAL_COST_ATTR . "=\"" . rawurlencode($this->orders_total_cost) . "\" " .
					ORDER_USER_EMAIL_ATTR . "=\"" . rawurlencode($this->orders_user_email) . "\" " .
					ORDER_USER_NAME_ATTR . "=\"" . rawurlencode($this->orders_user_name) . "\" " .
					ORDER_USER_TIME_HERE_ATTR . "=\"" . rawurlencode($this->orders_user_time_here) . "\" " .
					ORDER_LOCATION_ID_ATTR . "=\"" . rawurlencode($this->orders_location_id) . "\" " .
					INCARNATION_ATTR . "=\"" . rawurlencode($this->incarnation) . "\" " .
					ORDER_USER_CAR_INFO. "=\"" . rawurlencode($this->user_car_info)  . "\" " .
					ORDER_USER_TAG. "=\"" . rawurlencode($this->user_tag) . "\" " .
					ORDER_USER_IS_DEMO_ATTR. "=\"" . rawurlencode($this->orders_user_is_demo) . "\" " .
					ORDER_USER_CLIENT_TYPE_ATTR. "=\"" . rawurlencode($this->orders_client_type) . "\" " .
					ORDER_ARRIVE_MODE_ATTR. "=\"" . rawurlencode($this->orders_arrive_mode) . "\" " .
					ORDER_ITEMS_TOTAL_ATTR. "=\"" . rawurlencode($this->orders_items_cost) . "\" " .
					ORDER_DISCOUNT_ATTR. "=\"" . rawurlencode($this->orders_discount) . "\" " .
					ORDER_TIP_ATTR. "=\"" . rawurlencode($this->orders_tip_amount) . "\" " .
					ORDER_PAY_METHOD_ATTR. "=\"" . rawurlencode($this->orders_pay_method) . "\" " .
					ORDER_GLOBAL_ORDER_TAG_ATTR . "=\"" . rawurlencode($this->global_order_tag)  . "\" " .
					ORDER_IS_TAXABLE_ATTR . "=\"" . rawurlencode($this->order_is_taxable)  . "\" " .
					ORDER_TAXABALE_AMOUNT_ATTR . "=\"" . rawurlencode($this->order_taxable_amount)  . "\" " .
					ORDER_TAX . "=\"" . rawurlencode($this->order_total_tax)  . "\" " .
					ORDER_TAX_RATE . "=\"" . rawurlencode($this->order_tax_rate)  . "\" " .
					ORDER_CONV_FEE_ATTR . "=\"" . rawurlencode($this->order_convenience_fee)  . "\" " .
					
					">");
		}
		
		function PrintClosingTagAsXML()
		{
			print ("</" . ORDER_TAG . ">");
		}
		
		function ConvertUTCTimesToUserTZ()
		{	
			global $session;
			global $database;
			$UtcTZ = new DateTimeZone("UTC");
			$UserTZ = new DateTimeZone($session->user_data->UserTZ);
			if(is_null($session->user_data->UserTZ) || strlen($session->user_data->UserTZ)==0)
			{
				$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_FAIL,
						"ConvertUTCTimesToUserTZ::TZ EMPTY. FAILING UserInfo. UserInfo: " . var_export($session->user_data,true) . " Order Info: " . var_export($this,true));
				return false;
			}
			
			// Start Time
			$start_t = new DateTime($this->orders_start_time_utc,$UtcTZ);
			$start_t->setTimezone($UserTZ);
			$this->orders_start_time=$start_t->format(DEFAULT_DATE_TIME_FORMAT);
			$this->orders_start_date = $start_t->format(DEFAULT_DATE_ONLY_FORMAT); // Used by OM to get todays orders
			
			// End Time
			if($this->orders_end_time_utc!=NULL)
			{
				$end_t = new DateTime($this->orders_end_time_utc,$UtcTZ);
				$end_t->setTimezone($UserTZ);
				$this->orders_end_time=$end_t->format(DEFAULT_DATE_TIME_FORMAT);
			}
			
			// Time Needed
			$time_needed = new DateTime($this->orders_time_needed_utc,$UtcTZ);
			$time_needed->setTimezone($UserTZ);
			$this->orders_time_needed=$time_needed->format(DEFAULT_DATE_TIME_FORMAT);
		}
		
				
		function MatchesDate($date_input)
		{
			if(strcmp($this->orders_start_date,$date_input)==0)
			{
				return true;
			}
			return false;
			
		}
		
		function IsDemoOrder()
		{
			if($this->orders_user_is_demo==0)
			{
				return false;
			}
			return true;
		}
		function SetDispositionTextFromCurrentID()
		{
			$this->orders_disposition_text=$this->GetDispositionTextFromID($this->orders_disposition);
		}
		
		function SetDisposition($disposition)
		{
			$this->orders_disposition=$disposition;
			$this->orders_disposition_text=$this->GetDispositionTextFromID($disposition);
		}
		
		function IsPayAtLocation()
		{
			return LocationPayMethod::s_IsPayAtLocation($this->orders_pay_method);
		}
		
		function GetDispositionTextFromID($disposition)
		{
			switch($disposition)
			{
				case ORDER_RECEIVED:
					return "Received";
				case ORDER_DELIVERED:
					return "Delivered";
				case ORDER_REFUNDED:
					return "Refunded";
				case ORDER_INPROGRESS:
					return "In Progress";
				case ORDER_NOSHOW:
					return "No Show";
				case ORDER_CANCELLED:
					return "Cancelled"; // For Refunds of Pay@ location orders
				default:
					return "Unknown";
			}
					
		}
	}
	
?>