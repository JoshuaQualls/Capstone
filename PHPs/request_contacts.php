<?php

// Create connection
$conn = mysqli_connect("db.sice.indiana.edu", "i494f18_team48", "my+sql=i494f18_team48", "i494f18_team48");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$JSON_Received = file_get_contents('php://input');


$obj = json_decode($JSON_Received, true);


$users = array();

$userID = $obj['userID'];


foreach ($obj['contact'] as $key => $value) {
    $sql    = "SELECT uID FROM users WHERE phone = '$value'";
    $result = mysqli_query($conn, $sql);
    if (!$result) {
    } else {
        $row = $result->fetch_assoc();
        $ID = $row['uID'];
        if($ID != ''){
        array_push($users, $row['uID']);
        }
    }
    unset($sql);
    unset($row);
    unset($result);
    
}
foreach ($users as $user) {
    $sql = "INSERT INTO friendship(friend_1, friend_2, status) VALUES('$userID', '$user', 'pending')";
    if ($conn->query($sql) === TRUE) {
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}
}

//Close Connection
mysqli_close($conn);
?>