<?php
// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$barID = mysqli_real_escape_string($conn, $_POST['barID']);
$sql = "SELECT cover
FROM bar
WHERE barID = '$barID'";
$result = mysqli_query($conn, $sql);
if (!$result){
	die('Could not query:' . mysql_error());
}

$row = $result->fetch_assoc();
$cover = $row['cover'];

echo $cover;

// Close Connection
mysqli_close($conn);
?>