package com.pikamachu.trafficgen.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.pikamachu.trafficgen.model.ElasticsearchLog;

/**
 * The Interface ElasticsearchLogsRepository.
 */
public interface ElasticsearchLogsRepository extends ElasticsearchRepository<ElasticsearchLog, String> {

	/**
	 * Find by environment.
	 *
	 * @param environment
	 *            the environment
	 * @param pageable
	 *            the pageable
	 * @return the list
	 */
	List<ElasticsearchLog> findByEnvironment(String environment, Pageable pageable);

	/**
	 * Find by environment and timestamp between.
	 *
	 * @param environment
	 *            the environment
	 * @param timestampFrom
	 *            the timestamp from
	 * @param timestampTo
	 *            the timestamp to
	 * @param pageable
	 *            the pageable
	 * @return the list
	 */
	@Query("{" +
		"\"bool\" : {" +
		      "\"must\" : [ {" +
		        "\"query_string\" : {" +
		          "\"query\" : \"?0\"," +
		          "\"fields\" : [ \"environment\" ]," +
		          "\"default_operator\" : \"and\"" +
		        "}" +
		      "}, {" +
		        "\"range\" : {" +
		          "\"@timestamp\" : {" +
		            "\"from\" : ?1," +
		            "\"to\" : ?2," +
		            "\"include_lower\" : true," +
		            "\"include_upper\" : true" +
		          "}" +
		        "}" +
		      "} ]" +
		    "}" +
		  "}")
	List<ElasticsearchLog> findByEnvironmentAndTimestampBetween(String environment, Long timestampFrom, Long timestampTo, Pageable pageable);
	
	@Query("{" +
		"\"bool\" : {" +
		      "\"must\" : [ {" +
		        "\"query_string\" : {" +
		          "\"query\" : \"?0\"," +
		          "\"fields\" : [ \"environment\" ]," +
		          "\"default_operator\" : \"and\"" +
		        "}" +
		      "}, {" +
		        "\"query_string\" : {" +
		          "\"query\" : \"?1\"," +
		          "\"fields\" : [ \"request\" ]," +
		          "\"default_operator\" : \"and\"" +
		        "}" +
		      "}, {" +
		        "\"range\" : {" +
		          "\"@timestamp\" : {" +
		            "\"from\" : ?2," +
		            "\"to\" : ?3," +
		            "\"include_lower\" : true," +
		            "\"include_upper\" : true" +
		          "}" +
		        "}" +
		      "} ]" +
		    "}" +
		  "}")
	List<ElasticsearchLog> findByEnvironmentAndRequestAndTimestampBetween(String environment, String request, Long timestampFrom, Long timestampTo, Pageable pageable);
	
	/**
	 * Find by environment and not request and timestamp between.
	 *
	 * @param environment
	 *            the environment
	 * @param request
	 *            the request
	 * @param timestampFrom
	 *            the timestamp from
	 * @param timestampTo
	 *            the timestamp to
	 * @param pageable
	 *            the pageable
	 * @return the list
	 */
	@Query("{" +
		"\"bool\" : {" +
		      "\"must\" : [ {" +
		        "\"query_string\" : {" +
		          "\"query\" : \"?0\"," +
		          "\"fields\" : [ \"environment\" ]," +
		          "\"default_operator\" : \"and\"" +
		        "}" +
		      "}, {" +
		        "\"query_string\" : {" +
		          "\"query\" : \"?1\"," +
		          "\"fields\" : [ \"request\" ]," +
		          "\"default_operator\" : \"and\"" +
		        "}" +
		      "}, {" +
		        "\"range\" : {" +
		          "\"@timestamp\" : {" +
		            "\"from\" : ?3," +
		            "\"to\" : ?4," +
		            "\"include_lower\" : true," +
		            "\"include_upper\" : true" +
		          "}" +
		        "}" +
		      "} ]," +
		      "\"must_not\" : [ {" +
		        "\"query_string\" : {" +
		          "\"query\" : \"?2\"," +
		          "\"fields\" : [ \"request\" ]," +
		          "\"default_operator\" : \"and\"" +
		        "}" +
			  "} ]" +
		    "}" +
		  "}")
	List<ElasticsearchLog> findByEnvironmentAndRequestAndNotRequestAndTimestampBetween(String environment, String request, String notRequest, Long timestampFrom, Long timestampTo, Pageable pageable);

}
