package ru.microarch.ddd.delivery.infrastructure.adapters.postgres.entity

import org.springframework.data.domain.Persistable
import org.springframework.data.annotation.Transient

/**
 * По умолчанию R2DBC при сохранении сущности ориентируется на поле помеченное аннотацией @Id:
 * - если значение != null, то предполагается, что соответствующая строка в БД уже есть – нужно обновить данные;
 * - если значение == null, то происходит добавление новой строки в БД.
 *
 * При этом, если значение != null и в БД нет соответствующей строки, то возникнет ошибка.
 * Так как мы сами управляем генерацией ID, то наши сущности должны имплементировать интерфейс Persistable.
 */
abstract class PersistableEntity<ID>(
    @Transient
    @JvmField
    val entityId: ID,
    @Transient
    @JvmField
    var isNew: Boolean = false
) : Persistable<ID> {

    override fun getId(): ID {
        return entityId
    }

    override fun isNew(): Boolean {
        return isNew
    }

}
