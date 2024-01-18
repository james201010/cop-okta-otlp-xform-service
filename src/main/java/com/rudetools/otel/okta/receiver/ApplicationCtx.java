/**
 * 
 */
package com.rudetools.otel.okta.receiver;

import java.util.ArrayList;
import java.util.List;

import com.rudetools.otel.okta.receiver.config.ServiceConfig;
import com.rudetools.otel.okta.receiver.model.oag.OagCluster;

/**
 * @author james101
 *
 */
public class ApplicationCtx {

	public static ServiceConfig SRVC_CONF = null;
	public static List<OagCluster> CLUSTER_LIST = new ArrayList<OagCluster>();
	public static List<Thread> ENTITY_THREADS = new ArrayList<Thread>();
	
}
