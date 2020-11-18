package com.canopus.nonblocking

import org.apache.kafka.clients.producer.ProducerRecord
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaOutbound

/**
 * @author Minseok Kwon
 */
fun KafkaOutbound<String, String>.send(record: ProducerRecord<String, String>): KafkaOutbound<String, String> =
    this.send(Mono.just(record))
