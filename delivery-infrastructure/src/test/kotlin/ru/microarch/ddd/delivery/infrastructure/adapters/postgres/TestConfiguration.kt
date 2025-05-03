package ru.microarch.ddd.delivery.infrastructure.adapters.postgres

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.transaction.ReactiveTransactionManager
import ru.microarch.ddd.delivery.core.ports.CourierRepository
import ru.microarch.ddd.delivery.core.ports.OrderRepository
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.repository.*
import ru.microarch.ddd.utils.UnitOfWork

@Configuration
@EnableR2dbcRepositories
class TestConfiguration {

    @Autowired
    lateinit var orderDbRepository: OrderDbRepository

    @Autowired
    lateinit var courierDbRepository: CourierDbRepository

    @Autowired
    lateinit var transportDbRepository: TransportDbRepository

    @Autowired
    lateinit var transactionManager: ReactiveTransactionManager

    @Bean
    fun createOrderRepository(): OrderRepository {
        return OrderRepositoryImpl(orderDbRepository)
    }

    @Bean
    fun createCourierRepository(): CourierRepository {
        return CourierRepositoryImpl(courierDbRepository, transportDbRepository)
    }

    @Bean
    fun createUnitOfWork(): UnitOfWork {
        return UnitOfWorkImpl(transactionManager)
    }

}
