package com.handcontrol.server.mqtt.command

import com.handcontrol.server.mqtt.command.enums.ApiMqttDynamicTopic
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
abstract class DynamicCommand(val topic: ApiMqttDynamicTopic) {
    abstract fun handlePayloadAndId(id: String, byteArray: ByteArray)
}