package com.purgatorystudios.dungeonmastertools;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;

public class viewCharacter extends AppCompatActivity {

    public TextView alignment,city, faction, notes;
    public TextView name;
    private final static String DROPBOX_NAME = "dropbox_prefs";
    private final static String ACCESS_KEY = "097abv0lh6d3wig";
    private final static String ACCESS_SECRET = "jm8lcpz61ktmodd";
    private DropboxAPI<AndroidAuthSession> dropbox;
    public static String FILE_DIR = "/DropboxSample/";



    public void Authorize(){
        Log.w("test","trying to authorize");


        AndroidAuthSession session;
        AppKeyPair pair = new AppKeyPair(ACCESS_KEY, ACCESS_SECRET);

        SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
        String key = prefs.getString(ACCESS_KEY, null);
        String secret = prefs.getString(ACCESS_SECRET, null);

        if (key != null && secret != null) {
            AccessTokenPair token = new AccessTokenPair(key, secret);
            session = new AndroidAuthSession(pair, Session.AccessType.DROPBOX, token);

        } else {
            session = new AndroidAuthSession(pair, Session.AccessType.DROPBOX); //appFolder
        }
        dropbox = new DropboxAPI<AndroidAuthSession>(session);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_character);

        name = (TextView)findViewById(R.id.txtName);
        alignment = (TextView)findViewById(R.id.txtAlignment);
        city = (TextView)findViewById(R.id.txtCity);
        faction = (TextView)findViewById(R.id.txtFaction);
        notes = (TextView)findViewById(R.id.txtNotes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FILE_DIR=getIntent().getStringExtra("dir");
        Authorize();


        String tempName=this.getIntent().getStringExtra("name");
        setTitle(tempName);

        downloadCharacter download = new downloadCharacter(this,viewCharacter.this, dropbox,
                FILE_DIR, tempName);
        download.execute();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_character_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
        } else if (id == R.id.btnEdit) {
           // prepareXML();
            Intent intent = new Intent(viewCharacter.this,newCharacter.class);
            intent.putExtra("isEditing", true);
            intent.putExtra("fileName", getTitle().toString());
            intent.putExtra("dir",FILE_DIR);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    public void prepareXML(){
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            xmlSerializer.setOutput(writer);
            //start Document
            xmlSerializer.startDocument("UTF-8", true);
            //open tag <items>
            xmlSerializer.text("\n");
            xmlSerializer.startTag("", "character");
            xmlSerializer.text("\n");
            xmlSerializer.startTag("", "Name");
            xmlSerializer.text(name.getText().toString());
            xmlSerializer.endTag("", "Name");
            xmlSerializer.text("\n");
            xmlSerializer.startTag("", "Alignment");
            xmlSerializer.text(alignment.getText().toString());
            xmlSerializer.endTag("", "Alignment");
            xmlSerializer.text("\n");
            xmlSerializer.startTag("", "City");
            xmlSerializer.text(city.getText().toString());
            xmlSerializer.endTag("", "City");
            xmlSerializer.text("\n");
            xmlSerializer.startTag("", "Faction");
            xmlSerializer.text(faction.getText().toString());
            xmlSerializer.endTag("", "Faction");
            xmlSerializer.text("\n");
            xmlSerializer.startTag("", "Notes");
            xmlSerializer.text(notes.getText().toString());
            xmlSerializer.endTag("", "Notes");
            xmlSerializer.text("\n");

            xmlSerializer.endTag("", "character");
            xmlSerializer.endDocument();

            UploadFileToDropbox upload = new UploadFileToDropbox(this, dropbox,
                    "/DropboxSample/", writer.toString(), name.getText().toString());
            upload.execute();

        }
        catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException: " + e.getMessage());
            // throw new SAXException(e);

        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }


    }*/

    public void initialize(String _name, String _alignment, String _city, String _faction, String _notes, String _rev){
        Log.w("test", "name: " + _name + " alignment: " + _alignment);
        name.setText(_name);
        alignment.setText(_alignment);
        city.setText(_city);
        faction.setText(_faction);
        notes.setText(_notes);

    }
    @Override
    protected void onResume() {
        super.onResume();
        downloadCharacter download = new downloadCharacter(this,viewCharacter.this, dropbox,
                FILE_DIR, getTitle().toString());
        download.execute();

    }

}
