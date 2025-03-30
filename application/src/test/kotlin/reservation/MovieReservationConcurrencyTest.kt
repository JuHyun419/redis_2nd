package reservation

import com.hanghe.redis.message.MessageClient
import com.hanghe.redis.movie.seat.SeatCode
import com.hanghe.redis.movie.seat.SeatEntity
import com.hanghe.redis.mysql.reservation.ReservationRepository
import com.hanghe.redis.mysql.screening.ScreeningRepository
import com.hanghe.redis.mysql.seat.SeatRepository
import com.hanghe.redis.reservation.MovieReservationService
import com.hanghe.redis.reservation.UserReservationPolicy
import com.hanghe.redis.theater.TheaterEntity
import fixture.ScreeningEntityFixture
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class MovieReservationConcurrencyTest : BehaviorSpec({

    val reservationRepository = mockk<ReservationRepository>(relaxUnitFun = true)
    val seatRepository = mockk<SeatRepository>()
    val screeningRepository = mockk<ScreeningRepository>()
    val messageClient = mockk<MessageClient>(relaxed = true)
    val reservationPolicy = mockk<UserReservationPolicy>()

    val service = MovieReservationService(
        reservationRepository,
        seatRepository,
        screeningRepository,
        messageClient,
        reservationPolicy
    )

    val screeningId = 1L
    val seatIds = listOf(1L)
    val userId1 = "userA"
    val userId2 = "userB"

    val theater = TheaterEntity(name = "CGV")
    val seat = SeatEntity(1L, SeatCode("A1"), theater)
    val screening = ScreeningEntityFixture.create()

    given("MovieReservationService.reservation() 동시성 테스트") {
        `when`("락이 없는 상태에서 두 사용자가 동시에 예매를 시도하면") {
            then("예외가 발생하지 않고 중복 예매에 성공한다") {
                every { reservationRepository.findByScreeningId(screeningId) } returns emptyList()
                every { seatRepository.findByScreeningIdAndSeatIds(screeningId, seatIds) } returns listOf(seat)
                every { screeningRepository.findById(screeningId) } returns screening
                every { reservationPolicy.validate(any(), any(), any()) } just Runs
                every { reservationRepository.saveAll(any()) } returnsArgument 0

                val executor = Executors.newFixedThreadPool(2)
                val latch = CountDownLatch(2)
                val exceptionCount = AtomicInteger()

                listOf(userId1, userId2).forEach { userId ->
                    executor.submit {
                        try {
                            service.reservation(screeningId, userId, seatIds)
                        } catch (e: Throwable) {
                            exceptionCount.incrementAndGet()
                        } finally {
                            latch.countDown()
                        }
                    }
                }

                latch.await()

                exceptionCount.get() shouldBe 0
            }
        }
    }
})
