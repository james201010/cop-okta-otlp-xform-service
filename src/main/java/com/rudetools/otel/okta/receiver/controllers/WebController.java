/**
 * 
 */
package com.rudetools.otel.okta.receiver.controllers;

import java.util.Calendar;
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

import com.rudetools.otel.okta.receiver.ApplicationCtx;
import com.rudetools.otel.okta.receiver.model.oag.OagCluster;

import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.common.v1.KeyValue;
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

	public static final Logger lgr = LoggerFactory.getLogger(WebController.class);
	private static long NUM_REQS = 1;
	
	/**
	 * 
	 */
	public WebController() {
		
	}

	
	@RequestMapping(value="/v1/metrics", method = RequestMethod.POST)
	public String receive(HttpServletRequest req, ModelMap model) {
	
		try {
			
			long startTime = Calendar.getInstance().getTimeInMillis();
			long reqctr = Long.parseLong("" + NUM_REQS);
			
			
			log("", true);
			log("", true);
			
			log("################### REQUEST " + reqctr + " BEGIN ###################", true);
			log("", true);
			
			int clength = Integer.parseInt(req.getHeader("content-length"));
			
			Enumeration<String> str = req.getHeaderNames();
			while (str.hasMoreElements()) {
				String hName = (String) str.nextElement();
				log(hName + " = " + req.getHeader(hName), true);
			}
			
			GZIPInputStream gzipis = new GZIPInputStream(req.getInputStream(), clength);

			log("", true);
			log("################ PAYLOAD " + reqctr + " BEGIN ################", true);
			log("", true);

			
			ExportMetricsServiceRequest emsReq = ExportMetricsServiceRequest.parseFrom(gzipis);
			
			List<ResourceMetrics> rms = emsReq.getResourceMetricsList();
			
			
			for (OagCluster oagClust : ApplicationCtx.CLUSTER_LIST) {
				
				oagClust.processRequest(rms);
			}
			
			
			
			for (ResourceMetrics rm : rms) {
				
				handleResource(rm.getResource());
				
				handleScopeMetrics(rm.getScopeMetricsList());

			}
			
			
			
			
			log("################ PAYLOAD " + reqctr + " END ################", true);
			log("", true);
			
			
			long endTime = Calendar.getInstance().getTimeInMillis();
			long totalTimeSecs = (endTime - startTime) / 1000;
			long totalTimeMins = totalTimeSecs / 60;			
			long minsInSecs = totalTimeMins * 60;
			long remainingSecs = totalTimeSecs - minsInSecs;
			
			
			log("", true);
			log(" - Request Total Elapsed Time = " + totalTimeMins + " minutes : " + remainingSecs + " seconds", true);
			log("", true);			
			log("", true);
			
			log("################### REQUEST " + reqctr + " END ###################", true);
			log("", true);
			log("", true);
			
			
			
			NUM_REQS++;
			
			
			
			return "success";
			
		} catch (Throwable ex) {
			
			ex.printStackTrace();
			log("################### REQUEST END ###################", true);
			log("", true);
			log("", true);

			NUM_REQS++;
			
			return "error";
		}
		
	}
	
	private static void log(String msg, boolean isInfo) {
		if (isInfo) {
			lgr.info(msg);
		} else {
			lgr.debug(msg);
		}
		
	}
	
	private static void handleScopeMetrics(List<ScopeMetrics> scopeMetrics) {
		
		if (scopeMetrics != null && scopeMetrics.size() > 0) {
			
			for (ScopeMetrics sms : scopeMetrics) {
				
				if (sms.getScope() != null) {

					log("Scope Name = " + sms.getScope().getName(), false);
					log("Scope Version = " + sms.getScope().getVersion(), false);

				}
				
				log("", true);
				handleAttributes(sms.getScope().getAttributesList());
				log("", true);
				
				List<Metric> metrics = sms.getMetricsList();
				
				if (metrics != null && metrics.size() > 0) {
					
					int cntr = 1;
					for (Metric met : metrics) {
						log("Metric # " + cntr + ": -------------->", false);
						String metricType = getMetricType(met);
						handleMetric(met, metricType);
						cntr++;
						
						log("", false);
						
					}

				}
				
				log("", false);
				
			}
			
		}
		
		
	}
	
	
	private static void handleAttributes(List<KeyValue> attrs) {
		
		for (KeyValue kv : attrs) {	
			
			if (kv.getValue().hasIntValue()) {
				
				log("    -> " + kv.getKey() + ": Int(" + kv.getValue().getIntValue() + ")", false);
				
			} else if (kv.getValue().hasBoolValue()) {
				
				log("    -> " + kv.getKey() + ": Bool(" + kv.getValue().getBoolValue() + ")", false);
				
			} else if (kv.getValue().hasDoubleValue()) {
				
				log("    -> " + kv.getKey() + ": Double(" + kv.getValue().getDoubleValue() + ")", false);
				
			} else if (kv.getValue().hasKvlistValue()) {
				
				log("          -> " + kv.getKey() + ": KvListValue Begin", false);
				List<KeyValue> kvAttrList = kv.getValue().getKvlistValue().getValuesList();
				
				handleAttributes(kvAttrList);
				log("", true);
				
				log("          -> " + kv.getKey() + ": KvListValue End", false);

			} else {
				
				log("    -> " + kv.getKey() + ": Str(" + kv.getValue().getStringValue() + ")", false);;
				
			}
			
			
														
		}		
		
	}
	
	
	private static void handleResource(Resource resource) {
		
		log("Metric Resource Attributes: -------------------------->", false);
		if (resource != null && resource.getAttributesList() != null && resource.getAttributesList().size() > 0) {
			
			handleAttributes(resource.getAttributesList());
			
			log("", false);
		}
		
	}
	
	
	private static void handleMetric(Metric metric, String metricType) {
		
		log("Metric Name = " + metric.getName(), false);
		log("Metric Description = " + metric.getDescription(), false); 
		//log("Metric Unit = " + metric.getUnit());
		
		

		if (metricType.equals("GAUGE")) {
			handleGaugeMetric(metric, metricType);
		} else if (metricType.equals("SUM")) {
			handleSumMetric(metric, metricType);
		}
		
		
		log("", false);
		
	}
	
	
	private static void handleSumMetric(Metric metric, String metricType) {
		List<NumberDataPoint> dataPs = null;
		
		Sum sum = metric.getSum();
		dataPs = sum.getDataPointsList();
		if (dataPs != null) {
			metricType = "sum";
			log("Metric Type = SUM", false);
			log("", false);
		} else {
			log("Metric Sum = NULL", false);
			log("", false);
		}				
		
		if (dataPs != null) {
			for (NumberDataPoint ndp : dataPs) {
				
				if (ndp.hasAsDouble()) {
					log("Metric Double Value = " + ndp.getAsDouble(), false);
				}
				
				if (ndp.hasAsInt()) {
					log("Metric Int Value = " + ndp.getAsInt(), false);
					
				}
				
				log("Metric Start Time = " + ndp.getStartTimeUnixNano(), false);
				
				log("Metric End Time = " + ndp.getTimeUnixNano(), false);
				
				
				log("Metric Attributes: -------------->", false);
				handleAttributes(ndp.getAttributesList());
				
				log("", false);
				
			}
			
		} else {
			log("Metric DataPoint List = NULL", false);
		}
	
		
	}
	
	private static void handleGaugeMetric(Metric metric, String metricType) {
		
		List<NumberDataPoint> dataPs = null;
		Gauge gauge = metric.getGauge();
		dataPs = gauge.getDataPointsList();
		if (dataPs != null) {
			metricType = "gauge";
			log("Metric Type = GAUGE", false);		
			log("", false);
			
		} else {
			log("Metric Gauge = NULL", false);
			log("", false);
		}
		
		if (dataPs != null) {
			for (NumberDataPoint ndp : dataPs) {
				
				if (ndp.hasAsDouble()) {
					log("Metric Double Value = " + ndp.getAsDouble(), false);
				}
				
				if (ndp.hasAsInt()) {
					log("Metric Int Value = " + ndp.getAsInt(), false);
				}
				
				log("Metric Attributes: -------------->", false);
				
				handleAttributes(ndp.getAttributesList());
				log("", false);
				
			}
			
		} else {
			log("Metric DataPoint List = NULL", false);
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
