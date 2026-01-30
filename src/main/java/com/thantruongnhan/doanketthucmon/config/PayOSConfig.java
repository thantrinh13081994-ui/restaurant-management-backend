package com.thantruongnhan.doanketthucmon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thantruongnhan.doanketthucmon.payos.PayOSClient;

@Configuration
public class PayOSConfig {

    @Value("${payos.clientId}")
    private String clientId;

    @Value("${payos.apiKey}")
    private String apiKey;

    @Value("${payos.checksumKey}")
    private String checksumKey;

    @Bean
    public PayOSClient payOSClient() {
        return new PayOSClient(clientId, apiKey, checksumKey);
    }
}
