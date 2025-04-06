package fake

import com.hanghe.redis.ratelimiter.RateLimiter

class FakeRateLimiter : RateLimiter {
    private val redis = mutableMapOf<String, Int>()
    private var isBlocked = false

    override fun getMoviesRateLimit(ip: String) {
        if (isBlocked) {
            throw RateLimitExceededException("Too many requests! Try again later")
        }

        if (redis.getOrDefault(ip, 0) >= 50) {
            throw RateLimitExceededException("Too many requests! You are blocked for 1 hour")
        }

        redis[ip] = redis.getOrDefault(ip, 50)
    }

    override fun reservedRateLimit(screeningId: Long, userId: String) {
        if (redis.containsKey("$screeningId:$userId")) {
            throw RateLimitExceededException("같은 시간대의 영화는 5분에 최대 1회 예약이 가능합니다. 잠시 후 시도해주세요.")
        }
    }

    override fun getMoviesRateLimitWithLuaScript(ip: String) {
        TODO("Not yet implemented")
    }

    fun block() {
        this.isBlocked = true
    }

    fun exceedRequest(ip: String) {
        this.redis[ip] = 50
    }

    fun setReserved(screeningId: Long, userId: String) {
        this.redis["$screeningId:$userId"] = 1
    }

    fun clear() {
        this.redis.clear()
        this.isBlocked = false
    }
}

class RateLimitExceededException(message: String) : RuntimeException(message)
