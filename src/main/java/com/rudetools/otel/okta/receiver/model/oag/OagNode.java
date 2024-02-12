/**
 * 
 */
package com.rudetools.otel.okta.receiver.model.oag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rudetools.otel.okta.receiver.ApplicationConstants;
import com.rudetools.otel.okta.receiver.ApplicationCtx;
import com.rudetools.otel.okta.receiver.config.OagNodeConfig;
import com.rudetools.otel.okta.receiver.config.ServiceConfig;
import com.rudetools.otel.okta.receiver.model.otlp.EntityInstance;
import com.rudetools.otel.okta.receiver.model.otlp.MetricDefinition;
import com.rudetools.otel.okta.receiver.model.otlp.OtlpEntityThread;
import com.rudetools.otel.okta.receiver.utils.AnyVal;
import com.rudetools.otel.okta.receiver.utils.NumberUtils;
import com.rudetools.otel.okta.receiver.utils.StringUtils;

import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import io.opentelemetry.proto.metrics.v1.ResourceMetrics;
import io.opentelemetry.proto.metrics.v1.ScopeMetrics;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.semconv.ResourceAttributes;

/**
 * @author james101
 *
 */
public class OagNode extends OagEntity implements ApplicationConstants {

	public static final Logger lgr = LoggerFactory.getLogger(OagNode.class);
	
	public static final String ENTITY_TYPE_NAME = "oag_node";
	
	private final OagNodeConfig config;
	private final OagCluster cluster;
	private String serviceInstanceId;
	private String serviceName;
	private String netHostPort;
	private String httpScheme;
	
	private OagEngine oagEngine = null;
	private OagMemory oagMemory = null;
	private List<OagCpu> oagCpus = null;
	private List<OagDisk> oagDisks = null;
	private List<OagNetwork> oagNics = null;
	
	private EntityInstance entityInst = null;
	
	
	/**
	 * 
	 */
	public OagNode(OagNodeConfig nodeConfig, OagCluster cluster) {
		
		super();
		this.config = nodeConfig;
		this.cluster = cluster;
		
		lgr.info("");
		lgr.info(" -- Creating OAG Node:");
		lgr.info("    -- Host Name: " + this.getHostName());
		lgr.info("    -- IP Address: " + this.getIpAddress());
		lgr.info("    -- Is Master Node: " + this.isMasterNode());
		lgr.info("");		
	}

	public void processRequest(List<ResourceMetrics> resMetrics) throws Throwable {
	
		ResourceMetrics rm = findMatchingResourceMetricsForNode(this, resMetrics);
		
		if (rm != null) {
			//log(" *--------* Found matching node for cluster : " + this.getClusterName(), false);
			//log("", true);
		
			
			// 
			if (this.entityInst == null) {
				
				Map<String, AnyVal> resAttrs = null;;
				OtlpEntityThread entityThread;
				Thread tempThread;
				Resource resorc = rm.getResource();
				if (resorc != null && resorc.getAttributesList() != null && resorc.getAttributesList().size() > 0) {
				
					resAttrs = new HashMap<String, AnyVal>();
					
					for (KeyValue kv : resorc.getAttributesList()) {
						
						if (kv.getKey().equals(ENTITY_ATTR_SVCNAME)) {
							this.serviceName = kv.getValue().getStringValue();
							resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + ENTITY_TYPE_NAME + "." + "service_name", new AnyVal(this.serviceName));							
							
						} else if (kv.getKey().equals(ENTITY_ATTR_NET_HOSTNAME)) {
							resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + ENTITY_TYPE_NAME + "." + "ip_address", new AnyVal(this.getIpAddress()));
							
						} else if (kv.getKey().equals(ENTITY_ATTR_INSTID)) {
							this.serviceInstanceId = kv.getValue().getStringValue();
							resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + ENTITY_TYPE_NAME + "." + "service_instance_id", new AnyVal(this.serviceInstanceId));	
							
						} else if (kv.getKey().equals(ENTITY_ATTR_NET_HOSTPORT)) {
							this.netHostPort = kv.getValue().getStringValue();
							resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + ENTITY_TYPE_NAME + "." + "net_host_port", new AnyVal(this.netHostPort));	
							
						} else if (kv.getKey().equals(ENTITY_ATTR_HTTP_SCHEME)) {
							this.httpScheme = kv.getValue().getStringValue();
							resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + ENTITY_TYPE_NAME + "." + "http_scheme", new AnyVal(this.httpScheme));	
							
						}
						
					}
					
					resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + ENTITY_TYPE_NAME + "." + "host_name", new AnyVal(this.getHostName()));
					resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + ENTITY_TYPE_NAME + "." + "is_master_node", new AnyVal(this.config.isMasterNode()));
					resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + ENTITY_TYPE_NAME + "." + "cluster_name", new AnyVal(this.getClusterName()));
					
					resAttrs.put(ENTITY_ATTR_SVCNAME, new AnyVal(COPSOL_SVCNAME));
					resAttrs.put(ResourceAttributes.TELEMETRY_SDK_NAME.getKey(), new AnyVal(ApplicationCtx.SRVC_CONF.getCopSolutionName()));
					
					
					//this.entityInst = new EntityInstance(resAttrs, "okta-oag-node_" + this.getClusterName() + "_" + this.getIpAddress());
					this.entityInst = new EntityInstance(resAttrs, ApplicationCtx.SRVC_CONF.getCopSolutionName());
					
					entityThread = new OtlpEntityThread(this.entityInst, "OAG_NODE : " + this.getClusterName() + " : " + this.getIpAddress());
					tempThread = new Thread(entityThread, "OTLP_OAG_NODE : " + this.getClusterName() + " : " + this.getIpAddress());
					
					ApplicationCtx.ENTITY_THREADS.add(tempThread);

				}
				
				
				
				//resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + ENTITY_TYPE_NAME + "." + "number_of_cpus", new AnyVal(this.));
				

				processResourceMetrics(this, rm);
				
			} else {
				processResourceMetrics(this, rm);
			}
			
			
			
			
			
			
			
		} else {
			//lgr.info(" *--------* No matching node found for cluster : " + this.getClusterName());
			//lgr.info("");
		}
		
	}
	
	
	// here we are dealing with just the metrics that have been identified for the node passed in
	// we are grouping them into the different entities they belong to
	private static void processResourceMetrics(final OagNode node, final ResourceMetrics rm) throws Throwable {
		
		//Map<String, List<Metric>>
		//Map<String, List<Metric>> childMetrics = new HashMap<String, List<Metric>>();
		
		List<Metric> clusterMetrics = new ArrayList<Metric>();
		List<Metric> engineMetrics = new ArrayList<Metric>();
		List<Metric> diskMetrics = new ArrayList<Metric>();
		List<Metric> cpuMetrics = new ArrayList<Metric>();
		List<Metric> memoryMetrics = new ArrayList<Metric>();
		List<Metric> networkMetrics = new ArrayList<Metric>();
		
		List<ScopeMetrics> lsms = rm.getScopeMetricsList();
		if (lsms != null && lsms.size() > 0) {
			
			for (ScopeMetrics sms : lsms) {
				
				List<Metric> metrics = sms.getMetricsList();
				
				if (metrics != null && metrics.size() > 0) {
					
					for (Metric met : metrics) {
						
						log("Metric Name: " + met.getName(), false);
						
						if (met.getName().startsWith(OAG_CLUSTER_METRIC_PFX)) {
							if (met.getName().startsWith(OAG_CLUSTER_VALIDATION)) {
								clusterMetrics.add(met);
							} else if (met.getName().startsWith(OAG_CLUSTER_LAST_SYNC)) {
								clusterMetrics.add(met);
							}
							
						} else if (met.getName().startsWith(OAG_ENGINE_METRIC_PFX_1) ||  met.getName().startsWith(OAG_ENGINE_METRIC_PFX_2)) {
							if (OAG_ENGINE_METRIC_NAMES.contains(met.getName())) {
								engineMetrics.add(met);
							}
							
						} else if (met.getName().startsWith(OAG_DISK_METRIC_PFX)) {
							if (OAG_DISK_METRIC_NAMES.contains(met.getName())) {
								diskMetrics.add(met);
							}
							
						// } else if (met.getName().startsWith(OAG_CPU_METRIC_PFX_1) || met.getName().startsWith(OAG_CPU_METRIC_PFX_2)) {	
						} else if (met.getName().startsWith(OAG_CPU_METRIC_PFX_1)) {
							if (OAG_CPU_METRIC_NAMES.contains(met.getName())) {
								cpuMetrics.add(met);
							}
							
						} else if (met.getName().startsWith(OAG_MEMORY_METRIC_PFX)) {
							if (OAG_MEMORY_METRIC_NAMES.contains(met.getName())) {
								memoryMetrics.add(met);
							}
							
						} else if (met.getName().startsWith(OAG_NET_METRIC_PFX)) {
							if (OAG_NET_METRIC_NAMES.contains(met.getName())) {
								networkMetrics.add(met);
							}
							
						}

						
					}
					
					log("Cluster Metrics Size  =  " + clusterMetrics.size(), false);
					log("Engine Metrics Size   =  " + engineMetrics.size(), false);
					log("Disk Metrics Size     =  " + diskMetrics.size(), false);
					log("CPU Metrics Size      =  " + cpuMetrics.size(), false);
					log("Memory Metrics Size   =  " + memoryMetrics.size(), false);
					log("Network Metrics Size  =  " + networkMetrics.size(), false);
					

				}				
				
			}
			
//			childMetrics.put(OagEngine.ENTITY_TYPE_NAME, engineMetrics);
//			childMetrics.put(OagDisk.ENTITY_TYPE_NAME, diskMetrics);
//			childMetrics.put(OagCpu.ENTITY_TYPE_NAME, cpuMetrics);
//			childMetrics.put(OagMemory.ENTITY_TYPE_NAME, memoryMetrics);
//			childMetrics.put(OagDisk.ENTITY_TYPE_NAME, diskMetrics);
//			childMetrics.put(OagDisk.ENTITY_TYPE_NAME, diskMetrics);
			
			processEngineMetrics(node, engineMetrics);
			processDiskMetrics(node, diskMetrics);
			processCpuMetrics(node, cpuMetrics);
			processMemoryMetrics(node, memoryMetrics);
			processNetworkMetrics(node, networkMetrics);
			processClusterMetrics(node, clusterMetrics);
			
			
		}
		
		
		
	}
	
	private static void processNetworkMetrics(final OagNode node, final List<Metric> metrics) throws Throwable {
		
		if (metrics.size() > 0) { 
			
			if (node.oagNics == null) {

				Map<String, AnyVal> resAttrs;
				OtlpEntityThread entityThread;
				Thread tempThread;
				EntityInstance einst;
				
				resAttrs = new HashMap<String, AnyVal>();
				resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagNetwork.ENTITY_TYPE_NAME + "." + "host_name", new AnyVal(node.getHostName()));
				resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagNetwork.ENTITY_TYPE_NAME + "." + "ip_address", new AnyVal(node.getIpAddress()));
				
				resAttrs.put(ENTITY_ATTR_SVCNAME, new AnyVal(COPSOL_SVCNAME));
				resAttrs.put(ResourceAttributes.TELEMETRY_SDK_NAME.getKey(), new AnyVal(ApplicationCtx.SRVC_CONF.getCopSolutionName()));
				

				for (Metric met : metrics) {
					
					List<NumberDataPoint> ndpList = getNumberDataPointsForMetricType(met);
					String netName = null;
					
					for (NumberDataPoint ndp : ndpList) {
						
						List<KeyValue> kvList = ndp.getAttributesList();
						for (KeyValue kv : kvList) {
							
							if (kv.getKey().equals(OAG_NET_ATTR_DEVICE)) {
								netName = kv.getValue().getStringValue();
							}
							
							// here we are creating a new network if it has not already been created
							if (!node.hasNetwork(netName)) {
								
								Map<String, AnyVal> entityAttrs = new HashMap<String, AnyVal>();;
								
								entityAttrs.putAll(resAttrs);
								
								entityAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagNetwork.ENTITY_TYPE_NAME + "." + "device_name", new AnyVal(netName));
								
								einst = new EntityInstance(entityAttrs, ApplicationCtx.SRVC_CONF.getCopSolutionName());
								
								entityThread = new OtlpEntityThread(einst, "OAG_NIC : " + node.getClusterName() + " : " + node.getIpAddress() + " : " + node.getHostName() + " : " + netName);
								tempThread = new Thread(entityThread, "OTLP_OAG_NIC : " + node.getClusterName() + " : " + node.getIpAddress() + " : " + node.getHostName() + " : " + netName);
								
								node.addNetwork(new OagNetwork(einst, node, netName));
								
								ApplicationCtx.ENTITY_THREADS.add(tempThread);

								
							}
						}						
					}
				}
			}
			
			
		}
		
		if (node.oagNics != null && node.oagNics.size() > 0) {
			for (OagNetwork oagEntity : node.oagNics) {
				oagEntity.processRequest(metrics);
			}
		}

	}
	
	private static void processMemoryMetrics(final OagNode node, final List<Metric> metrics) throws Throwable {
		
		if (metrics.size() > 0) {
			
			if (node.oagMemory == null) {

				Map<String, AnyVal> resAttrs;
				OtlpEntityThread entityThread;
				Thread tempThread;
				EntityInstance einst;
				
				resAttrs = new HashMap<String, AnyVal>();
				resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagMemory.ENTITY_TYPE_NAME + "." + "host_name", new AnyVal(node.getHostName()));
				resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagMemory.ENTITY_TYPE_NAME + "." + "ip_address", new AnyVal(node.getIpAddress()));
				
				resAttrs.put(ENTITY_ATTR_SVCNAME, new AnyVal(COPSOL_SVCNAME));
				resAttrs.put(ResourceAttributes.TELEMETRY_SDK_NAME.getKey(), new AnyVal(ApplicationCtx.SRVC_CONF.getCopSolutionName()));

				einst = new EntityInstance(resAttrs, ApplicationCtx.SRVC_CONF.getCopSolutionName());
				
				entityThread = new OtlpEntityThread(einst, "OAG_MEMORY : " + node.getClusterName() + " : " + node.getIpAddress() + " : " + node.getHostName());
				tempThread = new Thread(entityThread, "OTLP_OAG_MEMORY : " + node.getClusterName() + " : " + node.getIpAddress() + " : " + node.getHostName());
				
				node.oagMemory = new OagMemory(einst, node);
				
				ApplicationCtx.ENTITY_THREADS.add(tempThread);

			}
			
			node.oagMemory.processRequest(metrics);
			
		}
	}	
	// format host name to match the stupid okta metric name format
	private static String xformHostName(final String hostName) {
		String tmp1 = StringUtils.replaceAll(hostName, ".", "_");
		return StringUtils.replaceAll(tmp1, "-", "_");
	}
	
	
	protected void processNodeMetricsFromCluster(final List<Metric> metrics) throws Throwable {
		
		if (!this.isMasterNode()) {
			if (this.getEntityInstance() != null) {
				
				if (this.getEntityInstance().getMetricDefinitions() == null) {
					for (Metric met : metrics) {
						createDefinitionsForMetric(this, met);
					}
					
				} else {
					for (Metric met : metrics) {
						updateDefinitionsForMetric(this, met);
					}
					
				}
			}
			
		} else {
			if (this.getEntityInstance() != null) {
				if (this.getEntityInstance().getMetricDefinitions() == null) {
					MetricDefinition metricDef = createDefinitionForMetric(this.getEntityInstance(), 
							COP_CLUSTER_VALIDATION,
							MetricDefinition.GAUGE_LONG,
							COP_CLUSTER_VALIDATION,
							MetricDefinition.GAUGE_LONG,
							"HA validation status : Not applicable for this master node.");
					createLongMetricDataPoint(metricDef, 0);

					metricDef = createDefinitionForMetric(this.getEntityInstance(), 
							COP_CLUSTER_LAST_SYNC,
							MetricDefinition.GAUGE_LONG,
							COP_CLUSTER_LAST_SYNC,
							MetricDefinition.GAUGE_LONG,
							"HA last sync status : Not applicable for this master node.");
					createLongMetricDataPoint(metricDef, 0);
				
				
				}
			}			
		}
		

		
	}
	
	
	private static void createDefinitionsForMetric(final OagNode oNode, final Metric metric) {
		String srcMetName = metric.getName();
		
		if (srcMetName.contains(xformHostName(oNode.getHostName()))) {
			
			if (srcMetName.startsWith(OAG_CLUSTER_VALIDATION)) {
				
				createDefinitionAndDataPointForMetric(oNode.getEntityInstance(), 
						getNumberDataPointForMetricType(metric),
						srcMetName,
						MetricDefinition.GAUGE_DOUBLE,
						COP_CLUSTER_VALIDATION,
						MetricDefinition.GAUGE_LONG,
						metric.getDescription());
				
			} else if (srcMetName.startsWith(OAG_CLUSTER_LAST_SYNC)) {
				
				MetricDefinition metricDef = createDefinitionForMetric(oNode.getEntityInstance(), 
						srcMetName,
						MetricDefinition.GAUGE_DOUBLE,
						COP_CLUSTER_LAST_SYNC,
						MetricDefinition.GAUGE_LONG,
						metric.getDescription());
				createLongMetricDataPoint(metricDef, 1);

			}
			
			
			
		}
	}
	
	private static void updateDefinitionsForMetric(final OagNode oNode, final Metric metric) {
		String srcMetName = metric.getName();
		
		if (srcMetName.contains(xformHostName(oNode.getHostName()))) {
			
			if (srcMetName.startsWith(OAG_CLUSTER_VALIDATION)) {
				MetricDefinition metricDef = findMatchingMetricDefinition(oNode.getEntityInstance().getMetricDefinitions(), COP_CLUSTER_VALIDATION);
				updateDataPointForMetric(oNode.getEntityInstance(), metricDef, MetricDefinition.GAUGE_DOUBLE, metric);
			}
			
		}
	}
	
	
	private static void processClusterMetrics(final OagNode node, final List<Metric> metrics) throws Throwable { 
		
		if (node.isMasterNode()) {
			if (metrics.size() > 0) {
				node.cluster.clusterNodeMetrics = new ArrayList<Metric>();
				node.cluster.clusterNodeMetrics.addAll(metrics);
			}
		}
		
		
	}
	
	private static void processCpuMetrics(final OagNode node, final List<Metric> metrics) throws Throwable {
		
		if (metrics.size() > 0) { 
			
			if (node.oagCpus == null) {
				
			
				Map<String, AnyVal> resAttrs;
				OtlpEntityThread entityThread;
				Thread tempThread;
				EntityInstance einst;
				
				resAttrs = new HashMap<String, AnyVal>();
				resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagCpu.ENTITY_TYPE_NAME + "." + "host_name", new AnyVal(node.getHostName()));
				resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagCpu.ENTITY_TYPE_NAME + "." + "ip_address", new AnyVal(node.getIpAddress()));
				
				resAttrs.put(ENTITY_ATTR_SVCNAME, new AnyVal(COPSOL_SVCNAME));
				resAttrs.put(ResourceAttributes.TELEMETRY_SDK_NAME.getKey(), new AnyVal(ApplicationCtx.SRVC_CONF.getCopSolutionName()));
				

				for (Metric met : metrics) {
					
					List<NumberDataPoint> ndpList = getNumberDataPointsForMetricType(met);
					String cpuId = null;
					
					for (NumberDataPoint ndp : ndpList) {
						
						List<KeyValue> kvList = ndp.getAttributesList();
						for (KeyValue kv : kvList) {
							
							if (kv.getKey().equals(OAG_CPU_SECONDS_ATTR_CPU)) {
								cpuId = kv.getValue().getStringValue();
							}
							
							// here we are creating a new cpu if it has not already been created
							if (!node.hasCpu(cpuId)) {
								
								Map<String, AnyVal> entityAttrs = new HashMap<String, AnyVal>();;
								
								entityAttrs.putAll(resAttrs);
								
								entityAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagCpu.ENTITY_TYPE_NAME + "." + "cpu_id", new AnyVal(cpuId));
								
								einst = new EntityInstance(entityAttrs, ApplicationCtx.SRVC_CONF.getCopSolutionName());
								
								entityThread = new OtlpEntityThread(einst, "OAG_CPU : " + node.getClusterName() + " : " + node.getIpAddress() + " : " + node.getHostName() + " : " + cpuId);
								tempThread = new Thread(entityThread, "OTLP_OAG_CPU : " + node.getClusterName() + " : " + node.getIpAddress() + " : " + node.getHostName() + " : " + cpuId);
								
								node.addCpu(new OagCpu(einst, node, cpuId));
								
								ApplicationCtx.ENTITY_THREADS.add(tempThread);

								
							}
							
							
							
						}						
						
						
					}
					
				}

			}
			
			
			
		}
		
		if (node.oagCpus != null && node.oagCpus.size() > 0) {
			for (OagCpu oagEntity : node.oagCpus) {
				oagEntity.processRequest(metrics);
			}
		}
		
	}
	
	private static void processDiskMetrics(final OagNode node, final List<Metric> metrics) throws Throwable {
		
		if (metrics.size() > 0) {
			
			if (node.oagDisks == null) {

				Map<String, AnyVal> resAttrs;
				OtlpEntityThread entityThread;
				Thread tempThread;
				EntityInstance einst;
				
				resAttrs = new HashMap<String, AnyVal>();
				resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagDisk.ENTITY_TYPE_NAME + "." + "host_name", new AnyVal(node.getHostName()));
				resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagDisk.ENTITY_TYPE_NAME + "." + "ip_address", new AnyVal(node.getIpAddress()));
				
				resAttrs.put(ENTITY_ATTR_SVCNAME, new AnyVal(COPSOL_SVCNAME));
				resAttrs.put(ResourceAttributes.TELEMETRY_SDK_NAME.getKey(), new AnyVal(ApplicationCtx.SRVC_CONF.getCopSolutionName()));
				

				for (Metric met : metrics) {
					
					List<NumberDataPoint> ndpList = getNumberDataPointsForMetricType(met);
					String dDevicePath = null;
					String dFileSystemType = null;
					String dMountPoint = null;
					
					for (NumberDataPoint ndp : ndpList) {
						
						List<KeyValue> kvList = ndp.getAttributesList();
						for (KeyValue kv : kvList) {
							if (kv.getKey().equals(OAG_DISK_FILESYS_ATTR_DEVICE)) {
								dDevicePath = kv.getValue().getStringValue();
							} else if (kv.getKey().equals(OAG_DISK_FILESYS_ATTR_FSTYPE)) {
								dFileSystemType = kv.getValue().getStringValue();
							} else if (kv.getKey().equals(OAG_DISK_FILESYS_ATTR_MOUNTPOINT)) {
								dMountPoint = kv.getValue().getStringValue();
							}
						}
						
						
						
						// here we are creating a new disk if it has not already been created
						if (!node.hasDisk(dDevicePath, dFileSystemType, dMountPoint)) {
							
							Map<String, AnyVal> entityAttrs = new HashMap<String, AnyVal>();;
							
							entityAttrs.putAll(resAttrs);
							
							entityAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagDisk.ENTITY_TYPE_NAME + "." + "device_path", new AnyVal(dDevicePath));
							entityAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagDisk.ENTITY_TYPE_NAME + "." + "file_sys_type", new AnyVal(dFileSystemType));
							entityAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagDisk.ENTITY_TYPE_NAME + "." + "mountpoint", new AnyVal(dMountPoint));
							
							einst = new EntityInstance(entityAttrs, ApplicationCtx.SRVC_CONF.getCopSolutionName());
							
							entityThread = new OtlpEntityThread(einst, "OAG_DISK : " + node.getClusterName() + " : " + node.getIpAddress() + " : " + node.getHostName() + " : " + dDevicePath + " : " + dFileSystemType + " : " + dMountPoint);
							tempThread = new Thread(entityThread, "OTLP_OAG_DISK : " + node.getClusterName() + " : " + node.getIpAddress() + " : " + node.getHostName() + " : " + dDevicePath + " : " + dFileSystemType + " : " + dMountPoint);
							
							node.addDisk(new OagDisk(einst, node, dDevicePath, dFileSystemType, dMountPoint));
							
							ApplicationCtx.ENTITY_THREADS.add(tempThread);
							
						}
						
						
						
						
					}
					
					
//					if (!hasDisk(node, dDevicePath, dFileSystemType, dMountPoint)) {
//						
//						Map<String, AnyVal> entityAttrs = new HashMap<String, AnyVal>();;
//						
//						entityAttrs.putAll(resAttrs);
//						
//						entityAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagDisk.ENTITY_TYPE_NAME + "." + "device_path", new AnyVal(dDevicePath));
//						entityAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagDisk.ENTITY_TYPE_NAME + "." + "file_sys_type", new AnyVal(dFileSystemType));
//						entityAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagDisk.ENTITY_TYPE_NAME + "." + "mountpoint", new AnyVal(dMountPoint));
//						
//						einst = new EntityInstance(entityAttrs, ApplicationCtx.SRVC_CONF.getCopSolutionName());
//						
//						entityThread = new OtlpEntityThread(einst, "OAG_DISK_" + node.getClusterName() + "_" + node.getIpAddress() + "_" + node.getHostName() + "_" + dDevicePath + "_" + dFileSystemType + "_" + dMountPoint);
//						tempThread = new Thread(entityThread, "OTLP_OAG_DISK_" + node.getClusterName() + "_" + node.getIpAddress() + "_" + node.getHostName() + "_" + dDevicePath + "_" + dFileSystemType + "_" + dMountPoint);
//						
//						node.addDisk(new OagDisk(einst, node, dDevicePath, dFileSystemType, dMountPoint));
//						//node.oagDisks.add(new OagDisk(einst, node, dDevicePath, dFileSystemType, dMountPoint));
//						
//						ApplicationCtx.ENTITY_THREADS.add(tempThread);
//						
//					}
					
					
					
				}
	
				

			}
			
			if (node.oagDisks != null && node.oagDisks.size() > 0) {
				for (OagDisk oagEntity : node.oagDisks) {
					oagEntity.processRequest(metrics);
				}
			}

			
			
			
		}
	}

	public boolean hasNetwork(final String deviceName) {
		
		if (this.oagNics != null && this.oagNics.size() > 0) {
			for (OagNetwork nodeNet : this.oagNics) {
				if (nodeNet.equalsObject(this, deviceName)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean hasCpu(final String cpuId) {
		
		if (this.oagCpus != null && this.oagCpus.size() > 0) {
			for (OagCpu nodeCpu : this.oagCpus) {
				if (nodeCpu.equalsObject(this, cpuId)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean hasDisk(final String dDevicePath, final String dFileSystemType, final String dMountPoint) {
		
		if (this.oagDisks != null && this.oagDisks.size() > 0) {
			for (OagDisk nodeDisk : this.oagDisks) {
				if (nodeDisk.equalsObject(this, dDevicePath, dFileSystemType, dMountPoint)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	private static void processEngineMetrics(final OagNode node, final List<Metric> metrics) throws Throwable {
		
		if (metrics.size() > 0) {
			
			if (node.oagEngine == null) {

				Map<String, AnyVal> resAttrs;
				OtlpEntityThread entityThread;
				Thread tempThread;
				EntityInstance einst;
				
				resAttrs = new HashMap<String, AnyVal>();
				resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagEngine.ENTITY_TYPE_NAME + "." + "host_name", new AnyVal(node.getHostName()));
				resAttrs.put(ApplicationCtx.SRVC_CONF.getCopSolutionName() + "." + OagEngine.ENTITY_TYPE_NAME + "." + "ip_address", new AnyVal(node.getIpAddress()));
				
				resAttrs.put(ENTITY_ATTR_SVCNAME, new AnyVal(COPSOL_SVCNAME));
				resAttrs.put(ResourceAttributes.TELEMETRY_SDK_NAME.getKey(), new AnyVal(ApplicationCtx.SRVC_CONF.getCopSolutionName()));

				einst = new EntityInstance(resAttrs, ApplicationCtx.SRVC_CONF.getCopSolutionName());
				
				entityThread = new OtlpEntityThread(einst, "OAG_ENGINE : " + node.getClusterName() + " : " + node.getIpAddress() + " : " + node.getHostName());
				tempThread = new Thread(entityThread, "OTLP_OAG_ENGINE : " + node.getClusterName() + " : " + node.getIpAddress() + " : " + node.getHostName());
				
				node.oagEngine = new OagEngine(einst, node);
				
				ApplicationCtx.ENTITY_THREADS.add(tempThread);

			}
			
			node.oagEngine.processRequest(metrics);
			
		}
	}
	
	
	
	
	private static void log(String msg, boolean isInfo) {
		if (isInfo) {
			lgr.info(msg);
		} else {
			lgr.debug(msg);
		}
		
	}	
	
	private static ResourceMetrics findMatchingResourceMetricsForNode(final OagNode oagNode, final List<ResourceMetrics> resMetrics) {
		
		for (ResourceMetrics rm : resMetrics) {
			Resource res = rm.getResource();
			
			if (res != null && res.getAttributesList() != null && res.getAttributesList().size() > 0) {
				
				List<KeyValue> attrs = res.getAttributesList();
				
				for (KeyValue kv : attrs) {
					
					if (kv.getValue().hasStringValue()) {
						
						String key = kv.getKey();
						if (key != null && key.equals(ENTITY_ATTR_NET_HOSTNAME)) {
							
							String hostName = kv.getValue().getStringValue();
												
							if (oagNode.getIpAddress().equals(hostName)) {
								log("", true);
								log(" *--------* Found matching node from payload where: ", true);
								log("        ---> Cluster Name        = " + oagNode.getClusterName(), true);
								log("        ---> Hostname for Node   = " + oagNode.getHostName(), true);
								log("        ---> IP for Node         = " + oagNode.getIpAddress(), true);
								log("        ---> IP from Payload     = " + hostName, true);
								log("", true);
								return rm;
							} else {
								log("", true);
								log(" *--------* No matching node found from payload where: ", true);
								log("        ---> Cluster Name        = " + oagNode.getClusterName(), true);
								log("        ---> Hostname for Node   = " + oagNode.getHostName(), true);
								log("        ---> IP for Node         = " + oagNode.getIpAddress(), true);
								log("        ---> IP from Payload     = " + hostName, true);
								log("", true);
							}
						
							
						}
					}
					
				}
				
				
				//lgr.info("");
			}

			
		}
		
		return null;
		
	}	
	public String getClusterName() {
		return this.cluster.getClusterName();
	}


	public String getHostName() {
		return this.config.getNodeHostName();
	}


	public String getIpAddress() {
		return this.config.getNodeIpAddress();
	}


	public boolean isMasterNode() {
		return this.config.isMasterNode();
	}

	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getNetHostPort() {
		return netHostPort;
	}

	public String getHttpScheme() {
		return httpScheme;
	}

	
	public EntityInstance getEntityInstance() {
		return this.entityInst;
	}	
	
	public void addNetwork(OagNetwork oNet) {
		if (this.oagNics == null) {
			this.oagNics = new ArrayList<OagNetwork>();
		}
		log("OagNode addNetwork for :", false);
		log(" ---> cluster        = " + this.getClusterName(), false);
		log(" ---> hostname       = " + this.getHostName(), false);
		log(" ---> ipaddress      = " + this.getIpAddress(), false);
		log(" ---> device name    = " + oNet.getDeviceName(), false);
		log("", false);			
		this.oagNics.add(oNet);		
	}
	
	
	public void addCpu(OagCpu oCpu) {
		if (this.oagCpus == null) {
			this.oagCpus = new ArrayList<OagCpu>();
		}
		log("OagNode addCpu for :", false);
		log(" ---> cluster        = " + this.getClusterName(), false);
		log(" ---> hostname       = " + this.getHostName(), false);
		log(" ---> ipaddress      = " + this.getIpAddress(), false);
		log(" ---> cpu id         = " + oCpu.getCpuId(), false);
		log("", false);			
		this.oagCpus.add(oCpu);		
	}
	
	
	public void addDisk(OagDisk oDisk) {
		if (this.oagDisks == null) {
			this.oagDisks = new ArrayList<OagDisk>();
		}
		log("OagNode addDisk for :", false);
		log(" ---> cluster        = " + this.getClusterName(), false);
		log(" ---> hostname       = " + this.getHostName(), false);
		log(" ---> ipaddress      = " + this.getIpAddress(), false);
		log(" ---> device path    = " + oDisk.getDevicePath(), false);
		log(" ---> fstype         = " + oDisk.getFileSystemType(), false);
		log(" ---> mountpoint     = " + oDisk.getMountPoint(), false);
		log("", false);			
		this.oagDisks.add(oDisk);		
	}
	
	
}
