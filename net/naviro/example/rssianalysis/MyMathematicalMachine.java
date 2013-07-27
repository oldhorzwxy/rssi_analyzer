package net.naviro.example.rssianalysis;

import java.util.ArrayList;
import java.util.Iterator;

import net.naviro.example.rssianalysis.RSSIAnalysis.RSSIValue;

public class MyMathematicalMachine {
	
	/**
	 * Get a list of RSSI measures. Select some of them by Gauss Filter formula.
	 * @param list the list passed into the math machine to get Gauss filtered
	 * @return a list with Gauss filtered values
	 */
	static ArrayList< RSSIValue > doGaussFiltering( ArrayList< RSSIValue > list ){
		// calculate average and standard deviation
		ArrayList< Integer > intList = new ArrayList< Integer >();
		for( int i = 0; i < list.size(); i++ ){
			intList.add( list.get( i ).getValue() );
		}
		double average = getArithmeticAverage( intList );
		double standardDeviation = getStandardDeviation( intList );
		
		/*ArrayList< RSSIValue > result = new ArrayList< RSSIValue >();
		for( int i = 0; i < list.size(); i++ ){
			int item = list.get( i ).getValue();
			double upright = ( item - average ) * ( item - average ) / ( 2 * standardDeviation * standardDeviation );
			double f = 1.0 / ( standardDeviation * Math.sqrt( 2 * Math.PI ) )
					* Math.pow( Math.E, 0.0 - upright );
			// if this item lies in certain area, it's valid in result list
			if( f <= 1.0 && f >= GAUSS_POSSIBILITY ){
				result.add( list.get( i ) );
			}
		}*/
		// I have changed the selecting algorithm
		ArrayList< RSSIValue > result = new ArrayList< RSSIValue >();
		for( int i = 0; i < list.size(); i++ ){
			int item = list.get( i ).getValue();
			// if this item lies in certain area, it's valid in result list
            // the area is around average in ONE standard deviation
			if( item <= average + standardDeviation && item >= average - standardDeviation ){
				result.add( list.get( i ) );
			}
		}
		
		return result;
	}
	
	private static double getArithmeticAverage( ArrayList< Integer > list ){
		double result = 0.0;
		// get int out of list
		Iterator< Integer > iterator = list.iterator();
		while( iterator.hasNext() ){
			int item = iterator.next();
			result += item;
		}
		// calculate average
		result = result / list.size();
		return result;
	}
	
	private static double getDoubleArithmeticAverage( ArrayList< Double > list ){
		double result = 0.0;
		// get double out of list
		Iterator< Double > iterator = list.iterator();
		while( iterator.hasNext() ){
			double item = iterator.next();
			result += item;
		}
		// calculate average
		result = result / list.size();
		return result;
	}
	
	/**
	 * Warning: if list.size() == 0, it'll meet DividedByZero Exception.
	 */
	private static double getStandardDeviation( ArrayList< Integer > list ){
		// get average of the values
		double average = getArithmeticAverage( list );
		double result = 0.0;
		// get int out of list
		Iterator< Integer > iterator = list.iterator();
		while( iterator.hasNext() ){
			int item = iterator.next();
			result += ( item - average ) * ( item - average );
		}
		// warning: divided by zero
		result = result / ( list.size() - 1 );
		result = Math.sqrt( result );
		return result;
	}
	
	/**
	 * Calculate the geometrical average value of a list of RSSI values.
     * Its current implementation is dealing only with integers, under presumption of RSSI value is integer.
	 */
	static double getGeometricalAverage( ArrayList< RSSIValue > list ){
		ArrayList< Integer > intlist = new ArrayList< Integer >();
		for( int i = 0; i < list.size(); i++ ){
			intlist.add( list.get( i ).getValue() );
		}
		return getIntegerGeometricalAverage( intlist );
	}
	
	/**
	 * Calculate the geometrical average value of a list of integer values.
	 */
	private static double getIntegerGeometricalAverage( ArrayList< Integer > list ){
		double result = 1.0;
		for( int i = 0; i < list.size(); i++ ){
			result *= list.get( i );
		}
		result = Math.pow( result, 1.0 / list.size() );
		return result;
	}
	
	/**
	 * Calculator the factors in linear regression function.
	 */
	static LRFPair getLRFPair( ArrayList< LRVPair > list ){
		// calculate average of x
		ArrayList< Double > xDoubleList = new ArrayList< Double >();
		for( int i = 0; i < list.size(); i++ ){
			xDoubleList.add( list.get( i ).getX() );
		}
		double xAverage = getDoubleArithmeticAverage( xDoubleList );
		// calculate average of y
		ArrayList< Double > yDoubleList = new ArrayList< Double >();
		for( int i = 0; i < list.size(); i++ ){
			yDoubleList.add( list.get( i ).getY() );
		}
		double yAverage = getDoubleArithmeticAverage( yDoubleList );
		
		// sigma (xy)
		double sumXY = 0.0;
		for( int i = 0; i < list.size(); i++ ){
			sumXY += list.get( i ).getX() * list.get( i ).getY();
		}
		// n(xy)
		double nXY = list.size() * xAverage * yAverage;
		// sigma (xx)
		double sumXX = 0.0;
		for( int i = 0; i < list.size(); i++ ){
			sumXX += list.get( i ).getX() * list.get( i ).getX();
		}
		// n(xx)
		double nXX = list.size() * xAverage * xAverage;
		
		// get b value
		double b = ( sumXY - nXY ) / ( sumXX - nXX );
		double a = yAverage - b * xAverage;
		
		return new LRFPair( a, b );
	}
	
	/**
	 * The pair of the ( x, y ) values in linear regression function.
	 * x stands for variable and y stands for measured value.
	 * @author wxy
	 *
	 */
	static class LRVPair{
		
		private double x;	// variable x
		private double y;	// measured value y
		
		LRVPair( double x, double y ){
			this.setX( x );
			this.setY( y );
		}

		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}
	}
	
	/**
	 * The pair of the ( a, b ) factors in linear regression function.
	 * a stands for additional value of bx and b stands for factor of x.
	 * Formula: y = bx + a
	 * @author wxy
	 *
	 */
	static class LRFPair{
		
		private double a;	// value 'a' in formula
		private double b;	// factor 'b' in formula
		
		LRFPair( double a, double b ){
			this.setA( a );
			this.setB( b );
		}
		
		public double getA() {
			return a;
		}
		public void setA(double a) {
			this.a = a;
		}
		public double getB() {
			return b;
		}
		public void setB(double b) {
			this.b = b;
		}
		
		@Override
		public boolean equals( Object o ){
			if( o instanceof LRFPair ){
				return Math.abs( ( ( ( LRFPair )o ).getA() - this.a ) ) < 0.001
						&& Math.abs( ( ( ( LRFPair )o ).getB() - this.b ) ) < 0.001;
			}
			return false;
		}
		
		@Override
		public String toString(){
			return "Pair: a = " + this.a + " b = " + this.b; 
		}
	}
}
