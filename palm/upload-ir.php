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
   $target_file="./".$photoId.".jpg";
   if (move_uploaded_file($file_tmp, $target_file)) {
      $res->sendResponse("Uploaded",$target_file,"ok");
   }else $res->sendResponse("Something went wrong[U500]","","no");
?>