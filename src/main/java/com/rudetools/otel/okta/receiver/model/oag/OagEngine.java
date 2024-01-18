/**
 * 
 */
package com.rudetools.otel.okta.receiver.model.oag;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rudetools.otel.okta.receiver.ApplicationConstants;
import com.rudetools.otel.okta.receiver.model.otlp.EntityInstance;
import com.rudetools.otel.okta.receiver.model.otlp.MetricDefinition;

import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;

/**
 * @author james101
 *
 */
public class OagEngine extends OagEntity implements ApplicationConstants {

	public static final Logger lgr = LoggerFactory.getLogger(OagEngine.class);
	public static final String ENTITY_TYPE_NAME = "oag_engine";
	
	private final OagNode node;
	private final EntityInstance entityInst;
	
	/**
	 * 
	 */
	public OagEngine(EntityInstance einst, OagNode oagNode) {
		super();
		this.entityInst = einst;
		this.node = oagNode;
	}
	
	
	public void processRequest(List<Metric> metrics) throws Throwable {
		
		// 7 source metrics turns into 10
		if (metrics != null && metrics.size() > 0) {
		
			//if (this.entityInst.getMetricDefinitions() == null) {
				
			for (Metric met : metrics) {
				
				if (metricDefinitionExists(met.getName(), this.entityInst.getMetricDefinitions())) {
					updateDefinitionsForMetric(this.entityInst, met);
				} else {
					createDefinitionsForMetric(this.entityInst, met);
				}
				
			}
					
			//}

		}
		
	}

	private static void createDefinitionsForMetric(final EntityInstance entity, final Metric metric) {
		
		String srcMetName = metric.getName();
		
		switch (srcMetName) {
		
		case OAG_ENGINE_ACTIVE_CONNS:
			
			createDefinitionAndDataPointForMetric(entity, 
					getNumberDataPointForMetricType(metric),
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_ENGINE_ACTIVE_CONNS,
					MetricDefinition.GAUGE_LONG,
					metric.getDescription());
			break;

		case OAG_ENGINE_HANDLED_CONNS:
			
			createDefinitionAndDataPointForMetric(entity, 
					getNumberDataPointForMetricType(metric),
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_ENGINE_HANDLED_CONNS,
					MetricDefinition.GAUGE_LONG,
					metric.getDescription());			
			break;

		case OAG_ENGINE_TOTAL_REQS_NUM:
			
			createDefinitionAndDataPointForMetric(entity, 
					getNumberDataPointForMetricType(metric),
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_ENGINE_TOTAL_REQS_NUM,
					MetricDefinition.GAUGE_LONG,
					metric.getDescription());			
			break;

		case OAG_ENGINE_WAITING_NUM:
			
			createDefinitionAndDataPointForMetric(entity, 
					getNumberDataPointForMetricType(metric),
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_ENGINE_WAITING_NUM,
					MetricDefinition.GAUGE_LONG,
					metric.getDescription());			
			break;

		case OAG_ENGINE_ACCEPTED_CONNS:
			
			createDefinitionAndDataPointForMetric(entity, 
					getNumberDataPointForMetricType(metric),
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_ENGINE_ACCEPTED_CONNS,
					MetricDefinition.GAUGE_LONG,
					metric.getDescription());			
			break;
			
		case OAG_ENGINE_METRIC_HANDLER_REQS_TOTAL:
			
			List<NumberDataPoint> ndpList1 = metric.getSum().getDataPointsList();
			
			for (NumberDataPoint ndp : ndpList1) {
				
				List<KeyValue> kvList = ndp.getAttributesList();
				for (KeyValue kv : kvList) {
					if (kv.getKey().equals(OAG_ENGINE_METRIC_HANDLER_REQS_TOTAL_ATTR_CODE)) {
						String attrStr = kv.getValue().getStringValue();
						
						if (attrStr.equals("200")) {
							createDefinitionAndDataPointForMetric(entity, ndp, srcMetName,
									MetricDefinition.UPDOWN_DOUBLE,
									COP_ENGINE_METRIC_HANDLER_REQS_200,
									MetricDefinition.GAUGE_LONG,
									metric.getDescription());			

						} else if (attrStr.equals("500")) {
							createDefinitionAndDataPointForMetric(entity, ndp, srcMetName,
									MetricDefinition.UPDOWN_DOUBLE,
									COP_ENGINE_METRIC_HANDLER_REQS_500,
									MetricDefinition.GAUGE_LONG,
									metric.getDescription());			
							
						} else if (attrStr.equals("503")) {
							createDefinitionAndDataPointForMetric(entity, ndp, srcMetName,
									MetricDefinition.UPDOWN_DOUBLE,
									COP_ENGINE_METRIC_HANDLER_REQS_503,
									MetricDefinition.GAUGE_LONG,
									metric.getDescription());			

						}
					}
				}
			}
			
			break;

			
		case OAG_ENGINE_METRIC_HANDLER_ERRS:
			
			List<NumberDataPoint> ndpList2 = metric.getSum().getDataPointsList();
			
			for (NumberDataPoint ndp : ndpList2) {
				
				List<KeyValue> kvList = ndp.getAttributesList();
				for (KeyValue kv : kvList) {
					if (kv.getKey().equals(OAG_ENGINE_METRIC_HANDLER_ERRORS_ATTR_CAUSE)) {
						String attrStr = kv.getValue().getStringValue();
						
						if (attrStr.equals("encoding")) {
							createDefinitionAndDataPointForMetric(entity, ndp, srcMetName,
									MetricDefinition.UPDOWN_DOUBLE,
									COP_ENGINE_METRIC_HANDLER_ERRS_ENCODE,
									MetricDefinition.GAUGE_LONG,
									metric.getDescription());			

						} else if (attrStr.equals("gathering")) {
							createDefinitionAndDataPointForMetric(entity, ndp, srcMetName,
									MetricDefinition.UPDOWN_DOUBLE,
									COP_ENGINE_METRIC_HANDLER_ERRS_GATHER,
									MetricDefinition.GAUGE_LONG,
									metric.getDescription());			

						}
					}
				}
			}			
			
			break;
			
			
		default:
			break;
		}
		
	}	
		
	
	private static void updateDefinitionsForMetric(final EntityInstance entity, final Metric metric) {
		
		String srcMetName = metric.getName();
		
		List<MetricDefinition> metDefs = findMatchingMetricDefinitions(entity, metric);
		
		switch (srcMetName) {
		case OAG_ENGINE_ACTIVE_CONNS:
			
			updateDataPointForMetric(entity, metDefs.get(0), MetricDefinition.GAUGE_DOUBLE, metric);
			break;
			
		case OAG_ENGINE_HANDLED_CONNS:
			
			updateDataPointForMetric(entity, metDefs.get(0), MetricDefinition.GAUGE_DOUBLE, metric);
			break;
			
		case OAG_ENGINE_TOTAL_REQS_NUM:
			
			updateDataPointForMetric(entity, metDefs.get(0), MetricDefinition.GAUGE_DOUBLE, metric);
			break;

		case OAG_ENGINE_WAITING_NUM:
			
			updateDataPointForMetric(entity, metDefs.get(0), MetricDefinition.GAUGE_DOUBLE, metric);
			break;

		case OAG_ENGINE_ACCEPTED_CONNS:
			
			updateDataPointForMetric(entity, metDefs.get(0), MetricDefinition.GAUGE_DOUBLE, metric);
			break;
			
		case OAG_ENGINE_METRIC_HANDLER_REQS_TOTAL:
			
			List<NumberDataPoint> ndpList1 = metric.getSum().getDataPointsList();
			
			for (NumberDataPoint ndp : ndpList1) {
				
				List<KeyValue> kvList = ndp.getAttributesList();
				for (KeyValue kv : kvList) {
					if (kv.getKey().equals(OAG_ENGINE_METRIC_HANDLER_REQS_TOTAL_ATTR_CODE)) {
						String attrStr = kv.getValue().getStringValue();
						
						if (attrStr.equals("200")) {
							updateDataPointForMetric(entity, ndp, 
									findMatchingMetricDefinition(metDefs, COP_ENGINE_METRIC_HANDLER_REQS_200), 
									MetricDefinition.UPDOWN_DOUBLE);
							
						} else if (attrStr.equals("500")) {
							updateDataPointForMetric(entity, ndp, 
									findMatchingMetricDefinition(metDefs, COP_ENGINE_METRIC_HANDLER_REQS_500), 
									MetricDefinition.UPDOWN_DOUBLE);

						} else if (attrStr.equals("503")) {
							updateDataPointForMetric(entity, ndp, 
									findMatchingMetricDefinition(metDefs, COP_ENGINE_METRIC_HANDLER_REQS_503), 
									MetricDefinition.UPDOWN_DOUBLE);

						}
					}
				}
			}			
			
			break;

		case OAG_ENGINE_METRIC_HANDLER_ERRS:
			
			List<NumberDataPoint> ndpList2 = metric.getSum().getDataPointsList();
			
			for (NumberDataPoint ndp : ndpList2) {
				
				List<KeyValue> kvList = ndp.getAttributesList();
				for (KeyValue kv : kvList) {
					if (kv.getKey().equals(OAG_ENGINE_METRIC_HANDLER_REQS_TOTAL_ATTR_CODE)) {
						String attrStr = kv.getValue().getStringValue();
						
						if (attrStr.equals("encoding")) {
							updateDataPointForMetric(entity, ndp, 
									findMatchingMetricDefinition(metDefs, COP_ENGINE_METRIC_HANDLER_ERRS_ENCODE), 
									MetricDefinition.UPDOWN_DOUBLE);
							
						} else if (attrStr.equals("gathering")) {
							updateDataPointForMetric(entity, ndp, 
									findMatchingMetricDefinition(metDefs, COP_ENGINE_METRIC_HANDLER_ERRS_GATHER), 
									MetricDefinition.UPDOWN_DOUBLE);

						}
					}
				}
			}			
			
			
			break;
			
		default:
			break;
		}
		
		
	}

	
	protected EntityInstance getEntityInstance() {
		return this.entityInst;
	}	
}
