//The top-level JSON interpretation object
package com.ourbenefactors.treeidentification2;

public class ResponseData{
   	private Cursor cursor;
   	private Results[] results;

 	public Cursor getCursor(){
		return this.cursor;
	}
	public void setCursor(Cursor cursor){
		this.cursor = cursor;
	}
 	public Results[] getResults(){
		return this.results;
	}
	public void setResults(Results[] results){
		this.results = results;
	}
}
