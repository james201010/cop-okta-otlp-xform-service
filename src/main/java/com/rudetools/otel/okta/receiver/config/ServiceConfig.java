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
	private String otelCollectorHttpProtoMetricsEndpoint;
	private String otelCollectorHttpProtoLogsEndpoint;
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

	public String getOtelCollectorHttpProtoMetricsEndpoint() {
		return otelCollectorHttpProtoMetricsEndpoint;
	}

	public void setOtelCollectorHttpProtoMetricsEndpoint(String otelCollectorHttpProtoMetricsEndpoint) {
		this.otelCollectorHttpProtoMetricsEndpoint = otelCollectorHttpProtoMetricsEndpoint;
	}

	public String getOtelCollectorHttpProtoLogsEndpoint() {
		return otelCollectorHttpProtoLogsEndpoint;
	}

	public void setOtelCollectorHttpProtoLogsEndpoint(String otelCollectorHttpProtoLogsEndpoint) {
		this.otelCollectorHttpProtoLogsEndpoint = otelCollectorHttpProtoLogsEndpoint;
	}

	
	
}
