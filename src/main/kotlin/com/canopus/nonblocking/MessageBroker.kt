package com.canopus.nonblocking

/**
 * @author Minseok Kwon
 */
interface MessageBroker {
    suspend fun send(topic: String, message: Any)
}
