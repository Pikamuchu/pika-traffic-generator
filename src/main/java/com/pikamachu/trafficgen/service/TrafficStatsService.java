package com.pikamachu.trafficgen.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

/**
 * The Class TrafficStatsService.
 */
@Service
@ConfigurationProperties(prefix = "traffic.stats.service")
public class TrafficStatsService {

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(TrafficStatsService.class);

	/** The Constant SUMMARY. */
	private static final String SUMMARY = "summary";

	/** The Constant API_GET. */
	private static final String API_GET = "api-" + HttpMethod.GET;

	/** The Constant API_POST. */
	private static final String API_POST = "api-" + HttpMethod.POST;

	/** The Constant PAGE. */
	private static final String PAGE = "page";

	/** The Constant API_REGEX. */
	private static final String API_REGEX = ".*/api/.*";

	/** The response time. */
	Map<String, SummaryStatistics> responseTime = new LinkedHashMap<String, SummaryStatistics>();

	/**
	 * Calculate stats.
	 *
	 * @param method
	 *            the method
	 * @param url
	 *            the url
	 * @param time
	 *            the time
	 */
	public void addValue(String method, String url, long time) {
		if (this.enabled == null || !this.enabled) {
			return;
		}

		if (responseTime == null) {
			responseTime = new LinkedHashMap<String, SummaryStatistics>();
		}

		// Removing url querystring params
		String key = null;
		if (url.indexOf("?") > 0) {
			key = url.substring(0, url.indexOf("?"));
		} else {
			key = url;
		}

		// Global stats
		addTimeValueStats(SUMMARY, time);
		if (url.matches(API_REGEX)) {
			if (method.equalsIgnoreCase(HttpMethod.GET.name())) {
				addTimeValueStats(API_GET, time);
			} else {
				addTimeValueStats(API_POST, time);
			}
		} else {
			addTimeValueStats(PAGE, time);
		}

		// Detailed stats
		if (this.withDetails != null && this.withDetails) {
			addTimeValueStats(key, time);
		}

	}

	/**
	 * Adds the time value stats.
	 *
	 * @param key
	 *            the key
	 * @param time
	 *            the time
	 */
	private void addTimeValueStats(String key, double time) {
		SummaryStatistics timeStats = responseTime.get(key);
		if (timeStats == null) {
			timeStats = new SummaryStatistics();
			responseTime.put(key, timeStats);
		}
		timeStats.addValue(time);
	}

	/**
	 * Reset stats.
	 */
	public void resetStats() {
		this.responseTime = new LinkedHashMap<String, SummaryStatistics>();
	}

	@Override
	public String toString() {
		return "TrafficStatsService [responseTime=" + responseTime + "]";
	}

	/**
	 * To html.
	 *
	 * @return the string
	 */
	public String toHtml() {
		StringBuffer stats = new StringBuffer();
		stats.append(SUMMARY + " (ms) " + responseTime.get(SUMMARY));
		stats.append("\n" + PAGE + " (ms) " + responseTime.get(PAGE));
		stats.append("\n" + API_GET + " (ms) " + responseTime.get(API_GET));
		stats.append("\n" + API_POST + " (ms) " + responseTime.get(API_POST));
		for (Map.Entry<String, SummaryStatistics> entry : responseTime.entrySet()) {
			String key = entry.getKey();
			if (key == null || key.equalsIgnoreCase(SUMMARY) || key.equalsIgnoreCase(PAGE)
					|| key.equalsIgnoreCase(API_GET) || key.equalsIgnoreCase(API_POST)) {
				continue;
			}
			stats.append("\nRequests " + entry.getKey() + " (ms) " + entry.getValue().toString());
		}
		return stats.toString();
	}

	public Map<String, String> getSummaryInfoMap() {
		Map<String, String> summaryInfo = new LinkedHashMap<String, String>();
		if (responseTime != null) {
			summaryInfo.put("stats." + SUMMARY, toInfoString(responseTime.get(SUMMARY)));
			summaryInfo.put("stats." + PAGE, toInfoString(responseTime.get(PAGE)));
			summaryInfo.put("stats." + API_GET, toInfoString(responseTime.get(API_GET)));
			summaryInfo.put("stats." + API_POST, toInfoString(responseTime.get(API_POST)));
		}
		return summaryInfo;
	}

	private String toInfoString(SummaryStatistics summaryStatistics) {
		StringBuffer info = new StringBuffer();
		if (summaryStatistics != null) {
			info.append(summaryStatistics.getN() + " requests");
			info.append(" " + Double.valueOf(summaryStatistics.getMean()).longValue() 
					+ " (" + Double.valueOf(summaryStatistics.getMin()).longValue() 
					+ "-" + Double.valueOf(summaryStatistics.getMax()).longValue() + ") ms response time.");
		}
		return info.toString();
	}

	/** The enabled. */
	private Boolean enabled;

	/** The with details. */
	private Boolean withDetails;

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getWithDetails() {
		return withDetails;
	}

	public void setWithDetails(Boolean withDetails) {
		this.withDetails = withDetails;
	}

}
