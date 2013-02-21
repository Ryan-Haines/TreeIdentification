package com.ourbenefactors.treeidentification2;

import com.ourbenefactors.treeidentification2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

//This is the title screen
public class treeID extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
   
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 
        setViews();
	}
	
	//Link views to title screen buttons
    public void setViews(){
    	View newFlowerButton = findViewById(R.id.new_tree_button);
    	newFlowerButton.setOnClickListener(this);
    	
    	View instructionsButton = findViewById(R.id.instructions_button);
    	instructionsButton.setOnClickListener(this);
    	
    	View aboutButton = findViewById(R.id.about_button);
    	aboutButton.setOnClickListener(this);
    	
    	//View testLayoutButton = findViewById(R.id.test_layout_button);
    	//testLayoutButton.setOnClickListener(this);
    }
    
    //What to do when a title screen button is clicked
    public void onClick(View v){
    	Intent i;
    	switch(v.getId()){
    		case R.id.new_tree_button:
    			i=new Intent(this, NewTree.class);
    			startActivity(i);
    			break;
    		case R.id.instructions_button:
    			i = new Intent(this, Instructions.class);
    			startActivity(i);
    			break;
    		case R.id.about_button:
    			i = new Intent(this, About.class);
    			startActivity(i);
    			break;
    		//case R.id.test_layout_button:
    			//setContentView(R.layout.testlayout);
    			//break;
    	}
    }
}