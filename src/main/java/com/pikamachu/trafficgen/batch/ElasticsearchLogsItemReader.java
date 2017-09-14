package com.pikamachu.trafficgen.batch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.pikamachu.trafficgen.model.ElasticsearchLog;
import com.pikamachu.trafficgen.repository.ElasticsearchLogsRepository;

/**
 * The Class ElasticsearchLogsItemReader.
 */
@Component
@Scope("prototype")
@ConfigurationProperties(prefix = "traffic.batch.reader")
public class ElasticsearchLogsItemReader implements ItemReader<ElasticsearchLog> {

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(ElasticsearchLogsItemReader.class);

	/** The Constant MAX_PAGE_SIZE. */
	private static final int DEFAULT_MAX_PAGE_SIZE = 1000;

	/** The Constant DEFAULT_TIMESTAMP_RANGE. */
	private static final long DEFAULT_TIMESTAMP_RANGE = 300000L;

	/** The Constant DEFAULT_NO_MORE_LOGS_FOUND_SLEEP_TIME. */
	private static final long DEFAULT_NO_MORE_LOGS_FOUND_SLEEP_TIME = 30000L;

	/** The elasticsearch logs repository. */
	@Autowired
	private ElasticsearchLogsRepository elasticsearchLogsRepository;
	
	/**
	 * Instantiates a new elasticsearch logs item reader.
	 */
	public ElasticsearchLogsItemReader() {
	}

	/** The list. */
	private List<ElasticsearchLog> list;

	@Override
	public ElasticsearchLog read() {
		ElasticsearchLog elasticsearchLog = null;

		log.debug("start read: ");
		
		doInitialSleep();

		do {

			if (isNoMoreItemsToProccess()) {

				log.debug("    Elasticsearch getting logs...");

				List<ElasticsearchLog> results = null;
				try {
					PageRequest pagination = new PageRequest(0, maxPageSize,
							new Sort(new Sort.Order(Sort.Direction.ASC, "@timestamp")));
					long now = Calendar.getInstance().getTimeInMillis();
					// Using request query
					if (StringUtils.isBlank(this.discardRequestsQuery)) {
						results = elasticsearchLogsRepository.findByEnvironmentAndRequestAndTimestampBetween(
								this.logsEnvironment,
								this.includeOnlyRequestsQuery,
								this.timestampFromRange, 
								now, 
								pagination);
					} else {
						results = elasticsearchLogsRepository.findByEnvironmentAndRequestAndNotRequestAndTimestampBetween(
								this.logsEnvironment,
								this.includeOnlyRequestsQuery,
								this.discardRequestsQuery, 
								this.timestampFromRange, 
								now,
								pagination);
					}
				} catch (Exception e) {
					log.error("Something goes wrong with elasticsearch repository! {}", e);
				}

				if (results != null && !results.isEmpty()) {
					list = new ArrayList<ElasticsearchLog>();
					list.addAll(results);
					log.info("    Found {} logs!", list.size());
				}

				if (isNoMoreItemsToProccess()) {
					if (finishOnNoMoreLogsFound) {
						log.info("    No logs found. Breaking!");
						break;
					} else {
						log.info("    No logs found. Sleeping {} millis!", noMoreLogsFoundSleepTime);
						sleep(noMoreLogsFoundSleepTime);
					}
				}
			}
			if (list != null && !list.isEmpty()) {
				elasticsearchLog = list.remove(0);
				timestampFromRange = elasticsearchLog.getTimestamp().getTime();
			}
		} while (elasticsearchLog == null);

		log.debug("finish read: {}", elasticsearchLog);

		return elasticsearchLog;
	}

	/**
	 * Do initial sleep.
	 *
	 * @return true, if successful
	 */
	private void doInitialSleep() {
		if (initialSleepTime > 0) {
			log.debug("    Initial sleeping {} millis!", initialSleepTime);
			sleep(initialSleepTime);
			initialSleepTime = 0L;
		}
	}
	
	private boolean isNoMoreItemsToProccess() {
		return (list == null || list.isEmpty());
	}
	
	/**
	 * Sleep.
	 *
	 * @param sleepTime
	 *            the sleep time
	 */
	private void sleep(Long sleepTime) {
		try {
			Thread.sleep(sleepTime);
		} catch (Exception e) {
		}
	}
	
	/** Configuration properties. */

	/** The timestamp from range. */
	private Long timestampFromRange = Calendar.getInstance().getTimeInMillis() - DEFAULT_TIMESTAMP_RANGE;

	/** The max page size. */
	private Integer maxPageSize = DEFAULT_MAX_PAGE_SIZE;

	/** The timestamp range. */
	private Long timestampRange = DEFAULT_TIMESTAMP_RANGE;

	/** The no more logs found sleep time. */
	private Long noMoreLogsFoundSleepTime = DEFAULT_NO_MORE_LOGS_FOUND_SLEEP_TIME;

	/** The finish on no more logs found. */
	private Boolean finishOnNoMoreLogsFound = Boolean.FALSE;
	
	/** The include only requests query. */
	private String includeOnlyRequestsQuery = "*";

	/** The discard requests query. */
	private String discardRequestsQuery;

	/** The logs environment. */
	private String logsEnvironment = "PRO";
	
	/** The initial sleep time. */
	private Long initialSleepTime = 0L;

	public ElasticsearchLogsRepository getElasticsearchLogsRepository() {
		return elasticsearchLogsRepository;
	}

	public void setElasticsearchLogsRepository(ElasticsearchLogsRepository elasticsearchLogsRepository) {
		this.elasticsearchLogsRepository = elasticsearchLogsRepository;
	}

	public Integer getMaxPageSize() {
		return maxPageSize;
	}

	public void setMaxPageSize(Integer maxPageSize) {
		this.maxPageSize = maxPageSize;
	}

	public Long getTimestampRange() {
		return timestampRange;
	}

	public void setTimestampRange(Long timestampRange) {
		this.timestampRange = timestampRange;
		if (timestampRange > 0) {
			this.timestampFromRange = Calendar.getInstance().getTimeInMillis() - timestampRange;
		}
	}

	public Long getNoMoreLogsFoundSleepTime() {
		return noMoreLogsFoundSleepTime;
	}

	public void setNoMoreLogsFoundSleepTime(Long noMoreLogsFoundSleepTime) {
		this.noMoreLogsFoundSleepTime = noMoreLogsFoundSleepTime;
	}

	public Boolean getFinishOnNoMoreLogsFound() {
		return finishOnNoMoreLogsFound;
	}

	public void setFinishOnNoMoreLogsFound(Boolean finishOnNoMoreLogsFound) {
		this.finishOnNoMoreLogsFound = finishOnNoMoreLogsFound;
	}

	public String getDiscardRequestsQuery() {
		return discardRequestsQuery;
	}

	public void setDiscardRequestsQuery(String discardRequestsQuery) {
		this.discardRequestsQuery = discardRequestsQuery;
	}

	public String getIncludeOnlyRequestsQuery() {
		return includeOnlyRequestsQuery;
	}

	public void setIncludeOnlyRequestsQuery(String includeOnlyRequestsQuery) {
		this.includeOnlyRequestsQuery = includeOnlyRequestsQuery;
	}

	public Long getInitialSleepTime() {
		return initialSleepTime;
	}

	public void setInitialSleepTime(Long initialSleepTime) {
		this.initialSleepTime = initialSleepTime;
	}

}