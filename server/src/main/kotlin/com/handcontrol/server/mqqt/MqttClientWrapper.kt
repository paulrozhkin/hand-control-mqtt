package com.handcontrol.server.mqqt

import io.vertx.core.buffer.Buffer

interface MqttClientWrapper {
    fun connect()
    fun disconnect()
    fun subscribe(topic: String)
    fun unsubscribe(topic: String)
    fun publish(topic: String, msg: String)
}