<?php

/*
 * This file provides a web service to add a user to the database. Given an "email", "password",
 *  "question" and "answer," attempts to add user info to database. Returns success message if successful,
 *  or returns an error with error message.
 */

ini_set('display_errors', '1');
error_reporting(E_ALL);

 
//establish database connection

    // Connect to the Database
$dsn = 'mysql:host=';
        $db_username = '';
        $db_password = '';
    
//get input 
$email = isset($_GET['email']) ? $_GET['email'] : '';
$password = isset($_GET['password']) ? $_GET['password'] : '';

//validate input
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo '{"result": "fail", "error": "Please enter a valid email."}';
} else if (strlen($password) < 6) {
    echo '{"result": "fail", "error": "Please enter a valid password (longer than five characters)."}';
} else {    
    //build query
    $sql = "INSERT INTO users";
    $sql .= " VALUES ('$email', '$password')";
       
   try { 
		$conn = new PDO($dsn, $db_username, $db_password);
		$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		//attempts to add record
		if ($result = $conn->query($sql)) {
			echo '{"result": "success"}';
			$conn = null;
		} 
   } catch(PDOException $e) {
		if ((int)($e->getCode()) == 23000) {
			echo '{"result": "fail", "error": "That email address has already been registered."}';
		} else {
			//echo 'Error Number: ' . $e->getCode() . '<br>';
			echo '{"result": "fail", "error": "Unknown error (' . (((int)($e->getCode()) + 123) * 2) .')"}';
			//on error, return error message in json
		}
    }
}
?>