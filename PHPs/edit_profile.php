<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$uID = mysqli_real_escape_string($conn, $_POST['userID']);
$user_email = mysqli_real_escape_string($conn, $_POST['user_email']);
$user_pass = mysqli_real_escape_string($conn, $_POST['user_pass']);
$phone = mysqli_real_escape_string($conn, $_POST['phone']);
$fname = mysqli_real_escape_string($conn, $_POST['fname']);
$lname = mysqli_real_escape_string($conn, $_POST['lname']);
$image = mysqli_real_escape_string($conn, $_POST['image']);


$sql = "UPDATE users SET uID = '$uID' AND user_email = '$user_email' AND user_pass = 'user_pass' AND phone = '$phone'
WHERE uID = '$uID'";
$result = mysqli_query($conn, $sql);
if (!$result) {
    die('Could not query:');
	return False;
}else{
	echo True;
}
$sql2 = "UPDATE patron SET fname = '$fname' and lname = '$lname'
WHERE patronID = '$uID'";
$result2 = mysqli_query($conn, $sql2);
if (!$result2) {
    die('Could not query:');
	return False;
}else{
	echo True;
}
// Close Connection
mysqli_close($conn);

 $path = "photos/'$userID'.png";
 
    $actualpath = "http://cgi.sice.indiana.edu/~team48/'$path'";
	$sql3 = "UPDATE patron SET image = '$actualpath' WHERE patronID = $uID'";
    mysqli_query($conn,$sql3);

    file_put_contents($path,base64_decode($image));
    echo "Successfully Uploaded";
?>