package com.handcontrol.server.mqtt.command.set

import com.handcontrol.server.cache.ProsthesisCache
import com.handcontrol.server.mqtt.command.StaticCommand
import com.handcontrol.server.mqtt.command.enums.ApiMqttStaticTopic
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory

/**
 *  Waiting for prosthesis id -> mark the prosthesis as inactive (due to loss of connection).
 */
@ExperimentalSerializationApi
object GetOffline : StaticCommand(ApiMqttStaticTopic.GET_OFFLINE) {
    private val logger = LoggerFactory.getLogger(GetOffline::class.java)

    override fun handlePayload(byteArray: ByteArray) {
        val id = ProtobufSerializer.deserialize<String>(byteArray)
        logger.debug("Prosthesis {} is lost the connection", id)
        ProsthesisCache.addInactiveState(id)
        // todo move to redis
    }

}