package ru.microarch.ddd.delivery.infrastructure.adapters.postgres.extension

import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.junit.jupiter.SpringExtension

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RunSql(vararg val value: String)

class RunSqlExtension : BeforeEachCallback {

    override fun beforeEach(context: ExtensionContext) {
        val testInstance = context.requiredTestInstance
        val testMethod = context.requiredTestMethod

        val sql = testMethod.getAnnotation(RunSql::class.java)?.value
            ?: testInstance::class.java.getAnnotation(RunSql::class.java)?.value

        val appCtx = SpringExtension.getApplicationContext(context)
        val databaseClient = appCtx.getBean(DatabaseClient::class.java)

        sql?.forEach {
            databaseClient.sql(it).then().block()
        }
    }

}
