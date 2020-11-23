package com.handcontrol.server.mqtt.command.get

import com.handcontrol.server.mqtt.command.DynamicCommand
import com.handcontrol.server.mqtt.command.dto.TelemetryDto
import com.handcontrol.server.mqtt.command.enums.DynamicApi.DynamicTopic.GET_TELEMETRY
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 *  Prosthesis telemetry - contains all the actual information about the prosthesis.
 *  Updates every second
 */
@Component
@ExperimentalSerializationApi
class GetTelemetry : DynamicCommand(GET_TELEMETRY) {
    private val logger = LoggerFactory.getLogger(GetTelemetry::class.java)

    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        val telemetry = ProtobufSerializer.deserialize<TelemetryDto>(byteArray)
        logger.debug("Get telemetry from {}: {}", id, telemetry)
        // todo save it?
    }

}