package com.pikamachu.trafficgen.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.pikamachu.trafficgen.model.ElasticsearchLog;
import com.pikamachu.trafficgen.model.TrafficRequest;
import com.pikamachu.trafficgen.service.TrafficDummyDataService;

/**
 * The Class ElasticsearchLogsItemProcessor.
 */
@Component
@ConfigurationProperties(prefix = "traffic.batch.processor")
public class ElasticsearchLogsItemProcessor implements ItemProcessor<ElasticsearchLog, TrafficRequest> {

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(ElasticsearchLogsItemProcessor.class);
	
	private static final String API_CONTEXT_PATH = "/api";

	@Autowired
	TrafficDummyDataService trafficDummyDataService;
	
	/**
	 * Instantiates a new elasticsearch logs item processor.
	 */
	ElasticsearchLogsItemProcessor() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.item.ItemProcessor#process(java.lang.Object)
	 */
	@Override
	public TrafficRequest process(final ElasticsearchLog elasticsearchLog) throws Exception {
		log.debug("start process: {}", elasticsearchLog);
		
		TrafficRequest trafficRequest = new TrafficRequest();
		trafficRequest.setId(elasticsearchLog.getId());
		trafficRequest.setMethod(elasticsearchLog.getVerb());
		trafficRequest.setUrl(doUrlModifications(elasticsearchLog.getRequest()));
		trafficRequest.setUserAgent(elasticsearchLog.getAgent());
		trafficRequest.setPostData(doPostDataModifications(elasticsearchLog.getRequest(), elasticsearchLog.getData()));
		
		log.debug("finish process: {}", trafficRequest);
		
		return trafficRequest;
	}
	
	/**
	 * Do url modifications.
	 *
	 * @param request
	 *            the request
	 * @return the string
	 */
	private String doUrlModifications(String request) {
		String url = new String(request);

		// Only on API requests
		if (url.startsWith(API_CONTEXT_PATH)) {
			url = trafficDummyDataService.doRequestAddQuerystring(url);
		// Only on SEO Pages
		} else { 
			url = trafficDummyDataService.doRequestReplacements(url);
		}

		return url;
	}
	
	/**
	 * Do post data modifications.
	 *
	 * @param request
	 *            the request
	 * @param data
	 *            the data
	 * @return the string
	 */
	private String doPostDataModifications(String request, String data) {
		// Only on API requests
		if (request.startsWith(API_CONTEXT_PATH)) {
			return trafficDummyDataService.doPostDataModifications(request, data);
		} else {
			return data;
		}		
	}

}
