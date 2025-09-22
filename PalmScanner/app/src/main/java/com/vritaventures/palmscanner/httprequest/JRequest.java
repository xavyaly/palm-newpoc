package com.vritaventures.palmscanner.httprequest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.json.JSONObject;
import com.vritaventures.palmscanner.JConfig;
public class JRequest {
    URL url = null;
    HttpURLConnection connection = null;
    HashMap<String, String> paramsHash = null;
    String lineEnd = "\r\n";
    String host = null;
    String location = null;

    public JRequest(String _host, String _location, HashMap<String, String> _paramsHash) {
        try {
            this.host = _host;
            this.location = _location;
            this.url = new URL(_host + "" + _location);
            this.paramsHash = _paramsHash;
        } catch (Exception ee) {
            System.out.println("E0097:" + ee.toString());
        }
    }

    // Cookie Session//
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE_VALUE_DELIMITER = ";";
    private static final String PATH = "path";
    private static final String EXPIRES = "expires";
    private static final String DATE_FORMAT = "EEE, dd-MMM-yyyy hh:mm:ss z";
    private static final String SET_COOKIE_SEPARATOR = "; ";
    private static final String COOKIE = "Cookie";

    private static final char NAME_VALUE_SEPARATOR = '=';
    private static final char DOT = '.';

    public String getSession(String sessionName) {
        try {
            String headerName = null;
            for (int i = 1; (headerName = this.connection.getHeaderFieldKey(i)) != null; i++) {
                System.out.println("SessionID=> "+headerName+":::"+this.connection.getHeaderField(i));

                if (headerName.equalsIgnoreCase("Set-Cookie")) {
                    StringTokenizer st = new StringTokenizer(this.connection.getHeaderField(i), COOKIE_VALUE_DELIMITER);
                    if (st.hasMoreTokens()) {
                        String token = st.nextToken();

                        String name = token.substring(0, token.indexOf(NAME_VALUE_SEPARATOR));
                        String value = token.substring(token.indexOf(NAME_VALUE_SEPARATOR) + 1, token.length());
                            if (name.equals(sessionName)) { // 98e2199c0a63f3ec2ec0b1203296d55b
                                System.out.println(token);
                                System.out.println("SessionID :"+value);
                                return (value);
                            }
                    }

                    while (st.hasMoreTokens()) {
                        String token = st.nextToken();
                        String name = token.substring(0, token.indexOf(NAME_VALUE_SEPARATOR));
                        String value = token.substring(token.indexOf(NAME_VALUE_SEPARATOR) + 1, token.length());
                        if (name.equals(sessionName)) {
                            System.out.println(token);
                            System.out.println("SessionID :"+value);
                            return (value);
                        }
                    }
                }

            }
        } catch (Exception ee) {
            System.out.println("SessionID :"+ee);
        }
        System.out.println("SessionID Cookie Error");
        return(null);
    }


    private String getDataString(HashMap<String, String> params) throws  Exception{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
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
            this.connection.setRequestProperty("User-Agent", "Android HTTP Client 1.0");
            this.connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            this.connection.setRequestProperty("charset", "utf-8");
            this.connection.setRequestProperty("xdevice", "mx-100");// for it is mobile
            this.connection.setRequestProperty("Cookie", JConfig.SESSION_NAME+"="+ JConfig.SESSION_ID);
            //conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            this.connection.setUseCaches(false);
            String params=getDataString(paramsHash);
            OutputStream outputStream=this.connection.getOutputStream();
            outputStream.write(params.getBytes(),0,params.length());
            int statusCode = this.connection.getResponseCode();
            String tmpSessionID=getSession(JConfig.SESSION_NAME);
            JConfig.SESSION_ID=tmpSessionID==null? JConfig.SESSION_ID:tmpSessionID;
            InputStream inputStream = this.connection.getInputStream();
            int status = this.connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();
                this.connection.disconnect();
                jsonResponse.put("status",1);
                jsonResponse.put("data",response.toString());
            } else {
                jsonResponse.put("status",0);
                jsonResponse.put("data","no response");
            }
            jsonResponse.put("code",status);
            System.out.println("JRequest Complteted:"+jsonResponse.getString("data"));
            return(jsonResponse);

        }catch(Exception ee){
            System.out.println("E0098:"+ee.toString());
            try {
                jsonResponse.put("status", -1);
                jsonResponse.put("data", "no response");
                return(jsonResponse);
            }catch (Exception eei){
                return(null);
            }
        }
    }

    // Impload Image Non UI Thread
} // UploadImage Class
