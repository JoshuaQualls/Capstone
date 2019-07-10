<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$userID = mysqli_real_escape_string($conn, $_POST['userID']);
$friendID = mysqli_real_escape_string($conn, $_POST['friendID']);


$sql = "INSERT INTO friendship(friend_1, friend_2, status) VALUES('$userID', '$friendID', 'friends')";
if ($conn->query($sql) === TRUE) {
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}
$sql2 = "UPDATE friendship SET status ='friends' WHERE friend_1 = '$friendID' AND friend_2 ='$userID' AND status ='pending'";
if ($conn->query($sql2) === TRUE) {
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}
mysqli_close($conn);
?>