<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$userID = mysqli_real_escape_string($conn, $_POST['userID']);
$friendID = mysqli_real_escape_string($conn, $_POST['friendID']);


$sql = "INSERT INTO friendship(friend_1, friend_2, status) VALUES('$userID', '$friendID', 'pending')";
if ($conn->query($sql) === TRUE) {
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$sql2 = "SELECT u.firebaseID, p2.fname, p2.lname FROM users AS u, patron AS p, patron AS p2 WHERE u.uID = p.patronID AND  u.uID = '$friendID'  AND p2.patronID = '$userID'";
$result2 = mysqli_query($conn, $sql2);
$dbdata = array();
if ($result2->num_rows > 0) {
    // output data of each row
    while($row = $result2->fetch_assoc()) {
           $dbdata[]=$row; }
} else {
    echo "Connection Failed";
}

echo json_encode($dbdata);
// Close Connection
mysqli_close($conn);
?>