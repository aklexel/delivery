package ru.microarch.ddd.delivery.core.domain.service

import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.Courier
import ru.microarch.ddd.delivery.core.domain.model.order.aggregate.Order
import java.util.*

interface DispatchService {

    fun dispatch(order: Order, couriers: List<Courier>): Courier

    class CourierNotFoundException(orderId: UUID) :
        RuntimeException("Courier for order <$orderId> was not found")

}
