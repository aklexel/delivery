rootProject.name = "delivery"

include("delivery-api")
include("delivery-core")
include("delivery-infrastructure")
include("utils")

pluginManagement {
    val kotlinVersion: String by settings
    val springBootVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion apply false
        kotlin("plugin.spring") version kotlinVersion apply false
        id("org.springframework.boot") version springBootVersion apply false
    }
}
