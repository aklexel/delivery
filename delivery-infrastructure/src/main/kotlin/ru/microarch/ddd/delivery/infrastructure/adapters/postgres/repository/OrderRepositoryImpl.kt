package ru.microarch.ddd.delivery.infrastructure.adapters.postgres.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.microarch.ddd.delivery.core.domain.model.order.aggregate.Order
import ru.microarch.ddd.delivery.core.domain.model.order.aggregate.OrderStatus
import ru.microarch.ddd.delivery.core.ports.OrderRepository
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.entity.OrderEntity
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.mapper.toOrder
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.mapper.toOrderEntity
import java.util.*

@Repository("postgres-order-adapter")
class OrderRepositoryImpl(private val repository: OrderDbRepository) : OrderRepository {
    override suspend fun add(order: Order): Order {
        return repository
            .save(order.toOrderEntity(isNew = true))
            .map(OrderEntity::toOrder)
            .awaitSingle()
    }

    override suspend fun update(order: Order): Order {
        return repository
            .save(order.toOrderEntity())
            .map(OrderEntity::toOrder)
            .awaitSingle()
    }

    override suspend fun get(orderId: UUID): Order? {
        return repository
            .findById(orderId)
            .map(OrderEntity::toOrder)
            .awaitSingleOrNull()
    }

    override suspend fun getAnyWithCreatedStatus(): Order? {
        return repository
            .findFirstByStatus(OrderStatus.CREATED.name)
            .map(OrderEntity::toOrder)
            .awaitSingleOrNull()
    }

    override suspend fun getAllWithAssignedStatus(): Flow<Order> {
        return repository
            .findAllByStatus(OrderStatus.ASSIGNED.name)
            .map(OrderEntity::toOrder)
            .asFlow()
    }

}

interface OrderDbRepository : R2dbcRepository<OrderEntity, UUID> {
    fun findAllByStatus(status: String): Flux<OrderEntity>
    fun findFirstByStatus(status: String): Mono<OrderEntity>
}
