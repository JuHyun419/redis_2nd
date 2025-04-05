package reservation

import com.hanghe.redis.movie.seat.SeatCode
import com.hanghe.redis.movie.seat.SeatEntity
import com.hanghe.redis.reservation.ReservationRepository
import com.hanghe.redis.screening.ScreeningRepository
import com.hanghe.redis.seat.SeatRepository
import com.hanghe.redis.reservation.MovieReservationService
import com.hanghe.redis.reservation.ReservationEntity
import com.hanghe.redis.reservation.UserReservationPolicy
import com.hanghe.redis.theater.TheaterEntity
import fake.FakeRateLimiter
import fake.RateLimitExceededException
import fixture.ScreeningEntityFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.springframework.context.ApplicationEventPublisher
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class MovieReservationServiceTest : BehaviorSpec({

    val reservationRepository = mockk<ReservationRepository>(relaxed = true)
    val seatRepository = mockk<SeatRepository>(relaxed = true)
    val screeningRepository = mockk<ScreeningRepository>(relaxed = true)
    val eventPublisher = mockk<ApplicationEventPublisher>(relaxed = true)
    val reservationPolicy = mockk<UserReservationPolicy>(relaxed = true)
    val fakeRateLimiter = FakeRateLimiter()

    val service = MovieReservationService(
        reservationRepository,
        seatRepository,
        screeningRepository,
        eventPublisher,
        reservationPolicy,
        fakeRateLimiter
    )

    val screeningId = 1L
    val seatIds = listOf(1L)
    val userId1 = "userA"
    val userId2 = "userB"

    val theater = TheaterEntity(name = "CGV")
    val seat = SeatEntity(1L, SeatCode("A1"), theater)
    val screening = ScreeningEntityFixture.create()

    lateinit var reserved: AtomicBoolean

    beforeTest {
        reserved = AtomicBoolean(false)
    }

    fun mockBasicSetup() {
        every { reservationRepository.findByScreeningId(screeningId) } returns emptyList()
        every { seatRepository.findByScreeningIdAndSeatIds(screeningId, seatIds) } returns listOf(seat)
        every { screeningRepository.findById(screeningId) } returns screening
        every { screeningRepository.getById(screeningId) } returns screening
        every { reservationPolicy.validate(any(), any(), any()) } just Runs
        every { reservationRepository.saveAll(any()) } returnsArgument 0
        every { seatRepository.findByTheaterIdAndSeatIds(screeningId, seatIds) } returns listOf(seat)
    }

    fun runConcurrentTest(task: (String) -> Unit): Int {
        val exceptionCount = AtomicInteger()
        val executor = Executors.newFixedThreadPool(2)
        val latch = CountDownLatch(2)

        listOf(userId1, userId2).forEach { userId ->
            executor.submit {
                try {
                    task(userId)
                } catch (e: Throwable) {
                    exceptionCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()

        executor.shutdown()

        return exceptionCount.get()
    }

    given("MovieReservationService.reservation() 동시성 테스트") {
        `when`("락이 없는 상태에서 두 사용자가 동시에 예매를 시도하면") {
            then("예외가 발생하지 않고 중복 예매에 성공한다") {
                mockBasicSetup()

                val exceptionCount = runConcurrentTest { userId ->
                    service.reservation(screeningId, userId, seatIds)
                }

                exceptionCount shouldBe 0
            }
        }
    }

    given("MovieReservationService.reservationWithPessimisticLock() 동시성 테스트") {
        `when`("두 사용자가 동일한 좌석을 동시에 예약하려고 시도하면") {
            then("한 명은 예약에 성공하고 다른 한 명은 ConcurrentModificationException 예외가 발생한다") {
                mockBasicSetup()

                val reservation = ReservationEntity(1L, screening, seat)
                every {
                    reservationRepository.findByScreeningIdAndSeatIdsWithPessimisticLock(screeningId, seatIds)
                } answers {
                    if (reserved.compareAndSet(false, true)) {
                        emptyList()
                    } else {
                        listOf(reservation)
                    }
                }

                val exceptionCount = runConcurrentTest { userId ->
                    service.reservationWithPessimisticLock(screeningId, userId, seatIds)
                }

                exceptionCount shouldBe 1
            }
        }
    }

    given("RateLimiter Test") {
        `when`("상영 영화에 대해 유저가 이미 예약을 한 경우") {
            then("RateLimitExceedException 예외를 던진다") {
                fakeRateLimiter.setReserved(screeningId, userId1)

                shouldThrow<RateLimitExceededException> {
                    service.reservationWithPessimisticLock(screeningId, userId1, seatIds)
                }.message shouldBe "같은 시간대의 영화는 5분에 최대 1회 예약이 가능합니다. 잠시 후 시도해주세요."
            }
        }
    }
})
