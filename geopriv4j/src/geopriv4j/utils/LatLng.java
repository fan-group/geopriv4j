package geopriv4j.utils;

/*
 * This class is used to store the latitude and longitude values of a location
 */

public class LatLng{
	public double latitude;
	public double longitude;

	public LatLng(){
		super();
	}
	public LatLng(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[latitude = "+this.latitude+ ",longitude = "+this.longitude+"]";
	}

}