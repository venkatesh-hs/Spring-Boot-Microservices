package com.photoapp.api.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import reactor.core.publisher.Mono;

@Configuration
public class GlobalFiltersConfiguration {
	final Logger logger = LoggerFactory.getLogger(GlobalFiltersConfiguration.class);
	
	@Order(2)
	@Bean
	public GlobalFilter secondGlobalFilter() {
		return (exchange, chain) -> {
			logger.info("Inside second Global pre-filter");
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				logger.info("Inside second global post filter!!");
			}));
		};
	}
	
	@Order(1)
	@Bean
	public GlobalFilter thirdGlobalFilter() {
		return (exchange, chain) -> {
			logger.info("Inside third Global pre-Filter");
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				logger.info("Inside third global post-filter!!");
			}));
		};
	}
}
