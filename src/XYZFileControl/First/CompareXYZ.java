package XYZFileControl.First;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class CompareXYZ {
	static public double CheckDistance = 3.0;
	public static void main(String[] args) {
		// PointFile을 작은 파일로 한다.
		String pointFile = "/home/iswook/PointList/3. 5000_SUPPRESS_SO/2011_UTM.xyz";
		String pointFile2 = "/home/iswook/PointList/7. 25000납품원도수심/2011_25000_UTM52.xyz";
		String NewFileName = "/home/iswook/PointList/2011_5000-25000납품_비교.xyz";

		// 베이스 좌표를 가져온다.
		System.out.println("Loading : " + pointFile);
		ArrayList<Point3D>  ptList = LoadPoint(pointFile);
		System.out.println(pointFile + "\t Load Completed");
		System.out.println("\n");
		System.out.println("Loading : " + pointFile2);
		ArrayList<Point3D>  ptList2 = LoadPoint(pointFile2);
		
		System.out.println("\n"+pointFile + "\t Load Completed");
	
		ComparePointMem(ptList, ptList2, NewFileName);
		
//		ComparePoint(pointFile2, ptList, NewFileName);

		System.out.println("Points : " + ptList.size());
	}
	
	private static void ComparePointMem(ArrayList<Point3D> ptList, ArrayList<Point3D> ptList2, String newFileName) {
		FileWriter out = null;
		BufferedWriter writer = null;
		int cntLine = 0;
		try {
			out = new FileWriter(newFileName);
			writer = new BufferedWriter(out);

			Iterator<Point3D> pt3dIter1 = ptList.iterator();
			while (pt3dIter1.hasNext()) {
				Point3D pt1 = pt3dIter1.next();
				
				Iterator<Point3D> pt3dIter2 = ptList2.iterator();
				while (pt3dIter2.hasNext()) {
					Point3D pt2 = pt3dIter2.next();
					double distance = pt1.GetPoint2D().distance(pt2.GetPoint2D());
					if (distance <= CheckDistance) {
						writer.write(pt1.GetSourceString() + "\t" + pt2.GetSourceString() + "\t" + String.format("%.2f", distance));
//						System.out.println(pt1.GetSourceString() + "\t" + pt2.GetSourceString() + "\t" + String.format("%.2f", distance));
						
						writer.newLine();
						continue;
					}
				}
				cntLine ++;
				if (cntLine % 10000 == 0) {
					System.out.println("cnt : " + (cntLine));
				}
			}
			System.out.println("Lines : " + cntLine);
			System.out.println("Compare Completed!!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ;
	}

	private static void ComparePoint(String pointFile, ArrayList<Point3D> ptList, String newFileName) {
		
		FileReader in = null;
		BufferedReader reader = null;
		int cntLine = 0;
		int errcnt = 0;

		FileWriter out = null;
		BufferedWriter writer = null;

		ArrayList<Point3D> minPTList = new ArrayList<Point3D>();
		for (int i = 0 ; i < ptList.size() ; i++) minPTList.add(null);
		
		try {
			out = new FileWriter(newFileName);
			writer = new BufferedWriter(out);

			in = new FileReader(pointFile);
			reader = new BufferedReader(in);
			String str;
			Point3D pt3d = null;
			while (true) {
				str = reader.readLine();
				if (str == null) 
					break;
				cntLine ++;
				pt3d = Point3D.Create(str);
				if (cntLine % 1000 == 0) {
					System.out.println("cnt : " + (cntLine));
				}
				
				if (pt3d != null) {
					Iterator<Point3D> iter = ptList.iterator();
					Point3D closedPT = null;
					double closedPTDistance = 0.0;
					while (iter.hasNext()) {
						Point3D nowPT = iter.next();
						if (closedPT == null) {
							closedPT = nowPT;
							closedPTDistance = pt3d.GetPoint2D().distance(closedPT.GetPoint2D());
						} else {
							double nowPTDistance = nowPT.GetPoint2D().distance(pt3d.GetPoint2D()); 
							if (nowPTDistance < closedPTDistance) {
								closedPT = nowPT;
								closedPTDistance = nowPTDistance;
							}
						}
					}
					writer.write(pt3d.GetSourceString() + "\t" + closedPT.GetSourceString() + "\t" + String.format("%.2f", closedPTDistance));
					writer.newLine();
				} else {
					System.out.println("Err2 : " + str);
				}
			}
			System.out.println("Lines : " + cntLine);
			System.out.println("errcnt : " + errcnt);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				writer.flush();
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ;
	}
	
	private static ArrayList<Point3D> LoadPoint(String pointFile) {
		ArrayList<Point3D> pointList = new ArrayList<Point3D>();
		FileReader in = null;
		BufferedReader reader = null;
		int cntLine = 0;
		double minX = 999999.0;
		double minY = 9999999.0;
		double maxX = 0;
		double maxY = 0;
		int dupcnt = 0;
		try {
			in = new FileReader(pointFile);
			reader = new BufferedReader(in);
			String str;
			Point3D pt3d = null;
			while (true) {
				str = reader.readLine();
				if (str == null) 
					break;
				cntLine ++;
				pt3d = Point3D.Create(str);
				if (cntLine % 1000000 == 0 ) 
					System.out.println("cnt : " + (cntLine));// + " \t pt3D : " + utm.toDMSString());
				
				if (pt3d != null) {
					minX = Math.min(minX, pt3d.GetX());
					minY = Math.min(minY, pt3d.GetY());
					maxX = Math.max(maxX, pt3d.GetX());
					maxY = Math.max(maxY, pt3d.GetY());
					pointList.add(pt3d);
				} else {
					System.out.println("Err : " + str);
				}
			}
			System.out.println("minX : " + minX);
			System.out.println("minY : " + minY);
			System.out.println("maxX : " + maxX);
			System.out.println("maxY : " + maxY);
			System.out.println("Lines : " + cntLine);
			System.out.println("dupcnt : " + dupcnt);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return pointList;
	}
}
