package com.hanghe.redis.movie.seat

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class SeatCodeTest : BehaviorSpec({

    given("SeatCode.invoke()") {
        `when`("올바른 좌석 코드인 경우") {
            then("SeatCode 객체가 정상적으로 생성된다") {
                val expected = "A1"

                val actual = SeatCode(expected)

                actual.value shouldBe expected
            }
        }

        `when`("좌석 코드가 A1 ~ E5 가 아닌 경우") {
            then("예외가 발생한다") {
                val invalidCodes = listOf("4A", "F1", "AA", "B0", "", "Z9")

                invalidCodes.forEach {
                    shouldThrow<IllegalArgumentException> {
                        SeatCode(it)
                    }.message shouldContain "좌석 코드는 A1~E5 형식이어야 합니다"
                }
            }
        }
    }

    given("SeatCode.validateNextTo()") {

        `when`("두 좌석이 같은 행에 있고 열이 연속될 때") {
            then("검증을 통과한다") {
                val a1 = SeatCode("A1")
                val a2 = SeatCode("A2")

                shouldNotThrow<IllegalArgumentException> {
                    a1.validateNextTo(a2)
                }
            }
        }

        `when`("두 좌석이 다른 행일 때") {
            then("예외가 발생한다") {
                val a1 = SeatCode("A1")
                val b2 = SeatCode("B2")

                shouldThrow<IllegalArgumentException> {
                    a1.validateNextTo(b2)
                }.message shouldContain "동일한 행에 있어야 합니다"
            }
        }

        `when`("두 좌석의 열이 연속되지 않을 때") {
            then("예외가 발생한다") {
                val a1 = SeatCode("A1")
                val a3 = SeatCode("A3")

                shouldThrow<IllegalArgumentException> {
                    a1.validateNextTo(a3)
                }.message shouldContain "연속된 열이어야 합니다"
            }
        }
    }

    given("SeatCodes.toLogString()") {

        `when`("좌석 코드가 여러 개 주어졌을 때") {
            then("콤마로 연결된 문자열을 반환한다") {
                val seatCodes = SeatCodes(listOf("A1", "A2", "A3").map { SeatCode(it) })

                val result = seatCodes.toLogString()

                result shouldBe "A1, A2, A3"
            }
        }

        `when`("좌석 코드가 한 개만 주어졌을 때") {
            then("해당 좌석 코드 문자열만 반환한다") {
                val seatCodes = SeatCodes(listOf(SeatCode("B1")))

                val result = seatCodes.toLogString()

                result shouldBe "B1"
            }
        }
    }

})

