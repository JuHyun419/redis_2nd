package com.hanghe.redis.screening

import jakarta.persistence.EntityNotFoundException
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

    override fun getById(screeningId: Long): ScreeningEntity {
        return findById(screeningId) ?: throw EntityNotFoundException()
    }
}
