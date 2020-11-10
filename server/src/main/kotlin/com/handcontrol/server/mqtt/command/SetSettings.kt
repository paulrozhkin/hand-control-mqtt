package com.handcontrol.server.mqtt.command

import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.dto.SetSettingsDto
import com.handcontrol.server.mqtt.command.enums.ApiMqttDynamicTopic
import com.handcontrol.server.util.ObjectSerializer
import org.slf4j.LoggerFactory

/**
 * Change prosthesis configuration
 */
@ExperimentalUnsignedTypes
object SetSettings : DynamicCommand(ApiMqttDynamicTopic.SET_SETTINGS) {

    private val logger = LoggerFactory.getLogger(SetSettings::class.java)
    lateinit var mqttWrapper: MqttClientWrapper

    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        if (!this::mqttWrapper.isInitialized) {
            val errMsg = "MqttWrapper should be initialized"
            logger.error(errMsg)
            throw IllegalStateException(errMsg)
        }

        val settings = ObjectSerializer.deserialize<SetSettingsDto>(byteArray)
        logger.info("Try to send settings to id {}: {}", id, settings)

        val topic = topic.topicName.replace("+", id)
        mqttWrapper.publish(topic, byteArray)
    }

}