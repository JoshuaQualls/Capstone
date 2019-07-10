<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$userID = mysqli_real_escape_string($conn, $_POST['userID']);


$sql = "SELECT f.friend_1, p.fname, p.lname 
FROM friendship AS f, patron AS p 
WHERE p.patronID = f.friend_1
AND  friend_2 ='$userID' 
AND status ='pending'";
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