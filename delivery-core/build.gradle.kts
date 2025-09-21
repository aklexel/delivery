dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    implementation(project(":utils"))

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
