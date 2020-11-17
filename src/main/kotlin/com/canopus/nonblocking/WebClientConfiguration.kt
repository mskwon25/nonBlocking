package com.canopus.nonblocking

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 * @author Minseok Kwon
 */
@Configuration
class WebClientConfiguration {

    @Bean
    fun webClient() = WebClient.builder().baseUrl("http://localhost:8080").build()
}
