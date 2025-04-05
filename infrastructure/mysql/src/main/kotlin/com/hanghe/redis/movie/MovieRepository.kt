package com.hanghe.redis.movie

interface MovieRepository {

    fun findAll(): List<MovieEntity>

    fun search(title: String?, genre: String?): List<MovieEntity>
}
