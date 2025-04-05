import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

allprojects {
    group = "com.hanghe.redis"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("kotlin-kapt")
        plugin("io.spring.dependency-management")
        plugin("kotlin-spring")
        apply(plugin = "jacoco")
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    }

    jacoco {
        toolVersion = "0.8.11"
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test)
        reports {
            html.required = true
            xml.required = true
            csv.required = false
        }

        val qDomainList = mutableListOf<String>()
        for (char in 'A'..'Z') {
            qDomainList.add("**/Q$char*")
        }

        classDirectories.setFrom(
            files(
                classDirectories.files.map {
                    fileTree(it) {
                        exclude(
                            "**/MovieGrade.class",
                            "**/*Entity.class",
                            "**/*Request.class",
                            "**/*Response.class",
                            "**/BaseEntity.class",
                            "**/config/**",
                            "**/DataInitializer.class",
                            *qDomainList.toTypedArray()
                        )
                    }
                }
            )
        )
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        finalizedBy(tasks.jacocoTestReport)
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
                jvmTarget = "21"
            }
        }
    }
}
