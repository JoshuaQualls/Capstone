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

$sql = "INSERT INTO users(user_email, user_pass) VALUES('$email', '$pass')";
$result = mysqli_query($conn, $sql);



// Close Connection
mysqli_close($conn);
?>