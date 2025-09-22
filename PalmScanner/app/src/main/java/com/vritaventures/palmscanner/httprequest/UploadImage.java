package com.vritaventures.palmscanner.httprequest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.vritaventures.palmscanner.ExampleActivity;
import com.vritaventures.palmscanner.JConfig;

import javax.net.ssl.HttpsURLConnection;

public class UploadImage {
    URL url=null;
    HttpURLConnection  connection=null;
    String boundary=null;
    String lineEnd="\r\n";
    String twoHyphens="--";
    String filefield = "file";
    String host=null;
    String location=null;
    File file=null;
    String fileName=null;
    String uploadedFileURL=null;
    String photoId="";
    float MAX_IMAGE_SIZE=1200;
    ExampleActivity exampleActivity;
    public UploadImage(String _host, String _location, File _file,String _photoId){
        try{
            this.host=_host;
            this.location=_location;
            this.file=_file;
            this.fileName=_file.getName();
            this.photoId=_photoId;

            this.boundary="________"+Long.toString(System.currentTimeMillis())+"_______";
            this.url=new URL(_host+""+_location);
        }catch(Exception ee){
            System.out.println("E0097:"+ee.toString());
        }
    }
    public void call(ExampleActivity exampleActivity){
        this.exampleActivity=exampleActivity;
        PostImageToImaggaAsync postImageToImaggaAsync = new PostImageToImaggaAsync(this);
        postImageToImaggaAsync.execute();

    }
    public JSONObject start(){
        JSONObject jsonResponse=new JSONObject();
        try{
            this.connection=(HttpURLConnection) url.openConnection();
            this.connection.setDoInput(true);
            this.connection.setDoOutput(true);
            this.connection.setUseCaches(false);
            this.connection.setRequestMethod("POST");
            this.connection.setRequestProperty("Connection", "Keep-Alive");
            this.connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            this.connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
            this.connection.setRequestProperty("Cookie", JConfig.SESSION_NAME+"="+ JConfig.SESSION_ID);
            this.connection.setRequestProperty("assetId", this.photoId);
            //this.connection.setRequestProperty("Authorization", "<insert your own Authorization e.g. Basic YWNjX>");

            System.out.println("Start Uploading");
            int bytesRead=0, bytesAvailable=0, bufferSize=0;
            byte[] buffer;
            int maxBufferSize = 1*1024*1024;
            InputStream inputStream = null;
            FileInputStream fileInputStream = new FileInputStream(file);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + fileName +"\"" + lineEnd);
            outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
            outputStream.writeBytes(lineEnd);
            //Bitmap resizedBitmap=BitmapFactory.decodeFile(file.getAbsolutePath());// JCxSAJDAS.resizeImage(file,MAX_IMAGE_SIZE,false);
            //Bitmap resizedBitmap= BitmapFactory.decodeStream(new FileInputStream(file));
            //resizedBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while(bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();
            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                inputStream.close();
                connection.disconnect();
                fileInputStream.close();
                outputStream.flush();
                outputStream.close();
                jsonResponse.put("status",1);
                jsonResponse.put("data",response.toString());
            } else {
                jsonResponse.put("status",0);
                jsonResponse.put("data","no response");
            }
            jsonResponse.put("code",status);
            System.out.println("Uploading Complteted");
            return(jsonResponse);

        }catch(Exception ee){
            System.out.println("E0098:"+ee.toString());
            try {
                jsonResponse.put("status", -1);
                jsonResponse.put("data", "noXX response ::"+ee);
                return(jsonResponse);
            }catch (Exception eei){
                return(null);
            }
        }
    }
    // Upload Class
    // Upload IMage Non UI Thread
    class PostImageToImaggaAsync extends AsyncTask<Void, JSONObject, JSONObject> {
        UploadImage uploadImage=null;
        PostImageToImaggaAsync(UploadImage _uploadImage){
            this.uploadImage=_uploadImage;
        }
        @Override
        protected void onPreExecute() {
            try{
            }catch (Exception ee){}
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                return(start());

            } catch (Exception e) {
                try {
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("status", -1);
                    jsonResponse.put("data", "no start::"+e);
                    return (jsonResponse);
                }catch (Exception er){}
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            String payment_msg="";
            try{
                uploadImage.uploadedFileURL=null;
                if(result!=null){
                    if(result.getInt("status")==1){
                        JSONObject _obj=new JSONObject(result.getString("data"));
                        if(_obj.getInt("status")==1) {
                            Toast.makeText(JConfig.context, _obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            uploadImage.exampleActivity.text_palm_errro.setText(_obj.getString("msg"));
                            if(uploadImage.exampleActivity.JSACN_MODE==0){
                                payment_msg="Paid amount: $ "+uploadImage.exampleActivity.__AMOUNT__;

                            }
                        }
                        else if(_obj.getInt("status")==0) {
                            Toast.makeText(JConfig.context, _obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            uploadImage.exampleActivity.text_palm_errro.setText(_obj.getString("msg"));
                            payment_msg=_obj.getString("msg");
                        }
                    }
                    else if(result.getInt("status")==0){
                        Toast.makeText(JConfig.context,"Server Connection Fail",Toast.LENGTH_SHORT).show();
                        uploadImage.exampleActivity.text_palm_errro.setText("Server Connection Fail");
                        payment_msg="Server COnnection Fail";
                    }
                    else if(result.getInt("status")==-1){
                        Toast.makeText(JConfig.context,result.getString("data"),Toast.LENGTH_SHORT).show();
                        uploadImage.exampleActivity.text_palm_errro.setText(result.getString("data"));
                        payment_msg=result.getString("data");
                    }
                }else{Toast.makeText(JConfig.context,"No Response",Toast.LENGTH_SHORT).show();
                    uploadImage.exampleActivity.text_palm_errro.setText("No Response");
                    payment_msg="No Response";
                }
                if(uploadImage.exampleActivity.JSACN_MODE==0){
                    uploadImage.exampleActivity.txt_payment_result.setText(payment_msg);
                }
            }catch (Exception ee){

            }

        }
    }
    // Impload Image Non UI Thread
    // ENd Upload Class
}
