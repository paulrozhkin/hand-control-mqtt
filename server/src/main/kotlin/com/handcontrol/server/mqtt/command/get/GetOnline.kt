package com.handcontrol.server.mqtt.command.get

import com.handcontrol.server.cache.ProsthesisCache
import com.handcontrol.server.mqtt.command.StaticCommand
import com.handcontrol.server.mqtt.command.enums.StaticApi
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 *  Waiting for prosthesis id -> mark the prosthesis as active.
 *  Required update every 60 sec, else changed the prosthesis state as inactive.
 */
@Component
@ExperimentalSerializationApi
class GetOnline : StaticCommand(StaticApi.StaticTopic.GET_ONLINE) {
    private val logger = LoggerFactory.getLogger(GetOnline::class.java)

    override fun handlePayload(byteArray: ByteArray) {
        val id = String(byteArray)
        ProsthesisCache.addActiveState(id)
        // todo move to redis
    }

}