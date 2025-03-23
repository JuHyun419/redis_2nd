package com.hanghe.redis.mysql.movie

import com.hanghe.redis.movie.MovieEntity

interface MovieRepository {

    fun findAll(): List<MovieEntity>

    fun search(title: String?, genre: String?): List<MovieEntity>
}
