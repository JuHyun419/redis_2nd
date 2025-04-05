package com.hanghe.redis.seat

import com.hanghe.redis.movie.seat.SeatEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SeatJpaRepository : JpaRepository<SeatEntity, Long> {

    fun findByTheaterIdAndIdIn(theaterId: Long, seatIds: List<Long>): List<SeatEntity>
}
