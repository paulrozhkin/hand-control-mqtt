package com.handcontrol.server.mqtt.command

import com.handcontrol.server.mqtt.command.enums.ApiMqttStaticTopic

@ExperimentalUnsignedTypes
abstract class StaticCommand(val topic: ApiMqttStaticTopic) {
    abstract fun handlePayload(byteArray: ByteArray)
}