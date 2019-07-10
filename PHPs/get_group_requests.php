<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$userID = mysqli_real_escape_string($conn, $_POST['userID']);


$sql = "SELECT p.fname, p.lname, p.patronID, ag.groupID
FROM patron AS p, active_groups AS ag, groups AS g
WHERE ag.groupID = g.groupID
AND g.creatorID = p.patronID
AND ag.userID = '$userID'
AND ag.status='pending'";
$dbdata = array();
$result = mysqli_query($conn, $sql);
if (!$result) {
    die('Could not query:' . mysql_error());
}
if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
           $dbdata[]=$row; }
} else {
    die('Could not query:' . mysql_error());
}

echo json_encode($dbdata);
//Close Connection
mysqli_close($conn);
?>