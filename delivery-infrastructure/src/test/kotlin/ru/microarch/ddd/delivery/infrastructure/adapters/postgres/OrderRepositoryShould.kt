package ru.microarch.ddd.delivery.infrastructure.adapters.postgres

import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ru.microarch.ddd.delivery.core.domain.model.order.aggregate.Order
import ru.microarch.ddd.delivery.core.domain.model.order.aggregate.OrderStatus
import ru.microarch.ddd.delivery.core.domain.model.shared.kernel.Location
import ru.microarch.ddd.delivery.core.ports.OrderRepository
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.extension.PostgresTest
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.extension.RunSql
import ru.microarch.ddd.utils.toUUID
import java.util.UUID
import kotlin.test.*

@PostgresTest
class OrderRepositoryShould {

    @Autowired
    lateinit var repository: OrderRepository

    @Test
    @RunSql(
        "delete from orders",
        """insert into 
            orders (id, courier_id, location_x, location_y, status) 
            values ('17cb3248-da92-4336-a42f-6ce850849d0c', NULL, 5, 6, 'COMPLETED')"""
    )
    fun `get order by id`() = runTest {
        val id = "17cb3248-da92-4336-a42f-6ce850849d0c".toUUID()

        val order = repository.get(id)

        assertNotNull(order)
        assertEquals(id, order.id)
        assertNull(order.courierId)
        assertEquals(Location(5, 6), order.location)
        assertEquals(OrderStatus.COMPLETED, order.status)
    }

    @Test
    @RunSql(
        "delete from orders",
        """insert into
            orders (id, courier_id, location_x, location_y, status) 
            values 
                ('17cb3248-da92-4336-a42f-6ce850849d0c', NULL, 5, 6, 'CREATED'),
                ('da7196de-77ef-422a-9ee6-0c195336bfe0', NULL, 7, 8, 'ASSIGNED'),
                ('bad11eda-0c9a-4727-afa7-6a465ad0515a', NULL, 3, 4, 'COMPLETED')"""
    )
    fun `get order with created status`() = runTest {
        val order = repository.getAnyWithCreatedStatus()

        assertNotNull(order)
        assertEquals("17cb3248-da92-4336-a42f-6ce850849d0c".toUUID(), order.id)
        assertNull(order.courierId)
        assertEquals(Location(5, 6), order.location)
        assertEquals(OrderStatus.CREATED, order.status)
    }

    @Test
    @RunSql(
        "delete from orders",
        """insert into 
            orders (id, courier_id, location_x, location_y, status) 
            values 
                ('17cb3248-da92-4336-a42f-6ce850849d0c', NULL, 5, 6, 'CREATED'),
                ('da7196de-77ef-422a-9ee6-0c195336bfe0', NULL, 7, 8, 'ASSIGNED'),
                ('bad11eda-0c9a-4727-afa7-6a465ad0515a', NULL, 3, 4, 'COMPLETED'),
                ('5b1335c3-1bb1-4456-bf6d-08726297abcd', NULL, 1, 2, 'ASSIGNED')"""
    )
    fun `get all orders with assigned status`() = runTest {
        val assignedOrderIds = setOf("5b1335c3-1bb1-4456-bf6d-08726297abcd", "da7196de-77ef-422a-9ee6-0c195336bfe0")

        val orders = repository.getAllWithAssignedStatus()

        assertEquals(2, orders.count())
        assertEquals(assignedOrderIds, orders.map { it.id.toString() }.toSet())
        orders.collect {
            assertEquals(OrderStatus.ASSIGNED, it.status)
        }
    }

    @Test
    fun `add order`() = runTest {
        val order = Order(UUID.randomUUID(), Location.random())

        val savedOrder = repository.add(order)
        val dbOrder = repository.get(order.id)

        assertOrderEquals(order, savedOrder)
        assertOrderEquals(order, dbOrder)
    }

    @Test
    @RunSql(
        """insert into 
            transports (id, name, speed) 
            values ('83e9ba1b-0781-4f44-a87e-242131a0b4f3', 'bike', 2)""",
        """insert into 
            couriers (id, name, transport_id, location_x, location_y, status) 
            values ('a94595ce-e68d-4d7a-a561-2bfc198e4120', 'John', '83e9ba1b-0781-4f44-a87e-242131a0b4f3', 5, 6, 'BUSY')""",
        """insert into
            orders (id, courier_id, location_x, location_y, status) 
            values 
                ('08fc4146-ad10-4ce1-91a0-db0c187f52e1', 'a94595ce-e68d-4d7a-a561-2bfc198e4120', 5, 6, 'ASSIGNED'),
                ('08fc4146-ad10-4ce1-91a0-db0c187f52e2', NULL, 3, 4, 'CREATED')"""
    )
    fun `update order`() = runTest {
        val id = "08fc4146-ad10-4ce1-91a0-db0c187f52e1".toUUID()
        val order = repository.get(id)

        order!!.complete()
        val updatedOrder = repository.update(order)

        assertNotNull(updatedOrder)
        assertEquals(id, updatedOrder.id)
        assertEquals(OrderStatus.COMPLETED, updatedOrder.status)

        val dbOrder = repository.get(id)
        assertOrderEquals(updatedOrder, dbOrder)
    }

    companion object {
        fun assertOrderEquals(expected: Order, actual: Order?) {
            assertNotNull(actual)
            assertEquals(expected.id, actual.id)
            assertEquals(expected.courierId, actual.courierId)
            assertEquals(expected.location, actual.location)
            assertEquals(expected.status, actual.status)
        }
    }

}
