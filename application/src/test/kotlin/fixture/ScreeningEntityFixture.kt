package fixture

import com.hanghe.redis.movie.MovieEntity
import com.hanghe.redis.screening.ScreeningEntity
import com.hanghe.redis.theater.TheaterEntity
import java.time.LocalTime

object ScreeningEntityFixture {
    fun create(): ScreeningEntity {
        // 매개변수가 없는 경우 기본 값으로 `create` 호출
        return create(
            id = 1L,
            theaterName = "CGV",
            movie = MovieEntityFixture.create(),
            theater = TheaterEntityFixture.create(),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(12, 30)
        )
    }

    fun create(id: Long, theaterName: String): ScreeningEntity {
        return create(
            id = id,
            theaterName = theaterName,
            movie = MovieEntityFixture.create(),
            theater = TheaterEntityFixture.create(),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(12, 30)
        )
    }

    private fun create(
        id: Long? = 1L,
        theaterName: String = "CGV",
        movie: MovieEntity = MovieEntityFixture.create(),
        theater: TheaterEntity = TheaterEntityFixture.create(),
        startTime: LocalTime = LocalTime.of(10, 0),
        endTime: LocalTime = LocalTime.of(12, 30)
    ): ScreeningEntity {
        return ScreeningEntity(
            id = id,
            theaterName = theaterName,
            movie = movie,
            theater = theater,
            startTime = startTime,
            endTime = endTime
        )
    }
}
