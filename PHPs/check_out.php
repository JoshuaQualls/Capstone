<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$userID = mysqli_real_escape_string($conn, $_POST['userID']);



$sql = "DELETE FROM current_patrons WHERE patronID='$userID'";
$result = mysqli_query($conn, $sql);
if (!$result) {
    die('Could not query:' . mysql_error());
}

unset($sql);$sql = "DELETE FROM active_groups WHERE patronID = '$userID'";
$result = mysqli_query($conn, $sql);
if (!$result) {
    die('Could not query:' . mysql_error());
}
// Close Connection
mysqli_close($conn);
?>