<?php
include 'connection.php';

$data = json_decode(file_get_contents('php://input'), true);


$token=$data['token'];


$idadder=verfitoken($token,$con);
if($idadder !== -1)
{
	
echo $date;
	

}else
{
	echo "Error: Login again your connection lost #timedate00";
}








?>