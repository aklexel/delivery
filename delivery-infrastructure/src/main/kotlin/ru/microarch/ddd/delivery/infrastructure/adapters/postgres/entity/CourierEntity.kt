package ru.microarch.ddd.delivery.infrastructure.adapters.postgres.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table(name = "couriers")
data class CourierEntity(
    @Id
    @JvmField
    val id: UUID,
    val name: String,
    val transportId: UUID,
    val status: String,
    val locationX: Int,
    val locationY: Int,
) : PersistableEntity<UUID>(id)
