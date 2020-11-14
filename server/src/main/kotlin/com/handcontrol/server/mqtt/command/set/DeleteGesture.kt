package com.handcontrol.server.mqtt.command.set

import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.DynamicCommand
import com.handcontrol.server.mqtt.command.dto.gesture.DeleteGestureDto
import com.handcontrol.server.mqtt.command.enums.ApiMqttDynamicTopic
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory

/**
 * Delete a gesture
 */
@ExperimentalSerializationApi
object DeleteGesture : DynamicCommand(ApiMqttDynamicTopic.DELETE_GESTURE) {

    private val logger = LoggerFactory.getLogger(DeleteGesture::class.java)
    lateinit var mqttWrapper: MqttClientWrapper

    // todo all write commands have the same function -> move to util
    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        if (!this::mqttWrapper.isInitialized) {
            val errMsg = "MqttWrapper should be initialized"
            logger.error(errMsg)
            throw IllegalStateException(errMsg)
        }

        val deleteGesture = ProtobufSerializer.deserialize<DeleteGestureDto>(byteArray)
        logger.info("Try to send DeleteGesture to id {}: {}", id, deleteGesture)

        val topic = topic.topicName.replace("+", id)
        mqttWrapper.publish(topic, byteArray)
    }

}