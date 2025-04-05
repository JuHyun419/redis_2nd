package com.hanghe.redis.reservation

import org.springframework.stereotype.Repository

@Repository
class ReservationRepositoryImpl(
    private val repository: ReservationJpaRepository,
) : ReservationRepository {

    override fun findByScreeningId(screeningId: Long): List<ReservationEntity> {
        return repository.findByScreeningId(screeningId)
    }

    override fun saveAll(newReservations: List<ReservationEntity>) {
        repository.saveAll(newReservations)
    }

    override fun findByScreeningIdAndSeatIds(screeningId: Long, seatIds: List<Long>): List<ReservationEntity> {
        return repository.findByScreeningIdAndSeatIdIn(screeningId, seatIds)
    }

    override fun findByScreeningIdAndSeatIdsWithPessimisticLock(screeningId: Long, seatIds: List<Long>): List<ReservationEntity> {
        return repository.findByScreeningIdAndSeatIdsWithPessimisticLock(screeningId, seatIds)
    }

    override fun findByScreeningIdAndSeatIdsWithOptimisticLock(
        screeningId: Long,
        seatIds: List<Long>
    ): List<ReservationEntity> {
        return repository.findByScreeningIdAndSeatIdsWithOptimisticLock(screeningId, seatIds)
    }
}
