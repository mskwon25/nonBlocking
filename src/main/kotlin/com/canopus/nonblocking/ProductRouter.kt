package com.canopus.nonblocking

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaOutbound
import reactor.kafka.sender.KafkaSender
import reactor.util.function.Tuples
import java.lang.RuntimeException

/**
 * Non Blocking 지원하는 R2DBC, Reactive Kafka, WebClient 사용해
 * 샹품 조회 API 만들기
 *
 * 기능 설명
 * 1. 상품 번호를 파라미터로 받아서 DB 로부터 상품 정보을 조회한다
 * 2. 상품 정보중 재고 번호, 전시 번호를 바탕으로 다시 재고, 전시 서버로 부터 각각의 상세 정보를 요청한다
 * 3. 상품 조회수를 증가시키기 위해 Kafka 메시지를 발행한다
 * 4. 응답 객체를 내려준다
 *
 * @author Minseok Kwon
 */
@Configuration
class ProductRouter(
    private val productRepository: ProductRepository,
    private val kafkaProducer: KafkaProducer,
    private val webClient: WebClient
) {
    @Bean
    fun coRouteProduct(): RouterFunction<ServerResponse> {
        return coRouter {
            GET("/products/{id}") { request ->
                val id = request.pathVariable("id").toInt()
                val product = productRepository.getProduct(id) ?: throw ProductNotFoundException()
                val stockInfo = getStockInfo(product.stockNo)
                val displayInfo = getDisplayInfo(product.displayNo)
                kafkaProducer.send("productInquired", product.id)

                ok().bodyValueAndAwait(ProductResponse(product.name, stockInfo.stock, displayInfo.displayName))
            }
        }
    }

    /**
     * 재고 정보를 조회하기 위해 재고 API 호출
     *
     * @param stockNo 재고 번호
     */
    suspend fun getStockInfo(stockNo: Int): StockInfo {
        return webClient.get()
            .uri("localhost:8081/stocks/$stockNo")
            .retrieve()
            .awaitBody()
    }

    /**
     * 전시 정보를 조회하기 위해 재고 API 호출
     *
     * @param displayNo 전시 번호
     */
    suspend fun getDisplayInfo(displayNo: Int): DisplayInfo {
        return webClient.get()
            .uri("localhost:8081/display/$displayNo")
            .retrieve()
            .awaitBody()
    }
}

/**
 * 상품 정보에 접근하기 위한 R2DBC Repository
 *
 * @property [DatabaseClient]
 */
@Repository
class ProductRepository(private val client: DatabaseClient) {

    /**
     * 상품 번호로 상품 정보를 조회한다
     *
     * @param id
     */
    suspend fun getProduct(id: Int): Product? {
        return client
            .execute("SELECT * FROM product WHERE id = $id")
            .`as`(Product::class.java)
            .fetch()
            .one()
            .awaitFirstOrNull()
    }
}

/**
 * Kafka 메세지를 발행하기 위한 Reactive Kafka Producer
 *
 * @property [KafkaSender]
 */
@Component
class KafkaProducer(private val kafkaSender: KafkaSender<String, String>) {

    /**
     * 메세지 발행
     *
     * @param topic
     * @param message
     */
    suspend fun send(topic: String, message: Any) {
        kafkaSender.createOutbound()
            .send(ProducerRecord(topic, message.toJson()))
            .then()
            .awaitFirstOrNull()
    }

    fun Any.toJson(): String = ObjectMapper().writeValueAsString(this)
}

/**
 * 상품 정보
 *
 * @property id 상품 ID
 * @property name 상품명
 * @property stockNo 재고 번호
 * @property displayNo 전시 번호
 */
class Product(
    val id: Int,
    val name: String,
    val stockNo: Int,
    val displayNo: Int
)

/**
 * 재고 정보
 *
 * @property stockNo 재고 번호
 * @property stock 재고량
 */
class StockInfo(
    private val stockNo: Int,
    val stock: Int
)

/**
 * 전시 정보
 *
 * @property displayNo 전시 번호
 * @property displayName 전시명
 */
class DisplayInfo(
    private val displayNo: Int,
    val displayName: String
)

/**
 * 상품 조회 응답 객체
 *
 * @property productName 상품명
 * @property stock 재고
 * @property displayName 전시명
 */
data class ProductResponse(
    val productName: String,
    val stock: Int,
    val displayName: String
)

class ProductNotFoundException : RuntimeException()
