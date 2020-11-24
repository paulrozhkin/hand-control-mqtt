package com.handcontrol.server.mqtt.command.enums

import com.handcontrol.server.mqtt.command.DynamicCommand
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * A prosthesis API as dynamic mqtt topics (with regexp)
 *
 * @see <a href="https://github.com/paulrozhkin/handcontrol-documentation/blob/master/api.md">API Description</a>
 */
@Service
@ExperimentalSerializationApi
class DynamicApi {

    private val logger = LoggerFactory.getLogger(DynamicApi::class.java)

    @Autowired
    lateinit var commands: List<DynamicCommand>

    fun handle(topicName: String, id: String, payload: ByteArray) {
        val topic = DynamicTopic.getByName(topicName)
        if (topic == null) {
            val errMsg = String.format("Unknown dynamic mqtt topic: %s.", topicName)
            logger.error(errMsg)
            throw IllegalArgumentException(errMsg)
        }

        val command = commands.find { it.topic == topic }
        command?.handlePayloadAndId(id, payload)
    }

    enum class DynamicTopic(val topicName: String, val mode: TopicMode) {
        GET_TELEMETRY("+/data/telemetry", TopicMode.READ),
        GET_SETTINGS("+/data/settings", TopicMode.READ),
        SET_SETTINGS("+/action/settings", TopicMode.WRITE),
        GET_GESTURES("+/data/gestures", TopicMode.READ),
        SAVE_GESTURE("+/action/gestures", TopicMode.WRITE),
        DELETE_GESTURE("+/action/gestures/remove", TopicMode.WRITE),
        PERFORM_GESTURE_BY_ID("+/action/performGestureId", TopicMode.WRITE),
        PERFORM_GESTURE_RAW("+/action/performGestureRaw", TopicMode.WRITE),
        SET_POSITIONS("+/action/positions", TopicMode.WRITE);

        companion object {
            fun getByName(name: String): DynamicTopic? {
                return values().find { name == it.topicName }
            }
        }
    }
}