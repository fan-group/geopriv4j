package geopriv4j;


/*
 * We snap each latitude and longitude to the nearest point on a square 
 * grid with spacing ∆ in meters. 
 * 
 * This method has been implemented from the paper by Krumm, John. 
 * "Inference attacks on location tracks." International Conference on Pervasive Computing. 
 * Springer, Berlin, Heidelberg, 2007.
 */

import java.text.DecimalFormat;
import java.util.Random;

import geopriv4j.utils.Constants;
import geopriv4j.utils.LatLng;

public class RoundingAlgorithm {


	public static LatLng generate(LatLng location, double delta){

		Random rand = new Random();

		System.out.println("delta : "+delta);

		// setting the offset to both lat and lng
		double round_lat_delta = delta;
		double round_lng_delta = delta;

		//converting the lat and lng to degrees
		double lat_in_degrees = round_lat_delta/Constants.earth_radius;
		double lng_in_degrees = round_lng_delta/(Constants.earth_radius * Math.cos(Math.PI*location.latitude/180));
		double rounded_lat =  lat_in_degrees *180/Math.PI;
		double rounded_lng =  lng_in_degrees *180/Math.PI;

		if(rand.nextGaussian()<0.5) {
			rounded_lat *=-1;
		}

		if(rand.nextGaussian()<0.5) {
			rounded_lng *=-1;
		}

		double lat = location.latitude + rounded_lat;
		double lng = location.longitude + rounded_lng;


		/*
		 * Rounding off decimal places based on delta values
		 * 
        decimal
        places   degrees          distance
        -------  -------          --------
        0        1                111  km
        1        0.1              11.1 km
        2        0.01             1.11 km
        3        0.001            111  m
        4        0.0001           11.1 m
        5        0.00001          1.11 m
        6        0.000001         11.1 cm
        7        0.0000001        1.11 cm
        8        0.00000001       1.11 mm
		 */

		if(delta<111000 && delta>11100){
			DecimalFormat ft = new DecimalFormat("####.#");
			lat = Double.parseDouble(ft.format(lat));
			lng = Double.parseDouble(ft.format(lng));
		}
		else if(delta<11100 && delta>1110){
			DecimalFormat ft = new DecimalFormat("####.##");
			lat = Double.parseDouble(ft.format(lat));
			lng = Double.parseDouble(ft.format(lng));
		}
		else if(delta<1110 && delta>111){
			DecimalFormat ft = new DecimalFormat("####.###");
			lat = Double.parseDouble(ft.format(lat));
			lng = Double.parseDouble(ft.format(lng));
		}
		else if(delta<111 && delta>11.1){
			DecimalFormat ft = new DecimalFormat("####.####");
			lat = Double.parseDouble(ft.format(lat));
			lng = Double.parseDouble(ft.format(lng));
		}
		else if(delta<11.1 && delta>1.11){
			DecimalFormat ft = new DecimalFormat("####.#####");
			lat = Double.parseDouble(ft.format(lat));
			lng = Double.parseDouble(ft.format(lng));
		}
		else if(delta<1.11 && delta>0.111){
			DecimalFormat ft = new DecimalFormat("####.######");
			lat = Double.parseDouble(ft.format(lat));
			lng = Double.parseDouble(ft.format(lng));
		}
		else {
			DecimalFormat ft = new DecimalFormat("####.#######");
			lat = Double.parseDouble(ft.format(lat));
			lng = Double.parseDouble(ft.format(lng));
		}
		LatLng rounded = new LatLng(lat,lng);

		return rounded;
	}



}
