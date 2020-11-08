package com.handcontrol.server.mqtt.command

import com.handcontrol.server.mqtt.command.dto.Id
import com.handcontrol.server.mqtt.command.enums.ApiMqttTopic

/**
 *  Waiting for prosthesis id -> mark the prosthesis as active.
 *  Required update every 60 sec, else changed the prosthesis state as inactive.
 */
object SetOnline : Command(ApiMqttTopic.SET_ONLINE) {

    // todo move to more suitable class: id + state
    var pActive = hashMapOf<Id, Boolean>()

    override fun handlePayload(byteArray: ByteArray) {
        val id = String(byteArray)

        println("msg " + id)
        TODO("Not yet implemented")
    }

}