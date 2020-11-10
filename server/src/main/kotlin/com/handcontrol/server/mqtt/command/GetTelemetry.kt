package com.handcontrol.server.mqtt.command

import com.handcontrol.server.mqtt.command.dto.Telemetry
import com.handcontrol.server.mqtt.command.enums.ApiMqttDynamicTopic
import com.handcontrol.server.util.ObjectSerializer
import org.slf4j.LoggerFactory

/**
 *  Prosthesis telemetry - contain all the actual information about the prosthesis.
 *  Updates every second
 */
object GetTelemetry : DynamicCommand(ApiMqttDynamicTopic.GET_TELEMETRY) {
    private val logger = LoggerFactory.getLogger(GetTelemetry::class.java)

    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        val telemetry = ObjectSerializer.deserialize<Telemetry>(byteArray)
        logger.debug("Get telemetry from {id}: {}", telemetry)
        // todo save it?
    }

}