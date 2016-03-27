package com.purgatorystudios.dungeonmastertools;

import java.util.ArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;

public class ListDropboxFiles extends AsyncTask<Void, Void, ArrayList<String>> {

    private DropboxAPI<?> dropbox;
    private String path;
    private Handler handler;
    private ArrayList<String> directories;


    public ListDropboxFiles(DropboxAPI<?> dropbox, String path, Handler handler) {
        this.dropbox = dropbox;
        this.path = path;
        this.handler = handler;
        //try {
            Log.w("test", "path(ListDropboxFiles): " + path);
        //}

    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        ArrayList<String> files = new ArrayList<String>();
        directories= new ArrayList<String>();
        try {
            Entry directory = dropbox.metadata(path, 1000, null, true, null);
            for (Entry entry : directory.contents) {
                if (entry.isDir){
                    directories.add(entry.fileName());
                    //Log.w("test", entry.fileName()+" is a directory!");

                }
                else {
                    files.add(entry.fileName());
                }
            }
        } catch (DropboxException e) {
            e.printStackTrace();
        }

        return files;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        Log.w("test", "listDropBoxFile result size: "+result.size());
        Message msgObj = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putStringArrayList("data", result);
        b.putStringArrayList("directories", directories);
        msgObj.setData(b);
        handler.sendMessage(msgObj);

    }
}