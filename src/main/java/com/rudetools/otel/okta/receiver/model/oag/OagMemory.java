/**
 * 
 */
package com.rudetools.otel.okta.receiver.model.oag;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rudetools.otel.okta.receiver.ApplicationConstants;
import com.rudetools.otel.okta.receiver.model.otlp.EntityInstance;
import com.rudetools.otel.okta.receiver.model.otlp.MetricDataPoint;
import com.rudetools.otel.okta.receiver.model.otlp.MetricDefinition;
import com.rudetools.otel.okta.receiver.utils.NumberUtils;

import io.opentelemetry.proto.metrics.v1.Metric;

/**
 * @author james101
 *
 */
public class OagMemory extends OagEntity implements ApplicationConstants {

	public static final Logger lgr = LoggerFactory.getLogger(OagMemory.class);
	public static final String ENTITY_TYPE_NAME = "oag_memory";
	
	private final OagNode node;
	private final EntityInstance entityInst;
	
	protected double totalMemBytes;
	protected double freeMemBytes;
	protected double availableMemBytes;	
	/**
	 * 
	 */
	public OagMemory(EntityInstance einst, OagNode oagNode) {
		super();
		this.entityInst = einst;
		this.node = oagNode;
		log("OagMemory Constructor for :", false);
		log(" ---> cluster        = " + this.node.getClusterName(), false);
		log(" ---> hostname       = " + this.node.getHostName(), false);
		log(" ---> ipaddress      = " + this.node.getIpAddress(), false);
		log("", false);		
	}

	public void processRequest(List<Metric> metrics) throws Throwable {
		
		// 3 source metrics turns into 9
		if (metrics != null && metrics.size() > 0) {
		
			for (Metric met : metrics) {
				
				if (metricDefinitionExists(met.getName(), this.entityInst.getMetricDefinitions())) {
					updateDefinitionsForMetric(this, met);
				} else {
					createDefinitionsForMetric(this, met);
				}
				
			}
			
			handleDefinitionsForDerivedMetrics(this);

		}		
		
	}
	
	private static void updateDefinitionsForMetric(final OagMemory oMem, final Metric metric) {
		
		String srcMetName = metric.getName();
		MetricDefinition metricDef = null;
		
		switch (srcMetName) {
		
		case OAG_MEMORY_TOTAL_BYTES:
			
			metricDef = findMatchingMetricDefinition(oMem.getEntityInstance().getMetricDefinitions(), COP_MEMORY_TOTAL_BYTES);
			updateDataPointForMetric(oMem.getEntityInstance(), metricDef, MetricDefinition.GAUGE_DOUBLE, metric);
			
			// set the mem bytes for the memory passed in
			oMem.totalMemBytes = metricDef.getDataPoints().get(0).getDoubleVal();
			
			metricDef = findMatchingMetricDefinition(oMem.getEntityInstance().getMetricDefinitions(), COP_MEMORY_TOTAL_GB);
			metricDef.getDataPoints().get(0).setDoubleVal(NumberUtils.bytesToGb(oMem.totalMemBytes));
			break;

		case OAG_MEMORY_AVAIL_BYTES:
			
			metricDef = findMatchingMetricDefinition(oMem.getEntityInstance().getMetricDefinitions(), COP_MEMORY_AVAIL_BYTES);
			updateDataPointForMetric(oMem.getEntityInstance(), metricDef, MetricDefinition.GAUGE_DOUBLE, metric);
			
			// set the mem bytes for the memory passed in
			oMem.availableMemBytes = metricDef.getDataPoints().get(0).getDoubleVal();
			
			metricDef = findMatchingMetricDefinition(oMem.getEntityInstance().getMetricDefinitions(), COP_MEMORY_AVAIL_GB);
			metricDef.getDataPoints().get(0).setDoubleVal(NumberUtils.bytesToGb(oMem.availableMemBytes));
			break;

		case OAG_MEMORY_FREE_BYTES:
			
			metricDef = findMatchingMetricDefinition(oMem.getEntityInstance().getMetricDefinitions(), COP_MEMORY_FREE_BYTES);
			updateDataPointForMetric(oMem.getEntityInstance(), metricDef, MetricDefinition.GAUGE_DOUBLE, metric);
			
			// set the mem bytes for the memory passed in
			oMem.freeMemBytes = metricDef.getDataPoints().get(0).getDoubleVal();
			
			metricDef = findMatchingMetricDefinition(oMem.getEntityInstance().getMetricDefinitions(), COP_MEMORY_FREE_GB);
			metricDef.getDataPoints().get(0).setDoubleVal(NumberUtils.bytesToGb(oMem.freeMemBytes));
			break;
			
		default:
			break;
		}
		
		
	}
	
	
	private static void handleDefinitionsForDerivedMetrics(final OagMemory oMem) {
		
		MetricDefinition metricDef = null;
		
		
		if (metricDefinitionExists(COP_MEMORY_USED_PERC, oMem.getEntityInstance().getMetricDefinitions())) {
			
			metricDef = findMatchingMetricDefinition(oMem.getEntityInstance().getMetricDefinitions(), COP_MEMORY_USED_PERC);
			metricDef.getDataPoints().get(0).setDoubleVal(NumberUtils.calculatePercentUsed(oMem.totalMemBytes, oMem.freeMemBytes));
			
		} else {
			metricDef = createDefinitionForMetric(oMem.getEntityInstance(), 
					COP_MEMORY_USED_PERC,
					MetricDefinition.GAUGE_DOUBLE,
					COP_MEMORY_USED_PERC,
					MetricDefinition.GAUGE_DOUBLE,
					"Memory percentage used.");
			createDoubleMetricDataPoint(metricDef, NumberUtils.calculatePercentUsed(oMem.totalMemBytes, oMem.freeMemBytes));
		}

		
		if (metricDefinitionExists(COP_MEMORY_AVAIL_PERC, oMem.getEntityInstance().getMetricDefinitions())) {

			metricDef = findMatchingMetricDefinition(oMem.getEntityInstance().getMetricDefinitions(), COP_MEMORY_AVAIL_PERC);
			metricDef.getDataPoints().get(0).setDoubleVal(NumberUtils.calculatePercentFree(oMem.totalMemBytes, oMem.availableMemBytes));
			
		} else {
			metricDef = createDefinitionForMetric(oMem.getEntityInstance(), 
					COP_MEMORY_AVAIL_PERC,
					MetricDefinition.GAUGE_DOUBLE,
					COP_MEMORY_AVAIL_PERC,
					MetricDefinition.GAUGE_DOUBLE,
					"Memory percentage available.");
			createDoubleMetricDataPoint(metricDef, NumberUtils.calculatePercentFree(oMem.totalMemBytes, oMem.availableMemBytes));
		}		
		
		
		if (metricDefinitionExists(COP_MEMORY_FREE_PERC, oMem.getEntityInstance().getMetricDefinitions())) {

			metricDef = findMatchingMetricDefinition(oMem.getEntityInstance().getMetricDefinitions(), COP_MEMORY_FREE_PERC);
			metricDef.getDataPoints().get(0).setDoubleVal(NumberUtils.calculatePercentFree(oMem.totalMemBytes, oMem.freeMemBytes));
			
		} else {
			metricDef = createDefinitionForMetric(oMem.getEntityInstance(), 
					COP_MEMORY_FREE_PERC,
					MetricDefinition.GAUGE_DOUBLE,
					COP_MEMORY_FREE_PERC,
					MetricDefinition.GAUGE_DOUBLE,
					"Memory percentage available.");
			createDoubleMetricDataPoint(metricDef, NumberUtils.calculatePercentFree(oMem.totalMemBytes, oMem.freeMemBytes));
		}
		
	}
	
	
	private static void createDefinitionsForMetric(final OagMemory oMem, final Metric metric) {
		
		String srcMetName = metric.getName();
		
		MetricDefinition metricDef = null;
		MetricDataPoint metricDp = null;
		
		switch (srcMetName) {
		
		case OAG_MEMORY_TOTAL_BYTES:
			
			metricDef = createDefinitionForMetric(oMem.getEntityInstance(), 
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_MEMORY_TOTAL_BYTES,
					MetricDefinition.GAUGE_DOUBLE,
					metric.getDescription());
			metricDp = createMetricDataPoint(metricDef, metric.getGauge().getDataPoints(0), MetricDefinition.GAUGE_DOUBLE, MetricDefinition.GAUGE_DOUBLE);
			
			oMem.totalMemBytes = metricDp.getDoubleVal();
			
			metricDef = createDefinitionForMetric(oMem.getEntityInstance(), 
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_MEMORY_TOTAL_GB,
					MetricDefinition.GAUGE_DOUBLE,
					"Total memory in GBs.");
			metricDp = createDoubleMetricDataPoint(metricDef, NumberUtils.bytesToGb(oMem.totalMemBytes));
						
			break;
			
		case OAG_MEMORY_AVAIL_BYTES:
			
			metricDef = createDefinitionForMetric(oMem.getEntityInstance(), 
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_MEMORY_AVAIL_BYTES,
					MetricDefinition.GAUGE_DOUBLE,
					metric.getDescription());
			metricDp = createMetricDataPoint(metricDef, metric.getGauge().getDataPoints(0), MetricDefinition.GAUGE_DOUBLE, MetricDefinition.GAUGE_DOUBLE);
			
			oMem.availableMemBytes = metricDp.getDoubleVal();
			
			metricDef = createDefinitionForMetric(oMem.getEntityInstance(), 
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_MEMORY_AVAIL_GB,
					MetricDefinition.GAUGE_DOUBLE,
					"Available memory in GBs.");
			metricDp = createDoubleMetricDataPoint(metricDef, NumberUtils.bytesToGb(oMem.availableMemBytes));

			break;

		case OAG_MEMORY_FREE_BYTES:

			metricDef = createDefinitionForMetric(oMem.getEntityInstance(), 
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_MEMORY_FREE_BYTES,
					MetricDefinition.GAUGE_DOUBLE,
					metric.getDescription());
			metricDp = createMetricDataPoint(metricDef, metric.getGauge().getDataPoints(0), MetricDefinition.GAUGE_DOUBLE, MetricDefinition.GAUGE_DOUBLE);
			
			oMem.freeMemBytes = metricDp.getDoubleVal();
			
			metricDef = createDefinitionForMetric(oMem.getEntityInstance(), 
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_MEMORY_FREE_GB,
					MetricDefinition.GAUGE_DOUBLE,
					"Free memory in GBs.");
			metricDp = createDoubleMetricDataPoint(metricDef, NumberUtils.bytesToGb(oMem.freeMemBytes));
			
			break;


		default:
			break;
		}
		
		
		
	}
	
	private static void log(String msg, boolean isInfo) {
		if (isInfo) {
			lgr.info(msg);
		} else {
			lgr.info(msg);
		}
		
	}
	
	public EntityInstance getEntityInstance() {
		return this.entityInst;
	}
}
