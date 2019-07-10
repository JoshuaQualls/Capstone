<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$userID = mysqli_real_escape_string($conn, $_POST['userID']);


$sql = "SELECT fname, lname
FROM patron
WHERE patronID = '$userID'";
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
// Close Connection
mysqli_close($conn);
?>