package com.hanghe.redis.mysql.theater

import com.hanghe.redis.theater.TheaterEntity

interface TheaterRepository {

    fun findAll(): List<TheaterEntity>
}
