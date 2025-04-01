package ru.microarch.ddd.delivery.core.domain.model.order.aggregate

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.Courier
import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.CourierStatus
import ru.microarch.ddd.delivery.core.domain.model.shared.kernel.Location
import java.util.*

class OrderShould {

    @Test
    fun `allow to create instance`() {
        val id = UUID.randomUUID()
        val location = Location.random()
        val order = Order(id, location)

        assertEquals(id, order.id)
        assertEquals(location, order.location)
        assertEquals(OrderStatus.CREATED, order.status)
        assertEquals(null, order.courierId)
    }

    @Test
    fun `allow to assign to courier`() {
        val order = Order(UUID.randomUUID(), Location.random())
        val courier = Courier("John", "bike", 2, Location.random())

        order.assign(courier)

        assertEquals(courier.id, order.courierId)
        assertEquals(OrderStatus.ASSIGNED, order.status)
        assertEquals(CourierStatus.BUSY, courier.status)
    }

    @Test
    fun `not allow to assign to busy courier`() {
        val order = Order(UUID.randomUUID(), Location.random())
        val courier = Courier("John", "bike", 2, Location.random())

        courier.setBusy()

        assertThrows<Order.AssignOrderToNotFreeCourierException> {
            order.assign(courier)
        }
    }

    @Test
    fun `not allow to assign when order is already assigned`() {
        val order = Order(UUID.randomUUID(), Location.random())
        val courier = Courier("John", "bike", 2, Location.random())

        order.assign(courier)

        assertThrows<Order.AssignNotCreatedOrderException> {
            order.assign(courier)
        }
    }

    @Test
    fun `not allow to assign when order is completed`() {
        val order = Order(UUID.randomUUID(), Location.random())
        val courier = Courier("John", "bike", 2, Location.random())

        order.assign(courier)
        order.complete()

        assertThrows<Order.AssignNotCreatedOrderException> {
            order.assign(courier)
        }
    }

    @Test
    fun `allow to complete when order is assigned`() {
        val order = Order(UUID.randomUUID(), Location.random())
        val courier = Courier("John", "bike", 2, Location.random())

        order.assign(courier)
        order.complete()

        assertEquals(OrderStatus.COMPLETED, order.status)
    }

    @Test
    fun `not allow to complete twice`() {
        val order = Order(UUID.randomUUID(), Location.random())
        val courier = Courier("John", "bike", 2, Location.random())

        order.assign(courier)
        order.complete()

        assertThrows<Order.CompleteNotAssignedOrderException> {
            order.complete()
        }
    }

    @Test
    fun `not allow to complete when order is not assigned`() {
        val order = Order(UUID.randomUUID(), Location.random())

        assertThrows<Order.CompleteNotAssignedOrderException> {
            order.complete()
        }

        assertEquals(OrderStatus.CREATED, order.status)
    }

}
