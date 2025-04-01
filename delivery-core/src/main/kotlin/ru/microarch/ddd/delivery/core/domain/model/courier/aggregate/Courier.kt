package ru.microarch.ddd.delivery.core.domain.model.courier.aggregate

import ru.microarch.ddd.delivery.core.domain.model.shared.kernel.Location
import ru.microarch.ddd.utils.Entity
import java.util.*
import kotlin.math.ceil

class Courier(name: String, transportName: String, speed: Int, location: Location) : Entity<UUID>() {

    override val id: UUID = UUID.randomUUID()

    init {
        require(name.isNotBlank()) { "Name must not be blank" }
    }

    var name = name
        private set

    var transport: Transport = Transport(transportName, speed)
        private set

    var location: Location = location
        private set

    var status: CourierStatus = CourierStatus.FREE
        set(value) {
            if (value == CourierStatus.BUSY && field == value)
                throw SetBusyStatusToBusyCourierException(id)

            if (value == CourierStatus.FREE && field == value)
                throw SetFreeStatusToFreeCourierException(id)

            field = value
        }

    fun calculateTimeToLocation(target: Location): Int {
        val distance = location.distance(target).toDouble() / transport.speed
        return ceil(distance).toInt()
    }

    fun move(target: Location) {
        location = transport.move(location, target)
    }

    class SetBusyStatusToBusyCourierException(id: UUID) :
        RuntimeException("Unable to set busy status for courier <$id> who is already busy")

    class SetFreeStatusToFreeCourierException(id: UUID) :
        RuntimeException("Unable to set free status for courier <$id> who is already free")

}
