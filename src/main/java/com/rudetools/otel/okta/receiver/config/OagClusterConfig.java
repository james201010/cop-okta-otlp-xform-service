/**
 * 
 */
package com.rudetools.otel.okta.receiver.config;

import java.util.List;

/**
 * @author james101
 *
 */
public class OagClusterConfig {

	private String clusterName;
	private List<OagNodeConfig> oagNodes;
	
	/**
	 * 
	 */
	public OagClusterConfig() {
		
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public List<OagNodeConfig> getOagNodes() {
		return oagNodes;
	}

	public void setOagNodes(List<OagNodeConfig> oagNodes) {
		this.oagNodes = oagNodes;
	}

	
	
}
