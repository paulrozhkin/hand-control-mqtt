package com.handcontrol.server.mqtt.command.get

import com.handcontrol.server.mqtt.command.DynamicCommand
import com.handcontrol.server.mqtt.command.enums.ApiMqttDynamicTopic
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory

/**
 *  Get actual gestures from a prosthesis
 */
@ExperimentalSerializationApi
object GetGestures : DynamicCommand(ApiMqttDynamicTopic.GET_GESTURES) {
    private val logger = LoggerFactory.getLogger(GetGestures::class.java)

    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        val gestures = ProtobufSerializer.deserialize<GetGestures>(byteArray)
        logger.debug("Get gestures from {}: {}", id, gestures)
        // todo save it?
    }

}