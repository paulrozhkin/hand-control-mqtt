package com.handcontrol.server.mqtt.command.set

import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.DynamicCommand
import com.handcontrol.server.mqtt.command.dto.settings.SetSettingsDto
import com.handcontrol.server.mqtt.command.enums.ApiMqttDynamicTopic
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory

/**
 * Change prosthesis configuration
 */
@ExperimentalSerializationApi
object SetSettings : DynamicCommand(ApiMqttDynamicTopic.SET_SETTINGS) {

    private val logger = LoggerFactory.getLogger(SetSettings::class.java)
    lateinit var mqttWrapper: MqttClientWrapper

    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        if (!this::mqttWrapper.isInitialized) {
            val errMsg = "MqttWrapper should be initialized"
            logger.error(errMsg)
            throw IllegalStateException(errMsg)
        }

        val settings = ProtobufSerializer.deserialize<SetSettingsDto>(byteArray)
        logger.info("Try to send SetSettings to id {}: {}", id, settings)

        val topic = topic.topicName.replace("+", id)
        mqttWrapper.publish(topic, byteArray)
    }

}