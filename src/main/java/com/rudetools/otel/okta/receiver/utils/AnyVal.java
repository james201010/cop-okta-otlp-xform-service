/**
 * 
 */
package com.rudetools.otel.okta.receiver.utils;

/**
 * @author james101
 *
 */
public class AnyVal {

	private boolean booleanVal;
	private long longVal;
	private double doubleVal;
	private String stringVal;
	
	private final boolean hasBooleanVal;
	private final boolean hasLongVal;
	private final boolean hasDoubleVal;
	private final boolean hasStringVal;
	

	public AnyVal(boolean theVal) {
		this.booleanVal = theVal;
		
		this.hasBooleanVal = true;
		this.hasLongVal = false;
		this.hasDoubleVal = false;
		this.hasStringVal = false;
	}

	public AnyVal(long theVal) {
		this.longVal = theVal;
		
		this.hasBooleanVal = false;
		this.hasLongVal = true;
		this.hasDoubleVal = false;
		this.hasStringVal = false;
	}	
	
	public AnyVal(double theVal) {
		this.doubleVal = theVal;
		
		this.hasBooleanVal = false;
		this.hasLongVal = false;
		this.hasDoubleVal = true;
		this.hasStringVal = false;
	}	
	
	
	public AnyVal(String theVal) {
		this.stringVal = theVal;
		
		this.hasBooleanVal = false;
		this.hasLongVal = false;
		this.hasDoubleVal = false;
		this.hasStringVal = true;
	}

	public boolean getBooleanVal() {
		return booleanVal;
	}

	public long getLongVal() {
		return longVal;
	}

	public double getDoubleVal() {
		return doubleVal;
	}

	public String getStringVal() {
		return stringVal;
	}

	public boolean hasBooleanVal() {
		return hasBooleanVal;
	}

	public boolean hasLongVal() {
		return hasLongVal;
	}

	public boolean hasDoubleVal() {
		return hasDoubleVal;
	}

	public boolean hasStringVal() {
		return hasStringVal;
	}	
	
	
	
}
