package fixture

import com.hanghe.redis.theater.TheaterEntity

object TheaterEntityFixture {
    fun create(): TheaterEntity {
        return TheaterEntity(
            id = 1L,
            name = "CGV"
        )
    }
}
