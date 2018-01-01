<?php
include 'connection.php';

$data = json_decode(file_get_contents('php://input'), true);


$token=$data['token'];
$patientid=$data['patientid'];
$type=$data['type'];
$value=$data['value'];




$idadder=verfitoken($token,$con);
if($idadder !== -1)
{
	
	
$patientidarr=explode(",",$patientid);
$patientidv=$patientidarr[1];
		$qu="INSERT INTO `treatmentmenu`(`patientid`, `type`, `value`, `datetime`) VALUES ('$patientidv','$type','$value','$date')";
		$res=mysqli_query($con,$qu) or die('Error: #addt5521411');
		echo $type."  added Successfully to last patient";
		
	

}else
{
	echo "Error: Login again your connection lost #addtre2698800";
}








?>