package com.hanghe.redis.mysql.screening

import com.hanghe.redis.screening.ScreeningEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class ScreeningRepositoryImpl(
    private val screeningJpaRepository: ScreeningJpaRepository
) : ScreeningRepository {

    override fun findByMovieIdOrderByStartTime(movieId: Long): List<ScreeningEntity> {
        return screeningJpaRepository.findByMovieIdOrderByStartTime(movieId)
    }

    override fun saveAll(movies: List<ScreeningEntity>): List<ScreeningEntity> {
        return screeningJpaRepository.saveAll(movies)
    }

    override fun findById(screeningId: Long): ScreeningEntity? {
        return screeningJpaRepository.findByIdOrNull(screeningId)
    }
}
