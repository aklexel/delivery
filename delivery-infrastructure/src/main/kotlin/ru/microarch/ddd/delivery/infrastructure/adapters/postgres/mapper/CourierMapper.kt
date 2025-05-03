package ru.microarch.ddd.delivery.infrastructure.adapters.postgres.mapper

import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.Courier
import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.CourierStatus
import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.Transport
import ru.microarch.ddd.delivery.core.domain.model.shared.kernel.Location
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.entity.CourierEntity
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.entity.TransportEntity

fun Courier.toCourierAndTransportEntities(isNew: Boolean = false): Pair<CourierEntity, TransportEntity> {
    val courier = CourierEntity(
        id = id,
        name = name,
        status = status.name,
        locationX = location.x,
        locationY = location.y,
        transportId = transport.id,
    ).apply {
        this.isNew = isNew
    }

    val transport = TransportEntity(
        id = transport.id,
        name = transport.name,
        speed = transport.speed
    ).apply {
        this.isNew = isNew
    }

    return Pair(courier, transport)
}

fun CourierEntity.toCourier(transportEntity: TransportEntity): Courier {
    val transport = transportEntity.let {
        Transport.from(it.id, it.name, it.speed)
    }

    return Courier.from(id, name, transport, Location(locationX, locationY), CourierStatus.valueOf(status))
}
