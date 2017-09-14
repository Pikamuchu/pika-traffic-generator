package com.pikamachu.trafficgen.component;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.actuate.endpoint.InfoEndpoint;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

import com.pikamachu.trafficgen.service.TrafficStatsService;

/**
 * The Class TrafficInfoComponent.
 */
@Component
public class TrafficInfoComponent {
	
	@Bean
	public InfoEndpointRebinderConfiguration infoEndpointRebinderConfiguration() {
		return new InfoEndpointRebinderConfiguration();
	}

	private static class InfoEndpointRebinderConfiguration
			implements ApplicationListener<EnvironmentChangeEvent>, BeanPostProcessor {
		
		@Autowired
		private TrafficStatsService statsService;
		
		@Autowired
		private ConfigurableEnvironment environment;
		
		private TrafficInfoEndpoint infoEndpoint;

		@Override
		public void onApplicationEvent(EnvironmentChangeEvent event) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			for (String key : event.getKeys()) {
				if (key.startsWith("info.")) {
					map.put(key.substring("info.".length()), this.environment.getProperty(key));
				}
			}
			infoEndpoint.putAdditionalInfo(map);
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
			if (bean instanceof InfoEndpoint) {
				return infoEndpoint((InfoEndpoint) bean);
			}
			return bean;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
			return bean;
		}

		private InfoEndpoint infoEndpoint(InfoEndpoint endpoint) {
	
			if (this.infoEndpoint == null) {
				this.infoEndpoint = new TrafficInfoEndpoint(statsService);
				this.infoEndpoint.setId(endpoint.getId());
				this.infoEndpoint.setEnabled(endpoint.isEnabled());
				this.infoEndpoint.setSensitive(endpoint.isSensitive());
			}
			/*{
				@Override
				public Map<String, Object> invoke() {
					Map<String, Object> info = new LinkedHashMap<String, Object>(super.invoke());
					info.putAll(InfoEndpointRebinderConfiguration.this.map);
					return info;
				}
			};*/

			this.infoEndpoint.putAdditionalInfo(endpoint.invoke());
			
			return this.infoEndpoint;
			
/*
			if (endpoint instanceof TrafficInfoEndpoint) {
				((TrafficInfoEndpoint) endpoint).putAdditionalInfo(InfoEndpointRebinderConfiguration.this.map);
			}
			return endpoint;
*/
		}

	}

}
