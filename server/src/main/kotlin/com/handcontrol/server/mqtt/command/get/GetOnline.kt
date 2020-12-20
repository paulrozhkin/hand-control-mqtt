package com.handcontrol.server.mqtt.command.get

import com.handcontrol.server.mqtt.command.StaticCommand
import com.handcontrol.server.mqtt.command.enums.StaticApi
import com.handcontrol.server.service.ProthesisService
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *  Waiting for prosthesis id -> mark the prosthesis as active.
 *  Required update every 60 sec, else changed the prosthesis state as inactive.
 */
@Component
@ExperimentalSerializationApi
class GetOnline : StaticCommand(StaticApi.StaticTopic.GET_ONLINE) {
    private val logger = LoggerFactory.getLogger(GetOnline::class.java)

    @Autowired
    private lateinit var svc: ProthesisService

    override fun handlePayload(byteArray: ByteArray) {
        val id = String(byteArray)
        val upd = svc.setOnline(id)
        logger.info("Update and save prosthesis state: {}", upd)
        logger.debug("Prosthesis {} is online", id)
    }

}