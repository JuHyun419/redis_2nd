package com.hanghe.redis.ratelimiter

import com.hanghe.redis.exception.RateLimitExceededException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisRateLimiter(
    private val redisTemplate: RedisTemplate<String, String>,
) : RateLimiter {

    /**
     * 1분 내 50회 이상 요청 시 1시간 동안 해당 IP 차단
     */
    override fun getMoviesRateLimit(ip: String) {
        if (isBlocked(ip)) {
            throw RateLimitExceededException("Too many requests! Try again later")
        }

        val key = "$MOVIES_RATE_LIMIT_KEY:$ip"
        val currentRequest = redisTemplate.opsForValue().increment(key, 1) ?: 0L

        setExpireWhenFirstRequest(currentRequest, key)

        validateExceedRequests(ip, currentRequest)
    }

    /**
     * 유저당 같은 시간대의 영화는 5분에 최대 1번 예약 가능
     */
    override fun reservedRateLimit(screeningId: Long, userId: String) {
        val key = "$RESERVATION_BLOCKED_KEY:$screeningId:$userId"

        val isFirstReservation = redisTemplate
            .opsForValue()
            .setIfAbsent(key, "BLOCKED", RESERVATION_BLOCK_TIME)

        if (isFirstReservation != true) {
            throw RateLimitExceededException("같은 시간대의 영화는 5분에 최대 1회 예약이 가능합니다. 잠시 후 시도해주세요.")
        }
    }

    override fun getMoviesRateLimitWithLuaScript(ip: String) {
        val script = """
            local rateLimitKey = KEYS[1]
            local blockedKey = KEYS[2]
            
            local requestLimitCount = tonumber(ARGV[1])
            local blockTime = tonumber(ARGV[2])
            lock rateLimitTTL = tonumber(ARGV[3])
            
            if redis.call("EXISTS", blockedKey) == 1 then
                return -1
            end
            
            local current = redis.call("INCR", rateLimitKey)
            if current == 1 then
                redis.call("EXPIRE", rateLimitKey, rateLimitTTL)
            end

            if current > tonumber(ARGV[1]) then
                redis.call("SET", blockedKey, "BLOCKED", "EX", blockTime)
                return -2
            end
            
            return current
        """.trimIndent()

        val result = redisTemplate.execute(
            RedisScript.of(script, Long::class.java),
            listOf("$MOVIES_RATE_LIMIT_KEY:$ip", "$MOVIES_BLOCKED_KEY:$ip"),
            "$REQUEST_LIMIT", // ARGV[1] - 요청 한도
            "3600", // ARGV[2] - 블록 타임 (1시간)
            "60" // ARGV[3] - TTL (1분)
        )

        when (result) {
            -1L -> throw RateLimitExceededException("Too many requests! Try again later")
            -2L -> throw RateLimitExceededException("Too many requests! You are blocked for 1 hour")
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
