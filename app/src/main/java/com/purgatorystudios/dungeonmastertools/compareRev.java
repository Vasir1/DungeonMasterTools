package com.purgatorystudios.dungeonmastertools;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import org.w3c.dom.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class compareRev extends AsyncTask<Void, Void, Boolean> {

    private DropboxAPI<?> dropbox;
    private String path;
    private String filemame;

    private Context context;

    private String characterName;
    Element name, alignment, city, faction, notes;
    viewCharacter passed_viewcharacter;
    newCharacter passed_newcharacter;
    public String rev;

    public compareRev(Context context,newCharacter _newCharacter, DropboxAPI<?> dropbox,
                             String path, String _filename,String _rev) {
        this.context = context.getApplicationContext();
        this.dropbox = dropbox;
        this.path = path;
        this.filemame=_filename;
        this.passed_newcharacter=_newCharacter;
        this.rev=_rev;
        Log.w("test", "Rev check: to download: " + path + " - " + _filename);
        Log.w("test", "Rev check: to check against: " + rev);
    }
    @Override
    protected Boolean doInBackground(Void... params) {
        final File tempDir = context.getCacheDir();
        File tempFile;
        FileWriter fr;
        try {
            File file = File.createTempFile("file", ".txt", tempDir);
            FileOutputStream outputStream = new FileOutputStream(file);
            DropboxAPI.DropboxFileInfo info = dropbox.getFile(path+filemame, null, outputStream, null);
            Log.i("test", "The file's rev is: " + info.getMetadata().rev);
            if (rev.equals(info.getMetadata().rev)){
                Log.i("test","rev matches");
                //match
                return true;

            }
            return false;
            //don't match



        } catch (IOException e) {
            e.printStackTrace();
        } catch (DropboxException e) {
            e.printStackTrace();
        }

        return false;
    }
    protected void onPostExecute(Boolean result) {
        if (result) {
            passed_newcharacter.prepareXML();

        }
        else{
            Toast.makeText(context, "File changed since downloaded", Toast.LENGTH_LONG)
                    .show();
        }

    }
}
