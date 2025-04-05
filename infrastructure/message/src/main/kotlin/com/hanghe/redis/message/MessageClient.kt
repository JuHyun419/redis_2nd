package com.hanghe.redis.message

import com.hanghe.redis.message.fcm.FCMMessageClient

interface MessageClient {

    fun send(event: FCMMessageClient.ReservationCompletedEvent)
}
