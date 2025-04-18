package com.hanghe.redis.theater

import org.springframework.stereotype.Repository

@Repository
class TheaterRepositoryImpl(
    private val theaterJpaRepository: TheaterJpaRepository
) : TheaterRepository {

    override fun findAll(): List<TheaterEntity> {
        return theaterJpaRepository.findAll()
    }
}
