<?php

define ("ERROR_MAJOR_NONE",0);
define ("ERROR_MAJOR_BILLING",1);
define ("ERROR_MAJOR_DATABASE",2);
define ("ERROR_MAJOR_INTERNAL",3);
define ("ERROR_MAJOR_LOCATION",4);
define ("ERROR_MAJOR_SIGNUP",5);
define ("ERROR_MAJOR_SIGNON",6);
define ("ERROR_MAJOR_VALIDATION",7);
define ("ERROR_MAJOR_OUT_OF_SYNC",8);

define ("ERROR_MINOR_NONE",0);

// MINOR DATABASE
define ("ERROR_MINOR_DATABASE_GENERAL",0);

// MINOR INTERNAL
define ("ERROR_MINOR_STATE_NOT_FOUND",0);

// MINOR BILLING
define ("ERROR_MINOR_BILLING_ZERO_COST",0);
define ("ERROR_MINOR_BILLING_NO_CARD",1);
define ("ERROR_MINOR_BILLING_NEGATIVE_COST",2);
// Strings

// MINOR LOCATION
define ("ERROR_MINOR_LOCATION_CLOSED",0);
define ("ERROR_MINOR_LOCATION_ARRIVE_NOT_SUPPORTED",1);
define ("ERROR_MINOR_LOCATION_PAY_METHOD_NOT_SUPPORTED",2);

// MAJOR
define ("ERROR_MAJOR_BILLING_TEXT","Billing Error");


// DATABASE TEXT
define ("ERROR_DATABASE_GENERAL_TEXT","Database Execution Error");

// SIGNUP
define("ERROR_MINOR_SIGNUP_INTERNAL",0);
define("ERROR_MINOR_SIGNUP_VERSION",1);
define("ERROR_MINOR_SIGNUP_AUTH_FAIL",2);

// SIGNON
define("ERROR_MINOR_SIGNON_VERSION",0);
define("ERROR_MINOR_SIGNON_INTERNAL",1);

// VALIDATION
define("ERROR_MINOR_VALID_OUT_OF_RANGE",0);

// OUT_OF_SYNC
define("ERROR_MINOR_USER_OUT_OF_SYNC",0);
define("ERROR_MINOR_USER_LOC_OUT_OF_SYNC",1);
class FCError
{
	var $ErrorCodeMajor;
	var $ErrorCodeMinor;
	var $DisplayText;
	var $LongText;
	var $Reportable; // Hint to client to allow user to report it (or piggyback on a later request)
	
	function __construct()
	{
		$this->Reset();
	}
	
	function Reset()
	{
		$this->ErrorCodeMajor=ERROR_MAJOR_NONE;
		$this->ErrorCodeMinor=ERROR_MINOR_NONE;
		$this->DisplayText="";
		$this->LongText="";
	}
	
	function PrintAsXML()
	{
		print("<" . ERROR_TAG  );
		print(" " . ERROR_CODE_MAJOR . "=\"" . rawurlencode($this->ErrorCodeMajor) . "\" ");
		print(" " . ERROR_CODE_MINOR . "=\"" . rawurlencode($this->ErrorCodeMinor) . "\" ");
		print(" " . ERROR_DISPLAY_TEXT . "=\"" . rawurlencode($this->DisplayText) . "\" ");
		print(" " . ERROR_LONG_TEXT . "=\"" . rawurlencode($this->LongText) . "\" ");
		print ("></" . ERROR_TAG . ">");
		
	}
	
	function GetForEmail()
	{
		$result = " ErrorCode Major: " . $this->ErrorCodeMajor ."\n" 
		. " ErrorCode Minorr: " . $this->ErrorCodeMinor ."\n"
		. " Display Text: " . $this->DisplayText . "\n"
		. " Internal Text: " . $this->LongText. "\n";
		return $result;
		
	}
}
?>