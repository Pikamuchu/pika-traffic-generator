package com.pikamachu.trafficgen.component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.InfoEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.pikamachu.trafficgen.service.TrafficStatsService;

/**
 * The Class TrafficInfoEndpoint.
 */
@ConfigurationProperties(prefix = "endpoints.info")
public class TrafficInfoEndpoint extends InfoEndpoint {
	
	
	/** The additional info map. */
	private Map<String, Object> additionalInfoMap = new LinkedHashMap<String, Object>();

	/** The service. */
	private TrafficStatsService statsService;

	/**
	 * Instantiates a new traffic info component.
	 *
	 * @param statsService
	 *            the stats service
	 */
	public TrafficInfoEndpoint(TrafficStatsService statsService) {
		super(new LinkedHashMap<String, Object>());
		super.setId("info");
		super.setSensitive(false);
		this.statsService = statsService;
	}

	@Override
	public Map<String, Object> invoke() {
		Map<String, Object> info = new LinkedHashMap<String, Object>();
		info.putAll(getAdditionalInfo());
		info.putAll(statsService.getSummaryInfoMap());
		return info;
	}
	
	protected Map<String, Object> getAdditionalInfo() {
		return additionalInfoMap;
	}
	
	public void putAdditionalInfo(Map<String, Object> info) {
		this.additionalInfoMap.putAll(info);
	}

}
