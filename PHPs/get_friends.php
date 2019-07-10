<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$userID = mysqli_real_escape_string($conn, $_POST['userID']);
$dbdata = array();
$sql = "SELECT f.friend_2, p2.fname, p2.lname
FROM patron AS p, patron AS p2, friendship AS f
WHERE p.patronID = f.friend_1
AND p2.patronID = f.friend_2
AND p.patronID = '$userID'
AND f.status = 'friends';
";
$result = mysqli_query($conn, $sql);
if (!$result) {
    die('Could not query:' . mysql_error());
}
if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
           $dbdata[]=$row; }
} else {
    echo $userID;
}

echo json_encode($dbdata);
// Close Connection
mysqli_close($conn);
?>