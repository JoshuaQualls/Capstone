<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$userID = mysqli_real_escape_string($conn, $_POST['userID']);
$dbdata = array();


$sql = "SELECT groupID FROM active_groups WHERE userID = '$userID' AND status = 'grouped'";
$result = mysqli_query($conn, $sql);
if(!$result){
	die('Could not query:' . mysqli_error());
	}
	$row = $result->fetch_assoc();
	
$groupID = $row['groupID'];

echo $groupID;

// Close Connection
mysqli_close($conn);
?>