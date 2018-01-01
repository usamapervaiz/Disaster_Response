<?php
include 'connection.php';

$data = json_decode(file_get_contents('php://input'), true);


$firstname=$data['firstname'];
$lastname=$data['lastname'];
$email=$data['email'];
$pass=$data['pass'];
$hospitalname=$data['hospitalname'];
$hospitalid=$data['hospitalid'];
$birthdayday=$data['birthday'];
$gender=$data['gender'];
$phonenumber=$data['phonenumber'];







$token=generatesession($pass,$email);


$qu="INSERT INTO `users_data`( `firstname`, `lastname`, `emailaddress`, `pass`, `hospitalname`, `hospital_id`, `birthdayday`, `gender`, `phonenumber`, `token`, `datetime`) 
						VALUES ('$firstname','$lastname','$email','$pass','$hospitalname','$hospitalid','$birthdayday','$gender','$phonenumber','$token','$date') ";
$res=mysqli_query($con,$qu) or die('Error: Make sure that you are not registered before !');
echo "Success";


?>