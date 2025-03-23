dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.4.3")

    implementation(project(":domain"))
    implementation(project(":infrastructure"))

    // kotest
    testImplementation("io.kotest:kotest-runner-junit5:5.3.2")
    testImplementation("io.kotest:kotest-assertions-core:5.3.2")
    testImplementation("io.kotest:kotest-framework-api:5.3.2")
    testImplementation("io.mockk:mockk:1.13.5")
}
