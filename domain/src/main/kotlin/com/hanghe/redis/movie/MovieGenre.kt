package com.hanghe.redis.movie

enum class MovieGenre(val type: String) {
    ACTION("액션"),

    ROMANCE("로맨스"),

    HORROR("호러"),

    SF("SF");

    companion object {
        fun from(genre: String): MovieGenre? {
            return entries.find { it.name == genre }
        }
    }
}
