package ru.microarch.ddd.delivery.infrastructure.adapters.postgres.mapper

import ru.microarch.ddd.delivery.core.domain.model.order.aggregate.Order
import ru.microarch.ddd.delivery.core.domain.model.order.aggregate.OrderStatus
import ru.microarch.ddd.delivery.core.domain.model.shared.kernel.Location
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.entity.OrderEntity

fun Order.toOrderEntity(isNew: Boolean = false): OrderEntity {
    return OrderEntity(
        id = id,
        courierId = courierId,
        locationX = location.x,
        locationY = location.y,
        status = status.name
    ).apply {
        this.isNew = isNew
    }
}

fun OrderEntity.toOrder(): Order =
    Order.from(id, Location(locationX, locationY), OrderStatus.valueOf(status), courierId)
