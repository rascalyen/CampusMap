package edu.calstatela.cs454.campusmap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * DetailActivity is to show detail information of a department.
 * 
 * @author Rascal
 * 
 */
public class DetailActivity extends Activity implements OnClickListener {
	
	private Button goButton;
	private TextView department;
	private TextView college;
	private TextView departWeb;
	private TextView departEmail;
	private TextView departPhone;
	private TextView departBuilding;
	private TextView departRoom;
	private DBAdapter db = new DBAdapter(this);
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.department_detail);    
        
        this.department = (TextView) this.findViewById(R.id.department_name);
        this.college = (TextView) this.findViewById(R.id.college_name);
        this.departWeb = (TextView) this.findViewById(R.id.department_web);
        this.departEmail = (TextView) this.findViewById(R.id.department_email);
        this.departPhone = (TextView) this.findViewById(R.id.department_phone);
        this.departBuilding = (TextView) this.findViewById(R.id.department_location);
        this.departRoom = (TextView) this.findViewById(R.id.department_room);
                
        this.goButton = (Button) this.findViewById(R.id.goToMap);
        this.goButton.setOnClickListener(this);
        
        String depart = this.getIntent().getExtras().getString("department");
        db.openRead();
        Cursor c = db.getViewByDepartment(depart);
        this.department.setText(c.getString(2));
        this.college.setText(c.getString(1));
        this.departWeb.setText(c.getString(3));
        this.departEmail.setText(c.getString(4));
        this.departPhone.setText(c.getString(5));
        this.departBuilding.setText(c.getString(9));
        this.departRoom.setText(c.getString(6));
        c.close();
        db.close();
    }
    
	public void onClick(View v) {
		String department = this.getIntent().getExtras().getString("department");
		Intent mainIntent = new Intent(DetailActivity.this, SchoolMapActivity.class);
        mainIntent.putExtra("department", department);
		DetailActivity.this.startActivityForResult(mainIntent, 1);		
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
		                   
        case R.id.exit:    startActivity(new Intent(DetailActivity.this, MainActivity.class));
                           return true;      
                           
        default:
            return super.onOptionsItemSelected(item);
        }
    }      	
	
	
    
}
