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

<div class="well"><center><h2>Patients Page</h2></center></div>

<footer class="container-fluid text-center">
  <p>Search for Patient by number # or patient name</p>  
  <form class="form-inline">
    <input type="patient" class="form-control" name='patientsearch' size="50" placeholder="Patient by number # or patient name">
    <button type="submit" class="btn btn-danger">Search</button>
  </form>
</footer>
<br/>


  <?php 
 if(isset($_GET['patientsearch']))
{
	$idsearch=mysqli_real_escape_string($con,$_GET['patientsearch']);
	$qu="SELECT `id`,`patientimg`, `datetimepatient`, `lastname`, `fistname`, `middlename`, `tag`
 FROM `patients` where `id` like '%$idsearch%' or `lastname` like '%$idsearch%' or `fistname` like '%$idsearch%' or `middlename` like '%$idsearch%' limit 500";

 $res=mysqli_query($con,$qu) or die('Error: try again later #patientsp8899999');
  ?>
  <div class="container">    
  <div class="row">
  <?php
  while($arr_res=mysqli_fetch_array($res))
  {

  
  ?>

  <div class="col-sm-3">
      <div class="panel panel-primary" >
	  <a href='patientdetails.php?id=<?php echo $arr_res['id'];?>'>
	  		<div class="well well-sm" style='<?php echo "background-color: ".$arr_res['tag'];?>'><center><h4>Patient Tag : <?php echo $arr_res['tag'];?></h4></center></div>

        <div class="panel-heading"><?php echo "Patient # ".$arr_res['id']; ?></div>
        <div class="panel-body"><img src="<?php echo "data:image/;base64,".$arr_res['patientimg']; ?>" class="img-responsive" style="width:100%" alt="Image"></div>
        <div class="panel-footer"><?php echo "Patient Name : <br/>".$arr_res['fistname']." ".$arr_res['middlename']." ".$arr_res['lastname']; ?></div>
</a>     
	 </div>
    </div>
	
    
  <?php
  }?>
  </div></div>
  <?php
 
}

if(isset($_GET['scenesid']))
{
$iarr=$_GET['scenesid'];

for($count=0;$count<count($iarr);$count++)
{	
$scenetoget=$iarr[$count];
$qu="SELECT `id`,`patientimg`, `datetimepatient`, `lastname`, `fistname`, `middlename`, `tag`
 FROM `patients` WHERE `scenenumberrelated`='$scenetoget' limit 500";

?>

<br/><div class="container">    
  <div class="row"><div class="well well-sm"><center><h4>Patients for scene # <?php echo $iarr[$count];?></h4></center>
  <br/>
  <?php 
  $scenenumber=$iarr[$count];
  $qutag="SELECT count(*) FROM `patients` WHERE `scenenumberrelated`='$scenenumber'  and `tag`='Red'";
  $restag=mysqli_query($con,$qutag) or die('Error: try again later #patientsp8899999');
  $arr_restag=mysqli_fetch_array($restag);
$countred=$arr_restag[0];

 $qutag="SELECT count(*) FROM `patients` WHERE `scenenumberrelated`='$scenenumber'  and `tag`='Green'";
  $restag=mysqli_query($con,$qutag) or die('Error: try again later #patientsp8899999');
  $arr_restag=mysqli_fetch_array($restag);
$countgreen=$arr_restag[0];

 $qutag="SELECT count(*) FROM `patients` WHERE `scenenumberrelated`='$scenenumber'  and `tag`='Yellow'";
  $restag=mysqli_query($con,$qutag) or die('Error: try again later #patientsp8899999');
  $arr_restag=mysqli_fetch_array($restag);
$countyellow=$arr_restag[0];


 $qutag="SELECT count(*) FROM `patients` WHERE `scenenumberrelated`='$scenenumber'  and `tag`='Black'";
  $restag=mysqli_query($con,$qutag) or die('Error: try again later #patientsp8899999');
  $arr_restag=mysqli_fetch_array($restag);
$countblack=$arr_restag[0];

$countredp=0.0;
$countgreenp=0.0;
$countyellowp=0.0;
$countblackp=0.0;

$sumtag=$countred+$countgreen+$countyellow+$countblack;
if($sumtag>0)
{
	$sumtag=$sumtag+0.0;
$countredp=$countred/$sumtag * 100.0;
$countgreenp=$countgreen/$sumtag * 100.0;
$countyellowp=$countyellow/$sumtag * 100.0;
$countblackp=$countblack/$sumtag * 100.0;
}
echo "Number of patients with Green Tag: <strong>".$countgreen." </strong>Percentage :<strong> ".$countgreenp."%</strong><br/>";
echo "Number of patients with Yellow Tag: <strong>".$countyellow." </strong>Percentage : <strong>".$countyellowp."%</strong><br/>";
echo "Number of patients with Red Tag: <strong>".$countred." </strong>Percentage : <strong>".$countredp."%</strong><br/>";
echo "Number of patients with Black Tag:<strong> ".$countblack."</strong> Percentage : <strong>".$countblackp."%</strong><br/>";



  
  ?>
  
  </div><br/>

<?php
 $res=mysqli_query($con,$qu) or die('Error: try again later #patientsp8899999');
  
  while($arr_res=mysqli_fetch_array($res))
  {

  
  ?>
  <div class="col-sm-3">
      <div class="panel panel-primary" >
	   <a href='patientdetails.php?id=<?php echo $arr_res['id'];?>'>
	   <div class="well well-sm" style='<?php echo "background-color: ".$arr_res['tag'];?>'><center><h4>Patient Tag : <?php echo $arr_res['tag'];?></h4></center></div><br/>

        <div class="panel-heading"><?php echo "Patient # ".$arr_res['id']; ?></div>
        <div class="panel-body"><img src="<?php echo "data:image/;base64,".$arr_res['patientimg']; ?>" class="img-responsive" style="width:100%" alt="Image"></div>
        <div class="panel-footer"><?php echo "Patient Name : <br/>".$arr_res['fistname']." ".$arr_res['middlename']." ".$arr_res['lastname']; ?></div>
      </a>
	  </div>
    </div>
    
  <?php
  }
  ?>
  </div></div>
  <?php
 
}
  
  
  }
  
  
  ?>
    
	
  </div>
</div><br>

<br><br>



</body>
</html>
