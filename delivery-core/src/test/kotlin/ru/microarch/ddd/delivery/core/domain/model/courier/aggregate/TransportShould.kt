package ru.microarch.ddd.delivery.core.domain.model.courier.aggregate

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import ru.microarch.ddd.delivery.core.domain.model.shared.kernel.Location

class TransportShould {

    @ParameterizedTest
    @CsvSource(
        "bike, -1",
        "bike, 0",
        "bike, 4",
        "'', 1",
    )
    fun `not allow to create transport with incorrect values`(name: String, speed: Int) {
        assertThrows<IllegalArgumentException> { Transport(name, speed) }
    }

    @Test
    fun `be equal to another instance when both have the same id`() {
        val t1 = Transport("bike", 1)
        val t2 = Transport("bike", 1)

        assertEquals(t1, t1)
        assertNotEquals(t1, t2)
    }

    @ParameterizedTest
    @CsvSource(
        // don't need to move
        "1, 1, 1, 1, 1",
        "10, 10, 10, 10, 2",
        // move forward
        "1, 1, 1, 2, 1",
        "1, 1, 1, 2, 2",
        "1, 1, 2, 1, 1",
        "1, 1, 2, 1, 2",
        "1, 1, 2, 2, 2",
        "1, 1, 2, 2, 3",
        // move backward
        "10, 10, 10, 9, 1",
        "10, 10, 10, 9, 2",
        "10, 10, 9, 10, 1",
        "10, 10, 9, 10, 2",
        "10, 10, 9, 9, 2",
        "10, 10, 9, 9, 3",
        // move forward by X and backward by Y
        "5, 5, 6, 4, 2",
        "5, 5, 6, 4, 3",
        // move backward by X and forward by Y
        "5, 5, 4, 6, 2",
        "5, 5, 4, 6, 3",
    )
    fun `move to target location for one step when it's near`(
        curX: Int,
        curY: Int,
        targetX: Int,
        targetY: Int,
        speed: Int
    ) {
        val currentLocation = Location(curX, curY)
        val targetLocation = Location(targetX, targetY)
        val transport = Transport("bike", speed)

        val newLocation = transport.move(currentLocation, targetLocation)

        assertEquals(targetLocation, newLocation)
    }

    @ParameterizedTest
    @CsvSource(
        // move forward
        "1, 1, 1, 3",
        "1, 1, 3, 1",
        "1, 1, 2, 2",
        // move backward
        "10, 10, 8, 10",
        "10, 10, 10, 8",
        "10, 10, 9, 9",
        // move forward by X and backward by Y
        "5, 5, 6, 4",
        // move backward by X and forward by Y
        "5, 5, 4, 6",
    )
    fun `move one step closer to target location when speed=1 and it's far away`(
        curX: Int,
        curY: Int,
        targetX: Int,
        targetY: Int
    ) {
        val speed = 1
        val currentLocation = Location(curX, curY)
        val targetLocation = Location(targetX, targetY)
        val expectedDistance = currentLocation.distance(targetLocation) - speed
        val transport = Transport("bike", speed)

        val newLocation = transport.move(currentLocation, targetLocation)

        assertEquals(expectedDistance, newLocation.distance(targetLocation))
    }

    @ParameterizedTest
    @CsvSource(
        // move forward
        "1, 1, 1, 5",
        "1, 1, 5, 1",
        "1, 1, 3, 3",
        // move backward
        "10, 10, 6, 10",
        "10, 10, 10, 6",
        "10, 10, 8, 8",
        // move forward by X and backward by Y
        "5, 5, 7, 3",
        // move backward by X and forward by Y
        "5, 5, 3, 7",
    )
    fun `move one step closer to target location when speed=3 and it's far away`(
        curX: Int,
        curY: Int,
        targetX: Int,
        targetY: Int
    ) {
        val speed = 3
        val currentLocation = Location(curX, curY)
        val targetLocation = Location(targetX, targetY)
        val expectedDistance = currentLocation.distance(targetLocation) - speed
        val transport = Transport("car", speed)

        val newLocation = transport.move(currentLocation, targetLocation)

        assertEquals(expectedDistance, newLocation.distance(targetLocation))
    }

}
