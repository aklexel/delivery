package ru.microarch.ddd.delivery.infrastructure.adapters.postgres

import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.test.runTest
import org.springframework.beans.factory.annotation.Autowired
import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.Courier
import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.CourierStatus
import ru.microarch.ddd.delivery.core.domain.model.shared.kernel.Location
import ru.microarch.ddd.delivery.core.ports.CourierRepository
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.extension.PostgresTest
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.extension.RunSql
import ru.microarch.ddd.utils.toUUID
import kotlin.test.*

@PostgresTest
class CourierRepositoryShould {

    @Autowired
    lateinit var repository: CourierRepository

    @Test
    @RunSql(
        """insert into 
            transports (id, name, speed) 
            values ('4c7b911e-f833-4b17-a725-9e1183723509', 'bike', 2)""",
        """insert into 
            couriers (id, name, transport_id, location_x, location_y, status) 
            values ('d8319f99-736b-48f4-afe7-115e6ee8ce51', 'John', '4c7b911e-f833-4b17-a725-9e1183723509', 5, 6, 'FREE')"""
    )
    fun `get courier by id`() = runTest {
        val id = "d8319f99-736b-48f4-afe7-115e6ee8ce51".toUUID()
        val transportId = "4c7b911e-f833-4b17-a725-9e1183723509".toUUID()

        val courier = repository.get(id)

        assertNotNull(courier)
        assertEquals(id, courier.id)
        assertEquals("John", courier.name)
        assertEquals(CourierStatus.FREE, courier.status)
        assertEquals(Location(5, 6), courier.location)

        val transport = courier.transport

        assertEquals(transportId, transport.id)
        assertEquals("bike", transport.name)
        assertEquals(2, transport.speed)
    }

    @Test
    @RunSql(
        "delete from couriers",
        "delete from transports",
        """insert into 
            transports (id, name, speed) 
            values 
                ('545a2b3e-1e66-4c42-ba55-86f22af95504', 'bike', 2),
                ('a12c5b3d-1aa9-490f-864d-ea54c7832d05', 'car', 3)""",
        """insert into 
            couriers (id, name, transport_id, location_x, location_y, status) 
            values 
                ('512f0307-61d6-4e16-b139-56a01f9420d0', 'John', '545a2b3e-1e66-4c42-ba55-86f22af95504', 5, 6, 'FREE'),
                ('5b393424-d912-4d96-b9cb-eddb7625ca95', 'Mike', '545a2b3e-1e66-4c42-ba55-86f22af95504', 5, 6, 'BUSY'),
                ('ec34abcd-27f4-4d7d-a050-6a115218faa4', 'Tom', 'a12c5b3d-1aa9-490f-864d-ea54c7832d05', 5, 6, 'FREE')"""
    )
    fun `get all couriers with free status`() = runTest {
        val freeCourierIds = setOf("512f0307-61d6-4e16-b139-56a01f9420d0", "ec34abcd-27f4-4d7d-a050-6a115218faa4")

        val couriers = repository.getAllWithFreeStatus()

        assertEquals(2, couriers.count())
        assertEquals(freeCourierIds, couriers.map { it.id.toString() }.toSet())
        couriers.collect {
            assertEquals(CourierStatus.FREE, it.status)

            val transport = it.transport

            if (it.id == "512f0307-61d6-4e16-b139-56a01f9420d0".toUUID()) {
                assertEquals("545a2b3e-1e66-4c42-ba55-86f22af95504".toUUID(), transport.id)
                assertEquals("bike", transport.name)
                assertEquals(2, transport.speed)
            } else {
                assertEquals("a12c5b3d-1aa9-490f-864d-ea54c7832d05".toUUID(), transport.id)
                assertEquals("car", transport.name)
                assertEquals(3, transport.speed)
            }
        }

    }

    @Test
    fun `add courier`() = runTest {
        val courier = Courier("John Smith", "super bike", 3, Location.random())

        val savedCourier = repository.add(courier)
        val dbCourier = repository.get(courier.id)

        assertCourierEquals(courier, savedCourier)
        assertCourierEquals(courier, dbCourier)
    }

    @Test
    @RunSql(
        """insert into 
            transports (id, name, speed) 
            values ('fe60a3c7-1618-4c90-b962-be252c0abcde', 'bike', 2)""",
        """insert into 
            couriers (id, name, transport_id, location_x, location_y, status) 
            values ('b45985fb-d083-423b-a7f2-e30bed2e1e76', 'John', 'fe60a3c7-1618-4c90-b962-be252c0abcde', 5, 6, 'FREE')"""
    )
    fun `update courier`() = runTest {
        val id = "b45985fb-d083-423b-a7f2-e30bed2e1e76".toUUID()
        val location = Location(6, 7)
        val courier = repository.get(id)

        courier!!.apply {
            move(location)
            setBusy()
        }

        val updatedCourier = repository.update(courier)

        assertNotNull(updatedCourier)
        assertEquals(id, updatedCourier.id)
        assertEquals(location, updatedCourier.location)
        assertEquals(CourierStatus.BUSY, updatedCourier.status)

        val dbCourier = repository.get(id)
        assertCourierEquals(updatedCourier, dbCourier)
    }

    companion object {
        fun assertCourierEquals(expected: Courier, actual: Courier?) {
            assertNotNull(actual)
            assertEquals(expected.id, actual.id)
            assertEquals(expected.name, actual.name)
            assertEquals(expected.status, actual.status)
            assertEquals(expected.location, actual.location)
            assertEquals(expected.transport.id, actual.transport.id)
            assertEquals(expected.transport.name, actual.transport.name)
            assertEquals(expected.transport.speed, actual.transport.speed)
        }
    }

}
