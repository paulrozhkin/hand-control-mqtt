package com.handcontrol.server.mqtt

import com.handcontrol.server.mqtt.command.MobileWriteApi
import com.handcontrol.server.mqtt.command.set.DeleteGesture
import com.handcontrol.server.mqtt.command.set.SetSettings
import kotlinx.serialization.ExperimentalSerializationApi
import org.springframework.stereotype.Service

@Service
@ExperimentalSerializationApi
class TestService(val lst: List<MobileWriteApi<*>>) {

    fun testSetSettings() {
        val command = lst.find { it is SetSettings }
//        command?.writeToProsthesis(id, grpcObj)
    }

    fun testDeleteGesture() {
        val command = lst.find { it is DeleteGesture }
//        command?.writeToProsthesis(id, grpcObj)
    }

}