package ru.microarch.ddd.delivery.infrastructure.adapters.postgres

import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import ru.microarch.ddd.utils.UnitOfWork

@Component
class UnitOfWorkImpl(
    transactionManager: ReactiveTransactionManager
) : UnitOfWork {

    private val transactionalOperator = TransactionalOperator.create(transactionManager)

    override suspend fun <T> commitChanges(block: suspend () -> T): T {
        return transactionalOperator.executeAndAwait {
            block()
        }
    }

}
