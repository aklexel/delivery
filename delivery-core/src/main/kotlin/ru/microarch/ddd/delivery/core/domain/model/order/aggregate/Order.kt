package ru.microarch.ddd.delivery.core.domain.model.order.aggregate

import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.Courier
import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.CourierStatus
import ru.microarch.ddd.delivery.core.domain.model.shared.kernel.Location
import ru.microarch.ddd.utils.Entity
import java.util.*

class Order(override val id: UUID, location: Location) : Entity<UUID>() {

    var location: Location = location
        private set

    var status: OrderStatus = OrderStatus.CREATED
        private set

    var courierId: UUID? = null
        private set

    fun assign(courier: Courier) {
        if (status != OrderStatus.CREATED)
            throw AssignNotCreatedOrderException(this)

        if (courier.status != CourierStatus.FREE)
            throw AssignOrderToNotFreeCourierException(this, courier)

        courier.setBusy()
        status = OrderStatus.ASSIGNED
        courierId = courier.id
    }

    fun complete() {
        if (status != OrderStatus.ASSIGNED)
            throw CompleteNotAssignedOrderException(this)

        status = OrderStatus.COMPLETED
    }

    class AssignOrderToNotFreeCourierException(order: Order, courier: Courier) :
        RuntimeException("Order <${order.id}> cannot be assigned to courier <${courier.id}> with status <${courier.status}>")

    class AssignNotCreatedOrderException(order: Order) :
        RuntimeException("Order <${order.id}> with status <${order.status}> cannot be assigned")

    class CompleteNotAssignedOrderException(order: Order) :
        RuntimeException("Order <${order.id}> with status <${order.status}> cannot be completed")
}
