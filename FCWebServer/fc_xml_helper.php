<?php
/**
 * fc_xml_helper.php
 * 
 * The Session class is meant to simplify the task of keeping
 * track of logged in users and also guests.
 *
 * (C) Copyright Freeway Coffee, 2011
 */
	
require_once("fc_constants.php");
	
	
	
/* Schema
 *
 *
 * <register_response>
 * result="failed_email | failed_other_retry | success_register_only | success_register_signin" 
 * </register_response>

 */

    require_once("fc_constants.php");
    require_once("fc_user_objects.php");
    
 
    define("SIGNON_RESPONSE_OK",0);
    define("SIGNON_RESPONSE_FAIL",1);
	
class XMLHelper
{
	
	// Assumes main tag is open
	// $object_response text is one of OBJECT_RESPONSE_HAVE_LATEST etc 
	// $object_ver could be NULL
	
	
        
	static function PrintObjectResponse($result,$object_response_text,$object_ver)
	{
	
		print(RESULT_ATTR . "=\"" . rawurlencode($result) . "\" " .
			OBJECT_RESPONSE_ATTR  . "=\"" . rawurlencode($object_response_text) . "\" " );
		if(!is_null($object_ver))
		{
			print(OBJECT_RESPONSE_LATEST_VER_ATTR . "=\"" . rawurlencode($object_ver) . "\" " );
		}
	}
	
	function GenerateXMLHeader()
	{
		print('<?xml version=\"1.0\""encoding=\"utf-8\"\?\>');
	}

	function GenerateUserHereSuccessStart($already_here, $order_id)
	{
		print("<" . USER_ORDER_HERE_RESPONSE_TAG .
				" result=\"ok\"" . " " .
				ORDER_ID_ATTR . "=\"{$order_id}\"" . ">");
				
	}
	function GenerateUserHereSuccessEnd()
	{
		print("</" . USER_ORDER_HERE_RESPONSE_TAG . ">");
	}
	
	function PrintUpdatedLocationSuccess(Location $location)
	{
	 
		print("<" . Location::$LOCATION_UPDATED_TAG .
				" result=\"ok\"" . ">");
		$location->PrintAsXML();
		print("</" . Location::$LOCATION_UPDATED_TAG . ">");
	}
	
	function PrintUpdatedLocationFail()
	{
		print("<" . Location::$LOCATION_UPDATED_TAG .
				" result=\"" . FAILED . "\"" . ">" . Location::$LOCATION_UPDATED_TAG . ">");
		
	}
	
	function GenerateUserHereFail($order_id)
	{
		print("<" . USER_ORDER_HERE_RESPONSE_TAG .
				" result=\"" . FAILED . "\"" . ">" .
				 USER_ORDER_HERE_RESPONSE_TAG . ">");
	}
	
	function GenerateSetUserTipSuccessResponse(UserTip $tip)
	{
		print("<" . SET_USER_TIP_CMD . " " .
				RESULT_ATTR . "=\"" . COMMAND_SUCCESS . "\" >");
		if(!is_null($tip))
		{
			// If Null then god alone can help us
			$tip->PrintAsXML();
		}
		
		print("</" . SET_USER_TIP_CMD . ">");
	}
	
	function GenerateSetUserTipFailResponse()
	{
		print("<" . SET_USER_TIP_CMD . " " .
				RESULT_ATTR . "=\"" . COMMAND_FAILED ."\"");
		
		print("></" . SET_USER_TIP_CMD . ">");
	}
	function GenerateOrderSuccessfulShortStart($order_id)
	{
		global $session;
		// Oh yeah, got to send back the new free drinks count!
		print("<" . USER_ORDER_RESPONSE_TAG  . 
				" result=\"ok\" " . " " .
				ORDER_ID_ATTR . "=\"{$order_id}\"" . " " .
				"user_free_drinks=\"" . rawurlencode($session->user_data->user_free_drinks_count) ."\" " .
				">");
		
	}
	
	function GenerateOrderSuccessfulShortEnd()
	{
		print("</" . USER_ORDER_RESPONSE_TAG .">");
	}
	
    function GenerateOrderSuccessful($order_id,$credit_data,$total_cost,$card_last4,$card_descr,$time_ready,$location,
    								$drink_data,$car_data)
    {
    
    	
        print("<" . USER_ORDER_HERE_RESPONSE_TAG ." ");
        print("result=\"ok\" ");
        print("order_id=\"" . rawurlencode($order_id) . "\" ");
        print("total_cost=\"" . rawurlencode(number_format($total_cost,2)) . "\" ");
        print("credit_card_last4=\"" . rawurlencode($card_last4) . "\" ");
        print("credit_card_name=\"" . rawurlencode($card_descr) . "\" ");
        print("time_ready=\"" . rawurlencode($time_ready) . "\" ");
        print("location_description=\"" . rawurlencode($location[USER_LOCATION_DESCRIPTION]) . "\" ");
        print("location_address=\"" . rawurlencode($location[USER_LOCATION_ADDRESS]) . "\" ");
        print(USER_ORDER_RESPONSE_LOCATION_PHONE_ATTR . "=\"" . rawurlencode($location[USER_LOCATION_PHONE]) . "\" ");
        print(USER_ORDER_RESPONSE_LOCATION_EMAIL_ATTR . "=\"" . rawurlencode($location[USER_LOCATION_EMAIL]) . "\" ");

        $index=1;
        
        if($drink_data!=NULL)
        {
	        foreach ($drink_data as $drink)
	        {
	        	print(ORDER_DRINK_DESCR . $index ."=\"" . rawurlencode($drink->MakeDrinkText()) . "\" ");
	        	//print(ORDER_DRINK_COST . $index ."=\"" . rawurlencode( number_format($drink->cost,2)) . "\" ");
	        	$index++;
        }
        }
        
        print("></" . USER_ORDER_RESPONSE_TAG .">");
    }
    
    function GenerateOrderFailed($error_object)
    {
        print("<" . USER_ORDER_HERE_RESPONSE_TAG . " ");
        print("result=\"failed\" ");
        
        print(">");
        $error_object->PrintAsXML();
        print("</" . USER_ORDER_HERE_RESPONSE_TAG .">");
    }
    
    static function GenerateNeedDataUpdate($error_object,$version)
    {
    	XMLHelper::GenerateOrderResponseStart();
    	XMLHelper::PrintObjectResponse(COMMAND_FAILED,OBJECT_RESPONSE_NEED_NEW,$version);
    	print (">");
    	$error_object->PrintAsXML();
    	
    	
    	XMLHelper::GenerateOrderResponseEnd();
    }

    static function GenerateOrderResponseStart()
    {
    	print("<" . USER_ORDER_RESPONSE_TAG ." ");
    }
    
    static function GenerateOrderResponseEnd()
    {
    	print("</" . USER_ORDER_RESPONSE_TAG . ">");
    }
    
	function GenerateSignupResponseCommon($client_type)
	{
		
		//$this->GenerateXMLHeader();
		print("<register_response ");
        print("compat_level=\"" . MAIN_COMPAT_LEVEL . "\" ");
        print("compat_release_needed=\"" . Session::GetCompatReleaseStringForClientType($client_type) . "\" ");	
	}
	
	function GenerateUpdatePayMethodResponse($result,$error)
	{
		print("<" . UPDATE_PAYMENT_METHOD_COMMAND_RESPONSE_TAG . " " .
				RESULT_ATTR . "=\"");
		if($result)
		{
			print(COMMAND_SUCCESS . "\">");
		}
		else 
		{
			print(COMMAND_FAILED. "\">");
			if(!is_null($error))
			{
				$error->PrintAsXML();
			}
		}
		
		
		print("</" . UPDATE_PAYMENT_METHOD_COMMAND_RESPONSE_TAG .">");	
	}
	
	
	function GenerateSignupSuccessResponse($client_type)
	{
		$this->GenerateSignupResponseCommon($client_type);
		print("result=\"success_register_signin\"> ");
		print("</register_response>");
	}
	
	function GenerateSignupFailResponse($client_type,FCError $error)
	{
		$this->GenerateSignupResponseCommon($client_type);
		print("result=\"failed\" ");
        print(">");
        $error->PrintAsXML();
		print("</register_response>");
	}

	function GenerateSigonResponse($response_code,$client_type,$error)
	{
		$this->GenerateSignonResponseStart($response_code,$client_type,$error);
		$this->GenerateSignonResponseEnd();
	}
	
		 
	function GenerateSignonResponseStart($response_code,$client_type,$error)
	{
		print("<signon_response ");
		print("compat_level=\"" . rawurlencode(MAIN_COMPAT_LEVEL) . "\" ");
		if(!is_null($client_type))
		{
			print("compat_release_needed=\"" . rawurlencode(Session::GetCompatReleaseStringForClientType($client_type)) . "\" ");
		}
		print("result=\"");
		switch($response_code)
		{
			case SIGNON_RESPONSE_OK:
				print("signon_ok");
			break;
			
			case SIGNON_RESPONSE_FAIL:
				print("signon_failed");
			break;
			default:
				print("signon_failed");
			break;
		}
		print("\" ");
		print(">");
		if(!is_null($error))
		{
			$error->PrintAsXML();
		}
		
	}
	
	function GenerateSignonResponseEnd()
	{
		print("</signon_response>");
	}
							 						
	function GenerateDeleteCreditCardSuccess($card_id)
	{
		
		print("<" . DELETE_CREDIT_CARD . " result=\"ok\"" .  " " .
				CREDIT_CARD_ID_CMD_PARAM . "=\"" .$card_id .  "\" " .
				  "></" . DELETE_CREDIT_CARD . ">");
	}
	
	
	function GenerateDeleteCreditCardFail()
	{
		print("<" . DELETE_CREDIT_CARD . "result=\"". COMMAND_FAILED . "\"" . "></" . DELETE_CREDIT_CARD . ">");
	}
		
	static function GenerateUserAddDrinkFailedNeedUpdate($error_object)
	{
		print("<" . USER_ADD_DRINK_TAG . " ");
		XMLHelper::PrintObjectResponse(COMMAND_FAILED,OBJECT_RESPONSE_NEED_NEW,null);
		print (">");
		$error_object->PrintAsXML();
		print("></" . USER_ADD_DRINK_TAG . ">");
	}
	
	static function GenerateUserEditDrinkFailedNeedUpdate($error_object)
	{
		print("<" . USER_EDIT_DRINK_TAG . " ");
		XMLHelper::PrintObjectResponse(COMMAND_FAILED,OBJECT_RESPONSE_NEED_NEW,null);
		print (">");
		$error_object->PrintAsXML();
			
			
		print("></" . USER_EDIT_DRINK_TAG . ">");
	}
	
	
	static function GenerateUserEditDrinkFailed()
	{
		print("<" . USER_EDIT_DRINK_TAG . " ");
		print("result=\"failed\"");
		print("></" . USER_EDIT_DRINK_TAG . ">");
	}
	
	
    static function GenerateUserAddDrinkFailed()
    {
        print("<" . USER_ADD_DRINK_TAG ." ");
        print("result=\"failed\"");
        print("></" . USER_ADD_DRINK_TAG .">");
        
    }
    
    function GenerateDrinkDeleteResponse($succeeded,$drink_id)
    {
        print("<drink_delete_response ");
        if($succeeded==true)
        {
            print("result=\"ok\"");
            print(" deleted_drink_id=\"" . rawurlencode($drink_id) . "\" ");
        }
        else
        {
            print("result=\"failed\"");
        }
        print("></drink_delete_response>");
    }
    
    function GenerateUpdatedTag($result,$user_tag)
    {
        $user_tag_utf = rawurlencode($user_tag);
        
        print("<updated_user_tag_response ");
        
        if($result==true)
        {
            print("result=\"ok\"");
            print("><user_tag user_tag=\"{$user_tag_utf}\">
                  </user_tag>");
            
        }
        else
        {
            print("result=\"failed\"");
        }
        
        print("</updated_user_tag_response>");
        
    }
    function GenerateUpdatedCarAndTag($result,$user_tag,$car_info)
    {
        $user_tag_utf = rawurlencode($user_tag);
        
        print("<updated_user_tag_response ");
        
        if($result==true)
        {
            print("result=\"ok\"");
            print("><user_tag user_tag=\"{$user_tag_utf}\">
                  </user_tag>");
            $car_info->GenerateUserInfoXML();
            
        }
        else
        {
            print("result=\"failed\"");
        }
        
        print("</updated_user_tag_response>");
        
    }
    
    function GenerateUpdateCreditCardResponse($success,FCError $error_object)
    {
        print("<update_credit_card_response ");
        print("result=\"");
        if($success==true)
        {
            print("ok\"");
            print(">");
        }
        else
        {
            print("failed\"");
            print(">");
            $error_object->PrintAsXML();
        }
        print("</update_credit_card_response>");
        
    }
    
    function GenerateUpdatedTimeToLocation($success)
    {
        print("<updated_time_to_location_result ");
        print("result=\"");
        if($success)
        {
            print("ok\"");
        }
        else
        {
            print("failed\"");
        }
        print("></updated_time_to_location_result>");
        
    }
    

    
    function PrintOrderUpdateSuccessStart($order_id)
    {
    	print("<" . ORDER_UPDATED_TAG ." result=\"ok\" " . 
    			ORDER_ID_ATTR . "=\"" . rawurlencode($order_id) . "\" " .
    			">");
    }
    
    function PrintOrderUpdateSuccessEnd()
    {
    	print("</" . ORDER_UPDATED_TAG . ">");
    }
    
    function PrintOrderUpdateFail($order_id,$error_object)
    {
    	print("<" . ORDER_UPDATED_TAG ." result=\"failed\" " .
    			ORDER_ID_ATTR . "=\"" . rawurlencode($order_id) . "\" " .
    			">");
    	if(!is_null($error_object))
    	{
    		$error_object->PrintAsXML();
    	}	
    	print("</" . ORDER_UPDATED_TAG . ">");
    }
    	
    
    function GenerateUserItemsHeader()
    {
        print("<user_items compat_level=\"" . USER_ITEMS_COMPAT_LEVEL . "\">");
    }
     
     
    
};
	