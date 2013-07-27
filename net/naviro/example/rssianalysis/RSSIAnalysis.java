package net.naviro.example.rssianalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The entrance of the RSSI analysis package.
 * 
 * @author wxy
 * 
 */
public class RSSIAnalysis {

	public static ANPair doRSSIAnalysis(
			ArrayList<ArrayList<RSSIValue>> rssiList,
			ArrayList<Distance> distanceList) {
		// get filtered RSSI value
		ArrayList<Double> singleRssiList = new ArrayList<Double>();
		for (int i = 0; i < rssiList.size(); i++) {
			ArrayList<RSSIValue> afterGauss = MyMathematicalMachine
					.doGaussFiltering(rssiList.get(i));
			double geometricalAverage = MyMathematicalMachine
					.getGeometricalAverage(afterGauss);
			singleRssiList.add(geometricalAverage);
		}
		// get LRV pairs
		ArrayList<MyMathematicalMachine.LRVPair> lrvPairs = new ArrayList<MyMathematicalMachine.LRVPair>();
		for (int i = 0; i < rssiList.size(); i++) {
			MyMathematicalMachine.LRVPair p = new MyMathematicalMachine.LRVPair(
					Math.log10(distanceList.get(i).getDistance()),
					singleRssiList.get(i));
			lrvPairs.add(p);
		}
		// get LRF pair
		MyMathematicalMachine.LRFPair lrfPair = MyMathematicalMachine
				.getLRFPair(lrvPairs);
		double a = lrfPair.getA();
		double n = lrfPair.getB() / -10;
		return new ANPair(a, n);
	}

	/**
	 * Pair of value ( A, n ) in formula. The formula describes the relationship
	 * of RSSI value and distance.
	 * 
	 * @author wxy
	 * 
	 */
	static class ANPair {

		private double a;
		private double n;

		public ANPair(double a, double n) {
			this.setA(a);
			this.setN(n);
		}

		public double getA() {
			return a;
		}

		public void setA(double a) {
			this.a = a;
		}

		public double getN() {
			return n;
		}

		public void setN(double n) {
			this.n = n;
		}
	}

	/**
	 * Encapsulation of RSSI value.
	 * 
	 * @author wxy
	 * 
	 */
	static class RSSIValue {

		private int value;

		public RSSIValue(int value) {
			this.setValue(value);
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	}

	/**
	 * Encapsulation of distance value.
	 * 
	 * @author wxy
	 */
	static class Distance {

		private double distance;

		Distance(double distance) {
			this.setDistance(distance);
		}

		public double getDistance() {
			return distance;
		}

		public void setDistance(double distance) {
			this.distance = distance;
		}
	}

	private final static void parseFile(File file) {
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			ArrayList<ArrayList<RSSIValue>> lines = new ArrayList<ArrayList<RSSIValue>>();
			ArrayList<Distance> distanceList = new ArrayList<Distance>();
			while (br.ready()) {
				String line = br.readLine();
				String[] item = line.split(",");
				Distance distance = new Distance(Integer.parseInt(item[0]));
				distanceList.add(distance);
				ArrayList<RSSIValue> rssis = new ArrayList<RSSIValue>();
				for (int i = 0; i < item.length - 1; i++) {
					rssis.add(new RSSIValue(Integer.parseInt(item[i + 1])));
				}
				lines.add(rssis);
			}
			ANPair pair = doRSSIAnalysis(lines, distanceList);
			System.out.println("A = " + pair.getA() + " n = " + pair.getN());
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		File file = new File("Book2.csv");
		parseFile(file);
		file = new File("Book3.csv");
		parseFile(file);
		file = new File("Book4.csv");
		parseFile(file);
		file = new File("Book5.csv");
		parseFile(file);
		file = new File("Book6.csv");
		parseFile(file);
	}
}
