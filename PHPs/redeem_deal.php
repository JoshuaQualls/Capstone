<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$groupID = mysqli_real_escape_string($conn, $_POST['groupID']);
$dealID = mysqli_real_escape_string($conn, $_POST['dealID']);



$sql = "INSERT INTO redeemed_deals(dealID, groupID, time_redeemed)
VALUES('$dealID','$groupID', CURRENT_TIMESTAMP)";
$result = mysqli_query($conn, $sql);
if (!$result) {
    die('Could not query:' . mysql_error());
	echo "False";
}else{
	echo "True";
}
// Close Connection
mysqli_close($conn);
?>