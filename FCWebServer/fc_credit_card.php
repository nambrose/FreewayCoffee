<?php
	
    /* Nick Ambrose
     * Credit Cards
     * (C) Copyright Freeway Coffee, 2011
     */
    
    
//    define("CARD_TYPE_MC", 0); 
//    define("CARD_TYPE_VS", 1);  
//    define("CARD_TYPE_AX", 2); 
//    define("CARD_TYPE_DC", 3);  
//    define("CARD_TYPE_DS", 4);  
//    define("CARD_TYPE_JC", 5);   
    
    // Provider 1=e-onlinedata
    class OrderCreditCard
    {
        var $card_id;
        var $profile_id;
        var $provider_id;
        var $trans_id;
        var $auth_code;
        var $refund_auth_code;
        var $card_type;
        var $card_last4;
        var $card_descr;
        
        static $csv_columns = array("Card ID","Profile ID","Provider ID","Trans ID","Auth Code","Refund Auth Code","Card Type","Last Four","Card Descr");
        
        function PrintAsXML()
        {
        	print("<" . ORDER_CREDIT_CARD_TAG . " id=\"" . rawurlencode($this->card_id)  . "\" " .
        			ORDER_CREDIT_CARD_PROFILE_ID_ATTR . "=\"" . rawurlencode($this->profile_id) . "\" " .
        			ORDER_CREDIT_CARD_PROVIDER_ID_ATTR . "=\"" . rawurlencode($this->provider_id) . "\" " .
        			ORDER_CREDIT_CARD_TRANS_ID_ATTR . "=\"" . rawurlencode($this->trans_id) . "\" " .
        			ORDER_CREDIT_CARD_AUTH_CODE . "=\"" . rawurlencode($this->auth_code) . "\" " .
        			ORDER_CREDIT_CARD_REFUND_AUTH_CODE . "=\"" . rawurlencode($this->refund_auth_code) . "\" " .
        			ORDER_CREDIT_CARD_CARD_TYPE  . "=\"" . rawurlencode($this->card_type). "\" " .
        			ORDER_CREDIT_CARD_CARD_LAST4 . "=\"" . rawurlencode($this->card_last4) . "\" " .
        			ORDER_CREDIT_CARD_DESCR . "=\"" . rawurlencode($this->card_descr) . "\" " .
        				
        			">" . "</" . ORDER_CREDIT_CARD_TAG . ">");
        }
        
        
        
        static function PrintCSVHeaders($output_file)
        {
        	return fputcsv($output_file,OrderCreditCard::$csv_columns);
        }
        
        function PrintAsCSV($output_file)
        {
        	$csv_data = array();
        	$csv_data[]=$this->card_id;
        	$csv_data[]=$this->profile_id;
        	$csv_data[]=$this->provider_id;
        	$csv_data[]=$this->trans_id;
        	$csv_data[]=$this->auth_code;
        	$csv_data[]=$this->refund_auth_code;
        	$csv_data[]=$this->card_type;
        	$csv_data[]=$this->card_last4;
        	$csv_data[]=$this->card_descr;
        	
        	fputcsv($output_file,$csv_data);
        		
        }
        

    }

    
    class CreditCard
    {
        var $card_number;
        var $card_exp_month;
        var $card_exp_year;
        var $card_billing_zip;

        var $card_type; // Set internally
    	static $zip_five_zeroes="00000";
    	
        function CreditCard($number, $exp_month, $exp_year, $billing_zip)
        {
            $this->card_number=$number;
            $this->card_exp_year=$exp_year;
            $this->card_exp_month=$exp_month;
            $this->card_billing_zip = $billing_zip;
            
            $this->card_type = $this->generateCardType();
            
        }
    
        function GetExpirationForAuthNet()
        {
        	$card_exp = "";
        	
        	$card_exp = $this->card_exp_year . "-";
        	if(strlen($this->card_exp_month)<2)
        	{
        		$card_exp .= "0";
        	}
        	$card_exp .= $this->card_exp_month;
        	//print("EXP: {$card_exp}");
        	return $card_exp;
      	
        }
        
        function IsZipValidFormat()
        {
        	if(is_null($this->card_billing_zip))
        	{
        		return false;
        	}
        	if(strlen($this->card_billing_zip)<5)
        	{
        		return false;
        	}
        	if(strcmp($this->card_billing_zip,CreditCard::$zip_five_zeroes)==0)
        	{
        		return false;
        	}
        	return true;
        }
        function generateCardType()
        {
            
            if(preg_match("#^5[1-5][0-9]{14}$#", $this->card_number)!=0)
            {
                return CARD_TYPE_MC;
                
            }
            else if(preg_match("#^4[0-9]{12}([0-9]{3})?$#",$this->card_number)!=0)
            {
                return CARD_TYPE_VS;
                
            }
            else if(preg_match("#^3[47][0-9]{13}$#",$this->card_number)!=0)
            {
                return CARD_TYPE_AX;
            }
            else if(preg_match("#^3(0[0-5]|[68][0-9])[0-9]{11}$#",$this->card_number)!=0)
            {
                return CARD_TYPE_DC;
            }
            else if(preg_match("#^6011[0-9]{12}$#",$this->card_number)!=0)
            {
                return CARD_TYPE_DS;
            }
            else if(preg_match("#^(3[0-9]{4}|2131|1800)[0-9]{11}$#",$this->card_number)!=0)
            {
                return CARD_TYPE_JC;
            }
            
            return 0;
        }
                
                
                
        function GetLastFour()
        {
            $chars=4;
            $last_four = substr($this->card_number, strlen($this->card_number)-$chars,$chars);
            return $last_four;
        }
    
        function getCardTypeString()
        {
            return CreditCard::s_getCardTypeString($this->card_type);
        }
        
        static function s_getCardTypeString($card_type)
        {
            switch($card_type)  
            {      
                case CARD_TYPE_MC:  
                    return 'Mastercard';  
                    break; 
                case CARD_TYPE_VS:  
                    return 'Visa';  
                    break;  
                case CARD_TYPE_AX:  
                    return 'Amex';  
                    break;   
                case CARD_TYPE_DC:  
                    return 'Diners Club';  
                    break;  
                case CARD_TYPE_DS:  
                    return 'Discover';  
                    break;  
                case CARD_TYPE_JC:  
                    return 'JCB';  
                    break;   
                default:    
                    return 'Unknown';
            }
        }
    
    }
?>
