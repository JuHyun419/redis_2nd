package fixture

import com.hanghe.redis.movie.MovieEntity
import com.hanghe.redis.movie.MovieGenre
import com.hanghe.redis.movie.MovieGrade
import java.time.LocalDate

object MovieEntityFixture {

    fun create(): MovieEntity {
        return create(
            id = 1L,
            title = "Inception",
            grade = MovieGrade.ALL,
            genre = MovieGenre.ACTION,
            releaseDate = LocalDate.of(2010, 7, 16),
            thumbnailImage = "thumb/default-thumbnail.jpg",
            runningTime = 148
        )
    }

    fun create(id: Long, title: String, genre: String): MovieEntity {
        return create(
            id = id,
            title = title,
            grade = MovieGrade.ALL,
            genre = MovieGenre.from(genre)!!,
            releaseDate = LocalDate.of(2010, 7, 16),
            thumbnailImage = "thumb/default-thumbnail.jpg",
            runningTime = 148
        )
    }


    fun create(id: Long, title: String, genre: MovieGenre): MovieEntity {
        return create(
            id = id,
            title = title,
            grade = MovieGrade.ALL,
            genre = genre,
            releaseDate = LocalDate.of(2010, 7, 16),
            thumbnailImage = "thumb/default-thumbnail.jpg",
            runningTime = 148
        )
    }

    private fun create(
        id: Long? = 1L,
        title: String = "Inception",
        grade: MovieGrade = MovieGrade.ALL,
        genre: MovieGenre = MovieGenre.ACTION,
        releaseDate: LocalDate = LocalDate.of(2010, 7, 16),
        thumbnailImage: String = "thumb/default-thumbnail.jpg",
        runningTime: Int = 148
    ): MovieEntity {
        return MovieEntity(
            id = id,
            title = title,
            grade = grade,
            genre = genre,
            releaseDate = releaseDate,
            thumbnailImage = thumbnailImage,
            runningTime = runningTime
        )
    }
}
