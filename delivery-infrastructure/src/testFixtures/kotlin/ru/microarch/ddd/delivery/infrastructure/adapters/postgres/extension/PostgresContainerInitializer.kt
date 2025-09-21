package ru.microarch.ddd.delivery.infrastructure.adapters.postgres.extension

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer

class PostgresContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object {
        private const val DOCKER_IMAGE: String = "postgres:17"
        private const val SPRING_R2DBC_CONFIG_PREFIX: String = "spring.r2dbc"
        private const val R2DBC_URL: String = "$SPRING_R2DBC_CONFIG_PREFIX.url"
        private const val R2DBC_USERNAME: String = "$SPRING_R2DBC_CONFIG_PREFIX.username"
        private const val R2DBC_PASSWORD: String = "$SPRING_R2DBC_CONFIG_PREFIX.password"
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val postgresContainer = PostgreSQLContainer(DOCKER_IMAGE)
            .apply {
                start()
                System.setProperty(R2DBC_URL, r2dbcUrl())
                System.setProperty(R2DBC_USERNAME, username)
                System.setProperty(R2DBC_PASSWORD, password)
            }

        applicationContext.beanFactory.registerSingleton(postgresContainer::class.simpleName!!, postgresContainer)
    }

    private fun PostgreSQLContainer<*>.r2dbcUrl(): String {
        return "r2dbc:postgresql://${host}:${getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)}/${databaseName}"
    }

}
