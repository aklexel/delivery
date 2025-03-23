package ru.microarch.ddd.delivery.core.domain.model.shared.kernel

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class LocationShould {

    @Test
    fun `allow to check if two instances are equal`() {
        val l1 = Location(1, 10)
        val l2 = Location(1, 10)

        assertEquals(l1, l1)
        assertEquals(l1, l2)
    }

    @ParameterizedTest
    @CsvSource(
        "1, 1, 1, 2",
        "1, 1, 2, 1",
        "10, 10, 10, 1",
        "10, 10, 1, 10",
    )
    fun `allow to check if two instances are not equal`(x1: Int, y1: Int, x2: Int, y2: Int) {
        val l1 = Location(x1, y1)
        val l2 = Location(x2, y2)

        assertNotEquals(l1, l2)
    }

    @ParameterizedTest
    @CsvSource(
        "0, 0",
        "0, 1",
        "1, 0",
        "1, 11",
        "11, 1",
        "11, 11",
    )
    fun `not allow to use incorrect coordinate value`(x: Int, y: Int) {
        assertThrows<IllegalArgumentException> { Location(x, y) }
    }

    @Test
    fun `generate random instance`() {
        val l1 = Location.random()
        val l2 = Location.random()

        assertNotEquals(l1, l2)
    }

    @ParameterizedTest
    @CsvSource(
        "1, 1, 1, 1, 0",
        "1, 1, 2, 1, 1",
        "1, 1, 1, 2, 1",
        "2, 6, 4, 9, 5",
        "1, 1, 10, 10, 18",
    )
    fun `calculate distance between two instances`(x1: Int, y1: Int, x2: Int, y2: Int, distance: Int) {
        val l1 = Location(x1, y1)
        val l2 = Location(x2, y2)

        assertEquals(distance, l1.distance(l2))
        assertEquals(distance, l2.distance(l1))
    }
}
