<?php
$headers =getallheaders(); 

include "./myresponse.php";
$res=new MyResponse();
$req=new MyRequest($res);
  $file_name = $_FILES['file']['name'];
  $file_size =$_FILES['file']['size'];
  $file_tmp =$_FILES['file']['tmp_name'];
  $file_type=$_FILES['file']['type'];
  $file_ext = strtolower(pathinfo($file_name,PATHINFO_EXTENSION));
  $photoId=$headers['assetId'];
  $extensions= array("jpeg","jpg","png","jfif");
  if(in_array($file_ext,$extensions)=== false)
  {
           $res->sendResponse("Please Upload Image of jpeg|jpg|png|jfif only","",0);
  }
  if($file_size > (10*1024*1024)){
      $res->sendResponse("File size must be less than 10.MB","",0);
   }
   $check = getimagesize($file_tmp);
   if($check === false){
      $res->sendResponse("File is not image","",0);
   }
   // On success validation
   if($photoId=='' || $photoId==null) $photoId=floor(microtime(true) * 1000)."-".rand(1000,9999);
   $photoId='100001_RGB';
   $target_file="./".$photoId.".jpg";
   
   if (move_uploaded_file($file_tmp, $target_file)) {
	  $command = escapeshellcmd('python3 ./python/hand-detection.py');
	  $output = " ".shell_exec($command);
	  $log_filename = "log.txt";
	  file_put_contents($log_filename,"");
	  if(strpos($output,"HAND DETECTED")>=1){
		  $command = escapeshellcmd('python3 ./python/bulkmatching.py');
          $output = $output." ".shell_exec($command);
		  file_put_contents($log_filename, $output . "", FILE_APPEND);
        if(strpos($output,"PALM MATCHED")>=1){
		   $res->sendResponse("MATCHED",$target_file,1);
        } else $res->sendResponse("NOT MATCHING",$target_file,0);
		  
	  }
      else {
		file_put_contents($log_filename, "PALM NOT DETECTED[Payment]" . "", FILE_APPEND);
		$res->sendResponse("PALM NOT DETECTED",$target_file,0); 
	  }
	  
   }else $res->sendResponse("Something went wrong[U500]","",-1);
?>