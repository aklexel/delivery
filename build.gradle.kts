plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot") apply false
}

allprojects {
    group = "ru.microarch.ddd"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        jvmArgs("-Xshare:off")
    }
}

val springBootProjects = listOf(
    "delivery-api",
    "delivery-infrastructure",
)

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")

        if (project.name in springBootProjects) {
            plugin("org.springframework.boot")
            plugin("org.jetbrains.kotlin.plugin.spring")
        }
    }

    dependencies {
        implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))
    }

    kotlin {
        jvmToolchain(21)
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }
}
