plugins {
    id("java-test-fixtures")
}

dependencies {
    val r2dbcMigrateVersion: String by project
    val testcontainersPostgresqlVersion: String by project

    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("name.nkonev.r2dbc-migrate:r2dbc-migrate-spring-boot-starter:$r2dbcMigrateVersion")
    implementation("org.postgresql:r2dbc-postgresql")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation(project(":delivery-core"))
    implementation(project(":utils"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(testFixtures(project(":delivery-infrastructure")))

    testFixturesApi("org.testcontainers:postgresql:$testcontainersPostgresqlVersion")
    testFixturesImplementation("org.springframework.boot:spring-boot-starter-test")
    testFixturesImplementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    testFixturesImplementation("name.nkonev.r2dbc-migrate:r2dbc-migrate-spring-boot-starter:$r2dbcMigrateVersion")
    testFixturesImplementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))
}

tasks.bootJar {
    enabled = false
}
