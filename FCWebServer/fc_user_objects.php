<?php
	
    /* Nick Ambrose
     * User Objects 
     * (C) Copyright Freeway Coffee, 2012, 2013
     */

	require_once ("fc_constants.php");
	
	class UserTip
	{
		var $user_id;
		var $location_id;
		var $tip_type;
		var $tip_amount;
		var $round_up;
		
		function PrintAsXML()
		{
			print("<" . USER_TIP_TAG . " " .
					USER_ID_ATTR . "=\"" . rawurlencode($this->user_id)  . "\" " .
					LOCATION_ID_ATTR . "=\"" . rawurlencode($this->location_id) . "\" " .
					USER_TIP_TYPE_ATTR . "=\"" . rawurlencode($this->tip_type) . "\" " .
					USER_TIP_AMOUNT_ATTR . "=\"" . rawurlencode($this->tip_amount) . "\" " .
					USER_TIP_ROUND_UP_ATTR . "=\"" . rawurlencode($this->round_up) . "\" " );
				
			print ("></" . USER_TIP_TAG . ">");
		}
		
		function CalculateDollarAmount($amount_to_tip_on)
		{
			// No "extra" mode --- just return tip amount
			if($this->round_up==TRUE)
			{
				$result = bcadd($this->tip_amount,$amount_to_tip_on);
				$result = bcadd($result,"1.00",0); // Add one dollar and truncate
				$result = bcsub($result, $amount_to_tip_on); // Minus off what we started with for a tip.
				return $result;
			}
			else 
			{
				return $this->tip_amount;
			}
		}
	}

?>