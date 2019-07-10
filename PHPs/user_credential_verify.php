<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

// Grab Users
$email = mysqli_real_escape_string($conn, $_POST['email']);
$pass = mysqli_real_escape_string($conn, $_POST['pass']);


$sql = "SELECT uID, type FROM users WHERE user_email = '$email' AND user_pass = '$pass'";
$dbdata = array();
$result = mysqli_query($conn, $sql);
if (!$result) {
    die('Could not query 1:' . mysql_error());
}
if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
           $dbdata[]=$row; }
} else {
    die('Could not query 2:' . mysql_error());
}

echo json_encode($dbdata);


// Close Connection
mysqli_close($conn);
?>