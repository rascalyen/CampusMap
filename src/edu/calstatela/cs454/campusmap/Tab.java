package edu.calstatela.cs454.campusmap;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;


/**
 * This tab activity holds LocationActivity and DepartmentActivity together.
 * 
 * @author Rascal
 *
 */
public class Tab extends TabActivity {
	

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tab);

		/**
		 *   Steps of making tab:
		 * 
		 *   1. instantiate resource object to get Drawables
		 *   2. Create The activity TabHost
		 *   3. instantiate resusable TabSpec for each tab
		 *   4. instantiate reusable Intent for each tab
		 *   5. Create an Intent to launch an Activity for the tab (to be reused)
		 *   6. Initialize a TabSpec for each tab and add it to the TabHost
		 *   7. setCurrentTab(0)
		 *   
		 */	    
	    Resources res = getResources(); 
	    TabHost tabHost = getTabHost();  
	    TabHost.TabSpec spec;  
	    Intent intent;  

	    intent = new Intent().setClass(this, LocationActivity.class);
	    spec = tabHost.newTabSpec("locations").setIndicator("Locations", 
	    		res.getDrawable(R.drawable.ic_tab_artists)).setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, DepartmentActivity.class);
	    spec = tabHost.newTabSpec("departments").setIndicator("Departments",
	            res.getDrawable(R.drawable.ic_tab_artists)).setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(0);
	}
}
