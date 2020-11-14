package com.handcontrol.server.mqtt.command.enums

import com.handcontrol.server.mqtt.command.get.GetGestures
import com.handcontrol.server.mqtt.command.get.GetSettings
import com.handcontrol.server.mqtt.command.get.GetTelemetry
import com.handcontrol.server.mqtt.command.set.DeleteGesture
import com.handcontrol.server.mqtt.command.set.PerformGestureById
import com.handcontrol.server.mqtt.command.set.PerformGestureRaw
import com.handcontrol.server.mqtt.command.set.SaveGesture
import com.handcontrol.server.mqtt.command.set.SetSettings
import kotlinx.serialization.ExperimentalSerializationApi

/**
 * A prosthesis API as dynamic mqtt topics (with regexp)
 *
 * @see <a href="https://github.com/paulrozhkin/handcontrol-documentation/blob/master/api.md">API Description</a>
 */
@ExperimentalSerializationApi
enum class ApiMqttDynamicTopic(val topicName: String, val mode: TopicMode) {
    GET_TELEMETRY("+/data/telemetry", TopicMode.READ) {
        override fun getContentHandler(): (String, ByteArray) -> Unit {
            return GetTelemetry::handlePayloadAndId
        }
    },
    GET_SETTINGS("+/data/settings", TopicMode.READ) {
        override fun getContentHandler(): (String, ByteArray) -> Unit {
            return GetSettings::handlePayloadAndId
        }
    },
    SET_SETTINGS("+/action/settings", TopicMode.WRITE) {
        override fun getContentHandler(): (String, ByteArray) -> Unit {
            return SetSettings::handlePayloadAndId
        }
    },
    GET_GESTURES("+/data/gestures", TopicMode.READ) {
        override fun getContentHandler(): (String, ByteArray) -> Unit {
            return GetGestures::handlePayloadAndId
        }
    },
    SAVE_GESTURE("+/action/gestures", TopicMode.WRITE) {
        override fun getContentHandler(): (String, ByteArray) -> Unit {
            return SaveGesture::handlePayloadAndId
        }
    },
    DELETE_GESTURE("+/action/gestures/remove", TopicMode.WRITE) {
        override fun getContentHandler(): (String, ByteArray) -> Unit {
            return DeleteGesture::handlePayloadAndId
        }
    },
    PERFORM_GESTURE_BY_ID("+/action/performGestureId", TopicMode.WRITE) {
        override fun getContentHandler(): (String, ByteArray) -> Unit {
            return PerformGestureById::handlePayloadAndId
        }
    },
    PERFORM_GESTURE_RAW("+/action/performGestureRaw", TopicMode.WRITE) {
        override fun getContentHandler(): (String, ByteArray) -> Unit {
            return PerformGestureRaw::handlePayloadAndId
        }
    };

    abstract fun getContentHandler(): (String, ByteArray) -> Unit

    companion object {
        fun getByName(name: String): ApiMqttDynamicTopic? {
            return values().find { name == it.topicName }
        }
    }
}
