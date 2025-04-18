package com.hanghe.redis.movie

import com.hanghe.redis.cache.CacheInfo
import com.hanghe.redis.cache.CacheManager
import com.hanghe.redis.movie.response.GetMovieScreeningResponses
import com.hanghe.redis.ratelimiter.RateLimiter
import com.hanghe.redis.screening.ScreeningRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MovieService(
    private val movieRepository: MovieRepository,
    private val screeningRepository: ScreeningRepository,

    private val cacheManager: CacheManager,
    private val rateLimiter: RateLimiter
) {

    fun getAllScreeningMovies(
        title: String?,
        genre: String?,
        ip: String
    ): GetMovieScreeningResponses {
        validateParams(title, genre)

        rateLimiter.getMoviesRateLimitWithLuaScript(ip)
//        rateLimiter.getMoviesRateLimit(ip)

        return if (title.isNullOrEmpty()) {
            searchCached(title, genre)
        } else {
            search(title, genre)
        }
    }

    private fun searchCached(title: String?, genre: String?): GetMovieScreeningResponses {
        val (key, ttl) = CacheInfo.getMovies(genre!!)

        return cacheManager.getOrPut(key, ttl, GetMovieScreeningResponses::class) {
            search(title, genre)
        }
    }

    private fun search(title: String?, genre: String?): GetMovieScreeningResponses {
        val allMovies = movieRepository.search(title, genre)

        val responses = allMovies.map { movie ->
            val screenings = screeningRepository.findByMovieIdOrderByStartTime(movie.id!!)

            GetMovieScreeningResponses.GetMovieScreeningResponse.fromEntity(movie, screenings)
        }

        return GetMovieScreeningResponses(responses)
    }

    private fun validateParams(title: String?, genre: String?) {
        title?.let { require(it.length <= 255) { "Title cannot exceed 255 characters" } }
        genre?.let { requireNotNull(MovieGenre.from(genre)) { "$genre is not exist in MovieGenre" } }
    }
}
