package com.purgatorystudios.dungeonmastertools;

import org.w3c.dom.Element;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class downloadCharacter extends AsyncTask<Void, Void, Boolean> {

    private DropboxAPI<?> dropbox;
    private String path;
    private String filemame;

    private Context context;

    private String characterName;
    Element name, alignment, city, faction, notes;
    viewCharacter passed_viewcharacter;
    public String rev;

    public downloadCharacter(Context context,viewCharacter _viewCharacter, DropboxAPI<?> dropbox,
                        String path, String _filename) {
        this.context = context.getApplicationContext();
        this.dropbox = dropbox;
        this.path = path;
        this.filemame=_filename;
        this.passed_viewcharacter=_viewCharacter;
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
            rev=info.getMetadata().rev;

            String received=read(file);
            Log.w("test",received);
            readXML(file);

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
            passed_viewcharacter.initialize(name.getTextContent(),alignment.getTextContent(),city.getTextContent(),faction.getTextContent(),
                    notes.getTextContent(),rev);
            Toast.makeText(context, "File downloaded Sucesfully!",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Failed to download file", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void readXML(File fname)
    {
        Log.w("test","Beginning to read XML");
        Document doc = null;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder;
        try
        {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fname);
            doc.getDocumentElement().normalize();
            // DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //DocumentBuilder db = dbf.newDocumentBuilder();
            //doc = db.parse(fname);

            //doc.getDocumentElement().normalize();
            //doc = parseXML(fname);
            // doc = parseXML("/home/abc/Test.xml");
        }
        catch (ParserConfigurationException e)
        {
            //;
            Log.w("test",e.toString());
        }
        catch (SAXException e)
        {
            //e.printStackTrace();
            Log.w("test","error: "+e.toString());
        }
        catch (IOException e)
        {
            Log.w("test",e.toString());
            // e.printStackTrace();
        }

        if(doc != null)
        {
            NodeList nList = doc.getElementsByTagName("character");
            //NodeList nList = doc.getElementsByTagName("character");
            // Node name= doc.getElementById("name");


            for (int i = 0; i < nList.getLength(); i++)
            {
                Node nNode = nList.item(i);
                Element eElement = (Element) nNode;
                Element cElement =  (Element) eElement.getElementsByTagName("Alignment").item(0);
                 name =  (Element) eElement.getElementsByTagName("Name").item(0);
                alignment =  (Element) eElement.getElementsByTagName("Alignment").item(0);
                 city =  (Element) eElement.getElementsByTagName("City").item(0);
                 faction =  (Element) eElement.getElementsByTagName("Faction").item(0);
                 notes =  (Element) eElement.getElementsByTagName("Notes").item(0);

               // characterName=name.getTextContent();



                }




            }

    }

    private Document parseXML(String filePath) throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(filePath);
        doc.getDocumentElement().normalize();
        return doc;
    }
}
