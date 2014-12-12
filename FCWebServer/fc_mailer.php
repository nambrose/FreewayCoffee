<?php
/**
 * Mailer.php
 *
 * The Mailer class is meant to simplify the task of sending
 * emails to users. Note: this email system will not work
 * if your server is not setup to send mail.
 *
 *
 * (C) Copyright Freeway Coffee, 2011,2012,2013
 */
	
require_once("fc_location_objects.php");

class Mailer
{

	var $from; // Always the same for now
	var $store_order_email_list; // Who to email the store copies of orders to
	
	function Mailer()
	{
		$this->store_order_email_list=array();
		if(DEBUG_FC==1)
		{
			$this->from = "From: " . DEBUG_EMAIL_FROM_ADDR;
			$this->store_order_email_list [] = 'nick.a.ambrose@gmail.com';
			
		}
		else
		{
			$this->from = "From: " . EMAIL_FROM_ADDR ;
			$this->store_order_email_list [] = 'nick.a.ambrose@gmail.com';
			$this->store_order_email_list [] = 'orders@freewaycoffee.com';
			//$this->store_order_email_list [] = 'freecoffjones@gmail.com';
			
		}
	}
	
	
	function SendReportComplete($user_name,$user_email,$directory,$meta)
	{
		$subject = "Report Ready";
		$body = "Report Ready: \n\nGenerating User: $user_name({$user_email})\n\n"
		. "Information:\n" . $meta . "\n";
		
		if(!mail($user_email,$subject,$body,$this->from))
		{
			$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
					"SendReportComplete: Delivery to: {$user_email} Failed for Report: {$directory}");
			return false;
		}
		return true;
	}
	
	function sendWelcome($user_email, $user_name)
	{
      $subject = "Freeway Coffee - Welcome + Tell a Friend!!";
      $body = "Hi " . $user_name . ",\n\n"
             ."Welcome to Freeway Coffee!\n\n"
             ."We're sorry, but we are currently not yet in operation, but hope to be up and running in the Seattle area very soon now.\n\n"
             ."In the meantime, please feel free to check us out on facebook at: http://www.facebook.com/FreewayCoffee\n\n"
             ."Or feel free to check out our blog at: http://www.freewaycoffee.com/blog/\n\n"
             ."We'd like to add you our periodic mailing list, and promise never to spam you. If you would rather not be on the list, please reply and let us know, or simply unsubscribe from the list (there is a link at the end of each message).\n\n"
             ."We sincerely thank you for your interest and hope to be serving you soon.\n\n"
             ."You've registered with the following information:\n"
             ."Email: ".$user_email."\n"
             ."Name: ". $user_name."\n\n"
             ."Have feedback? Please reply and let us know your thoughts."
      		 . "\n\n"
			 ."- The FreewayCoffee team";

     	$retval=true;
		if(!mail('nick.a.ambrose@gmail.com',$subject,$body,$this->from))
		{
			$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
   					"sendWelcome: Delivery to: 'nick.a.ambrose@gmail.com Failed");
   			$retval=false;
		}
		if(!mail($user_email,$subject,$body,$this->from))
		{
			$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
					"sendWelcome: Delivery to: {$to_address} Failed");
			$retval=false;
		}
		return $retval;
   }
   
   
   function SendStoreOrderFailEmail($user_email,$user_name,Location $location_info, $reason)
   {
   		global $database;
   		$subject = "Freeway Coffee [ Order: FAILED, Name: {$user_name} ]";
   		$body = "Order for " . $user_name . "(email: {$user_email} ), FAILED \n\n";
   		$body.="Reason: $reason";
   		
   	 
   		$body  .= "- FreewayCoffee";
   	 
   		$retval=true;
   		foreach ($this->store_order_email_list as $to_address)
   		{
   			if(!mail($to_address,$subject,$body,$this->from))
   			{
   				$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
   						"SendStoreOrderFailEmail: Delivery to: {$to_address} Failed for Order: {$order_id}");
   				$retval=false;
   			}
   		}
   		
   		
   		if(strcmp($location_info->LocationEmail,"")!=0)
   		{
   			if(!mail($location_info->LocationEmail,$subject,$body,$this->from))
   			{
   				$this->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
  				 	"SendStoreOrderFailEmail: Delivery to: {$location_info->LocationEmail} Failed for Order: {$order_id}");
  			 	$retval=false;
  		 	}
  	 	}
   	return $retval;
   }
   
   function SendStoreOrderEmailNoPDF(UserData $user, $order_id,
   									 $total_cost, $total_items_cost, $discount,$tip_amount,
   									 $credit_card, $order_time_ready,
   							  		 $item_data, $car_make_model_info, 
   									 Location $location_info, $arrive_mode, Order $order)
   {
   		global $database;
   		$subject = "Freeway Coffee [ Order: {$order_id}, Cost: $" . $total_cost .", Name: {$user->user_name} ]";
   		$body = "Order for " . $user->user_name . ",\n\n"
   		."Order (#{$order_id}) with a total cost of $" . $total_cost
   		." (ItemsTotal: $" . $total_items_cost .", Discount: $" .$discount .", Tip: $ " .$tip_amount .") was successful.\n\n";
   		
   		if($user->IsUserPayAtLocation()==true)
   		{
   			$body .= "Customer has not paid yet. MUST pay at location\n\n";
   		}
   		else 
   		{
   		 $body .="Total  billed to  ({$credit_card->card_descr}) Ending in ({$credit_card->card_last4}).\n\n";
   
   		}
   		if(strcmp($arrive_mode,ARRIVE_MODE_CAR_STR)==0)
   		{
   			$body .= "In-Car Order\n\n";
   		}
   		else if(strcmp($arrive_mode,ARRIVE_MODE_WALKUP_STR)==0)
   		{
   			$body .= "Walkup Order(Car Info included just in case needed)\n\n";
   		}
   		if( ($car_make_model_info!=NULL) && (strlen($car_make_model_info))>0)
   		{
   			$body .= "Car Info: {$car_make_model_info}\n\n";
   		}
   		
   		if( (!is_null($user->user_tag))  && (strlen($user->user_tag)>0) )
   		{
   			$body .= "Tag: {$user->user_tag}\n\n";
   		}
   		
   		$body .= "Order will be READY at: {$order_time_ready}\n\n";
   		
   		$body .= $this->GetPriceBreakdown($order);
   		
   		
   		$body  .= $this->MakeItemText($item_data);
   		$body  .= "- FreewayCoffee";
   		
   		$retval=true;
   		foreach ($this->store_order_email_list as $to_address)
   		{
   			if(!mail($to_address,$subject,$body,$this->from))
   			{
   				$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
   							"SendStoreOrderEmailNoPDF: Delivery to: {$to_address} Failed for Order: {$order_id}");
   				$retval=false;
 			}
   		}

   		if(strcmp($location_info->LocationEmail,"")!=0)
   		{
   			if(!mail($location_info->LocationEmail,$subject,$body,$this->from))
   			{
   				$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
   						"SendStoreOrderEmailNoPDF: Delivery to: {$location_info->LocationEmail} Failed for Order: {$order_id}");
   				$retval=false;
   			}
   		}
   		return $retval;
   		
   }
  
   
   function GetPriceBreakdown(Order $order)
   {
   		$result = "\n\nPrice Breakdown\n===============\n" 
   		     . "Items Total:$" . $order->orders_items_cost ."\n"
   		     . "Additional:$" . $order->order_convenience_fee . "\n"
   		     . "Tax:$" . $order->order_total_tax . "\n"
   		     . "\nTip:$" . $order->orders_tip_amount
   		     . "\n===============\n"
   		     . "Total:$" . $order->orders_total_cost . "\n\n";
  	 	return $result;
   }
    	
   function SendUserOrderSuccessEmailNoPDF(UserData $user,$order_id,$total_cost,$tip_amount,$credit_card_data,
   										   $order_time_ready,$item_data,
   										   $car_make_model_info,Location $location_info, Order $order)
   {
   		global $database;
   		$subject = "Your Freeway Coffee Order";
  	 	$body = "Hi " . $user->user_name . ",\n\n"
  	 	."Congratulations, your order (#{$order_id}) with a total cost of $" . $total_cost ." (Tip: $ " .$tip_amount .") was successful.\n\n";
  	 	
  	 	if($user->IsUserPayAtLocation()==true)
  	 	{
  	 		$body .="Please pay when you arrive at the location\n\n";
  	 	}
  	 	else
  	 	{
  	 		$body .="Your {$credit_card_data->card_descr} was charged.\n\n";
  	 	}
  	 	
  	 	$body .= "Your order info follows.\n\n"
  	 		."Location: \n" . $location_info->LocationDescription  . "\n"
  	 		. $location_info->LocationAddress . "\n";
  	 	
  	 	if($location_info->LocationShowPhone)
  	 	{
  	 		$body .= "Phone: " .  $location_info->LocationPhone . "\n";
  	 	}
  	 	if($location_info->LocationShowEmail)
  	 	{
  	 		$body .= "Email: " .  $location_info->LocationEmail . "\n\n";
  	 	}
  	 	
  	 	if( ($car_make_model_info!=NULL) && (strlen($car_make_model_info))>0)
 	  	{
  		 	$body .= "Car Info: {$car_make_model_info}\n";
 	  	}
 	  	
  	 	//$body .= "Tag: {$user->user_tag}\n\n";

 	  	$body .=$this->GetPriceBreakdown($order);
  	 	
  	 	
  	 	$body .= $this->MakeItemText($item_data);
   
  	 	$body .= "Have feedback? Please reply and let us know your thoughts.\n\n";
  	 	
   		$body .="- FreewayCoffee";
   
   
   		$retval=true;
   		foreach ($this->store_order_email_list as $to_address)
   		{
   			if(!mail($to_address,$subject,$body,$this->from))
   			{
   				$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,LOG_CAT_EMAIL,
   						"SendStoreOrderEmailNoPDF: Delivery to: {$to_address} Failed for Order: {$order_id}");
   				$retval=false;
   			}
   		}
   		if(!mail($user->user_email,$subject,$body,$this->from))
   		{
   	 
   			$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,LOG_CAT_EMAIL,
   					"SendUserOrderSuccessEmailNoPDF: Delivery to: {$to_address} Failed for Order: {$order_id}");
   			$retval=false;
   			
   		}
   		return $retval;
   }
   
    function SendOrderEmail($user_email,$user_name,$order_id,$total_cost,$card_descr,$card_last4,$order_time_ready,$pdf_mainfilename,$pdf_filenames,
    		$drink_data, $car_make_model_info, $usertag)
    {
        $subject = "Freeway Coffee [ Order: {$order_id}, Cost: $" . $total_cost .", Name: {$user_name} ]";
        $body = "Order for " . $user_name . ",\n\n"
        ."Order (#{$order_id}) with a total cost of $" . $total_cost ." was successful.\n\n"
        ."Total  billed to  ({$card_descr}) Ending in ({$card_last4}).\n\n";
        
        if( ($car_make_model_info!=NULL) && (strlen($car_make_model_info))>0)
        {
        	$body .= "Car Info: {$car_make_model_info}\n\n";
        }
        $body .= "Tag: {$usertag}\n\n";
        
        $body .= "Order will be READY at: {$order_time_ready}\n\n";
        $body  .= $this->MakeItemText($drink_data);
   	    $body  .= "- FreewayCoffee";
   
    
   	    /* REMOVED (Ontime F-213)
        $fileatttype = "application/pdf";
        
        
        // Attach main file
        $main_file = fopen($pdf_mainfilename, 'rb');
        
        $data = fread($main_file, filesize($pdf_mainfilename));
        
        fclose($main_file);
        $data = chunk_split(base64_encode($data));
        
        $semi_rand = md5(time());
        
        $mime_boundary = "Multipart_Boundary_x{$semi_rand}x";
        
         
        $headers = $this->from . "n" . "\nMIME-Version: 1.0\n" . "Content-Type: multipart/mixed;" . " boundary=\"{$mime_boundary}\"";
        
        $message = "--{$mime_boundary}\n" . "Content-Type: text/plain; charset=\"iso-8859-1\"\n" .
        "Content-Transfer-Encoding: 7bit\n\n" . $body . "\n\n"; 
        
        $message .= "--{$mime_boundary}\n";
        
        $message .= "Content-Type: application/octet-stream; name=\"".basename($pdf_mainfilename)."\"\n" .
        "Content-Description: ".basename($pdf_mainfilename)."\n" .
        "Content-Disposition: attachment;" . " filename=\"".basename($pdf_mainfilename)."\"; size=".filesize($pdf_mainfilename).";\n" .
        "Content-Transfer-Encoding: base64\n\n" . $data . "\n\n";

        $index=0;
        foreach($pdf_filenames as $file)
        {
            //print($pdf_filenames[$index]);
            
            $file=fopen($pdf_filenames[$index], 'rb');
            $data = fread($file, filesize($pdf_filenames[$index]));
            
            fclose($file);
            $data = chunk_split(base64_encode($data));

            $message .= "--{$mime_boundary}\n";
            
            $message .= "Content-Type: application/octet-stream; name=\"".basename($pdf_filenames[$index])."\"\n" .
            "Content-Description: ".basename($pdf_filenames[$index])."\n" .
            "Content-Disposition: attachment;" . " filename=\"".basename($pdf_filenames[$index])."\"; size=".filesize($pdf_filenames[$index]).";\n" .
            "Content-Transfer-Encoding: base64\n\n" . $data . "\n\n";
            $index++;
        }
        $message .= "--{$mime_boundary}--";
        
        */
        
        // F-213 if(!mail(ORDER_EMAIL,$subject,$message,$headers))
   	    if(!mail(ORDER_EMAIL,$subject,$body,$this->from))
		{
			//print ("crap crap");
            return false;
		}
		return true;
        
    }
    
    function SendFeedbackEmail($user_id,$email_to_use,$feedback_code,$feedback,$feedback_happiness,$date_time_now)
    {
    	global $user;
    	global $database;
    	
    	$subject = "Freeway Coffee - Feedback {$email_to_use } Feedbaack: {$feedback}";
    	
    	$body = "Feedback from {$email_to_use} \n\n Date: {$date_time_now}\n\n Feedback Type({$feedback_code}): " . $user->GetFeebackTypeStringFromID($feedback_code) .
    	        "\n\nHappiness: {$feedback_happiness} \n\n Feedback:\n {$feedback}";
    	
    	$retval=true;
    	if(!mail(FEEDBACK_EMAIL,$subject,$body,$this->from))
    	{
    		$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,LOG_CAT_EMAIL,
    				"SendFeedbackEmail: Delivery to: {$to_address} Failed for Order: {$order_id}");
    		$retval=false;
    	}
    	if(!mail('brendan@freewaycoffee.com',$subject,$body,$this->from))
    	{
    		$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,LOG_CAT_EMAIL,
    				"SendFeedbackEmail: Delivery to: {$to_address} Failed for Order: {$order_id}");
    		$retval=false;
    	}
    	
    }
    
    function SendUserHereForOrder($user_email,$user_name,$order_id,$time_here,$arrived_mode,Location $location_info)
    {
        $arrive_str="Unknown";
        
        if(strcmp($arrived_mode,ARRIVE_MODE_CAR_STR)==0)
        {
        	$arrive_str = "In-Car Order";
        }
        else if(strcmp($arrived_mode,ARRIVE_MODE_WALKUP_STR)==0)
        {
        	$arrive_str = "Walkup Order";
        }
        $subject = "Freeway Coffee - User {$user_name} Here ({$arrive_str}) for Order {$order_id} @ {$time_here} ";
        $body = "Customer Arrived";
        
        $body .= "{$arrive_str}\n\n";
        
        $retval=true;
        
        $retval=true;
        foreach ($this->store_order_email_list as $to_address)
        {
        	if(!mail($to_address,$subject,$body,$this->from))
        	{
        		$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
        				"SendStoreOrderEmailNoPDF: Delivery to: {$to_address} Failed for Order: {$order_id}");
        		$retval=false;
        	}
        }
         
         
        if(strcmp($location_info->LocationEmail,"")!=0)
        {
        	if(!mail($location_info->LocationEmail,$subject,$body,$this->from))
        	{
        		$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
        				"SendStoreOrderEmailNoPDF: Delivery to: {$location_info->LocationEmail} Failed for Order: {$order_id}");
        		$retval=false;
        	}
        }
        
        if(!mail(ORDER_EMAIL,$subject,$body,$this->from))
		{
			$database->AddLogEntryReal($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_SUCCESS,
					"SendUserHereForOrder: Delivery to: {$location_info[USER_LOCATION_EMAIL]} Failed for Order: {$order_id}");
			$retval=false;
			
            return false;
		}
		return $retval;
    }
    
    function SendCreditCardInfoChanged($user_email, $user_name, $card_descr, $card_last4)
    {
        $subject = "Freeway Coffee - Credit Card Change";
        $body = "Hi " . $user_name . ",\n\n"
                . "Someone (hopefully you) attempted to change your credit card on file to:\n\n CardType: {$card_descr} ending in : {$card_last4}\n"
                . "If you did not initiate this action, please call your bank to check for unauthorized access, change your password and generally run around like crazy.\n\n"
                . "- FreewayCoffee";
        if(!mail($user_email,$subject,$body,$this->from))
		{
			//die ("crap");
             return false;
		}
		return true;
    }
    
    
    function MakeItemText($item_data)
    {
    	$result = "Order Summary:\n\n";
    	$current_item_type="";
    	if($item_data!=NULL)
	    {
	    	foreach($item_data as $item)
	    	{
	    		if( (strcmp($current_item_type,"")==0) || (strcmp($current_item_type,$item->GetItemTypeAsString())!=0) )
	    		{
	    			$current_item_type = $item->GetItemTypeAsString();
	    			
	    			$result .= $current_item_type . "s:\n\n";
	    		}
	    		$result .= $item->MakeDrinkText();
	    		$result .= "\n\n";
	    	}
	    } 	
        return $result;
        	
    }
    
    
   /**
    * sendNewPass - Sends the newly generated password
    * to the user's email address that was specified at
    * sign-up.
    */
/*   function sendNewPass($user, $email, $pass){
      $from = "From: ".EMAIL_FROM_NAME." <".EMAIL_FROM_ADDR.">";
      $subject = "Jpmaster77's Site - Your new password";
      $body = $user.",\n\n"
             ."We've generated a new password for you at your "
             ."request, you can use this new password with your "
             ."username to log in to Jpmaster77's Site.\n\n"
             ."Username: ".$user."\n"
             ."New Password: ".$pass."\n\n"
             ."It is recommended that you change your password "
             ."to something that is easier to remember, which "
             ."can be done by going to the My Account page "
             ."after signing in.\n\n"
             ."- Jpmaster77's Site";
             
      return mail($email,$subject,$body,$from);
   }
 */
};

/* Initialize mailer object */
//$mailer = new Mailer;
 
?>
