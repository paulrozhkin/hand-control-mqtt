package com.handcontrol.server.mqtt.command

import com.handcontrol.server.mqtt.command.dto.Id
import com.handcontrol.server.mqtt.command.enums.ApiMqttStaticTopic
import com.handcontrol.server.util.ObjectSerializer
import org.slf4j.LoggerFactory

/**
 *  Waiting for prosthesis id -> mark the prosthesis as active.
 *  Required update every 60 sec, else changed the prosthesis state as inactive.
 */
@ExperimentalUnsignedTypes
object SetOnline : StaticCommand(ApiMqttStaticTopic.SET_ONLINE) {
    private val logger = LoggerFactory.getLogger(SetOnline::class.java)

    // todo move to more suitable class: id + state
    var pActive = hashMapOf<Id, Boolean>()

    override fun handlePayload(byteArray: ByteArray) {
        val key = ObjectSerializer.deserialize<Id>(byteArray)
        pActive[key] = true
        logger.debug("Prosthesis {} is online", key.id)
        //todo add coroutine for change state to false after 60 sec
    }

}