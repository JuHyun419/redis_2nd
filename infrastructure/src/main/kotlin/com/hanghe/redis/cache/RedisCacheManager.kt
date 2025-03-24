package com.hanghe.redis.cache

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import java.time.Duration
import kotlin.reflect.KClass

class RedisCacheManager(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
): CacheManager {

    override fun <T : Any> getOrNull(key: String, clazz: KClass<T>): T? {
        return redisTemplate.opsForValue().get(key)?.let {
            objectMapper.readValue(it, clazz.java)
        }
    }

    override fun <T : Any> getOrPut(key: String, ttl: Duration, clazz: KClass<T>, cacheable: () -> T): T {
        return getOrNull(key, clazz) ?: run {
            put(key, ttl, clazz, cacheable)
        }
    }

    override fun <T : Any> put(key: String, ttl: Duration, clazz: KClass<T>, cacheable: () -> T): T {
        val value = cacheable()

        runCatching {
            val serializedValue = objectMapper.writeValueAsString(value)
            redisTemplate.opsForValue().set(key, serializedValue, ttl)
        }.onFailure { e ->
            logger.error("Failed to cache data put in Redis, key: `$key`, Error Message: ${e.message}")
        }

        return value
    }

    companion object {
        private val logger = LoggerFactory.getLogger(RedisCacheManager::class.java)
    }
}
