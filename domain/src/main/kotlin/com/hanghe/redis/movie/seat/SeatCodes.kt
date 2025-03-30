package com.hanghe.redis.movie.seat

@JvmInline
value class SeatCodes(val values: List<SeatCode>) {

    init {
        require(values.isNotEmpty()) { "좌석이 비어있습니다." }
    }

    fun validate(seatCodes: SeatCodes?) {
        validateContinuity()
        validateNoOverlapWith(seatCodes)
    }

    // 모든 좌석이 이어 붙어진 형식인지 검증
    fun validateContinuity() {
        values
            .sortedBy { it.column }
            .zipWithNext { prev, next -> prev.validateNextTo(next) }
    }

    // 중복 예약이 존재하지 않는지 검증
    fun validateNoOverlapWith(existing: SeatCodes?) {
        if (existing == null) return

        val duplicated = values.filter { it in existing.values }

        require(duplicated.isEmpty()) {
            "요청하신 예약 중 이미 예약된 좌석이 존재합니다. 중복 좌석: ${duplicated.map { it.value }}"
        }
    }

    companion object {
        private fun from(seats: List<SeatEntity>): SeatCodes = SeatCodes(seats.map { it.seatCode })

        operator fun invoke(seats: List<SeatEntity>): SeatCodes = from(seats)
    }
}

fun SeatCodes.toLogString(): String {
    return this.values.joinToString(", ") { it.value }
}
