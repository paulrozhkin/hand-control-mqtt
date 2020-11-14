package com.handcontrol.server.mqtt.command.set

import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.DynamicCommand
import com.handcontrol.server.mqtt.command.dto.gesture.PerformGestureByIdDto
import com.handcontrol.server.mqtt.command.enums.ApiMqttDynamicTopic
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory

/**
 * Perform a gesture by gestures's id
 */
@ExperimentalSerializationApi
object PerformGestureById : DynamicCommand(ApiMqttDynamicTopic.PERFORM_GESTURE_BY_ID) {

    private val logger = LoggerFactory.getLogger(PerformGestureById::class.java)
    lateinit var mqttWrapper: MqttClientWrapper

    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        if (!this::mqttWrapper.isInitialized) {
            val errMsg = "MqttWrapper should be initialized"
            logger.error(errMsg)
            throw IllegalStateException(errMsg)
        }

        val performGestureById =
                ProtobufSerializer.deserialize<PerformGestureByIdDto>(byteArray)
        logger.info("Try to send PerformGestureById to id {}: {}", id, performGestureById)

        val topic = topic.topicName.replace("+", id)
        mqttWrapper.publish(topic, byteArray)
    }

}