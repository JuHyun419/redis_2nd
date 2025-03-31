package com.hanghe.redis.mysql.reservation

import com.hanghe.redis.reservation.ReservationEntity
import jakarta.persistence.LockModeType
import jakarta.persistence.QueryHint
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints

interface ReservationJpaRepository : JpaRepository<ReservationEntity, Long> {

    fun findByScreeningId(screeningId: Long): List<ReservationEntity>

    fun findByScreeningIdAndSeatIdIn(screeningId: Long, seatIds: List<Long>): List<ReservationEntity>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT r FROM reservations r
        WHERE r.screening.id = :screeningId
            AND r.seat.id IN :seatIds
    """)
    @QueryHints(QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
    fun findByScreeningIdAndSeatIdsWithPessimisticLock(screeningId: Long, seatIds: List<Long>): List<ReservationEntity>

    @Lock(LockModeType.OPTIMISTIC)
    @Query("""
        SELECT r FROM reservations r
        WHERE r.screening.id = :screeningId
            AND r.seat.id IN :seatIds
    """)
    fun findByScreeningIdAndSeatIdsWithOptimisticLock(screeningId: Long, seatIds: List<Long>): List<ReservationEntity>
}
