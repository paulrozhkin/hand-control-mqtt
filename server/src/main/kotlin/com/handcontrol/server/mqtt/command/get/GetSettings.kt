package com.handcontrol.server.mqtt.command.get

import com.handcontrol.server.mqtt.command.DynamicCommand
import com.handcontrol.server.mqtt.command.dto.settings.GetSettingsDto
import com.handcontrol.server.mqtt.command.enums.ApiMqttDynamicTopic
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory

/**
 *  Get actual configuration of a prosthesis
 */
@ExperimentalSerializationApi
object GetSettings : DynamicCommand(ApiMqttDynamicTopic.GET_SETTINGS) {
    private val logger = LoggerFactory.getLogger(GetSettings::class.java)

    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        val settings = ProtobufSerializer.deserialize<GetSettingsDto>(byteArray)
        logger.debug("Get settings from {}: {}", id, settings)
        // todo save it?
    }

}