<?php
include 'connection.php';

$data = json_decode(file_get_contents('php://input'), true);


$token=$data['token'];
$patientid=$data['patientid'];
$pulse=$data['pulse'];
$bloodpressure=$data['bloodpressure'];
$respiration=$data['respiration'];
$levelofconsiciousness=$data['levelofconsiciousness'];




$idadder=verfitoken($token,$con);
if($idadder !== -1)
{
	
	
$patientidarr=explode(",",$patientid);
$patientidv=$patientidarr[1];

		$qu="INSERT INTO `treatmentvital`( `patientid`, `timedate`, `pulse`, `bloodpressure`, `respiration`, `levelofconsiciousness`) 
		VALUES ('$patientidv','$date','$pulse','$bloodpressure','$respiration','$levelofconsiciousness')";
		
		
		$res=mysqli_query($con,$qu) or die('Error: #addvital9632');
		echo " Vital added ";
		
	

}else
{
	echo "Error: Login again your connection lost #addvital9636";
}








?>