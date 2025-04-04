package com.hanghe.redis.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiGlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(ApiGlobalExceptionHandler::class.java)

    @ExceptionHandler(Exception::class, RuntimeException::class)
    fun handleException(e: Exception): ProblemDetail {
        logger.error("An unexpected exception occurred: ${e.message}, class: ${e::class}")

        return toProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.message ?: "Unknown error")
    }

    @ExceptionHandler(RateLimitExceededException::class)
    fun handleRateLimitExceededException(e: RateLimitExceededException): ProblemDetail {
        logger.error("Too many requests: ${e.message}")

        return toProblemDetail(HttpStatus.TOO_MANY_REQUESTS, e.message ?: "Unknown error")
    }

    private fun toProblemDetail(status: HttpStatus, message: String): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(status, message)
    }
}
