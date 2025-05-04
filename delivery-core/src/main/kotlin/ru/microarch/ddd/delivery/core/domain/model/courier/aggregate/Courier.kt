package ru.microarch.ddd.delivery.core.domain.model.courier.aggregate

import ru.microarch.ddd.delivery.core.domain.model.shared.kernel.Location
import ru.microarch.ddd.utils.Entity
import java.util.*
import kotlin.math.ceil

class Courier private constructor(
    override val id: UUID,
    name: String,
    transport: Transport,
    location: Location,
    status: CourierStatus
) : Entity<UUID>() {

    constructor(name: String, transportName: String, speed: Int, location: Location) : this(
        UUID.randomUUID(),
        name,
        Transport(transportName, speed),
        location,
        CourierStatus.FREE
    )

    init {
        require(name.isNotBlank()) { "Name must not be blank" }
    }

    var name = name
        private set

    var transport: Transport = transport
        private set

    var location: Location = location
        private set

    var status: CourierStatus = status
        private set

    fun setBusy() {
        if (status == CourierStatus.BUSY)
            throw SetBusyStatusToBusyCourierException(id)

        status = CourierStatus.BUSY
    }

    fun setFree() {
        if (status == CourierStatus.FREE)
            throw SetFreeStatusToFreeCourierException(id)

        status = CourierStatus.FREE
    }

    fun calculateTimeToLocation(target: Location): Int {
        val distance = location.distance(target).toDouble() / transport.speed
        return ceil(distance).toInt()
    }

    fun move(target: Location) {
        location = transport.move(location, target)
    }

    override fun toString(): String {
        return "Courier(id=$id, name=$name, transport=$transport, location=$location, status=$status)"
    }

    companion object {
        fun from(id: UUID, name: String, transport: Transport, location: Location, status: CourierStatus): Courier {
            return Courier(id, name, transport, location, status)
        }
    }

    class SetBusyStatusToBusyCourierException(id: UUID) :
        RuntimeException("Unable to set busy status for courier <$id> who is already busy")

    class SetFreeStatusToFreeCourierException(id: UUID) :
        RuntimeException("Unable to set free status for courier <$id> who is already free")

}
