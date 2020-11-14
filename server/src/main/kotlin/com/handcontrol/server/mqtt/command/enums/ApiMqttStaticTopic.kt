package com.handcontrol.server.mqtt.command.enums

import com.handcontrol.server.mqtt.command.set.GetOffline
import com.handcontrol.server.mqtt.command.set.GetOnline
import kotlinx.serialization.ExperimentalSerializationApi

/**
 * A prosthesis API as mqtt topics
 *
 * @see <a href="https://github.com/paulrozhkin/handcontrol-documentation/blob/master/api.md">API Description</a>
 */
@ExperimentalSerializationApi
enum class ApiMqttStaticTopic(val topicName: String, val mode: TopicMode) {
    GET_ONLINE("controllers/online", TopicMode.READ) {
        override fun getContentHandler(): (ByteArray) -> Unit {
            return GetOnline::handlePayload
        }
    },
    GET_OFFLINE("controllers/offline", TopicMode.READ) {
        override fun getContentHandler(): (ByteArray) -> Unit {
            return GetOffline::handlePayload
        }
    };

    abstract fun getContentHandler(): (ByteArray) -> Unit

    companion object {
        fun getByName(name: String): ApiMqttStaticTopic? {
            return values().find { name == it.topicName }
        }
    }
}
