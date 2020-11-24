package com.handcontrol.server.mqtt.command.set

import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.DynamicCommand
import com.handcontrol.server.mqtt.command.MobileWriteApi
import com.handcontrol.server.mqtt.command.dto.gesture.PerformGestureByIdDto
import com.handcontrol.server.mqtt.command.enums.DynamicApi.DynamicTopic.PERFORM_GESTURE_BY_ID
import com.handcontrol.server.protobuf.Gestures
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Perform a gesture by gestures's id
 */
@Component
@ExperimentalSerializationApi
class PerformGestureById(val mqttWrapper: MqttClientWrapper) :
        DynamicCommand(PERFORM_GESTURE_BY_ID), MobileWriteApi<Gestures.PerformGestureById> {

    private val logger = LoggerFactory.getLogger(PerformGestureById::class.java)

    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun writeToProsthesis(id: String, grpcObj: Gestures.PerformGestureById) {
        val dto = PerformGestureByIdDto.createFrom(grpcObj)
        logger.info("Try to send PerformGestureById to id {}: {}", id, dto)

        val byteArray = ProtobufSerializer.serialize(dto)
        val topic = topic.topicName.replace("+", id)
        mqttWrapper.publish(topic, byteArray)
    }

}