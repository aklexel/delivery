package ru.microarch.ddd.delivery.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.microarch.ddd.delivery.core.domain.service.DispatchService
import ru.microarch.ddd.delivery.core.domain.service.DispatchServiceImpl

@Configuration
class AppConfig {

    @Bean
    fun DispatchService(): DispatchService = DispatchServiceImpl()

}
