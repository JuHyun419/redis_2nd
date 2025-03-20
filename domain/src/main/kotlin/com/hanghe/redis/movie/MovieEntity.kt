package com.hanghe.redis.movie

import com.hanghe.redis.BaseEntity
import com.hanghe.redis.screening.ScreeningEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.time.LocalDate

@Entity(name = "movies")
class MovieEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val title: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val grade: MovieGrade,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val genre: MovieGenre,

    @Column(nullable = false)
    val releaseDate: LocalDate,

    @Column
    val thumbnailImage: String = DEFAULT_THUMBNAIL_IMAGE,

    @Column(nullable = false)
    val runningTime: Int,

    @OneToMany(mappedBy = "movie")
    val screenings: Set<ScreeningEntity> = emptySet()
) : BaseEntity() {

    companion object {
        private const val DEFAULT_THUMBNAIL_IMAGE = "https://www.naver.com/"
    }
}
