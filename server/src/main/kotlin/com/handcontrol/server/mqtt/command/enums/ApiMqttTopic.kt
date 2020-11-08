package com.handcontrol.server.mqtt.command.enums

import com.handcontrol.server.mqtt.command.SetOnline

/**
 * A prosthesis API as mqtt topics
 *
 * @see <a href="https://github.com/paulrozhkin/handcontrol-documentation/blob/master/api.md">API Description</a>
 */
enum class ApiMqttTopic(val topicName: String, val mode: TopicMode) {
    SET_ONLINE("controllers/online", TopicMode.READ) {
        override fun getContentHandler(): (ByteArray) -> Unit {
            return SetOnline::handlePayload
        }
    };

    abstract fun getContentHandler(): (ByteArray) -> Unit

    companion object {
        fun getByName(name: String): ApiMqttTopic {
            return values().first { name == it.topicName }
        }
    }
}