package com.canopus.nonblocking

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.server.*
import reactor.kafka.sender.KafkaSender

/**
 * Non Blocking 지원하는 R2DBC, Reactive Kafka, WebClient 이용해서
 * 샹품 조회 API 만들기
 *
 * 1. 상품 번호를 받아서 DB 로부터 상품 정보을 조회한다
 * 2. 상품 정보의 재고 번호, 전시 번호를 바탕으로 다시 재고, 전시 서버로 부터 각각의 정보를 요청한다
 * 3. 상품 조회수 증가를 위한 MessageQueue 메시지를 발행한다.
 * 4. 응답 객체를 내려준다
 *
 * @author Minseok Kwon
 */
@Configuration
class ProductRouter(
    private val productRepository: ProductRepository,
    private val kafkaProducer: KafkaProducer
) {
    @Bean
    fun coRouteProduct(): RouterFunction<ServerResponse> {
        TODO("not implemented")
    }
}

/**
 * 상품 정보를 DB 로부터 읽어고 위한 R2DBC Repository
 *
 * @property [DatabaseClient]
 */
@Repository
class ProductRepository {

    /**
     * 상품 번호로 상품 정보를 조회한다
     *
     * @param id
     */
    fun getProduct() {
        TODO("not implemented")
    }
}

/**
 * Reactive Kafka Producer
 *
 * @property [KafkaSender]
 */
@Component
class KafkaProducer {

    /**
     * kafka message 발행
     *
     * @param topic
     * @param message
     */
    fun send() {
        TODO("not implemented")
    }

    //    fun Any.toJson(): String = ObjectMapper().writeValueAsString(this)
}

/**
 * 상품 정보
 *
 * @property id 상품 ID
 * @property name 상품명
 * @property stockNo 재고 번호
 * @property displayCategoryNo 전시 번호
 */
class Product(
    val id: Int,
    val name: String,
    val stockNo: Int,
    val displayCategoryNo: Int
)

/**
 * 상품 조회 응답 객체
 *
 * @property productName 상품명
 * @property stock 재고
 * @property displayCategoryName 전시명
 */
class ProductResponse(
    val productName: String,
    val stock: Int,
    val displayCategoryNo: Int
)
