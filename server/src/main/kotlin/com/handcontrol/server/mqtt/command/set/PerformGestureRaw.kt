package com.handcontrol.server.mqtt.command.set

import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.DynamicCommand
import com.handcontrol.server.mqtt.command.MobileWriteApi
import com.handcontrol.server.mqtt.command.dto.gesture.PerformGestureRawDto
import com.handcontrol.server.mqtt.command.enums.DynamicApi.DynamicTopic.PERFORM_GESTURE_RAW
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
class PerformGestureRaw(val mqttWrapper: MqttClientWrapper) :
        DynamicCommand(PERFORM_GESTURE_RAW), MobileWriteApi<Gestures.PerformGestureRaw> {

    private val logger = LoggerFactory.getLogger(PerformGestureRaw::class.java)

    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun writeToProsthesis(id: String, grpcObj: Gestures.PerformGestureRaw) {
        val dto = PerformGestureRawDto.createFrom(grpcObj)
        logger.info("Try to send PerformGestureRaw to id {}: {}", id, dto)

        val byteArray = ProtobufSerializer.serialize(dto)
        val topic = topic.topicName.replace("+", id)
        mqttWrapper.publish(topic, byteArray)
    }

}