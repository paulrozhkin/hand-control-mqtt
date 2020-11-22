package com.handcontrol.server.mqtt.command.set

import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.DynamicCommand
import com.handcontrol.server.mqtt.command.MobileWriteApi
import com.handcontrol.server.mqtt.command.dto.settings.SetSettingsDto
import com.handcontrol.server.mqtt.command.enums.DynamicApi.DynamicTopic.SET_SETTINGS
import com.handcontrol.server.protobuf.Settings
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Change prosthesis configuration
 */
@Component
@ExperimentalSerializationApi
class SetSettings(val mqttWrapper: MqttClientWrapper) :
        DynamicCommand(SET_SETTINGS), MobileWriteApi<Settings.SetSettings> {

    private val logger = LoggerFactory.getLogger(SetSettings::class.java)

    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun writeToProsthesis(id: String, grpcObj: Settings.SetSettings) {
        val dto = SetSettingsDto.createFrom(grpcObj)
        logger.info("Try to send SetSettings to id {}: {}", id, dto)

        val byteArray = ProtobufSerializer.serialize(dto)
        val topic = topic.topicName.replace("+", id)
        mqttWrapper.publish(topic, byteArray)
    }

}