package tk.icudi.map;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.util.LengthUnit;

public class Point {

	double middleLat = 50.0075;
	double middleLng = 8.266;
	
	double latTileDistance = 0.0005;
	double lngTileDistance = 0.001;
	
	private Double lat;
	private Double lng;
	private int x;
	private int y;
	
	public Point(Double lat, Double lng) {
		this.lat = lat;
		this.lng = lng;		
		double xd = (lat-middleLat)/latTileDistance;
		double yd = (lng-middleLng)/lngTileDistance;
		this.x = (int)Math.floor(xd);
		this.y = (int)Math.floor(yd);
		//System.out.println(" --- created new point: (" + x + "/" + y + "), ("+lat+"/"+lng+")");	
	}

	public Point(String latString, String lonString) {
		this(Double.valueOf(latString),Double.valueOf(lonString));
	}

	public Point move(int xInc, int yInc) {
		double lat = this.lat+(xInc*latTileDistance);
		double lng = this.lng+(yInc*lngTileDistance);
		return new Point(lat, lng);
	}
	
	public double distFrom(Point other) {
		return distFrom(this.lat, this.lng, other.lat, other.lng);
	}
	
	public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
		LatLng point1 = new LatLng(lat1, lng1);
		LatLng point2 = new LatLng(lat2, lng2);
		return com.javadocmd.simplelatlng.LatLngTool.distance(point1, point2, LengthUnit.METER);
	}

	public double getAngleFrom(Point userLoc) {
		double longDistance = this.lng - userLoc.lng;
		double latDistance = this.lat - userLoc.lat;
		double hypothenuse = Math.sqrt((longDistance * longDistance) + (latDistance * latDistance));
		
		if(hypothenuse == 0){
			return 0;
		}
		
		double vAngle = (Math.acos(latDistance / hypothenuse) * 360) / (2 * Math.PI);
		if(longDistance < 0){
			vAngle = 360 - vAngle;
		}
		
		return vAngle;
	}
	
	public Direction getDirectionFrom(Point userLoc) {
		return Direction.valueOfAngle(getAngleFrom(userLoc));
	}
	
	public Double getLat() {
		return lat;
	}

	public Double getLng() {
		return lng;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	
}
