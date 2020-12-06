package com.handcontrol.server.mqtt.command.get

import com.handcontrol.server.mqtt.command.StaticCommand
import com.handcontrol.server.mqtt.command.enums.StaticApi
import com.handcontrol.server.service.ProthesisService
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *  Waiting for prosthesis id -> mark the prosthesis as inactive (due to loss of connection).
 */
@Component
@ExperimentalSerializationApi
class GetOffline : StaticCommand(StaticApi.StaticTopic.GET_OFFLINE) {
    private val logger = LoggerFactory.getLogger(GetOffline::class.java)

    @Autowired
    private lateinit var svc: ProthesisService

    override fun handlePayload(byteArray: ByteArray) {
        val id = String(byteArray)
        logger.debug("Prosthesis {} is lost the connection", id)
        svc.setOffline(id)
        logger.debug("Prosthesis {} is offline", id)
    }

}