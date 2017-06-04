<?PHP
ini_set('display_errors', '1');
error_reporting(E_ALL);
$command = $_GET['cmd'];

	// Connect to the Database
	$dsn = 'mysql:host=';
    	$username = '';
    	$password = '';

    	try {
        	$db = new PDO($dsn, $username, $password);
	        	
			if ($command == "courses") {
				$select_sql = 'SELECT id, shortDesc, longDesc, prereqs FROM Courses';
				$course_query = $db->query($select_sql);
				$courses = $course_query->fetchAll(PDO::FETCH_ASSOC);
				if ($courses) {	
	   				echo json_encode($courses);
				}
			}
			else if ($command == "instructors") {
				$select_sql = 'SELECT fullName, title, email, photoUrl, office FROM Instructors';
				$query = $db->query($select_sql);
				$instructors = $query->fetchAll(PDO::FETCH_ASSOC);
				if ($instructors) {
					echo json_encode($instructors);
				}
			}
    	} catch (PDOException $e) {
        	$error_message = $e->getMessage();
        	echo 'There was an error connecting to the database.';
			echo $error_message;
        	exit();
    	}

?>
