package tk.icudi.map;

public class Point {

	double middleLat = 50.0075;
	double middleLng = 8.266;
	
	private Double lat;
	private Double lng;
	private int x;
	private int y;
	
	public Point(Double lat, Double lng) {
		this.lat = lat;
		this.lng = lng;
		
		System.out.println(" --- lat: " + lat);
		System.out.println(" --- lng: " + lng);
		
		double xd = (lat-middleLat)*(10000/5);
		double yd = (lng-middleLng)*(1000);
		
		this.x = (int)Math.floor(xd);
		this.y = (int)Math.floor(yd);
		System.out.println(" --- x: " + x + "/" + y);	
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
