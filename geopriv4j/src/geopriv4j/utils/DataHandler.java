package geopriv4j.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DataHandler {
	public static <T> void writeData(String fileName, ArrayList<T> objects) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(new File(fileName));
        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
        objectOut.writeObject(objects);
        objectOut.close();
        System.out.println("The Object  was succesfully written to a file "+objects);
		
	}
	
	public static <T> ArrayList<T> readData(String fileName) throws IOException, ClassNotFoundException {
		FileInputStream fi = new FileInputStream(new File(fileName));
		ObjectInputStream oi = new ObjectInputStream(fi);
		
		ArrayList<T> objects = (ArrayList<T>) oi.readObject();
		
        System.out.println("The Object  was succesfully read from a file "+objects);
		return objects;
		
	}
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		arrayList.add(5);
		arrayList.add(6);
		arrayList.add(7);

		writeData("data/data.txt", arrayList);
		
		readData("data/data.txt");

		
		
	}
}
