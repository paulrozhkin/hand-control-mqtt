package com.handcontrol.server.mqtt.command.set

import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.DynamicCommand
import com.handcontrol.server.mqtt.command.dto.gesture.PerformGestureRawDto
import com.handcontrol.server.mqtt.command.enums.ApiMqttDynamicTopic
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory

/**
 * Perform a gesture by gestures's id
 */
@ExperimentalSerializationApi
object PerformGestureRaw : DynamicCommand(ApiMqttDynamicTopic.PERFORM_GESTURE_RAW) {

    private val logger = LoggerFactory.getLogger(PerformGestureRaw::class.java)
    lateinit var mqttWrapper: MqttClientWrapper

    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        if (!this::mqttWrapper.isInitialized) {
            val errMsg = "MqttWrapper should be initialized"
            logger.error(errMsg)
            throw IllegalStateException(errMsg)
        }

        val performGestureRaw =
                ProtobufSerializer.deserialize<PerformGestureRawDto>(byteArray)
        logger.info("Try to send PerformGestureRaw to id {}: {}", id, performGestureRaw)

        val topic = topic.topicName.replace("+", id)
        mqttWrapper.publish(topic, byteArray)
    }

}