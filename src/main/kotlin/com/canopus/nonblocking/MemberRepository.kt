package com.canopus.nonblocking

/**
 * @author Minseok Kwon
 */
interface MemberRepository {
    suspend fun addMember(name: String, age: Int): Int
}
