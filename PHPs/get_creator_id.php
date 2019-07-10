<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$groupID = mysqli_real_escape_string($conn, $_POST['groupID']);
$sql = "SELECT creatorID FROM groups WHERE groupID = '$groupID'";

$result = mysqli_query($conn, $sql);
if(!$result){
	die('Could not query:' . mysqli_error());
	}
	$row = $result->fetch_assoc();
	
$creatorID = $row['creatorID'];

echo $creatorID;
mysqli_close($conn);
?>