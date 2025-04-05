package com.hanghe.redis.reservation

import com.hanghe.redis.message.fcm.FCMMessageClient
import com.hanghe.redis.movie.seat.SeatCodes
import com.hanghe.redis.movie.seat.SeatEntity
import com.hanghe.redis.reservation.ReservationRepository
import com.hanghe.redis.screening.ScreeningRepository
import com.hanghe.redis.seat.SeatRepository
import com.hanghe.redis.ratelimiter.RateLimiter
import com.hanghe.redis.screening.ScreeningEntity
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 예약에 대한 비즈니스 로직
 *
 * 1. 상영 시간에 예약된 정보를 조회하고, 최대 예매 좌석에 대한 유효성 검증을 진행한다.
 * 2. 예약 요청한 좌석 정보를 조회하고, 좌석의 형식, 중복 예약 등에 대한 유효성 검증을 진행한다.
 * 3. 예매에 대한 동시성 이슈를 해결하고자 Pessimistic, Optimistic, Distributed Lock 을 적용하여 예매를 진행한다.
 * 4. 예매 정보에 대한 메시지를 발송한다.
 */
@Service
@Transactional
class MovieReservationService(
    val reservationRepository: ReservationRepository,
    val seatRepository: SeatRepository,
    val screeningRepository: ScreeningRepository,

    val eventPublisher: ApplicationEventPublisher,
    private val reservationPolicy: UserReservationPolicy,
    private val rateLimiter: RateLimiter
) {

    private val logger = LoggerFactory.getLogger(MovieReservationService::class.java)

    fun reservation(
        screeningId: Long,
        userId: String,
        seatIds: List<Long>
    ) {
        rateLimiter.reservedRateLimit(screeningId, userId)

        val reservations = reservationRepository.findByScreeningId(screeningId)
        reservationPolicy.validate(userId, reservations.countByUser(userId), seatIds.size)

        val seats = seatRepository.findByScreeningIdAndSeatIds(screeningId, seatIds)
        val requestSeatCodes = SeatCodes(seats)
        val reservedSeatCodes = SeatCodes(reservations)

        requestSeatCodes.validate(reservedSeatCodes)

        val existingReservedSeatIds = reservationRepository
            .findByScreeningIdAndSeatIds(screeningId, seatIds)
            .mapNotNull { it.seat.id }

        if (existingReservedSeatIds.isNotEmpty()) {
            throw IllegalStateException("요청하신 좌석 중 이미 예약된 좌석이 존재합니다.")
        }

        val screening = screeningRepository.getById(screeningId)
        val newReservations = reservationEntities(seats, screening, userId)
        reservationRepository.saveAll(newReservations)

        eventPublisher.publishEvent(
            FCMMessageClient.ReservationCompletedEvent(
                userId,
                screening.theaterName,
                screening.movie.title,
                requestSeatCodes
            )
        )
    }


    fun reservationWithPessimisticLock(screeningId: Long, userId: String, seatIds: List<Long>) {
        rateLimiter.reservedRateLimit(screeningId, userId)

        val reservations = reservationRepository.findByScreeningId(screeningId)
        reservationPolicy.validate(userId, reservations.countByUser(userId), seatIds.size)

        val seats = seatRepository.findByTheaterIdAndSeatIds(screeningId, seatIds)
        val requestSeatCodes = SeatCodes(seats)
        val reservedSeatCodes = SeatCodes(reservations)

        requestSeatCodes.validate(reservedSeatCodes)
        validateConcurrencyReservationWithPessimisticLock(screeningId, seatIds)

        val screening = screeningRepository.getById(screeningId)
        val newReservations = reservationEntities(seats, screening, userId)
        reservationRepository.saveAll(newReservations)

        eventPublisher.publishEvent(
            FCMMessageClient.ReservationCompletedEvent(
                userId,
                screening.theaterName,
                screening.movie.title,
                requestSeatCodes
            )
        )
    }


    fun reservationWithOptimisticLock(screeningId: Long, userId: String, seatIds: List<Long>) {
        val reservations = reservationRepository.findByScreeningId(screeningId)
        reservationPolicy.validate(userId, reservations.countByUser(userId), seatIds.size)

        val seats = seatRepository.findByTheaterIdAndSeatIds(screeningId, seatIds)
        val requestSeatCodes = SeatCodes(seats)
        val reservedSeatCodes = SeatCodes(reservations)

        requestSeatCodes.validate(reservedSeatCodes)
        validateConcurrencyReservationWithOptimisticLock(screeningId, seatIds)

        val screening = screeningRepository.getById(screeningId)
        val newReservations = reservationEntities(seats, screening, userId)
        reservationRepository.saveAll(newReservations)

        eventPublisher.publishEvent(
            FCMMessageClient.ReservationCompletedEvent(
                userId,
                screening.theaterName,
                screening.movie.title,
                requestSeatCodes
            )
        )
    }

    private fun reservationEntities(
        seats: List<SeatEntity>,
        screening: ScreeningEntity,
        userId: String
    ) = seats.map { seat ->
        ReservationEntity(
            screening = screening,
            seat = seat,
        ).apply {
            this.createdBy = userId
        }
    }

    private fun validateConcurrencyReservationWithPessimisticLock(screeningId: Long, seatIds: List<Long>) {
        val existingReservedSeatIds = reservationRepository
            .findByScreeningIdAndSeatIdsWithPessimisticLock(screeningId, seatIds)
            .map { it.seat.id }

        // TODO: Custom Exception
        if (existingReservedSeatIds.isNotEmpty()) {
            logger.error("Movie Reservation ConcurrentModificationException! SeatIds: ${existingReservedSeatIds.joinToString()}")
            throw ConcurrentModificationException("요청하신 좌석 중 이미 예약된 좌석이 존재합니다.")
        }
    }

    private fun validateConcurrencyReservationWithOptimisticLock(screeningId: Long, seatIds: List<Long>) {
        val existingReservedSeatIds = reservationRepository
            .findByScreeningIdAndSeatIdsWithOptimisticLock(screeningId, seatIds)
            .map { it.seat.id }

        // TODO: Custom Exception
        if (existingReservedSeatIds.isNotEmpty()) {
            logger.error("Movie Reservation ConcurrentModificationException! SeatIds: ${existingReservedSeatIds.joinToString()}")
            throw ConcurrentModificationException("요청하신 좌석 중 이미 예약된 좌석이 존재합니다.")
        }
    }
}
