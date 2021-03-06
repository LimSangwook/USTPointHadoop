package XYZFileControl.First;

import java.awt.geom.Path2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.coords.UTMCoord;

public class C52ZoneTo51Zone {
	public static void main(String []args) {
		String pointFile = "/home/iswook/PointList/h3724041_52_utm_Java.xyz";
		String saveFile = "/home/iswook/PointList/h3724041_51_utm_Java.xyz";
		WritePointList(pointFile, saveFile);
		
	}
	private static ArrayList<Point3D> WritePointList(String pointFile, String saveFile) {
		ArrayList<Point3D> pointList = new ArrayList<Point3D>();
		FileReader in = null;
		BufferedReader reader = null;
		int cntLine = 0;
		double minX = 999999.0;
		double minY = 9999999.0;
		double maxX = 0;
		double maxY = 0;

		FileWriter out = null;
		BufferedWriter writer = null;
		try {
			out = new FileWriter(saveFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer = new BufferedWriter(out);
		
		
		try {
			in = new FileReader(pointFile);
			reader = new BufferedReader(in);
			String str;
			Point3D pt3d = null;
			long cntContained = 0;
			while (true) {
				str = reader.readLine();
				if (cntLine % 1000000 == 0) {
					System.out.println("cnt : " + (cntLine) + "\t, cnt contained : " + cntContained);
					writer.flush();
				}
				
				if (str == null) 
					break;
				cntLine ++;
				pt3d = Point3D.Create(str);
				
				if (pt3d != null) {
					minX = Math.min(minX, pt3d.GetX());
					minY = Math.min(minY, pt3d.GetY());
					maxX = Math.max(maxX, pt3d.GetX());
					maxY = Math.max(maxY, pt3d.GetY());

					UTMCoord utm = UTMCoord.fromUTM(52, AVKey.NORTH, pt3d.GetX(), pt3d.GetY());
					UTMCoord utm2 = UTMCoord.fromLatLon2UTMZone(Angle.fromDegrees(utm.getLatitude().getDegrees()), Angle.fromDegrees(utm.getLongitude().getDegrees()), 51);
					double x = utm2.getEasting();
					double y = utm2.getNorthing();
					writer.write(String.format("%.2f", x) + " " + String.format("%.2f", y) + " " + String.format("%.1f", pt3d.GetZ()));
					writer.newLine();
					cntContained++;						

				} else {
					System.out.println("Err : " + str);
				}
			}
			System.out.println("minX : " + minX);
			System.out.println("minY : " + minY);
			System.out.println("maxX : " + maxX);
			System.out.println("maxY : " + maxY);
			System.out.println("Lines : " + cntLine);
			System.out.println("cntContained : " + cntContained);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				writer.flush();
				out.flush();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		
		return pointList;
	}
}
