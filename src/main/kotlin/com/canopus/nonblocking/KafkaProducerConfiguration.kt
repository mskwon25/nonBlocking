package com.canopus.nonblocking

import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions

/**
 * @author Minseok Kwon
 */
@Configuration
class KafkaProducerConfiguration(private val kafkaProperties: KafkaProperties) {
    @Bean
    fun kafkaSender(): KafkaSender<String, String> {
        return KafkaSender.create(SenderOptions.create(kafkaProperties.buildProducerProperties()))
    }
}
