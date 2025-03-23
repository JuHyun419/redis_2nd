package com.hanghe.redis.movie

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MovieGenreTest : BehaviorSpec({

    given("MovieGenre.from()") {

        `when`("장르가 'ACTION'일 때") {
            then("MovieGenre.ACTION을 반환한다") {
                val actual = MovieGenre.from("ACTION")

                actual shouldBe MovieGenre.ACTION
            }
        }

        `when`("장르가 'ROMANCE'일 때") {
            then("MovieGenre.ROMANCE를 반환한다") {
                val actual = MovieGenre.from("ROMANCE")

                actual shouldBe MovieGenre.ROMANCE
            }
        }

        `when`("장르가 'HORROR'일 때") {
            then("MovieGenre.HORROR를 반환한다") {
                val actual = MovieGenre.from("HORROR")

                actual shouldBe MovieGenre.HORROR
            }
        }

        `when`("장르가 'SF'일 때") {
            then("MovieGenre.SF를 반환한다") {
                val actual = MovieGenre.from("SF")

                actual shouldBe MovieGenre.SF
            }
        }

        `when`("존재하지 않는 장르가 주어졌을 때") {
            then("null을 반환한다") {
                val actual = MovieGenre.from("DRAMA")

                actual shouldBe null
            }
        }

        `when`("빈 문자열이 주어졌을 때") {
            then("null을 반환한다") {
                val actual = MovieGenre.from("")

                actual shouldBe null
            }
        }
    }
})
