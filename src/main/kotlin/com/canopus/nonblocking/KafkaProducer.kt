package com.canopus.nonblocking

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender

/**
 * @author Minseok Kwon
 */
@Component
class KafkaProducer(private var kafkaSender: KafkaSender<String, String>) : MessageBroker {
    override suspend fun send(topic: String, message: Any) {
        kafkaSender.createOutbound()
            .send(Mono.just(ProducerRecord<String, String>(topic, message.toJson())))
            .then()
            .awaitFirstOrNull()
    }

    fun Any.toJson(): String = ObjectMapper().writeValueAsString(this)
}
