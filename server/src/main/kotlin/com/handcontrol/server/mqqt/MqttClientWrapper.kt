package com.handcontrol.server.mqqt

/**
 * Wrap main functions of vertex mqtt client.
 */
interface MqttClientWrapper {
    fun connect()
    fun disconnect()
    fun subscribe(topic: String)
    fun unsubscribe(topic: String)
    fun publish(topic: String, msg: String)
}