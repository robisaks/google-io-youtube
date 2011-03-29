package com.rei.youtube;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ChannelInfo extends Activity {
	ListView list;
    LazyAdapter adapter;
    ArrayList<Video> vids = new ArrayList<Video>();
    String query ="";
    Button btnSearch;
    TextView percent;
    int uncaptioned = 0;
    int captioned = 0;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.channel);
        
        percent = (TextView) findViewById(R.id.percent);
        
        btnSearch = (Button) findViewById(R.id.go);
        
		btnSearch.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				query = btnSearch.getText().toString();
				updateData();
			}
		});

    }    
    private void updateData(){
    	setProgressBarIndeterminateVisibility(true);
    	new Thread(new Runnable() {
    	    public void run() {
    	    	
    	    	URL url;
    	    	uncaptioned = 0;
	            captioned = 0;
    	    	try {
    	            String featuredFeed = "http://gdata.youtube.com/feeds/api/videos?q="+
    	            	query + "&caption=true&format=1&v=2";
    	            
    	            url = new URL(featuredFeed);
    	            
    	            URLConnection connection;
    	            connection = url.openConnection();

    	            HttpURLConnection httpConnection = (HttpURLConnection)connection; 
    	            
    	            int responseCode = httpConnection.getResponseCode(); 
    	            if (responseCode == HttpURLConnection.HTTP_OK) { 
    	            	InputStream in = httpConnection.getInputStream(); 
    	                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	                DocumentBuilder db = dbf.newDocumentBuilder();
    	                // Parse the earthquake feed.
    	                Document dom = db.parse(in);      
    	                Element docEle = dom.getDocumentElement();
    	                // Get a list of each earthquake entry.
    	                NodeList nl = docEle.getElementsByTagName("entry");
    	                if (nl != null && nl.getLength() > 0) {
    	                  captioned = nl.getLength();
    	                	
    	                }
    	                /*
    	                 * TODO: this
    	                 * 
    	                Element nl = (Element) docEle.getElementsByTagName("openSearch:totalResults").item(0);
    	                String cap = nl.getNodeValue();
    	                captioned = Integer.parseInt(cap.trim());*/
    	            }
    	            
    	            featuredFeed = "http://gdata.youtube.com/feeds/api/videos?q="+
	            	query + "&format=1&v=2";
	            
		            url = new URL(featuredFeed);
		            
		            connection = url.openConnection();
	
		            httpConnection = (HttpURLConnection)connection; 
	
		            
		            responseCode = httpConnection.getResponseCode(); 
		            if (responseCode == HttpURLConnection.HTTP_OK) { 
		            	InputStream in = httpConnection.getInputStream(); 
		                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		                DocumentBuilder db = dbf.newDocumentBuilder();
		                // Parse the earthquake feed.
		                Document dom = db.parse(in);      
		                Element docEle = dom.getDocumentElement();
		                // Get a list of each earthquake entry.
		                NodeList nl = docEle.getElementsByTagName("entry");
		                if (nl != null && nl.getLength() > 0) {
		                  uncaptioned = nl.getLength();
		                	
		                }
		            }
    	          } catch (MalformedURLException e) {
    	            e.printStackTrace();
    	          } catch (IOException e) {
    	            e.printStackTrace();
    	          } catch (ParserConfigurationException e) {
    	            e.printStackTrace();
    	          } catch (SAXException e) {
    	            e.printStackTrace();
    	          }
    	          finally {
    	          }

    	    	
    	    	
    	  btnSearch.post(new Runnable() {
    	        public void run() {
    	        	percent.setText(captioned+" Captioned / "+uncaptioned+" Uncaptioned...");
    	        	setProgressBarIndeterminateVisibility(false);
    	        }
    	      });
    	    }
    	  }).start();
    }
}