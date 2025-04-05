package com.hanghe.redis.movie

import org.springframework.data.jpa.repository.JpaRepository

interface MovieJpaRepository : JpaRepository<MovieEntity, Long>
