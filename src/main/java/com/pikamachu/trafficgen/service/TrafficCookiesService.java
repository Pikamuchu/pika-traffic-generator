package com.pikamachu.trafficgen.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * The Class TrafficCookiesService.
 */
@Service
@Scope("prototype")
public class TrafficCookiesService {
	
	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(TrafficCookiesService.class);

	/** The Constant COOKIE_INDEX_CHAR. */
	private static final String COOKIE_INDEX_CHAR = ";";

	/** The Constant COOKIE_STRING_SEPARATOR. */
	private static final String COOKIE_STRING_SEPARATOR = "; ";

	/** The Constant COOKIE_KEY_VALUE_SEPARATOR. */
	private static final String COOKIE_KEY_VALUE_SEPARATOR = "=";

	private Map<String, String> cookieMap = new HashMap<String, String>();

	public String getCookiesString() {
		if (cookieMap == null || cookieMap.isEmpty()) {
			return null;
		}
		
		StringBuffer cookies = new StringBuffer();
		for (String cookie : cookieMap.values()) {
			if (StringUtils.isNotEmpty(cookie)) {
				cookies.append(cookie.substring(0, cookie.indexOf(COOKIE_INDEX_CHAR))).append(COOKIE_STRING_SEPARATOR);
			}
		}
		return cookies.toString();
	}
	
	/**
	 * Adds the all cookies.
	 *
	 * @param cookies
	 *            the cookies
	 */
	public void addAllCookies(Collection<String> cookies) {
		if (cookieMap == null) {
			cookieMap = new HashMap<String, String>();
		}
		if (cookies != null && !cookies.isEmpty()) {
			for (String cookie : cookies) {
				if (StringUtils.isNotEmpty(cookie)) {
					String cookieKey = cookie.substring(0, cookie.indexOf(COOKIE_KEY_VALUE_SEPARATOR));
					cookieMap.put(cookieKey, cookie);
				}
			}
		}
	}
	
	public void resetCookies() {
		if (cookieMap != null) {
			cookieMap.clear();
		}
	}

}
