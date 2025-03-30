package com.hanghe.redis.mysql.seat

import com.hanghe.redis.movie.seat.SeatEntity

interface SeatRepository {

    fun findByTheaterIdAndSeatIdsIn(theaterId: Long, seatIds: List<Long>): List<SeatEntity>

    fun findByScreeningIdAndSeatIds(screeningId: Long, seatIds: List<Long>): List<SeatEntity>

    fun findByTheaterIdAndSeatIdsByPessimisticLock(theaterId: Long, seatIds: List<Long>): List<SeatEntity>

    fun findByTheaterIdAndSeatIdsByOptimisticLock(theaterId: Long, seatIds: List<Long>): List<SeatEntity>
}
