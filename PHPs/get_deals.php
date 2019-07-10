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

$sql = "SELECT deal_description, group_size
FROM active_deals
WHERE barID = '$barID'
AND((time_start IS NULL AND time_end IS NULL) OR
(CURRENT_TIMESTAMP > time_start
AND CURRENT_TIMESTAMP < time_end))
ORDER BY group_size ASC";
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
