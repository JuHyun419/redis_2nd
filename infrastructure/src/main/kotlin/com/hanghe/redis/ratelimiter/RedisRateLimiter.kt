package com.hanghe.redis.ratelimiter

import com.hanghe.redis.exception.RateLimitExceededException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisRateLimiter(
    private val redisTemplate: RedisTemplate<String, String>
) : RateLimiter {

    override fun validateAllowed(ip: String) {
        if (isBlocked(ip)) {
            throw RateLimitExceededException("Too many requests! Try again later")
        }

        val key = "$MOVIES_RATE_LIMIT_KEY:$ip"
        val currentRequest = redisTemplate.opsForValue().increment(key, 1) ?: 0L

        setExpireWhenFirstRequest(currentRequest, key)

        validateExceedRequests(ip, currentRequest)
    }

    override fun reservedRateLimit(screeningId: Long, userId: String) {
        val key = "$RESERVATION_BLOCKED_KEY:$screeningId:$userId"

        val isFirstReservation = redisTemplate
            .opsForValue()
            .setIfAbsent(key, "BLOCKED", RESERVATION_BLOCK_TIME)

        if (isFirstReservation != true) {
            throw RateLimitExceededException("같은 시간대의 영화는 5분에 최대 1회 예약이 가능합니다. 잠시 후 시도해주세요.")
        }
    }

    private fun validateExceedRequests(ip: String, currentRequest: Long) {
        if (currentRequest >= REQUEST_LIMIT) {
            blocked(ip)
            throw RateLimitExceededException("Too many requests! You are blocked for 1 hour")
        }
    }

    private fun isBlocked(ip: String): Boolean {
        val blockedKey = "$MOVIES_BLOCKED_KEY:$ip"

        return redisTemplate.hasKey(blockedKey)
    }

    private fun blocked(ip: String) {
        val blockedKey = "$MOVIES_BLOCKED_KEY:$ip"

        redisTemplate.opsForValue().set(blockedKey, "BLOCKED", BLOCK_TIME)
    }

    private fun setExpireWhenFirstRequest(current: Long, key: String) {
        if (current == FIRST_REQUEST_COUNT) {
            redisTemplate.expire(key, RATE_LIMIT_TIME_WINDOW)
        }
    }

    companion object {
        private const val MOVIES_RATE_LIMIT_KEY = "movies:rate:limit"
        private const val MOVIES_BLOCKED_KEY = "movies:blocked"
        private const val RESERVATION_BLOCKED_KEY = "reservation:blocked"

        private const val REQUEST_LIMIT = 50
        private const val FIRST_REQUEST_COUNT = 1L

        private val RATE_LIMIT_TIME_WINDOW = Duration.ofMinutes(1)
        private val BLOCK_TIME = Duration.ofHours(1)

        private val RESERVATION_BLOCK_TIME = Duration.ofMinutes(5)
    }
}
