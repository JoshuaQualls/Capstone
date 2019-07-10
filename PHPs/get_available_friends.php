
<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$userID = mysqli_real_escape_string($conn, $_POST['userID']);
$barID = mysqli_real_escape_string($conn, $_POST['barID']);
$dbdata = array();
$sql = "SELECT f.friend_2, p2.fname, p2.lname
FROM patron AS p, patron AS p2, current_patrons AS c, friendship AS f
WHERE p.patronID = '$userID'
AND p.patronID = f.friend_1
AND p2.patronID = f.friend_2
AND c.barID = '$barID'
AND c.patronID = p2.patronID
AND f.status = 'friends'
AND f.friend_2 NOT IN 
	(SELECT userID FROM active_groups)
";
$result = mysqli_query($conn, $sql);
if (!$result) {
    die('Could not query:' . mysqli_error());
}
if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
           $dbdata[]=$row; }
} else {
    die('Could not query:' . mysqli_error());
}

echo json_encode($dbdata);
// Close Connection
mysqli_close($conn);
?>