package com.purgatorystudios.dungeonmastertools;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;


public class DropboxActivity extends Activity implements OnClickListener {
    private DropboxAPI<AndroidAuthSession> dropbox;
    public final static String FILE_DIR = "/DropboxSample/";
    public String SECONDARY_DIR="";
    private final static String DROPBOX_NAME = "dropbox_prefs";
    private final static String ACCESS_KEY = "097abv0lh6d3wig";
    private final static String ACCESS_SECRET = "jm8lcpz61ktmodd";
    private boolean isLoggedIn;
    private Button logIn;
    private Button uploadFile;
    private Button listFiles;
    private LinearLayout container;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.content_dropboxactivity);
        setContentView(R.layout.activity_dropbox);

        logIn = (Button) findViewById(R.id.dropbox_login);
        logIn.setOnClickListener(this);
        uploadFile = (Button) findViewById(R.id.upload_file);
        uploadFile.setOnClickListener(this);
        listFiles = (Button) findViewById(R.id.list_files);
        listFiles.setOnClickListener(this);
        container = (LinearLayout) findViewById(R.id.container_files);

        loggedIn(false);
        AndroidAuthSession session;
        AppKeyPair pair = new AppKeyPair(ACCESS_KEY, ACCESS_SECRET);

        SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
        String key = prefs.getString(ACCESS_KEY, null);
        String secret = prefs.getString(ACCESS_SECRET, null);

        if (key != null && secret != null) {
            AccessTokenPair token = new AccessTokenPair(key, secret);
            session = new AndroidAuthSession(pair, AccessType.DROPBOX, token);
            loggedIn(true); // Setting to logged in so you don't have to authorize again

        } else {
            session = new AndroidAuthSession(pair, AccessType.DROPBOX); //appFolder
        }
        dropbox = new DropboxAPI<AndroidAuthSession>(session);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(DropboxActivity.this,newCharacter.class);
                //intent.putExtras("intent",DropboxActivity.this);
                //intent.putExtras(this);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);


                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                      //  .setAction("Action", null).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        AndroidAuthSession session = dropbox.getSession();
        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();
                TokenPair tokens = session.getAccessTokenPair();
                SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
                Editor editor = prefs.edit();
                String accessToken = session.getOAuth2AccessToken();
                editor.putString(ACCESS_KEY, tokens.key);
                editor.putString(ACCESS_SECRET, tokens.secret);
                editor.commit();
                loggedIn(true);
            } catch (IllegalStateException e) {
                Toast.makeText(this, "Error during Dropbox authentication",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void loggedIn(boolean isLogged) {
        isLoggedIn = isLogged;
        uploadFile.setEnabled(isLogged);
        listFiles.setEnabled(isLogged);
        logIn.setText(isLogged ? "Log out" : "Log in");
    }

    private final Handler handler = new Handler() { //, Message dir
        public void handleMessage(Message msg) { // // TODO: 3/26/2016  make this static and highlight directories
            ArrayList<String> result = msg.getData().getStringArrayList("data");
            ArrayList<String> directories = msg.getData().getStringArrayList("directories");
            Log.w("test","directory count: "+directories.size());
            //
            /*
            I sent a message with two arrays, one holds directories which float to the top
            If its a directory I add a tag with 'dir'.
            But first I remove all views.
             */
            container.removeAllViews();
            if (SECONDARY_DIR!=null){ // Up a folder
                Button backButton=new Button(DropboxActivity.this);
                backButton.setText("..");
                backButton.setId(View.generateViewId());
                backButton.setTag("Back button");
                backButton.setOnClickListener(DropboxActivity.this);
                backButton.setBackgroundColor(Color.BLUE);
                container.addView(backButton);

            }
            //list directories
            for (String fileName : directories) {

                Log.i("ListFiles", fileName);
                Button btnFile=new Button(DropboxActivity.this);
                btnFile.setText(fileName);
                btnFile.setId(View.generateViewId());
                btnFile.setTag(fileName+" dir");
                if (btnFile.getTag().toString().contains("dir")){

                }
                btnFile.setOnClickListener(DropboxActivity.this);
                btnFile.setBackgroundColor(Color.BLUE);

                container.addView(btnFile);
            }
            //go through files
            for (String fileName : result) {

                Log.i("ListFiles", fileName);
                Button btnFile=new Button(DropboxActivity.this);
                btnFile.setText(fileName);
                btnFile.setId(View.generateViewId());
                btnFile.setTag(fileName);
                btnFile.setOnClickListener(DropboxActivity.this);
                //btnFile.setOnClickListener(getOnClickOpenFile(btnFile));
                container.addView(btnFile);
            }
        }
    };


    @Override
    public void onClick(View v) {
        String tag = String.valueOf(v.getTag());
        Log.w("test", "clicked: "+tag);
        //Only the hardcoded buttons have no tag!
        if (tag.equals("null")){
        switch (v.getId()) {
            case R.id.dropbox_login:
                if (isLoggedIn) {
                    dropbox.getSession().unlink();
                    loggedIn(false);
                } else {
                    dropbox.getSession().startAuthentication(DropboxActivity.this);
                }

                break;
            case R.id.list_files:
                ListDropboxFiles list = new ListDropboxFiles(dropbox, FILE_DIR,
                        handler);
                list.execute();
                break;
            case R.id.upload_file:
                UploadFileToDropbox upload = new UploadFileToDropbox(this, dropbox,
                        FILE_DIR);
                upload.execute();
                break;
            default:
                break;
        }
        }
        else{
            //If user clicked something with a tag

            //if user clicked a directory
            if (v.getTag().toString().contains("dir")){


                Button vtemp= (Button)v;
                Log.w("test", "user clicked a directory! things are getting real now " + ((Button) v).getText());
                String sTemp=((Button) v).getText().toString()+"/";


                //Building a second directory string to maintain a root.
                Log.w("test", " SECONDARY_DIR : " + SECONDARY_DIR);
                SECONDARY_DIR=SECONDARY_DIR+sTemp;
                Log.w("test"," SECONDARY_DIR : "+SECONDARY_DIR);
                ListDropboxFiles list = new ListDropboxFiles(dropbox, FILE_DIR+SECONDARY_DIR,
                        handler);
                list.execute();

            }
             else if (v.getTag().equals("Back button")){
                String[] paths = SECONDARY_DIR.split("/");
                StringBuilder s=new StringBuilder();
                Log.w("test"," paths size: "+paths.length);
                Log.w("test"," SECONDARY_DIR : "+SECONDARY_DIR);
                if (paths.length==1){
                    SECONDARY_DIR="";

                }
                else {
                    for (int i = 0; i < paths.length - 1; i++) {
                        s.append(paths[i]+"/");


                    }
                    SECONDARY_DIR=s.toString();
                    Log.w("test"," Directory above : "+s.toString());

                }
                //TODO turn this into a function
                String dir;
                if (SECONDARY_DIR!=""){

                    dir=FILE_DIR+SECONDARY_DIR;

                }
                else{
                    Log.w("test","Should be at root?");
                    dir=FILE_DIR;
                }
                ListDropboxFiles list = new ListDropboxFiles(dropbox, dir,
                        handler);
                list.execute();

            }
            else {
                //If user clicked a file...
                String dir;
                Log.w("test","secondary: "+SECONDARY_DIR);
                //!SECONDARY_DIR.equals("null") ||
                if (SECONDARY_DIR!=null){

                    dir=FILE_DIR+SECONDARY_DIR;

                }
                else{
                    dir=FILE_DIR;
                }
                /*
                downloadFile download = new downloadFile(this, dropbox,
                        dir, tag);
                download.execute();*/
                Intent intent = new Intent(DropboxActivity.this,viewCharacter.class);
                intent.putExtra("name",tag);
                startActivity(intent);
            }
        }
    }
}