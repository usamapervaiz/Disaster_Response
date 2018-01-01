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

<div class="well"><center><h2>Scene Page</h2></center></div>

<footer class="container-fluid text-center">
  <p>Search for scene by number #</p>  
  <form class="form-inline" method='get' action='index.php'>Scene #:
    <input type="search" name='search' class="form-control" size="50" placeholder="Scene #">
    <button type="submit" class="btn btn-danger">Search</button>
  </form>
</footer>
<br/>

<div class="container">    
  <div class="row">
  <form action='patients.php' method='get'>
  <?php 

if(isset($_GET['search']))
{$idsearch=mysqli_real_escape_string($con,$_GET['search']);
	  $qu="SELECT `id`, `imgscene`, `scenenumber`, `locationgps`, `manuallocation`, `manualpostalcode`, `disastertype`, `datetime` FROM `scene` WHERE `scenenumber` like '%$idsearch%' order by `datetime` desc limit 200";

}else{
	  $qu="SELECT `id`, `imgscene`, `scenenumber`, `locationgps`, `manuallocation`, `manualpostalcode`, `disastertype`, `datetime` FROM `scene` WHERE 1 order by `datetime` desc limit 200";

}	
 $res=mysqli_query($con,$qu) or die('Error: try again later #log8899999');
  
  while($arr_res=mysqli_fetch_array($res))
  {

$locgps="";
$locarr=explode(',',$arr_res['locationgps']);

if(count($locarr)>2)$locgps=$locarr[1].",".$locarr[2];

  
  ?>
  
  <div class="col-sm-3">
      <div class="panel panel-primary">
        <div class="panel-heading"><input type="checkbox" style='zoom: 1.2;color:green' name="scenesid[]" value="<?php echo $arr_res['scenenumber']; ?>"> <?php echo "Scene # ".$arr_res['scenenumber']; ?></div>
        <div class="panel-body"><img src="<?php echo "data:image/;base64,".$arr_res['imgscene']; ?>" class="img-responsive" style="width:100%" alt="Image"></div>
        <div class="panel-footer"><?php echo "<a target='_blank' href='https://www.google.com/maps?q=loc:".$locgps."'>location</a> ".$arr_res['manuallocation']; ?></div>
      </div>
    </div>
    
  <?php
  }
  
  
  ?>
    
	
  </div>
</div><br>
<center><button type="submit" class="btn btn-success">Show patients of selected scenes</button></center>
</form>
<br><br>



</body>
</html>
