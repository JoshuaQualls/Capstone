<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$groupID = mysqli_real_escape_string($conn, $_POST['groupID']);
$friendID = mysqli_real_escape_string($conn, $_POST['friendID']);
$userID = mysqli_real_escape_string($conn, $_POST['userID']);

$sql = "INSERT INTO active_groups(groupID, userID, status) VALUES('$groupID', '$friendID', 'pending')";
if ($conn->query($sql) === TRUE) {
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

unset($sql);

$sql = "SELECT u.firebaseID, p2.fname, p2.lname 
FROM users AS u, patron AS p, patron AS p2 
WHERE u.uID = p.patronID 
AND  u.uID = '$friendID'  
AND p2.patronID = '$userID'";

$result = mysqli_query($conn, $sql);
if (!$result) {
    die('Could not query:' . mysql_error() . '1');
}
if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
           $dbdata[]=$row; }
} else {
    die('Could not query:' . mysql_error() . '2');
}

echo json_encode($dbdata);

mysqli_close($conn);
?>