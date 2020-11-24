package com.handcontrol.server.mqtt.command.set

import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.DynamicCommand
import com.handcontrol.server.mqtt.command.MobileWriteApi
import com.handcontrol.server.mqtt.command.dto.gesture.DeleteGestureDto
import com.handcontrol.server.mqtt.command.enums.DynamicApi.DynamicTopic.DELETE_GESTURE
import com.handcontrol.server.protobuf.Gestures
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Delete a gesture
 */
@Component
@ExperimentalSerializationApi
class DeleteGesture(val mqttWrapper: MqttClientWrapper) :
        DynamicCommand(DELETE_GESTURE), MobileWriteApi<Gestures.DeleteGesture> {

    private val logger = LoggerFactory.getLogger(DeleteGesture::class.java)

    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun writeToProsthesis(id: String, grpcObj: Gestures.DeleteGesture) {
        val dto = DeleteGestureDto.createFrom(grpcObj)
        logger.info("Try to send DeleteGesture to id {}: {}", id, dto)

        val byteArray = ProtobufSerializer.serialize(dto)
        val topic = topic.topicName.replace("+", id)
        mqttWrapper.publish(topic, byteArray)
    }

}