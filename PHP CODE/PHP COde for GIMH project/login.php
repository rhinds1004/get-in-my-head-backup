<?php
/* 
 * This file provides a web service to authenticate a user. 
 */
 
 //establish database connection

    // Connect to the Database
$dsn = 'mysql:host=';
        $db_username = '';
        $db_password = '';
    

//get input - customize
$email = isset($_GET['email']) ? $_GET['email'] : '';
$password = isset($_GET['password']) ? $_GET['password'] : '';
    
//validate input - customize
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo '{"result": "fail", "error": "Please enter a valid email."}';
} else if (strlen($password) < 6) {
    echo '{"result": "fail", "error": "Please enter a valid password (longer than five characters)."}';
} else { 
	$conn = new PDO($dsn, $db_username, $db_password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);   
    //build query
    $sql = "SELECT email, password FROM users ";
    $sql .= " WHERE email = '" . $email . "'";

        
    $q = $conn->prepare($sql);
    $q->execute();
    $result = $q->fetch(PDO::FETCH_ASSOC);
    
       
    //check results
    if ($result != false) {
        //on success, return the user id
        if ($password == $result['password'])
        	echo '{"result": "success"}';
	else 
		echo '{"result": "fail", "error": "Incorrect password."}';
    } else {
        echo '{"result": "fail", "error": "Incorrect email."}';
    }
}

//close database connection
$conn = null;
?>