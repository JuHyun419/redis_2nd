package com.hanghe.redis.message

import com.hanghe.redis.movie.seat.SeatCodes

interface MessageClient {

    fun send(userId: String, theaterName: String, title: String, seatCodes: SeatCodes)
}
