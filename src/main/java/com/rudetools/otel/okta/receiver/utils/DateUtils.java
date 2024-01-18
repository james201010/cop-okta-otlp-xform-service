/**
 * 
 */
package com.rudetools.otel.okta.receiver.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author James Schneider
 *
 */
public class DateUtils {

	/**
	 * 
	 */
	public DateUtils() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
		
			
			Calendar cal = Calendar.getInstance();
			
			cal.roll(Calendar.MONTH, -1);
			cal.set(Calendar.DAY_OF_MONTH, 12);
			
			DateFormat df = new SimpleDateFormat("MMM d");
			
			System.out.println(df.format(cal.getTime()));
			
			
			//System.out.println(cal.getTime());
			
			
			//System.out.println(getLastLogin());
			//System.out.println(getNextPaymentDate("mortgage"));
			//System.out.println(getNextPaymentDate("auto"));
			
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		
	}

	public static String getNextPaymentDate(String paymentType) {
	
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		if (paymentType.equals("mortgage")) {
			cal.roll(Calendar.MONTH, 1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			return df.format(cal.getTime());
		} else {
			if (day > 17) {
				cal.roll(Calendar.MONTH, 1);
				cal.set(Calendar.DAY_OF_MONTH, 17);
				return df.format(cal.getTime());
			} else {
				cal.set(Calendar.DAY_OF_MONTH, 17);
				return df.format(cal.getTime());				
			}
			
		}
		
	}
	
	public static int getPaymentDayOfMonth(String paymentType) {
		if (paymentType.equals("mortgage")) {
			return 1;
		} else {
			return 17;
		}
		
	}
	
	public static String getLastLogin() {
		
		Calendar cal = Calendar.getInstance();
		
		cal.roll(Calendar.DAY_OF_MONTH, false);
		cal.roll(Calendar.HOUR_OF_DAY, true);
		cal.roll(Calendar.MINUTE, 3);
		
		return cal.getTime().toString();
	}
}
