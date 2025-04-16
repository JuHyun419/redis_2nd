package com.hanghe.redis.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.core.script.RedisScript

@Configuration
class LuaScriptConfig {

    @Bean
    fun moviesRateLimitScript(): RedisScript<Long> {
        val scriptSource = ClassPathResource("redis-scripts/movie_rate_limit.lua")

        return RedisScript.of(scriptSource, Long::class.java)
    }
}
