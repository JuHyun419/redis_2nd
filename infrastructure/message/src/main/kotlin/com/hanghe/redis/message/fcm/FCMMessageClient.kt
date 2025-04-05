package com.hanghe.redis.message.fcm

import com.hanghe.redis.message.MessageClient
import com.hanghe.redis.movie.seat.SeatCodes
import com.hanghe.redis.movie.seat.toLogString
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class FCMMessageClient : MessageClient {

    private val logger = LoggerFactory.getLogger(FCMMessageClient::class.java)

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    override fun send(event: ReservationCompletedEvent) {
        logger.info("`${event.userId}`님, ${event.theaterName} 영화관의 ${event.movieTitle} 영화 예매가 완료되었습니다. 좌석 정보는 [${event.seatCodes.toLogString()}] 입니다.")
    }

    data class ReservationCompletedEvent(
        val userId: String,
        val theaterName: String,
        val movieTitle: String,
        val seatCodes: SeatCodes
    )
}
