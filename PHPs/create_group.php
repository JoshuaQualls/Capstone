<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu","i494f18_team48","my+sql=i494f18_team48","i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$userID = mysqli_real_escape_string($conn, $_POST['userID']);


$sql = "SELECT barID FROM current_patrons WHERE patronID = '$userID'";

$result = mysqli_query($conn, $sql);
if (!$result){
	die('Could not query:' . mysqli_error());
}

$row = $result->fetch_assoc();
$barID = $row['barID'];

unset($sql);

$sql = "INSERT INTO groups(barID, creatorID) VALUES('$barID', '$userID')";
if ($conn->query($sql) === TRUE) {
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

unset($sql);
unset($result);
unset($row);

$sql = "SELECT groupID FROM groups WHERE creatorID = '$userID'";

$result = mysqli_query($conn, $sql);
if(!$result){
	die('Could not query:' . mysqli_error());
	}
	$row = $result->fetch_assoc();

$groupID = $row['groupID'];

unset($sql);

$sql = "INSERT INTO active_groups(groupID, userID, status) VALUES('$groupID', '$userID', 'grouped')";
if ($conn->query($sql) === TRUE) {
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}
echo $groupID;
	
	
mysqli_close($conn);
?>