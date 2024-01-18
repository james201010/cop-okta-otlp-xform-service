package com.rudetools.otel.okta.receiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class OtlpReceiverApplication {

	public static void main(String[] args) {
		
		//SpringApplication.run(OtlpReceiverApplication.class, args);
	    SpringApplication springApplication = new SpringApplication(OtlpReceiverApplication.class);
	    springApplication.addListeners(new OtlpReceiverAppListener());
	    springApplication.run(args);		
	}

}
