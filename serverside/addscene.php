<?php
include 'connection.php';

$data = json_decode(file_get_contents('php://input'), true);


$token=$data['token'];
$scenenumber=$data['scenenumber'];
$locationgps=$data['locationgps'];
$manuallocation=$data['manuallocation'];
$manualpostalcode=$data['manualpostalcode'];
$disastertype=$data['disastertype'];
$img=$data['img'];

//$scenenumber=$data['scenenumber'];

$idadder=verfitoken($token,$con);
if($idadder !== -1)
{
	
	if(checkscenenumber($scenenumber,$con))
	{

		$qu="INSERT INTO `scene`(`scenenumber`, `locationgps`, `manuallocation`, `manualpostalcode`, `idadder`,`disastertype`,`datetime`,`imgscene`) VALUES 
							('$scenenumber','$locationgps','$manuallocation','$manualpostalcode','$idadder','$disastertype','$date','$img')";
		$res=mysqli_query($con,$qu) or die('Error: #addscene99999122');
	echo "Successfully added scene ";
		
	}else
	{
		$bigestscenenumber=findmaximumscenenumber($con);
		echo "Error: Scene number already exist ,try another scene number bigger than ".$bigestscenenumber;
	}
	

}else
{
	echo "Error: Login again your connection lost ".$token;
}


function checkscenenumber($scenenumberwanted,$con)
{
$qu="SELECT `id` FROM `scene` WHERE `scenenumber`='$scenenumberwanted'";

$res=mysqli_query($con,$qu) or die('Error: login again something went wrong ');
$arr_res=mysqli_fetch_array($res);
if(strlen($arr_res['id'])>0)
{
return false;
}else
{
return true;	
}

	
}



function findmaximumscenenumber($con)
{

$qu="SELECT max(`scenenumber`) FROM `scene` WHERE 1";
$res=mysqli_query($con,$qu) or die('Error: #addscene56660000');
$arr_res=mysqli_fetch_array($res);
return $arr_res[0];
	
}

?>