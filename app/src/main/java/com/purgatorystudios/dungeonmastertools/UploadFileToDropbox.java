package com.purgatorystudios.dungeonmastertools;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class UploadFileToDropbox extends AsyncTask<Void, Void, Boolean> {

    private DropboxAPI<?> dropbox;
    private String path;
    private Context context;
    private String file;
    private String characterName;
    public  String oldRev="";
    public  boolean overwrite;

    public UploadFileToDropbox(Context context, DropboxAPI<?> dropbox,
                               String path) {
        this.context = context.getApplicationContext();
        this.dropbox = dropbox;
        this.path = path;
    }
    public UploadFileToDropbox(Context context, DropboxAPI<?> dropbox,
                               String path, String _file, String _name, boolean _overwrite) {
        //_name is ulpload name and includes (or should include) the extension.
        this.context = context.getApplicationContext();
        this.dropbox = dropbox;
        this.path = path;
        this.file= _file;
        this.characterName=_name;
        this.overwrite=_overwrite;
        Log.w("test"," data I should be writing: "+_file);
        Log.i("test","file name?: "+_file);
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        final File tempDir = context.getCacheDir();
        File tempFile;
        FileWriter fr;
        try {
            tempFile = File.createTempFile("file", ".txt", tempDir);
            fr = new FileWriter(tempFile);
            fr.write(file);
            //fr.write("Sample text file created for demo purpose. You may use some other file format for your app ");
            fr.close();

            FileInputStream fileInputStream = new FileInputStream(tempFile);
            if (overwrite==true) {
                dropbox.putFileOverwrite(path + characterName, fileInputStream,
                        tempFile.length(), null);
            }
            else{
                dropbox.putFile(path + characterName, fileInputStream,
                        tempFile.length(), null, null);
            }
            //dropbox.putFile(path + characterName+".xml", fileInputStream,
           // tempFile.length(), null, null);

            tempFile.delete();
            if (tempFile.exists()){
                Log.w("testE","Error: temporary file still exists!");
            }
            else{
                Log.w("testE","Error: temporary file cleared!");
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DropboxException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Toast.makeText(context, "File Uploaded Sucesfully!",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Failed to upload file", Toast.LENGTH_LONG)
                    .show();
        }
    }
}
