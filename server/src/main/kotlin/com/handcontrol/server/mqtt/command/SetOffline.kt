package com.handcontrol.server.mqtt.command

import com.handcontrol.server.mqtt.command.dto.Id
import com.handcontrol.server.mqtt.command.enums.ApiMqttStaticTopic
import com.handcontrol.server.util.ObjectSerializer
import org.slf4j.LoggerFactory

/**
 *  Waiting for prosthesis id -> mark the prosthesis as inactive (due to loss of connection).
 */
object SetOffline : StaticCommand(ApiMqttStaticTopic.SET_OFFLINE) {
    private val logger = LoggerFactory.getLogger(SetOffline::class.java)

    // todo move to more suitable class: id + state
    var pActive = hashMapOf<Id, Boolean>()

    override fun handlePayload(byteArray: ByteArray) {
        val key = ObjectSerializer.deserialize<Id>(byteArray)
        pActive[key] = false
        logger.debug("Prosthesis {} is offline (connection lost)", key.id)
    }

}