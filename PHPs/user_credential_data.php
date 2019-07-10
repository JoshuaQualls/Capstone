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
$fname = mysqli_real_escape_string($conn, $_POST['fname']);
$lname = mysqli_real_escape_string($conn, $_POST['lname']);
$phone = mysqli_real_escape_string($conn, $_POST['phone']);
$fID = mysqli_real_escape_string($conn, $_POST['instanceID']);

if($pass == "" OR $email == ""){
echo "Empty Entry";
}
else{
$sql = "INSERT INTO users(user_email, user_pass, phone, firebaseID) VALUES('$email', '$pass', '$phone', '$fID')";
$check = "SELECT user_email FROM users WHERE user_email = '$email'";
$check_results = mysqli_query($conn, $check);
$num_rows = mysqli_num_rows($check_results);

if ($num_rows==1){
 echo "false";
}
else{
$result = mysqli_query($conn, $sql);
unset($sql);
unset($result);

$sql = "SELECT uID FROM users WHERE user_email = '$email' AND user_pass = '$pass'";
$result = mysqli_query($conn, $sql);
$row = $result->fetch_assoc();
$userID =  $row['uID'];

unset($sql);
unset($result);

$sql = "INSERT INTO patron(patronID, fname, lname) VALUES('$userID', '$fname', '$lname')";
$result = mysqli_query($conn, $sql);
if (!$result) {
    die('Could not query:' . mysql_error());
}
echo $userID;
}
}



// Close Connection
mysqli_close($conn);
?>