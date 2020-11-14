package com.handcontrol.server.mqtt.command.set

import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.DynamicCommand
import com.handcontrol.server.mqtt.command.dto.gesture.SaveGestureDto
import com.handcontrol.server.mqtt.command.enums.ApiMqttDynamicTopic
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory

/**
 * Save or update a gesture
 */
@ExperimentalSerializationApi
object SaveGesture : DynamicCommand(ApiMqttDynamicTopic.SAVE_GESTURE) {

    private val logger = LoggerFactory.getLogger(SaveGesture::class.java)
    lateinit var mqttWrapper: MqttClientWrapper

    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        if (!this::mqttWrapper.isInitialized) {
            val errMsg = "MqttWrapper should be initialized"
            logger.error(errMsg)
            throw IllegalStateException(errMsg)
        }

        val saveGesture = ProtobufSerializer.deserialize<SaveGestureDto>(byteArray)
        logger.info("Try to send SaveGesture to id {}: {}", id, saveGesture)

        val topic = topic.topicName.replace("+", id)
        mqttWrapper.publish(topic, byteArray)
    }

}