package com.hanghe.redis.mysql.theater

import com.hanghe.redis.theater.TheaterEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TheaterJpaRepository : JpaRepository<TheaterEntity, Long>
