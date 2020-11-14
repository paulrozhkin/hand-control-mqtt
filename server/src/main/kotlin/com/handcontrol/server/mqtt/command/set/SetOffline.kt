package com.handcontrol.server.mqtt.command.set

import com.handcontrol.server.mqtt.command.StaticCommand
import com.handcontrol.server.mqtt.command.enums.ApiMqttStaticTopic
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory

/**
 *  Waiting for prosthesis id -> mark the prosthesis as inactive (due to loss of connection).
 */
@ExperimentalSerializationApi
object SetOffline : StaticCommand(ApiMqttStaticTopic.SET_OFFLINE) {
    private val logger = LoggerFactory.getLogger(SetOffline::class.java)

    // todo move to more suitable class: id + state
    var pActive = hashMapOf<String, Boolean>()

    override fun handlePayload(byteArray: ByteArray) {
        val key = ProtobufSerializer.deserialize<String>(byteArray)
        pActive[key] = false
        logger.debug("Prosthesis {} is offline (connection lost)", key)
    }

}