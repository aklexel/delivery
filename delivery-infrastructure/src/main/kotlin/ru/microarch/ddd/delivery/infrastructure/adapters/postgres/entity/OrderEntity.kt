package ru.microarch.ddd.delivery.infrastructure.adapters.postgres.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table(name = "orders")
data class OrderEntity(
    @Id
    @JvmField
    val id: UUID,
    val courierId: UUID?,
    val locationX: Int,
    val locationY: Int,
    val status: String,
) : PersistableEntity<UUID>(id)
