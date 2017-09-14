package com.pikamachu.trafficgen.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * The Class TrafficDummyDataService.
 */
@Service
@ConfigurationProperties(prefix = "traffic.dummydata")
public class TrafficDummyDataService {
	
	private static final String KEY_VALUE_SEPARATOR = " = ";
	
	private static final String KEY_VALUE_WITHOUT_SPACES_SEPARATOR = "=";

	private static final String KEY_LOCALE_REPLACE_SEPARATOR = " ; ";

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(TrafficDummyDataService.class);
	
	/** The request replacements. */
	private Map<String, String> mapRequestReplacements;

	/** The request add querystring. */
	private Map<String, String> mapRequestAddQuerystring;

	/** The request post dummy data. */
	private Map<String, String> mapPostDataDummyInfo;
	
	public Map<String, String> getMapRequestReplacements() {
		return mapRequestReplacements;
	}

	public Map<String, String> getMapRequestAddQuerystring() {
		return mapRequestAddQuerystring;
	}

	public Map<String, String> getMapPostDataDummyInfo() {
		return mapPostDataDummyInfo;
	}
	
	/**
	 * Inits the.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@PostConstruct
	public void init() throws Exception {
		// Convert property lists to a Map
		this.mapRequestReplacements = fromListToMapString(requestReplacements);
		this.mapRequestAddQuerystring = fromListToMapString(requestAddQuerystring);
		this.mapPostDataDummyInfo = fromListToMapString(postDataDummyInfo);
	}
	
	/**
	 * Do request add querystring.
	 *
	 * @param url
	 *            the url
	 * @return the string
	 */
	public String doRequestAddQuerystring(String url) {
		Map<String, String> requestAddQuerystring = getMapRequestAddQuerystring();
		if (requestAddQuerystring != null) {
			for (Map.Entry<String, String> entry : requestAddQuerystring.entrySet()) {
				try {
					if (!url.matches(entry.getKey())) {
						continue;
					}
					if (url.indexOf("?") > 0) {
						url = url + "&" + entry.getValue();
					} else {
						url = url + "?" + entry.getValue();
					}
				} catch (Exception e) {
					log.warn("Something goes wrong adding querystring values url={} entry={}. Exception {}", url, entry,
							e.getMessage());
				}
			}
		}
		return url;
	}

	/**
	 * Do request replacements.
	 *
	 * @param url
	 *            the url
	 * @return the string
	 */
	public String doRequestReplacements(String url) {
		String returnUrl = new String(url);
		Map<String, String> requestReplacements = getMapRequestReplacements();
		if (requestReplacements != null) {
			for (Map.Entry<String, String> entry : requestReplacements.entrySet()) {
				try {
					String key = entry.getKey();
					if (key.indexOf(KEY_LOCALE_REPLACE_SEPARATOR) > 0) {
						// Case especific replacement
						String[] keys = key.split(KEY_LOCALE_REPLACE_SEPARATOR, 2);
						String locale = StringUtils.trim(keys[0]);
						if (url.indexOf(locale) >= 0) {
							key = StringUtils.trim(keys[1]);
							returnUrl = url.replaceFirst(key, entry.getValue());
							break;
						}
					} else {
						// Case generic replacement
						returnUrl = url.replaceFirst(key, entry.getValue());
					}
				} catch (Exception e) {
					log.warn("Something goes wrong doing url replacements url={} entry={}. Exception {}", url, entry,
							e.getMessage());
				}
			}
		}
		return returnUrl;
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
	public String doPostDataModifications(String request, String data) {
		Map<String, String> postDataDummyInfo = getMapPostDataDummyInfo();
		if (postDataDummyInfo != null) {
			for (Map.Entry<String, String> entry : postDataDummyInfo.entrySet()) {
				try {
					if (request.matches(entry.getKey())) {
						return entry.getValue();
					}
				} catch (Exception e) {
					log.warn("Something goes wrong setting dummy data request={} entry={}. Exception {}", request, entry,
							e.getMessage());
				}
			}
		}

		// No replacement found
		return data;
	}
	
	/**
	 * From string to map string.
	 *
	 * @param list
	 *            the string
	 * @return the map
	 */
	private Map<String, String> fromListToMapString(List<String> list) {
		Map<String, String> map = new HashMap<String, String>();
		if (list != null && !list.isEmpty()) {
			for (String pair : list) {
				try {
					String[] keyValue = pair.split(KEY_VALUE_SEPARATOR, 2);
					if (keyValue.length < 2) {
						keyValue = pair.split(KEY_VALUE_WITHOUT_SPACES_SEPARATOR, 2);
					}
					map.put(StringUtils.trim(keyValue[0]), StringUtils.trim(keyValue[1]));
				} catch (Exception e) {
					log.warn("Something goes wrong doing from list to Map conversion pair={}. Exception {}", pair, e.getMessage());
				}
			}
		}
		return map;
	}
	
	/**
	 * Configuration properties.
	 */

	/** The request replacements. */
	private List<String> requestReplacements = new ArrayList<String>();

	/** The request add querystring. */
	private List<String> requestAddQuerystring = new ArrayList<String>();

	/** The request post dummy data. */
	private List<String> postDataDummyInfo = new ArrayList<String>();

	public List<String> getRequestReplacements() {
		return requestReplacements;
	}

	public List<String> getRequestAddQuerystring() {
		return requestAddQuerystring;
	}

	public List<String> getPostDataDummyInfo() {
		return postDataDummyInfo;
	}

}
