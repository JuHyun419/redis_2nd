package com.hanghe.redis.movie

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class MovieRepositoryImpl(
    private val movieJpaRepository: MovieJpaRepository,
    private val jpaQueryFactory: JPAQueryFactory
) : MovieRepository {

    override fun findAll(): List<MovieEntity> {
        return movieJpaRepository.findAll()
    }

    override fun search(title: String?, genre: String?): List<MovieEntity> {
        return jpaQueryFactory.selectFrom(qMovie)
            .where(
                likeTitle(title),
                eqGenre(genre)
            )
            .orderBy(qMovie.releaseDate.desc())
            .fetch()
    }

    private fun likeTitle(title: String?): BooleanExpression? {
        return title?.let { qMovie.title.like("%$it%") }
    }

    private fun eqGenre(genre: String?): BooleanExpression? {
        return genre?.let { qMovie.genre.eq(MovieGenre.valueOf(it)) }
    }

    companion object {
        private val qMovie = QMovieEntity.movieEntity
    }

}
