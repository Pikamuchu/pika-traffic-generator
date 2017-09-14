package com.pikamachu.trafficgen.model;

import java.io.Serializable;

/**
 * The Class TrafficRequest.
 */
public class TrafficRequest implements Serializable {

	/** The serialVersionUID. */
	private static final long serialVersionUID = -7589327488253031711L;

	/** The id. */
	private String id;

	/** The url. */
	private String method;

	/** The url. */
	private String url;

	/** The user agent. */
	private String userAgent;

	/** The post data. */
	private String postData;

	/**
	 * Instantiates a new traffic interaction.
	 */
	public TrafficRequest() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getPostData() {
		return postData;
	}

	public void setPostData(String postData) {
		this.postData = postData;
	}

	@Override
	public String toString() {
		return "TrafficRequest [id=" + id + ", method=" + method + ", url=" + url + ", userAgent=" + userAgent
				+ ", postData=" + postData + "]";
	}

}
