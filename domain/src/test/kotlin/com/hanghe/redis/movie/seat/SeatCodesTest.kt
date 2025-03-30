package com.hanghe.redis.movie.seat

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.string.shouldContain

class SeatCodesTest : BehaviorSpec({

    given("SeatCodes.validateContinuity()") {

        `when`("좌석들이 같은 행에서 연속된 열을 가질 때") {
            then("검증을 통과한다") {
                val codes = listOf("A2", "A1", "A3").map { SeatCode(it) }
                val seatCodes = SeatCodes(codes)

                shouldNotThrow<IllegalArgumentException> {
                    seatCodes.validateContinuity()
                }
            }
        }

        `when`("좌석들이 연속되지 않은 열을 가질 때") {
            then("예외가 발생한다") {
                val invalidSeatCodes = listOf("A1", "A3", "A4").map { SeatCode(it) }

                val actual = SeatCodes(invalidSeatCodes)

                shouldThrow<IllegalArgumentException> {
                    actual.validateContinuity()
                }.message shouldContain "연속된 열이어야 합니다"
            }
        }

        `when`("좌석들이 다른 행에 있을 때") {
            then("예외가 발생한다") {
                val invalidSeatCodes = listOf("A1", "B2").map { SeatCode(it) }

                val actual = SeatCodes(invalidSeatCodes)

                shouldThrow<IllegalArgumentException> {
                    actual.validateContinuity()
                }.message shouldContain "동일한 행에 있어야 합니다"
            }
        }
    }

    given("SeatCodes.validateNoOverlapWith()") {

        `when`("중복된 좌석이 없는 경우") {
            then("검증을 통과한다") {
                val requested = SeatCodes(listOf("A1", "A2", "A3").map { SeatCode(it) })
                val existing = SeatCodes(listOf("B1", "B2").map { SeatCode(it) })

                shouldNotThrow<IllegalArgumentException> {
                    requested.validateNoOverlapWith(existing)
                }
            }
        }

        `when`("중복된 좌석이 존재하는 경우") {
            then("예외가 발생한다") {
                val requested = SeatCodes(listOf("A1", "A2", "A3").map { SeatCode(it) })
                val existing = SeatCodes(listOf("A2", "B2").map { SeatCode(it) })

                val exception = shouldThrow<IllegalArgumentException> {
                    requested.validateNoOverlapWith(existing)
                }

                exception.message shouldContain "중복 좌석"
                exception.message shouldContain "A2"
            }
        }
    }
})
