package com.hanghe.redis.mysql.movie

import com.hanghe.redis.movie.MovieEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MovieJpaRepository : JpaRepository<MovieEntity, Long>
