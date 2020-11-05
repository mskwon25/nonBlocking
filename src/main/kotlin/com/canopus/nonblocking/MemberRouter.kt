package com.canopus.nonblocking

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.*

/**
 * @author Minseok Kwon
 */
@Configuration
class MemberRouter(
    private val memberRepository: MemberRepository,
    private val messageBroker: MessageBroker
) {
    @Bean
    fun coRouteMember(): RouterFunction<ServerResponse> {
        return coRouter {
            POST("/member") { request ->
                val model = request.awaitBody<MemberAddRequestModel>()
                val memberNo = memberRepository.addMember(model.name, model.age)
                messageBroker.send("memberCreated", memberNo)

                ok().buildAndAwait()
            }
        }
    }
}

