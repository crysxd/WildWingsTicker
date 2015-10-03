<?php
    /****************************************************************************************************************************
     * This code is executed implicitly when the utils file is imported
     */

    /* Global Definitions */
    define('DEBUG', 'true');
    define('API_KEY_HEADER', 'Api-Key');
    define('SECRET', 'xUSMhPJR7tfd2FnEh9Zz8LvKLaRQqYWMJFqRkYzhhEn9azDhzvHx737H4PqhDbUdHaVTYk27Pe6PDAX4UwrSppsg3FrxHaAfamYZwRWaVTQrjDj5ZAMeN4nGzC57DwdE');

    /* Error codes */
    define("ERR_GENERAL", 0);
    define("ERR_ACCESS_DENIED", 1);
    define("ERR_MISSING_PARAM", 2);

    /* Set return contet type to json */
    header('content-type: application/json');


    /* Disable error reporting */
    if(DEBUG) {
        error_reporting(E_ERROR | E_WARNING | E_PARSE | E_NOTICE);
    } else {
        error_reporting(0);   
    }

    /* Validate access */
    $headers = apache_request_headers();
    if(!array_key_exists(API_KEY_HEADER, $headers) || $headers[API_KEY_HEADER] != SECRET) {
        die_with_error_unauthorized();
    }

    /****************************************************************************************************************************
     * Creates an Error message and kills the process
     */
    function die_with_error($err_msg="", $err_no=ERR_GENERAL) {
        $answer['success'] = false;
        $answer['err_no'] = $err_no;
        $answer['err_msg'] = $err_msg;
        
        // THIS IS ONLY FOR DEBUGGING PURPOSE!
        // WE SHOULD NOT GIVE AN SQL-ERROR DESCRIPTION TO A POTENTIAL ATTACKER!
        // WE INSTEAD SEND BACK AN EMPTY ARRAY TO HIDE THE ERROR!
        if(DEBUG) {
          $answer['err_loc'] = debug_backtrace();

        }

        die(json_encode($answer));

    }

    /****************************************************************************************************************************
     * Creates an Error message for unaythorized access and kills the process
     */
    function die_with_error_unauthorized() {
        header("HTTP/1.1 401 Unauthorized");
        die_with_error('Access denied', ERR_ACCESS_DENIED);

    }

    /****************************************************************************************************************************
     * Writes the answer as JSON and kills the process
     */
    function die_with_success($answer=array()) {
        $answer['success'] = true;
        die(json_encode($answer));
    
    }

    /****************************************************************************************************************************
     * Assures all neeeded parameteres are availabel in array
     */
    function check_parms_available($params, &$array) { 	
        global $db_link;
        
        /* Iterate over needed keys */
        foreach($params as $i => $p) {
            /* Check if key is available */
            if(!array_key_exists ($p, $array) || strlen(serialize($array[$p])) == 0) {
                /* Die with an error if a param is missing */
                die_with_error("Required parameter \"$p\" is missing.", ERR_MISSING_PARAM);
            }

            /* Escape the param */
            $array[$p] = htmlentities($db_link->real_escape_string($array[$p])); 
        
        }   
    }

    /****************************************************************************************************************************
     * Assures the current method is supported
     */
    function check_http_method($methods) { 	        
        if(!in_array($_SERVER['REQUEST_METHOD'], $methods)) {
            header("HTTP/1.1 405 Method not allowed");
            die_with_error('Method not allowed');   
            
        }
    }

?>