package com.hanghe.redis.mysql.reservation

import com.hanghe.redis.reservation.ReservationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ReservationJpaRepository : JpaRepository<ReservationEntity, Long> {

    fun findByScreeningId(screeningId: Long): List<ReservationEntity>
}
