package ru.microarch.ddd.delivery.core.ports

import kotlinx.coroutines.flow.Flow
import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.Courier
import java.util.UUID

/**
 * Repository of Courier Aggregate
 */
interface CourierRepository {
    /**
     * Добавить курьера
     */
    suspend fun add(courier: Courier): Courier

    /**
     * Обновить курьера
     */
    suspend fun update(courier: Courier): Courier

    /**
     * Получить курьера по идентификатору
     */
    suspend fun get(courierId: UUID): Courier?

    /**
     * Получить всех свободных курьеров (курьеры со статусом "FREE")
     */
    suspend fun getAllWithFreeStatus(): Flow<Courier>
}
