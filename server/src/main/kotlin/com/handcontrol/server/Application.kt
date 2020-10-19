package com.handcontrol.server

import com.handcontrol.server.grpc.HandleRequestImpl
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication(
        exclude = [DataSourceAutoConfiguration::class]
)
open class Application{
    @Bean
    open fun service(): HandleRequestImpl {
        return HandleRequestImpl()
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}