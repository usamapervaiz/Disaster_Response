<?php
include 'connection.php';

$data = json_decode(file_get_contents('php://input'), true);


$token=$data['token'];
$patientid=$data['patientid'];
$bodyfront=$data['bodyfront'];




$idadder=verfitoken($token,$con);
if($idadder !== -1)
{
	
	
$patientidarr=explode(",",$patientid);
$patientidv=$patientidarr[1];
		$qu="UPDATE `patients` SET `bodyfront`='$bodyfront' WHERE `id`='$patientidv'";
		$res=mysqli_query($con,$qu) or die('Error: #addt5521411');
		echo " bodyfront added to last patient";
		
	

}else
{
	echo "Error: Login again your connection lost #addfrontbodimg5521400";
}








?>