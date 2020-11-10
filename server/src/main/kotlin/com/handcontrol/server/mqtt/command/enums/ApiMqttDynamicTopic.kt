package com.handcontrol.server.mqtt.command.enums

import com.handcontrol.server.mqtt.command.GetTelemetry
import com.handcontrol.server.mqtt.command.SetSettings

/**
 * A prosthesis API as dynamic mqtt topics (with regexp)
 *
 * @see <a href="https://github.com/paulrozhkin/handcontrol-documentation/blob/master/api.md">API Description</a>
 */
@ExperimentalUnsignedTypes
enum class ApiMqttDynamicTopic(val topicName: String, val mode: TopicMode) {
    GET_TELEMETRY("+/data/telemetry", TopicMode.READ) {
        override fun getContentHandler(): (String, ByteArray) -> Unit {
            return GetTelemetry::handlePayloadAndId
        }
    },
    SET_SETTINGS("+/action/settings", TopicMode.WRITE) {
        override fun getContentHandler(): (String, ByteArray) -> Unit {
            return SetSettings::handlePayloadAndId
        }
    };

    abstract fun getContentHandler(): (String, ByteArray) -> Unit

    companion object {
        fun getByName(name: String): ApiMqttDynamicTopic? {
            return values().find { name == it.topicName }
        }
    }
}
