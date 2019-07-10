<?php

$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

$userID = mysqli_real_escape_string($conn, $_POST['userID']);
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $image = $_POST['image'];
}



$path = "photos/" . $userID . ".png";

$actualpath = "http://cgi.sice.indiana.edu/~team48/" . $path;

file_put_contents($path, base64_decode($image));
echo "Successfully Uploaded";
?>