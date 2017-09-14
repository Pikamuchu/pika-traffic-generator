package com.pikamachu.trafficgen.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class ElasticsearchLog.
 */
// Apache logs
// @Document(indexName = "logstash-b2c-latest", type = "httpd-access-log")
// Nginx logs
@Document(indexName = "logstash-ngb2c-latest", type = "nginx-cache-access-log")
public class ElasticsearchLog implements Serializable {

	/** The serialVersionUID. */
	private static final long serialVersionUID = -7589327488253031711L;

	/** The id. */
	@Id
	private String id;

	/** The environment. */
	private String environment;

	/** The verb. */
	private String verb;

	/** The request. */
	private String request;

	/** The response. */
	private String response;

	/** The agent. */
	private String agent;

	/** The timestamp. */
	@Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
	@JsonProperty(value = "@timestamp")
	private Date timestamp;

	/** The data. */
	private String data;

	/**
	 * Instantiates a new traffic interaction.
	 */
	public ElasticsearchLog() {
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
