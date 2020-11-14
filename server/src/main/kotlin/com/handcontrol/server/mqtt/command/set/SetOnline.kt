package com.handcontrol.server.mqtt.command.set

import com.handcontrol.server.cache.ProsthesisCache
import com.handcontrol.server.mqtt.command.StaticCommand
import com.handcontrol.server.mqtt.command.enums.ApiMqttStaticTopic
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory

/**
 *  Waiting for prosthesis id -> mark the prosthesis as active.
 *  Required update every 60 sec, else changed the prosthesis state as inactive.
 */
@ExperimentalSerializationApi
object SetOnline : StaticCommand(ApiMqttStaticTopic.SET_ONLINE) {
    private val logger = LoggerFactory.getLogger(SetOnline::class.java)

    override fun handlePayload(byteArray: ByteArray) {
        val id = ProtobufSerializer.deserialize<String>(byteArray)
        ProsthesisCache.addActiveState(id)
    }

}