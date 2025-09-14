package ru.microarch.ddd.delivery

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.extension.PostgresContainerInitializer

@SpringBootTest
@ContextConfiguration(initializers = [PostgresContainerInitializer::class])
class DeliveryApplicationShould {

    @Test
    fun startup() {
    }

}
