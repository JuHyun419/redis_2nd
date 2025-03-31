package com.hanghe.redis.reservation

import org.springframework.stereotype.Component

@Component
class UserReservationPolicy(
    private val maxAllowedSeatsPerScreening: Int = 5
) {

    fun validate(
        userId: String,
        existingCount: Int,
        newCount: Int
    ) {
        require(existingCount + newCount <= maxAllowedSeatsPerScreening) {
            """
            상영 별 최대 $maxAllowedSeatsPerScreening 개의 좌석을 예매할 수 있습니다.
            사용자: $userId, 이미 예매한 좌석 수: $existingCount, 요청한 좌석 수: $newCount
            """.trimIndent()
        }
    }
}

fun List<ReservationEntity>.countByUser(userId: String) = this.count { it.createdBy == userId }
