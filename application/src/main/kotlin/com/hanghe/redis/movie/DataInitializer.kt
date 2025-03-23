package com.hanghe.redis.movie

import com.hanghe.redis.mysql.movie.MovieRepository
import com.hanghe.redis.mysql.screening.ScreeningRepository
import com.hanghe.redis.mysql.theater.TheaterRepository
import com.hanghe.redis.screening.ScreeningEntity
import org.springframework.boot.CommandLineRunner
import java.time.LocalTime
import kotlin.random.Random

/**
 * 초기 상영 데이터 셋업을 위해 필요한 클래스로, 최초 실행 후 Bean 등록 제거를 위해 @Component 어노테이션을 제거합니다.
 */
//@Component
class DataInitializer(
    private val screeningRepository: ScreeningRepository,
    private val movieRepository: MovieRepository,
    private val theaterRepository: TheaterRepository
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val movies = movieRepository.findAll()
        val theaters = theaterRepository.findAll()

        if (movies.isEmpty() || theaters.isEmpty()) {
            println("영화와 극장 데이터를 먼저 삽입해주세요!")
            return
        }

        val random = Random(System.currentTimeMillis())
        val screenings = mutableListOf<ScreeningEntity>()

        repeat(500) {
            val movie = movies[random.nextInt(movies.size)]
            val theater = theaters[random.nextInt(theaters.size)]
            val startTime = LocalTime.of(random.nextInt(9, 23), random.nextInt(0, 60))
            val endTime = startTime.plusMinutes(movie.runningTime.toLong())

            screenings.add(
                ScreeningEntity(
                    theaterName = theater.name,
                    movie = movie,
                    theater = theater,
                    startTime = startTime,
                    endTime = endTime
                )
            )
        }

        screeningRepository.saveAll(screenings)
    }
}
