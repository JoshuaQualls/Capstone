<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$userID = mysqli_real_escape_string($conn, $_POST['userID']);
$friendID = mysqli_real_escape_string($conn, $_POST['friendID']);


$sql = "DELETE FROM  friendship WHERE friend_1 = '$friendID' AND friend_2 ='$userID' AND status ='friends'";
if ($conn->query($sql) === TRUE) {
	echo "Removed friend one way!";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}
$sql2 = "DELETE FROM friendship WHERE friend_2 = '$friendID' AND friend_1 ='$userID' AND status ='friends'";
if ($conn->query($sql2) === TRUE) {
	echo "Removed friend the other!"; 
} else {
    echo "Error: " . $sql2 . "<br>" . $conn->error;
}
mysqli_close($conn);
?>