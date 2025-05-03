dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	implementation(project(":utils"))
	implementation(project(":delivery-core"))
	implementation(project(":delivery-infrastructure"))

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}
