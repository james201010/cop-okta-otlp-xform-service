/**
 * 
 */
package com.rudetools.otel.okta.receiver.config;

/**
 * @author james101
 *
 */
public class OagNodeConfig {

	private String nodeHostName;
	private String nodeIpAddress;
	private boolean isMasterNode = false;
	
	/**
	 * 
	 */
	public OagNodeConfig() {
		
	}

	public String getNodeHostName() {
		return nodeHostName;
	}

	public void setNodeHostName(String nodeHostName) {
		this.nodeHostName = nodeHostName;
	}

	public String getNodeIpAddress() {
		return nodeIpAddress;
	}

	public void setNodeIpAddress(String nodeIpAddress) {
		this.nodeIpAddress = nodeIpAddress;
	}

	public boolean isMasterNode() {
		return isMasterNode;
	}

	public void setIsMasterNode(boolean isMasterNode) {
		this.isMasterNode = isMasterNode;
	}

	
	
}
