package net.naviro.example.rssianalysis;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;
import org.junit.Assert;

import net.naviro.example.rssianalysis.RSSIAnalysis.RSSIValue;

public class MyMathematicalMachine {
	
	/**
	 * Get a list of RSSI measures. Select some of them by Gauss Filter formula.
	 * @param list
	 * @return
	 */
	static ArrayList< RSSIValue > doGaussFiltering( ArrayList< RSSIValue > list ){
		// calculate average and standard deviation
		ArrayList< Integer > intList = new ArrayList< Integer >();
		for( int i = 0; i < list.size(); i++ ){
			intList.add( new Integer( list.get( i ).getValue() ) );
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
			int item = iterator.next().intValue();
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
			double item = iterator.next().doubleValue();
			result += item;
		}
		// calculate average
		result = result / list.size();
		return result;
	}
	
	/**
	 * Warning: if list.size() == 0, it'll meet DividedByZero Exception.
	 * @param list
	 * @return
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
	 * @param list
	 * @return
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
	 * @param list
	 * @return
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
	 * @param list
	 * @return
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
	
	/* Test case */
	@Test
	public void testArithmeticAverage() {
		// case 1
		ArrayList< Integer > inputList1 = new ArrayList< Integer >();
		inputList1.add( new Integer( 0 ) );
		inputList1.add( new Integer( 1 ) );
		inputList1.add( new Integer( 2 ) );
		inputList1.add( new Integer( 3 ) );
		inputList1.add( new Integer( 4 ) );
		inputList1.add( new Integer( 5 ) );
		inputList1.add( new Integer( 6 ) );
		inputList1.add( new Integer( 7 ) );
		Assert.assertTrue( getArithmeticAverage( inputList1 ) == 3.5 );
		
		// case 2
		ArrayList< Integer > inputList2 = new ArrayList< Integer >();
		inputList2.add( new Integer( 5 ) );
		Assert.assertTrue( getArithmeticAverage( inputList2 ) == 5.0 );
		
		// case 3
		ArrayList< Integer > inputList3 = new ArrayList< Integer >();
		inputList3.add( new Integer( 5 ) );
		inputList3.add( new Integer( 1 ) );
		inputList3.add( new Integer( 2 ) );
		inputList3.add( new Integer( 100 ) );
		inputList3.add( new Integer( 5235 ) );
		// though it's dangerous to use '==' to compare double values
		Assert.assertTrue( getArithmeticAverage( inputList3 ) == 1068.6 );
	}
	
	@Test
	public void testStandardDeviation() {
		ArrayList< Integer > inputList = new ArrayList< Integer >();
		inputList.add( new Integer( 5 ) );
		inputList.add( new Integer( 9 ) );
		inputList.add( new Integer( 1 ) );
		inputList.add( new Integer( 6 ) );
		Assert.assertTrue( Math.abs( getStandardDeviation( inputList ) - Math.sqrt( 10.916667 ) ) < 0.001 );
	}
	
	@Test
	public void testIntegerGeometricalAverage(){
		ArrayList< Integer > inputList = new ArrayList< Integer >();
		inputList.add( new Integer( 5 ) );
		inputList.add( new Integer( 9 ) );
		inputList.add( new Integer( 1 ) );
		inputList.add( new Integer( 6 ) );
		Assert.assertTrue( Math.abs( getIntegerGeometricalAverage( inputList ) - Math.pow( 270.0, 0.25 ) ) < 0.001 );
	}
	
	@Test
	public void testLinearRegressionCalculation(){
		ArrayList< LRVPair > inputList = new ArrayList< LRVPair >();
		inputList.add( new LRVPair( 7.5, 6.7 ) );
		inputList.add( new LRVPair( 7.8, 7.0 ) );
		inputList.add( new LRVPair( 8.1, 7.4 ) );
		inputList.add( new LRVPair( 8.6, 7.7 ) );
		inputList.add( new LRVPair( 8.6, 7.6 ) );
		Assert.assertTrue( "Excepted: " + new LRFPair( 0.411, 0.846 ) + ", Actually: " + getLRFPair( inputList ),
				getLRFPair( inputList ).equals( new LRFPair( 0.411, 0.846 ) ) );
	}
	
	@Test
	public void overallTest(){
		ArrayList< RSSIValue > gaussList = new ArrayList< RSSIValue >();
		gaussList.add( new RSSIValue( 72 ) );
		gaussList.add( new RSSIValue( 69 ) );
		gaussList.add( new RSSIValue( 78 ) );
		gaussList.add( new RSSIValue( 73 ) );
		gaussList.add( new RSSIValue( 74 ) );
		gaussList.add( new RSSIValue( 72 ) );
		gaussList.add( new RSSIValue( 76 ) );
		gaussList.add( new RSSIValue( 69 ) );
		gaussList.add( new RSSIValue( 70 ) );
		gaussList.add( new RSSIValue( 69 ) );
		gaussList.add( new RSSIValue( 68 ) );
		gaussList.add( new RSSIValue( 65 ) );
		gaussList.add( new RSSIValue( 63 ) );
		gaussList.add( new RSSIValue( 72 ) );
		gaussList.add( new RSSIValue( 68 ) );
		ArrayList< RSSIValue > gaussResult = doGaussFiltering( gaussList );
		Assert.assertTrue( "Excepted: 11, Actually: " + gaussResult.size(), gaussResult.size() == 11 );
	}
}
