plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "redis_2nd"

include("api")
include("common")
include("domain")
include("infrastructure")
include("application")
include("infrastructure:mysql")
include("infrastructure:message")
include("infrastructure:cache")
include("infrastructure:ratelimiter")
