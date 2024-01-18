/**
 * 
 */
package com.rudetools.otel.examples.okta.otlpreceiver.controllers;

import java.util.Enumeration;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.common.v1.KeyValueList;
import io.opentelemetry.proto.metrics.v1.Gauge;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.Metric.DataCase;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import io.opentelemetry.proto.metrics.v1.ResourceMetrics;
import io.opentelemetry.proto.metrics.v1.ScopeMetrics;
import io.opentelemetry.proto.metrics.v1.Sum;
import io.opentelemetry.proto.resource.v1.Resource;



/**
 * @author james101
 *
 */

@Controller
public class WebController {

	public static final Logger logger = LoggerFactory.getLogger(WebController.class);
	
	/**
	 * 
	 */
	public WebController() {
		
	}

	
	@RequestMapping(value="/v1/metrics", method = RequestMethod.POST)
	public String receive(HttpServletRequest req, ModelMap model) {
	
		try {
			
			int clength = Integer.parseInt(req.getHeader("content-length"));
			
			Enumeration<String> str = req.getHeaderNames();
			while (str.hasMoreElements()) {
				String hName = (String) str.nextElement();
				logger.info(hName + " = " + req.getHeader(hName));
			}
			
			GZIPInputStream gzipis = new GZIPInputStream(req.getInputStream(), clength);

			logger.info("");
			logger.info("!!!!!!!!!!!!!!!!!!!!!!!! BEGIN payload !!!!!!!!!!!!!!!!!!!!!!!!");
			logger.info("");
			
			ExportMetricsServiceRequest emsReq = ExportMetricsServiceRequest.parseFrom(gzipis);
			
			List<ResourceMetrics> rms = emsReq.getResourceMetricsList();
			
			for (ResourceMetrics rm : rms) {
				
				handleResource(rm.getResource());
				
				handleScopeMetrics(rm.getScopeMetricsList());

			}
			
			
			logger.info("!!!!!!!!!!!!!!!!!!!!!!!! END payload !!!!!!!!!!!!!!!!!!!!!!!!");
			logger.info("");
			
			return "success";
			
		} catch (Throwable ex) {
			ex.printStackTrace();
			return "error";
		}
		
	}
	
	private static void handleScopeMetrics(List<ScopeMetrics> scopeMetrics) {
		
		if (scopeMetrics != null && scopeMetrics.size() > 0) {
			
			for (ScopeMetrics sms : scopeMetrics) {
				
				logger.info("Scope Name = " + sms.getScope().getName());
				logger.info("Scope Version = " + sms.getScope().getVersion());
				logger.info("");
				handleAttributes(sms.getScope().getAttributesList());
				
				List<Metric> metrics = sms.getMetricsList();
				
				if (metrics != null && metrics.size() > 0) {
					
					int cntr = 1;
					for (Metric met : metrics) {
						logger.info("Metric # " + cntr + ": -------------->");
						String metricType = getMetricType(met);
						handleMetric(met, metricType);
						cntr++;
						
						logger.info("");
						
					}

				}
				
				logger.info("");
				
			}
			
		}
		
		
	}
	
	
	private static void handleAttributes(List<KeyValue> attrs) {
		
		for (KeyValue kv : attrs) {	
			
			if (kv.getValue().hasIntValue()) {
				
				logger.info("    -> " + kv.getKey() + ": " + kv.getValue().getIntValue());
			} else if (kv.getValue().hasBoolValue()) {
				
				logger.info("    -> " + kv.getKey() + ": " + kv.getValue().getBoolValue());
			} else if (kv.getValue().hasDoubleValue()) {
				
				logger.info("    -> " + kv.getKey() + ": " + kv.getValue().getDoubleValue());
			} else if (kv.getValue().hasKvlistValue()) {
				
				logger.info("          -> " + kv.getKey() + ": KvListValue");
				List<KeyValue> kvAttrList = kv.getValue().getKvlistValue().getValuesList();
				
				handleAttributes(kvAttrList);
				
				//logger.info("    -> " + kv.getKey() + ": " + kv.getValue().getStringValue());

			} else {
				logger.info("    -> " + kv.getKey() + ": " + kv.getValue().getStringValue());
				
			}
														
		}		
		
	}
	
	
	private static void handleResource(Resource resource) {
		
		logger.info("Metric Resource Attributes: -------------------------->");
		if (resource != null && resource.getAttributesList() != null && resource.getAttributesList().size() > 0) {
			
			handleAttributes(resource.getAttributesList());
			
			logger.info("");
		}
		
	}
	
	
	private static void handleMetric(Metric metric, String metricType) {
		
		logger.info("Metric Name = " + metric.getName());
		logger.info("Metric Description = " + metric.getDescription()); 
		

		if (metricType.equals("GAUGE")) {
			handleGaugeMetric(metric, metricType);
		} else if (metricType.equals("SUM")) {
			handleSumMetric(metric, metricType);
		}
		
		
		logger.info("");
		
	}
	
	
	private static void handleSumMetric(Metric metric, String metricType) {
		List<NumberDataPoint> dataPs = null;
		
		Sum sum = metric.getSum();
		dataPs = sum.getDataPointsList();
		if (dataPs != null) {
			metricType = "sum";
			logger.info("Metric Type = SUM");										
		} else {
			logger.info("Metric Sum = NULL");
		}				
		
		if (dataPs != null) {
			for (NumberDataPoint ndp : dataPs) {
				
				if (ndp.hasAsDouble()) {
					logger.info("Metric Double Value = " + ndp.getAsDouble());
				}
				
				if (ndp.hasAsInt()) {
					logger.info("Metric Int Value = " + ndp.getAsInt());
				}
				
				logger.info("Metric Attributes: -------------->");
				handleAttributes(ndp.getAttributesList());
				
				logger.info("");
				
			}
			
		} else {
			logger.info("Metric DataPoint List = NULL");
		}
	
		
	}
	
	private static void handleGaugeMetric(Metric metric, String metricType) {
		
		List<NumberDataPoint> dataPs = null;
		Gauge gauge = metric.getGauge();
		dataPs = gauge.getDataPointsList();
		if (dataPs != null) {
			metricType = "gauge";
			logger.info("Metric Type = GAUGE");		
			
			
		} else {
			logger.info("Metric Gauge = NULL");
		}
		
		if (dataPs != null) {
			for (NumberDataPoint ndp : dataPs) {
				
				if (ndp.hasAsDouble()) {
					logger.info("Metric Double Value = " + ndp.getAsDouble());
				}
				
				if (ndp.hasAsInt()) {
					logger.info("Metric Int Value = " + ndp.getAsInt());
				}
				
				logger.info("Metric Attributes: -------------->");
				
				handleAttributes(ndp.getAttributesList());
				
				
			}
			
		} else {
			logger.info("Metric DataPoint List = NULL");
		}
		
		

	}
	

	
	
	private static String getMetricType(Metric metric) {
		String metricType = null;
		DataCase dataCase = metric.getDataCase();
		switch (dataCase.getNumber()) {
		case 5:
			metricType = "GAUGE";
			break;

		case 7:
			metricType = "SUM";
			break;
			
		case 9:
			metricType = "HISTOGRAM";
			break;
			
		case 10:
			metricType = "EXPONENTIAL_HISTOGRAM";
			break;
			
		case 11:
			metricType = "SUMMARY";
			break;
			
		default:
			metricType = "data_not_set";
			break;
		}

		return metricType;
	}
	
	
	
}
