<?php
	
    /* Nick Ambrose
     * Food
     * (C) Copyright Freeway Coffee,2012
     */

	require_once ("fc_constants.php");
	
	class AppSetting
	{
		var $setting_name;
		var $setting_value;
		
		function PrintAsXML()
		{
			print("<" . APP_SETTING_TAG . " " .
					APP_SETTING_NAME_ATTR . "=\"" . rawurlencode($this->setting_name)  . "\" " .
					APP_SETTING_VALUE_ATTR . "=\"" . rawurlencode($this->setting_value) . "\" " 
					);
				
			print ("></" . APP_SETTING_TAG . ">");
		}
	}

?>