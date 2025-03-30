package com.hanghe.redis.mysql.reservation

import com.hanghe.redis.reservation.ReservationEntity

interface ReservationRepository {

    fun findByScreeningId(screeningId: Long): List<ReservationEntity>

    fun saveAll(newReservations: List<ReservationEntity>)
}
