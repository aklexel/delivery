plugins {
	id("org.springframework.boot")
	id("io.spring.dependency-management")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	implementation(project(":delivery-core"))
	implementation(project(":delivery-infrastructure"))

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}
