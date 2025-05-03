package ru.microarch.ddd.delivery.infrastructure.adapters.postgres.mapper

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.Courier
import ru.microarch.ddd.delivery.core.domain.model.order.aggregate.Order
import ru.microarch.ddd.delivery.core.domain.model.shared.kernel.Location
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.entity.OrderEntity
import ru.microarch.ddd.utils.toUUID
import java.util.*
import kotlin.test.*

class OrderMapperShould {

    @Test
    fun `map new order to order entity`() {
        val order = Order(UUID.randomUUID(), Location(5, 5))

        val entity = order.toOrderEntity(isNew = true)

        assertEquals(order.id, entity.id)
        assertEquals(order.courierId, entity.courierId)
        assertEquals(order.location.x, entity.locationX)
        assertEquals(order.location.y, entity.locationY)
        assertEquals(order.status.name, entity.status)
        assertTrue(entity.isNew)
    }

    @Test
    fun `map assigned order to order entity`() {
        val order = Order(UUID.randomUUID(), Location(5, 5))
        val courier = Courier("John", "bike", 2, Location(3, 4))
        order.assign(courier)

        val entity = order.toOrderEntity()

        assertEquals(order.id, entity.id)
        assertEquals(order.courierId, entity.courierId)
        assertEquals(order.location.x, entity.locationX)
        assertEquals(order.location.y, entity.locationY)
        assertEquals(order.status.name, entity.status)
        assertFalse(entity.isNew)
    }

    @ParameterizedTest
    @CsvSource(
        "fe60a3c7-1618-4c90-b962-be2520abcdef, , 2,3, CREATED",
        "fe60a3c7-1618-4c90-b962-be2520abcdef, , 3,4, COMPLETED",
        "fe60a3c7-1618-4c90-b962-be2520abcdef, 0101ebf5-e93c-4c76-a349-8870e12b887e, 5,6, ASSIGNED",
    )
    fun `map entity to order`(id: String, courierId: String?, locationX: Int, locationY: Int, status: String) {
        val entity = OrderEntity(id.toUUID(), courierId?.toUUID(), locationX, locationY, status)

        val order = entity.toOrder()

        assertEquals(entity.id, order.id)
        assertEquals(entity.courierId, order.courierId)
        assertEquals(entity.locationX, order.location.x)
        assertEquals(entity.locationY, order.location.y)
        assertEquals(entity.status, order.status.name)
    }

}
