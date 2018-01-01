<?php
include 'connection.php';

$data = json_decode(file_get_contents('php://input'), true);


$token=$data['token'];
$scenenumberrelated=$data['scenenumberrelated'];
$patientlocationgps=$data['patientlocationgps'];
$lastname=$data['lastname'];
$firstname=$data['fistname'];
$middlename=$data['middlename'];
$birthday=$data['birthday'];
$agerange=$data['agerange'];
$race=$data['race'];
$gender=$data['gender'];
$patienthieght=$data['patienthieght'];
$patientweight=$data['patientweight'];
$streetaddresspatient=$data['streetaddresspatient'];
$citypatinetlive=$data['citypatinetlive'];
$statepatientlive=$data['statepatientlive'];
$zipcode=$data['zipcode'];
$phonenumber=$data['phonenumber'];
$patientproblem=$data['patientproblem'];
$patientmedications=$data['patientmedications'];
$alergies=$data['alergies'];
$img=$data['img'];



$idadder=verfitoken($token,$con);
if($idadder !== -1)
{
	
	

		$qu="INSERT INTO `patients`( `datetimepatient`, `lastname`, `fistname`, `middlename`, `birthday`, `agerange`, `race`, `gender`, `patienthieght`, `patientweight`, `streetaddresspatient`, `citypatinetlive`, `statepatientlive`, `zipcode`, `phonenumber`, `patientproblem`, `patientmedications`, `alergies`, `scenenumberrelated`,`patientlocationgps`,`patientimg`) 
								VALUES ('$date','$lastname','$firstname','$middlename','$birthday','$agerange','$race','$gender','$patienthieght','$patientweight','$streetaddresspatient','$citypatinetlive','$statepatientlive','$zipcode','$phonenumber','$patientproblem','$patientmedications','$alergies','$scenenumberrelated','$patientlocationgps','$img');
								

								";
		$res=mysqli_query($con,$qu) or die('Error: #addscene99999122');
		
				$qu="SELECT LAST_INSERT_ID();";
		$res=mysqli_query($con,$qu) or die('Error: #addscene99999155');
		$arr_res=mysqli_fetch_array($res);
	echo ",".$arr_res[0].",";
		
	

}else
{
	echo "Error: Login again your connection lost ";
}








?>