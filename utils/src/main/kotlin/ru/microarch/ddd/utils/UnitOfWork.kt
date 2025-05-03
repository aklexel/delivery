package ru.microarch.ddd.utils

interface UnitOfWork {

    suspend fun <T> commitChanges(block: suspend () -> T): T

}
