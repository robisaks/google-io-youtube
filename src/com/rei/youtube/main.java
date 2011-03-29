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
import android.widget.ListView;

public class main extends Activity {
	ListView list;
    LazyAdapter adapter;
    ArrayList<Video> vids = new ArrayList<Video>();
    String query ="";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        
        list=(ListView)findViewById(R.id.list);
        setProgressBarIndeterminateVisibility(true);
        
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
           query = intent.getStringExtra(SearchManager.QUERY);
        }
        Video vid = new Video();
        vid.title = "Loading..."; 
        vid.author = "Please Wait";
        vid.img = "";
        vid.url = "";
        vids.add(vid);
        adapter=new LazyAdapter(main.this, vids);
    	list.setAdapter(adapter);
        updateList();  
        list.setOnItemClickListener(theListListener);
    }
    public OnItemClickListener theListListener = new OnItemClickListener() {
    	public void onItemClick(android.widget.AdapterView<?> parent, View v, int position, long id) {
    		Uri uri = Uri.parse(vids.get(position).url);
    	    String vid = uri.getQueryParameter("v");
    	    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+vid)); 
    	    startActivity(i);
    	} 
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.search:
        	onSearchRequested();
            return true;
        case R.id.channel_info:
        	Intent myIntent = new Intent(main.this, ChannelInfo.class);
        	main.this.startActivity(myIntent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.context_menu, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      switch (item.getItemId()) {
      case 0:
    	  Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
    	  shareIntent.setType("text/plain");
    	  shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "YouTube Video");
    	  shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, vids.get(info.position).url);

    	  startActivity(Intent.createChooser(shareIntent, "Send YouTube Video"));
        return true;
      default:
        return super.onContextItemSelected(item);
      }
    }
    private void updateList(){
    	new Thread(new Runnable() {
    	    public void run() {
    	    	URL url;
    	    	
    	    	try {
    	            String featuredFeed = "http://gdata.youtube.com/feeds/api/videos?q="+
    	            	query + "&start-index=1&max-results=15&caption=true&format=1&v=2";
    	            
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
    	                  for (int i = 0 ; i < nl.getLength(); i++) {
    	                    Element entry = (Element)nl.item(i);
    	                    
    	                    Element title = (Element)entry.getElementsByTagName("title").item(0);
    	                    String titleStr = title.getFirstChild().getNodeValue();
    	                    
    	                    
    	                    
    	                    Element author = (Element)entry.getElementsByTagName("author").item(0);
    	                    Element auth = (Element)author.getElementsByTagName("name").item(0);
    	                    String authStr = auth.getFirstChild().getNodeValue();
    	                    
    	                    Element links = (Element)entry.getElementsByTagName("link").item(0);
    	                    String linkStr = links.getAttribute("href");
    	                    
    	                    Element media = (Element)entry.getElementsByTagName("media:thumbnail").item(0);
    	                    String imgStr = media.getAttribute("url");
    	                    
    	                    
    	                    Video vid = new Video();
    	                    vid.title = titleStr; 
    	                    vid.author = authStr;
    	                    vid.img = imgStr;
    	                    vid.url = linkStr;
    	                    vids.add(vid);
    	                  }
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

    	    	
    	    	
    	      list.post(new Runnable() {
    	        public void run() {
    	        	vids.remove(0);
    	        	adapter=new LazyAdapter(main.this, vids);
    	        	list.setAdapter(adapter);
    	        	setProgressBarIndeterminateVisibility(false);
    	        	list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
    	    			public void onCreateContextMenu(ContextMenu menu, View v,
    	    				ContextMenu.ContextMenuInfo menuInfo) {
    	    					menu.add(0, 0, 0, "Share");
    	    				}
    	    			});
    	        }
    	      });
    	    }
    	  }).start();
    }
    
}