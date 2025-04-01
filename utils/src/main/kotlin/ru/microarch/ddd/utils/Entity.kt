package ru.microarch.ddd.utils

abstract class Entity<IdT> {
    abstract val id: IdT

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (this.javaClass != other?.javaClass) return false

        other as Entity<*>

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
