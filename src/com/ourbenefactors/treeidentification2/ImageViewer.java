package com.ourbenefactors.treeidentification2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

//Class that holds the imageviews
public class ImageViewer{
	
	public ImageViewer(String name, Context c) {
		this.setName(name);
		this.setContext(c);		
		//TODO: probably need to push this into threads
		//this.getImgUrlArray(this.getName());
	}

	ImageView inFocus;
	Context myContext;
	String myName;
	//Index of the image currently in focus
	int inFocusIndex = 0;
	//Arraylist to hold URLS of images available
	ArrayList<String> imgURLs = new ArrayList<String>();
	//ArrayList to hold drawables (images) that have already been downloaded
	ArrayList<Drawable> cachedImgs = new ArrayList<Drawable>();
	
	public void loadImageInFocus(){
		this.setImageFromURL(imgURLs.get(inFocusIndex));
	}

	//Set the image in focus to the given drawable
	public void setImage(Drawable d){
		Log.i("setImage", "about to set inFocus with a drawable");
		this.inFocus.setImageDrawable(d);
	}
	
	//Caches the given drawable at the given index
	public void cacheImg(Drawable d, int index){
		cachedImgs.add(index, d);
	}

	public void initializeImageView(){
		this.inFocus = new ImageView(myContext);
	}

	//sets the image url list corresponding to the given search term
	public void setURLList(String searchTerm){
		this.imgURLs = this.getImgUrlArray(searchTerm);
	}
	
	
	//gets the drawable for the given index from the cache.
	public Drawable getImageFromIndex(int index){
		//if the requested index is larger than the size of images we've cached
		Drawable d;
		if(index > cachedImgs.size()){
			d = ImageViewer.loadImageFromURL(imgURLs.get(index));
		}
		else{
			d = cachedImgs.get(index);
		}
		return d;
		
	}

	//Sets the image to the given drawable and caches it at the location of the index in focus
	public void setImageFromURL(String url){
		Log.i("setImageFromURL", "setting with url: "+url);
		Drawable d = ImageViewer.loadImageFromURL(url);
		this.setImage(d);
		this.cacheImg(d, inFocusIndex);
	}

	//Sets this imageView visible
	public void setVisible(){
		inFocus.setVisibility(View.VISIBLE);
	}

	//Does the cache have an entry at this index?
	//if the index given is greater than the size of the cached images,
	//we know that there is an image at that point
	private boolean cacheAt(int index){
		return cachedImgs.size() > index;
	}
	
	//If the next index is not larger than the size of available URLS, 
	//then set the image from the next URL
	//Set/pull from cache in this function
	public void setNextImg(){
		Log.i("setNextImg","started");
		String nextURL ="";
		if(++inFocusIndex < imgURLs.size()){
			Log.i("setNextImg", "passed size test");
			//if there's already an image in cache here, set that as the drawable
			if (cacheAt(inFocusIndex)){
				Log.i("setNextImg", "Pulling from cache index: "+ inFocusIndex);
				setImage(getImageFromIndex(inFocusIndex));
			}
			//otherwise, set the image from the appropriate URL
			else{
				nextURL = imgURLs.get(inFocusIndex);
				Log.i("setNextImg","about to set image from:" +nextURL);
				setImageFromURL(nextURL);
			}
		}
		else{
			Log.w("setNextImg", "tried to go past last image");
			Toast myToast = Toast.makeText(myContext, "This is the last image", Toast.LENGTH_SHORT);
			myToast.show();
			inFocusIndex--;
		}
		return;
	}

	//If the previous index is greater than 0, then set the image from the previous URL
	public void setPrevImg(){
		if(--inFocusIndex < 0){
			Log.w("setPrevImg", "tried to go before first image");
			Toast myToast = Toast.makeText(myContext, "This is the first image", Toast.LENGTH_SHORT);
			myToast.show();
			inFocusIndex = 0;
			return;
		}
		Log.i("setPrevImg", "Getting image from index: " +inFocusIndex);
		setImage(this.getImageFromIndex(inFocusIndex));
	}
		
	//Returns a drawable of the image located at the given URL
	private static Drawable loadImageFromURL(String url) {
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, url);
			return d;
		} catch (Exception e) {
			Log.e("loadImageFromURL", "Caught exception: "+e.getMessage());
			return null;
		}
	}

	//return the image currently in focus
	public ImageView getImage(){
		return inFocus;
	}

	//return an imageview containing the next image
	public ImageView getNextImage(){
		this.setNextImg();
		return this.getImage();
	}

	//Return an imageview containing the previous image
	public ImageView getPrevImage(){
		this.setPrevImg();
		return this.getImage();
	}

	//Get the IP of the device
	private String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						String ip = Formatter.formatIpAddress(inetAddress.hashCode());
						Log.i("getLocalIpAddress", "***** IP="+ ip);
						return ip;
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("getLocalIpAddress socket exception", ex.toString());
		}
		return null;
	}

	//Format the search term to include only the first term in the event it has a /
	//And add a "%20" for every space
	private String formatTermForURL(String searchTerm){
		String formattedTerm = searchTerm;
		int slashLocation = formattedTerm.indexOf("/");
		//if the string has a slash, return the first term
		if(slashLocation >0){
			formattedTerm = formattedTerm.substring(0, slashLocation);
		}
		formattedTerm = formattedTerm.replace(" ", "%20");

		return formattedTerm;
	}

	//given a search term and a start position, returns a JSON url which can then be
	//made into a JSON object
	public URL makeJSONURL(String searchTerm, String start){
		String ip = "&userip="+getLocalIpAddress();
		String formattedTerm = formatTermForURL(searchTerm);
		String myStart = "&start="+start;
		URL JSONURL=null;
		try{
			JSONURL = new URL("https://ajax.googleapis.com/ajax/services/search/images?" +
					"v=1.0&q="+formattedTerm+ip+myStart);
		}
		catch(MalformedURLException e)
		{Log.e("makeJSONURL", "badly formed url: "+ JSONURL);}

		Log.i("makeJSONURL: "+searchTerm + ", " +start , "formatted URL is: "+JSONURL);
		return JSONURL;
	}

	//returns a JSONObject (not yet a Java object, just a big nasty string) from the given URL
	public JSONObject getJSONfromURL(URL JSONURL){
		StringBuilder builder= null;
		JSONObject json = null;
		try{
			//eventual goal	

			URLConnection connection = JSONURL.openConnection();
			connection.addRequestProperty("Referer","http://google.com");
			String line;
			builder = new StringBuilder();
			InputStream inStream = connection.getInputStream();
			InputStreamReader inStreamReader = new InputStreamReader(inStream);
			BufferedReader reader = new BufferedReader(inStreamReader);
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}
			//Log.i("getJSONimgs", "finished while loop");
		}
		catch(IOException e){
			Log.e("IOException", "IO error caught:" +e.getMessage());
		}

		//Log.i("getJSONimgs", "builder.toString(): " +builder.toString());

		try{
			json = new JSONObject(builder.toString());
		}
		catch(JSONException e){
			Log.e("JSONException", "builder error caught");
		}
		return json;
	}

	//Returns a URL array of all image urls available for this search term
	public ArrayList<String> getImgUrlArray(String searchTerm){
		//ArrayList<String> imgURLs = new ArrayList<String>(); 

		//get a JSON URL
		URL jsonURL = makeJSONURL(searchTerm, "0");
		//use that URL to get a JSON object
		JSONObject searchResults = getJSONfromURL(jsonURL);
		//Get the string representation of the JSON object
		String searchResultsString = searchResults.toString();
		//convert that JSON string to a Java object
		Gson gson = new GsonBuilder().create();
		GoogleImageJson json = gson.fromJson(searchResultsString, GoogleImageJson.class);
		//get the pages in the result
		Pages[] myPages = json.getResponseData().getCursor().getPages();
		Results[] pageResults = json.getResponseData().getResults();

		//For every page in the initial result
		for(int i = 0; i<myPages.length; i++){
			//skip the first time around
			if(i>0){
				//set the start string equal to the start value of the page at the given index
				String start = myPages[i].getStart();
				//make a new JSONURL with our search term and start position
				jsonURL = makeJSONURL(searchTerm, start);
				//make a JSON object from the new url
				searchResults =getJSONfromURL(jsonURL);
				//Get the string representation of the new url
				searchResultsString = searchResults.toString();
				//Make a java object out of it
				json = gson.fromJson(searchResultsString, GoogleImageJson.class);
				//Get the results from that java object
				pageResults = json.getResponseData().getResults();
				//then add the image URLS from that object to the array
			}
			for(int j = 0; j<pageResults.length; j++){
				imgURLs.add(pageResults[j].getUnescapedUrl());
			}
		} 
		return imgURLs;
	}
	
	public String getName(){
		return myName;
	}
	
	public void setName(String name){
		myName = name;
	}
	
	public void setContext(Context c){
		myContext = c;
	}
}
