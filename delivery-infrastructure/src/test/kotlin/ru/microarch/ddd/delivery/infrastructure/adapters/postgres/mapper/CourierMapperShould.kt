package ru.microarch.ddd.delivery.infrastructure.adapters.postgres.mapper

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.Courier
import ru.microarch.ddd.delivery.core.domain.model.order.aggregate.Order
import ru.microarch.ddd.delivery.core.domain.model.shared.kernel.Location
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.entity.CourierEntity
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.entity.TransportEntity
import ru.microarch.ddd.utils.toUUID
import java.util.UUID
import kotlin.test.*

class CourierMapperShould {

    @Test
    fun `map new courier to order and transport entities`() {
        val courier = Courier("John", "bike", 2, Location(3, 4))

        val (courierEntity, transportEntity) = courier.toCourierAndTransportEntities(isNew = true)

        assertEquals(courier.id, courierEntity.id)
        assertEquals(courier.name, courierEntity.name)
        assertEquals(courier.transport.id, courierEntity.transportId)
        assertEquals(courier.status.name, courierEntity.status)
        assertEquals(courier.location.x, courierEntity.locationX)
        assertEquals(courier.location.y, courierEntity.locationY)
        assertTrue(courierEntity.isNew)

        assertEquals(courier.transport.id, transportEntity.id)
        assertEquals(courier.transport.name, transportEntity.name)
        assertEquals(courier.transport.speed, transportEntity.speed)
        assertTrue(transportEntity.isNew)
    }

    @Test
    fun `map busy courier to order and transport entities`() {
        val courier = Courier("John", "bike", 2, Location(3, 4))
        val order = Order(UUID.randomUUID(), Location.random())
        order.assign(courier)

        val (courierEntity, transportEntity) = courier.toCourierAndTransportEntities()

        assertEquals(courier.id, courierEntity.id)
        assertEquals(courier.name, courierEntity.name)
        assertEquals(courier.transport.id, courierEntity.transportId)
        assertEquals(courier.status.name, courierEntity.status)
        assertEquals(courier.location.x, courierEntity.locationX)
        assertEquals(courier.location.y, courierEntity.locationY)
        assertFalse(courierEntity.isNew)

        assertEquals(courier.transport.id, transportEntity.id)
        assertEquals(courier.transport.name, transportEntity.name)
        assertEquals(courier.transport.speed, transportEntity.speed)
        assertFalse(transportEntity.isNew)
    }

    @ParameterizedTest
    @CsvSource(
        "fe60a3c7-1618-4c90-b962-be2520abcdef, John, FREE, 1,2, 0101ebf5-e93c-4c76-a349-8870e12b887e, bike, 2",
        "fe60a3c7-1618-4c90-b962-be2520abcdef, Tom, BUSY, 3,4, 0101ebf5-e93c-4c76-a349-8870e12b887e, car, 3",
    )
    fun `map courier and transport entities to courier`(
        id: String,
        name: String,
        status: String,
        locationX: Int,
        locationY: Int,
        transportId: String,
        transportName: String,
        transportSpeed: Int
    ) {
        val courierEntity = CourierEntity(id.toUUID(), name, transportId.toUUID(), status, locationX, locationY)
        val transportEntity = TransportEntity(transportId.toUUID(), transportName, transportSpeed)


        val courier = courierEntity.toCourier(transportEntity)
        assertEquals(courierEntity.id, courier.id)
        assertEquals(courierEntity.name, courier.name)
        assertEquals(courierEntity.locationX, courier.location.x)
        assertEquals(courierEntity.locationY, courier.location.y)
        assertEquals(courierEntity.status, courier.status.name)
        assertEquals(courierEntity.transportId, courier.transport.id)

        assertEquals(transportEntity.id, courier.transport.id)
        assertEquals(transportEntity.name, courier.transport.name)
        assertEquals(transportEntity.speed, courier.transport.speed)
    }

}
