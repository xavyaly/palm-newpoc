package com.vritaventures.palmscanner;

import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.StandardCopyOption;

public class ExtractModels {
    ExampleActivity activity;
    ExtractModels(ExampleActivity _activity){
    activity=_activity;
    }
    public void extract(){
        String location="/sdcard/palm";
        try{
            File f=new File(location);
            if(!f.exists())
                if(!f.mkdir()) {
                    Toast.makeText(activity, "Folder /sdcard/palm not found", Toast.LENGTH_SHORT).show();
                    return;
                }

        } catch (Exception e) {
            Toast.makeText(activity, ""+e.toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        String files[]={
                "dim_detection_ir_pipeline_config.pbtxt",
                "dim_detection_pipeline_config.pbtxt",
                "dim_extract_features_ir_pipeline_config.pbtxt",
                "dim_extract_features_pipeline_config.pbtxt",
                "dim_extract_features_with_palm_status_ir_pipeline_config.pbtxt",
                "dim_extract_features_with_palm_status_pipeline_config.pbtxt",
                "dim_operators_config.pbtxt",
                "dim_operators_ir_config.pbtxt",
                "dim_recognition_ir_pipeline_config.pbtxt",
                "dim_recognition_pipeline_config.pbtxt",
                "dim_register_ir_pipeline_config.pbtxt",
                "dim_register_pipeline_config.pbtxt",
                "dim_status_ir_pipeline_config.pbtxt",
                "dim_status_pipeline_config.pbtxt",
                "palm_models_1.2.6.bin",
                "palm_models_config.pbtxt",
                "palm_models_ir_config.pbtxt"
        };
            int c=0;
            for (String inFile:files) {
                try {

                    InputStream fi = activity.getAssets().open("models/"+inFile);
                    Files.copy(fi,new File(location+"/"+inFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    c++;
                }catch(Exception ee){
                    Toast.makeText(activity,"Sorry Try Again::["+c+"]"+ee,Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        Toast.makeText(activity,"Ok:"+c,Toast.LENGTH_SHORT).show();
    }
}
