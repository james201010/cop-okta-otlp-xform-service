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

import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;

/**
 * @author james101
 *
 */
public class OagCpu extends OagEntity implements ApplicationConstants {

	public static final Logger lgr = LoggerFactory.getLogger(OagCpu.class);
	public static final String ENTITY_TYPE_NAME = "oag_cpu";
	
	private final EntityInstance entityInst;
	private final OagNode node;
	private final String cpuId;
	
	/**
	 * 
	 */
	public OagCpu(EntityInstance einst, OagNode oagNode, String cpuId) {
		super();
		this.entityInst = einst;
		this.node = oagNode;	
		this.cpuId = cpuId;		
		
		
	}

	
	// we already know that the metrics list passed in are only related to the parent node
	// however, these metrics will be for multiple disks related to the node
	public void processRequest(List<Metric> metrics) throws Throwable {
		
		log("OagCpu processRequest for :", false);
		log(" ---> cluster        = " + this.node.getClusterName(), false);
		log(" ---> hostname       = " + this.node.getHostName(), false);
		log(" ---> ipaddress      = " + this.node.getIpAddress(), false);
		log(" ---> cpu id         = " + this.getCpuId(), false);
		
		log("", false);		
		
		// 4 source metrics turns into 12
		if (metrics != null && metrics.size() > 0) {
		
			for (Metric met : metrics) {
				
				if (metricDefinitionExists(met.getName(), this.entityInst.getMetricDefinitions())) {
					//updateDefinitionsForMetric(this, met);
				} else {
					createDefinitionsForMetric(this, met);
					
				}
				
			}
			
			//handleDefinitionsForDerivedMetrics(this);
			
		}

	}	
	
	private static void updateDefinitionsForMetric(final OagCpu oCpu, final Metric metric) { 
		
		String srcMetName = metric.getName();
		MetricDefinition metricDef = null;
		NumberDataPoint numDataPoint = null;
		
		switch (srcMetName) {
		
		case OAG_CPU_SECONDS:
			
			double totalCpuSecs = 0;
			double idleCpuSecs = 0;
			
			// idle
			metricDef = findMatchingMetricDefinition(oCpu.getEntityInstance().getMetricDefinitions(), COP_CPU_SECS_IDLE);
			numDataPoint = findNumberDataPointForCpuSecsMetric(oCpu, "idle", metric.getSum().getDataPointsList());
			if (numDataPoint != null) {
				updateDataPointForMetric(oCpu.getEntityInstance(), numDataPoint, metricDef, MetricDefinition.UPDOWN_DOUBLE);
				metricDef.getDataPoints().get(0);
				totalCpuSecs = totalCpuSecs + metricDef.getDataPoints().get(0).getDoubleVal();
				idleCpuSecs = metricDef.getDataPoints().get(0).getDoubleVal();
			}
			// iowait
			metricDef = findMatchingMetricDefinition(oCpu.getEntityInstance().getMetricDefinitions(), COP_CPU_SECS_IOWAIT);
			numDataPoint = findNumberDataPointForCpuSecsMetric(oCpu, "iowait", metric.getSum().getDataPointsList());
			if (numDataPoint != null) {
				updateDataPointForMetric(oCpu.getEntityInstance(), numDataPoint, metricDef, MetricDefinition.UPDOWN_DOUBLE);
				metricDef.getDataPoints().get(0);
				totalCpuSecs = totalCpuSecs + metricDef.getDataPoints().get(0).getDoubleVal();
			}
			// irq
			metricDef = findMatchingMetricDefinition(oCpu.getEntityInstance().getMetricDefinitions(), COP_CPU_SECS_IRQ);
			numDataPoint = findNumberDataPointForCpuSecsMetric(oCpu, "irq", metric.getSum().getDataPointsList());
			if (numDataPoint != null) {
				updateDataPointForMetric(oCpu.getEntityInstance(), numDataPoint, metricDef, MetricDefinition.UPDOWN_DOUBLE);
				metricDef.getDataPoints().get(0);
				totalCpuSecs = totalCpuSecs + metricDef.getDataPoints().get(0).getDoubleVal();
			}
			// nice
			metricDef = findMatchingMetricDefinition(oCpu.getEntityInstance().getMetricDefinitions(), COP_CPU_SECS_NICE);
			numDataPoint = findNumberDataPointForCpuSecsMetric(oCpu, "nice", metric.getSum().getDataPointsList());
			if (numDataPoint != null) {
				updateDataPointForMetric(oCpu.getEntityInstance(), numDataPoint, metricDef, MetricDefinition.UPDOWN_DOUBLE);
				metricDef.getDataPoints().get(0);
				totalCpuSecs = totalCpuSecs + metricDef.getDataPoints().get(0).getDoubleVal();
			}
			// softirq
			metricDef = findMatchingMetricDefinition(oCpu.getEntityInstance().getMetricDefinitions(), COP_CPU_SECS_SOFTIRQ);
			numDataPoint = findNumberDataPointForCpuSecsMetric(oCpu, "softirq", metric.getSum().getDataPointsList());
			if (numDataPoint != null) {
				updateDataPointForMetric(oCpu.getEntityInstance(), numDataPoint, metricDef, MetricDefinition.UPDOWN_DOUBLE);
				metricDef.getDataPoints().get(0);
				totalCpuSecs = totalCpuSecs + metricDef.getDataPoints().get(0).getDoubleVal();
			}
			// steal
			metricDef = findMatchingMetricDefinition(oCpu.getEntityInstance().getMetricDefinitions(), COP_CPU_SECS_STEAL);
			numDataPoint = findNumberDataPointForCpuSecsMetric(oCpu, "steal", metric.getSum().getDataPointsList());
			if (numDataPoint != null) {
				updateDataPointForMetric(oCpu.getEntityInstance(), numDataPoint, metricDef, MetricDefinition.UPDOWN_DOUBLE);
				metricDef.getDataPoints().get(0);
				totalCpuSecs = totalCpuSecs + metricDef.getDataPoints().get(0).getDoubleVal();
			}
			// system
			metricDef = findMatchingMetricDefinition(oCpu.getEntityInstance().getMetricDefinitions(), COP_CPU_SECS_SYSTEM);
			numDataPoint = findNumberDataPointForCpuSecsMetric(oCpu, "system", metric.getSum().getDataPointsList());
			if (numDataPoint != null) {
				updateDataPointForMetric(oCpu.getEntityInstance(), numDataPoint, metricDef, MetricDefinition.UPDOWN_DOUBLE);
				metricDef.getDataPoints().get(0);
				totalCpuSecs = totalCpuSecs + metricDef.getDataPoints().get(0).getDoubleVal();
			}			
			// user
			metricDef = findMatchingMetricDefinition(oCpu.getEntityInstance().getMetricDefinitions(), COP_CPU_SECS_USER);
			numDataPoint = findNumberDataPointForCpuSecsMetric(oCpu, "user", metric.getSum().getDataPointsList());
			if (numDataPoint != null) {
				updateDataPointForMetric(oCpu.getEntityInstance(), numDataPoint, metricDef, MetricDefinition.UPDOWN_DOUBLE);
				metricDef.getDataPoints().get(0);
				totalCpuSecs = totalCpuSecs + metricDef.getDataPoints().get(0).getDoubleVal();
			}
			
			// cpu percent used
			metricDef = findMatchingMetricDefinition(oCpu.getEntityInstance().getMetricDefinitions(), COP_CPU_PERC_USED);
			
			double cpuPercUsed = 0;
			if (totalCpuSecs > 0 && idleCpuSecs > 0) {
				cpuPercUsed = NumberUtils.calculatePercentUsed(totalCpuSecs, idleCpuSecs);
			}
			metricDef.getDataPoints().get(0).setDoubleVal(cpuPercUsed);
			
			break;

		default:
			break;
		}
	}
	
	
	private static void createDefinitionsForMetric(final OagCpu oCpu, final Metric metric) {
		
		String srcMetName = metric.getName();
		
		MetricDefinition metricDef = null;
		MetricDataPoint metricDp = null;
		NumberDataPoint numDataPoint = null;
		
		switch (srcMetName) {
		
		case OAG_CPU_SECONDS:
			
			double totalCpuSecs = 0;
			double idleCpuSecs = 0;
			
			// idle
			numDataPoint = findNumberDataPointForCpuSecsMetric(oCpu, "idle", metric.getSum().getDataPointsList());
			if (numDataPoint != null) {
				metricDef = createDefinitionForMetric(oCpu.getEntityInstance(), 
						srcMetName,
						MetricDefinition.UPDOWN_DOUBLE,
						COP_CPU_SECS_IDLE,
						MetricDefinition.GAUGE_DOUBLE,
						metric.getDescription());
				metricDp = createMetricDataPoint(metricDef, numDataPoint, MetricDefinition.UPDOWN_DOUBLE, MetricDefinition.GAUGE_DOUBLE);
				totalCpuSecs = totalCpuSecs + metricDp.getDoubleVal();
				idleCpuSecs = metricDp.getDoubleVal();
			}
			
			// iowait
			numDataPoint = findNumberDataPointForCpuSecsMetric(oCpu, "iowait", metric.getSum().getDataPointsList());
			if (numDataPoint != null) {
				metricDef = createDefinitionForMetric(oCpu.getEntityInstance(), 
						srcMetName,
						MetricDefinition.UPDOWN_DOUBLE,
						COP_CPU_SECS_IOWAIT,
						MetricDefinition.GAUGE_DOUBLE,
						metric.getDescription());
				metricDp = createMetricDataPoint(metricDef, numDataPoint, MetricDefinition.UPDOWN_DOUBLE, MetricDefinition.GAUGE_DOUBLE);
				totalCpuSecs = totalCpuSecs + metricDp.getDoubleVal();
			}
			
			// irq
			numDataPoint = findNumberDataPointForCpuSecsMetric(oCpu, "irq", metric.getSum().getDataPointsList());
			if (numDataPoint != null) {
				metricDef = createDefinitionForMetric(oCpu.getEntityInstance(), 
						srcMetName,
						MetricDefinition.UPDOWN_DOUBLE,
						COP_CPU_SECS_IRQ,
						MetricDefinition.GAUGE_DOUBLE,
						metric.getDescription());
				metricDp = createMetricDataPoint(metricDef, numDataPoint, MetricDefinition.UPDOWN_DOUBLE, MetricDefinition.GAUGE_DOUBLE);
				totalCpuSecs = totalCpuSecs + metricDp.getDoubleVal();
			}
			
			// nice
			numDataPoint = findNumberDataPointForCpuSecsMetric(oCpu, "nice", metric.getSum().getDataPointsList());
			if (numDataPoint != null) {
				metricDef = createDefinitionForMetric(oCpu.getEntityInstance(), 
						srcMetName,
						MetricDefinition.UPDOWN_DOUBLE,
						COP_CPU_SECS_NICE,
						MetricDefinition.GAUGE_DOUBLE,
						metric.getDescription());
				metricDp = createMetricDataPoint(metricDef, numDataPoint, MetricDefinition.UPDOWN_DOUBLE, MetricDefinition.GAUGE_DOUBLE);
				totalCpuSecs = totalCpuSecs + metricDp.getDoubleVal();
			}			
			
			// softirq
			numDataPoint = findNumberDataPointForCpuSecsMetric(oCpu, "softirq", metric.getSum().getDataPointsList());
			if (numDataPoint != null) {
				metricDef = createDefinitionForMetric(oCpu.getEntityInstance(), 
						srcMetName,
						MetricDefinition.UPDOWN_DOUBLE,
						COP_CPU_SECS_SOFTIRQ,
						MetricDefinition.GAUGE_DOUBLE,
						metric.getDescription());
				metricDp = createMetricDataPoint(metricDef, numDataPoint, MetricDefinition.UPDOWN_DOUBLE, MetricDefinition.GAUGE_DOUBLE);
				totalCpuSecs = totalCpuSecs + metricDp.getDoubleVal();
			}			
			
			// steal
			numDataPoint = findNumberDataPointForCpuSecsMetric(oCpu, "steal", metric.getSum().getDataPointsList());
			if (numDataPoint != null) {
				metricDef = createDefinitionForMetric(oCpu.getEntityInstance(), 
						srcMetName,
						MetricDefinition.UPDOWN_DOUBLE,
						COP_CPU_SECS_STEAL,
						MetricDefinition.GAUGE_DOUBLE,
						metric.getDescription());
				metricDp = createMetricDataPoint(metricDef, numDataPoint, MetricDefinition.UPDOWN_DOUBLE, MetricDefinition.GAUGE_DOUBLE);
				totalCpuSecs = totalCpuSecs + metricDp.getDoubleVal();
			}			
			
			// system
			numDataPoint = findNumberDataPointForCpuSecsMetric(oCpu, "system", metric.getSum().getDataPointsList());
			if (numDataPoint != null) {
				metricDef = createDefinitionForMetric(oCpu.getEntityInstance(), 
						srcMetName,
						MetricDefinition.UPDOWN_DOUBLE,
						COP_CPU_SECS_SYSTEM,
						MetricDefinition.GAUGE_DOUBLE,
						metric.getDescription());
				metricDp = createMetricDataPoint(metricDef, numDataPoint, MetricDefinition.UPDOWN_DOUBLE, MetricDefinition.GAUGE_DOUBLE);
				totalCpuSecs = totalCpuSecs + metricDp.getDoubleVal();
			}			
			
			// user
			numDataPoint = findNumberDataPointForCpuSecsMetric(oCpu, "user", metric.getSum().getDataPointsList());
			if (numDataPoint != null) {
				metricDef = createDefinitionForMetric(oCpu.getEntityInstance(), 
						srcMetName,
						MetricDefinition.UPDOWN_DOUBLE,
						COP_CPU_SECS_USER,
						MetricDefinition.GAUGE_DOUBLE,
						metric.getDescription());
				metricDp = createMetricDataPoint(metricDef, numDataPoint, MetricDefinition.UPDOWN_DOUBLE, MetricDefinition.GAUGE_DOUBLE);
				totalCpuSecs = totalCpuSecs + metricDp.getDoubleVal();
			}					
			
			// cpu percent used
			metricDef = createDefinitionForMetric(oCpu.getEntityInstance(), 
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_CPU_PERC_USED,
					MetricDefinition.GAUGE_DOUBLE,
					"Percent of CPU utilized.");
			
			double cpuPercUsed = 0;
			if (totalCpuSecs > 0 && idleCpuSecs > 0) {
				cpuPercUsed = NumberUtils.calculatePercentUsed(totalCpuSecs, idleCpuSecs);
			}
			metricDp = createDoubleMetricDataPoint(metricDef, cpuPercUsed);			
			
			
			
			
			break;
			
		case OAG_CPU_LOAD_1:
			
			break;
						
		case OAG_CPU_LOAD_5:
			
			break;
						
		case OAG_CPU_LOAD_15:
			
			break;
						

		default:
			break;
		}
		
	}	
	
	
	private static NumberDataPoint findNumberDataPointForCpuSecsMetric(final OagCpu oCpu, final String cpuSecsMode, final List<NumberDataPoint> numDataPoints) {
		
		for (NumberDataPoint ndp : numDataPoints) {
			
			List<KeyValue> kvList = ndp.getAttributesList();
			String cpuId = null;
			String mode = null;
			
			for (KeyValue kv : kvList) {
				if (kv.getKey().equals(OAG_CPU_SECONDS_ATTR_CPU)) {
					cpuId = kv.getValue().getStringValue();
				} else if (kv.getKey().equals(OAG_CPU_SECONDS_ATTR_MODE)) {
					mode = kv.getValue().getStringValue();
				}
			}
			
			if (oCpu.getCpuId().equals(cpuId)) {
				if (cpuSecsMode.equals(mode)) {
					return ndp;
				}
			}
			
		}
		
		return null;
	}
	
	public EntityInstance getEntityInstance() {
		return this.entityInst;
	}

	private static void log(String msg, boolean isInfo) {
		if (isInfo) {
			//lgr.info(msg);
		} else {
			//lgr.info(msg);
		}
	}
	
	public String getClusterName() {
		return this.node.getClusterName();
	}
	
	public String getHostName() {
		return this.node.getHostName();
	}
	
	public String getIpAddress() {
		return this.node.getIpAddress();
	}

	public String getCpuId() {
		return cpuId;
	}

	public boolean equalsObject(OagNode obj, String cpu) {
		
		if (obj != null) {
			try {
				if (obj.getClusterName().equals(this.getClusterName())) {
					if (obj.getHostName().equals(this.getHostName())) {
						if (obj.getIpAddress().equals(this.getIpAddress())) {
							if (this.getCpuId().equals(cpu)) {
								return true;
							}
						}
					}
				}
			} catch (Exception e) {
				// do nothing
			}
			
		}
		return false;
	}	
}
