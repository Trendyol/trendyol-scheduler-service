package com.trendyol.scheduler.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AsyncJobRestTemplateConfiguration {

    private static final int DEFAULT_TIMEOUT = 10000;

    @Bean
    public AsyncRestTemplate asyncJobRestTemplate(RestTemplate restTemplate) {
        HttpComponentsAsyncClientHttpRequestFactory requestFactory = new HttpComponentsAsyncClientHttpRequestFactory();
        requestFactory.setReadTimeout(DEFAULT_TIMEOUT);
        requestFactory.setConnectTimeout(DEFAULT_TIMEOUT);
        return new AsyncRestTemplate(requestFactory, restTemplate);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().setConnectTimeout(DEFAULT_TIMEOUT).setReadTimeout(DEFAULT_TIMEOUT).build();
    }
}