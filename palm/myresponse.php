<?php
class MyResponse
{
	public $status;
	public $msg;
	public $data;
	public $token;
	function __construct()
	{
	$this->status=0;
	$this->msg=null;	
	}
	public function sendResponse($msg,$data,$status,$token="")
	{
		$this->msg=$msg;
		$this->data=$data;
		$this->status=$status;
		$this->token=$token;
		$jsonData=json_encode($this,true);
		die($jsonData);
	}
};
class MyRequest
{
	private $res;
	function __construct($response)
	{
	$this->res=$response;
	}
	function validateParam($value,$type,$filed){
		
		if($type=="text-only"){
			if (preg_match("/^[a-zA-Z]+$/",$value)) return true;
			$this->res->sendResponse("must be A-Z",$filed,-1);
		}
		else if($type=="text-number"){
			if (preg_match("/^[a-zA-Z0-9]+$/",$value)) return true;
			$this->res->sendResponse("must be A-Z0-9",$filed,-1);
		}
		else if($type=="text-space"){
			if (preg_match("/^[a-zA-Z\s]+$/",$value)) return true;
			$this->res->sendResponse("must be A-Z",$filed,-1);
		}
		else if($type=="text-number-space"){
			if (preg_match("/^[a-zA-Z0-9\s]+$/",$value)) return true;
			$this->res->sendResponse("must be A-Z0-9",$filed,-1);
		}
		else if($type=="text-number-space-dot"){
			if (preg_match("/^[a-zA-Z0-9\s.]+$/",$value)) return true;
			$this->res->sendResponse("must be A-Z0-9.",$filed,-1);
		}
		else if($type=="text-mixed"){
			if (preg_match("/^[a-zA-Z0-9\s\.\-_:]+$/",$value)) return true;
			$this->res->sendResponse("must be A-Z0-9._-",$filed,-1);
		}
		else if($type=="text-freetext"){
			if (preg_match("/^[a-zA-Z0-9\s.\-_:,\'\"\/]+$/",$value)) return true;
			$this->res->sendResponse("must be A-Z0-9._-:/\"',",$filed,-1);
		}
		else if($type=="decimal"){
			if (preg_match("/^\d+(\.\d+)?$/",$value)) return true;
			$this->res->sendResponse("must be number or decimal number",$filed,-1);
		}
		else if($type=="integer"){
			if (preg_match("/^[0-9]+$/",$value)) return true;
			$this->res->sendResponse("must be number nor decimal number",$filed,-1);
		}
		else if($type=="yyyy-mm-dd"){
			if (preg_match("/^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$/",$value)) {
				return true;
			} else {
				$this->res->sendResponse("must be yyyy-mm-dd",$filed,-1);
			}
		}else if($type=="phone10"){
			if (preg_match("/^[6-9][0-9]{9}$/",$value)) {
				return true;
			} else {
				$this->res->sendResponse("must be 10 digits",$filed,-1);
			}
		}
		else if($type=="password"){
			if (preg_match("/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@!#$%&\-_\^\.])[a-zA-Z0-9@!#$%&\-_\^\.]{8,}$/",$value)) return true;
			$this->res->sendResponse("must be one upper,one lower and one special letter.",$filed,-1);
			
		}
		else if($type=="email"){
			if (!filter_var($value, FILTER_VALIDATE_EMAIL)) {
				$this->res->sendResponse("must be valid email",$filed,-1);
			}
		}
		else if($type=="person-name"){
			if (preg_match("/^[A-Za-z\.\s]+$/",$value)) {
				return true;
			} else {
				$this->res->sendResponse("must be A-Z .",$filed,-1);
			}
		}
		else if($type=="age"){
			if (preg_match("/^[0-9]{1,2}$/",$value)) {
				return true;
			} else {
				$this->res->sendResponse("must be 0 or up to 99",$filed,-1);
			}
		}
		else if($type=="iadhaar"){
			if (preg_match("/^[0-9]{12}$/",$value)) {
				return true;
			} else {
				$this->res->sendResponse("must be 12 digits",$filed,-1);
			}
		}
  
	}
	function getPost($_paramName,$_slashed=true){
		
		$val=isset($_POST[$_paramName])?$_POST[$_paramName]:null;
		
		if($val==null) return(null);
		$val=trim($val);
		if($_slashed==true)
			$val=addslashes($val);
		$val=str_replace("<","&lt;",$val);
		$val=str_replace(">","&gt;",$val);
		$val=str_replace("(","&#40;",$val);
		$val=str_replace(")","&#41;",$val);
		$val=str_replace(";","&#59;",$val);
		return($val);
	}
	
	function getGet($_paramName){
		$val=isset($_GET[$_paramName])?$_GET[$_paramName]:null;
		
		if($val==null) return(null);
		$val=trim($val);
		$val=addslashes($val);
		$val=str_replace("<","&lt;",$val);
		$val=str_replace(">","&gt;",$val);
		$val=str_replace("(","&#40;",$val);
		$val=str_replace(")","&#41;",$val);
		$val=str_replace(";","&#59;",$val);
		return($val);
	}
	function get($_paramName){
		$val=isset($_GET[$_paramName])?$_GET[$_paramName]:null; 
		if($val==null) return($this->getPost($_paramName));
		$val=trim($val);
		$val=addslashes($val);
		$val=str_replace("<","&lt;",$val);
		$val=str_replace(">","&gt;",$val);
		$val=str_replace("(","&#40;",$val);
		$val=str_replace(")","&#41;",$val);
		return($val);
	}
	function getJsonBody(){
		$rawData=file_get_contents('php://input');
		$dcrybody=$this->cryptoJsAesDecrypt("HBsgds3784632784",$rawData);
		$jsonReq=json_decode($dcrybody,false);
		if( json_last_error()!=JSON_ERROR_NONE){
			$this->res->sendResponse("Kindly check your data","",-1);
		}
		return($jsonReq);
	}
	function getRawBody(){
		$rawData=file_get_contents('php://input');
		return($rawData);
	}
	// Crypto JS
	function cryptoJsAesDecrypt($passphrase, $jsonString){
		$jsondata = json_decode($jsonString, true);
		try {
			$salt = hex2bin($jsondata["s"]);
			$iv  = hex2bin($jsondata["iv"]);
		} catch(Exception $e) { return null; }
		$ct = base64_decode($jsondata["ct"]);
		$concatedPassphrase = $passphrase.$salt;
		$md5 = array();
		$md5[0] = md5($concatedPassphrase, true);
		$result = $md5[0];
		for ($i = 1; $i < 3; $i++) {
			$md5[$i] = md5($md5[$i - 1].$concatedPassphrase, true);
			$result .= $md5[$i];
		}
		$key = substr($result, 0, 32);
		$data = openssl_decrypt($ct, 'aes-256-cbc', $key, true, $iv);
		return $data;
		//return json_decode($data, true);
	}
	/**
	* Encrypt value to a cryptojs compatiable json encoding string
	*
	* @param mixed $passphrase
	* @param mixed $value
	* @return string
	*/
	function cryptoJsAesEncrypt($passphrase, $value){
		$salt = openssl_random_pseudo_bytes(8);
		$salted = '';
		$dx = '';
		while (strlen($salted) < 48) {
			$dx = md5($dx.$passphrase.$salt, true);
			$salted .= $dx;
		}
		$key = substr($salted, 0, 32);
		$iv  = substr($salted, 32,16);
		$encrypted_data = openssl_encrypt(json_encode($value), 'aes-256-cbc', $key, true, $iv);
		$data = array("ct" => base64_encode($encrypted_data), "iv" => bin2hex($iv), "s" => bin2hex($salt));
		return json_encode($data);
	}
	// ENd Crypto JS
};
?>