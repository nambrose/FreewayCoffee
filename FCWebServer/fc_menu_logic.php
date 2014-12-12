<?php
    /**
     * fc_drink_pick_logic.php
     * 
     *
     * (C) Copyright Freeway Coffee, 2011,2012,2013
     */
	
	class MenuLogic
	{
		static $GET_MENU_RESPONSE_TAG="get_menu_response";
		
		// The overall wrapper for response. May or may not include a menu
		function PrintGetMenuResponseStart($result,$object_response_text,$object_ver)
		{
			print("<" . MenuLogic::$GET_MENU_RESPONSE_TAG . " " .
					RESULT_ATTR . "=\"" . rawurlencode($result) . "\" " .
					OBJECT_RESPONSE_ATTR  . "=\"" . rawurlencode($object_response_text) . "\" " );
			if(!is_null($object_ver))
			{
				print(OBJECT_RESPONSE_LATEST_VER_ATTR . "=\"" . rawurlencode($object_ver) . "\" " );
			}
			print(">"); // Close the attributes
		
		}
		 
		static function CheckMenuVersion($menu_id,$user_menu_ver)
		{
			//print("MenuID: " . $menuid);
			//print("Incoming Menu Ver:" . $user_menu_ver);
			
			if(is_null($user_menu_ver))
			{
				return false; // Need to get a new menu dude.
			}
				
			if(Menu::IsNoneMenuID($menu_id))
			{
				return false;
			}
			
			$menu = Menu::DB_GetMenu($menu_id);
			if($menu==NULL)
			{
				return false;
			}
			
			if($menu->IsNoneMenu())
			{
				return false;
			}
			
			if($menu->menu_revision!=$user_menu_ver)
			{
				return false;
			}
			return true;
		}
		
		
		function PrintGetMenuResponseEnd()
		{
					print ("</" . MenuLogic::$GET_MENU_RESPONSE_TAG . ">");
			//$this->
		}
		
		function PrintMenuForUser($user_id)
		{
			// FALSE is the "if needed" flag
			$this->PrintMenuForUserIfNeeded($user_id,FALSE,Menu::$MENU_NONE_VERSION);
		}
		
		function PrintMenuForUserIfNeeded($user_id,$if_needed,$has_menu_version)
		{
			global $session;
		
			// if its the "None" Location, print an empty menu and we are done
			if($session->user_location->isNoneLocation())
			{
				$this->PrintGetMenuResponseStart(COMMAND_FAILED,OBJECT_RESPONSE_NOT_EXIST,NULL);
				$this->PrintGetMenuResponseEnd();
				return;
			}
			
			$menu = Menu::DB_GetMenu($session->user_location->LocationMenuID);
			if($menu==NULL)
			{
				$this->PrintGetMenuResponseStart(COMMAND_FAILED,OBJECT_RESPONSE_NOT_EXIST,NULL);
				$this->PrintGetMenuResponseEnd();
				return;
			}
			
			if($menu->IsNoneMenu())
			{
				$this->PrintGetMenuResponseStart(COMMAND_FAILED,OBJECT_RESPONSE_NOT_EXIST,NULL);
				$this->PrintGetMenuResponseEnd();
				return;
			}
			// TODO LATER -- see if we have a cached XML File here
			if($if_needed==TRUE)
			{
				// Doing a conditional get
				if($has_menu_version==$menu->menu_revision)
				{
					// Client already has it
					$this->PrintGetMenuResponseStart(COMMAND_SUCCESS,OBJECT_RESPONSE_HAVE_LATEST,$has_menu_version);
				}
				else
				{
					// Client is out of date and asked for it (not just a check version)
					// Including the current rev is redundant but OK
					$this->PrintGetMenuResponseStart(COMMAND_SUCCESS,OBJECT_RESPONSE_OBJ_INCLUDED,$menu->menu_revision);
				}
			}
			else
			{
				// Not conditional
				// Including the current rev is redundant but OK
				$this->PrintGetMenuResponseStart(COMMAND_SUCCESS,OBJECT_RESPONSE_OBJ_INCLUDED,$menu->menu_revision);
			}
			$menu->PrintAsXML();
			$this->PrintGetMenuResponseEnd();
		}
		
		function PrintMenu($menu_id)
		{
			// FALSE is the "if needed" flag
			$this->PrintMenuIfNeeded($menu_id,FALSE,Menu::$MENU_NONE_VERSION);
		}
		
		function PrintMenuIfNeeded($menu_id,$if_needed,$has_menu_version)
		{
			global $session;
		
			// if its the "None" Location, print an empty menu and we are done
			if(Menu::IsNoneMenuID($menu_id))
			{
				$this->PrintGetMenuResponseStart(COMMAND_FAILED,OBJECT_RESPONSE_NOT_EXIST,NULL);
				$this->PrintGetMenuResponseEnd();
				return;
			}
				
			$menu = Menu::DB_GetMenu($menu_id);
			if($menu==NULL)
			{
				$this->PrintGetMenuResponseStart(COMMAND_FAILED,OBJECT_RESPONSE_NOT_EXIST,NULL);
				$this->PrintGetMenuResponseEnd();
				return;
			}
				
			if($menu->IsNoneMenu())
			{
				$this->PrintGetMenuResponseStart(COMMAND_FAILED,OBJECT_RESPONSE_NOT_EXIST,NULL);
				$this->PrintGetMenuResponseEnd();
				return;
			}
			// TODO LATER -- see if we have a cached XML File here
			if($if_needed==TRUE)
			{
				// Doing a conditional get
				if($has_menu_version==$menu->menu_revision)
				{
					// Client already has it
					$this->PrintGetMenuResponseStart(COMMAND_SUCCESS,OBJECT_RESPONSE_HAVE_LATEST,$has_menu_version);
				}
				else
				{
					// Client is out of date and asked for it (not just a check version)
					// Including the current rev is redundant but OK
					$this->PrintGetMenuResponseStart(COMMAND_SUCCESS,OBJECT_RESPONSE_OBJ_INCLUDED,$menu->menu_revision);
				}
			}
			else
			{
				// Not conditional
				// Including the current rev is redundant but OK
				$this->PrintGetMenuResponseStart(COMMAND_SUCCESS,OBJECT_RESPONSE_OBJ_INCLUDED,$menu->menu_revision);
			}
			$menu->PrintAsXML();
			$this->PrintGetMenuResponseEnd();
		}
	}
    class Menu
    {
    	static $NONE_MENU_ID=0;
    	static $MENU_NONE_VERSION=0; // No real menu would have this
    	static $MENU_TAG="menu";
    	
    	static $MENU_REVISION_ATTR="m_rev";
    	static $MENU_NAME_ATTR="m_name";
    	
    	static $COMPAT_LEVEL_ATTR_VALUE="3";
    	
    	var $valid;
    	var $menu_id;
    	var $menu_name;
    	var $menu_revision;
    	
    	
    	static function DB_GetMenu($menu_id)
    	{
    		global $database;
    		global $session;
    		
    		$stmt = mysqli_stmt_init($database->getConnection());
    		
    		mysqli_stmt_prepare($stmt,
    				"SELECT menu_id, menu_name, menu_version from menus where menu_id=?");
    		 
    		mysqli_stmt_bind_param($stmt,'i',$menu_id);
    		if(mysqli_stmt_execute($stmt))
    		{
    			mysqli_stmt_store_result($stmt);
    		}
    		else
    		{
    			$database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    					"Menu::DB_GetMenu: Menu:[{$menu_id}] Could not store result from SELECT (" . mysqli_stmt_error($stmt) . ")" );
    			return NULL;
    		}
    		
    		$menu = new Menu();
    		 
    		if(!mysqli_stmt_bind_result($stmt,
    				$menu->menu_id,
    				$menu->menu_name,
    				$menu->menu_revision))
    				
    		{
    			 
    			$this->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
    					"Menu::DB_GetMenu: Menu:[{$menu_id}] Could not bind result from GetLocationInfo SELECT (" . mysqli_stmt_error($stmt) . ")");
    			mysqli_stmt_close($stmt);
    			return NULL;
    		}
    		if(!mysqli_stmt_fetch($stmt))
    		{
    			mysqli_stmt_close($stmt);
    			return NULL;
    		}
    		mysqli_stmt_close($stmt);
    		$menu->valid=true;
    		return $menu;
    	}
    	
    	function Menu()
    	{
    		$this->valid=false;
    	}
    	
    	static function IsNoneMenuID($menu_id)
    	{
    		if(is_null($menu_id))
    		{
    			return TRUE;
    		}
    		return ($menu_id == Menu::$NONE_MENU_ID);
    		
    	}
    	function IsNoneMenu()
    	{
    		if($this->menu_id==Menu::$NONE_MENU_ID)
    		{
    			return true;
    		}
    		return false;
    	} 
    	
    	
    	
    	static function PrintEmptyMenuAsXML()
    	{
    		print("<" . Menu::$MENU_TAG . " id=\"" . rawurlencode(Menu::$NONE_MENU_ID) ."\" " .
    				Menu::$MENU_REVISION_ATTR . "=\"" . rawurlencode("0") ."\" " .
    				COMPAT_LEVEL_ATTR . "=\"" . rawurlencode("0") ."\" " . 
    				"></" . Menu::$MENU_TAG . ">");
    	}
    	
    	function PrintMenuHeaderAsXML()
    	{
    		print("<" . Menu::$MENU_TAG . " id=\"" . rawurlencode($this->menu_id) ."\" " .
    				Menu::$MENU_REVISION_ATTR. "=\"" . rawurlencode($this->menu_revision) ."\" " .
    				Menu::$MENU_NAME_ATTR. "=\"" . rawurlencode($this->menu_name) ."\" " .
    				COMPAT_LEVEL_ATTR . "=\"" . rawurlencode(Menu::$COMPAT_LEVEL_ATTR_VALUE) ."\" " .
    				">");
    	}
    	
    	function PrintMenuFooterAsXML()
    	{
    		print ("</" . Menu::$MENU_TAG . ">");
    	}
    	
    	function PrintAsXML()
    	{
    		$this->PrintMenuHeaderAsXML();
    		
    		// Drink Option Groups (includes Drink Options);
    		$this->DB_PrintDrinkOptionGroups();
    		
    		// Item Type Groups
    		$this->DB_PrintItemTypeGroups();
    		
    		// Now the drink types
    		$this->PrintDrinkTypes();
    		
    		// Mandatory Drink Options
    		$this->DB_PrintMandatoryDrinkOptionsAsXML();
    		
    		// Drink Default Options
    		$this->DB_PrintDrinkTypesDefaultOptionsAsXML();
    		
    		$this->PrintMenuFooterAsXML();
    		
    	}
        
    	function DB_PrintItemTypeGroups()
    	{
    		global $database;
    		
    		print("<" .ItemTypeGroup::$ITEM_TYPE_GROUP_LIST_TAG .">");
    		
    		$stmt = $database->OpenGetItemTypeGroups($this->menu_id,EnumFetchActive::FETCH_ACTIVE_ONLY);
    		$item_type_group = $database->GetNextItemTypeGroups($stmt);
    		 
    		while($item_type_group!=null)
    		{
    			$item_type_group->PrintAsXML();
    		
    			
    			$item_type_group = $database->GetNextItemTypeGroups($stmt);
    		}
    		print("</" .ItemTypeGroup::$ITEM_TYPE_GROUP_LIST_TAG .">");
    		
    		$database->CloseGetItemTypeGroups($stmt);
    		
    		
    	}
        function DB_PrintDrinkOptionGroups()
        {
        	global $database;
        	
        	print("<" . DRINK_OPTION_GROUPS_LIST_TAG . ">");
        	
        	$stmt = $database->OpenGetDrinkOptionGroups($this->menu_id,EnumFetchActive::FETCH_ACTIVE_ONLY);
        	if(!is_null($stmt))
        	{
        		$drink_option_group = $database->GetNextDrinkOptionGroups($stmt);
        	
        		while($drink_option_group!=null)
        		{
        			$drink_option_group->PrintAsXML_noClosingTag();
        		
        			// Now print Drink Options for this Drink Option Group
        			$this->DB_PrintDrinkOptionsForDrinkOptionGroupID($drink_option_group->group_id,EnumFetchActive::FETCH_ACTIVE_ONLY);
        			$drink_option_group->PrintClosingTag();
        			$drink_option_group = $database->GetNextDrinkOptionGroups($stmt);
        		}
        	}
        	print("</" . DRINK_OPTION_GROUPS_LIST_TAG . ">");
        	if(!is_null($stmt))
        	{
        		$database->CloseGetDrinkOptionGroups($stmt);
        	}
        }
        
        function DB_PrintDrinkTypeOptionsForDrinkTypeID($drink_type_id,$active_mode)
        {
        	global $database;
        	 
        	print("<" . DRINK_TYPE_OPTIONS_LIST_TAG .">");
        	 
        	// Iterate through each Option for this Group
        	$stmt=$database->OpenGetDrinkTypeOptionsForDrinkTypeID($drink_type_id,$active_mode);
        	if(!is_null($stmt))
        	{
        		$drink_type_option=$database->GetNextDrinkTypeOptionsForDrinkTypeID($stmt,$this->menu_id);
        		while($drink_type_option!=NULL)
        		{
        			$drink_type_option->PrintAsXML();
        		
        			$drink_type_option=$database->GetNextDrinkTypeOptionsForDrinkTypeID($stmt,$this->menu_id);
        		}
        		$database->CloseGetDrinkTypeOptionsForDrinkTypeID($stmt);
        	}
        	
        	print("</" . DRINK_TYPE_OPTIONS_LIST_TAG . ">");
        }
        function DB_PrintDrinkOptionsForDrinkOptionGroupID($group_id,$active_mode)
        {
        	global $database;
        	
        	print("<" . DRINK_OPTIONS_LIST_TAG . ">");
        	
        	// Iterate through each Option for this Group
        	$stmt=$database->OpenGetDrinkOptionsForDrinkOptionGroup($group_id,$active_mode);
        	if(!is_null($stmt))
        	{
        		$drink_option=$database->GetNextDrinkOptionsForDrinkOptionGroup($stmt,$this->menu_id);
        		while($drink_option!=NULL)
        		{
        			$drink_option->PrintAsXML();
        		
        			$drink_option=$database->GetNextDrinkOptionsForDrinkOptionGroup($stmt,$this->menu_id);
        		}
        	 
        		$database->CloseGetDrinkOptionsForDrinkOptionsGroup($stmt);
        	}
        	print("</" . DRINK_OPTIONS_LIST_TAG .">");
        	 
        }
        
        function DB_PrintDrinkTypesDefaultOptionsAsXML()
        {
            global $database;
            
            print("<" . DRINK_TYPES_DEFAULT_OPTION_LIST_TAG . ">");
            
            $stmt = $database->OpenDrinkTypesDefaultOptions($this->menu_id,EnumFetchActive::FETCH_ACTIVE_ONLY);
            if(!is_null($stmt))
            {
            	$default_option = $database->GetNextDrinkTypesDefaultOptions($stmt);
            
            	while($default_option!=NULL)
            	{
                	$default_option->PrintAsXML();
                	$default_option = $database->GetNextDrinkTypesDefaultOptions($stmt);
            	}
            	$database->CloseDrinkTypesDefaultOptions($stmt);
            }
            print("</" . DRINK_TYPES_DEFAULT_OPTION_LIST_TAG . ">");

        }
        
        function DB_PrintMandatoryDrinkOptionsAsXML()
        {
            global $database;
            
            print("<" . DRINK_TYPES_MAND_OPTION_LIST_TAG . ">");
            
            $stmt = $database->OpenDrinkTypesMandatoryOptions($this->menu_id,EnumFetchActive::FETCH_ACTIVE_ONLY);
            if(!is_null($stmt))
            {
            	$mand_option = $database->GetNextDrinkTypesMandatoryOptions($stmt);
            
            	while($mand_option!=NULL)
            	{
                	$mand_option->PrintAsXML();
                	$mand_option = $database->GetNextDrinkTypesMandatoryOptions($stmt);
            	}
            	$database->CloseDrinkTypesMandatoryOptions($stmt);
            }
           	print("</" . DRINK_TYPES_MAND_OPTION_LIST_TAG . ">");
        }
        
        function PrintDrinkTypes()
        {
            global $database;
            
            print("<" . DRINK_TYPES_LIST_TAG . ">");
            
            // Iterate through each drink type
            $stmt=$database->OpenGetDrinkTypes($this->menu_id,EnumFetchActive::FETCH_ACTIVE_ONLY);
            if(!is_null($stmt))
            {
            	$drink_type=$database->GetNextDrinkTypes($stmt);
            	while($drink_type!=NULL)
            	{   
            		$drink_type->PrintAsXML_noClosingTag();
          			// Drink Type Options.....
            		$this->DB_PrintDrinkTypeOptionsForDrinkTypeID($drink_type->drink_type_id,EnumFetchActive::FETCH_ACTIVE_ONLY);
          			$drink_type->PrintXMLClosingTag();
               
                	$drink_type=$database->GetNextDrinkTypes($stmt);
            	}
            	$database->CloseGetDrinkTypes($stmt);
            }
           
            print("</" . DRINK_TYPES_LIST_TAG .">");

            
        }
        
    }
    
?>
