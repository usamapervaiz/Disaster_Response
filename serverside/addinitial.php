<?php
include 'connection.php';

$data = json_decode(file_get_contents('php://input'), true);


$token=$data['token'];
$patientid=$data['patientid'];
$ini=$data['ini'];




$idadder=verfitoken($token,$con);
if($idadder !== -1)
{
	
	
$patientidarr=explode(",",$patientid);
$patientidv=$patientidarr[1];
		$qu="UPDATE `patients` SET `triagecode`='$ini' WHERE `id`='$patientidv'";
		$res=mysqli_query($con,$qu) or die('Error: #addini55221');
		echo " triage code added to last patient";
		
	

}else
{
	echo "Error: Login again your connection lost #addini552200";
}








?>