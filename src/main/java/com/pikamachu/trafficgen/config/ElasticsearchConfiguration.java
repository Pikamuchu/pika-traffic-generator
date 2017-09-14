package com.pikamachu.trafficgen.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * The Class ElasticsearchConfiguration.
 */
@Configuration
@ConfigurationProperties(prefix = "traffic.elasticsearch")
@EnableElasticsearchRepositories(basePackages = "com.pikamachu.trafficgen.repository")
public class ElasticsearchConfiguration {

	/** The host name. */
	protected String host;

	/** The port number. */
	protected String port;

	/**
	 * Client.
	 *
	 * @return the client
	 * @throws Exception
	 *             the exception
	 */
	@Bean
	public Client client() throws Exception {
		TransportClient client = new TransportClient();
		TransportAddress address = new InetSocketTransportAddress(this.host, Integer.parseInt(this.port));
		client.addTransportAddress(address);
		return client;
	}

	/**
	 * Elasticsearch template.
	 *
	 * @return the elasticsearch operations
	 * @throws Exception
	 *             the exception
	 */
	@Bean
	public ElasticsearchOperations elasticsearchTemplate() throws Exception {
		return new ElasticsearchTemplate(client());
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

}
