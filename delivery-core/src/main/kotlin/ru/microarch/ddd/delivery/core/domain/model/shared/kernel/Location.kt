package ru.microarch.ddd.delivery.core.domain.model.shared.kernel

import kotlin.math.abs
import kotlin.random.Random

data class Location(val x: Int, val y: Int) {

    init {
        require(x >= MIN_X) { "x must be greater or equal to $MIN_X" }
        require(y >= MIN_Y) { "y must be greater or equal to $MIN_Y" }
        require(x <= MAX_X) { "x must be less than or equal to $MAX_X" }
        require(y <= MAX_Y) { "y must be less than or equal to $MAX_Y" }
    }

    companion object {
        const val MIN_X = 1
        const val MAX_X = 10
        const val MIN_Y = 1
        const val MAX_Y = 10

        fun random(): Location {
            val x = Random.nextInt(MIN_X, MAX_X + 1)
            val y = Random.nextInt(MIN_Y, MAX_Y + 1)

            return Location(x, y)
        }
    }

    fun distance(other: Location): Int {
        return abs(x - other.x) + abs(y - other.y)
    }

}