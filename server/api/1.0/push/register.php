<?php

    /* Include utils script. This will validate the access */
    include_once("../_utils.php");
    include_once("../_db.php");

    /* Connect to db */
    db_open();

    /* Assure the current method is supported */
    check_http_method(array('POST'));

    /* Assure all needed parameters are available */
    check_parms_available(array('id'), $_POST);

    /* Insert into db */
    $stmt = $db_link->prepare('INSERT INTO push_targets(id) VALUES(?) ON DUPLICATE KEY UPDATE registered_on=NOW()');
    db_check_result($stmt);
    $result = $stmt->bind_param('s', $_POST['id']);
    db_check_result($result);
    $result = $stmt->execute();
    db_check_result($result);
    $result = $stmt->get_result();

    /* Write answer */
    $answer['success'] = true;
    echo json_encode($answer);

    /* Close db */
    db_close();

?>