package com.handcontrol.server.mqqt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "mqtt")
data class MqttProperties(
        val brokerAddr: String,
        val brokerPort: Int,
        val qosLevel: Int
)