<?php include 'connection.php';?>

<!DOCTYPE html>
<html lang="en">
<head>
  <title>Bootstrap Example</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  <style>
    /* Remove the navbar's default rounded borders and increase the bottom margin */ 
    .navbar {
      margin-bottom: 50px;
      border-radius: 0;
    }
    
    /* Remove the jumbotron's default bottom margin */ 
     .jumbotron {
      margin-bottom: 0;
    }
   
    /* Add a gray background color and some padding to the footer */
    footer {
      background-color: #f2f2f2;
      padding: 25px;
    }
  </style>
</head>
<body>


<nav class="navbar navbar-inverse">
  <div class="container-fluid">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>                        
      </button>
      <a class="navbar-brand" href="index.php">Disaster Response</a>
    </div>
    <div class="collapse navbar-collapse" id="myNavbar">
     
    </div>
  </div>
</nav>



<br/>

<div class="container">    
  <div class="row">
 
  <?php 

if(isset($_GET['id']))
{
	$id=mysqli_real_escape_string($con,$_GET['id']);
	$qu="SELECT `patientimg`, `datetimepatient`, `lastname`, `fistname`, `middlename`, `birthday`, `agerange`, `race`, `gender`, `patienthieght`, `patientweight`, `streetaddresspatient`, `citypatinetlive`, `statepatientlive`, `zipcode`, `phonenumber`, `patientproblem`, `patientmedications`, `alergies`, `scenenumberrelated`, `patientlocationgps`, `tag`, `bodyfront`, `bodyback`, `triagecode`, `injuryinfo` FROM `patients` WHERE `id`='$id'";
	$res=mysqli_query($con,$qu) or die('Error: try again later #log8899999');
    $arr_res=mysqli_fetch_array($res);

$locgps="";
$locarr=explode(',',$arr_res['patientlocationgps']);

if(count($locarr)>2)$locgps=$locarr[1].",".$locarr[2];
  ?>
  
  <div class="well"><center><h2>Patient name: <?php echo $arr_res['fistname']." ".$arr_res['middlename']." ".$arr_res['lastname']; ?><br/></h2></center></div>

  <div class="well well-sm" style='<?php echo "background-color: ".$arr_res['tag'];?>'><center><h4>Patient Tag : <?php echo $arr_res['tag'];?></h4></center></div>
      
  <div class="col-sm-4">
      <div class="panel panel-primary">
	    <div class="panel-heading"><?php echo "patient # ".$_GET['id']; ?></div>
        <div class="panel-body"><img src="<?php echo "data:image/;base64,".$arr_res['patientimg']; ?>" class="img-responsive" style="width:100%" alt="Image"></div>
        <div class="panel-footer"></div>
      </div>
    </div>
    
	  <div class="col-sm-4">
      <div class="panel panel-primary">
	    <div class="panel-heading">Patient info</div>
        <div class="panel-body">
		<strong>Patient name:</strong> <?php echo $arr_res['fistname']." ".$arr_res['middlename']." ".$arr_res['lastname']; ?><br/>
		<strong>Patient Birthday:</strong> <?php echo $arr_res['birthday']; ?><br/>
		<strong>Patient agerange:</strong> <?php echo $arr_res['agerange']; ?><br/>
		<strong>Patient race:</strong> <?php echo $arr_res['race']; ?><br/>
		<strong>Patient gender:</strong> <?php echo $arr_res['gender']; ?><br/>
		<strong>Patient hieght:</strong> <?php echo $arr_res['patienthieght']; ?><br/>
		<strong>Patient weight:</strong> <?php echo $arr_res['patientweight']; ?><br/>
		<strong>Street address :</strong><?php echo $arr_res['streetaddresspatient']; ?><br/>
		<strong>City:</strong> <?php echo $arr_res['citypatinetlive']; ?><br/>
		<strong>Zipcode:</strong> <?php echo $arr_res['zipcode']; ?><br/>
		<strong>Date and time added:</strong> <?php echo $arr_res['datetimepatient']; ?><br/>
		<strong>Phone number:</strong> <?php echo $arr_res['phonenumber']; ?><br/>
		<strong>Patient problem :</strong> <?php echo $arr_res['patientproblem']; ?><br/>
		<strong>Phone medications:</strong> <?php echo $arr_res['patientmedications']; ?><br/>
		<strong>Alergies:</strong> <?php echo $arr_res['alergies']; ?><br/>
		<strong>Triage code:</strong> <?php echo $arr_res['triagecode']; ?><br/>
		<strong>Injury info:</strong> <?php echo $arr_res['injuryinfo']; ?><br/>
		
		<strong>Patient location gps :</strong> <?php echo "<a target='_blank' href='https://www.google.com/maps?q=loc:".$locgps."'>location</a> "; ?><br/>
		
		
		<br/>
		
		</div>
        <div class="panel-footer"></div>
      </div>
    </div>
	
	 <div class="col-sm-4">
      <div class="panel panel-primary">
	    <div class="panel-heading">Patient Vital</div>
        <div class="panel-body">
		<?php 
		$qu="SELECT `id`, `timedate`, `pulse`, `bloodpressure`, `respiration`, `levelofconsiciousness` FROM `treatmentvital` WHERE `patientid`='$id'";
		$res=mysqli_query($con,$qu) or die('Error: try again later #log8899999');
   
		while($arr_res2=mysqli_fetch_array($res))
		{
		?>
		<strong>Patient pulse:</strong> <?php echo $arr_res2['pulse']; ?><br/>
		<strong>Patient blood pressure:</strong> <?php echo $arr_res2['bloodpressure']; ?><br/>
		<strong>Patient respiration:</strong> <?php echo $arr_res2['respiration']; ?><br/>
		<strong>Patient level of consiciousness:</strong> <?php echo $arr_res2['levelofconsiciousness']; ?><br/>
		<strong>Patient Time & date:</strong> <?php echo $arr_res2['timedate']; ?><br/>
		<strong>*********************</strong><br/>
		
		
<?php }?>
		<br/>
		
		</div>
        <div class="panel-footer"></div>
      </div>
    </div>
	
	

</div> <div class="row">
	
	<div class="col-sm-4">
      <div class="panel panel-primary">
	    <div class="panel-heading"><?php echo "patient Body front problem location"; ?></div>
        <div class="panel-body"><img src="<?php echo "data:image/;base64,".$arr_res['bodyfront']; ?>" class="img-responsive" style="width:100%" alt="Image"></div>
        <div class="panel-footer"></div>
      </div>
    </div>
	
	<div class="col-sm-4">
      <div class="panel panel-primary">
	    <div class="panel-heading"><?php echo "patient Body front problem location"; ?></div>
        <div class="panel-body"><img src="<?php echo "data:image/;base64,".$arr_res['bodyback']; ?>" class="img-responsive" style="width:100%" alt="Image"></div>
        <div class="panel-footer"></div>
      </div>
    </div>
  <?php
}
  ?>
    
	
  </div>
  
  <div class="row">
  <div class="col-sm-12">
      <div class="panel panel-primary">
	    <div class="panel-heading">Patient Treatment</div>
        <div class="panel-body">
		<?php 
		$qu="SELECT `id`, `type`, `value`, `datetime` FROM `treatmentmenu` WHERE `patientid`='$id'";
		$res=mysqli_query($con,$qu) or die('Error: try again later #log8899999');
   
		while($arr_res3=mysqli_fetch_array($res))
		{
		?>
		<strong>Type:</strong> <?php echo $arr_res3['type']; ?><br/>
		<strong>Value:</strong> <?php echo $arr_res3['value']; ?><br/>
		<strong>Time & date:</strong> <?php echo $arr_res3['datetime']; ?><br/>
		<strong>*********************</strong><br/>
		
		
<?php }?>
		<br/>
		
		</div>
        <div class="panel-footer"></div>
      </div>
    </div>
	</div>
	
	
	
</div><br>

<br><br>



</body>
</html>
