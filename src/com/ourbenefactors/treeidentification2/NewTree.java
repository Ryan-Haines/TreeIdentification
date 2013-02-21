package com.ourbenefactors.treeidentification2;

import java.io.BufferedReader;
import java.io.File;
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
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class NewTree extends Activity implements OnClickListener {
	
	//the maximum number of results we will display
	//Currently need a case for each in onClick, unsure how to abstract
	static int MAX_RESULTS = 15;
	BufferedReader reader;
	File treeData;
	ArrayList<treeEntry>trees = new ArrayList<treeEntry>();
	String selectedTraits;
		//"...................................................";
	ResultSet[] resultSet;
	//Array of all result names in the order they appear on screen
	//String[] resultNames;
	//Array of all imageviews
	//ImageView[] images;
	//Layout that holds all buttons and images
	LinearLayout allResults;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		readData();
		setContentView(R.layout.newtree);
		setViews();
	}
	
	private void setViews(){
    	View idFlowersButton = findViewById(R.id.id_trees_button);
    	idFlowersButton.setOnClickListener(this);
    }
	
	
	
	//List all flowers and data
	//debug method
	private String listTrees(){
		String list = "";
		for(int i = 0; i < trees.size(); i++){
			list += trees.get(i).toString();
			list += "\n";
		}
		return list;
	}
	
	//List all the flower names of the given ArrayList
	//debug method
	private String listFlowersNames(ArrayList<treeEntry> mFlowers){
		String list = "";
		
		for(int i = 0; i < mFlowers.size(); i++){
			list += mFlowers.get(i).name;
			list += "\n";
		}
		return list;	
	}
	
	//Return an ArrayList of all trees that match the selected traits
	private ArrayList<treeEntry> giveMatches(){
		ArrayList<treeEntry> matchedFlowers = new ArrayList<treeEntry>();
		setMarkedTraits(findMarkedTraits());
		for(int i = 0; i< trees.size(); i++){
			treeEntry currentTree = trees.get(i);
			if(currentTree.traitsMatch(selectedTraits)){
				matchedFlowers.add(currentTree);
			}
		}
		return matchedFlowers;
	}
	
	//Return an ArrayList of single views for flowers that match the selected traits
	private ArrayList<View> giveViewMatches(){
		ArrayList<View> allViews = new ArrayList<View>();
		ArrayList<treeEntry> matchedFlowers = giveMatches();
		setMarkedTraits(findMarkedTraits());

		return allViews;
	}
	
	//Build the string of selected traits
	private void setMarkedTraits(ArrayList<Integer> marks){
		selectedTraits = "";
		for (int i = 0; i< 51; i++){
			if(marks.contains(i))
				selectedTraits += "+";
			else
				selectedTraits += ".";
		}
	}
	
	//Returns the width of the screen in density-independent pixels
	private float dpWidth(){
		//create variables for 80pct / 20pct width in pixels for button layout
		
		//find max dip width, set button size to a pct of this
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics ();
		display.getMetrics(outMetrics);
		float density  = getResources().getDisplayMetrics().density;
		float dpWidth  = outMetrics.widthPixels / density;
		return dpWidth;
	}
	
	//Returns the linear layout for a single result with the given result name and given index
	//Has search button, image button, and hidden imageview with next/prev
	private LinearLayout getResultView(String resultName, int i){
		//code to determine pixel size of various buttons based on screen density
		float dpWidth = dpWidth();
		float buttonDP = (float)(dpWidth * .8);
		float imgButtonDP = (float)(dpWidth * .2);
		float nextPrevDP = (float)(dpWidth * .1);
		float maxImgSizeDP = (float)(dpWidth * .8);
		Resources r = getResources();
		int buttonPx = (int)
			TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, buttonDP, r.getDisplayMetrics());
		int imgButtonPx = (int)
			TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, imgButtonDP, r.getDisplayMetrics());
		int nextPrevPx = (int)
			TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, nextPrevDP, r.getDisplayMetrics());
		int maxImgSizePx = (int)
			TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, maxImgSizeDP, r.getDisplayMetrics());
		
		//set up a horizontal layout for the buttons of the result
		LinearLayout Buttons = new LinearLayout(this);
		Buttons.setOrientation(LinearLayout.HORIZONTAL);
		Buttons.setTag("Buttons");
		
		//set a second horizontal layout for the next/prev buttons and the image
		LinearLayout ImgLayout = new LinearLayout(this);
		ImgLayout.setOrientation(LinearLayout.HORIZONTAL);
		ImgLayout.setTag("ImgLayout");
		
		//set a vertical layout to hold them both
		LinearLayout Result = new LinearLayout(this);
		Result.setOrientation(LinearLayout.VERTICAL);
		Result.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		Result.setTag("Result");
		Result.setId(i);
		
		//create the buttons and add them to their view
		Button myButton = new Button(this);
		Button imageButton = new Button(this);
		
		//set values for the search buttons
		myButton.setText(resultName);
		myButton.setTag(getString(R.string.getResultBtn));
		myButton.setId(i);
		myButton.setWidth(buttonPx);
		imageButton.setText(getString(R.string.getImgBtn));
		imageButton.setTag(getString(R.string.getImgBtn));
		imageButton.setId(i);
		imageButton.setWidth(imgButtonPx);
		//add those buttons to the buttons view
		Buttons.addView(myButton);
		Buttons.addView(imageButton);
		
		//Create next/prev buttons, initialize imageview 
		resultSet[i] = new ResultSet(resultName, this);
		Button next = new Button(this);
		Button prev = new Button(this);
		//set values for ImageView and next/prev buttons
		//May be able to get images asynchronously here in future
		resultSet[i].getImageView().setLayoutParams
			(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		resultSet[i].getImageView().setTag(resultName);
		resultSet[i].getImageView().setAdjustViewBounds(true);
		resultSet[i].getImageView().setMaxHeight(maxImgSizePx);
		resultSet[i].getImageView().setMaxWidth(maxImgSizePx);
		next.setText(getString(R.string.next));
		next.setTag(getString(R.string.next));
		next.setId(i);
		next.setWidth(nextPrevPx);
		
		prev.setText(getString(R.string.prev));
		prev.setTag(getString(R.string.prev));
		prev.setId(i);
		prev.setWidth(nextPrevPx);
		//Add the next/prev buttons and the imageview, then set view gone (invisible + no space)
		ImgLayout.addView(prev);
		//ImgLayout.addView(images[i]);
		ImgLayout.addView(resultSet[i].getImageView());
		ImgLayout.addView(next);
		ImgLayout.setVisibility(View.GONE);
		
		/* future code to right align prev button for prettiness
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)prev.getLayoutParams();
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.LEFT_OF, i);
		prev.setLayoutParams(params); //causes layout update
		*/
		
		//Now, add the search buttons and the next/prev buttons to the results view
		Result.addView(Buttons);
		Result.addView(ImgLayout);
		
		//set click listeners for all the new buttons
		myButton.setOnClickListener(this);
		imageButton.setOnClickListener(this);
		next.setOnClickListener(this);
		prev.setOnClickListener(this);
		
		return Result;
	}
	
	//Make an array of buttons corresponding to matching entries
	//then we place those buttons back on screen
	private void addButtonsToScreen(ArrayList<treeEntry> elementsToAdd){
		int numAdding = elementsToAdd.size();
		//Make a new set of results
		//ResultSet test = new ResultSet("something", getApplicationContext());
		resultSet = new ResultSet[numAdding];
		allResults = (LinearLayout) findViewById(R.id.resultButtonContainer);
		
		//clear any previous results first
		allResults.removeAllViews();
		//if there are too many results to display them all (arbitrary), inform user
		if(numAdding > MAX_RESULTS){
			Toast myToast = Toast.makeText(getApplicationContext(), "Too many results ("+numAdding+"), add more parameters", Toast.LENGTH_SHORT);
			myToast.show();
			
		}
		//otherwise, create buttons for each match
		//create buttons for image search here as well
		else{
			for (int i=0; i<numAdding; i++){
				String resultName = elementsToAdd.get(i).getName();
				//Add buttons for this result to the view of all results
				allResults.addView(getResultView(resultName, i));
			}
		}
		//Log.i("addButtonsToScreen", "allresults child count is:" +allResults.getChildCount());
	}
	
	public void doResults(ArrayList<treeEntry> matches){
		if (matches.size() > 0){
			addButtonsToScreen(matches);
			//break;
		}
		else{
			Toast myToast = Toast.makeText(getApplicationContext(), "No results, try fewer parameters", Toast.LENGTH_SHORT);
			myToast.show();
			//break;
		}
	}
	
	//What to do when a screen button is clicked
    public void onClick(View v){
    	ArrayList<treeEntry> matches = giveMatches();
    	String viewTag = v.getTag().toString();
    	Log.i("clicked view tag is", viewTag);
    	String imageButtonText = getString(R.string.getImgBtn);
    	int viewID = v.getId();
    	switch(viewID){
    		case R.id.id_trees_button:
    			Log.i("id trees button", "pressed");
    			doResults(matches);
    			break;
    			
    		//below are cases for potential dynamically created buttons
    		//regular/image buttons have same ID, so have to test which one it is
    		//to determine whether to search/imagesearch
    		case 0:
    			Log.i("before determining button type" , "view id is: "+viewID);
    			if(viewTag.equals(getString(R.string.getImgBtn))){
    				//need to get async working for loading icons to work
    				//set loading icon
    				resultSet[viewID].getImageViewer().getImage().setImageResource(R.drawable.loadingicon);
    				//set it visible
    				allResults.getChildAt(viewID).findViewWithTag("ImgLayout").setVisibility(View.VISIBLE);
    				resultSet[viewID].getImageViewer().setURLList(resultSet[viewID].getName());
    				resultSet[viewID].getImageViewer().loadImageInFocus();
    				Log.i("image setting", "set images["+viewID+"]");
    				Log.i("button 0 image search", resultSet[0].getName() );
    				break;
    			}
    			else if(viewTag.equals(getString(R.string.next))){
    				Log.i("onclick", "attempting to set next image");
    				resultSet[viewID].setNextImage();
    				break;
    			}
    			else if(viewTag.equals(getString(R.string.prev))){
    				resultSet[viewID].setPrevImage();
    				break;
    			}
    			else{
    				Uri uri = Uri.parse("http://www.google.com/#q=" + resultSet[0].getName());
    				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    				startActivity(intent);
    				Log.i("button 0 search", resultSet[0].getName());
    				break;	
    			}
    		case 1:
    			Uri uri1 = Uri.parse("http://www.google.com/#q=" +resultSet[1].getName() );
    			Intent intent1 = new Intent(Intent.ACTION_VIEW, uri1);
    			startActivity(intent1);
    			break;
    		case 2:
    			Uri uri2 = Uri.parse("http://www.google.com/#q=" +resultSet[2].getName() );
    			Intent intent2 = new Intent(Intent.ACTION_VIEW, uri2);
    			startActivity(intent2);
    			break;
    		case 3:
    			Uri uri3 = Uri.parse("http://www.google.com/#q=" +resultSet[3].getName() );
    			Intent intent3 = new Intent(Intent.ACTION_VIEW, uri3);
    			startActivity(intent3);
    			break;
    		case 4:
    			Uri uri4 = Uri.parse("http://www.google.com/#q=" +resultSet[4].getName() );
    			Intent intent4 = new Intent(Intent.ACTION_VIEW, uri4);
    			startActivity(intent4);
    			break;
    		case 5:
    			Uri uri5 = Uri.parse("http://www.google.com/#q=" +resultSet[5].getName() );
    			Intent intent5 = new Intent(Intent.ACTION_VIEW, uri5);
    			startActivity(intent5);
    			break;
    		case 6:
    			Uri uri6 = Uri.parse("http://www.google.com/#q=" +resultSet[6].getName() );
    			Intent intent6 = new Intent(Intent.ACTION_VIEW, uri6);
    			startActivity(intent6);
    			break;
    		case 7:
    			Uri uri7 = Uri.parse("http://www.google.com/#q=" +resultSet[7].getName() );
    			Intent intent7 = new Intent(Intent.ACTION_VIEW, uri7);
    			startActivity(intent7);
    			break;
    		case 8:
    			Uri uri8 = Uri.parse("http://www.google.com/#q=" +resultSet[8].getName() );
    			Intent intent8 = new Intent(Intent.ACTION_VIEW, uri8);
    			startActivity(intent8);
    			break;
    		case 9:
    			Uri uri9 = Uri.parse("http://www.google.com/#q=" +resultSet[9].getName() );
    			Intent intent9 = new Intent(Intent.ACTION_VIEW, uri9);
    			startActivity(intent9);
    			break;
    		case 10:
    			Uri uri10 = Uri.parse("http://www.google.com/#q=" +resultSet[10].getName() );
    			Intent intent10 = new Intent(Intent.ACTION_VIEW, uri10);
    			startActivity(intent10);
    			break;
    		case 11:
    			Uri uri11 = Uri.parse("http://www.google.com/#q=" +resultSet[11].getName() );
    			Intent intent11 = new Intent(Intent.ACTION_VIEW, uri11);
    			startActivity(intent11);
    			break;
    		case 12:
    			Uri uri12 = Uri.parse("http://www.google.com/#q=" +resultSet[12].getName() );
    			Intent intent12 = new Intent(Intent.ACTION_VIEW, uri12);
    			startActivity(intent12);
    			break;
    		case 13:
    			Uri uri13 = Uri.parse("http://www.google.com/#q=" +resultSet[13].getName() );
    			Intent intent13 = new Intent(Intent.ACTION_VIEW, uri13);
    			startActivity(intent13);
    			break;
    		case 14:
    			Uri uri14 = Uri.parse("http://www.google.com/#q=" +resultSet[14].getName() );
    			Intent intent14 = new Intent(Intent.ACTION_VIEW, uri14);
    			startActivity(intent14);
    			break;
    		case 15:
    			Uri uri15 = Uri.parse("http://www.google.com/#q=" +resultSet[15].getName() );
    			Intent intent15 = new Intent(Intent.ACTION_VIEW, uri15);
    			startActivity(intent15);
    			break;
    		default:
    			Log.i("something was pressed", "something was pressed");
    			
    	}
    }
	
	//Just the try/catch part, for more readable code
    //Helper to read tree data and add to arraylist
	private void readData(){
		try {
			readDataCatch();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Read in flower data file, add all to the ArrayList
	private void readDataCatch() throws IOException{
		InputStreamReader treeStream = 
			new InputStreamReader(getResources().openRawResource(R.raw.trees));
		BufferedReader treeReader = new BufferedReader(treeStream);
		
		String currentLine = treeReader.readLine();
		while(!currentLine.equals("end")){
			//Log.e("treeID.readDataCatch", "fails at loop in readDataCatch");
			trees.add(parseLine(currentLine));
			//Read in next line at end of loop
			currentLine = treeReader.readLine();
		}
	}
	
	//Parse one line of the flower data into a flowerEntry object with a name/id
	private treeEntry parseLine(String line){
		String parsing, name, id; 
		parsing = line;
		parsing = parsing.substring(4); //get rid of numbers at beginning
		//gets all characters up to "  "
		name = parsing.substring(0, nameLength(parsing)); 
		name = name.trim();
		//parsing should now be whitespace plus id
		parsing = parsing.substring(nameLength(parsing));
		parsing = parsing.trim(); //should be trimmed id
		id = parsing; 
		return new treeEntry(name, id);
	}
	
	//Delimiter to isolate name from ..+..+ etc
	private int nameLength(String line){
		for(int i = 0; i<line.length(); i++){
			if ((line.charAt(i) == '.' || line.charAt(i) == '+') && 
					(line.charAt(i+1) == '.' || line.charAt(i+1) == '+'))
				return i;
		}
		Log.e("wildFlowerID.nameLength", "could not determine name end index");
		return -1;
	}
	
	//Indicate all traits that are checked by returning an integer array of the positions
	//the traits were marked at
	private ArrayList<Integer> findMarkedTraits(){
		ArrayList<Integer> MarkedTraits = new ArrayList<Integer>();
		
		final RadioButton needles1 = (RadioButton) findViewById(R.id.needles1);
		final RadioButton needles2 = (RadioButton) findViewById(R.id.needles2);
		final RadioButton needles3 = (RadioButton) findViewById(R.id.needles3);
		final RadioButton needles4 = (RadioButton) findViewById(R.id.needles4);
		
		if(needles1.isChecked())
			MarkedTraits.add(0);
		if(needles2.isChecked())
			MarkedTraits.add(1);
		if(needles3.isChecked())
			MarkedTraits.add(2);
		if(needles4.isChecked())
			MarkedTraits.add(3);
		
		final CheckBox singles1 = (CheckBox) findViewById(R.id.singles1);
		final CheckBox singles2 = (CheckBox) findViewById(R.id.singles2);
		final CheckBox singles3 = (CheckBox) findViewById(R.id.singles3);
		final CheckBox singles4 = (CheckBox) findViewById(R.id.singles4);
		final CheckBox singles5 = (CheckBox) findViewById(R.id.singles5);
		
		if(singles1.isChecked())
			MarkedTraits.add(4);
		if(singles2.isChecked())
			MarkedTraits.add(5);
		if(singles3.isChecked())
			MarkedTraits.add(6);
		if(singles4.isChecked())
			MarkedTraits.add(7);
		if(singles5.isChecked())
			MarkedTraits.add(8);
		
		final RadioButton length1 = (RadioButton) findViewById(R.id.length1);
		final RadioButton length2 = (RadioButton) findViewById(R.id.length2);
		final RadioButton length3 = (RadioButton) findViewById(R.id.length3);
		final RadioButton length4 = (RadioButton) findViewById(R.id.length4);
		
		if (length1.isChecked())
			MarkedTraits.add(9);
		if (length2.isChecked())
			MarkedTraits.add(10);
		if (length3.isChecked())
			MarkedTraits.add(11);
		if (length4.isChecked())
			MarkedTraits.add(12);
		
		final RadioButton flattened1 = (RadioButton) findViewById(R.id.flattened1);
		final RadioButton flattened2 = (RadioButton) findViewById(R.id.flattened2);
		
		if (flattened1.isChecked())
			MarkedTraits.add(13);
		if (flattened2.isChecked())
			MarkedTraits.add(14);
		
		final RadioButton coneLength1 = (RadioButton) findViewById(R.id.coneLength1);
		final RadioButton coneLength2 = (RadioButton) findViewById(R.id.coneLength2);
		final RadioButton coneLength3 = (RadioButton) findViewById(R.id.coneLength3);
		final RadioButton coneLength4 = (RadioButton) findViewById(R.id.coneLength4);
		
		if (coneLength1.isChecked())
			MarkedTraits.add(15);
		if (coneLength2.isChecked())
			MarkedTraits.add(16);
		if (coneLength3.isChecked())
			MarkedTraits.add(17);
		if (coneLength4.isChecked())
			MarkedTraits.add(18);
		
		final CheckBox fruit1 = (CheckBox) findViewById(R.id.fruit1);
		final CheckBox fruit2 = (CheckBox) findViewById(R.id.fruit2);
		final CheckBox fruit3 = (CheckBox) findViewById(R.id.fruit3);
		final CheckBox fruit4 = (CheckBox) findViewById(R.id.fruit4);
		final CheckBox fruit5 = (CheckBox) findViewById(R.id.fruit5);
		final CheckBox fruit6 = (CheckBox) findViewById(R.id.fruit6);
		final CheckBox fruit7 = (CheckBox) findViewById(R.id.fruit7);
		final CheckBox fruit8 = (CheckBox) findViewById(R.id.fruit8);
		final CheckBox fruit9 = (CheckBox) findViewById(R.id.fruit9);
		
		if (fruit1.isChecked())
			MarkedTraits.add(19);
		if (fruit2.isChecked())
			MarkedTraits.add(20);
		if (fruit3.isChecked())
			MarkedTraits.add(21);
		if (fruit4.isChecked())
			MarkedTraits.add(22);
		if (fruit5.isChecked())
			MarkedTraits.add(23);
		if (fruit6.isChecked())
			MarkedTraits.add(24);
		if (fruit7.isChecked())
			MarkedTraits.add(25);
		if (fruit8.isChecked())
			MarkedTraits.add(26);
		if (fruit9.isChecked())
			MarkedTraits.add(27);
		
		final CheckBox thorns1 = (CheckBox) findViewById(R.id.thorns1);
		
		if (thorns1.isChecked())
			MarkedTraits.add(28);
		
		final RadioButton leafEdge1 = (RadioButton) findViewById(R.id.leafEdge1);
		final RadioButton leafEdge2 = (RadioButton) findViewById(R.id.leafEdge2);
		
		if (leafEdge1.isChecked())
			MarkedTraits.add(29);
		if (leafEdge2.isChecked())
			MarkedTraits.add(30);
		
		final RadioButton leafArrangement1 = (RadioButton) findViewById(R.id.leafArrangement1);
		final RadioButton leafArrangement2 = (RadioButton) findViewById(R.id.leafArrangement2);
		
		if (leafArrangement1.isChecked())
			MarkedTraits.add(31);
		if (leafArrangement2.isChecked())
			MarkedTraits.add(32);
		
		final CheckBox simple1 = (CheckBox) findViewById(R.id.simple1);
		final CheckBox simple2 = (CheckBox) findViewById(R.id.simple2);
		final CheckBox simple3 = (CheckBox) findViewById(R.id.simple3);
		final CheckBox simple4 = (CheckBox) findViewById(R.id.simple4);
		final CheckBox simple5 = (CheckBox) findViewById(R.id.simple5);
		final CheckBox simple6 = (CheckBox) findViewById(R.id.simple6);
		final CheckBox simple7 = (CheckBox) findViewById(R.id.simple7);
		final CheckBox simple8 = (CheckBox) findViewById(R.id.simple8);
		final CheckBox simple9 = (CheckBox) findViewById(R.id.simple9);
		final CheckBox simple10 = (CheckBox) findViewById(R.id.simple10);
		final CheckBox simple11 = (CheckBox) findViewById(R.id.simple11);
		final CheckBox simple12 = (CheckBox) findViewById(R.id.simple12);
		
		if (simple1.isChecked())
			MarkedTraits.add(33);
		if (simple2.isChecked())
			MarkedTraits.add(34);
		if (simple3.isChecked())
			MarkedTraits.add(35);
		if (simple4.isChecked())
			MarkedTraits.add(36);
		if (simple5.isChecked())
			MarkedTraits.add(37);
		if (simple6.isChecked())
			MarkedTraits.add(38);
		if (simple7.isChecked())
			MarkedTraits.add(39);
		if (simple8.isChecked())
			MarkedTraits.add(40);
		if (simple9.isChecked())
			MarkedTraits.add(41);
		if (simple10.isChecked())
			MarkedTraits.add(42);
		if (simple11.isChecked())
			MarkedTraits.add(43);
		if (simple12.isChecked())
			MarkedTraits.add(44);
		
		final CheckBox compound1 = (CheckBox) findViewById(R.id.compound1);
		final CheckBox compound2 = (CheckBox) findViewById(R.id.compound2);
		final CheckBox compound3 = (CheckBox) findViewById(R.id.compound3);
		final CheckBox compound4 = (CheckBox) findViewById(R.id.compound4);
		final CheckBox compound5 = (CheckBox) findViewById(R.id.compound5);
		
		if (compound1.isChecked())
			MarkedTraits.add(45);
		if (compound2.isChecked())
			MarkedTraits.add(46);
		if (compound3.isChecked())
			MarkedTraits.add(47);
		if (compound4.isChecked())
			MarkedTraits.add(48);
		if (compound5.isChecked())
			MarkedTraits.add(49);
		
		return MarkedTraits;
		
	}
	
	/*
	private ArrayList<Integer> getMarkedTraits(){
		return MarkedTraits;
	}
	*/
}