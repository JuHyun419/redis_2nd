package com.hanghe.redis.ratelimiter

interface RateLimiter {

    fun validateAllowed(ip: String)

    fun reservedRateLimit(screeningId: Long, userId: String)
}
