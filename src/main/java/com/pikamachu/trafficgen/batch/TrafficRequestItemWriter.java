package com.pikamachu.trafficgen.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.pikamachu.trafficgen.model.TrafficRequest;
import com.pikamachu.trafficgen.service.TrafficRequestService;

/**
 * The Class TrafficRequestItemWriter.
 */
@Component
@Scope("prototype")
public class TrafficRequestItemWriter implements ItemWriter<TrafficRequest> {

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(TrafficRequestItemWriter.class);

	/** The trafficRequestService. */
	@Autowired
	private TrafficRequestService trafficRequestService;

	public TrafficRequestItemWriter() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 */
	@Override
	public void write(List<? extends TrafficRequest> items) throws Exception {
		if (!CollectionUtils.isEmpty(items)) {
			for (TrafficRequest item : items) {
				try {
					log.debug("Writing traffic request: {}", item);
					trafficRequestService.send(item);
				} catch (Exception e) {
					log.error("Error writing Traffic Request: {}. Exception {}", item, e);
				}
			}
		}
	}

}