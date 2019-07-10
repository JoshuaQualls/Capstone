<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$name = mysqli_real_escape_string($conn, $_POST['name']);
$dbdata = array();
$sql = "SELECT p.patronID
FROM current_patrons as p,bar as b
WHERE b.bar_name= '$name'
AND b.barID=p.barID;
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
    echo "0 results";
}

echo json_encode($dbdata);
// Close Connection
mysqli_close($conn);
?>