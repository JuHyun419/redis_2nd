package com.hanghe.redis.reservation

import com.hanghe.redis.message.MessageClient
import com.hanghe.redis.movie.seat.SeatCodes
import com.hanghe.redis.mysql.reservation.ReservationRepository
import com.hanghe.redis.mysql.screening.ScreeningRepository
import com.hanghe.redis.mysql.seat.SeatRepository
import com.hanghe.redis.screening.ScreeningEntity
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MovieReservationService(
    val reservationRepository: ReservationRepository,
    val seatRepository: SeatRepository,
    val screeningRepository: ScreeningRepository,
    val messageClient: MessageClient,

    private val reservationPolicy: UserReservationPolicy
) {

    fun reservation(
        screeningId: Long,
        userId: String,
        seatIds: List<Long>
    ) {
        val reservations = reservationRepository.findByScreeningId(screeningId)

        reservationPolicy.validate(
            userId = userId,
            existingCount = reservations.count { it.createdBy == userId },
            newCount = seatIds.size
        )

        val seats = seatRepository.findByScreeningIdAndSeatIds(screeningId, seatIds)
        val requestSeatCodes = SeatCodes(seats)
        val reservedSeatCodes = reservations
            .takeIf { it.isNotEmpty() }
            ?.let { SeatCodes(it.map { r -> r.seat.seatCode }) }

        requestSeatCodes.validate(reservedSeatCodes)

        val screening: ScreeningEntity = screeningRepository.findById(screeningId)
            ?: throw EntityNotFoundException()

        val newReservations = seats.map { seat ->
            ReservationEntity(
                screening = screening,
                seat = seat,
            ).apply {
                this.createdBy = userId
            }
        }
        reservationRepository.saveAll(newReservations)

        // TODO: 비동기 적용
        messageClient.send(userId, screening.theaterName, screening.movie.title, requestSeatCodes)
    }

    fun reservationWithPessimisticLock(screeningId: Long, userId: String, seatIds: List<Long>) {
        val reservations = reservationRepository.findByScreeningId(screeningId)

        reservationPolicy.validate(
            userId = userId,
            existingCount = reservations.count { it.createdBy == userId },
            newCount = seatIds.size
        )

        val seats = seatRepository.findByTheaterIdAndSeatIdsByPessimisticLock(screeningId, seatIds)
        val requestSeatCodes = SeatCodes(seats)
        val reservedSeatCodes = reservations
            .takeIf { it.isNotEmpty() }
            ?.let { SeatCodes(it.map { r -> r.seat.seatCode }) }

        requestSeatCodes.validate(reservedSeatCodes)

        val screening: ScreeningEntity = screeningRepository.findById(screeningId)
            ?: throw EntityNotFoundException()

        val newReservations = seats.map { seat ->
            ReservationEntity(
                screening = screening,
                seat = seat,
            ).apply {
                this.createdBy = userId
            }
        }
        reservationRepository.saveAll(newReservations)

        // TODO: 비동기 적용
        messageClient.send(userId, screening.theaterName, screening.movie.title, requestSeatCodes)
    }
}
