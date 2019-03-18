package com.github.torczuk.reactivesandbox.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class Config {

    @Bean("streamClient")
    fun streamClient(): WebClient {
        return WebClient.builder()
                .baseUrl("http://localhost:8080/")
                .build()
    }

}