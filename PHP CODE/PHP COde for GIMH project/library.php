<?php

$flag = -1;

/** MYSQL DATABASE INFO **/
$mysql_username = "";
$mysql_password = "";
$mysql_host     = "localhost";
$connection     = mysql_connect($mysql_host, $mysql_username, $mysql_password);
mysql_select_db($mysql_username);

/** USER INPUT **/
if( !empty($_GET['email']) && !empty($_GET['title']) && !empty($_GET['body']) && !empty($_GET['position'])) {
    $flag = 1;
    $email          = $_GET['email'];
    $title          = $_GET['title'];
    $body           = $_GET['body'];
    $position       = $_GET['position'];

    $query = "INSERT INTO library (email,title,body,position) VALUES('$email','$title','$body',$position)";
} else if(!empty($_GET['useritems'])) {
    $flag = 2;
    $username = $_GET['useritems'];
    $query = "SELECT * FROM library WHERE email='$username'";
}







switch($flag) {

    case 1:
        $result = mysql_query($query, $connection);
        break;
    case 2:
        $result = mysql_query($query, $connection);
	$json_array = array();

	while($row = mysql_fetch_assoc($result)){
            $json_array[] = $row;
	}

	print json_encode($json_array);
        break;


}










?>
