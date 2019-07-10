<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$groupID = mysqli_real_escape_string($conn, $_POST['groupID']);
$dbdata = array();


$sql = "SELECT ad.dealID, ad.group_size, ad.deal_description FROM active_deals AS ad, groups AS g
WHERE g.barID = ad.barID
AND g.groupID = '$groupID'
AND ad.dealID NOT IN(
	SELECT rd.dealID 
	FROM redeemed_deals AS rd
	WHERE rd.groupID = '$groupID'
	)
ORDER BY ad.group_size ASC";
	
$result = mysqli_query($conn, $sql);
if (!$result) {
    die('Could not query:' . mysql_error());
}
if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
           $dbdata[]=$row; }
} else {
    echo "Could not query";
}

echo json_encode($dbdata);
// Close Connection
mysqli_close($conn);
?>