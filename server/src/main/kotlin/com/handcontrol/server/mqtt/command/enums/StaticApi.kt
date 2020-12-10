package com.handcontrol.server.mqtt.command.enums

import com.handcontrol.server.mqtt.command.StaticCommand
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * A prosthesis API as mqtt topics
 *
 * @see <a href="https://github.com/paulrozhkin/handcontrol-documentation/blob/master/api.md">API Description</a>
 */
@Service
@ExperimentalSerializationApi
class StaticApi {

    private val logger = LoggerFactory.getLogger(StaticApi::class.java)

    @Autowired
    lateinit var commands: List<StaticCommand>

    fun handle(topicName: String, payload: ByteArray) : Boolean {
        val topic = StaticTopic.getByName(topicName)
        if (topic == null) {
            logger.debug("Unknown static mqtt topic: {}.", topicName)
            return false
        }

        val command = commands.find { it.topic == topic }
        command?.handlePayload(payload)
        return true
    }

    enum class StaticTopic(val topicName: String, val mode: TopicMode) {
        GET_ONLINE("controllers/online", TopicMode.READ),
        GET_OFFLINE("controllers/offline", TopicMode.READ);

        companion object {
            fun getByName(name: String): StaticTopic? {
                return values().find { name == it.topicName }
            }
        }
    }
}
