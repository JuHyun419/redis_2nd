package movie

import com.hanghe.redis.movie.MovieService
import com.hanghe.redis.movie.response.GetMovieScreeningResponses
import com.hanghe.redis.movie.MovieRepository
import com.hanghe.redis.screening.ScreeningRepository
import fake.FakeCacheManager
import fake.FakeRateLimiter
import fake.RateLimitExceededException
import fixture.GetMovieScreeningResponsesFixture
import fixture.MovieEntityFixture
import fixture.ScreeningEntityFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.Duration

class MovieServiceTest : BehaviorSpec({

    val movieRepository = mockk<MovieRepository>(relaxed = true)
    val screeningRepository = mockk<ScreeningRepository>(relaxed = true)
    val fakeCacheManager = FakeCacheManager()
    val rateLimiter = FakeRateLimiter()

    val movieService = MovieService(
        movieRepository = movieRepository,
        screeningRepository = screeningRepository,
        cacheManager = fakeCacheManager,
        rateLimiter = rateLimiter,
    )

    beforeTest {
        rateLimiter.clear()
    }

    given("MovieService.getAllScreeningMovies()") {
        val expectedTitle = "Inception"
        val expectedGenre = "SF"
        val ip = "127.0.0.1"
        val expectedCacheKey = "movies:$expectedGenre:v1"
        val movies = listOf(MovieEntityFixture.create(1L, expectedTitle, expectedGenre))
        val screenings = listOf(ScreeningEntityFixture.create(1L, "CGV"))

        `when`("영화 검색 시 제목과 장르가 존재하는 경우") {
            every { movieRepository.search(expectedTitle, expectedGenre) } returns movies
            every { screeningRepository.findByMovieIdOrderByStartTime(1) } returns listOf(screenings[0])

            then("캐싱 처리를 하지 않고 검색 처리를 진행한다") {
                val actual = movieService.getAllScreeningMovies(expectedTitle, expectedGenre, ip)

                actual.responses.size shouldBe 1
                actual.responses[0].title shouldBe expectedTitle
                actual.responses[0].genre shouldBe expectedGenre

                val cachedResult = fakeCacheManager.getOrNull(expectedCacheKey, GetMovieScreeningResponses::class)

                cachedResult shouldBe null
            }
        }

        `when`("영화 검색 시 장르만 존재하고, 캐시 미스인 경우") {
            every { movieRepository.search(null, expectedGenre) } returns movies
            every { screeningRepository.findByMovieIdOrderByStartTime(1) } returns listOf(screenings[0])

            then("검색 처리를 진행하고 검색 결과를 캐싱처리 한다") {
                val actual = movieService.getAllScreeningMovies(null, expectedGenre, ip)
                val cachedActual = fakeCacheManager.getOrNull(expectedCacheKey, GetMovieScreeningResponses::class)

                actual.responses.size shouldBe 1
                cachedActual shouldBe actual
            }
        }

        `when`("영화 검색 시 장르만 존재하고, 캐시 히트인 경우") {
            fakeCacheManager.clear()

            val cachedResponse = GetMovieScreeningResponsesFixture.create(title = expectedTitle, genre = expectedGenre)

            fakeCacheManager.getOrPut(
                expectedCacheKey,
                Duration.ofHours(1),
                GetMovieScreeningResponses::class
            ) { cachedResponse }

            then("이미 존재하는 캐시 데이터를 가져온다") {
                val actual = movieService.getAllScreeningMovies(null, expectedGenre, ip)

                actual shouldBe cachedResponse
            }
        }

        `when`("제목이 255자를 초과할 경우") {
            val invalidTitle = "A".repeat(256)

            then("예외를 던진다") {
                shouldThrow<IllegalArgumentException> {
                    movieService.getAllScreeningMovies(invalidTitle, expectedGenre, ip)
                }.message shouldBe "Title cannot exceed 255 characters"
            }
        }

        `when`("영화 장르가 존재하지 않는 경우") {
            val title = "Inception"
            val invalidGenre = "INVALID"

            then("예외를 던진다") {
                shouldThrow<IllegalArgumentException> {
                    movieService.getAllScreeningMovies(title, invalidGenre, ip)
                }.message shouldBe "$invalidGenre is not exist in MovieGenre"
            }
        }

        `when`("IP 가 차단된 경우") {
            then("RateLimitExceedException 예외를 던진다") {
                rateLimiter.block()

                shouldThrow<RateLimitExceededException> {
                    movieService.getAllScreeningMovies(expectedTitle, expectedGenre, ip)
                }.message shouldBe "Too many requests! Try again later"
            }
        }

        `when`("동일한 IP 요청이 50회를 초과한 경우") {
            then("RateLimitExceedException 예외를 던진다") {
                rateLimiter.exceedRequest(ip)

                shouldThrow<RateLimitExceededException> {
                    movieService.getAllScreeningMovies(expectedTitle, expectedGenre, ip)
                }.message shouldBe "Too many requests! You are blocked for 1 hour"
            }
        }
    }
})
