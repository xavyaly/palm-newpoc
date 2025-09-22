package com.vritaventures.palmscanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Debug;
import android.provider.Settings;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class JConfig {
    protected static boolean isLogined=false;
    public static String DEPARTMENT_ID="LIGHT";
    public static Context context=null;
    public static Activity topActivity=null;

    public static String SESSION_NAME="__wa5788cbshdg3";
    public static String SESSION_ID="";
    public static String API_SERVER_HOST="https://example.com";
    public static String PALM_API_SERVER_IP="example.com";
    public static String PALM_API_SERVER_DOMAIN="example.com";
    public static String PALM_API_PROTOCOL="http";
    public static String PALM_API_PORT="80";
}

class JTools {

    public static void openNavigationMap(String lat,String lng){
        try {
            // Create a Uri from an intent string. Use the result to create an Intent.
            //Uri gmmIntentUri = Uri.parse("geo:23.341324909246367, 85.32593260168082?q=restaurants");
            //Uri gmmIntentUri = Uri.parse("google.streetview:cbll=23.341324909246367, 85.32593260168082");
            //Uri gmmIntentUri = Uri.parse("geo:23.341324909246367, 85.32593260168082");
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lng);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            JConfig.context.startActivity(mapIntent);
        /*if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }*/
        }catch(Exception ee){}

    }
    public static String toHexString(byte[] ba) {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < ba.length; i++)
            str.append(String.format("%x", ba[i]));
        return str.toString();
    }
    public static boolean isDebugEnabled() {
       /* try {
            boolean rootedFlag=ISRooted.isRooted();
            if(rootedFlag==true) {
                Toast.makeText(JConfig.context, "Your Phone is Rooted!", Toast.LENGTH_SHORT).show();
                return(true);
            }
            if(ISRooted.isInDebugState()==true){
                return(true);
            }

        } catch (Exception ee) {
        }*/
        return false;
    }

};

class ISRooted {
    public static boolean isRooted(){
        return(checkCustomOS() || checkFileExist()||checkRootWhichSU());
    }
    public static boolean checkCustomOS(){
        try {
            String buildTags = android.os.Build.TAGS;
            if (buildTags != null && buildTags.contains("test-keys")) {
                return true;
            }
        }catch (Exception ee) {}
        return false;
    }
    private static boolean checkFileExist() {
        try {
            String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                    "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
            for (String path : paths) {
                if (new File(path).exists()) {
                    System.out.println("ROOTED ACTION files:" +path);
                    return true;
                }
            }
        }catch (Exception ee){}
        return false;
    }
    private static boolean checkRootWhichSU() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/bin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line=null;
            if ((line=in.readLine()) != null) {System.out.println("ROOTED ACTION Which su:" +line);return true;}
            return false;
        } catch (Exception t) {
           try{ if (process != null) process.destroy(); } catch (Exception tx) {}
        }
        return(false);
    }

    // debugging
    public static boolean isInDebugState(){
        try {
            if (Settings.Secure.getInt(JConfig.context.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) == 1) {
                Toast.makeText(JConfig.context, "Your Phone is on debuge mode!", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (Settings.Secure.getInt(JConfig.context.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1) {
                Toast.makeText(JConfig.context, "Your Phone is on debuge mode!", Toast.LENGTH_SHORT).show();
                return true;
            }
            if(Debug.isDebuggerConnected()){
                Toast.makeText(JConfig.context, "Debugger is connected!", Toast.LENGTH_SHORT).show();
                return true;
            }
        }catch (Exception ee) {
         }
        return false;
    }


}