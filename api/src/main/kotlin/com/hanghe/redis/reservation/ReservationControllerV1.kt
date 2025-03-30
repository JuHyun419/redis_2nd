package com.hanghe.redis.reservation

import com.hanghe.redis.dto.MovieReservationRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/movies/reservations")
class ReservationControllerV1(
    private val service: MovieReservationService
) {

    @PostMapping
    fun reservation(
        @RequestBody request: MovieReservationRequest
    ) {
        service.reservation(
            screeningId = request.screeningId,
            userId = request.userId,
            seatIds = request.seatIds,
        )
    }

    @PostMapping("/pessimistic")
    fun reservationWithPessimisticLock(
        @RequestBody request: MovieReservationRequest
    ) {
        service.reservationWithPessimisticLock(
            screeningId = request.screeningId,
            userId = request.userId,
            seatIds = request.seatIds,
        )
    }
}
