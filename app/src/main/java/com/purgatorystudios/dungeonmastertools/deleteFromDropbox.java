package com.purgatorystudios.dungeonmastertools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

public class deleteFromDropbox extends AsyncTask<Void, Void, Boolean> {
    private DropboxAPI<?> dropbox;
    public String path;
    public Context context;
    public  DropboxActivity dropboxActivity;
    public String file;

    public deleteFromDropbox(Context context, DropboxAPI<?> dropbox,
                      String path, String _file) {
        this.dropbox=dropbox;
        this.path=path;
        this.context=context;
        this.dropboxActivity=(DropboxActivity) context;
        this.dropboxActivity=dropboxActivity;
        this.file=_file;

    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            Log.i("test", "file deleted");

            dropbox.delete(path+file);

            Log.i("test", "trying to refresh");

            dropboxActivity.refresh(path);



            // ListDropboxFiles list = new ListDropboxFiles(dropbox, FILE_DIR,
            //   handler);
            //list.execute();

            return true;
        }

        catch (DropboxException e) {
            e.printStackTrace();
        }
        return  false;

    }

    protected void onPostExecute(Boolean result) {
        if (result) {



        }
    }
}
