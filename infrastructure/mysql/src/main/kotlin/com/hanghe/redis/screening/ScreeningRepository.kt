package com.hanghe.redis.screening

interface ScreeningRepository {

    fun findByMovieIdOrderByStartTime(movieId: Long): List<ScreeningEntity>

    fun saveAll(movies: List<ScreeningEntity>): List<ScreeningEntity>

    fun findById(screeningId: Long): ScreeningEntity?

    fun getById(screeningId: Long): ScreeningEntity
}
