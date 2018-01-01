<?php
include 'connection.php';

$data = json_decode(file_get_contents('php://input'), true);


$token=$data['token'];
$patientid=$data['patientid'];
$tag=$data['tag'];




$idadder=verfitoken($token,$con);
if($idadder !== -1)
{
	
	
$patientidarr=explode(",",$patientid);
$patientidv=$patientidarr[1];
		$qu="UPDATE `patients` SET `tag`='$tag' WHERE `id`='$patientidv'";
		$res=mysqli_query($con,$qu) or die('Error: #addt5521411');
		echo $tag." tag added to last patient";
		
	

}else
{
	echo "Error: Login again your connection lost #addt5521400";
}








?>