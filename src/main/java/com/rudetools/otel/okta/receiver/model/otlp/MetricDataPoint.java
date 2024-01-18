/**
 * 
 */
package com.rudetools.otel.okta.receiver.model.otlp;

import java.util.HashMap;
import java.util.Map;

/**
 * @author james101
 *
 */
public class MetricDataPoint {

	private boolean isDoubleVal = true;
	private Double doubleVal = null;
	private Long longVal = null;
	private Map<String, String> attributesMap = null;
	
	/**
	 * 
	 */
	public MetricDataPoint(boolean isDouble) {
		this.isDoubleVal = isDouble;
	}

	
	public void addMetricAttribute(String key, String val) {
		
		if (this.attributesMap == null) {
			this.attributesMap = new HashMap<String, String>();
		}
		
		this.attributesMap.put(key, val);
	}	
	
	public double getDoubleVal() {
		return doubleVal;
	}

	public void setDoubleVal(double doubleVal) {
		this.doubleVal = doubleVal;
	}

	public long getLongVal() {
		return longVal;
	}

	public void setLongVal(long longVal) {
		this.longVal = longVal;
	}

	public Map<String, String> getAttributesMap() {
		return attributesMap;
	}

	public void setAttributesMap(Map<String, String> attributesMap) {
		this.attributesMap = attributesMap;
	}

	public boolean isDoubleVal() {
		return isDoubleVal;
	}

	
	
}
