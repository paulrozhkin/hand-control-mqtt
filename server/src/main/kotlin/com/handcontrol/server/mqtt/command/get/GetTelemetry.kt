package com.handcontrol.server.mqtt.command.get

import com.handcontrol.server.mqtt.command.DynamicCommand
import com.handcontrol.server.mqtt.command.dto.TelemetryDto
import com.handcontrol.server.mqtt.command.enums.DynamicApi.DynamicTopic.GET_TELEMETRY
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 *  Prosthesis telemetry - contains all the actual information about the prosthesis.
 *  Updates every second
 */
@Component
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class GetTelemetry : DynamicCommand(GET_TELEMETRY) {
    private val logger = LoggerFactory.getLogger(GetTelemetry::class.java)

    private val subscribers = mutableMapOf<String, Channel<TelemetryDto>>()

    override fun handlePayloadAndId(id: String, byteArray: ByteArray) {
        val telemetry = ProtobufSerializer.deserialize<TelemetryDto>(byteArray)
        logger.debug("Get telemetry from {}: {}", id, telemetry)

        val sub = subscribers[id]
        if (sub == null) {
            logger.debug("No subscribers for telemetry from id: {}", id)
            return
        }

        runBlocking {
            sub.send(telemetry)
        }
    }

    fun subscribe(id: String) {
        subscribers[id] = Channel(CONFLATED)
    }

    @FlowPreview
    fun getChannel(id: String): Channel<TelemetryDto> {
        return subscribers[id]!!
    }
}