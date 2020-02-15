package tk.icudi.map;

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
