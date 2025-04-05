package com.hanghe.redis.ratelimiter

interface RateLimiter {

    fun getMoviesRateLimit(ip: String)

    fun reservedRateLimit(screeningId: Long, userId: String)
}
