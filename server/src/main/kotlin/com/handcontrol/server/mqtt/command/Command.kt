package com.handcontrol.server.mqtt.command

import com.handcontrol.server.mqtt.command.enums.ApiMqttTopic

abstract class Command(val topic: ApiMqttTopic) {
    abstract fun handlePayload(byteArray: ByteArray)
}