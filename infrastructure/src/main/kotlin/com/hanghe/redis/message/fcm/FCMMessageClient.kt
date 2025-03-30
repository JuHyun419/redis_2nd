package com.hanghe.redis.message.fcm

import com.hanghe.redis.message.MessageClient
import com.hanghe.redis.movie.seat.SeatCodes
import com.hanghe.redis.movie.seat.toLogString
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FCMMessageClient: MessageClient {

    private val logger = LoggerFactory.getLogger(FCMMessageClient::class.java)

    override fun send(userId: String, theaterName: String, title: String, seatCodes: SeatCodes) {
        logger.info("`$userId`님, $theaterName 영화관의 $title 영화 예매가 완료되었습니다. 좌석 정보는 [${seatCodes.toLogString()}] 입니다.")
    }
}
