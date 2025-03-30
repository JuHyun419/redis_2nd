package com.hanghe.redis.mysql.seat

import com.hanghe.redis.movie.seat.SeatEntity
import jakarta.persistence.LockModeType
import jakarta.persistence.QueryHint
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints

interface SeatJpaRepository : JpaRepository<SeatEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT s 
    FROM seats s 
    WHERE s.theater.id = :theaterId 
      AND s.id IN :seatIds 
      """)
    @QueryHints(QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
    fun findByTheaterIdAndSeatIdsByPessimisticLock(theaterId: Long, seatIds: List<Long>): List<SeatEntity>

    @Lock(LockModeType.OPTIMISTIC)
    @Query("""
    SELECT s 
    FROM seats s 
    WHERE s.theater.id = :theaterId 
      AND s.id IN :seatIds 
      """)
    fun findByTheaterIdAndSeatIdsByOptimisticLock(theaterId: Long, seatIds: List<Long>): List<SeatEntity>
}
