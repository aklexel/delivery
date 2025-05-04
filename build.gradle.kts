plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "2.1.20"
    id("org.springframework.boot") version "3.4.4" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "ru.microarch.ddd"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.spring")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
    }

    kotlin {
        jvmToolchain(17)
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }
}
