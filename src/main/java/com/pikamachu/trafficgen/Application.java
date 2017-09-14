package com.pikamachu.trafficgen;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * The Class Application.
 */
@SpringBootApplication
@ComponentScan("com.pikamachu.trafficgen")
@EnableConfigurationProperties
@EnableBatchProcessing
@EnableAutoConfiguration
@EnableDiscoveryClient
public class Application {

	/**
	 * Main spring boot application.
	 * 
	 * @param args
	 *            arguments
	 */
	public static void main(String[] args) {
		// System.exit is common for Batch applications since the exit code can be used
		// to drive a workflow
		System.exit(SpringApplication.exit(SpringApplication.run(Application.class, args)));
	}

}
