package edu.calstatela.cs454.campusmap;

import java.util.ArrayList;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.TextView;


/**
 * LocationActivity provides expandable list views for 5 groups of location.
 * 
 * @author Rascal
 */
public class LocationActivity extends ExpandableListActivity {

	ExpandableListAdapter mAdapter;
	DBAdapter db = new DBAdapter(this);
		
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new MyExpandableListAdapter();
        setListAdapter(mAdapter);
        registerForContextMenu(getExpandableListView());
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
		                   
        case R.id.exit:    finish();  return true;      
		                   
        default:
            return super.onOptionsItemSelected(item);
        }
    }      
    
  
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Action");
        menu.add(0, 0, 0, R.string.location_action);
    }
    
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();

        String title = ((TextView) info.targetView).getText().toString();
        
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
        	Intent mainIntent = new Intent(LocationActivity.this, SchoolMapActivity.class);
        	mainIntent.putExtra("title", title);          
    		LocationActivity.this.startActivityForResult(mainIntent, 1);
            return true;
        } 
        else  return false;
    }    
 
    
   /**
    * MyExpandableListAdapter is a helper class to make customized expandable list
    * 
    * @author Rascal    
    */
    public class MyExpandableListAdapter extends BaseExpandableListAdapter {
        
        private String[] groups = {"Building", "Parking", "ATM", "Food Service", "Emergency"};
        private ArrayList<ArrayList<String>> children = new ArrayList<ArrayList<String>>();
        
        public MyExpandableListAdapter() {
        	addChildren("general");
        	addChildren("parking");
        	addChildren("atm");
        	addChildren("food");
        	addChildren("emergency");
        }
        
        public void addChildren(String type) {
           	db.openRead();
        	Cursor c = db.getLocationByType(type);
        	ArrayList<String> temp = new ArrayList<String>();
        	for(int i =0; i< c.getCount(); i++) {
        		c.moveToNext();
        		temp.add(c.getString(3));
        	}
        	children.add(temp);
        	c.close();
        	db.close();
        }
        
        public TextView getGenericView() {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 64);

            TextView textView = new TextView(LocationActivity.this);
            textView.setLayoutParams(lp);
            // Center the text vertically
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            textView.setPadding(55, 0, 0, 0);
            return textView;
        }
        
        public Object getChild(int groupPosition, int childPosition) {
            return children.get(groupPosition).get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return children.get(groupPosition).size();
        }
        
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getChild(groupPosition, childPosition).toString());
            return textView;
        }

        public Object getGroup(int groupPosition) {
            return groups[groupPosition];
        }

        public int getGroupCount() {
            return groups.length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getGroup(groupPosition).toString());
            return textView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }

}