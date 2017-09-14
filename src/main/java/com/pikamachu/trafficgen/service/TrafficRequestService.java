package com.pikamachu.trafficgen.service;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.pikamachu.trafficgen.model.TrafficRequest;

/**
 * The Class TrafficRequestClient.
 */
@Service
@Scope("prototype")
@ConfigurationProperties(prefix = "traffic.request.service")
public class TrafficRequestService {

	private static final int HTTP_CLIENT_MAX_POOL = 1;

	private static final int HTTP_CLIENT_REQUEST_TIMEOUT = 60000;

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(TrafficRequestService.class);

	/** The rest template. */
	private RestTemplate restTemplate;

	/** The cookies service. */
	@Autowired
	private TrafficCookiesService cookiesService;

	/** The stats service. */
	@Autowired
	private TrafficStatsService statsService;

	/** The num requests. */
	private long numRequests = 0;

	/**
	 * Rest template.
	 *
	 * @return the rest template
	 */
	public TrafficRequestService() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(createHttpClient());
		restTemplate = new RestTemplate(factory);
	}
	
	/**
	 * Creates the http client.
	 *
	 * @return the http client
	 */
	private HttpClient createHttpClient() {
	    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
	    cm.setMaxTotal(HTTP_CLIENT_MAX_POOL);
	    cm.setDefaultMaxPerRoute(HTTP_CLIENT_MAX_POOL);

	    RequestConfig.Builder requestBuilder = RequestConfig.custom();
	    requestBuilder = requestBuilder.setConnectTimeout(HTTP_CLIENT_REQUEST_TIMEOUT);
	    requestBuilder = requestBuilder.setSocketTimeout(HTTP_CLIENT_REQUEST_TIMEOUT);

	    HttpClientBuilder builder = HttpClientBuilder.create();
	    builder.setDefaultRequestConfig(requestBuilder.build());
	    builder.setConnectionManager(cm);

	    return builder.build();
	}

	/**
	 * Send.
	 *
	 * @param trafficRequest
	 *            the traffic request
	 * @return the http entity
	 * @throws Exception
	 *             the exception
	 */
	public HttpEntity<String> send(TrafficRequest trafficRequest) throws Exception {
		log.debug("start send:");

		long beginTime = System.currentTimeMillis();

		checkTrafficRequest(trafficRequest);

		HttpEntity<String> response = null;

		String url = createRequestUrl(trafficRequest);

		try {

			HttpHeaders headers = createRequestHeaders(trafficRequest);

			String method = trafficRequest.getMethod();
			if (method.equalsIgnoreCase(HttpMethod.GET.name())) {
				response = doGetRequest(headers, url);
			} else {
				response = doPostRequest(headers, url, trafficRequest.getPostData());
			}

			storeCookies(response);

			long endTime = System.currentTimeMillis() - beginTime;
			statsService.addValue(method, url, endTime);

			log.info("Success request url {}", url);

			// log.debug(" response: {}", response);
		} catch (HttpClientErrorException e) {
			log.warn("Client error on request url {} response: {}", url, e.getMessage());
		} catch (HttpServerErrorException e) {
			log.error("Server error on request url {} response: {}", url, e.getMessage());
		}

		resetCookies();

		log.debug("finish send.");

		return response;
	}

	/**
	 * Check traffic request.
	 *
	 * @param trafficRequest
	 *            the traffic request
	 */
	private void checkTrafficRequest(TrafficRequest trafficRequest) {
		String url = trafficRequest.getUrl();
		String method = trafficRequest.getMethod();
		if (StringUtils.isEmpty(url) || StringUtils.isEmpty(method)) {
			log.debug("    ");
			throw new IllegalArgumentException("url or method null!");
		}
	}

	/**
	 * Creates the request url.
	 *
	 * @param trafficRequest
	 *            the traffic request
	 * @return the string
	 */
	private String createRequestUrl(TrafficRequest trafficRequest) {
		// Adding domain to url
		return this.protocol + this.domain + trafficRequest.getUrl();
	}

	/**
	 * Creates the request headers.
	 *
	 * @param trafficRequest
	 *            the traffic request
	 * @return the http headers
	 */
	protected HttpHeaders createRequestHeaders(TrafficRequest trafficRequest) {
		HttpHeaders headers = new HttpHeaders();

		headers.setAccept(
				Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.ALL }));

		if (!StringUtils.isEmpty(this.authorization)) {
			headers.set(HttpHeaders.AUTHORIZATION, this.authorization);
		}

		if (useCookies) {
			String cookies = cookiesService.getCookiesString();
			if (!StringUtils.isEmpty(cookies)) {
				headers.set(HttpHeaders.COOKIE, cookies);
			}
		}

		headers.set("User-Agent", trafficRequest.getUserAgent());

		return headers;
	}

	/**
	 * Do post request.
	 *
	 * @param headers
	 *            the headers
	 * @param url
	 *            the url
	 * @param postData
	 *            the post data
	 * @return the http entity
	 */
	private HttpEntity<String> doPostRequest(HttpHeaders headers, String url, String postData) {
		HttpEntity<String> response = null;
		if (StringUtils.isNotEmpty(postData)) {
			log.debug("    send POST request for url {} with postData {}", url, postData);
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<String>(postData, headers);
			response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		} else {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "No post data!");
		}
		return response;
	}

	/**
	 * Do get request.
	 *
	 * @param headers
	 *            the headers
	 * @param url
	 *            the url
	 * @return the http entity
	 */
	private HttpEntity<String> doGetRequest(HttpHeaders headers, String url) {
		HttpEntity<String> response;
		log.debug("    send GET request for url {}", url);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		return response;
	}

	/**
	 * Store cookies.
	 *
	 * @param response
	 *            the response
	 */
	protected void storeCookies(HttpEntity<String> response) {
		if (useCookies) {
			HttpHeaders responseHeaders = response.getHeaders();
			List<String> cookies = responseHeaders.get(HttpHeaders.SET_COOKIE);
			if (cookies != null && !cookies.isEmpty()) {
				log.debug("Setting cookies: {}", cookies);
				cookiesService.addAllCookies(cookies);
			}
		}
	}

	/**
	 * Reset cookies.
	 */
	private void resetCookies() {
		this.numRequests++;
		Long resetRequest = this.resetCookiesRequests;
		if (resetRequest != null && resetRequest > 0) {
			Long numRequest = this.numRequests;
			if (numRequest % resetRequest == 0) {
				log.info("Reset cookies after {} requests (total {})", resetRequest, numRequest);
				cookiesService.resetCookies();
			}
		}
	}

	/**
	 * Configuration properties.
	 */

	private String authorization;

	/** The domain. */
	private String domain;

	/** The protocol. */
	private String protocol;

	/** The use cookies. */
	private Boolean useCookies;

	/** The reset cookies requests. */
	private Long resetCookiesRequests;

	public String getAuthorization() {
		return authorization;
	}

	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Boolean getUseCookies() {
		return useCookies;
	}

	public void setUseCookies(Boolean useCookies) {
		this.useCookies = useCookies;
	}

	public Long getResetCookiesRequests() {
		return resetCookiesRequests;
	}

	public void setResetCookiesRequests(Long resetCookiesRequests) {
		this.resetCookiesRequests = resetCookiesRequests;
	}

}
