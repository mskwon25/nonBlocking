package com.canopus.nonblocking

import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

/**
 * @author Minseok Kwon
 */
@Repository
class MemberRepositoryImpl(private val client: DatabaseClient) : MemberRepository {
    override suspend fun addMember(name: String, age: Int): Int {
        return client.execute("INSERT INTO member (name, age) VALUES ($1, $2)")
            .bind(0, name)
            .bind(1, age)
            .fetch()
            .rowsUpdated()
            .awaitFirst()
    }
}
