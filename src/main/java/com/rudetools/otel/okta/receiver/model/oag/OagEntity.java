/**
 * 
 */
package com.rudetools.otel.okta.receiver.model.oag;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rudetools.otel.okta.receiver.ApplicationConstants;
import com.rudetools.otel.okta.receiver.model.otlp.EntityInstance;
import com.rudetools.otel.okta.receiver.model.otlp.MetricDataPoint;
import com.rudetools.otel.okta.receiver.model.otlp.MetricDefinition;

import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.Metric.DataCase;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;

/**
 * @author james101
 *
 */
public abstract class OagEntity implements ApplicationConstants {

	public static final Logger lgr = LoggerFactory.getLogger(OagEntity.class);
	
	/**
	 * 
	 */
	public OagEntity() {
		
	}

	protected abstract EntityInstance getEntityInstance();
	
	
	protected static MetricDefinition createMetricDefinition(final EntityInstance entity, final String sourceMetricName, final int type, final String metricName, final String descr) {
		
		MetricDefinition def = new MetricDefinition(sourceMetricName, type, metricName, descr, entity.getMeter());
		entity.addMetricDefinition(def);
		return def;
		
	}	
	
	protected static MetricDataPoint createMetricDataPoint(final MetricDefinition metricDef, final NumberDataPoint dataPoint, final int sourceMetricType, final int metricType) {
		
		MetricDataPoint mdp = null;
		
		switch (sourceMetricType) {
		
		case MetricDefinition.GAUGE_DOUBLE:
			
			if (metricType == MetricDefinition.GAUGE_DOUBLE) {
				
				mdp = createDoubleMetricDataPoint(metricDef, dataPoint.getAsDouble());
				
			} else if (metricType == MetricDefinition.GAUGE_LONG) {
				
				mdp = createLongMetricDataPoint(metricDef, Double.valueOf(dataPoint.getAsDouble()).longValue());
			}
			
			break;

		case MetricDefinition.GAUGE_LONG:

			if (metricType == MetricDefinition.GAUGE_LONG) {
				
				mdp = createLongMetricDataPoint(metricDef, dataPoint.getAsInt());
				
			} else if (metricType == MetricDefinition.GAUGE_DOUBLE) {
				
				mdp = createDoubleMetricDataPoint(metricDef, Long.valueOf(dataPoint.getAsInt()).doubleValue());
			}
			
			break;		
			
		case MetricDefinition.UPDOWN_DOUBLE:
			
			if (metricType == MetricDefinition.GAUGE_DOUBLE) {
				
				mdp = createDoubleMetricDataPoint(metricDef, dataPoint.getAsDouble());
				
			} else if (metricType == MetricDefinition.GAUGE_LONG) {
				
				mdp = createLongMetricDataPoint(metricDef, Double.valueOf(dataPoint.getAsDouble()).longValue());
			}
			
			break;
			
		default:
			break;
		}

		return mdp;
	}
	
	
	protected static MetricDataPoint createLongMetricDataPoint(final MetricDefinition mdef, final long val) {
		
		MetricDataPoint mdp = new MetricDataPoint(false);
		mdp.setLongVal(val);
		mdef.addMetricDataPoint(mdp);
		return mdp;
	}
	
	
	protected static MetricDataPoint createDoubleMetricDataPoint(final MetricDefinition mdef, final double val) {
		
		MetricDataPoint mdp = new MetricDataPoint(true);
		mdp.setDoubleVal(val);
		mdef.addMetricDataPoint(mdp);
		return mdp;
	}	
	
	
	protected static boolean metricDefinitionExists(final String sourceMetricName, final List<MetricDefinition> metricDefinitions) {
		
		if (sourceMetricName != null && metricDefinitions != null) {
			
			for (MetricDefinition mdef : metricDefinitions) {
				if (mdef.getSourceMetricName().equals(sourceMetricName)) {
					return true;
				}
			}
			
		}
		return false;
	}	
	
	
	protected static List<NumberDataPoint> getNumberDataPointsForMetricType(final Metric metric) {
		String mtype = getMetricType(metric);
		
		switch (mtype) {
	
		case "GAUGE":
			return metric.getGauge().getDataPointsList();

		case "SUM":
			return metric.getSum().getDataPointsList();
			
		default:
			return null;
			
		}
	}
	
	
	protected static NumberDataPoint getNumberDataPointForMetricType(final Metric metric) {
		String mtype = getMetricType(metric);
		
		switch (mtype) {
	
		case "GAUGE":
			return metric.getGauge().getDataPoints(0);

		case "SUM":
			return metric.getSum().getDataPoints(0);
			
		default:
			return null;
			
		}
	}
	
	
	protected static void updateDataPointForMetric(final EntityInstance entity, final NumberDataPoint numberDp, final MetricDefinition metricDef, final int sourceMetricType) {
		
		List<MetricDataPoint> mdps =  metricDef.getDataPoints();
		
		if (mdps != null && mdps.size() > 0) {
						
			MetricDataPoint mdp = mdps.get(0);
			
			if (sourceMetricType == MetricDefinition.GAUGE_DOUBLE) {
				
				if (metricDef.getMetricType() == MetricDefinition.GAUGE_DOUBLE) {
					
					mdp.setDoubleVal(numberDp.getAsDouble());
					
				} else if (metricDef.getMetricType() == MetricDefinition.GAUGE_LONG) {
					
					mdp.setLongVal(Double.valueOf(numberDp.getAsDouble()).longValue());
				}
				
				
			} else if (sourceMetricType == MetricDefinition.GAUGE_LONG) {

				if (metricDef.getMetricType() == MetricDefinition.GAUGE_LONG) {
					
					mdp.setLongVal(numberDp.getAsInt());
					
				} else if (metricDef.getMetricType() == MetricDefinition.GAUGE_DOUBLE) {
					
					mdp.setDoubleVal(Long.valueOf(numberDp.getAsInt()).doubleValue());
					
				}
			
			} else if (sourceMetricType == MetricDefinition.UPDOWN_DOUBLE) {
				

				if (metricDef.getMetricType() == MetricDefinition.GAUGE_DOUBLE) {
					
					mdp.setDoubleVal(numberDp.getAsDouble());
				
				} else if (metricDef.getMetricType() == MetricDefinition.GAUGE_LONG) {
					
					mdp.setLongVal(Double.valueOf(numberDp.getAsDouble()).longValue());
				}
						
			}
			
		}
	}
	
	

	protected static void updateDataPointForMetric(final EntityInstance entity, final MetricDefinition metricDef, final int sourceMetricType, final Metric metric) {
		
		List<MetricDataPoint> mdps =  metricDef.getDataPoints();
		
		if (mdps != null && mdps.size() > 0) {
						
			MetricDataPoint mdp = mdps.get(0);
			
			if (sourceMetricType == MetricDefinition.GAUGE_DOUBLE) {
				
				if (metricDef.getMetricType() == MetricDefinition.GAUGE_DOUBLE) {
					
					mdp.setDoubleVal(metric.getGauge().getDataPoints(0).getAsDouble());
					
				} else if (metricDef.getMetricType() == MetricDefinition.GAUGE_LONG) {
					
					mdp.setLongVal(Double.valueOf(metric.getGauge().getDataPoints(0).getAsDouble()).longValue());
				}
				
				
			} else if (sourceMetricType == MetricDefinition.GAUGE_LONG) {

				if (metricDef.getMetricType() == MetricDefinition.GAUGE_LONG) {
					
					mdp.setLongVal(metric.getGauge().getDataPoints(0).getAsInt());
					
				} else if (metricDef.getMetricType() == MetricDefinition.GAUGE_DOUBLE) {
					
					mdp.setDoubleVal(Long.valueOf(metric.getGauge().getDataPoints(0).getAsInt()).doubleValue());
					
				}
			
			} else if (sourceMetricType == MetricDefinition.UPDOWN_DOUBLE) {
				

				if (metricDef.getMetricType() == MetricDefinition.GAUGE_DOUBLE) {
					
					mdp.setDoubleVal(metric.getSum().getDataPoints(0).getAsDouble());
				
				} else if (metricDef.getMetricType() == MetricDefinition.GAUGE_LONG) {
					
					mdp.setLongVal(Double.valueOf(metric.getSum().getDataPoints(0).getAsDouble()).longValue());
				}
						
			}
			
		}
	}

	
	protected static MetricDefinition createDefinitionForMetric(final EntityInstance entity, final String sourceMetricName, final int sourceMetricType, final String metricName, final int metricType, final String descr) {
		
		MetricDefinition metricDef;
		metricDef = createMetricDefinition(entity, sourceMetricName, metricType, metricName, descr);
		return metricDef;	
		
	}	
	
	protected static MetricDefinition createDefinitionAndDataPointForMetric(final EntityInstance entity, final NumberDataPoint dataPoint, final String sourceMetricName, final int sourceMetricType, final String metricName, final int metricType, final String descr) {
		
		MetricDefinition metricDef;
		metricDef = createMetricDefinition(entity, sourceMetricName, metricType, metricName, descr);
		createMetricDataPoint(metricDef, dataPoint, sourceMetricType, metricType);	
		return metricDef;
	}	
	
	protected static MetricDefinition findMatchingMetricDefinition(final List<MetricDefinition> metricDefs, final String metricName) {
		
		if (metricDefs != null && metricDefs.size() > 0) {
			for (MetricDefinition mDef : metricDefs) {
				log("findMatchingMetricDefinition: Metric name to find = " + metricName, false);
				log(" ---> MetricDef metric name = " + mDef.getMetricName(), false);
				if (mDef.getMetricName().equals(metricName)) {
					log(" ---> MetricDef metric name = " + mDef.getMetricName() + " : was a match", false);
					return mDef;
				}
			}
		} else {
			log("findMatchingMetricDefinition: metricDefs was NULL or Empty", false);
		}
		
		return null;
	}
	
	protected static List<MetricDefinition> findMatchingMetricDefinitions(final EntityInstance entity, final Metric metric) {
		
		
		List<MetricDefinition> extistingDefs = entity.getMetricDefinitions();
		
		if (extistingDefs != null && extistingDefs.size() > 0) {
			
			List<MetricDefinition> filteredDefs = new ArrayList<MetricDefinition>(); 
			
			for (MetricDefinition met : extistingDefs) {
				if (met.getSourceMetricName().equals(metric.getName())) {
					filteredDefs.add(met);
				}
			}
			
			return filteredDefs;
		}
		
		return null;
	}
	
	protected static String getMetricType(final Metric metric) {
		String metricType = null;
		DataCase dataCase = metric.getDataCase();
		switch (dataCase.getNumber()) {
		case 5:
			metricType = "GAUGE";
			break;

		case 7:
			metricType = "SUM";
			break;
			
		case 9:
			metricType = "HISTOGRAM";
			break;
			
		case 10:
			metricType = "EXPONENTIAL_HISTOGRAM";
			break;
			
		case 11:
			metricType = "SUMMARY";
			break;
			
		default:
			metricType = "data_not_set";
			break;
		}

		return metricType;
	}	
	
	private static void log(String msg, boolean isInfo) {
		if (isInfo) {
			lgr.info(msg);
		} else {
			lgr.info(msg);
		}
		
	}
	
}
