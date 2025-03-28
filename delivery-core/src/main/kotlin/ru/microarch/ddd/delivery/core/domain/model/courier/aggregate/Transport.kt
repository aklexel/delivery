package ru.microarch.ddd.delivery.core.domain.model.courier.aggregate

import ru.microarch.ddd.delivery.core.domain.model.shared.kernel.Location
import java.util.*
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

class Transport(name: String, speed: Int) {
    private val id: UUID = UUID.randomUUID()

    var name: String = name
        set(value) {
            validateName(value)
            field = value
        }

    var speed: Int = speed
        set(value) {
            validateSpeed(value)
            field = value
        }

    init {
        validateName(name)
        validateSpeed(speed)
    }

    private fun validateName(name: String) {
        require(name.isNotBlank(), { "name must not be blank" })
    }

    private fun validateSpeed(speed: Int) {
        require(speed in MIN_SPEED..MAX_SPEED) {
            "speed must be between $MIN_SPEED and $MAX_SPEED"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (this.javaClass != other?.javaClass) return false

        other as Transport

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Transport(id=$id, name='$name', speed=$speed)"
    }

    fun move(current: Location, target: Location): Location {
        if (current.distance(target) <= speed) return target

        val diffX = target.x - current.x
        val diffY = target.y - current.y

        val deltaX = diffX.sign * min(speed, abs(diffX))
        val deltaY = diffY.sign * min(speed - abs(deltaX), abs(diffY))

        return Location(current.x + deltaX, current.y + deltaY)
    }

    companion object {
        const val MIN_SPEED = 1
        const val MAX_SPEED = 3
    }
}
