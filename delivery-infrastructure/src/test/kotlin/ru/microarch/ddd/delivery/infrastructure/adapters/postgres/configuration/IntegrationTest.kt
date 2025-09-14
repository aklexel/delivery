package ru.microarch.ddd.delivery.infrastructure.adapters.postgres.configuration

import org.springframework.context.annotation.Import
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.extension.PostgresTest
import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited

@Import(TestConfiguration::class)
@PostgresTest
annotation class IntegrationTest
