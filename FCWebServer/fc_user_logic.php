<?php
    /**
     * fc_user_logic.php
     * 
     * The Database class is meant to simplify the task of accessing
     * user authentication information from the website's database.
     *
     *
     * (C) Copyright Freeway Coffee, 2011
     */
    //include("fc_constants.php");
	require_once("AuthorizeNet.php");
    require_once("fc_make_model_color_logic.php");
    require_once ("fc_error.php");
    require_once("fc_user_objects.php");
    require_once("fc_global_objects.php");
    require_once("fc_location_objects.php");
    
    class UserLogic
    {
        
        /* Class constructor */
        function UserLogic()
        {
            
        }
    
        function GenerateOrderManagerSignonOK($response_code,$client_type)
        {
        	global $session;
        	global $xml_helper;
        	global $database;
        	
        	$xml_helper->GenerateSignonResponseStart($response_code,$client_type,null);
        	$session->user_data->PrintAsXML();
        	
        	if($session->user_location!=NULL)
        	{
        		$session->user_location->PrintAsXML();
        	}
        	
        	$xml_helper->GenerateSignonResponseEnd();
        	
        }
        
        function GetUserCarInfo()
        {
        	global $session;
        	
        	$CarMakeModelInfo = new CarMakeModelInfo();
        	// Car Make Model Color
        	$CarMakeModelInfo->car_make_id=$session->user_data->user_car_make_id;
        	$CarMakeModelInfo->car_model_id=$session->user_data->user_car_model_id;
        	$CarMakeModelInfo->car_color_id=$session->user_data->user_car_color_id;
        	if($CarMakeModelInfo->DB_GetUserCarInfo()==true)
        	{
        		return $CarMakeModelInfo;
        	}
        	return NULL;
        	
        }
        
        function GetUserItems($user_id)
        {
            global $xml_helper;
            global $session;
            global $database;
            global $order_database;
            
           // First, header and compat level
            $xml_helper->GenerateUserItemsHeader();
            
            // Sadly, App settings are going under user items
            $this->PrintAppSettings();
            
            // now basic user items
            $session->user_data->PrintAsXML();

            $CarMakeModelInfo = $this->GetUserCarInfo();
            
            if($CarMakeModelInfo!=NULL)
            {
                $CarMakeModelInfo->GenerateUserInfoXML();
            }
          
            if(!is_null($session->user_location))
            {
            	$session->user_location->PrintAsXML();
            }
            
          // Credit card
            $credit_card = $database->GetUserDefaultCreditCard($user_id);
            if($credit_card!=NULL)
            {
                $this->GenerateCreditCardResponse($credit_card);
            }
            $this->PrintTipsForUser($_SESSION['user_id']);
            
            // User Drinks
            $this->GetUserDrinks();
            
            // If we have a location, get the last open order for this user. This became problematic with multiple locations
            // The original idea was we would get the last "Open" order so if the user signed out then back in they could
            // get sent back to the "I've arrived" page (and so they can view their last order)
            // This gets much more complicated if they change locations etc
            // I think when I change location I need to get the last open order for THAT location !!! Ahaha !!!!!
            // That means I must parse those orders in the location response but oh well!
            if(Location::isNoneLocationID($session->user_data->user_location_id)!=TRUE)
            {
            	$order_arr = $order_database->GetOpenInProgressOrdersForUserAndLocation($_SESSION['user_id'],1,$session->user_data->user_location_id);
            	if(!is_null($order_arr) && count($order_arr)>0)
            	{
            		$this->GenerateOrderAndLocation($order_arr[0],null);
            	}
            }
            
            print("</user_items>");
             
        }
        
        function GenerateOrderAndLocation(Order $order,$location)
        {
        	global $orders_logic;
        	global $order_database;
        	global $database;
        	
 			$order->PrintAsXML_NoClosingTag(); // This is an Order --- only send first one.
 			$orders_logic->PrintItemsForOrderAsXML($order);
 			// False => Not FOR UPDATE
 			$credit_card = $order_database->GetFirstOrderCreditCard($order->orders_id,false);
 			if(!is_null($credit_card))
 			{
 				$credit_card->PrintAsXML();
 			}
 			
 			if(is_null($location))
 			{
 				// Print the order location (may be different than the current user location but thats mostly wishful thinking for now!)
 				$order_location_data = Location::DB_GetLocationInfo($database, $order->orders_location_id);
 				 
 				if(!is_null($order_location_data))
 				{
 					$order_location_data->PrintAsOrderLocationXML();
 				}
 			}
 			else
 			{
 				$location->PrintAsOrderLocationXML();
 			}
        	
        	$order->PrintClosingTagAsXML();
        }
        
        function GenerateCreditCardResponse($credit_card)
        {
            print("<user_credit_card id=\"" . rawurlencode($credit_card['user_credit_card_id']) . "\" " .
                  "user_credit_card_last4=\"" . rawurlencode($credit_card['user_credit_card_last4']) . "\" " .
                  "user_credit_card_descr=\"" . rawurlencode($credit_card['user_credit_card_descr']) . "\" " .
                  "user_credit_card_exp=\"" . rawurlencode($credit_card['user_credit_card_exp']) . "\" " .
                  "type=\"user_credit_card\"" .
                  ">" . "</user_credit_card>");
        }
        
        function GenerateUserAddDrinkSuccess($drink_id)
        {
            global $database;
            $drink = $database->GetUserDrink($drink_id);
            if($drink==NULL)
            {
                print("<" . USER_DD_DRINK_TAG . " ");
                print("result=\"failed\"");
                print("></" . USER_ADD_DRINK_TAG . ">");
            }
            else
            {
                print("<" . USER_ADD_DRINK_TAG ." ");
                print("result=\"ok\">");
                $drink->PrintAsXML();
                
                print("</" . USER_ADD_DRINK_TAG .">");
            }
        }
        
        
        
        function UpdateTimeHere($order_id,$arrive_mode)
        {
            global $database;
            global $mailer;
            global $order_database;
            global $xml_helper;
            global $session;
            
            $retval= $database->UpdateUserHereTimeForOrder($order_id,$time_here,$arrive_mode);
          	if($retval==true)
            {
            	// Only useful to say you are here for an IN Progress Order
             	$order = $order_database->GetOrderByOrderIDAndTypeForLocation(ORDER_TYPE_IN_PROGRESS,$order_id,$session->user_data->user_location_id);
              	if(!is_null($order))
               	{
               		$already_set=false;
               		$xml_helper->GenerateUserHereSuccessStart($already_set,$order_id);
  		           	$this->GenerateOrderAndLocation($order,null);
           	    	$xml_helper->GenerateUserHereSuccessEnd();
               	    $mailer->SendUserHereForOrder($_SESSION['user_email'],$_SESSION['user_name'],$order_id,$time_here,$order->orders_arrive_mode,
               	    			$session->user_location);
                   	return true;
               	}
               	else
               	{
               		$xml_helper->GenerateUserHereFail($order_id);
               		return false;
               	}
           }
           else
           {
            	$xml_helper->GenerateUserHereFail($order_id);
               	return false;
           }
        }
        
        function GenerateUserEditDrinkSuccess($drink_id)
        {
            global $database;
            $drink = $database->GetUserDrink($drink_id);
            if($drink==NULL)
            {
                $this->GenerateUserEditDrinkFailed();
                return false;
            }
            else
            {
                print("<" . USER_EDIT_DRINK_TAG . " ");
                print("result=\"ok\">");
                $drink->PrintAsXML();
                
                print("</" . USER_EDIT_DRINK_TAG .">");
                return true;
            }

        }
       
        function PrintAppSettings()
        {
        	global $database;
        	
        	print("<" . APP_SETTING_LIST_TAG . ">");
        	
        	$stmt = $database->OpenGetAppSettings();
        	$app_setting = $database->GetNextAppSetting($stmt);
        	while($app_setting!=null)
        	{
        		$app_setting->PrintAsXML();
        		$app_setting = $database->GetNextAppSetting($stmt);
        	}
        	
        	
        	print("</" . APP_SETTING_LIST_TAG . ">");
        }
        
        function GetUserDrinks()
        {
            global $database;
                     
            print("<" . USER_DRINKS_TAG . ">");
            $stmt=$database->OpenGetUserItems();
            $drink = $database->GetNextUserDrink($stmt);
            while($drink!=NULL)
            {
            	$drink->PrintAsXML();
                
                $drink = $database->GetNextUserDrink($stmt);
            }
            print("</" . USER_DRINKS_TAG . ">");
            
            $database->CloseGetUserDrinks($stmt);
        }
        
        function UpdateTimeToLocation($update_time_to_location)
        {
            global $database;
            global $xml_helper;
            
            $result = $database->UpdateTimeToLocation($update_time_to_location);
            
            $xml_helper->GenerateUpdatedTimeToLocation($result);
                
            return $result;
            
            
        }
        
        function SetTipForUserAndLocation($user_id,$location_id,$tip_type,$tip_amount,$round_up)
        {
        	global $database;
        	global $xml_helper;
        	
        	
        	$int_tip_type = intval($tip_type);
        	if( ($int_tip_type!=USER_TIP_TYPE_AMOUNT) && 
        		($int_tip_type!=USER_TIP_TYPE_PERCENT))
        	{
        		$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,LOG_CAT_INTERNAL,
        				"SetTipForUserAndLocation: TipType of: {$tip_type} is Invalid" );
        		$xml_helper->GenerateSetUserTipFailResponse();
        		return false;
        	}
        	
        	$tip = new UserTip();
        	$tip->user_id=$user_id;
        	$tip->location_id=intval($location_id,10);
        	$tip->tip_type=$int_tip_type;
        	$tip->tip_amount=$tip_amount;
        	$tip->round_up=$round_up;
        	
        	$lookup_tip = $database->GetUserTip($user_id,$location_id);
        	$result=false;
        	if($lookup_tip==NULL)
        	{
        		$result = $database->AddUserTip($tip);
        		
        	}
        	else
        	{
        		$result = $database->UpdateUserTip($tip);
        	}
        	
        	if($result==true)
        	{
        		$xml_helper->GenerateSetUserTipSuccessResponse($tip,$result);
        	}
        	else
        	{
        		$xml_helper->GenerateSetUserTipFailResponse();
        	}
        	return $result;
        }
        
        function DeleteTipForUserAndLocation($user_id,$location_id)
        {
        	global $database;
        	global $xml_helper;
        	
        	$result = $database->DeleteUserTip($user_id,$location_id);
        	$xml_helper->GenerateDeleteUserTipRespinse($result);
        	return $result;
        }
        
        function PrintTipsForUser($user_id)
        {
        	global $database;
        	global $session;
        	
        	print("<" . USER_TIP_LIST_TAG . ">");
        	$stmt = $database->OpenGetAllUserTips($user_id);
        	 
        	$tip =$database->GetNextUserTips($stmt);
        	 
        	while ($tip!=NULL)
        	{
        	
        		$tip->PrintAsXML();
        		$tip =$database->GetNextUserTips($stmt);
        	}
        	
        	print("</" . USER_TIP_LIST_TAG . ">");
        	 
        	$database->CloseGetUserTips($stmt);
        	
        }
        
        function UpdateTag($user_tag)
        {
            global $database;
            global $xml_helper;
            
            $result = $database->UpdateUserTag($user_tag);
            
            $xml_helper->GenerateUpdatedTag($result,$user_tag);
            
            return $result;

        }
        
        function UpdatePaymentMethod($user_id,$payment_method)
        {
        	global $database;
        	global $xml_helper;
        	
        	$error = new FCError();
        	
        	if(LocationPayMethod::IsValidPaymentMethod($payment_method)!=true)
        	{
        		$error->ErrorCodeMajor=ERROR_MAJOR_VALIDATION;
        		$error->ErrorCodeMinor=ERROR_MINOR_VALID_OUT_OF_RANGE;
        		$error->DisplayText="An app error has Occurred. Please report.";
        		$error->LongText="An app error has Occurred. Please report.(Payment Method out of range)";
        		$xml_helper->GenerateUpdatePayMethodResponse(false, $error);
        		return false;
        	}
        	
        	if($database->UpdatePaymentMethod($user_id,$payment_method,$error))
        	{
        		$xml_helper->GenerateUpdatePayMethodResponse(true,null);
        		return true;
        	}
        	else
        	{
        		$xml_helper->GenerateUpdatePayMethodResponse(false,$error);
        		return true;
        	}
        	
        }
        
        function UpdateTagAndCar($user_tag,$user_car_make_id,$user_car_model_id,$user_car_color_id,$arrive_mode)
        {
            global $database;
            global $xml_helper;
            
            // TODO. This is not that great, but feasibly the App could send over a zero ID which is not that great to insert here
            if($user_car_make_id<1)
            {
                $user_car_make_id=1;
            }
            if($user_car_model_id<1)
            {
                $user_car_model_id=1;
            }
            if($user_car_color_id<1)
            {
                $user_car_color_id=1;
            }
            
            $result = $database->UpdateTagAndCar($user_tag,$user_car_make_id,$user_car_model_id,$user_car_color_id,$arrive_mode);
            
            $car_info = new CarMakeModelInfo;
            $car_info->car_make_id=$user_car_make_id;
            $car_info->car_model_id=$user_car_model_id;
            $car_info->car_color_id=$user_car_color_id;
            
            // Now get the updated info
            if($result==true)
            {
                // If the subsequent lookup fails, we also fail it.
                $result = $car_info->DB_GetUserCarInfo();
            }
            $xml_helper->GenerateUpdatedCarAndTag($result,$user_tag,$car_info);
            return $result;
            
        }
        
        function IsLocationValidForOrder(Location $location,FCError &$error_object,$arrive_mode)
        {
        	global $database;
        	global $session;
        	
        
       		if($location->IsArrivalModeValid($arrive_mode)!=TRUE)
       		{
       			$err_str = "Sorry, This location does not support An Arrival Type of: " 
       					. LocationArriveMode::MakeArriveModeStringFromID($arrive_mode)
       					. " Please report this error.";
       			
       			$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,LOG_CAT_STORE_CLOSED,$err_str);
       			 
       			$error_object->ErrorCodeMajor = ERROR_MAJOR_LOCATION;
       			$error_object->ErrorCodeMinor = ERROR_MINOR_LOCATION_ARRIVE_NOT_SUPPORTED;
       			$error_object->DisplayText=$err_str;
       			$error_object->LongText = $err_str;
       			return false;
       		}

       		if($location->IsPaymentMethodValid($session->user_data->user_pay_method)!=TRUE)
       		{
       			$err_str = "Sorry, This location does not support a payment type of: "
       						. LocationPayMethod::ConvertPayMethodToString($session->user_data->user_pay_method)
       						. " Please report this error";
       			
       			$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,LOG_CAT_STORE_CLOSED,$err_str);
       			 
       			$error_object->ErrorCodeMajor = ERROR_MAJOR_LOCATION;
       			$error_object->ErrorCodeMinor = ERROR_MINOR_LOCATION_PAY_METHOD_NOT_SUPPORTED;
       			$error_object->DisplayText=$err_str;
       			$error_object->LongText = $err_str;
       			return false;
       		}
       		
       		
       		return true;
       	}
        

        function MakeOrder($user_id,$item_id_list,&$order_id,$client_type,$arrive_mode,$user_incarnation,$user_location_incarnation)
        {
            global $database;
            global $xml_helper;
            global $session;
            global $mailer;
            global $order_database;
           
            
            // First, get all the drinks this user would like to order using the supplied list.
            //print ("UserLogic Make Order...");
            
            // Check the incarnations
            $error_object = new FCError();
            if(  (is_null($user_incarnation)!=true) && ($session->user_data->incarnation!=$user_incarnation))
            {
            	// User incarnation does not match
            	$err_str = "App information out of sync with server (User Info) Your data will refresh";
            	$error_object->DisplayText = "";
            	$error_object->ErrorCodeMajor = ERROR_MAJOR_OUT_OF_SYNC;
            	$error_object->ErrorCodeMinor = ERROR_MINOR_USER_OUT_OF_SYNC;
            	$error_object->DisplayText=$err_str;
            	$error_object->LongText=$err_str;
            	XMLHelper::GenerateNeedDataUpdate($error_object,$session->user_data->incarnation);
            	return false;
           	}
           	
           	
           	if(  (is_null($user_location_incarnation)!=true) && ($session->user_location->incarnation!=$user_location_incarnation))
           	{
           		// User Location incarnation does not match
           		
           		$err_str = "App information out of sync with server (User Location) Your data will refresh";
           		$error_object->DisplayText = "";
           		$error_object->ErrorCodeMajor = ERROR_MAJOR_OUT_OF_SYNC;
           		$error_object->ErrorCodeMinor = ERROR_MINOR_USER_LOC_OUT_OF_SYNC;
           		$error_object->DisplayText=$err_str;
           		$error_object->LongText=$err_str;
           		XMLHelper::GenerateNeedDataUpdate($error_object,$session->user_location->incarnation);
           		return false;
           	}
           	
            $total_items_cost=0.00;
            
            $highest_item_cost=0.00;
            $highest_cost_item_index=0;
            
            
            $location = Location::DB_GetLocationInfo($database, $session->user_data->user_location_id);
            
            if(is_null($location))
            {
            	$err_str = "That Location could not be found. Please report this error. Location ID: [" . $session->user_data->user_location_id . "]";
            	$error_object->DisplayText = "";
            	$error_object->ErrorCodeMajor = ERROR_MAJOR_INTERNAL;
            	$error_object->ErrorCodeMinor = ERROR_MINOR_STATE_NOT_FOUND;
            	$error_object->DisplayText=$err_str;
            	$error_object->LongText=$err_str;
            	$xml_helper->GenerateOrderFailed($error_object);
            	return false;
            }
            
            if($this->IsLocationValidForOrder($location,$error_object,$arrive_mode)!=true)
            {
            	
            	$xml_helper->GenerateOrderFailed($error_object);
            	return false;
            }
            
            $item_data = $database->GetItemsSummary($item_id_list,
            										$location->LocationMenuID,true,
            										$total_items_cost,$highest_item_cost,$highest_cost_item_index);
            
            if( ($item_data==NULL) || (count($item_data)==0) )
            {
                // TODO
                $err_str = "Your order did not contain any valid items. An internal error occurred, please report this.";
                
            	$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,LOG_CAT_INTERNAL,
            			"MakeOrder: No Items Returned for User: {$user_id} ID LIST: {$item_id_list}");
            	$error_object->DisplayText = "";
            	$error_object->ErrorCodeMajor = ERROR_MAJOR_INTERNAL;
            	$error_object->ErrorCodeMinor = ERROR_MINOR_STATE_NOT_FOUND;
            	$error_object->DisplayText=$err_str;
            	$error_object->LongText=$err_str .= " MakeOrder: No Items Returned for User: {$user_id} ID LIST: {$item_id_list}";
                $xml_helper->GenerateOrderFailed($error_object);
                $mailer->SendStoreOrderFailEmail($_SESSION['user_email'],$_SESSION['user_name'],$location, $error_object->GetForEmail());
                return false; // NO order to make !!
            }
            if($total_items_cost==0.00)
            {
            	$err_str = "You Cannot have an order total of $0.00. Please add an item with a price to your order.";
            	$error_object->DisplayText = $err_str;
            	$error_object->ErrorCodeMajor = ERROR_MAJOR_BILLING;
            	$error_object->ErrorCodeMinor = ERROR_MINOR_BILLING_ZERO_COST;
            	 
            	$error_object->LongText=$err_str . " Order Item Count: " . count($item_data);
            	$xml_helper->GenerateOrderFailed($error_object);
            	$mailer->SendStoreOrderFailEmail($_SESSION['user_email'],$_SESSION['user_name'],$location, $error_object->GetForEmail());
            	return false;
            }
            
            $tip = $database->GetUserTip($user_id,$session->user_data->user_location_id);
            if($tip==NULL)
            {
            	$tip = $database->GetUserTip($user_id,0); // Get Default tip
            }
            
            
            //var_dump($tip);
            $credit_data = new OrderCreditCard();
            $order_id=0;
            
            $time_ready="";

            $car_data = $this->GetUserCarInfo();
           
            //var_dump($item_data);
            //print("Make Order: Total Cost: {$total_items_cost}");
            
            if($database->MakeOrder($item_data,$total_items_cost,$highest_item_cost,$highest_cost_item_index,
            						$credit_data,$order_id,$time_ready,$error_object,$car_data,$location,$client_type,$arrive_mode,$tip))
            {
            	/*
                $xml_helper->GenerateOrderSuccessful($order_id,$credit_data,$total_items_cost,$credit_data->card_last4, $credit_data->card_descr,$time_ready,
                									$location,                                         
                                                    $item_data,$car_data);
                                                    */
            	$order = $order_database->GetOrderByOrderIDAndTypeForLocation(ORDER_TYPE_IN_PROGRESS,$order_id,$session->user_data->user_location_id);
            	if(is_null($order))
            	{
            		$err_str = "An Internal Server error Occurred, Please report via Feedback";
            		$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,LOG_CAT_INTERNAL,
            				"MakeOrder: GetOrderByOrderIDAndTypeForLocation Failed for Order {$order_id}");
            		$error_object->DisplayText = "";
            		$error_object->ErrorCodeMajor = ERROR_MAJOR_INTERNAL;
            		$error_object->ErrorCodeMinor = ERROR_MINOR_STATE_NOT_FOUND;
            		$error_object->DisplayText=$err_str;
            		$error_object->LongText=$err_str .=  " MakeOrder: GetOrderByOrderIDAndTypeForLocation Failed for Order {$order_id}";
            		$xml_helper->GenerateOrderFailed($error_object);
            		$mailer->SendStoreOrderFailEmail($_SESSION['user_email'],$_SESSION['user_name'],$location, $error_object->GetForEmail());
            		return false; // NO order to make !!
            	}
            	          	
            	$xml_helper->GenerateOrderSuccessfulShortStart($order_id);
            	
            	$this->GenerateOrderAndLocation($order,$location);

            	$xml_helper->GenerateOrderSuccessfulShortEnd();
                return true;
            }
            else
            {
                $xml_helper->GenerateOrderFailed($error_object);
                $mailer->SendStoreOrderFailEmail($_SESSION['user_email'],$_SESSION['user_name'],$location, $error_object->GetForEmail());
                return false;
            }
            
            // NOTREACHED
            return false;
        }
        
        function DeleteCreditCardForThisUser($card_id)
        {
        	global $database;
        	global $xml_helper;
        	
        	if($database->DeleteCreditCardForUser($card_id,$_SESSION['user_id'])==true)
        	{
        		$xml_helper->GenerateDeleteCreditCardSuccess($card_id);
        		return true;
        	}
        	else
        	{
        		$xml_helper->GenerateDeleteCreditCardFail();
        		return false;
        	}
        	
        		
        }
        function UpdateCreditCard($card_number,$exp_month,$exp_year,$billing_zip,$use_for_pay)
        {
            global $database;
            global $xml_helper;
            
            $card = new CreditCard($card_number, $exp_month, $exp_year, $billing_zip);
            
           // print("NUM: {$card_number} MON: {$exp_month} YEAR: {$exp_year}, ZIP: {$billing_zip}");
            $error_object = new FCError();
            $error_object->ErrorCodeMajor = ERROR_MAJOR_INTERNAL;
            $error_object->ErrorCodeMinor = ERROR_MINOR_STATE_NOT_FOUND;
            $error_object->DisplayText="An Error Occurred trying to update your credit card information, Please try again, Or contact customer support if this persists";
            $error_object->LongText=$error_object->DisplayText;
            
            // Update
            if($database->UpdateCreditCardInfo($card,$use_for_pay,$error_object)==true)
            {
                //print("CC: Got past update");
                
                // Then Get
                print("<update_credit_card_response ");
                print("result=\"");
                print("ok\">");
                
                
                $credit_card = $database->GetUserDefaultCreditCard($_SESSION['user_id']);
                $this->GenerateCreditCardResponse($credit_card);
                print("</update_credit_card_response>");

                return true;
            }
            else
            {
                $xml_helper->GenerateUpdateCreditCardResponse(false,$error_object);
            }
            return false;
        }
             
        function AddUserFeedback($user_id, $email_to_use,$feedback_code,$feedback,$feedback_happiness)
        {
        	global $database;
        	global $mailer;
        	
        	//print("Feed: ID: {$user_id} EM: {$email_to_use} : Code: {$feedback_code} Feedback: {$feedback} Happ: {$feedback_happiness}");
        	$unix_time = time();
        	$database->GetTimeAsUTCAndLocal($unix_time, $gm_date_time_now, $local_date_time_now);
        	
        	
        	if($database->AddUserFeedback($user_id,$email_to_use,$feedback_code,$feedback,$feedback_happiness,$gm_date_time_now)==TRUE)
        	{
        		$database->AddLogEntry($user_id,$email_to_use,session_id(),LOG_CODE_SUCCESS,"AddUserFeedback Success for {$user_id} / {$email_to_use}");
        	}
        	else
        	{
        		$database->AddLogEntry($user_id,$email_to_use,session_id(),LOG_CODE_SUCCESS,"AddUserFeedback Failed for {$user_id} / {$email_to_use}");
        	}
        	
        	$mailer->SendFeedbackEmail($user_id,$email_to_use,$feedback_code,$feedback,$feedback_happiness,$local_date_time_now);
        }
        
        function GetFeebackTypeStringFromID($feedback_code)
        {
        	switch($feedback_code)
        	{
        		case FEEDBACK_GENERAL_ID:
        			return FEEDBACK_GENERAL_TEXT;
        		case FEEDBACK_LOGIN_ID:
        			return FEEDBACK_LOGIN_TEXT;
        		case FEEDBACK_BILLING_ID:
        			return FEEDBACK_BILLING_TEXT;
        		case FEEDBACK_SUGGESTION_ID:
        			return FEEDBACK_SUGGESTION_TEXT;
        		default:
        			return FEEDBACK_UNKNOWN_TEXT;
        	}
        }
        
    
    }
?>
