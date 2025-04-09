package ru.microarch.ddd.delivery.core.domain.service

import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.Courier
import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.CourierStatus
import ru.microarch.ddd.delivery.core.domain.model.order.aggregate.Order

class DispatchServiceImpl : DispatchService {

    override fun dispatch(order: Order, couriers: List<Courier>): Courier {
        val optimalCandidate = couriers
            .filter { it.status == CourierStatus.FREE }
            .minByOrNull { it.calculateTimeToLocation(order.location) }
            ?: throw DispatchService.CourierNotFoundException(order.id)

        order.assign(optimalCandidate)
        return optimalCandidate
    }

}
