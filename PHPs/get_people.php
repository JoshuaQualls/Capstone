<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$userID = mysqli_real_escape_string($conn, $_POST['uID']);
$dbdata = array();
$sql = "SELECT p.patronID, p.fname, p.lname
FROM patron AS p
WHERE p.patronID NOT IN( 
SELECT friend_2 FROM friendship WHERE
friend_1 = '$userID'
)
AND p.patronID NOT IN(
SELECT friend_1 FROM friendship WHERE
friend_2 ='$userID'
AND status ='pending'
)
AND p.patronID != '$userID'";
$result = mysqli_query($conn, $sql);
if (!$result) {
    die('Could not query:' . mysql_error());
}
if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
           $dbdata[]=$row; }
} else {
    echo "0 results";
}

echo json_encode($dbdata);
// Close Connection
mysqli_close($conn);
?>