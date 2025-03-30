package com.hanghe.redis.mysql.reservation

import com.hanghe.redis.reservation.ReservationEntity
import org.springframework.stereotype.Repository

@Repository
class ReservationRepositoryImpl(
    private val repository: ReservationJpaRepository
) : ReservationRepository {

    override fun findByScreeningId(screeningId: Long): List<ReservationEntity> {
        return repository.findByScreeningId(screeningId)
    }

    override fun saveAll(newReservations: List<ReservationEntity>) {
        repository.saveAll(newReservations)
    }
}
