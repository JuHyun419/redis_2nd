package com.hanghe.redis.dto

data class MovieReservationRequest(
    val screeningId: Long,
    val userId: String,
    val seatIds: List<Long>
) {
    init {
        require(seatIds.isNotEmpty()) { "좌석을 1개 이상 선택해야 합니다." }
        require(seatIds.size <= 5) { "최대 5개의 좌석만 예매할 수 있습니다." }
    }
}
