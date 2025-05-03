package ru.microarch.ddd.utils

import java.util.UUID

fun String.toUUID(): UUID = UUID.fromString(this)
