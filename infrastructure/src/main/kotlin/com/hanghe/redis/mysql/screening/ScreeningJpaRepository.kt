package com.hanghe.redis.mysql.screening

import com.hanghe.redis.screening.ScreeningEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ScreeningJpaRepository : JpaRepository<ScreeningEntity, Long> {

    @Query("""
        SELECT s FROM screenings s 
        JOIN FETCH s.movie 
        JOIN FETCH s.theater 
        WHERE s.movie.id = :movieId 
        ORDER BY s.startTime
    """
    )
    fun findByMovieIdOrderByStartTime(movieId: Long): List<ScreeningEntity>
}
