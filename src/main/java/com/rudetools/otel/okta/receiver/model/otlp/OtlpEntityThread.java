/**
 * 
 */
package com.rudetools.otel.okta.receiver.model.otlp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author james101
 *
 */
public class OtlpEntityThread implements Runnable {

	public static final Logger lgr = LoggerFactory.getLogger(OtlpEntityThread.class);
	
	private EntityInstance entity = null;
	private final String threadName;
	/**
	 * 
	 */
	public OtlpEntityThread(EntityInstance entityInst, String threadName) {
		
		this.entity = entityInst;
		this.threadName = threadName;
	}

	@Override
	public void run() {
		
		try {
			
			this.entity.startRecording();
			
			while (true) {
				
				Thread.currentThread().sleep(30000);
				lgr.info(this.threadName + " | Okta Otel Entity Thread | Sleeping for 30 seconds");
					
		
			}			
			
		} catch (Throwable ex) {
			ex.printStackTrace();
		}

	}


}
