package com.canopus.nonblocking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux

@EnableWebFlux
@SpringBootApplication
class NonblockingApplication

fun main(args: Array<String>) {
	runApplication<NonblockingApplication>(*args)
}
