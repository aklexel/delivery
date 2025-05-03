dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("name.nkonev.r2dbc-migrate:r2dbc-migrate-spring-boot-starter:3.2.0")
    implementation("org.postgresql:r2dbc-postgresql")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation(project(":delivery-core"))
    implementation(project(":utils"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.testcontainers:postgresql:1.21.0")
}
