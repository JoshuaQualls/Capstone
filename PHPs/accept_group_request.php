<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$userID = mysqli_real_escape_string($conn, $_POST['userID']);
$groupID = mysqli_real_escape_string($conn, $_POST['groupID']);


$sql = "UPDATE active_groups SET status ='grouped' WHERE userID = '$userID' AND groupID = '$groupID'";
if ($conn->query($sql) === TRUE) {
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}
mysqli_close($conn);
?>