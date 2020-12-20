package com.handcontrol.server.mqtt.command.set

import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.DynamicCommand
import com.handcontrol.server.mqtt.command.MobileWriteApi
import com.handcontrol.server.mqtt.command.dto.gesture.SaveGestureDto
import com.handcontrol.server.mqtt.command.enums.DynamicApi.DynamicTopic.SAVE_GESTURE
import com.handcontrol.server.protobuf.Gestures
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Save or update a gesture
 */
@Service
@ExperimentalSerializationApi
class SaveGesture : DynamicCommand(SAVE_GESTURE), MobileWriteApi<Gestures.SaveGesture> {
    private val logger = LoggerFactory.getLogger(SaveGesture::class.java)

    @Autowired
    private lateinit var mqttWrapper: MqttClientWrapper

    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun writeToProsthesis(id: String, grpcObj: Gestures.SaveGesture) {
        val dto = SaveGestureDto.createFrom(grpcObj)
        logger.info("Try to send SaveGesture to id {}: {}", id, dto)

        val byteArray = ProtobufSerializer.serialize(dto)
        val topic = topic.topicName.replace("+", id)
        mqttWrapper.publish(topic, byteArray)
    }

}