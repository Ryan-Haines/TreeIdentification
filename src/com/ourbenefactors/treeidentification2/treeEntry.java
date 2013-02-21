package com.ourbenefactors.treeidentification2;

public class treeEntry {
	
	String name;
	String id;
	
	public treeEntry(String n, String identification){
		name = n;
		id = identification;
	}
	
	public String getName(){
		return name;
	}
	
	public String toString(){
		return "Name: " +this.name + "\n" + " ID: " + id + "\n \n";
	}
	
	//Is this trait valid at the given index?
	private boolean traitAt(int index, String traitString){
		if(traitString.charAt(index) == '+')
			return true;
		else
			return false;
	}
	
	//Are the given traits a subset of traits possessed by this tree?
	public boolean traitsMatch(String selectedTraits){
		boolean match = true;
		
		for (int i= 0; i< selectedTraits.length() &&
					   match == true && 
					   i < this.id.length(); i++){
			if (traitAt(i, selectedTraits)){
				if(!traitAt(i, this.id)){
					match = false;
				}
			}
		}
		
		return match;
	}
}
