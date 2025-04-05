package com.hanghe.redis.theater

interface TheaterRepository {

    fun findAll(): List<TheaterEntity>
}
