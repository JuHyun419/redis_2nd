package fixture

import com.hanghe.redis.movie.response.GetMovieScreeningResponses
import java.time.LocalDate
import java.time.LocalTime

object GetMovieScreeningResponsesFixture {
    fun create(title: String, genre: String): GetMovieScreeningResponses {
        return create(
            title = title,
            genre = genre,
            grade = "ALL",
            releaseDate = LocalDate.of(2010, 7, 16),
            thumbnailImage = "https://example.com/default-thumbnail.jpg",
            runningTime = 148,
            screeningCount = 1,
            screeningTimes = listOf(
                GetMovieScreeningResponses.GetMovieScreeningResponse.ScreeningTime(
                    theaterName = "CGV",
                    startTime = LocalTime.of(10, 0),
                    endTime = LocalTime.of(12, 30)
                )
            )
        )
    }

    private fun create(
        title: String = "Inception",
        genre: String = "ACTION",
        grade: String = "ALL",
        releaseDate: LocalDate = LocalDate.of(2010, 7, 16),
        thumbnailImage: String = "https://example.com/default-thumbnail.jpg",
        runningTime: Int = 148,
        screeningCount: Int = 1,
        screeningTimes: List<GetMovieScreeningResponses.GetMovieScreeningResponse.ScreeningTime> = listOf(
            GetMovieScreeningResponses.GetMovieScreeningResponse.ScreeningTime(
                theaterName = "CGV",
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 30)
            )
        )
    ): GetMovieScreeningResponses {
        val movieResponse = GetMovieScreeningResponses.GetMovieScreeningResponse(
            title = title,
            grade = grade,
            genre = genre,
            releaseDate = releaseDate,
            thumbnailImage = thumbnailImage,
            runningTime = runningTime,
            screeningCount = screeningCount,
            screeningTimes = screeningTimes
        )

        return GetMovieScreeningResponses(listOf(movieResponse))
    }
}
