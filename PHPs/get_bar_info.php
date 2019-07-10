<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$barID = mysqli_real_escape_string($conn, $_POST['barID']);
$sql = "SELECT bar_name, cover FROM bar WHERE barID = '$barID'";

$result = mysqli_query($conn, $sql);
$dbdata = array();
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