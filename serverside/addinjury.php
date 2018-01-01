<?php
include 'connection.php';

$data = json_decode(file_get_contents('php://input'), true);


$token=$data['token'];
$patientid=$data['patientid'];
$injuryinfo=$data['injuryinfo'];




$idadder=verfitoken($token,$con);
if($idadder !== -1)
{
	
	
$patientidarr=explode(",",$patientid);
$patientidv=$patientidarr[1];
		$qu="UPDATE `patients` SET `injuryinfo`='$injuryinfo' WHERE `id`='$patientidv'";
		$res=mysqli_query($con,$qu) or die('Error: #addinjury5521422');
		echo " injury info added to last patient";
		
	

}else
{
	echo "Error: Login again your connection lost #addinjury5521429";
}








?>