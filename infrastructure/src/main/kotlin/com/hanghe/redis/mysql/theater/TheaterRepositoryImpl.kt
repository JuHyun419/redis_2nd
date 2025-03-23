package com.hanghe.redis.mysql.theater

import com.hanghe.redis.theater.TheaterEntity
import org.springframework.stereotype.Repository

@Repository
class TheaterRepositoryImpl(
    private val theaterJpaRepository: TheaterJpaRepository
) : TheaterRepository {

    override fun findAll(): List<TheaterEntity> {
        return theaterJpaRepository.findAll()
    }
}
