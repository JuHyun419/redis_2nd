package com.hanghe.redis.cache

import java.time.Duration
import kotlin.reflect.KClass

interface CacheManager {

    fun <T : Any> getOrNull(key: String, clazz: KClass<T>): T?

    fun <T : Any> getOrPut(key: String, ttl: Duration, clazz: KClass<T>, cacheable: () -> T): T

    fun <T : Any> put(key: String, ttl: Duration, clazz: KClass<T>, cacheable: () -> T): T
}
