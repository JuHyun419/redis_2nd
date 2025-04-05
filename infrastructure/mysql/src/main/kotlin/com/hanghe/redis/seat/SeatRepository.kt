package com.hanghe.redis.seat

import com.hanghe.redis.movie.seat.SeatEntity

interface SeatRepository {

    fun findByTheaterIdAndSeatIdsIn(theaterId: Long, seatIds: List<Long>): List<SeatEntity>

    fun findByScreeningIdAndSeatIds(screeningId: Long, seatIds: List<Long>): List<SeatEntity>

    fun findByTheaterIdAndSeatIds(theaterId: Long, seatIds: List<Long>): List<SeatEntity>
}
