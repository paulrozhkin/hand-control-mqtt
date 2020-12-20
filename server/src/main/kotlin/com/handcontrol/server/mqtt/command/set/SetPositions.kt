package com.handcontrol.server.mqtt.command.set

import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.DynamicCommand
import com.handcontrol.server.mqtt.command.MobileWriteApi
import com.handcontrol.server.mqtt.command.dto.gesture.SetPositionsDto
import com.handcontrol.server.mqtt.command.enums.DynamicApi.DynamicTopic.SET_POSITIONS
import com.handcontrol.server.protobuf.Gestures
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Change prosthesis position
 */
@Component
@ExperimentalSerializationApi
class SetPositions: DynamicCommand(SET_POSITIONS), MobileWriteApi<Gestures.SetPositions> {
    private val logger = LoggerFactory.getLogger(SetPositions::class.java)

    @Autowired
    private lateinit var mqttWrapper: MqttClientWrapper

    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun writeToProsthesis(id: String, grpcObj: Gestures.SetPositions) {
        val dto = SetPositionsDto.createFrom(grpcObj)
        logger.info("Try to send SetPositions to id {}: {}", id, dto)

        val byteArray = ProtobufSerializer.serialize(dto)
        val topic = topic.topicName.replace("+", id)
        mqttWrapper.publish(topic, byteArray)

    }

}