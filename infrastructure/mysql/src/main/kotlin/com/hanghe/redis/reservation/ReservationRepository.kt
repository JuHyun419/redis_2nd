package com.hanghe.redis.reservation

interface ReservationRepository {

    fun findByScreeningId(screeningId: Long): List<ReservationEntity>

    fun saveAll(newReservations: List<ReservationEntity>)

    fun findByScreeningIdAndSeatIds(screeningId: Long, seatIds: List<Long>): List<ReservationEntity>

    fun findByScreeningIdAndSeatIdsWithPessimisticLock(screeningId: Long, seatIds: List<Long>): List<ReservationEntity>

    fun findByScreeningIdAndSeatIdsWithOptimisticLock(screeningId: Long, seatIds: List<Long>): List<ReservationEntity>
}
