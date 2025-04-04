package com.hanghe.redis.movie

import com.hanghe.redis.movie.response.GetMovieScreeningResponses
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/movies")
class MovieControllerV1(
    private val service: MovieService
) {

    @GetMapping
    fun getAllScreeningMovies(
        request: HttpServletRequest,
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) genre: String?
    ): ResponseEntity<GetMovieScreeningResponses> {
        val ip = request.remoteAddr

        return ResponseEntity.ok(
            service.getAllScreeningMovies(
                title, genre, ip
            )
        )
    }
}
