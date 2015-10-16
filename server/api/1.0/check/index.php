<?php
	DEFINE('DATA_URL', 'http://www.wildwingsticker.de:8080/wwticker/xloadwwtickerdaten.php');
	DEFINE('BUFFER_FILE_PATH', '_buffer.json');
	DEFINE('BUFFER_KEY_LENGTH', 'text_length');
	DEFINE('BUFFER_KEY_LAST_ID', 'last_id');
	DEFINE('MAX_GCM_MESSAGE_LENGTH', 4000);

	include_once('../_db.php');

	// Load files, parse buffer data into array
	$remoteText = get_url(DATA_URL);
	$remoteTextLength = strlen($remoteText);
	$bufferExists = file_exists(BUFFER_FILE_PATH);
	$bufferData = json_decode($bufferExists ? file_get_contents(BUFFER_FILE_PATH) : "{}", true);

	// If files have the same size, we asume nothing changed yet
	if(array_key_exists(BUFFER_KEY_LENGTH, $bufferData) && $bufferData[BUFFER_KEY_LENGTH] == $remoteTextLength) {
		die('No changes');
	}

	// Initialize last id
	if(!array_key_exists(BUFFER_KEY_LAST_ID, $bufferData)) {
		$bufferData[BUFFER_KEY_LAST_ID] = 0;
	}

	// If we get here, something has changed
	echo 'Changes detected'.PHP_EOL;

	// Parse the JSON texts
	$remoteData = json_decode($remoteText, true);

	// Get last id
	$lastId = end($remoteData['texte'])['id'];

	// If the last id of the currently loaded file is smaller than the saved on, reset it to 0. This means there is a new game
	if($lastId < $bufferData[BUFFER_KEY_LAST_ID]) {
		$bufferData[BUFFER_KEY_LAST_ID] = 0;
	}

	echo 'Last id in remote data source: '.$lastId.PHP_EOL;
	echo 'Last id pushed: '.$bufferData[BUFFER_KEY_LAST_ID].PHP_EOL;


	// Select all new data pieces
	$newData = array();

	// Iterate over all new data sets
	for($i=$bufferData[BUFFER_KEY_LAST_ID]; $i<sizeof($remoteData['texte']); $i++) {
		$newData[] = $remoteData['texte'][$i];
	}

	// Fetch IDs which should be notified
	db_open();
	$result = $db_link->query('SELECT gcm_id FROM push_targets');
	db_check_result($result);
	$ids = array();

	while($row = $result->fetch_assoc()) {
		$ids[] = $row['id'];
	}

	// Close db
	db_close();

	$size = sizeof($ids);
	echo "Pushing to $size clients".PHP_EOL;

	// Build the notification dat (the new text + the other data)
	$notificationData = array();
	$notificationData['texte'] = $newData;
	$notificationData['spielstand'] = $remoteData['spielstand'];
	$notificationData['spielstatus'] = $remoteData['spielstatus'];

	// Create JSON data text ans split
	$data = json_encode($notificationData);
	$data = str_split($data, MAX_GCM_MESSAGE_LENGTH);
	$parts_total = sizeof($data);
	echo "Splitting message into $parts_total parts".PHP_EOL;

	// Open connection
	$ch = curl_init();

	// Send parts
	$message_id = time();
	for($i=0; $i<$parts_total; $i++) {
		send_notification($ch, $ids, $data[$i], $i, $parts_total, $message_id);
	}

	// Close connection
	curl_close($ch);

	// Write buffer
	file_put_contents(BUFFER_FILE_PATH, json_encode(array(BUFFER_KEY_LENGTH => strlen($remoteText), BUFFER_KEY_LAST_ID => $lastId)));

	function send_notification($ch, $ids, $data, $part_index, $parts_total, $message_id) {
		echo "Sending $part_index of $parts_total".PHP_EOL;
		// Push data using Google Cloud Messaging API
		$googleApiParams = array();
		$googleApiParams['registration_ids'] = $ids;
		$googleApiParams['data'] = array('data' => $data, 'parts_total' => $parts_total, 'part_index' => $part_index, 'message_id' => $message_id);

		print_r($googleApiParams['data']);

		$headers = array( 
	        'Authorization: key=AIzaSyAIeuzn_BTMRgkCqFJmw1kD6_jI95mq3H8',
	        'Content-Type: application/json'
	    );

		// Set the url, number of POST vars, POST data
		curl_setopt($ch, CURLOPT_URL, 'https://android.googleapis.com/gcm/send');
		curl_setopt($ch, CURLOPT_POST, true);
		curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
		curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($googleApiParams));

		// Execute post
		echo "GCM response:".PHP_EOL;
		$result = curl_exec($ch);
		echo $result.PHP_EOL;

	}

	/**
	 * Loads the content of a url. Content with gzip encoding will be encoded.
	 */
	function get_url($url) {
	    //user agent is very necessary, otherwise some websites like google.com wont give zipped content
	    $opts = array(
	        'http'=>array(
	            'method'=>"GET",
	            'header'=>"Accept-Language: en-US,en;q=0.8rn" .
	                        "Accept-Encoding: gzip,deflate,sdchrn" .
	                        "Accept-Charset:UTF-8,*;q=0.5rn" .
	                        "User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:19.0) Gecko/20100101 Firefox/19.0 FirePHP/0.4rn"
	        )
	    );
	 
	    $context = stream_context_create($opts);
	    $content = file_get_contents($url ,false,$context); 
	     
	    //If http response header mentions that content is gzipped, then uncompress it
	    foreach($http_response_header as $c => $h)
	    {
	        if(stristr($h, 'content-encoding') and stristr($h, 'gzip'))
	        {
	            //Now lets uncompress the compressed data
	            $content = gzinflate( substr($content,10,-8) );
	        }
	    }
	     
	    return $content;
	}
 
?>