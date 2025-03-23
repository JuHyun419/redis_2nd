package com.hanghe.redis.cache

import java.time.Duration

object CacheInfo {

    private const val CACHE_VERSION = "v1"

    fun getMovies(genre: String): CachePair {
        return CachePair("movies:$genre:$CACHE_VERSION", Duration.ofHours(1))
    }

    fun getMovies(title: String, genre: String): CachePair {
        return CachePair("movies:$title:$genre:$CACHE_VERSION", Duration.ofHours(1))
    }
}

data class CachePair(val key: String, val ttl: Duration)
