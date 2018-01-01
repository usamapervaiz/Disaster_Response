<?php
include 'connection.php';

$data = json_decode(file_get_contents('php://input'), true);


$email=$data['email'];
$pass=$data['pass'];


$qu="SELECT `token` FROM `users_data` WHERE `emailaddress`='$email' and `pass`='$pass'";
$res=mysqli_query($con,$qu) or die('Error: try again later #log8899999');

$arr_res=mysqli_fetch_array($res);


if(strlen($arr_res['token'])>0)
{
	echo ",".$arr_res['token'].",";
	
}else
{
	echo "Error: Check that you enter correct email and password";
}

?>