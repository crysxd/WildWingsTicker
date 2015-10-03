<?php
    /* Global fields */
    define("DB_HOST", "localhost");
    define("DB_USER", "wwticker");
    define("DB_PASS", "ahni3jeiTh7noop");
    define("DB_NAME", "wwticker");

    /* Error codes */
    define("ERR_DB_UNAVAILABLE", 100);


    /****************************************************************************************************************************
     * Opens a connection to the database
     */
    function db_open() {
        global $db_link;

        /* Cancel if already connected */
        if($db_link != null) {
          return;
        }

        /* Connect */
        $db_link = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
        
        /* Handle error */
        if (!$db_link || $db_link->connect_error) {
          die_with_error($db_link->connect_error, ERR_DB_UNAVAILABLE);
            
        }
    }

    /****************************************************************************************************************************
     * Closes the connection to the database
     */
    function db_close() {
        global $db_link;

        /* Close if already connected */
        if($db_link != null) {
          $db_link->close();
          $db_link = null;

        } 
    }  

    /****************************************************************************************************************************
     * Checks the result of a database operation for errors
     */
    function db_check_result($result) {
        global $db_link;

        /* If the result is falsy, there was an error */
        if(!$result) {
            /* Send a complete report if we are in debug mode */
            if(DEBUG) {
                die_with_error($db_link->error, $db_link->errno);
                
            } 
            
            /* Send a general error if we are in production */
            else {
                die_with_error();
                
            }
        }
    }

?>