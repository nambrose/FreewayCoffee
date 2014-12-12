<?php
	
    /* Nick Ambrose
     * Item Objects
     * (C) Copyright Freeway Coffee, 2011,2012
     */
    
	// Implements a "Grouping" of items. 
	// Could be simple like drink/food
	// Or could be more complex like (Organic,Specials,Cold Drinks,Nackies) etc
	// Each Drink Type has a ItemTypeGroupID
	// Obviously each TypeGroup is menu-specific
	class ItemTypeGroup
	{
		static $ITEM_TYPE_GROUP_LIST_TAG="i_t_g_l";
		static $ITEM_TYPE_GROUP_TAG="i_t_g";
		static $ITEM_TYPE_GROUP_NAME="i_t_g_n";
		
		
		var $id;
		var $menu_id; 
		var $group_name;
		var $is_ative;
		var $sort_order;
		
		function PrintAsXML()
		{
			print("<" . ItemTypeGroup::$ITEM_TYPE_GROUP_TAG .
					" id=\"" . $this->id . "\" " .
					MENU_ID_ATTR . "=\"" . rawurlencode($this->menu_id) . "\" " .
					ItemTypeGroup::$ITEM_TYPE_GROUP_NAME . "=\"" . rawurlencode($this->group_name) . "\" " .
					SORT_ORDER_SHORT_ATTR . "=\"" . rawurlencode($this->sort_order) . "\" " .
					"></" . ItemTypeGroup::$ITEM_TYPE_GROUP_TAG . ">"
					);
		}
	}
	
    
	class DrinkOptionGroup
	{
		var $group_id;
		var $group_part_name;
		var $group_long_name;
		var $group_multi_select;
		var $sort_order;
		
		var $dog_menu_id;
		var $is_active;
		
		function PrintAsXML_noClosingTag()
		{
			
			print("<" . DRINK_OPTION_GROUP_TAG .
					" id=\"" . $this->group_id . "\" " .
					DRINK_OPTION_GROUP_PART_NAME_ATTR . "=\"" . rawurlencode($this->group_part_name) . "\" " .
					DRINK_OPTION_GROUP_LONG_NAME_ATTR . "=\"" . rawurlencode($this->group_long_name) . "\" " .
					DRINK_OPTION_GROUP_MULTISELECT_ATTR . "=\"" . rawurlencode($this->group_multi_select) . "\" " .
					MENU_ID_ATTR . "=\"" . rawurlencode($this->dog_menu_id) . "\" " .
					SORT_ORDER_SHORT_ATTR . "=\"" . rawurlencode($this->sort_order) . "\" " .
					">");
					
		}
		
		function PrintClosingTag()
		{
			print("</" . DRINK_OPTION_GROUP_TAG . ">");
		}
	}
    class DrinkOption
    {
    	var $option_id;
    	var $option_group_id;
    	var $option_name;
    	var $menu_id;
    	var $sort_order;
    	
    	function PrintAsXML()
    	{
    		print("<" . DRINK_OPTION_TAG .
    				" id=\"" . $this->option_id . "\" " .
    				DRINK_OPTION_NAME_ATTR . "=\"" . rawurlencode($this->option_name) . "\" " .
    				DRINK_OPTION_GROUP_ID . "=\"" . rawurlencode($this->option_group_id) . "\" " .
    				SORT_ORDER_SHORT_ATTR . "=\"" . rawurlencode($this->sort_order) . "\" " .
    				"></" . DRINK_OPTION_TAG .">");
    	}
    	
    }
    
    class UserDrinkOption
    {
    	var $user_drink_option_id;
    	var $user_drink_id;
    	var $drink_types_option_id;
    	var $user_drink_option_count;
    	var $cost_per;
    	
    	// Joined in from other tables
    	var $calc_cost; // $cost_per * user_drink_option_count
    	var $drink_option_id;
    	var $drink_option_group_id;
    	var $option_name;
    	var $sort_order;
    	var $menu_id;
    	
    	function PrintAsXML()
    	{
    		print("<" . USER_DRINK_OPTION_TAG . 
    				" id=\"" . $this->user_drink_option_id . "\" " .
    			 	USER_DRINK_OPTION_USER_DRINK_ID_ATTR . "=\"" . rawurlencode($this->user_drink_id) . "\" " .
    				USER_DRINK_OPTION_DRINK_TYPES_OPTION_ID_ATTR . "=\"" . rawurlencode($this->drink_types_option_id) . "\" " .
    				USER_DRINK_OPTION_DRINK_OPTION_ID_ATTR . "=\"" . rawurlencode($this->drink_option_id)  . "\" " .
    				USER_DRINK_OPTION_DRINK_OPTION_GROUP_ID_ATTR  . "=\"" . rawurlencode($this->drink_option_group_id) . "\" " .
    				SORT_ORDER_SHORT_ATTR . "=\"" . rawurlencode($this->sort_order) . "\" " );
    		if($this->user_drink_option_count!=1)
    		{
    			// Default it on the client side. Since its attached to a drink, count should be at least 1
    			print(	USER_DRINK_OPTION_COUNT_ATTR . "=\"" . rawurlencode($this->user_drink_option_count) . "\" ");
    		}
    		print("></" . USER_DRINK_OPTION_TAG . ">");
    	}
    }
    
    class DrinkTypesOption
    {
    	var $drink_types_option_id;
    	var $drink_types_drink_option_id;
    	var $drink_types_option_drink_type_id;
    	var $drink_types_drink_option_group_id;
    	var $drink_types_option_range_min;
    	var $drink_types_option_range_max;
    	var $drink_types_option_cost_per;
    	var $dto_charge_each_count; // If 0, just charge once no matter what count is. If 1, charge per each
    	var $menu_id;
    	
    	function DrinkTypesOption()
    	{
    		$this->SetEmpty();
    	}
    	
    	function SetEmpty()
    	{
    		$this->drink_types_option_id=0;
    		$this->drink_types_drink_option_id=0;
    		$this->drink_types_drink_option_group_id=0;
    		$this->drink_types_option_range_min=0;
    		$this->drink_types_option_range_max=0;
    		$this->drink_types_option_cost_per=0.00;
    		$this->dto_charge_each_count=1; // 1 is default here
    		$this->menu_id=-1;
    	}
    	
    	function PrintAsXML()
    	{
    		print("<" . DRINK_TYPE_OPTION_TAG .
    				" id=\"" . $this->drink_types_option_id . "\" " .
    				DRINK_TYPES_OPTION_DRINK_OPTION_ID_ATTR . "=\"" . rawurlencode($this->drink_types_drink_option_id) . "\" " .
    				DRINK_TYPES_OPTION_DRINK_TYPE_ID_ATTR . "=\"" . rawurlencode($this->drink_types_option_drink_type_id) . "\" " .
    				DRINK_TYPES_OPTION_DRINK_OPTION_GROUP_ID_ATTR . "=\"" . rawurlencode($this->drink_types_drink_option_group_id) . "\" ");
    		
    		// Only print min/max & cost if "non default" since most are 1,1,0.00 then I think this will save a ton of menu space
    		if ($this->drink_types_option_range_min!=1)
    		{
    			print(DRINK_TYPES_OPTION_RANGE_MIN . "=\"" . rawurlencode($this->drink_types_option_range_min) . "\" ");
    		}
    		if($this->drink_types_option_range_max!=1)
    		{
    			print(DRINK_TYPES_OPTION_RANGE_MAX . "=\"" . rawurlencode($this->drink_types_option_range_max) . "\" ");
    		}
    		if(	strcmp($this->drink_types_option_cost_per,"0.00")!=0)
    		{
    			print(DRINK_TYPES_OPTION_COST . "=\"" . rawurlencode($this->drink_types_option_cost_per) . "\" ");
    		}
    				
    		// 1 is default, so only print if zero
    		if($this->dto_charge_each_count==0)
    		{
    			print(DRINK_TYPES_OPTION_CHARGE_EACH . "=\"" . rawurlencode($this->dto_charge_each_count) . "\" ");
    		}
    		print("></" . DRINK_TYPE_OPTION_TAG . ">");
    	}
    }
    
    function compare_user_item_by_type(UserDrink $a, UserDrink $b)
    {
    	if($a->item_type==$b->item_type)
    	{
    		return 0;
    	}
    	else if($a->item_type<$b->item_type)
    	{
    		return -1;
    	}
    	else
    	{
    		return 1;
    	}
    
    }
    class UserDrink
    {
        
        var $user_drink_id;
        var $drink_type_id;
        var $user_drink_user_id;
        var $drink_type_short_descr;
        var $drink_type_long_descr;
        
        var $drink_include_default;
        var $user_drink_cost;
        var $user_drink_name;
        var $item_type_class;
        
        var $user_drink_extra;
        
        var $user_drink_options; // Array of User Drink Options, sorted by DrinkOptions->sort_order
        var $menu_id;
        
        static $GET_USER_DRINK_BASE_QUERY= "SELECT user_drinks.user_drink_id,  user_drinks.user_drink_name, user_drinks.user_id, user_drinks.drink_type_id,
    			  user_drinks.drink_include_default, user_drinks.user_drink_extra, user_drinks.item_type_class, user_drinks.menu_id, drink_types.drink_type_long_descr
    			  FROM user_drinks , drink_types ";
        
        function UserDrink()
        {
            $this->SetEmpty();
        }
        
        function SetEmpty()
        {
            $this->user_drink_id=0;
            $this->drink_type_id=0;
            $this->user_drink_user_id=0;
            
            $this->drink_type_long_descr="";
           	$this->drink_include_default=0;
            
           
            $this->user_drink_cost=0.00;
            $this->user_drink_name="";
            
            $this->user_drink_extra="";

            $this->menu_id=-1;
            
            $this->user_drink_options = array();
        }
        
        function GetItemTypeAsString()
        {
        	switch($this->item_type)
        	{
        		case DB_USER_ITEM_CLASS_DRINK:
        			return "Drink";
        		case DB_USER_ITEM_CLASS_FOOD:
        			return "Food";
        		default:
        			return "Other";
        	}
        	
        }
        function AddDrinkOption($drink_option)
        {
        	//print("AddDrinkOption. " . var_dump($drink_option));
        	
        	$this->user_drink_options[]=$drink_option; // Add the option in
        	
        	//print("AddDrinkOption Cost Before: $this->user_drink_cost");
        	$this->user_drink_cost = bcadd($drink_option->calc_cost, $this->user_drink_cost,10);
        	//print("AddDrinkOption Cost After: $this->user_drink_cost");
        }
        function PrintAsXML()
        {
        	
        		//print("S: " . $drink->syrups_strings . "SS: " . $database->MakeSyrupStringFromStringArray($drink->syrups_strings));
        		print("<" . USER_DRINK_TAG . 
        				" id=\"" . $this->user_drink_id . "\" " .       				
        				USER_DRINK_OPTIONS_TEXT_ATTR . "=\"" . rawurlencode($this->GenerateDrinkOptionsText()) . "\" " .
        				USER_DRINK_TYPE_NAME_ATTR . "=\"" . rawurlencode($this->drink_type_long_descr) . "\" " .
        				USER_DRINK_NAME_ATTR . "=\"" . rawurlencode($this->user_drink_name) . "\" " .
        				USER_DRINK_EXTRA_ATTR . "=\"" . rawurlencode($this->user_drink_extra) . "\" " .
        				USER_DRINK_DRINK_TYPE_ATTR . "=\"" . rawurlencode($this->drink_type_id) . "\" " .
        				USER_DRINK_INCLUDE_DEFAULT_ATTR . "=\"" . rawurlencode($this->drink_include_default) . "\" " .
        				USER_DRINK_COST_ATTR . "=\"" . rawurlencode(number_format($this->user_drink_cost,2)) . "\" " .
        				USER_DRINK_ITEM_TYPE_ATTR . "=\"" . rawurlencode($this->item_type_class) . "\" " .
        				MENU_ID_ATTR . "=\"" . rawurlencode($this->menu_id) . "\" " .
        				
        				">");

        		// Option List
        		print("<" . USER_DRINK_OPTION_LIST_TAG . ">");
        		
        		if($this->user_drink_options!=null)
        		{
        			for($index=0;$index<count($this->user_drink_options);$index++)     				
        			{
        				$option = $this->user_drink_options[$index];
        				if($option!=NULL)
        				{
        					$option->PrintAsXML();
        				}
        			}
        		}
        		print("</" . USER_DRINK_OPTION_LIST_TAG . ">");

        		print("</" . USER_DRINK_TAG . ">");
        }
        function MakeDrinkTextNoCost()
        {
        	$result="";
        	 
        	$result .= $this->drink_type_long_descr . " ";
        	
        	if( ($this->user_drink_name!=NULL) && (strlen($this->user_drink_name)>0))
        	{
        		$result .= "(" . $this->user_drink_name . ") ";
        	}
        	
        	$result .= ": ";
        	
        	$result .= $this->GenerateDrinkOptionsText();
        	
        	if( ($this->user_drink_extra!=NULL) && (strlen($this->user_drink_extra)>0))
        	{
        		$result .= ", Notes: " . $this->user_drink_extra;
        	}
        	return $result;
        }
        function MakeDrinkText()
        {
        	
    		$result = $this->MakeDrinkTextNoCost();
    		$result .= " (Cost: $" . number_format($this->user_drink_cost,2) . ") ";
    		return $result;
        }
        
        function GenerateDrinkOptionsText()
        {
        	$options_text="";
        	// Supposed to be sorted by the DB Query in SORT_ORDER already
        	$first_time=true;
        	
        	if($this->user_drink_options!=null)
        	{
        		for($index=0;$index<count($this->user_drink_options);$index++)     				
        		{
        			$option = $this->user_drink_options[$index];
        			if($option==NULL)
        			{
        				continue;
        			}
        			if($option->user_drink_option_count==0)
        			{
        				continue; // Some kind of error to have zero !
        			}
        			if( $first_time==false)
        			{
        				$options_text .= ", ";
 	      			}
        			$options_text .= $option->option_name;
        			if($option->user_drink_option_count>1)
        			{
        				$options_text .= "(" .  $option->user_drink_option_count . ")";
        			}
        			$first_time=false;
        		}
        	}
        	return $options_text;
       }
        
    }
    
    class DrinkTypeDefaultOptions
    {
        var $drtypes_defopt_id;                 // the ID of this element
        var $drtypes_defopt_drink_type_id;      // The ID of the corresponding Drink Type
        var $drtypes_defopt_option_group;        // The Drink Option Group
        var $drtypes_defopt_default_value;      // The Value (as an ID) of the option value (Large, Caffeine etc)
        var $drtypes_defoptions_quantity;
        var $menu_id;
        
        function PrintAsXML()
        {
        	//MENU_ID_ATTR . "=\"" . rawurlencode($this->menu_id) . "\" " .
            print("<" . DRINK_TYPES_DEFAULT_OPTION_TAG . 
            		//" id=\"" . rawurlencode($this->drtypes_defopt_id)          									. "\" " .
                  " " . DRINK_TYPES_DEFAULT_OPTION_DT_ID . "=\"" . rawurlencode($this->drtypes_defopt_drink_type_id)	 . "\" " .
                  DRINK_TYPES_DEFAULT_OPTION_OPT_GROUP . "=\"" . rawurlencode($this->drtypes_defopt_option_group)          . "\" " .
            		
                  DRINK_TYPES_DEFAULT_OPTION_OPT_VALUE_ID . "=\"" . rawurlencode($this->drtypes_defopt_default_value)    . "\" ");
            // Defaulted on client to '1'
            if($this->drtypes_defoptions_quantity!=1)
            {
            	print(	DRINK_TYPES_DEFAULT_OPTION_COUNT . "=\"" . rawurlencode($this->$drtypes_defoptions_quantity)  . "\" ");
            }
            
            print("></" . DRINK_TYPES_DEFAULT_OPTION_TAG . ">");
        }
        
    }
    
    class MandatoryDrinkOption
    {
        var $mand_option_id;
        var $drink_type_id;
        var $drink_option_group;
        var $menu_id;
        
        function PrintAsXML()
        {
        	//MENU_ID_ATTR . "=\"" . rawurlencode($this->menu_id) . "\" " .
            print("<" . DRINK_TYPES_MAND_OPTION .
            		//" id=\"" . rawurlencode($this->mand_option_id) . "\" " .
                   " " . DRINK_TYPES_MAND_OPTION_DT_ID . "=\"" . rawurlencode($this->drink_type_id)  . "\" " .
                  DRINK_TYPES_MAND_OPTION_DOG_ID . "=\"" . rawurlencode($this->drink_option_group)   . "\" " .
            		
                  "></" . DRINK_TYPES_MAND_OPTION .">");
        }
        
    }
    
  
    
    class DrinkType
    {
        var $drink_type_id;
        var $drink_type_short_descr;
        var $drink_type_long_descr;
        var $drink_type_text;
        var $sort_order; // So allow ordering in App/GUI
        var $base_price;
        var $item_type_group; // THis is a logical grouping (Organics, Entree, Breakfast, Cold Drinks etc)
        var $item_type_type; // Bad name. This is type 0=Drink, 1=Food etc. e.g. inherent type
        var $menu_id;
        
        
        function PrintAsXML_noClosingTag()
        {
        	print("<" . DRINK_TYPE_TAG .
        			" id=\"" . rawurlencode($this->drink_type_id) . "\" " .
        			DRINK_TYPE_LONG_DESCR_ATTR . "=\"" . rawurlencode($this->drink_type_long_descr) . "\" ".
        			DRINK_TYPE_TEXT . "=\"" . rawurlencode($this->drink_type_text) . "\" " .
        			MENU_ID_ATTR . "=\"" . rawurlencode($this->menu_id) . "\" " .
        			DRINK_TYPE_ITEM_GROUP_ATTR . "=\"" . rawurlencode($this->item_type_group) . "\" " .
   					DRINK_TYPE_TYPE_ID_ATTR . "=\"" . rawurlencode($this->item_type_type) . "\" " .
        			SORT_ORDER_ATTR . "=\"" . rawurlencode($this->sort_order) . "\" ");
        	if(strcmp($this->base_price,"0.00")!=0)
        	{
        		print( DRINK_TYPE_BASE_COST_ATTR . "=\"" . rawurlencode($this->base_price) . "\" ");
        	}
        			
        	print(">");
        	
        }
        
        function PrintXMLClosingTag()
        {
        	print("</" . DRINK_TYPE_TAG. ">");
        }
    }
    
    
        
    
?>