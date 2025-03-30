package com.hanghe.redis.mysql.seat

import com.hanghe.redis.movie.seat.QSeatEntity
import com.hanghe.redis.movie.seat.SeatEntity
import com.hanghe.redis.screening.QScreeningEntity
import com.hanghe.redis.theater.QTheaterEntity
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class SeatRepositoryImpl(
    private val seatJpaRepository: SeatJpaRepository,
    private val jpaQueryFactory: JPAQueryFactory
) : SeatRepository {

    override fun findByTheaterIdAndSeatIdsIn(theaterId: Long, seatIds: List<Long>): List<SeatEntity> {
        return jpaQueryFactory
            .selectFrom(qSeat)
            .where(
                qSeat.theater.id.eq(theaterId),
                qSeat.id.`in`(seatIds)
            )
            .fetch()
    }

    override fun findByScreeningIdAndSeatIds(
        screeningId: Long,
        seatIds: List<Long>
    ): List<SeatEntity> {
        return jpaQueryFactory
            .select(qSeat)
            .from(qSeat)
            .join(qSeat.theater, qTheater)
            .join(qScreening).on(qScreening.theater.id.eq(qTheater.id))
            .where(
                qScreening.id.eq(screeningId),
                qSeat.id.`in`(seatIds)
            )
            .fetch()
    }

    override fun findByTheaterIdAndSeatIdsByPessimisticLock(theaterId: Long, seatIds: List<Long>): List<SeatEntity> {
        return seatJpaRepository.findByTheaterIdAndSeatIdsByPessimisticLock(theaterId, seatIds)
    }

    override fun findByTheaterIdAndSeatIdsByOptimisticLock(theaterId: Long, seatIds: List<Long>): List<SeatEntity> {
        return seatJpaRepository.findByTheaterIdAndSeatIdsByOptimisticLock(theaterId, seatIds)
    }

    companion object {
        private val qSeat = QSeatEntity.seatEntity
        private val qTheater = QTheaterEntity.theaterEntity
        private val qScreening = QScreeningEntity.screeningEntity
    }
}
