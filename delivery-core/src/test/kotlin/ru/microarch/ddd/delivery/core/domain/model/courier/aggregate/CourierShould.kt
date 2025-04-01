package ru.microarch.ddd.delivery.core.domain.model.courier.aggregate

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import ru.microarch.ddd.delivery.core.domain.model.shared.kernel.Location

class CourierShould {

    @Test
    fun `allow to create instance`() {
        val name = "John Smith"
        val transportName = "super bike"
        val transportSpeed = 2
        val location = Location.random()

        val courier = Courier(name, transportName, transportSpeed, location)

        assertEquals(name, courier.name)
        assertEquals(transportName, courier.transport.name)
        assertEquals(transportSpeed, courier.transport.speed)
        assertEquals(location, courier.location)
        assertEquals(CourierStatus.FREE, courier.status)
    }

    @ParameterizedTest
    @CsvSource(
        "'', bike, 1",
        "' ', bike, 1",
        "'John', bike, -1",
        "'John', bike, 0",
        "'John', bike, 4",
        "'John', '', 1",
    )
    fun `not allow to create courier with incorrect values`(name: String, transportName: String, speed: Int) {
        assertThrows<IllegalArgumentException> {
            Courier(name, transportName, speed, Location.random())
        }
    }

    @Test
    fun `allow to set busy status`() {
        val courier = Courier("John", "bike", 2, Location.random())

        courier.status = CourierStatus.BUSY

        assertEquals(CourierStatus.BUSY, courier.status)
    }

    @Test
    fun `not allow to set busy status when status is already busy`() {
        val courier = Courier("John", "bike", 2, Location.random())

        courier.status = CourierStatus.BUSY

        assertThrows<Courier.SetBusyStatusToBusyCourierException> {
            courier.status = CourierStatus.BUSY
        }
    }

    @Test
    fun `allow to set free status`() {
        val courier = Courier("John", "bike", 2, Location.random())

        courier.status = CourierStatus.BUSY
        courier.status = CourierStatus.FREE

        assertEquals(CourierStatus.FREE, courier.status)
    }

    @Test
    fun `not allow to set free status when status is already free`() {
        val courier = Courier("John", "bike", 2, Location.random())

        courier.status = CourierStatus.BUSY
        courier.status = CourierStatus.FREE

        assertThrows<Courier.SetFreeStatusToFreeCourierException> {
            courier.status = CourierStatus.FREE
        }
    }

    @ParameterizedTest
    @CsvSource(
        // already at the location
        "5,5, 5,5, 1, 0",
        // one move required
        "5,5, 5,6, 1, 1",
        "5,5, 5,6, 2, 1",
        "5,5, 4,4, 2, 1",
        "5,5, 6,7, 3, 1",
        // several moves required
        "1,1, 5,5, 2, 4",
        "5,5, 1,2, 3, 3",
        "5,5, 8,2, 1, 6",
    )
    fun `calculate correct time to location`(
        curX: Int,
        curY: Int,
        targetX: Int,
        targetY: Int,
        speed: Int,
        time: Int
    ) {
        val current = Location(curX, curY)
        val target = Location(targetX, targetY)
        val courier = Courier("John", "bike", speed, current)

        assertEquals(time, courier.calculateTimeToLocation(target))
    }

    @ParameterizedTest
    @CsvSource(
        // already at the location
        "5,5, 5,5, 1",
        // one move required
        "5,5, 5,6, 1",
        "5,5, 5,6, 2",
        "5,5, 4,4, 2",
        "5,5, 6,7, 3",
    )
    fun `move to target location when it's near to it`(
        curX: Int,
        curY: Int,
        targetX: Int,
        targetY: Int,
        speed: Int,
    ) {
        val current = Location(curX, curY)
        val target = Location(targetX, targetY)
        val courier = Courier("John", "bike", speed, current)

        courier.move(target)

        assertEquals(target, courier.location)
    }

    @ParameterizedTest
    @CsvSource(
        "1,1, 1,3, 1",
        "1,1, 5,5, 2",
        "5,5, 1,2, 3",
        "5,5, 8,2, 1",
    )
    fun `move one step towards target location`(
        curX: Int,
        curY: Int,
        targetX: Int,
        targetY: Int,
        speed: Int
    ) {
        val current = Location(curX, curY)
        val target = Location(targetX, targetY)
        val courier = Courier("John", "bike", speed, current)
        val expectedDistance = current.distance(target) - speed

        courier.move(target)

        assertEquals(expectedDistance, courier.location.distance(target))
    }

}
