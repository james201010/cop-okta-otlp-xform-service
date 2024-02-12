/**
 * 
 */
package com.rudetools.otel.okta.receiver.model.oag;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rudetools.otel.okta.receiver.ApplicationConstants;
import com.rudetools.otel.okta.receiver.model.otlp.EntityInstance;
import com.rudetools.otel.okta.receiver.model.otlp.MetricDataPoint;
import com.rudetools.otel.okta.receiver.model.otlp.MetricDefinition;
import com.rudetools.otel.okta.receiver.utils.NumberUtils;

import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;

/**
 * @author james101
 *
 */
public class OagDisk extends OagEntity implements ApplicationConstants {

	public static final Logger lgr = LoggerFactory.getLogger(OagDisk.class);
	public static final String ENTITY_TYPE_NAME = "oag_disk";
	
	
	private final String devicePath;
	private final String fileSystemType;
	private final String mountPoint;
	private final OagNode node;
	private final EntityInstance entityInst;
	protected double totalDiskBytes;
	protected double freeDiskBytes;
	protected double availableDiskBytes;
	
	/**
	 * 
	 */
	public OagDisk(EntityInstance einst, OagNode oagNode, String devicePath, String fileSystemType, String mountPoint) {
		super();
		this.entityInst = einst;
		this.node = oagNode;	
		this.devicePath = devicePath;
		this.fileSystemType = fileSystemType;
		this.mountPoint = mountPoint;
		
		log("OagDisk Constructor for :", false);
		log(" ---> cluster        = " + this.node.getClusterName(), false);
		log(" ---> hostname       = " + this.node.getHostName(), false);
		log(" ---> ipaddress      = " + this.node.getIpAddress(), false);
		log(" ---> device path    = " + this.getDevicePath(), false);
		log(" ---> fstype         = " + this.getFileSystemType(), false);
		log(" ---> mountpoint     = " + this.getMountPoint(), false);
		log("", false);
	}
	
	private static void log(String msg, boolean isInfo) {
		if (isInfo) {
			//lgr.info(msg);
		} else {
			//lgr.info(msg);
		}
	}
	
	public EntityInstance getEntityInstance() {
		return this.entityInst;
	}	
	
	// we already know that the metrics list passed in are only related to the parent node
	// however, these metrics will be for multiple disks related to the node
	public void processRequest(List<Metric> metrics) throws Throwable {
		
		log("OagDisk processRequest for :", false);
		log(" ---> cluster        = " + this.node.getClusterName(), false);
		log(" ---> hostname       = " + this.node.getHostName(), false);
		log(" ---> ipaddress      = " + this.node.getIpAddress(), false);
		log(" ---> device path    = " + this.getDevicePath(), false);
		log(" ---> fstype         = " + this.getFileSystemType(), false);
		log(" ---> mountpoint     = " + this.getMountPoint(), false);
		log("", false);		
		
		// 3 source metrics turns into 9
		if (metrics != null && metrics.size() > 0) {
		
			for (Metric met : metrics) {
				
				if (metricDefinitionExists(met.getName(), this.entityInst.getMetricDefinitions())) {
					updateDefinitionsForMetric(this, met);
				} else {
					createDefinitionsForMetric(this, met);
					
				}
				
			}
			
			handleDefinitionsForDerivedMetrics(this);
			
		}

	}	

	private static NumberDataPoint findNumberDataPointForDisk(final OagDisk oDisk, final List<NumberDataPoint> numDataPoints) {
		
		for (NumberDataPoint ndp : numDataPoints) {
			
			List<KeyValue> kvList = ndp.getAttributesList();
			String devcePath = null;
			String fsType = null;
			String mountPnt = null;
			
			for (KeyValue kv : kvList) {
				if (kv.getKey().equals(OAG_DISK_FILESYS_ATTR_DEVICE)) {
					devcePath = kv.getValue().getStringValue();
				} else if (kv.getKey().equals(OAG_DISK_FILESYS_ATTR_FSTYPE)) {
					fsType = kv.getValue().getStringValue();
				} else if (kv.getKey().equals(OAG_DISK_FILESYS_ATTR_MOUNTPOINT)) {
					mountPnt = kv.getValue().getStringValue();
				}
			}
			
			if (oDisk.getDevicePath().equals(devcePath)) {
				if (oDisk.getFileSystemType().equals(fsType)) {
					if (oDisk.getMountPoint().equals(mountPnt)) {
						return ndp;
					}
				}
			}
			
		}
		
		return null;

	}
	
	private static void handleDefinitionsForDerivedMetrics(final OagDisk oDisk) {
		
		MetricDefinition metricDef = null;
		
		
		if (metricDefinitionExists(COP_DISK_FILESYS_USED_PERC, oDisk.getEntityInstance().getMetricDefinitions())) {
			
			metricDef = findMatchingMetricDefinition(oDisk.getEntityInstance().getMetricDefinitions(), COP_DISK_FILESYS_USED_PERC);
			metricDef.getDataPoints().get(0).setDoubleVal(NumberUtils.calculatePercentUsed(oDisk.totalDiskBytes, oDisk.freeDiskBytes));
			
		} else {
			metricDef = createDefinitionForMetric(oDisk.getEntityInstance(), 
					COP_DISK_FILESYS_USED_PERC,
					MetricDefinition.GAUGE_DOUBLE,
					COP_DISK_FILESYS_USED_PERC,
					MetricDefinition.GAUGE_DOUBLE,
					"Filesystem percentage of used space.");
			createDoubleMetricDataPoint(metricDef, NumberUtils.calculatePercentUsed(oDisk.totalDiskBytes, oDisk.freeDiskBytes));
			
		}

		
		if (metricDefinitionExists(COP_DISK_FILESYS_AVAIL_PERC, oDisk.getEntityInstance().getMetricDefinitions())) {

			metricDef = findMatchingMetricDefinition(oDisk.getEntityInstance().getMetricDefinitions(), COP_DISK_FILESYS_AVAIL_PERC);
			metricDef.getDataPoints().get(0).setDoubleVal(NumberUtils.calculatePercentFree(oDisk.totalDiskBytes, oDisk.availableDiskBytes));
			
		} else {
			metricDef = createDefinitionForMetric(oDisk.getEntityInstance(), 
					COP_DISK_FILESYS_AVAIL_PERC,
					MetricDefinition.GAUGE_DOUBLE,
					COP_DISK_FILESYS_AVAIL_PERC,
					MetricDefinition.GAUGE_DOUBLE,
					"Filesystem percentage of available space.");
			createDoubleMetricDataPoint(metricDef, NumberUtils.calculatePercentFree(oDisk.totalDiskBytes, oDisk.availableDiskBytes));
		
		
		}		
		
		
		if (metricDefinitionExists(COP_DISK_FILESYS_FREE_PERC, oDisk.getEntityInstance().getMetricDefinitions())) {

			metricDef = findMatchingMetricDefinition(oDisk.getEntityInstance().getMetricDefinitions(), COP_DISK_FILESYS_FREE_PERC);
			metricDef.getDataPoints().get(0).setDoubleVal(NumberUtils.calculatePercentFree(oDisk.totalDiskBytes, oDisk.freeDiskBytes));
			
		} else {
			metricDef = createDefinitionForMetric(oDisk.getEntityInstance(), 
					COP_DISK_FILESYS_FREE_PERC,
					MetricDefinition.GAUGE_DOUBLE,
					COP_DISK_FILESYS_FREE_PERC,
					MetricDefinition.GAUGE_DOUBLE,
					"Filesystem percentage of available space.");
			createDoubleMetricDataPoint(metricDef, NumberUtils.calculatePercentFree(oDisk.totalDiskBytes, oDisk.freeDiskBytes));
			
		}
		
	}
	
	
	private static void createDefinitionsForMetric(final OagDisk oDisk, final Metric metric) {
		
		String srcMetName = metric.getName();
		MetricDefinition metricDef = null;
		MetricDataPoint metricDp = null;
		NumberDataPoint numDataPoint = null;
		
		
		// we must match this disk to the matching number data point attributes
		switch (srcMetName) {
		
		case OAG_DISK_FILESYS_TOTAL_BYTES:
			
			numDataPoint = findNumberDataPointForDisk(oDisk, metric.getGauge().getDataPointsList());
			
			metricDef = createDefinitionForMetric(oDisk.getEntityInstance(), 
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_DISK_FILESYS_TOTAL_BYTES,
					MetricDefinition.GAUGE_DOUBLE,
					metric.getDescription());
			metricDp = createMetricDataPoint(metricDef, numDataPoint, MetricDefinition.GAUGE_DOUBLE, MetricDefinition.GAUGE_DOUBLE);
			
			
			// set the disk bytes for the disk passed in
			oDisk.totalDiskBytes = metricDp.getDoubleVal();
			
			
			metricDef = createDefinitionForMetric(oDisk.getEntityInstance(), 
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_DISK_FILESYS_TOTAL_GB,
					MetricDefinition.GAUGE_DOUBLE,
					"Filesystem free space in GBs.");
			metricDp = createDoubleMetricDataPoint(metricDef, NumberUtils.bytesToGb(oDisk.totalDiskBytes));
			
			
			break;

		case OAG_DISK_FILESYS_AVAIL_BYTES:
			
			numDataPoint = findNumberDataPointForDisk(oDisk, metric.getGauge().getDataPointsList());
			
			metricDef = createDefinitionForMetric(oDisk.getEntityInstance(), 
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_DISK_FILESYS_AVAIL_BYTES,
					MetricDefinition.GAUGE_DOUBLE,
					metric.getDescription());
			metricDp = createMetricDataPoint(metricDef, numDataPoint, MetricDefinition.GAUGE_DOUBLE, MetricDefinition.GAUGE_DOUBLE);
			
			
			// set the disk bytes for the disk passed in
			oDisk.availableDiskBytes = metricDp.getDoubleVal();
			
			
			metricDef = createDefinitionForMetric(oDisk.getEntityInstance(), 
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_DISK_FILESYS_AVAIL_GB,
					MetricDefinition.GAUGE_DOUBLE,
					"Filesystem space available to non-root users in GBs.");
			metricDp = createDoubleMetricDataPoint(metricDef, NumberUtils.bytesToGb(oDisk.availableDiskBytes));
			
			
			break;

		case OAG_DISK_FILESYS_FREE_BYTES:
			
			numDataPoint = findNumberDataPointForDisk(oDisk, metric.getGauge().getDataPointsList());
			
			metricDef = createDefinitionForMetric(oDisk.getEntityInstance(), 
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_DISK_FILESYS_FREE_BYTES,
					MetricDefinition.GAUGE_DOUBLE,
					metric.getDescription());
			metricDp = createMetricDataPoint(metricDef, numDataPoint, MetricDefinition.GAUGE_DOUBLE, MetricDefinition.GAUGE_DOUBLE);
			
			
			// set the disk bytes for the disk passed in
			oDisk.freeDiskBytes = metricDp.getDoubleVal();
			
			
			metricDef = createDefinitionForMetric(oDisk.getEntityInstance(), 
					srcMetName,
					MetricDefinition.GAUGE_DOUBLE,
					COP_DISK_FILESYS_FREE_GB,
					MetricDefinition.GAUGE_DOUBLE,
					"Filesystem free space in GBs.");
			metricDp = createDoubleMetricDataPoint(metricDef, NumberUtils.bytesToGb(oDisk.freeDiskBytes));
			
			
			break;
			
			
			
		default:
			break;
		}
		
		
		
	}	
		
	
	private static void updateDefinitionsForMetric(final OagDisk oDisk, final Metric metric) {
		
		
		String srcMetName = metric.getName();
		MetricDefinition metricDef = null;
		
		switch (srcMetName) {
		case OAG_DISK_FILESYS_TOTAL_BYTES:
			
			printDisk(oDisk, false);
			
			metricDef = findMatchingMetricDefinition(oDisk.getEntityInstance().getMetricDefinitions(), COP_DISK_FILESYS_TOTAL_BYTES);
			updateDataPointForMetric(oDisk.getEntityInstance(), metricDef, MetricDefinition.GAUGE_DOUBLE, metric);
			
			// set the disk bytes for the disk passed in
			oDisk.totalDiskBytes = metricDef.getDataPoints().get(0).getDoubleVal();
			
			metricDef = findMatchingMetricDefinition(oDisk.getEntityInstance().getMetricDefinitions(), COP_DISK_FILESYS_TOTAL_GB);
			metricDef.getDataPoints().get(0).setDoubleVal(NumberUtils.bytesToGb(oDisk.totalDiskBytes));
						
			break;
			
		case OAG_DISK_FILESYS_AVAIL_BYTES:
			
			printDisk(oDisk, false);
			
			metricDef = findMatchingMetricDefinition(oDisk.getEntityInstance().getMetricDefinitions(), COP_DISK_FILESYS_AVAIL_BYTES);
			updateDataPointForMetric(oDisk.getEntityInstance(), metricDef, MetricDefinition.GAUGE_DOUBLE, metric);
			
			// set the disk bytes for the disk passed in
			oDisk.availableDiskBytes = metricDef.getDataPoints().get(0).getDoubleVal();
			
			metricDef = findMatchingMetricDefinition(oDisk.getEntityInstance().getMetricDefinitions(), COP_DISK_FILESYS_AVAIL_GB);
			metricDef.getDataPoints().get(0).setDoubleVal(NumberUtils.bytesToGb(oDisk.availableDiskBytes));
						
			break;
			
		case OAG_DISK_FILESYS_FREE_BYTES:
			
			printDisk(oDisk, false);
			
			metricDef = findMatchingMetricDefinition(oDisk.getEntityInstance().getMetricDefinitions(), COP_DISK_FILESYS_FREE_BYTES);
			updateDataPointForMetric(oDisk.getEntityInstance(), metricDef, MetricDefinition.GAUGE_DOUBLE, metric);
			
			// set the disk bytes for the disk passed in
			oDisk.freeDiskBytes = metricDef.getDataPoints().get(0).getDoubleVal();
			
			metricDef = findMatchingMetricDefinition(oDisk.getEntityInstance().getMetricDefinitions(), COP_DISK_FILESYS_FREE_GB);
			metricDef.getDataPoints().get(0).setDoubleVal(NumberUtils.bytesToGb(oDisk.freeDiskBytes));
						
			break;

			
		default:
			break;
		}
		
		
	}

	
	
	
	
	public boolean equalsObject(OagNode obj, String dDevicePath, String dFileSystemType, String dMountPoint) {
		
		if (obj != null) {
			try {
				if (obj.getClusterName().equals(this.getClusterName())) {
					if (obj.getHostName().equals(this.getHostName())) {
						if (obj.getIpAddress().equals(this.getIpAddress())) {
							if (this.getDevicePath().equals(dDevicePath)) {
								if (this.getFileSystemType().equals(dFileSystemType)) {
									if (this.getMountPoint().equals(dMountPoint)) {
										return true;
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				// do nothing
			}
			
		}
		return false;
	}
	
	public boolean equalsObject(Object obj) {
		
		if (obj != null) {
			try {
				OagDisk disk = (OagDisk)obj;
				
				if (disk.getClusterName().equals(this.getClusterName())) {
					if (disk.getHostName().equals(this.getHostName())) {
						if (disk.getIpAddress().equals(this.getIpAddress())) {
							if (disk.getDevicePath().equals(this.getDevicePath())) {
								if (disk.getFileSystemType().equals(this.getFileSystemType())) {
									if (disk.getMountPoint().equals(this.getMountPoint())) {
										return true;
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				// do nothing
			}
			
		}
		return false;
	}
	
	public String getClusterName() {
		return this.node.getClusterName();
	}
	
	public String getHostName() {
		return this.node.getHostName();
	}
	
	public String getIpAddress() {
		return this.node.getIpAddress();
	}

	public String getDevicePath() {
		return devicePath;
	}

	public String getFileSystemType() {
		return fileSystemType;
	}

	public String getMountPoint() {
		return mountPoint;
	}
	
	private static void printDisk(OagDisk oDisk, boolean isInfo) {
		log("OagDisk for :", isInfo);
		log(" ---> cluster        = " + oDisk.node.getClusterName(), isInfo);
		log(" ---> hostname       = " + oDisk.node.getHostName(), isInfo);
		log(" ---> ipaddress      = " + oDisk.node.getIpAddress(), isInfo);
		log(" ---> device path    = " + oDisk.getDevicePath(), isInfo);
		log(" ---> fstype         = " + oDisk.getFileSystemType(), isInfo);
		log(" ---> mountpoint     = " + oDisk.getMountPoint(), isInfo);
		log("", false);
	}
}
