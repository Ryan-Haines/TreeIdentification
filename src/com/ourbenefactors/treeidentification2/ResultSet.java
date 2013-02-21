package com.ourbenefactors.treeidentification2;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

//Class to represent interaction with results after filtering by traits
public class ResultSet {

	//name of this result
	String resultName;
	//Array of all viewerviews
	ImageViewer viewer; 

	//When constructing this result set, give a name 
	//and initialize the viewerview for the given context
	public ResultSet(String name, Context c){
		setName(name);
		viewer = new ImageViewer(name, c);
		this.initImageView(c);
		//this.viewer= new ImageViewer(name);
	}

	//adds a name at the given index
	public void setName(String name){
		resultName=name;
	}

	//get the name of this result set
	public String getName(){
		return resultName;
	}

	//Get the viewerviewer object
	public ImageViewer getImageViewer(){
		return viewer;
	}

	//get the viewer currently in focus by this result set
	public ImageView getImageView(){
		return viewer.getImage();
	}

	//get the next viewer held by this result set
	public ImageView getNextImage(){
		return viewer.getNextImage();
	}

	//get the previous viewer held by this resultSet
	public ImageView getPrevImage(){
		return viewer.getPrevImage();
	}

	//Initialize the viewerview for this resultset
	private void initImageView(Context c){
		//this.viewer.initializeImageView(c);
		this.viewer.initializeImageView();
	}
	
	//Set the next viewer for this resultset
	public void setNextImage(){
		this.viewer.setNextImg();
	}

	//Set the previous viewer for this resultset
	public void setPrevImage(){
		this.viewer.setPrevImg();
	}

	//sets the viewerViewer at the given location visible
	public void setImageVisible(){
		viewer.setVisible();
	}

	//Given a drawable, set the viewer in this viewerview
	public void setImageFromDrawable(Drawable d){
		viewer.setImage(d);
	}

	//Given a URL, set the viewer in this viewerview
	public void setImageFromURL(String url){
		viewer.setImageFromURL(url);
	}

}
