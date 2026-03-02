package com.bankcore.accounts.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for setting up WebClient instances used to communicate with external services.
 * This class defines beans for WebClient that are configured with base URLs for the respective services.
 * @author BankCore Team - Sebastian Orjuela - Cristian Ortiz
 * @version 1.0
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient customersWebClient(@Value("${ms-customers.url}") String customersUrl){
        return WebClient.builder().baseUrl(customersUrl).build();
    }

}
