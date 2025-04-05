package fake

import com.hanghe.redis.cache.CacheManager
import java.time.Duration
import kotlin.reflect.KClass

class FakeCacheManager : CacheManager {
    private val cache = mutableMapOf<String, Any>()

    override fun <T : Any> getOrNull(key: String, clazz: KClass<T>): T? {
        return cache[key]?.let { clazz.java.cast(it) }
    }

    override fun <T : Any> getOrPut(
        key: String,
        ttl: Duration,
        clazz: KClass<T>,
        cacheable: () -> T
    ): T {
        return getOrNull(key, clazz) ?: run {
            put(key, ttl, clazz, cacheable)
        }
    }

    override fun <T : Any> put(
        key: String,
        ttl: Duration,
        clazz: KClass<T>,
        cacheable: () -> T
    ): T {
        val value = cacheable()
        cache[key] = value

        return value
    }

    fun clear() {
        cache.clear()
    }
}
