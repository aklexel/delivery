package ru.microarch.ddd.delivery.infrastructure.adapters.postgres.extension

import name.nkonev.r2dbc.migrate.autoconfigure.R2dbcMigrateAutoConfiguration
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.test.context.ContextConfiguration
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.TestConfiguration
import java.lang.annotation.Inherited


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited

@ContextConfiguration(
    classes = [TestConfiguration::class, R2dbcAutoConfiguration::class, R2dbcMigrateAutoConfiguration::class],
    initializers = [PostgresContainerInitializer::class]
)
@ExtendWith(RunSqlExtension::class)
@DataR2dbcTest
annotation class PostgresTest
