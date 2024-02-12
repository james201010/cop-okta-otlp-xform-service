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

import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;

/**
 * @author james101
 *
 */
public class OagNetwork extends OagEntity implements ApplicationConstants {

	public static final Logger lgr = LoggerFactory.getLogger(OagNetwork.class);
	public static final String ENTITY_TYPE_NAME = "oag_nic";
	
	private final OagNode node;
	private final EntityInstance entityInst;
	private final String deviceName;
	
	/**
	 * 
	 */
	public OagNetwork(EntityInstance einst, OagNode oagNode, String deviceName) {
		super();
		this.entityInst = einst;
		this.node = oagNode;
		this.deviceName = deviceName;
		log("OagNetwork Constructor for :", false);
		log(" ---> cluster        = " + this.node.getClusterName(), false);
		log(" ---> hostname       = " + this.node.getHostName(), false);
		log(" ---> ipaddress      = " + this.node.getIpAddress(), false);
		log("", false);				
	}

	public void processRequest(List<Metric> metrics) throws Throwable {
		
		// 3 source metrics turns into 3
		if (metrics != null && metrics.size() > 0) {
		
			for (Metric met : metrics) {
				
				if (metricDefinitionExists(met.getName(), this.entityInst.getMetricDefinitions())) {
					updateDefinitionsForMetric(this, met);
				} else {
					createDefinitionsForMetric(this, met);
				}
				
			}

		}		
		
	}
	private static void updateDefinitionsForMetric(final OagNetwork oNet, final Metric metric) {
		
		String srcMetName = metric.getName();
		MetricDefinition metricDef = null;
		
		switch (srcMetName) {
		
		case OAG_NET_RECEIVE_DROP:
			
			metricDef = findMatchingMetricDefinition(oNet.getEntityInstance().getMetricDefinitions(), COP_NET_RECEIVE_DROP);
			updateDataPointForMetric(oNet.getEntityInstance(), metricDef, MetricDefinition.UPDOWN_DOUBLE, metric);

			break;
			
		case OAG_NET_RECEIVE_ERRS:

			metricDef = findMatchingMetricDefinition(oNet.getEntityInstance().getMetricDefinitions(), COP_NET_RECEIVE_ERRS);
			updateDataPointForMetric(oNet.getEntityInstance(), metricDef, MetricDefinition.UPDOWN_DOUBLE, metric);
			
			break;
			
		case OAG_NET_RECEIVE_BYTES:
			
			metricDef = findMatchingMetricDefinition(oNet.getEntityInstance().getMetricDefinitions(), COP_NET_RECEIVE_BYTES);
			updateDataPointForMetric(oNet.getEntityInstance(), metricDef, MetricDefinition.UPDOWN_DOUBLE, metric);
			
			break;

		default:
			break;
		}
	}
	
	
	private static void createDefinitionsForMetric(final OagNetwork oNet, final Metric metric) {
		
		String srcMetName = metric.getName();
		
		MetricDefinition metricDef = null;
		NumberDataPoint numDataPoint = null;
		
		switch (srcMetName) {
		
		case OAG_NET_RECEIVE_DROP:
			
			numDataPoint = findNumberDataPointForNic(oNet, metric.getSum().getDataPointsList());
			
			metricDef = createDefinitionForMetric(oNet.getEntityInstance(), 
					srcMetName,
					MetricDefinition.UPDOWN_DOUBLE,
					COP_NET_RECEIVE_DROP,
					MetricDefinition.GAUGE_DOUBLE,
					metric.getDescription());
			createMetricDataPoint(metricDef, numDataPoint, MetricDefinition.UPDOWN_DOUBLE, MetricDefinition.GAUGE_DOUBLE);

			break;

		case OAG_NET_RECEIVE_ERRS:
			
			numDataPoint = findNumberDataPointForNic(oNet, metric.getSum().getDataPointsList());
			
			metricDef = createDefinitionForMetric(oNet.getEntityInstance(), 
					srcMetName,
					MetricDefinition.UPDOWN_DOUBLE,
					COP_NET_RECEIVE_ERRS,
					MetricDefinition.GAUGE_DOUBLE,
					metric.getDescription());
			createMetricDataPoint(metricDef, numDataPoint, MetricDefinition.UPDOWN_DOUBLE, MetricDefinition.GAUGE_DOUBLE);
			
			break;

		case OAG_NET_RECEIVE_BYTES:
			
			numDataPoint = findNumberDataPointForNic(oNet, metric.getSum().getDataPointsList());
			
			metricDef = createDefinitionForMetric(oNet.getEntityInstance(), 
					srcMetName,
					MetricDefinition.UPDOWN_DOUBLE,
					COP_NET_RECEIVE_BYTES,
					MetricDefinition.GAUGE_DOUBLE,
					metric.getDescription());
			createMetricDataPoint(metricDef, numDataPoint, MetricDefinition.UPDOWN_DOUBLE, MetricDefinition.GAUGE_DOUBLE);
			
			break;

		default:
			break;
		}
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

	public String getDeviceName() {
		return deviceName;
	}
	
	private static NumberDataPoint findNumberDataPointForNic(final OagNetwork oNet, final List<NumberDataPoint> numDataPoints) {
		
		for (NumberDataPoint ndp : numDataPoints) {
			
			List<KeyValue> kvList = ndp.getAttributesList();
			String nicName = null;
			
			
			for (KeyValue kv : kvList) {
				if (kv.getKey().equals(OAG_NET_ATTR_DEVICE)) {
					nicName = kv.getValue().getStringValue();
				}
			}
			
			if (oNet.getDeviceName().equals(nicName)) {
				return ndp;
			}
			
		}
		
		return null;

	}
	
	public boolean equalsObject(OagNode obj, String dDeviceName) {
		
		if (obj != null) {
			try {
				if (obj.getClusterName().equals(this.getClusterName())) {
					if (obj.getHostName().equals(this.getHostName())) {
						if (obj.getIpAddress().equals(this.getIpAddress())) {
							if (this.getDeviceName().equals(dDeviceName)) {
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
