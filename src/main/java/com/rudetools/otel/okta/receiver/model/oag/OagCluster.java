/**
 * 
 */
package com.rudetools.otel.okta.receiver.model.oag;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rudetools.otel.okta.receiver.ApplicationConstants;
import com.rudetools.otel.okta.receiver.ApplicationCtx;
import com.rudetools.otel.okta.receiver.config.OagClusterConfig;
import com.rudetools.otel.okta.receiver.config.OagNodeConfig;
import com.rudetools.otel.okta.receiver.model.otlp.EntityInstance;
import com.rudetools.otel.okta.receiver.model.otlp.MetricDefinition;
import com.rudetools.otel.okta.receiver.model.otlp.OtlpEntityThread;
import com.rudetools.otel.okta.receiver.utils.AnyVal;

import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.ResourceMetrics;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.semconv.ResourceAttributes;

/**
 * @author james101
 *
 */
public class OagCluster extends OagEntity implements ApplicationConstants {

	public static final Logger lgr = LoggerFactory.getLogger(OagCluster.class);
	public static final String ENTITY_TYPE_NAME = "oag_cluster";
	
	private final OagClusterConfig config;
	private List<OagNode> nodes = new ArrayList<OagNode>();
	
	private EntityInstance entityInst = null;
	
	protected List<Metric> clusterNodeMetrics = null;
	
	
	/**
	 * 
	 */
	public OagCluster(OagClusterConfig clusterConfig) throws Throwable {
		super();
		
		this.config = clusterConfig;
		
		lgr.info("");
		lgr.info(" -- Creating OAG Cluster: " + this.config.getClusterName());
		lgr.info("");
		
		List<OagNodeConfig> nodesConf = config.getOagNodes();
		
		// create a new node entity for each configured node
		for (OagNodeConfig nconf : nodesConf) {
			nodes.add(new OagNode(nconf, this));
		}
		
		
		
	}

	public synchronized void processRequest(List<ResourceMetrics> resMetrics) throws Throwable {
		
		if (hasMatchingNode(this, resMetrics)) {
			
			lgr.info(" ---------- Found matching node for cluster : " + this.getClusterName());
			lgr.info("");
			

			
			if (this.entityInst == null) {
				
				Map<String, AnyVal> resAttrs;
				OtlpEntityThread entityThread;
				Thread tempThread;
				
				
				resAttrs = new HashMap<String, AnyVal>();
				resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + ENTITY_TYPE_NAME + "." + "cluster_name", new AnyVal(this.getClusterName()));	
				resAttrs.put(ENTITY_ATTR_SVCNAME, new AnyVal(COPSOL_SVCNAME));
				resAttrs.put(ResourceAttributes.TELEMETRY_SDK_NAME.getKey(), new AnyVal(ApplicationCtx.SRVC_CONF.getCopSolutionName()));
				
				
				//this.entityInst = new EntityInstance(resAttrs, "okta-oag-cluster_" + this.getClusterName());
				
				this.entityInst = new EntityInstance(resAttrs, ApplicationCtx.SRVC_CONF.getCopSolutionName());
				
				
				MetricDefinition metricDef;
				//MetricDataPoint metricDp;
				
				metricDef = createMetricDefinition(this.entityInst, 
						"oag_cluster_heartbeat", 
						MetricDefinition.GAUGE_LONG, 
						"oag_cluster_heartbeat", 
						"Heartbeat for OAG Cluster.");
				
				// cause an error
				//createDoubleMetricDataPoint(metricDef, 1);
				
				// this is correct, as it should be, no errors
				createLongMetricDataPoint(metricDef, 1);
				
				
				entityThread = new OtlpEntityThread(entityInst, "OAG_CLUSTER : " + this.getClusterName());
				tempThread = new Thread(entityThread, "OTLP_OAG_CLUSTER : " + this.getClusterName());
				
				
				ApplicationCtx.ENTITY_THREADS.add(tempThread);
				
			} 

			
			for (OagNode oNode : this.getNodes()) {
				oNode.processRequest(resMetrics);
			}
			
			
			for (OagNode oNode : this.getNodes()) {
				oNode.processNodeMetricsFromCluster(this.clusterNodeMetrics);
			}

			
			// start any entity threads no yet started
			for (Thread tmpThd : ApplicationCtx.ENTITY_THREADS) {	
				
				if (tmpThd.getState().equals(State.NEW)) {
					
					tmpThd.start();
				}
			}

			
		} else {
			lgr.info(" ---------- No matching node found for cluster : " + this.getClusterName());
			lgr.info("");
		}
		
		
		
	}
	
	
	
	public String getClusterName() {
		return config.getClusterName();
	}

	public List<OagNode> getNodes() {
		return nodes;
	}
	
	private static boolean hasMatchingNode(final OagCluster cluster, final List<ResourceMetrics> resMetrics) {
		
		for (ResourceMetrics rm : resMetrics) {
			Resource res = rm.getResource();
			
			if (res != null && res.getAttributesList() != null && res.getAttributesList().size() > 0) {
				
				List<KeyValue> attrs = res.getAttributesList();
				
				for (KeyValue kv : attrs) {
					
					if (kv.getValue().hasStringValue()) {
						
						String key = kv.getKey();
						if (key != null && key.equals(ENTITY_ATTR_NET_HOSTNAME)) {
							
							String hostName = kv.getValue().getStringValue();
							
							List<OagNode> cnodes = cluster.getNodes();
							
							for (OagNode oagNode : cnodes) {
								
								if (oagNode.getIpAddress().equals(hostName)) {
									
									//lgr.info(" ---------- Found matching node from payload : " + hostName + " : for cluster : " + cluster.getClusterName());
									//lgr.info("");
									return true;
								}
							}
							
						}
					}
					
				}
				
				
				//lgr.info("");
			}

			
		}
		
		return false;
		
	}

	public EntityInstance getEntityInstance() {
		return this.entityInst;
	}
	
}
