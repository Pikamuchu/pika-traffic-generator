package com.pikamachu.trafficgen.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.pikamachu.trafficgen.batch.ElasticsearchLogsItemProcessor;
import com.pikamachu.trafficgen.batch.ElasticsearchLogsItemReader;
import com.pikamachu.trafficgen.batch.TrafficRequestItemWriter;
import com.pikamachu.trafficgen.model.ElasticsearchLog;
import com.pikamachu.trafficgen.model.TrafficRequest;

/**
 * The Class TrafficBatchConfiguration.
 */
@Configuration
@ConfigurationProperties(prefix = "traffic.batch.config")
public class TrafficBatchConfiguration {

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(TrafficBatchConfiguration.class);

	/** The context. */
	@Autowired
	private ApplicationContext context;
	
	/** The job builder factory. */
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	/** The step builder factory. */
	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	/**
	 * Traffic job.
	 *
	 * @return the job
	 */
	@Bean
	public Job elasticsearchLogsJob() {
		return jobBuilderFactory
				.get(jobName)
				.incrementer(new RunIdIncrementer())
				.start(elasticsearchLogsFlow())
				.end()
				.build();
	}

	/**
	 * Job Flow.
	 *
	 * @return the step
	 */
	public Flow elasticsearchLogsFlow() {
		FlowBuilder<Flow> splitFlow = new FlowBuilder<Flow>("Parallel Flows");

		if (concurrentFlows >= 1) {
			List<Flow> flowList = new ArrayList<Flow>();
			for (int i = 1; i <= concurrentFlows; i++) {
				flowList.add(new FlowBuilder<Flow>(flowPrefixName + i)
						.from(elasticsearchLogsStep(i))
						.end());
			}
			splitFlow = splitFlow.split(new SimpleAsyncTaskExecutor())
					.add(flowList.toArray(new Flow[flowList.size()]));
		}

		return splitFlow.build();
	}

	/**
	 * Job steps.
	 *
	 * @param stepNumber
	 *            the step number
	 * @return the step
	 */
	public Step elasticsearchLogsStep(int stepNumber) {
		return stepBuilderFactory
				.get(stepPrefixName + stepNumber)
				.<ElasticsearchLog, TrafficRequest>chunk(stepsChunk)
				.reader(elasticsearchLogsItemReader(stepNumber))
				.processor(elasticsearchLogsItemProcessor())
				.writer(elasticsearchLogsItemWriter())
				.build();
	}

	/**
	 * Elasticsearch logs item reader.
	 *
	 * @param stepNumber
	 *            the step number
	 * @return the elasticsearch logs item reader
	 */
	public ElasticsearchLogsItemReader elasticsearchLogsItemReader(int stepNumber) {
		ElasticsearchLogsItemReader reader = (ElasticsearchLogsItemReader) context.getBean(ElasticsearchLogsItemReader.class);
		
		setReaderTimestampRange(reader, stepNumber);
		setReaderRequestQuery(reader, stepNumber);
		setReaderInitialSleep(reader, stepNumber);
		
		return reader;
	}

	/**
	 * Sets the reader timestamp range.
	 *
	 * @param reader
	 *            the reader
	 * @param stepNumber
	 *            the step number
	 */
	private void setReaderTimestampRange(ElasticsearchLogsItemReader reader, int stepNumber) {
		Long timestampRange = reader.getTimestampRange();
		if (this.steppedDistributionFlow != null && this.steppedDistributionFlow) {
			// Calculating timestampRangeFlow(n) = timestampRange - ((n * timestampRange)/(concurrentFlows + 1))
			Long timestampRangeFlow = timestampRange - ((stepNumber * timestampRange)/(this.concurrentFlows + 1));
			log.info("timestampRangeFlow({}) is {}", stepNumber, timestampRangeFlow);
			reader.setTimestampRange(timestampRangeFlow);	
		}
	}

	/**
	 * Sets the reader request query.
	 *
	 * @param reader
	 *            the reader
	 * @param stepNumber
	 *            the step number
	 */
	private void setReaderRequestQuery(ElasticsearchLogsItemReader reader, int stepNumber) {
		if (this.locales != null && !this.locales.isEmpty()) {
			int index = ((stepNumber-1) % this.locales.size());
			String query = reader.getIncludeOnlyRequestsQuery();
			String localeQuery = "*" + this.locales.get(index) + "*";
			if (StringUtils.isEmpty(query)) {
				query = localeQuery;
			} else {
				query = query + " AND " + localeQuery;
			}
			log.debug("includeOnlyRequestsQuery({}) is {}", stepNumber, query);
			reader.setIncludeOnlyRequestsQuery(query);
		}
	}

	/**
	 * Sets the reader initial sleep.
	 *
	 * @param reader
	 *            the reader
	 * @param stepNumber
	 *            the step number
	 */
	private void setReaderInitialSleep(ElasticsearchLogsItemReader reader, int stepNumber) {
		if (this.concurrentFlows > 1 && this.rampUp != null && this.rampUp > 0) {
			// Calculating rampUp(n)
			Long initialSleepTime = Double.valueOf(((stepNumber-1) * ((this.rampUp/(this.concurrentFlows-1)))) * 1000).longValue();
			log.debug("initialSleepTime({}) is {} ms", stepNumber, initialSleepTime);
			reader.setInitialSleepTime(initialSleepTime);	
		}
	}

	/**
	 * Processor.
	 *
	 * @return the elasticsearch logs item processor
	 */
	public ElasticsearchLogsItemProcessor elasticsearchLogsItemProcessor() {
		return (ElasticsearchLogsItemProcessor) context.getBean(ElasticsearchLogsItemProcessor.class);
	}

	/**
	 * Writer.
	 *
	 * @return the item writer
	 */
	public TrafficRequestItemWriter elasticsearchLogsItemWriter() {
		return (TrafficRequestItemWriter) context.getBean(TrafficRequestItemWriter.class);
	}
	
	/** The job name. */
	private String jobName;

	/** The step prefix name. */
	private String stepPrefixName;

	/** The flow prefix name. */
	private String flowPrefixName;

	/** The number of concurrent flows. */
	private Integer concurrentFlows = 1;
	
	/** The stepped distribution flow. */
	private Boolean steppedDistributionFlow = false;

	/** The steps chunk. */
	private Integer stepsChunk = 100;
	
	/** The time limit. */
	private Integer timeLimit = 0;
	
	/** The ramp up. */
	private Integer rampUp = 100;
	
	/** The locales. */
	private List<String> locales;

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getStepPrefixName() {
		return stepPrefixName;
	}

	public void setStepPrefixName(String stepPrefixName) {
		this.stepPrefixName = stepPrefixName;
	}

	public String getFlowPrefixName() {
		return flowPrefixName;
	}

	public void setFlowPrefixName(String flowPrefixName) {
		this.flowPrefixName = flowPrefixName;
	}

	public Integer getConcurrentFlows() {
		return concurrentFlows;
	}

	public void setConcurrentFlows(Integer concurrentFlows) {
		this.concurrentFlows = concurrentFlows;
	}

	public Integer getStepsChunk() {
		return stepsChunk;
	}

	public void setStepsChunk(Integer stepsChunk) {
		this.stepsChunk = stepsChunk;
	}

	public Boolean getSteppedDistributionFlow() {
		return steppedDistributionFlow;
	}

	public void setSteppedDistributionFlow(Boolean steppedDistributionFlow) {
		this.steppedDistributionFlow = steppedDistributionFlow;
	}

	public List<String> getLocales() {
		return locales;
	}

	public void setLocales(List<String> locales) {
		this.locales = locales;
	}

	public Integer getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}

	public Integer getRampUp() {
		return rampUp;
	}

	public void setRampUp(Integer rampUp) {
		this.rampUp = rampUp;
	}

}
