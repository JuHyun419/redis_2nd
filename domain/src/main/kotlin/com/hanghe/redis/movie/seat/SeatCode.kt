package com.hanghe.redis.movie.seat

@JvmInline
value class SeatCode(val value: String) {
    private val row: Char get() = value[0]
    val column: Int get() = value.substring(1).toInt()

    init {
        require(value.matches(Regex("^[A-E][1-5]\$"))) {
            "좌석 코드는 A1~E5 형식이어야 합니다: $value"
        }
    }

    fun validateNextTo(next: SeatCode) {
        // TODO: Custom Exception
        require(this.row == next.row) { "좌석은 동일한 행에 있어야 합니다. (${this.value}, ${next.value})" }

        require(this.column + 1 == next.column) { "좌석은 같은 행에서 연속된 열이어야 합니다. (${this.column} → ${next.column})" }
    }

    companion object {
        operator fun invoke(value: String): SeatCode = from(value)

        private fun from(raw: String): SeatCode = SeatCode(raw.trim().uppercase())
    }
}
