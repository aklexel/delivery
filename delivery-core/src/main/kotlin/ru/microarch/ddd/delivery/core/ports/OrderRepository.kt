package ru.microarch.ddd.delivery.core.ports

import ru.microarch.ddd.delivery.core.domain.model.order.aggregate.Order
import java.util.UUID
import kotlinx.coroutines.flow.Flow

/**
 * Repository of Order Aggregate
 */
interface OrderRepository {
    /**
     * Добавить заказ
     */
    suspend fun add(order: Order): Order

    /**
     * Обновить заказ
     */
    suspend fun update(order: Order): Order

    /**
     * Получить заказ по идентификатору
     */
    suspend fun get(orderId: UUID): Order?

    /**
     * Получить 1 любой заказ со статусом "CREATED"
     */
    suspend fun getAnyWithCreatedStatus(): Order?

    /**
     * Получить все назначенные заказы (заказы со статусом "ASSIGNED")
     */
    suspend fun getAllWithAssignedStatus(): Flow<Order>
}
