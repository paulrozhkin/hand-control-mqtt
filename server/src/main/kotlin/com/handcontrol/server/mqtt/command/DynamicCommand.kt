package com.handcontrol.server.mqtt.command

import com.handcontrol.server.mqtt.command.enums.DynamicApi.DynamicTopic
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
abstract class DynamicCommand(val topic: DynamicTopic) {
    abstract fun handlePayloadAndId(id: String, byteArray: ByteArray)
}