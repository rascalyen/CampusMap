package edu.calstatela.cs454.campusmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Main activity of CampusMap APP
 * 
 * @author Rascal
 *
 */
public class MainActivity extends Activity implements OnClickListener{

	DBAdapter db = new DBAdapter(this);

	AutoCompleteTextView textView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ImageButton ib1 = (ImageButton) findViewById(R.id.imageButton1);
		ib1.setOnClickListener(this);

		ImageButton ib2 = (ImageButton) findViewById(R.id.imageButton2);
		ib2.setOnClickListener(this);

		this.textView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);

		Button b1= (Button) findViewById(R.id.mapit);
		b1.setOnClickListener(this);

		initiateDB(db);

		/**
		 *  if campus.db is empty, insert data into campus.db
		 */
		String destPath = "/data/data/"+getPackageName()+"/databases/campus.db";
		File f = new File(destPath);
		long dbSize = f.length();
		if(dbSize<7500) {
			db.openWrite();
			try {
				InsertAllData(db);
			} catch (IOException e) {
				e.printStackTrace();
			}
			db.close();
		}
		db.openRead();
		String[] departments = db.getAlldepartments();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, departments);
		textView.setAdapter(adapter);
		db.close();
	}


	private void initiateDB(DBAdapter db) {
		db.openRead();
		db.close();
	}

	
	/**
	 * insert all data into database from assets directory
	 * 
	 * @param db
	 * @throws IOException
	 */
	private void InsertAllData(DBAdapter db) throws IOException {   	
		AssetManager am = getAssets();
		InputStream fis = am.open("locations.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line="";		
		while((line=br.readLine())!=null){
			if (line.length()==0 || line.contains("@relation") || 
					line.contains("@attribute") || line.contains("@data"))
				continue;
			else {
				String str[] = line.split("[,]");
				db.insertLocation(str[0], str[1], str[2], str[3], str[4], str[5], str[6]);
			}
		}		
		br.close();
		fis.close();
		InputStream fis2 = am.open("departments.txt");
		BufferedReader br2 = new BufferedReader(new InputStreamReader(fis2));
		line="";		
		while((line=br2.readLine())!=null){
			if (line.length()==0 || line.contains("@relation") || 
					line.contains("@attribute") || line.contains("@data"))
				continue;
			else {
				String str[] = line.split("[,]");
				db.insertDepartment(str[0], str[1], str[2], str[3], str[4], str[5], str[6], str[7]);
			}
		}		
		br2.close();
		fis2.close();
	}    

	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		
		case R.id.imageButton1:   startActivity(new Intent(MainActivity.this, Tab.class));
		                          break;
		
		case R.id.imageButton2:   startActivity(new Intent(MainActivity.this, SchoolMapActivity.class));
		                          break;
		
		case R.id.mapit:          
			
			if (this.textView.getText().toString().equals("")) {
				showToast("Can't be empty !");
				break;
			}
		    else {
		        final String department = this.textView.getText().toString();
		        Intent mainIntent = new Intent(MainActivity.this, SchoolMapActivity.class);
		        mainIntent.putExtra("department", department);          
		        MainActivity.this.startActivityForResult(mainIntent, 1);
		        break;
		    }	
		}	
	}
	
	
	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
}
