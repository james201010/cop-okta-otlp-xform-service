/**
 * 
 */
package com.rudetools.otel.okta.receiver.utils;

/**
 * @author james101
 *
 */
public class NumberUtils {

	/**
	 * 
	 */
	public NumberUtils() {
		
	}

	public static double bytesToGb(final double bytesVal) {
		
		//double retVal = bytesVal / 1000 / 1000 / 1000;
		
		double retVal = bytesVal / 1024 / 1024 / 1024;
		return retVal;
		
	}
	
	public static double calculatePercentUsed(final double total, final double free) {
		double tmp = free / total;
		tmp = 1 - tmp;
		return 100 * tmp;		
	}
	
	public static double calculatePercentFree(final double total, final double free) {
		double tmp = free / total;
		return 100 * tmp;		
	}
	
}
