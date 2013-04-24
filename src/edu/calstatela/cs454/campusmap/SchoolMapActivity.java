package edu.calstatela.cs454.campusmap;

import com.google.android.maps.*;
import android.os.Bundle;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.*;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * SchoolMapActivity provides map functions.
 * 
 * @author Rascal
 *
 */
public class SchoolMapActivity extends MapActivity {

	MapView view = null;
	MapController control = null;
	List<Overlay> listOfOverLays = null;
	MapMarker destinationMarker = null;
	MapMarker userMarker = null;

	boolean showGroupBuildings = false;    // type 1
	boolean showGroupParking = false;      // type 2
	boolean showGroupEmergency = false;    // type 3
	boolean showGroupATM = false;          // type 4
	boolean showGroupFood = false;         // type 5
	DBAdapter dbAdapter = null;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		this.view = (MapView) findViewById(R.id.school_map);
		this.view.setBuiltInZoomControls(true);

		this.control = view.getController();
		this.initialize();
		this.userListener();
		this.buttonListeners();
		
		try{
			if(this.getIntent().getExtras().getString("title") != null){
				dbAdapter.openRead();
				Cursor locationData = dbAdapter.getLocationByTitle(this.getIntent().getExtras().getString("title"));
				locationData.moveToFirst();
				markDestination(new GeoPoint((int) (locationData.getDouble(1) * 1E6), (int) (locationData.getDouble(2) * 1E6)));
				locationData.close();
			}
			else if (this.getIntent().getExtras().getString("department") != null){
				dbAdapter.openRead();
				Cursor locationData = dbAdapter.getViewByDepartment(this.getIntent().getExtras().getString("department"));
				locationData.moveToFirst();
				markDestination(new GeoPoint((int) (locationData.getDouble(7) * 1E6), (int) (locationData.getDouble(8) * 1E6)));
				locationData.close();
			}
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		
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
		                   
        case R.id.exit:    startActivity(new Intent(SchoolMapActivity.this, MainActivity.class));	  
                           return true;         
		                   
        default:
            return super.onOptionsItemSelected(item);
        }
    }      	
	
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	
   /**
    *   to set up default view of CSULA campus	
    */
	protected void initialize() {
		this.dbAdapter = new DBAdapter(getApplicationContext());
		dbAdapter.openRead();

		String schoolCoordinates[] = { "34.0665065", "-118.168814" };
		double lat = Double.parseDouble(schoolCoordinates[0]);
		double lng = Double.parseDouble(schoolCoordinates[1]);

		GeoPoint p = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));

		this.listOfOverLays = this.view.getOverlays();
		this.control.animateTo(p);
		this.control.setZoom(17);
		this.view.invalidate();
	}	
	
		
	protected void buttonListeners(){
		
		final Button buildingsButton = (Button) findViewById(R.id.buildings);
		buildingsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleLocationGroup(1);
			}
		});

		final Button parkingButton = (Button) findViewById(R.id.parking);
		parkingButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleLocationGroup(2);
			}
		});

		final Button emergencyPhoneButton = (Button) findViewById(R.id.emergency);
		emergencyPhoneButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleLocationGroup(3);
			}
		});

		final Button atmButton = (Button) findViewById(R.id.atm);
		atmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleLocationGroup(4);
			}
		});

		final Button foodButton = (Button) findViewById(R.id.food);
		foodButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleLocationGroup(5);
			}
		});
	}

   /**
    *   listen user actions on map
    */
	protected void userListener() {

		LocationManager manager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		LocationListener listener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {

				GeoPoint p = new GeoPoint((int) (location.getLatitude() * 1E6),
						(int) (location.getLongitude() * 1E6));

				userMarker = new MapMarker(p,
						getApplicationContext(), 0);
				listOfOverLays = view.getOverlays();
				listOfOverLays.clear();
				listOfOverLays.add(userMarker);

				if (destinationMarker != null) {
					listOfOverLays.add(destinationMarker);
				}
				
				if(showGroupBuildings){
					showGroupBuildings = !showGroupBuildings;
					toggleLocationGroup(1);
				}
				else if(showGroupParking){
					showGroupParking = !showGroupParking;
					toggleLocationGroup(2);
				}
				else if(showGroupEmergency){
					showGroupEmergency = !showGroupEmergency;
					toggleLocationGroup(3);
				}
				else if(showGroupATM){
					showGroupATM = !showGroupATM;
					toggleLocationGroup(4);
				}
				else if(showGroupFood){
					showGroupFood = !showGroupFood;
					toggleLocationGroup(5);
				}
				
				control.animateTo(p);
				view.invalidate();
			}

			@Override
			public void onProviderDisabled(String arg0) {
			}

			@Override
			public void onProviderEnabled(String arg0) {
			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}
		};

		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				listener);
	}

	
	public void markDestination(GeoPoint p) {
		
		this.destinationMarker = new MapMarker(p, getApplicationContext(), 9);
		this.listOfOverLays.add(this.destinationMarker);
		this.control.animateTo(p);
		this.view.invalidate();
	}

	
   /**
	*  show user current location(if on campus) and destination markers on map
	*  AND/OR a group of location markers on map by groupType.
	* 
	* @param groupType  groupType is int from 1 to 5
	*/
	public void toggleLocationGroup(int groupType) {
		this.listOfOverLays.clear();
		if(this.userMarker != null){
			this.listOfOverLays.add(this.userMarker);
		}
		
		if(this.destinationMarker != null){
			this.listOfOverLays.add(this.destinationMarker);
		}
		
		Cursor groupData = null;

		switch (groupType) {
		case 1:
			this.showGroupBuildings = !this.showGroupBuildings;

			if (this.showGroupBuildings) {
				this.showGroupParking = false;
				this.showGroupEmergency = false;
				this.showGroupATM = false;
				this.showGroupFood = false;
				
				groupData = dbAdapter.getLocationByType("general");

				groupData.moveToFirst();
				
				while (groupData.isAfterLast() == false){
					GeoPoint p = new GeoPoint((int) (groupData.getDouble(1) * 1E6),
							(int) (groupData.getDouble(2) * 1E6));
					
					this.listOfOverLays.add(new MapMarker(p,
							getApplicationContext(), groupType));

					groupData.moveToNext();
				}
				groupData.close();
				
			} else {
				
			}
			break;
		case 2:
			this.showGroupParking = !this.showGroupParking;
			
			if (this.showGroupParking) {
				this.showGroupBuildings = false;
				this.showGroupEmergency = false;
				this.showGroupATM = false;
				this.showGroupFood = false;
				
				groupData = dbAdapter.getLocationByType("parking");

				groupData.moveToFirst();
				
				while (groupData.isAfterLast() == false){
					GeoPoint p = new GeoPoint((int) (groupData.getDouble(1) * 1E6),
							(int) (groupData.getDouble(2) * 1E6));
					
					this.listOfOverLays.add(new MapMarker(p,
							getApplicationContext(), groupType));

					groupData.moveToNext();
				}
				groupData.close();
				
			} else {
				
			}
			break;
		case 3:
			this.showGroupEmergency = !this.showGroupEmergency;
			
			if (this.showGroupEmergency) {
				this.showGroupBuildings = false;
				this.showGroupParking = false;
				this.showGroupATM = false;
				this.showGroupFood = false;
				
				groupData = dbAdapter.getLocationByType("emergency");

				groupData.moveToFirst();
				
				while (groupData.isAfterLast() == false){
					GeoPoint p = new GeoPoint((int) (groupData.getDouble(1) * 1E6),
							(int) (groupData.getDouble(2) * 1E6));
					
					this.listOfOverLays.add(new MapMarker(p,
							getApplicationContext(), groupType));

					groupData.moveToNext();
				}
				groupData.close();
				
			} else {
				
			}
			break;
		case 4:
			this.showGroupATM = !this.showGroupATM;
			
			if (this.showGroupATM) {
				this.showGroupBuildings = false;
				this.showGroupParking = false;
				this.showGroupEmergency = false;
				this.showGroupFood = false;
				
				groupData = dbAdapter.getLocationByType("atm");

				groupData.moveToFirst();
				
				while (groupData.isAfterLast() == false){
					GeoPoint p = new GeoPoint((int) (groupData.getDouble(1) * 1E6),
							(int) (groupData.getDouble(2) * 1E6));
					
					this.listOfOverLays.add(new MapMarker(p,
							getApplicationContext(), groupType));

					groupData.moveToNext();
				}
				groupData.close();
				
			} else {
				
			}
			break;
		case 5:
			this.showGroupFood = !this.showGroupFood;
			
			if (this.showGroupFood) {
				this.showGroupBuildings = false;
				this.showGroupParking = false;
				this.showGroupEmergency = false;
				this.showGroupATM = false;
				
				groupData = dbAdapter.getLocationByType("food");

				groupData.moveToFirst();
				
				while (groupData.isAfterLast() == false){
					GeoPoint p = new GeoPoint((int) (groupData.getDouble(1) * 1E6),
							(int) (groupData.getDouble(2) * 1E6));
					
					this.listOfOverLays.add(new MapMarker(p,
							getApplicationContext(), groupType));

					groupData.moveToNext();
				}
				groupData.close();
				
			} else {
				
			}
			break;
		default:
			break;
		}
		
		this.view.invalidate();

	}

}
