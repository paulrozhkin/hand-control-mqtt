package com.handcontrol.server.mqtt.command.get

import com.handcontrol.server.mqtt.command.DynamicCommand
import com.handcontrol.server.mqtt.command.dto.gesture.GetGesturesDto
import com.handcontrol.server.mqtt.command.enums.DynamicApi.DynamicTopic.GET_GESTURES
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 *  Get actual gestures from a prosthesis
 */
@Component
@ExperimentalSerializationApi
class GetGestures : DynamicCommand(GET_GESTURES) {
    private val logger = LoggerFactory.getLogger(GetGestures::class.java)

    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        val gestures = ProtobufSerializer.deserialize<GetGesturesDto>(byteArray)
        logger.debug("Get gestures from {}: {}", id, gestures)
        // todo save it?
    }

}