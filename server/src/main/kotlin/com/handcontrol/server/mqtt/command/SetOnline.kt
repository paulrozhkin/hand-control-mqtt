package com.handcontrol.server.mqtt.command

import com.handcontrol.server.mqtt.command.dto.Id
import com.handcontrol.server.mqtt.command.enums.ApiMqttTopic
import com.handcontrol.server.util.ObjectSerializer

/**
 *  Waiting for prosthesis id -> mark the prosthesis as active.
 *  Required update every 60 sec, else changed the prosthesis state as inactive.
 */
object SetOnline : Command(ApiMqttTopic.SET_ONLINE) {

    // todo move to more suitable class: id + state
    var pActive = hashMapOf<Id, Boolean>()

    override fun handlePayload(byteArray: ByteArray) {
        val key = ObjectSerializer.deserialize<Id>(byteArray)
        pActive[key] = true
        //todo add coroutine for change state to false after 60 sec
    }

}