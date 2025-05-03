package ru.microarch.ddd.delivery.infrastructure.adapters.postgres.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.Courier
import ru.microarch.ddd.delivery.core.domain.model.courier.aggregate.CourierStatus
import ru.microarch.ddd.delivery.core.ports.CourierRepository
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.entity.CourierEntity
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.entity.TransportEntity
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.mapper.toCourier
import ru.microarch.ddd.delivery.infrastructure.adapters.postgres.mapper.toCourierAndTransportEntities
import java.util.UUID


@Repository("postgres-courier-adapter")
class CourierRepositoryImpl(
    private val courierRepository: CourierDbRepository,
    private val transportRepository: TransportDbRepository,
) : CourierRepository {

    override suspend fun add(courier: Courier): Courier {
        val (courierEntity, transportEntity) = courier.toCourierAndTransportEntities(isNew = true)

        return transportRepository
            .findById(transportEntity.id)
            .map { transportEntity.apply { isNew = false } }
            .defaultIfEmpty(transportEntity)
            .flatMap {
                transportRepository.save(it)
            }
            .flatMap { transport ->
                courierRepository
                    .save(courierEntity)
                    .map { it.toCourier(transport) }
            }
            .awaitSingle()
    }

    override suspend fun update(courier: Courier): Courier {
        val (courierEntity, transportEntity) = courier.toCourierAndTransportEntities()

        return transportRepository
            .findById(transportEntity.id)
            .defaultIfEmpty(transportEntity.apply { isNew = true })
            .flatMap {
                transportRepository.save(it)
            }
            .flatMap { transport ->
                courierRepository
                    .save(courierEntity)
                    .map { it.toCourier(transport) }
            }
            .awaitSingle()
    }

    override suspend fun get(courierId: UUID): Courier? {
        return courierRepository
            .findById(courierId)
            .flatMap(this::joinTransport)
            .awaitSingleOrNull()
    }

    override suspend fun getAllWithFreeStatus(): Flow<Courier> {
        return courierRepository
            .findAllByStatus(CourierStatus.FREE.name)
            .flatMap(this::joinTransport)
            .asFlow()
    }

    private fun joinTransport(courier: CourierEntity): Mono<Courier> =
        transportRepository
            .findById(courier.transportId)
            .map { courier.toCourier(it) }

}

interface CourierDbRepository : R2dbcRepository<CourierEntity, UUID> {
    fun findAllByStatus(status: String): Flux<CourierEntity>
}

interface TransportDbRepository : R2dbcRepository<TransportEntity, UUID>
