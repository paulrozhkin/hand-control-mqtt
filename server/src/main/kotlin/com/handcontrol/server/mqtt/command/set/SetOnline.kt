package com.handcontrol.server.mqtt.command.set

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

    // todo move to more suitable class: id + state
    var pActive = hashMapOf<String, Boolean>()

    override fun handlePayload(byteArray: ByteArray) {
        val key = ProtobufSerializer.deserialize<String>(byteArray)
        pActive[key] = true
        logger.debug("Prosthesis {} is online", key)
        //todo add coroutine for change state to false after 60 sec
    }

}