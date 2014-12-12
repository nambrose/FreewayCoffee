<?php
    /**
     * fc_om_orders_logic.php
     * 
     *
     *
     * (C) Copyright Freeway Coffee, 2012,2013
     */
    require_once("fc_constants.php");
	require_once("AuthorizeNet.php");
    require_once("fc_make_model_color_logic.php");
    require_once ("fc_error.php");
    require_once("fc_credit_card.php");
    
    class OM_OrdersLogic
    {
    
    	function GetTodaysOrdersForLocation($location_id)
    	{
    		global $database;
    		global $order_database;
    		global $session;
    		
    		$statement = $order_database->OpenGetInProcessOrdersForTodayForLocation($location_id);
    		
    		return $this->GetOrdersByQueryStatement_today($location_id,$statement);
 	 	}
 	 	
    	function GetOrdersAfterOrderIDForLocation($location_id,$order_id)
    	{
    		global $database;
    		global $order_database;
    		global $session;
    	
 	  		$statement = $order_database->OpenGetOrdersAfterOrderIDForLocation($location_id,$order_id);
 	  		
    		return $this->GetOrdersByQueryStatement($location_id,$statement);	
    	}
    	
    	function MakeReport($num_days)
    	{
    		global $database;
    		global $session;
    		global $order_database;
    		global $mailer;
    		
    		$location=1; /// WOW !!!!!!
    		if(is_null($num_days))
    		{
    			return false;
    		}
    		
    		if(!is_numeric($num_days))
    		{
    			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    					"MakeReport: Days Not Numeric {$num_days} ");
    			return false;
    		}
    		if($num_days==0)
    		{
    			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    					"MakeReport: Num Days Cannot be Zero ");
    			return false;
    		}
    		
    		// Open Orders, Order Items and Order Item Options Files
    		$date_part = date(DEFAULT_DATE_ONLY_FORMAT);
    		$time_part = time();
    		$rand_part = mt_rand(0,10000);
    		
    		$filename_base="report_" . $_SESSION['user_name'] . "_" . $_SESSION['user_id'] . "_" . 
    						$date_part . "_" . $time_part . "_" . $rand_part;

    		$directory = "./reports/" . $filename_base ."/";
    		

    		$day_start_string="";
    		$day_start_date_time=NULL;
    		$day_start_date_time_user=NULL;
    		$day_end_string="";
    		$day_end_date_time=NULL;
    		$day_end_date_time_user=NULL;
    		
    		$order_database->CalculateDatesForDayRange($num_days,$day_start_string,$day_start_date_time,$day_start_date_time_user,
    													$day_end_string,$day_end_date_time,$day_end_date_time_user);
    		
    		//print("Start Date: {$day_start_string}, End Date: {$day_end_string}");
    		
    		//."-";
    		$filename_meta =  $filename_base . "_meta.txt";
    		
    		$filename_orders =  $filename_base . "_orders.csv";
    		$filename_orders_items =   $filename_base . "_orders_items.csv";
    		$filename_orders_items_options =   $filename_base . "_orders_items_options.csv";
    		$filename_orders_credit_cards = $filename_base . "_orders_credit_cards.csv";
    		
    		
    		if(mkdir($directory,0700,true)!=true) // Owner all, no one else
    		{
    			$error = error_get_last();
    			 
    			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    					"MakeReport:  Cannot Create Directory: {$directory}, Error: " .$error['message']);
    			return false;
    		}
    		
    		//print ("Files: Base: {$filename_base}, Meta:{$filename_meta} {Orders: {$filename_orders}, Items: {$filename_orders_items}, Options: {$filename_orders_items}");

    		$file_meta = fopen($directory . $filename_meta,"w");
    		if($file_meta===FALSE)
    		{
    			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    					"MakeReport:  Cannot Open Report File: {$filename_meta}");
    			return false;
    		}
    		
    		$file_orders = fopen($directory . $filename_orders,"w");
    		if($file_orders===FALSE)
    		{
    			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    					"MakeReport:  Cannot Open Report File: {$filename_orders}");
    			return false;
    		}
    		$file_orders_items = fopen($directory . $filename_orders_items,"w");
    		if($file_orders_items===FALSE)
    		{
    			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    					"MakeReport:  Cannot Open Report File: {$filename_orders_items}");
    			return false;
    		}
    		
    		$file_orders_items_options = fopen($directory . $filename_orders_items_options,"w");
    		if($file_orders_items_options===FALSE)
    		{
    			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    					"MakeReport:  Cannot Open Report File: {$filename_orders_items_options}");
    			return false;
    		}
    		$file_orders_credit_cards = fopen($directory . $filename_orders_credit_cards,"w");
    		if($file_orders_credit_cards===FALSE)
    		{
    			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    					"MakeReport:  Cannot Open Report File: {$filename_orders_credit_cards}");
    			return false;
    		}
    		
    		
    		// Print headers to each file
    		Order::PrintCSVHeaders($file_orders);
    		OrderItem::PrintCSVHeaders($file_orders_items);
    		OrderItemOption::PrintCSVHeaders($file_orders_items_options);
    		OrderCreditCard::PrintCSVHeaders($file_orders_credit_cards);
    		
    		// Open the Orders for Date Range
    		$ord_stmt = $order_database->OpenGetOrdersForDateTimeRange($location,$day_start_string,$day_end_string);
    		
    		// While Order
    		$order = $order_database->GetNextOrderByStatement($ord_stmt);
    		$total_orders=0;
    		$total_order_items=0;
    		$total_order_amount=0.00;
    		$order_item_option_count=0;
    		while($order!=null)
    		{
    			//printf("Order: {$order->orders_id}");
    			$total_orders++;
    			$total_order_amount = bcadd($total_order_amount,$order->orders_total_cost,10);
    			// Print Order As CSV
    			$order->PrintAsCSV($file_orders);
    			
    			// Credit card
    			if($order->IsPayAtLocation()!=true)
    			{
    				$order_credit_card = $order_database->GetFirstOrderCreditCard($order->orders_id,false);
    				if($order_credit_card==NULL)
    				{
    					$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    							"Report: No Order Credit Card For Order : " . $order->orders_id  );
    				}
    				else
    				{
    					$order_credit_card->PrintAsCSV($file_orders_credit_cards);
    				}
    			}
    			
    			// Print All Order Items As CSV
    			$item_stmt = $order_database->OpenGetItemsForOrderFull($order->orders_id);
    			$order_item = $order_database->GetNextItemsForOrderFull($item_stmt);
    			while($order_item!=NULL)
    			{
    				$total_order_items++;
    				// Print All Order Item Options As CSV
    				$order_item->PrintAsCSV($file_orders_items);
    				
    				// Items Options
    				$item_option_stmt = $order_database->OpenGetItemsOptionsForOrder($order->orders_id,$order_item->order_item_id);
    				
    				$order_item_option = $order_database->GetNextItemsOptionsForOrder($item_option_stmt);
    				while($order_item_option!=NULL)
    				{
    					$order_item_option_count++;
    					$order_item_option->PrintAsCSV($file_orders_items_options);
    					$order_item_option = $order_database->GetNextItemsOptionsForOrder($item_option_stmt);
    				}
    				$order_database->CloseGetItemsOptionsForOrder($item_option_stmt);
    				
    				// End Items Options
    				$order_item = $order_database->GetNextItemsForOrderFull($item_stmt);
    			}
    			$order_database->CloseGetItemsForOrder($item_stmt);
    			
    			
    			$order = $order_database->GetNextOrderByStatement($ord_stmt);
    			
    		}
    		$order_database->CloseGetOrderBy($ord_stmt);
    		
    		fclose($file_orders);
    		fclose($file_orders_items);
    		fclose($file_orders_items_options);
    		fclose($file_orders_credit_cards);
   		
    		$meta_string = "Report Generated on: {$date_part}\n" 
    					. "Directory: {$filename_base}\n"
    					. "\nFiles Generated:\n{$filename_meta}\n{$filename_orders}\n{$filename_orders_items}\n{$filename_orders_items_options}\n"
    					. "{$filename_orders_credit_cards}\n"
    					. "\n";
    		if(is_null($location))
    		{
    			$meta_string .="Location ID: ALL\n";
    		}
    		else
    		{
    			$meta_string .= "Location ID: {$location}\n";
    		}
    					
    		$meta_string .=	 "Date/Time Range(UTC): From: {$day_start_string} to {$day_end_string}\n"
    						. "Date/Time Range(" . $session->user_data->UserTZ .")"
    						." From: " . $day_start_date_time_user->format(DEFAULT_DATE_TIME_FORMAT) 
    						. " To: " . $day_end_date_time_user->format(DEFAULT_DATE_TIME_FORMAT) ."\n"
    						. "Total Orders: {$total_orders}\n"
    						. "Total Order Items: {$total_order_items}\n"
    						. "Total Order Items Options: {$order_item_option_count}\n"
    						. "Total Order Amount:$"  . number_format($total_order_amount,2);
    		
    		
    		fprintf($file_meta,"%s",$meta_string);
    		fclose($file_meta);
    		
    		// Send Email
    		$mailer->SendReportComplete($_SESSION['user_name'],$_SESSION['user_email'],$directory,$meta_string);
    		
    	}
    	
    	/*
    	function GetOrdersAfterTimeStampForLocation($location_id,$timestamp)
    	{
    		global $database;
    		global $order_database;
    		global $session;
    		 
    		
    		$statement = $order_database->OpenGetInProgressOrdersModifiedAfterTimestampForLocation($location_id,$timestamp);
    	  		
    		return $this->GetOrdersByQueryStatement($location_id,$statement);
    	}
    	*/
    	function UpdateLocationOpenMode($location_id,$new_mode)
    	{
    		global $database;
    		global $session;
    		global $xml_helper;
    		if($database->UpdateLocationOpenMode($location_id,$new_mode)==true)
    		{
    			// NOTE: Assuming that the user == current user here which wont work forever. Update Session user location
    			// TODO FIXME TODO TODO TODO
    			$session->user_location= Location::DB_GetLocationInfo($database,$location_id);
    			
    			$xml_helper->PrintUpdatedLocationSuccess($session->user_location);
    			
    		}
    		else
    		{
    			$xml_helper->PrintUpdatedLocationFail();
    		}
    	}
    	function GetOrdersAfterTagForLocation($location_id,$global_order_tag)
    	{
    		global $database;
    		global $order_database;
    		global $session;
    		 
    	
    		$statement = $order_database->OpenGetInProgressOrdersModifiedAfterTagForLocation($location_id,$global_order_tag);
    			
    		return $this->GetOrdersByQueryStatement($location_id,$statement);
    	}
    	
    	function GetOrdersByQueryStatement_today($location_id,$statement)
    	{
    		global $database;
    		global $order_database;
    		global $session;
    		$order = $order_database->GetNextOrderByStatement($statement);
    		//var_dump($order);
    		
    		print("<" . ORDER_LIST_TAG);
    	
    		if($order==NULL)
    		{
    			/*
    			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    					"GetOrdersByQueryStatement: No Orders for Location: {$location_id} ");
    			 */
    		}
    		else
    		{
    			// NOT NOW print( " " . ORDER_GLOBAL_ORDER_TAG_ATTR . "=\"" . rawurlencode($order->global_order_tag) ."\" " );
    		}
    		print(">"); // End Attributes
    		
    		$UserTZ = new DateTimeZone($session->user_data->UserTZ);
    			
    		$now_time = new DateTime();
    		$now_time->setTimezone($UserTZ);
    		$now_time_str = $now_time->format(DEFAULT_DATE_ONLY_FORMAT);
    		
    		
    		//var_dump ($order);
    		while($order!=NULL)
    		{
    			//if($order->MatchesDate($now_time_str)==true)
    			//{
    			
    				$order->PrintAsXML_NoClosingTag();
    				if($order->IsPayAtLocation()!=true)
    				{
    					$order_credit_card = $order_database->GetFirstOrderCreditCard($order->orders_id,false);
    					if($order_credit_card==NULL)
    					{
    						$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    								"GetOrdersByQueryStatement: No Order Credit Card For Order : " . $order->orders_id  );
    					}
    					else
    					{
    						$order_credit_card->PrintAsXML();
    					}
    				}
    				$this->PrintItemsForOrderAsXML($order);
    				//$this->PrintFoodItemsForOrderAsXML($order->orders_id);
    				$order->PrintClosingTagAsXML();
    			 
    				
    			//}
    			$order = $order_database->GetNextOrderByStatement($statement);
    			
    		}
    		print("</" . ORDER_LIST_TAG . ">");
    		return true;
    	}
    	function GetOrdersByQueryStatement($location_id,$statement)
    	{
    		global $database;
    		global $order_database;
    		global $session;
    		$order = $order_database->GetNextOrderByStatement($statement);
    		
    		print("<" . ORDER_LIST_TAG . " ");
    		
    		if($order==NULL)
    		{
    			/*
    			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    								"GetOrdersByQueryStatement: No Orders for Location: {$location_id} ");
    			*/
    		}
    		
    		else
    		{
    			print( " " . OM_ORDER_HIGHEST_TAG_ATTR . "=\"" . rawurlencode($order->global_order_tag) ."\"" );
    		}
    		print(">"); // End Attributes
    		
    		while($order!=NULL)
    		{
    			$this->PrintOrderAndItemsForOrder($order);
 	 			$order = $order_database->GetNextOrderByStatement($statement);
    		}
    		print("</" . ORDER_LIST_TAG . ">");
    		return true;
    	}
    	
    	function PrintItemsForOrderAsXML(Order $order)
    	{
    		global $database;
    		global $order_database;
    		global $session;
    		
    		$stmt = $order_database->OpenGetItemsForOrderShort($order->orders_id,$order->orders_location_id);
    		if($stmt==NULL)
    		{
    			return;
    		}
    		
    		$order_item = $order_database->GetNextItemsForOrderShort($stmt);
    		print("<" . OM_ORDER_ITEM_LIST_TAG . ">");
    		while($order_item!=NULL)
    		{
    			$order_item->PrintAsXML();
    			$order_item = $order_database->GetNextItemsForOrderShort($stmt);
    		}

    		print("</" . OM_ORDER_ITEM_LIST_TAG . ">");
    		$order_database->CloseGetItemsForOrder($stmt);
    	}
    
    	
		function GetAndPrintOrderAndItemsForOrderIDForLocation($order_id,$location_id)
		{
			global $order_database;
			$order = $order_database->GetOrderByOrderIDForLocation($order_id,$location_id);
			if(is_null($order))
			{
				return false;
			}
			return $this->PrintOrderAndItemsForOrder($order);
		}
		
		function PrintOrderAndItemsForOrder(Order $order)
		{
			global $order_database;
			global $database;
			
			$order->PrintAsXML_NoClosingTag();
			if($order->IsPayAtLocation()!=true)
			{
				$order_credit_card = $order_database->GetFirstOrderCreditCard($order->orders_id,false);
				if($order_credit_card==NULL)
				{
					$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
							"PrintOrderAndItemsForOrder: No Order Credit Card For Order : " . $order->orders_id );
				}
				else
				{	
					$order_credit_card->PrintAsXML();
				}
			}
			 
			$this->PrintItemsForOrderAsXML($order);
			 
			$order->PrintClosingTagAsXML();
		}
		
    	function DoDeliverInProgressOrder($order_id,$location_id,$incarnation)
    	{
    		global $order_database;
    		$retval = $this->UpdateInProgressOrderDisposition($order_id,$location_id,$incarnation,ORDER_DELIVERED);
    		return $retval;
    	}
    	
    	function DoRefundInProgressOrder($order_id,$location_id,$incarnation)
    	{
    		global $order_database;
    		global $database;
    		global $xml_helper;
    		$error_object = new FCError();
    		
    		
    		$err_str = "RefundOrder: Internal Error (Unknown) For Order : " . $order->orders_id;
    		$error_object->DisplayText = "";
    		$error_object->ErrorCodeMajor = ERROR_MAJOR_INTERNAL;
    		$error_object->ErrorCodeMinor = ERROR_MINOR_STATE_NOT_FOUND;
    		$error_object->DisplayText=$err_str;
    		$error_object->LongText=$err_str;
    		
    		$order = $order_database->GetOrderByOrderIDAndTypeForLocation(ORDER_TYPE_IN_PROGRESS,$order_id,$location_id);
    		if(is_null($order))
    		{
    			$err_str = "Refund Order, Could not find a matching Order for ID: {$order_id}";
    			$error_object->DisplayText = "";
    			$error_object->ErrorCodeMajor = ERROR_MAJOR_INTERNAL;
    			$error_object->ErrorCodeMinor = ERROR_MINOR_STATE_NOT_FOUND;
    			$error_object->DisplayText=$err_str;
    			$error_object->LongText=$err_str;
    			
    			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    					$err_str ."Incarnation: {$incarnation}");
    			$xml_helper->PrintOrderUpdateFail($order_id,$error_object);
    			return false;
    		}
    		
    		if($order->IsPayAtLocation())
    		{
    			$err_str = "Refund Order, Order was paid at location. Cannot refund. Cancel or NoShow this order: {$order_id}";
    			$error_object->DisplayText = "";
    			$error_object->ErrorCodeMajor = ERROR_MAJOR_INTERNAL;
    			$error_object->ErrorCodeMinor = ERROR_MINOR_STATE_NOT_FOUND;
    			$error_object->DisplayText=$err_str;
    			$error_object->LongText=$err_str;
    			 
    			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    					$err_str ."Incarnation: {$incarnation}");
    			$xml_helper->PrintOrderUpdateFail($order_id,$error_object);
    			return false;
    		}
    		
    		$retval =  $order_database->RefundInProgressOrder($order,$location_id,$incarnation,$error_object);
    		
    		if($retval==true)
    		{
    			$xml_helper->PrintOrderUpdateSuccessStart($order_id);
    			print("<" . ORDER_LIST_TAG . ">");
    			$this->GetAndPrintOrderAndItemsForOrderIDForLocation($order_id,$location_id);
    			
    			print("</" . ORDER_LIST_TAG . ">");
    			$xml_helper->PrintOrderUpdateSuccessEnd();
    			return true;
    		}
    		else
    		{
    			// error_object set inside RefundOrder
   				$xml_helper->PrintOrderUpdateFail($order_id,$error_object);
   				return false;
   			}
    		return $retval;
    	}
    	
    	function DoNoShowInProgressOrder($order_id,$incarnation,$location_id)
    	{
    		global $order_database;
    		$error_object = new FCError();
    		
    		return  $this->UpdateInProgressOrderDisposition($order_id,$location_id,$incarnation,ORDER_NOSHOW);
    		
    		return false;;	
    	}
    	
    	function UpdateInProgressOrderDisposition($order_id,$location_id,$incarnation,$disposition)
    	{
    		global $order_database;
    		global $xml_helper;
    		
    		if($order_database->UpdateInProgressOrderDisposition($order_id,$location_id,$incarnation,$disposition)==true)
    		{
    			$xml_helper->PrintOrderUpdateSuccessStart($order_id);
    			print("<" . ORDER_LIST_TAG . ">");
    			$this->GetAndPrintOrderAndItemsForOrderIDForLocation($order_id,$location_id);
    			
    			print("</" . ORDER_LIST_TAG . ">");
    			$xml_helper->PrintOrderUpdateSuccessEnd();
    			return true;
    		}
    		else
    		{
    			$xml_helper->PrintOrderUpdateFail($order_id);
    			return false;
    		}
    	}
    }
?>