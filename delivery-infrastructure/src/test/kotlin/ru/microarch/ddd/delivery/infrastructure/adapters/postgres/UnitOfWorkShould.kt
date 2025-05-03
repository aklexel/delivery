package ru.microarch.ddd.delivery.infrastructure.adapters.postgres

import kotlinx.coroutines.test.runTest
import org.springframework.beans.factory.annotation.Autowired
import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.Courier
import ru.microarch.ddd.delivery.core.domain.model.order.aggregate.Order
import ru.microarch.ddd.delivery.core.domain.model.order.aggregate.OrderStatus
import ru.microarch.ddd.delivery.core.domain.model.shared.kernel.Location
import ru.microarch.ddd.delivery.core.ports.CourierRepository
import ru.microarch.ddd.delivery.core.ports.OrderRepository
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.CourierRepositoryShould.Companion.assertCourierEquals
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.OrderRepositoryShould.Companion.assertOrderEquals
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.extension.PostgresTest
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.extension.RunSql
import ru.microarch.ddd.utils.UnitOfWork
import ru.microarch.ddd.utils.toUUID
import java.lang.RuntimeException
import java.util.UUID
import kotlin.test.*

@PostgresTest
class UnitOfWorkShould {

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var courierRepository: CourierRepository

    @Autowired
    lateinit var unitOfWork: UnitOfWork

    @Test
    fun `commit all changes`() = runTest {
        val courier = Courier("John", "bike", 2, Location.random())
        val order = Order(UUID.randomUUID(), Location.random())

        unitOfWork.commitChanges {
            orderRepository.add(order)
            courierRepository.add(courier)

            order.assign(courier)

            orderRepository.update(order)
            courierRepository.update(courier)
        }

        val dbCourier = courierRepository.get(courier.id)
        val dbOrder = orderRepository.get(order.id)
        assertCourierEquals(courier, dbCourier)
        assertOrderEquals(order, dbOrder)
    }

    @Test
    @RunSql(
        """insert into 
            orders (id, courier_id, location_x, location_y, status) 
            values ('0101ebf5-e93c-4c76-a349-8870e12b887e', NULL, 5, 6, 'CREATED')"""
    )
    fun `rollback all changes when error occurs`() = runTest {
        val courier = Courier("John", "bike", 2, Location.random())
        val order = orderRepository.get("0101ebf5-e93c-4c76-a349-8870e12b887e".toUUID())
        assertNotNull(order)

        try {
            unitOfWork.commitChanges {
                courierRepository.add(courier)

                order.assign(courier)

                orderRepository.update(order)
                courierRepository.update(courier)

                throw RuntimeException("Some error")
            }
        } catch (_: RuntimeException) {
            // do nothing
        }

        val dbOrder = orderRepository.get(order.id)
        assertNotNull(dbOrder)
        assertNull(dbOrder.courierId)
        assertEquals(OrderStatus.CREATED, dbOrder.status)

        val dbCourier = courierRepository.get(courier.id)
        assertNull(dbCourier)
    }

}
