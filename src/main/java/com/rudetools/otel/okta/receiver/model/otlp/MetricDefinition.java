/**
 * 
 */
package com.rudetools.otel.okta.receiver.model.otlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.metrics.DoubleGaugeBuilder;
import io.opentelemetry.api.metrics.LongGaugeBuilder;
import io.opentelemetry.api.metrics.Meter;

/**
 * @author james101
 *
 */
public class MetricDefinition {

	public static final Logger lgr = LoggerFactory.getLogger(MetricDefinition.class);
	
	public static final int GAUGE_DOUBLE = 1;
	public static final int GAUGE_LONG = 2;
	public static final int UPDOWN_DOUBLE = 3;
	
	final private String srcMetricName;
	final private String metricName;
	final private String metricDescr;
	final private int metricType;
	private List<MetricDataPoint> dataPoints = null;
	
	private Meter meter = null;
	private LongGaugeBuilder longGaugeBldr = null;
	private DoubleGaugeBuilder doubleGaugeBldr = null;
	
	/**
	 * 
	 */
	public MetricDefinition(String sourceMetricName, int type, String metricName, String descr, Meter meter) {
		this.srcMetricName = sourceMetricName;
		this.metricType = type;
		this.metricName = metricName;
		this.metricDescr = descr;
		this.meter = meter;
		
	}


	
	
	// !!!!  List<MetricDataPoint> (this.dataPoints) must be populated before this method is called
	public void startRecording() {
		
		switch (this.metricType) {
		
		case GAUGE_DOUBLE:
			
			this.doubleGaugeBldr = this.meter.gaugeBuilder(this.metricName);
			
			if (this.dataPoints != null && this.dataPoints.size() > 0) {
				
				for (MetricDataPoint dp : this.dataPoints) {
					
					if (dp.getAttributesMap() != null && dp.getAttributesMap().size() > 0) {
						
						Set<String> amKeys = dp.getAttributesMap().keySet();
						
						
						if (dp.getAttributesMap().size() > 1) {
							
							AttributesBuilder attrBldr = Attributes.builder();
							
							for (String amKey : amKeys) {
								
								attrBldr.put(AttributeKey.stringKey(amKey), dp.getAttributesMap().get(amKey));
								
							}
							
							this.doubleGaugeBldr.setDescription(this.getMetricDescr())
							.setUnit("")
							.buildWithCallback(measurement -> {
								
								try {
									measurement.record(dp.getDoubleVal(), attrBldr.build());
								} catch (Throwable ex) {
									lgr.error("The metric named '" + this.metricName + "' may have the wrong data type set for the DataPoint created for it.", ex);
								}
								
							});								
					
						} else {
							
							for (String amKey : amKeys) {
								
								this.doubleGaugeBldr.setDescription(this.getMetricDescr())
								.setUnit("")
								.buildWithCallback(measurement -> {
									
									try {
										measurement.record(dp.getDoubleVal(), Attributes.of(AttributeKey.stringKey(amKey), dp.getAttributesMap().get(amKey)));
									} catch (Throwable ex) {
										lgr.error("The metric named '" + this.metricName + "' may have the wrong data type set for the DataPoint created for it.", ex);
									}
									
								});								
								
							}
							
						}


					} else {
						
						this.doubleGaugeBldr.setDescription(this.getMetricDescr())
						.setUnit("")
						.buildWithCallback(measurement -> {
							
							try {
								measurement.record(dp.getDoubleVal(), Attributes.empty());
							} catch (Throwable ex) {
								lgr.error("The metric named '" + this.metricName + "' may have the wrong data type set for the DataPoint created for it.", ex);
								
							}
							
							
						});								
					}
					
				}
				
			}
			
	
			break;
			
		case GAUGE_LONG:
			
			this.longGaugeBldr = this.meter.gaugeBuilder(this.metricName).ofLongs();
			
			
			
			if (this.dataPoints != null && this.dataPoints.size() > 0) {
				
				for (MetricDataPoint dp : this.dataPoints) {
					
					if (dp.getAttributesMap() != null && dp.getAttributesMap().size() > 0) {
						
						Set<String> amKeys = dp.getAttributesMap().keySet();
						
						if (dp.getAttributesMap().size() > 1) {
							
							AttributesBuilder attrBldr = Attributes.builder();
							
							for (String amKey : amKeys) {
								
								attrBldr.put(AttributeKey.stringKey(amKey), dp.getAttributesMap().get(amKey));
								
							}
							
							this.longGaugeBldr.setDescription(this.getMetricDescr())
							.setUnit("")
							.buildWithCallback(measurement -> {
								
								try {
									measurement.record(dp.getLongVal(), attrBldr.build());
								} catch (Throwable ex) {
									lgr.error("The metric named '" + this.metricName + "' may have the wrong data type set for the DataPoint created for it.", ex);
								}
								
							});								
					
						} else {
							
							for (String amKey : amKeys) {
								
								this.longGaugeBldr.setDescription(this.getMetricDescr())
								.setUnit("")
								.buildWithCallback(measurement -> {
									
									try {
										measurement.record(dp.getLongVal(), Attributes.of(AttributeKey.stringKey(amKey), dp.getAttributesMap().get(amKey)));
									} catch (Throwable ex) {
										lgr.error("The metric named '" + this.metricName + "' may have the wrong data type set for the DataPoint created for it.", ex);
									}
									
								});								
								
							}
							
						}
						
						
						
					} else {
						
						this.longGaugeBldr.setDescription(this.getMetricDescr())
						.setUnit("")
						.buildWithCallback(measurement -> {
							
							try {
								measurement.record(dp.getLongVal(), Attributes.empty());
							} catch (Throwable ex) {
								lgr.error("The metric named '" + this.metricName + "' may have the wrong data type set for the DataPoint created for it.", ex);
							}
							
						});								
					}
					
				}
				
			}			
			
			break;


		default:
			break;
		}
		
		
		
	}
	
	//public MetricDataPoint getMetricDataPoint(String metricName) {}
	
	
	public void addMetricDataPoint(MetricDataPoint mdp) {
		
		if (this.dataPoints == null) {
			this.dataPoints = new ArrayList<MetricDataPoint>();
		}
		
		this.dataPoints.add(mdp);
	}	
	
	public List<MetricDataPoint> getDataPoints() {
		return dataPoints;
	}

	public void setDataPoints(List<MetricDataPoint> dataPoints) {
		this.dataPoints = dataPoints;
	}

	public LongGaugeBuilder getLongGaugeBldr() {
		return longGaugeBldr;
	}

	public void setLongGaugeBldr(LongGaugeBuilder longGaugeBldr) {
		this.longGaugeBldr = longGaugeBldr;
	}

	public DoubleGaugeBuilder getDoubleGaugeBldr() {
		return doubleGaugeBldr;
	}

	public void setDoubleGaugeBldr(DoubleGaugeBuilder doubleGaugeBldr) {
		this.doubleGaugeBldr = doubleGaugeBldr;
	}

	public String getMetricName() {
		return metricName;
	}
	
	public String getSourceMetricName() {
		return srcMetricName;
	}
	
	public String getMetricDescr() {
		return metricDescr;
	}

	public int getMetricType() {
		return metricType;
	}


	
	
}
