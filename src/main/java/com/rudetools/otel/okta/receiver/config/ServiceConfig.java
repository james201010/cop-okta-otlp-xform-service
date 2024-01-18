/**
 * 
 */
package com.rudetools.otel.okta.receiver.config;

import java.util.List;

/**
 * @author james101
 *
 */
public class ServiceConfig {
	
	private String copSolutionName;
	private List<OagClusterConfig> oagClusters;

	/**
	 * 
	 */
	public ServiceConfig() {
		
	}

	public List<OagClusterConfig> getOagClusters() {
		return oagClusters;
	}

	public void setOagClusters(List<OagClusterConfig> oagClusters) {
		this.oagClusters = oagClusters;
	}

	public String getCopSolutionName() {
		return copSolutionName;
	}

	public void setCopSolutionName(String copSolutionName) {
		this.copSolutionName = copSolutionName;
	}

	
	
}
