package com.purgatorystudios.dungeonmastertools;

import org.w3c.dom.Element;
import android.content.Context;
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

public class downloadFile extends AsyncTask<Void, Void, Boolean> {

    private DropboxAPI<?> dropbox;
    private String path;
    private String filemame;

    private Context context;

    private String characterName;

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
                Element cElement =  (Element) eElement.getElementsByTagName("alignment").item(0);
                Element name =  (Element) eElement.getElementsByTagName("name").item(0);
               // Element cElement =  (Element) eElement.getElementsByTagName("alignment").item(0);
                //Log.w("test","Manager ID : " + cElement.getAttribute("person"));
               // Log.w("test","Manager ID : " + eElement.getNodeValue()); //null
                //Log.w("test","Manager ID : " + eElement.toString()); //useless
                Log.w("test","Manager ID : " + eElement.getTextContent()); // Chaotic Good
               // Log.w("test","Manager ID : " + eElement.getNodeName()); // aignment
                Log.w("test","Child count?: " + eElement.hasChildNodes());
                //Log.w("test","Child count?: " + eElement.getChildNodes().item(0).getNodeName());
                Log.w("test","name should be: "+name.getTextContent());
                Log.w("test","alignment should be: "+cElement.getTextContent());
                //name.setTextContent(name.getTextContent());
                characterName=name.getTextContent();

                Log.w("test", "name should be: " + name.getTextContent());

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
                    xmlSerializer.startTag("", "name");
                   // xmlSerializer.text("\n");
                    //xmlSerializer.attribute("", "name", characterName);
                    xmlSerializer.text(characterName);
                    //xmlSerializer.text("\n");

                    xmlSerializer.endTag("", "name");
                    xmlSerializer.text("\n");
                    xmlSerializer.startTag("", "alignment");
                    //xmlSerializer.text("\n");
                    xmlSerializer.text("Chaotic Evil");
                   // xmlSerializer.text("\n");
                   // xmlSerializer.attribute("", "alignment", "Chaotic Evil");

                    xmlSerializer.endTag("", "alignment");
                    xmlSerializer.text("\n");

                    xmlSerializer.endTag("", "character");
                    xmlSerializer.endDocument();

                    final File tempDir = context.getCacheDir();
                    //FileOutputStream myFile=openFileOutput(f);
                    writer.toString();
                    File file = File.createTempFile("file", ".txt", tempDir);
                    //file.write(writer.toString().getBytes());
                   // file=writer.toString();
                   // file
                    /*
                    UploadFileToDropbox upload = new UploadFileToDropbox(context, dropbox,
                            "/DropboxSample/", writer.toString(), characterName);
                    upload.execute();*/

                }
                catch (FileNotFoundException e) {
                    System.err.println("FileNotFoundException: " + e.getMessage());
                   // throw new SAXException(e);

                } catch (IOException e) {
                    System.err.println("Caught IOException: " + e.getMessage());
                }



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
