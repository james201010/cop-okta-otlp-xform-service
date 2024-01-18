/**
 * 
 */
package com.rudetools.otel.okta.receiver.model.otlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rudetools.otel.okta.receiver.utils.AnyVal;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter;
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.resources.ResourceBuilder;

/**
 * 
 * @author james101
 *
 */
public class EntityInstance {

	public static final Logger lgr = LoggerFactory.getLogger(EntityInstance.class);
	
	private Map<String, AnyVal> attributesMap = null;
	private List<MetricDefinition> metricDefinitions = null;
	
	private Resource resource = null;
	private OpenTelemetry otelsdk = null;
	private Meter meter = null;
	
	
	/**
	 * Each instance will have its own OpenTelemetry instance, set with the entity instance resource attributes
	 * 
	 */
	public EntityInstance(Map<String, AnyVal> resourceAttrs, String scope) {
		
		this.attributesMap = resourceAttrs;
		this.resource = generateEntityResourceAttributes(this.attributesMap);
		this.otelsdk = generateOtelSdk(this.resource);
		this.meter = this.otelsdk.getMeterProvider().get(scope);
		
	}

	// !!!!  List<MetricDefinition> (this.metricDefinitions) must be populated before this method is called
	// !!!!  List<MetricDataPoint> (this.dataPoints) must be populated in each MetricDefinition before this method is called
	public void startRecording() {
		
		log("!!!!!!!!!!!!!!!!!!! start recording !!!!!!!!!!!!!!!!!!!", false);
		if (this.metricDefinitions != null) {
			for (MetricDefinition def : this.metricDefinitions) {
				def.startRecording();
			}
		}
		log("!!!!!!!!!!!!!!!!!!! finished start recording !!!!!!!!!!!!!!!!!!!", false);
		
	}
	
	
	public void addMetricDefinition(MetricDefinition def) {
		
		if (this.metricDefinitions == null) {
			this.metricDefinitions = new ArrayList<MetricDefinition>();
		}
		
		this.metricDefinitions.add(def);
	}
	
	public List<MetricDefinition> getMetricDefinitions() {
		return metricDefinitions;
	}


	public void setMetricDefinitions(List<MetricDefinition> metricDefinitions) {
		this.metricDefinitions = metricDefinitions;
	}


	public Meter getMeter() {
		return this.meter;
	}


	private static Resource generateEntityResourceAttributes(final Map<String, AnyVal> attrsMap) {
		
		ResourceBuilder rb = Resource.getDefault().toBuilder();
		
		Set<String> attrsKeys = attrsMap.keySet();
		
		for(String key : attrsKeys) {
			
			AnyVal aval = attrsMap.get(key);
			if (aval.hasBooleanVal()) {
				rb.put(key, aval.getBooleanVal());
			} else if (aval.hasDoubleVal()) {
				rb.put(key, aval.getDoubleVal());
			} else if (aval.hasLongVal()) {
				rb.put(key, aval.getLongVal());
			} else if (aval.hasStringVal()) {
				rb.put(key, aval.getStringVal());
			}
			
		}
		
		return rb.build();
	}
	
	
	
	private static OpenTelemetry generateOtelSdk(final Resource res) {
		
	    SdkMeterProvider sdkMeterProvider = SdkMeterProvider.builder().setResource(res)
	            .registerMetricReader(PeriodicMetricReader.builder(OtlpHttpMetricExporter.builder().build()).build())
	            //.setResource(resource)
	            .build();
	    

	    SdkLoggerProvider sdkLoggerProvider = SdkLoggerProvider.builder().setResource(res)
	            .addLogRecordProcessor(
	                    BatchLogRecordProcessor.builder(OtlpHttpLogRecordExporter.builder().build()).build())
	            //.setResource(resource)
	            .build();

	    OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
	        //.setTracerProvider(sdkTracerProvider)
	        .setMeterProvider(sdkMeterProvider)
	        .setLoggerProvider(sdkLoggerProvider)
	        
	        // TODO comment out line below and test
	        .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
	        
	        .build();
	        //.buildAndRegisterGlobal();	
	    
	    return openTelemetry;		
	}

	private static void log(String msg, boolean isInfo) {
		if (isInfo) {
			lgr.info(msg);
		} else {
			//lgr.info(msg);
		}
		
	}
	
}
