package com.hanghe.redis.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.hanghe.redis.cache.RedisCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate

// TODO: Redis 모듈 분리
@Configuration
class RedisConfig {

    @Bean
    fun redisCacheManager(stringRedisTemplate: StringRedisTemplate): RedisCacheManager {
        return RedisCacheManager(stringRedisTemplate, redisObjectMapper())
    }

    private fun redisObjectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            registerModule(JavaTimeModule())
            registerModule(Jdk8Module())
            registerModule(KotlinModule.Builder().build())

            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            activateDefaultTypingAsProperty(polymorphicTypeValidator, ObjectMapper.DefaultTyping.NON_FINAL, "@class")
        }
    }
}
