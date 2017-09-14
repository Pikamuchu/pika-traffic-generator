package com.pikamachu.trafficgen.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Class BatchApplication.
 */
@RestController
public class DiscoveryRest {
	
	/** The discovery client. */
	@Autowired
	private DiscoveryClient discoveryClient;
	
	/**
	 * Me.
	 *
	 * @return the service instance
	 */
	@RequestMapping("/me")
	public ServiceInstance me() {
		return discoveryClient.getLocalServiceInstance();
	}
	
	/**
	 * Instances.
	 *
	 * @return the list
	 */
	@RequestMapping("/instances")
	public List<ServiceInstance> instances() {
		return discoveryClient.getInstances("pika-traffic-generator");
	}

}
