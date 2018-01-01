<?php
include 'connection.php';

$data = json_decode(file_get_contents('php://input'), true);


$token=$data['token'];
$patientid=$data['patientid'];
$bodyback=$data['bodyback'];




$idadder=verfitoken($token,$con);
if($idadder !== -1)
{
	
	
$patientidarr=explode(",",$patientid);
$patientidv=$patientidarr[1];
		$qu="UPDATE `patients` SET `bodyback`='$bodyback' WHERE `id`='$patientidv'";
		$res=mysqli_query($con,$qu) or die('Error: #addt5521411');
		echo " bodyback added to last patient";
		
	

}else
{
	echo "Error: Login again your connection lost #addbackbodimg5521400";
}








?>