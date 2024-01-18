/**
 * 
 */
package com.rudetools.otel.okta.receiver;

import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.rudetools.otel.okta.receiver.config.OagClusterConfig;
import com.rudetools.otel.okta.receiver.config.ServiceConfig;
import com.rudetools.otel.okta.receiver.model.oag.OagCluster;
import com.rudetools.otel.okta.receiver.utils.StringUtils;





/**
 * @author james101
 *
 */
public class OtlpReceiverAppListener implements ApplicationConstants, ApplicationListener<ApplicationEvent> {

	
	public static final Logger lgr = LoggerFactory.getLogger(OtlpReceiverAppListener.class);
	
	//private static ServiceConfig SRVC_CONF;
	
	
	/**
	 * 
	 */
	public OtlpReceiverAppListener() {
		
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		
		if (event instanceof AvailabilityChangeEvent) {
			
			AvailabilityChangeEvent<?> ace = (AvailabilityChangeEvent<?>)event;
			
			if (ace.getState().equals(ReadinessState.ACCEPTING_TRAFFIC)) {
				
				try {
					
					lgr.info("");
					lgr.info("");
					lgr.info("######################################    STARTING CISCO OKTA OTEL RECEIVER    ######################################");
					lgr.info("");
					
					String confPath = System.getProperty(SERVICE_CONF_KEY);
					
					if (confPath == null || confPath.equals("")) {
						lgr.error("Missing JVM startup property -D" + SERVICE_CONF_KEY);
						lgr.error("Please set this property -D" + SERVICE_CONF_KEY + " with the full or relative path to the configuration Yaml file like this | -D" + SERVICE_CONF_KEY + "=/opt/cisco/okta-receiver/config.yaml");
						lgr.info("");
						lgr.info("");
						System.exit(1);
					}
					
					
					Yaml yaml = new Yaml(new Constructor(ServiceConfig.class));
					InputStream inputStream = StringUtils.getFileAsStream(confPath);
					
					ApplicationCtx.SRVC_CONF = yaml.load(inputStream);
					
					init();
					
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
				
				
			}
			
		}
		
		
	}
	
	private static void init() {
		
		try {
			
			List<OagClusterConfig> clusters = ApplicationCtx.SRVC_CONF.getOagClusters();
			
			for (OagClusterConfig clust : clusters) {
				ApplicationCtx.CLUSTER_LIST.add(new OagCluster(clust));
			}
			
			OAG_CLUSTER_METRIC_NAMES.add(OAG_CLUSTER_VALIDATION);
			OAG_CLUSTER_METRIC_NAMES.add(OAG_CLUSTER_LAST_SYNC);
			
			
			OAG_ENGINE_METRIC_NAMES.add(OAG_ENGINE_ACTIVE_CONNS);
			OAG_ENGINE_METRIC_NAMES.add(OAG_ENGINE_HANDLED_CONNS);
			OAG_ENGINE_METRIC_NAMES.add(OAG_ENGINE_TOTAL_REQS_NUM);
			OAG_ENGINE_METRIC_NAMES.add(OAG_ENGINE_WAITING_NUM);
			OAG_ENGINE_METRIC_NAMES.add(OAG_ENGINE_ACCEPTED_CONNS);
			OAG_ENGINE_METRIC_NAMES.add(OAG_ENGINE_METRIC_HANDLER_REQS_TOTAL);
			OAG_ENGINE_METRIC_NAMES.add(OAG_ENGINE_METRIC_HANDLER_ERRS);
			
			
			OAG_DISK_METRIC_NAMES.add(OAG_DISK_FILESYS_TOTAL_BYTES);
			OAG_DISK_METRIC_NAMES.add(OAG_DISK_FILESYS_AVAIL_BYTES);
			OAG_DISK_METRIC_NAMES.add(OAG_DISK_FILESYS_FREE_BYTES);
			
			
			OAG_CPU_METRIC_NAMES.add(OAG_CPU_SECONDS);
			OAG_CPU_METRIC_NAMES.add(OAG_CPU_LOAD_1);
			OAG_CPU_METRIC_NAMES.add(OAG_CPU_LOAD_5);
			OAG_CPU_METRIC_NAMES.add(OAG_CPU_LOAD_15);
			
			
			OAG_MEMORY_METRIC_NAMES.add(OAG_MEMORY_TOTAL_BYTES);
			OAG_MEMORY_METRIC_NAMES.add(OAG_MEMORY_AVAIL_BYTES);
			OAG_MEMORY_METRIC_NAMES.add(OAG_MEMORY_FREE_BYTES);
			
			
			OAG_NET_METRIC_NAMES.add(OAG_NET_RECEIVE_DROP);
			OAG_NET_METRIC_NAMES.add(OAG_NET_RECEIVE_ERRS);
			OAG_NET_METRIC_NAMES.add(OAG_NET_RECEIVE_BYTES);
			
			
			
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		
	}

}
