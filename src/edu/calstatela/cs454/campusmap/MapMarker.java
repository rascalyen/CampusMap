package edu.calstatela.cs454.campusmap;

import android.graphics.BitmapFactory;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;


public class MapMarker extends com.google.android.maps.Overlay{
	GeoPoint location = null;
	Context context = null;
	
	/**
	 *   picks the image to use as marker,
	 *   0 = user, 1 = building, 9 = destination, building by default
	 */
	public int markerType = 1;

	public MapMarker(GeoPoint location, Context context, int markerType){
		super();
		this.location = location;
		this.context = context;
		this.markerType = markerType;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		super.draw(canvas, mapView, shadow);
		Point screenPoint = new Point();
		mapView.getProjection().toPixels(this.location, screenPoint);

		switch(this.markerType){
			case 0: canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.green_marker),screenPoint.x, screenPoint.y , null);
				break;
			case 1: canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.gold_pin),screenPoint.x, screenPoint.y , null);
				break;
			case 2: canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.parking),screenPoint.x, screenPoint.y , null);
				break;
			case 3: canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.phone),screenPoint.x, screenPoint.y , null);
				break;
			case 4: canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.atm),screenPoint.x, screenPoint.y , null);
				break;
			case 5: canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.food),screenPoint.x, screenPoint.y , null);
				break;
			case 9: canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.red_dot),screenPoint.x, screenPoint.y , null);
				break;
			default: canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.gold_pin),screenPoint.x, screenPoint.y , null);
				break;
		
		}
	}
	
	public int getMarkerType(){
		return this.markerType;
	}
}
