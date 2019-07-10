<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$userID = mysqli_real_escape_string($conn, $_POST['userID']);
$friendID = mysqli_real_escape_string($conn, $_POST['friendID']);
$dbdata = array();


$sql = "DELETE FROM friendship
WHERE friend_1='$friendID'
AND friend_2='$userID'"
$result = mysqli_query($conn, $sql);
if (!$result) {
    die('Could not query:' . mysql_error());
	return False;
}else{
	return True;
}
// Close Connection
mysqli_close($conn);
?>