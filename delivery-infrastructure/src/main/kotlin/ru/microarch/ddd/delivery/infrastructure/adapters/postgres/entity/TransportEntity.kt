package ru.microarch.ddd.delivery.infrastructure.adapters.postgres.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table(name = "transports")
data class TransportEntity(
    @Id
    @JvmField
    val id: UUID,
    val name: String,
    val speed: Int
) : PersistableEntity<UUID>(id)
