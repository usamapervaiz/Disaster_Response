<?php

$date=date('y-m-d H:i:s');

function generatesession($pass,$email)
{
	$session=md5($pass.$email.$GLOBALS['date']);
	return $session;	
}

$con=mysqli_connect("localhost","root","");


// Check connection
if (mysqli_connect_errno())
  {
  echo "هناك خلل بالاتصال الرجاء العوده لاحقا 32065 " . mysqli_connect_error();
  }else{}


$db=mysqli_select_db($con,"nasa_disaster_response");
//$db=mysqli_select_db($con,"colorask_nasa-disaster-response");


function clearfromdanger($va)
{	
$vaa=str_replace("<?","",$va);
$vaa=str_replace("?>","",$vaa);
$vaa=str_replace("'","",$vaa);
$vaa=str_replace("*","",$vaa);
$vaa=str_replace("%","",$vaa);
$vaa=str_replace("\\","",$vaa);

return $vaa;
}


function verfitoken($tokenrec,$con)

{
	$tokentoprocessarr=explode(",",$tokenrec);
	$tokenvalue=$tokentoprocessarr[1];
	
	

$qu="SELECT `id` FROM `users_data` WHERE `token`='$tokenvalue'";
$res=mysqli_query($con,$qu) or die('Error: login again something went wrong #conn962');
$arr_res=mysqli_fetch_array($res);
if(strlen($arr_res['id'])>0)
{
	return $arr_res['id'];
}else
{
return -1;	
}


	
}

?>