package com.hanghe.redis.mysql.seat

import com.hanghe.redis.movie.seat.SeatEntity

interface SeatRepository {

    fun findByTheaterIdAndSeatIdsIn(theaterId: Any, seatIds: List<Long>): List<SeatEntity>

    fun findByScreeningIdAndSeatIds(screeningId: Long, seatIds: List<Long>): List<SeatEntity>
}
