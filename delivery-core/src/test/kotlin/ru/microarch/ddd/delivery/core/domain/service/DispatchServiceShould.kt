package ru.microarch.ddd.delivery.core.domain.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.Courier
import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.CourierStatus
import ru.microarch.ddd.delivery.core.domain.model.order.aggregate.Order
import ru.microarch.ddd.delivery.core.domain.model.order.aggregate.OrderStatus
import ru.microarch.ddd.delivery.core.domain.model.shared.kernel.Location
import java.util.*

class DispatchServiceShould {

    val dispatchService: DispatchService = DispatchServiceImpl()

    @Test
    fun `assign order to free courier`() {
        val order = Order(UUID.randomUUID(), Location(5, 5))
        val courier1 = Courier("Tom", "bike", 2, Location(1, 1))
        val courier2 = Courier("John", "bike", 2, Location(1, 1))

        courier1.setBusy()
        val courier = dispatchService.dispatch(order, listOf(courier1, courier2))

        assertEquals(courier2, courier)
        assertEquals(courier.id, order.courierId)
        assertEquals(OrderStatus.ASSIGNED, order.status)
        assertEquals(CourierStatus.BUSY, courier.status)
    }

    @Test
    fun `assign order to optimal courier`() {
        val order = Order(UUID.randomUUID(), Location(5, 5))
        val courier1 = Courier("Bill", "bike", 1, Location(3, 4)) // near but slow
        val courier2 = Courier("John", "bike", 2, Location(3, 3)) // optimal
        val courier3 = Courier("Mike", "bike", 3, Location(1, 1)) // fast but too far
        val courier4 = Courier("Tom", "bike", 3, Location(4, 4))  // near and fast but busy

        courier4.setBusy()
        val courier = dispatchService.dispatch(order, listOf(courier1, courier2, courier3, courier4))

        assertEquals(courier2, courier)
        assertEquals(courier.id, order.courierId)
        assertEquals(OrderStatus.ASSIGNED, order.status)
        assertEquals(CourierStatus.BUSY, courier.status)
    }

    @Test
    fun `throw CourierNotFoundException when all couriers are busy`() {
        val order = Order(UUID.randomUUID(), Location(5, 5))
        val courier1 = Courier("Tom", "bike", 1, Location(1, 1))
        val courier2 = Courier("John", "bike", 2, Location(2, 3))

        courier1.setBusy()
        courier2.setBusy()

        assertThrows<DispatchService.CourierNotFoundException> {
            dispatchService.dispatch(order, listOf(courier1, courier2))
        }
    }

    @Test
    fun `throw CourierNotFoundException when couriers list is empty`() {
        val order = Order(UUID.randomUUID(), Location(5, 5))

        assertThrows<DispatchService.CourierNotFoundException> {
            dispatchService.dispatch(order, emptyList())
        }
    }

}
