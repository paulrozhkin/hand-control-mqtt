package com.handcontrol.server.mqtt.command

import com.handcontrol.server.mqtt.command.enums.StaticApi
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
abstract class StaticCommand(val topic: StaticApi.StaticTopic) {
    abstract fun handlePayload(byteArray: ByteArray)
}