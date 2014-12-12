<?php
    /**
     * fc_car_make_model_color.php
     * 
         * (C) Copyright Freeway Coffee, 2011
     */
    require_once("fc_constants.php");
	require_once("AuthorizeNet.php");
    
    class CarMakeModelInfo
    {
        var $car_make_id;
        var $car_make_long_descr;
        var $car_make_short_descr;
        var $car_make_has_models;
        
        var $car_model_id;
        var $car_model_long_descr;
        var $car_model_short_descr;
        
        var $car_color_id;
        var $car_color_long_descr;
        var $car_color_short_descr;
        
        function UserLogic()
        {
            $this->car_make_id=0;
            $this->car_make_long_descr="";
            $this->car_make_short_descr="";
            $this->car_make_has_models=0;
            
            $this->car_model_id=0;
            $this->car_model_long_descr="";
            $this->car_model_short_descr="";
            
            $this->car_color_id=0;
            $this->car_color_long_descr="";
            $this->car_color_short_descr="";
            
        }
        
        function StripBrackets($str)
        {
            
            $result="";
            for($index=0;$index<strlen($str);$index++)
            {
                if( ($str[$index]!='(') && $str[$index]!=')')
                {
                    $result .= $str[$index];
                }
            }
            return $result;
        }
        
        function StripDisplayString($str)
        {
            $pos = strpos($str, "Other");
            if($pos===0)
            {
                // String starts with "Other "
                $make_descr = substr($str,strlen("Other"));
                $make_descr = $this->StripBrackets($make_descr);
            }
            else
            {
                $make_descr = $str;
            }
            return $make_descr;
        }

        function GenerateMakeModelDisplayString()
        {
            if($this->car_make_id<=1)
            {
                return "";
            }
            
            $descr = $this->StripDisplayString($this->car_make_long_descr);
            
            if($this->car_make_has_models==true)
            {
                $model_descr = $this->StripDisplayString($this->car_model_long_descr);
                if(strlen($model_descr)>0)
                {
                    $descr .= " (" . $model_descr . ")";
                }
            }
            return $descr;
        }
        
        // Returns a String. Generates a string suitable for display on a web page or Mobile App
        function GenerateLongDisplayString()
        {
            // Color first, if any
            if($this->car_color_id>1)
            {
                $display_string = $this->StripDisplayString($this->car_color_long_descr);
                                
                if($this->car_make_id<1)
                {
                    // Thought we had a color but it turned out to be "Other" and we had a make
                    if(strlen($display_string)==0)
                    {
                        $display_string="Unknown Vehicle";
                        return $display_string;
                    }
                    
                    $display_string .= " Vehicle";
                    
                    return $display_string;
                }
                else
                {
                    if(strlen($display_string)>0)
                    {
                        $display_string .=" "; // Only put a space if there was something else there like a color 
                    }
                    return $display_string . $this->GenerateMakeModelDisplayString();
                }
                
            }
            else
            {
                return $this->GenerateMakeModelDisplayString();
            }
            
            //$display_string = $this->car_make_long_descr . "/" . $this->car_model_long_descr . "/" . $this->car_color_long_descr;
            return "";
        }
        
        // Genrates (prints) the XML needed for this to be included in the UserInfo XML stream
        function GenerateUserInfoXML()
        {
            print("<user_car_data " .
                  "car_make_id=\"" . rawurlencode($this->car_make_id) . "\" " .
                  "car_make_long_descr=\"" . rawurlencode($this->car_make_long_descr) . "\" " .
                  "car_make_short_descr=\"" . rawurlencode($this->car_make_short_descr) . "\" " .
                  "car_make_has_models=\"" . rawurlencode($this->car_make_has_models) . "\" " .
                  "car_model_id=\"" . rawurlencode($this->car_model_id) . "\" " .
                  "car_model_long_descr=\"" . rawurlencode($this->car_model_long_descr)  . "\" " .
                  "car_model_short_descr=\"" . rawurlencode($this->car_model_short_descr)  . "\" " .
                  "car_color_id=\"" . rawurlencode($this->car_color_id)  . "\" " .
                  "car_color_long_descr=\"" . rawurlencode($this->car_color_long_descr)  . "\" " .
                  "car_color_short_descr=\"" . rawurlencode($this->car_color_short_descr)  . "\" " .
                  "user_car_info_long_string=\"" . rawurlencode($this->GenerateLongDisplayString()) . "\" " .
                   ">" . "</user_car_data>");
        }
        
        // Get the info from the DB by using the supplied IDs
        // Assumes $car_make_id, $car_model_id and $car_color_id are populated
        function DB_GetUserCarInfo()
        {
            global $database;
                  
            $stmt = mysqli_stmt_init($database->getConnection());            
            // Start with the Make info.
            mysqli_stmt_prepare($stmt,"SELECT car_make_long_descr, car_make_short_descr, car_make_has_models FROM car_make WHERE car_make_id=?");
            //print mysqli_stmt_error($stmt);
            
            //print ("MakeID: {$this->car_make_id}");
            
            mysqli_stmt_bind_param($stmt,"i", $this->car_make_id);
                  
            if(mysqli_stmt_execute($stmt))
            {
                mysqli_stmt_store_result($stmt);
            }
            else
            {
                  $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
                                         "CarMakeModelInfo:DB_GetUserCarInfo could not store SQL result for car make ( " . mysqli_stmt_error($stmt) . ")" );
                  mysqli_stmt_close($stmt);
                  return false;
            }
                  
            //print mysqli_stmt_error($stmt);
            if(!mysqli_stmt_bind_result($stmt,$this->car_make_long_descr, $this->car_make_short_descr, $this->car_make_has_models))
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
                                   "CarMakeModelInfo:DB_GetUserCarInfo Could not bind result for car make ( " . mysqli_stmt__error($stmt) . ")" );
                  mysqli_stmt_close($stmt);
                return false;
            }

            //print("Car: {$this->car_make_long_descr}, {$this->car_make_short_descr}");
            mysqli_stmt_fetch($stmt);
            mysqli_stmt_close($stmt);
                  
            if($this->car_make_has_models!=0)
            {
                if($this->DB_GetModelInfo()!=true)
                {
                  $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"CarMakeModelInfo:DB_GetUserCarInfo: DB_GetModelInfo Failed. Error");
                  return false;
                }
            }
            
            if($this->DB_GetColorInfo()!=true)
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,"CarMakeModelInfo:DB_GetUserCarInfo: DB_GetColorInfo Failed. Error");
                return false;
            }
            
            return true;
        }
        
        // Assumes $car_model_id is set
        function DB_GetModelInfo()
        {
            global $database;
                  
            $stmt = mysqli_stmt_init($database->getConnection());            
            
            mysqli_stmt_prepare($stmt,"SELECT car_model_long_descr, car_model_short_descr FROM car_model WHERE car_model_id=? AND car_make_id=?");
            mysqli_stmt_bind_param($stmt,"ii", $this->car_model_id, $this->car_make_id);
                  
            if(mysqli_stmt_execute($stmt))
            {
                mysqli_stmt_store_result($stmt);
            }
            else
            {
                  $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
                                         "CarMakeModelInfo:DB_GetModelInfo could not store SQL result for car model ( " . mysqli_stmt__error($stmt) . ")" );
                  mysqli_stmt_close($stmt);
                  return false;
            }
                  
            if(!mysqli_stmt_bind_result($stmt,$this->car_model_long_descr, $this->car_model_short_descr))
            {
                  $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
                                     "CarMakeModelInfo:DB_GetModelInfo Could not bind result for car model ( " . mysqli_stmt__error($stmt) . ")" );
                  mysqli_stmt_close($stmt);
                  return false;
            }
            mysqli_stmt_fetch($stmt);
            mysqli_stmt_close($stmt);
            return true;
        }
        
        // Get the color info. Assumes $car_color_id is set
        function DB_GetColorInfo()
        {
            global $database;
                  
            $stmt = mysqli_stmt_init($database->getConnection());            
                  
            mysqli_stmt_prepare($stmt,"SELECT car_color_long_descr, car_color_short_descr FROM car_colors WHERE car_color_id=? ");
            mysqli_stmt_bind_param($stmt,"i", $this->car_color_id);
                  
            if(mysqli_stmt_execute($stmt))
            {
                mysqli_stmt_store_result($stmt);
            }
            else
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
                                        "CarMakeModelInfo:DB_GetColorInfo could not store SQL result for car color ( " . mysqli_stmt__error($stmt) . ")" );
                mysqli_stmt_close($stmt);
                return false;
            }
                  
            if(!mysqli_stmt_bind_result($stmt,$this->car_color_long_descr, $this->car_color_short_descr))
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
                                    "CarMakeModelInfo:DB_GetColorInfo Could not bind result for car color ( " . mysqli_stmt__error($stmt) . ")" );
                mysqli_stmt_close($stmt);
                return false;
            }
            mysqli_stmt_fetch($stmt);
            mysqli_stmt_close($stmt);
            return true;

        }
        
        function DB_PrintAllCarMakesAsXML()
        {
            global $database;
            $stmt = mysqli_stmt_init($database->getConnection());            
            // Start with the Make info.
            mysqli_stmt_prepare($stmt,"SELECT car_make_id, car_make_long_descr, car_make_short_descr, car_make_has_models, car_make_sort_category FROM car_make ORDER BY car_make_sort_category DESC,car_make_long_descr ASC");
            //print mysqli_stmt_error($stmt);
            
            //print ("MakeID: {$this->car_make_id}");
            
            
            if(mysqli_stmt_execute($stmt))
            {
                mysqli_stmt_store_result($stmt);
            }
            else
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
                                       "CarMakeModelInfo:DB_PrintAllCarMakesAsXML could not store SQL result for car make ( " . mysqli_stmt_error($stmt) . ")" );
                mysqli_stmt_close($stmt);
                return false;
            }
            
            //print mysqli_stmt_error($stmt);
            if(!mysqli_stmt_bind_result($stmt,$car_make_id, $car_make_long_descr, $car_make_short_descr, $car_make_has_models,$sort_cat))
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
                                   "CarMakeModelInfo:DB_PrintAllCarMakesAsXML Could not bind result for car make ( " . mysqli_stmt__error($stmt) . ")" );
                mysqli_stmt_close($stmt);
                return false;
            }
            
            print("<"  . ALL_CAR_MAKES_TAG ."> ");
            //CAR_MAKE_SHORT_DESCR_ATTR ."=\"" . rawurlencode($car_make_short_descr) . "\" " .
            while(mysqli_stmt_fetch($stmt)==true)
            {

                  print ("<" . CAR_MAKE_TAG . " id=\"" . rawurlencode($car_make_id) . "\" " .
                  CAR_MAKE_LONG_DESCR_ATTR ."=\"" . rawurlencode($car_make_long_descr) . "\" " .        
                  CAR_MAKE_HAS_MODELS_ATTR ."=\"" . rawurlencode($car_make_has_models) . "\" " .
                  SORT_ORDER_SHORT_ATTR ."=\"" . rawurlencode($sort_cat) . "\" " .
                         "></" . CAR_MAKE_TAG .">");
            }

            print("</" . ALL_CAR_MAKES_TAG .">");
            mysqli_stmt_close($stmt);
            return true;
        }
        
        function DB_PrintAllCarModelsAsXML()
        {
            global $database;
            
            $stmt = mysqli_stmt_init($database->getConnection());            
            
            mysqli_stmt_prepare($stmt,"SELECT car_model_id, car_make_id, car_model_long_descr, car_model_short_descr,car_model_sort_category FROM car_model ORDER by car_model_sort_category DESC, car_model_long_descr ASC");
           
            
            if(mysqli_stmt_execute($stmt))
            {
                mysqli_stmt_store_result($stmt);
            }
            else
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
                                       "CarMakeModelInfo:DB_PrintAllCarModelsAsXML could not store SQL result for car model ( " . mysqli_stmt__error($stmt) . ")" );
                mysqli_stmt_close($stmt);
                return false;
            }
            
            if(!mysqli_stmt_bind_result($stmt,$car_model_id, $car_make_id,$car_model_long_descr, $car_model_short_descr,$sort_field))
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
                                   "CarMakeModelInfo:DB_PrintAllCarModelsAsXML Could not bind result for car model ( " . mysqli_stmt__error($stmt) . ")" );
                mysqli_stmt_close($stmt);
                return false;
            }
            
            // CAR_MODEL_SHORT_DESCR_ATTR ."=\"" . rawurlencode($car_model_short_descr)   . "\" " .
            
            print("<" . ALL_CAR_MODELS_TAG . "> ");
            while (mysqli_stmt_fetch($stmt)==true)
            {
                print("<" . CAR_MODEL_TAG . " id=\"" . rawurlencode($car_model_id) . "\" " .
                      CAR_MODEL_MAKE_ID_ATTR ."=\"" . rawurlencode($car_make_id) . "\" " .
                      CAR_MODEL_LONG_DESCR_ATTR ."=\"" . rawurlencode($car_model_long_descr)  . "\" " .
                      SORT_ORDER_SHORT_ATTR ."=\"" . rawurlencode($sort_field) . "\" " .
                      "></" . CAR_MODEL_TAG . ">");
            }
            print("</" . ALL_CAR_MODELS_TAG .">");
            
            
            mysqli_stmt_close($stmt);
            return true;

        }
        
        function DB_PrintAllCarColorsAsXML()
        {
            global $database;
            
            $stmt = mysqli_stmt_init($database->getConnection());            
            
            mysqli_stmt_prepare($stmt,"SELECT car_color_id, car_color_long_descr, car_color_short_descr,sort_order FROM car_colors ORDER BY car_color_long_descr ASC");
            
            
            if(mysqli_stmt_execute($stmt))
            {
                mysqli_stmt_store_result($stmt);
            }
            else
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
                                       "CarMakeModelInfo:DB_PrintAllCarColorsAsXML could not store SQL result for car color ( " . mysqli_stmt__error($stmt) . ")" );
                mysqli_stmt_close($stmt);
                return false;
            }
            
            if(!mysqli_stmt_bind_result($stmt,$car_color_id, $car_color_long_descr, $car_color_short_descr,$sort_order))
            {
                $database->AddLogEntry($_SESSION['user_id'],$_SESSION['user_email'],session_id(),LOG_CODE_ERROR,
                                   "CarMakeModelInfo:DB_PrintAllCarColorsAsXML Could not bind result for car color ( " . mysqli_stmt__error($stmt) . ")" );
                mysqli_stmt_close($stmt);
                return false;
            }
            print("<" . ALL_CAR_COLORS_TAG ."> ");
            
            //CAR_COLOR_SHORT_DESCR ."=\"" . rawurlencode($car_color_short_descr) . "\" " .
            while(mysqli_stmt_fetch($stmt)==true)
            {
                print("<" . CAR_COLOR_TAG . " id=\"" . rawurlencode($car_color_id) . "\" " .
                      CAR_COLOR_LONG_DESCR_ATTR ."=\"" . rawurlencode($car_color_long_descr)  . "\" " .    
                      SORT_ORDER_SHORT_ATTR ."=\"" . rawurlencode($sort_order) . "\" " .
                      "></" . CAR_COLOR_TAG . ">");
            }
            
            print("</" . ALL_CAR_COLORS_TAG .">");
            mysqli_stmt_close($stmt);
            return true;
            

        }

        function DB_PrintAllCarmakeModelColorAsXML()
        {
            print("<all_car_makes_and_models>");
            if($this->DB_PrintAllCarMakesAsXML()!=true)
            {
                return false;
            }
            if($this->DB_PrintAllCarModelsAsXML()!=true)
            {
                return false;
            }
            if($this->DB_PrintAllCarColorsAsXML()!=true)
            {
                return false;
            }
            print("</all_car_makes_and_models>");
            return true;
        }
    }
?>