package geopriv4j.utils;

public interface Constants {
	//Earth’s radius, sphere
	final public static int earth_radius = 6378137;
	
	//Specify the threshold for each cell in VHC algorithm
    public static int VHC_LIMIT = 500;
    
    //Specify the path to the open Street dataset
    public static String FILE = "data/maploc.txt";
}
