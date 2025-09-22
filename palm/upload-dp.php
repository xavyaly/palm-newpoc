<?php
include './../config.php';
include './../cors.php';
include "./../myresponse.php";
$res=new MyResponse();
$req=new MyRequest($res);
$requestAuthority0->validateSession();
$EMAIL=$requestAuthority0->getUserEmail();
  $file_name = $_FILES['upf']['name'];
  $file_size =$_FILES['upf']['size'];
  $file_tmp =$_FILES['upf']['tmp_name'];
  $file_type=$_FILES['upf']['type'];
  $file_ext = strtolower(pathinfo($file_name,PATHINFO_EXTENSION));
  $extensions= array("jpeg","jpg","png","jfif");
  if(in_array($file_ext,$extensions)=== false)
  {
           $res->sendResponse("Please Upload Image of jpeg|jpg|png|jfif only","",0);
  }
  if($file_size > (1*1024*1024)){
      $res->sendResponse("File size must be less than 1.MB","",0);
   }
   $check = getimagesize($file_tmp);
   if($check === false){
      $res->sendResponse("File is not image","",0);
   }
   // On success validation
   $new_name=str_replace("@","__",$EMAIL).".jpeg";
   $target_file="./../../contents/profile-image/".$new_name;
   if (move_uploaded_file($file_tmp, $target_file)) {
      $res->sendResponse("Uploaded",$new_name,1);
   }else $res->sendResponse("Something went wrong[U500]","",0);
?>