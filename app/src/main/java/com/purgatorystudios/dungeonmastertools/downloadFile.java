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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class downloadFile extends AsyncTask<Void, Void, Boolean> {

    private DropboxAPI<?> dropbox;
    private String path;
    private String filemame;

    private Context context;

    public downloadFile(Context context, DropboxAPI<?> dropbox,
                               String path, String _filename) {
        this.context = context.getApplicationContext();
        this.dropbox = dropbox;
        this.path = path;
        this.filemame=_filename;
        Log.w("test","to download: "+path+" - "+_filename);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        final File tempDir = context.getCacheDir();
        File tempFile;
        FileWriter fr;
        try {
            /*
            File file = new File("/"+this.filemame);
            FileOutputStream outputStream = new FileOutputStream(file);
            DropboxAPI.DropboxFileInfo info = dropbox.getFile("/"+filemame, null, outputStream, null);
            Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);*/

            //File file = new File("/magnum-opus.txt");
            File file = File.createTempFile("file", ".txt", tempDir);
            FileOutputStream outputStream = new FileOutputStream(file);
            DropboxAPI.DropboxFileInfo info = dropbox.getFile(path+filemame, null, outputStream, null);
            Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);

            String received=read(file);
            Log.w("test",received);

            /*
            tempFile = File.createTempFile("file", ".txt", tempDir);
            fr = new FileWriter(tempFile);
            fr.write("Sample text file created for demo purpose. You may use some other file format for your app ");
            fr.close();

            FileInputStream fileInputStream = new FileInputStream(tempFile);
            dropbox.putFile(path + "textfile.txt", fileInputStream,
                    tempFile.length(), null, null);
            tempFile.delete();*/
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DropboxException e) {
            e.printStackTrace();
        }

        return false;
    }
    public String read(File fname){

        BufferedReader br = null;
        String response = null;

        try {

            StringBuffer output = new StringBuffer();
            //String fpath = "/sdcard/"+fname+".txt";
            //String fpath=fname;

            br = new BufferedReader(new FileReader(fname));
            String line = "";
            while ((line = br.readLine()) != null) {
                output.append(line +"\n");
            }
            response = output.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }
        return response;

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
