/**
 * 
 */
package com.rudetools.otel.okta.receiver;

import java.util.ArrayList;
import java.util.List;

/**
 * @author james101
 *
 */
public interface ApplicationConstants {

	// TODO !!!!! Create the Cluster and Node object instances at startup from the config
	// that way, we can assume those objects are always created first without additional
	// logic to determine if they have yet been created.
	
	// Pass the (List<ResourceMetrics> rms = emsReq.getResourceMetricsList();) to each Cluster
	// and let the Cluster determine if it has a match, and if so, then pass the matching (ResourceMetrics rm)
	// to the matching Node, and so on.
	
	public static final String SERVICE_CONF_KEY = "config.file.path";
	
	public static final String COPSOL_SVCNAME = "okta-prometheus-xform";
	
	public static final String ENTITY_ATTR_SVCNAME = "service.name";
	public static final String ENTITY_ATTR_NET_HOSTNAME = "net.host.name";
	public static final String ENTITY_ATTR_INSTID = "service.instance.id";
	public static final String ENTITY_ATTR_NET_HOSTPORT = "net.host.port";
	public static final String ENTITY_ATTR_HTTP_SCHEME = "http.scheme";

	
	// !!!! CLUSTER
	public static final List<String> OAG_CLUSTER_METRIC_NAMES = new ArrayList<String>();
	
	public static final String OAG_CLUSTER_METRIC_PFX ="OAG_ha_";
	
	public static final String OAG_CLUSTER_VALIDATION = "OAG_ha_validation_result_"; // Gauge/Double
	public static final String COP_CLUSTER_VALIDATION = "oag_ha_validation"; // Gauge/Long
	public static final String OAG_CLUSTER_VALIDATION_DESCR_PARSE_PFX = "HA validation status between master node ";
	public static final String OAG_CLUSTER_VALIDATION_DESCR_PARSE_DELIM = " and worker node ";
	// Metric Description = HA validation status between master node okta-oag-linux-admin-1-new.dev.aws.glic.com and worker node okta-oag-linux-int-workernode-2-new.dev.aws.glic.com
	
	public static final String OAG_CLUSTER_LAST_SYNC = "OAG_ha_last_sync_timestamp_"; // Gauge/Double
	public static final String COP_CLUSTER_LAST_SYNC = "oag_ha_last_sync"; // if present send 1 and add start/end timestamp  // Gauge/Long
	public static final String OAG_CLUSTER_LAST_SYNC_DESCR_PARSE_PFX = "Last timestamp for HA worker node ";
	public static final String OAG_CLUSTER_LAST_SYNC_DESCR_PARSE_DELIM = " of master node ";
	// Metric Description = Last timestamp for HA worker node okta-oag-linux-int-workernode-1-new.dev.aws.glic.com of master node okta-oag-linux-admin-1-new.dev.aws.glic.com
	
	
	
	// !!!! ENGINE
	public static final List<String> OAG_ENGINE_METRIC_NAMES = new ArrayList<String>();
	
	public static final String OAG_ENGINE_METRIC_PFX_1 = "OAG_nginx_";
	public static final String OAG_ENGINE_METRIC_PFX_2 = "OAG_promhttp_";
	
	public static final String OAG_ENGINE_ACTIVE_CONNS = "OAG_nginx_active_connections"; // Gauge/Double
	public static final String COP_ENGINE_ACTIVE_CONNS = "oag_nginx_active_conns"; // Gauge/Long 
	
	public static final String OAG_ENGINE_HANDLED_CONNS = "OAG_nginx_handled_connections"; // Gauge/Double
	public static final String COP_ENGINE_HANDLED_CONNS = "oag_nginx_handled_conns"; // Gauge/Long 
	
	public static final String OAG_ENGINE_TOTAL_REQS_NUM = "OAG_nginx_requests_number"; // Gauge/Double
	public static final String COP_ENGINE_TOTAL_REQS_NUM = "oag_nginx_total_requests"; // Gauge/Long 
	
	public static final String OAG_ENGINE_WAITING_NUM = "OAG_nginx_waiting_number"; // Gauge/Double
	public static final String COP_ENGINE_WAITING_NUM = "oag_nginx_waiting_conns"; // Gauge/Long 
	
	public static final String OAG_ENGINE_ACCEPTED_CONNS = "OAG_nginx_accepted_connections"; // Gauge/Double
	public static final String COP_ENGINE_ACCEPTED_CONNS = "oag_nginx_accepted_conns"; // Gauge/Long 
	
	public static final String OAG_ENGINE_METRIC_HANDLER_REQS_TOTAL = "OAG_promhttp_metric_handler_requests"; // Sum/Double
	public static final String COP_ENGINE_METRIC_HANDLER_REQS_200 = "oag_promhttp_metric_handler_requests_200"; // Gauge/Long 
	public static final String COP_ENGINE_METRIC_HANDLER_REQS_500 = "oag_promhttp_metric_handler_requests_500"; // Gauge/Long 
	public static final String COP_ENGINE_METRIC_HANDLER_REQS_503 = "oag_promhttp_metric_handler_requests_503"; // Gauge/Long 
	
	public static final String OAG_ENGINE_METRIC_HANDLER_REQS_TOTAL_ATTR_CODE = "code";                      // "HTTP" code - 200, 500, 503
	
	public static final String OAG_ENGINE_METRIC_HANDLER_ERRS = "OAG_promhttp_metric_handler_errors"; // Sum/Double
	public static final String COP_ENGINE_METRIC_HANDLER_ERRS_ENCODE = "oag_promhttp_metric_handler_errs_encode"; // Gauge/Long 
	public static final String COP_ENGINE_METRIC_HANDLER_ERRS_GATHER = "oag_promhttp_metric_handler_errs_gather"; // Gauge/Long 
	
	public static final String OAG_ENGINE_METRIC_HANDLER_ERRORS_ATTR_CAUSE = "cause";                        // cause - encoding, gathering
	
	
	
	// !!!! DISK
	public static final List<String> OAG_DISK_METRIC_NAMES = new ArrayList<String>();
	
	public static final String OAG_DISK_METRIC_PFX = "OAG_node_filesystem_";
	
	public static final String OAG_DISK_FILESYS_TOTAL_BYTES = "OAG_node_filesystem_size_bytes"; // Gauge/Double  // add new metric converted to GB as well
	public static final String COP_DISK_FILESYS_TOTAL_BYTES = "oag_filesystem_size_bytes"; // Gauge/Double
	public static final String COP_DISK_FILESYS_TOTAL_GB = "oag_filesystem_size_gb"; // Gauge/Double
	
	public static final String OAG_DISK_FILESYS_AVAIL_BYTES = "OAG_node_filesystem_avail_bytes"; // Gauge/Double // add new metric converted to GB as well
	public static final String COP_DISK_FILESYS_AVAIL_BYTES = "oag_filesystem_avail_bytes"; // Gauge/Double
	public static final String COP_DISK_FILESYS_AVAIL_GB = "oag_filesystem_avail_gb"; // Gauge/Double
	
	public static final String OAG_DISK_FILESYS_FREE_BYTES = "OAG_node_filesystem_free_bytes"; // Gauge/Double  // add new metric converted to GB as well
	public static final String COP_DISK_FILESYS_FREE_BYTES = "oag_filesystem_free_bytes"; // Gauge/Double
	public static final String COP_DISK_FILESYS_FREE_GB = "oag_filesystem_free_gb"; // Gauge/Double
	
	// FREE_PERC = free / total
	// AVAIL_PERC = avail / total
	// USED_PERC = look at data	
	public static final String COP_DISK_FILESYS_USED_PERC = "oag_filesystem_used_perc"; // Gauge/Double
	public static final String COP_DISK_FILESYS_AVAIL_PERC = "oag_filesystem_avail_perc"; // Gauge/Double
	public static final String COP_DISK_FILESYS_FREE_PERC = "oag_filesystem_free_perc"; // Gauge/Double
	
	public static final String OAG_DISK_FILESYS_ATTR_DEVICE = "device";                                      // unique device path/name - /dev/mapper/ol_oag-root
	public static final String OAG_DISK_FILESYS_ATTR_FSTYPE = "fstype";                                      // - xfs
	public static final String OAG_DISK_FILESYS_ATTR_MOUNTPOINT = "mountpoint";                              // - /boot
	
	
	
	// !!!! CPU
	public static final List<String> OAG_CPU_METRIC_NAMES = new ArrayList<String>();
	
	public static final String OAG_CPU_METRIC_PFX_1 = "OAG_node_cpu_";
	public static final String OAG_CPU_METRIC_PFX_2 = "OAG_node_load";
	
	public static final String OAG_CPU_SECONDS = "OAG_node_cpu_seconds"; // Sum/Double
	public static final String COP_CPU_SECS_IDLE = "oag_cpu_secs_idle";  // Gauge/Double
	public static final String COP_CPU_SECS_IOWAIT = "oag_cpu_secs_iowait"; // Gauge/Double
	public static final String COP_CPU_SECS_IRQ = "oag_cpu_secs_irq"; // Gauge/Double
	public static final String COP_CPU_SECS_NICE = "oag_cpu_secs_nice"; // Gauge/Double
	public static final String COP_CPU_SECS_SOFTIRQ = "oag_cpu_secs_softirq"; // Gauge/Double
	public static final String COP_CPU_SECS_STEAL = "oag_cpu_secs_steal"; // Gauge/Double
	public static final String COP_CPU_SECS_SYSTEM = "oag_cpu_secs_system"; // Gauge/Double
	public static final String COP_CPU_SECS_USER = "oag_cpu_secs_user"; // Gauge/Double
	public static final String COP_CPU_PERC_USED = "oag_cpu_perc_used"; // Gauge/Double   // try with 100 - idle and try with idle / (sum of all)
	
	public static final String OAG_CPU_SECONDS_ATTR_CPU = "cpu";                                             // unique cpu identifier - 0, 1, 2
	public static final String OAG_CPU_SECONDS_ATTR_MODE = "mode";                                           // - idle, iowait, irq, nice, softirq, steal, system, user  ???
	
	public static final String OAG_CPU_LOAD_1 = "OAG_node_load1"; // Gauge/Double / % cpu load
	public static final String COP_CPU_LOAD_1 = "oag_cpu_load_1_min_avg"; // Gauge/Double
	
	public static final String OAG_CPU_LOAD_5 = "OAG_node_load5"; // Gauge/Double / % cpu load
	public static final String COP_CPU_LOAD_5 = "oag_cpu_load_5_min_avg"; // Gauge/Double
	
	public static final String OAG_CPU_LOAD_15 = "OAG_node_load15"; // Gauge/Double / % cpu load
	public static final String COP_CPU_LOAD_15 = "oag_cpu_load_15_min_avg"; // Gauge/Double
	
	
	
	
	// !!!! MEMORY
	public static final List<String> OAG_MEMORY_METRIC_NAMES = new ArrayList<String>();
	
	public static final String OAG_MEMORY_METRIC_PFX = "OAG_node_memory_";
	
	public static final String OAG_MEMORY_TOTAL_BYTES = "OAG_node_memory_MemTotal_bytes"; // Gauge/Double     // add new metric converted to GB as well
	public static final String COP_MEMORY_TOTAL_BYTES = "oag_memory_total_bytes"; // Gauge/Double
	public static final String COP_MEMORY_TOTAL_GB = "oag_memory_total_gb"; // Gauge/Double
	
	
	public static final String OAG_MEMORY_AVAIL_BYTES = "OAG_node_memory_MemAvailable_bytes"; // Gauge/Double // add new metric converted to GB as well
	public static final String COP_MEMORY_AVAIL_BYTES = "oag_memory_avail_bytes"; // Gauge/Double
	public static final String COP_MEMORY_AVAIL_GB = "oag_memory_avail_gb"; // Gauge/Double
	
	public static final String OAG_MEMORY_FREE_BYTES = "OAG_node_memory_MemFree_bytes"; // Gauge/Double       // add new metric converted to GB as well
	public static final String COP_MEMORY_FREE_BYTES = "oag_memory_free_bytes"; // Gauge/Double
	public static final String COP_MEMORY_FREE_GB = "oag_memory_free_gb";	 // Gauge/Double
	
	public static final String COP_MEMORY_USED_PERC = "oag_memory_used_perc"; // Gauge/Double
	public static final String COP_MEMORY_AVAIL_PERC = "oag_memory_avail_perc"; // Gauge/Double
	public static final String COP_MEMORY_FREE_PERC = "oag_memory_free_perc"; // Gauge/Double
	
	// OAG_MEMORY_DER_USED_BYTES = "total - free";  // add new metric converted to GB as well
	// percentage used !!!
	
	
	
	
	// !!!! NETWORK
	public static final List<String> OAG_NET_METRIC_NAMES = new ArrayList<String>();
	
	public static final String OAG_NET_METRIC_PFX = "OAG_node_network_";
	
	public static final String OAG_NET_RECEIVE_DROP = "OAG_node_network_receive_drop"; // Sum/Double
	public static final String COP_NET_RECEIVE_DROP = "oag_network_receive_drop"; // Gauge/Double
	
	public static final String OAG_NET_RECEIVE_ERRS = "OAG_node_network_receive_errs"; // Sum/Double
	public static final String COP_NET_RECEIVE_ERRS = "oag_network_receive_errs"; // Gauge/Double
	
	public static final String OAG_NET_RECEIVE_BYTES = "OAG_node_network_receive_bytes"; // Sum/Double
	public static final String COP_NET_RECEIVE_BYTES = "oag_network_receive_bytes"; // Gauge/Double
	
	public static final String OAG_NET_ATTR_DEVICE = "device";                                                 // unique device name - eth0
	

	
}
