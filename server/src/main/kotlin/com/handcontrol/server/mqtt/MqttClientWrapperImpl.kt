package com.handcontrol.server.mqtt

import com.handcontrol.server.mqtt.command.enums.DynamicApi
import com.handcontrol.server.mqtt.command.enums.DynamicApi.DynamicTopic
import com.handcontrol.server.mqtt.command.enums.StaticApi
import com.handcontrol.server.mqtt.command.enums.TopicMode
import io.netty.handler.codec.mqtt.MqttQoS
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.mqtt.MqttClient
import io.vertx.mqtt.messages.MqttPublishMessage
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
@ExperimentalSerializationApi
class MqttClientWrapperImpl(val mqttProps: MqttProperties) : MqttClientWrapper {
    private val logger = LoggerFactory.getLogger(MqttClientWrapperImpl::class.java)

    @Autowired
    lateinit var dynamicApi: DynamicApi

    @Autowired
    lateinit var staticApi: StaticApi

    private val client = MqttClient.create(Vertx.vertx())
    private lateinit var clientId: String

    @PostConstruct
    fun init() {
        // todo move handler to func and test it
        client.publishHandler { s: MqttPublishMessage ->
            val content = s.payload().bytes
            val topicName = s.topicName()
            logger.debug("Client {} received message from topic {}", clientId, topicName)

            if (staticApi.handle(topicName, content)) {
                return@publishHandler
            }

            val id = topicName.substringBefore('/')
            val topicWithRegex = topicName.replaceBefore('/', "+")
            if (dynamicApi.handle(topicWithRegex, id, content)) {
                return@publishHandler
            }

            val errMsg = "Unknown topic"
            throw IllegalArgumentException(errMsg)
        }
        connect()
    }

    override fun connect() {
        if (client.isConnected) {
            logger.warn("Client {} is already connected to broker", clientId)
            return
        }

        client.connect(mqttProps.brokerPort, mqttProps.brokerAddr) { ch ->
            if (ch.succeeded()) {
                clientId = client.clientId()
                logger.info("Client {} connected to a mqtt broker", clientId)
                StaticApi.StaticTopic.values()
                    .filter { it.mode == TopicMode.READ }
                    .forEach { subscribe(it.topicName) }

                DynamicTopic.values()
                    .filter { it.mode == TopicMode.READ }
                    .forEach { subscribe(it.topicName) }
            } else {
                logger.error(
                    "Client {} failed to connect to a mqtt broker. Reason: {}",
                    clientId, ch.cause().message
                )
            }
        }
    }

    override fun disconnect() {
        client.disconnect { dh ->
            if (dh.succeeded()) {
                logger.info("Client {} disconnected from a mqtt broker", clientId)
            } else {
                logger.error(
                    "Client {} failed to disconnect from a mqtt broker. Reason: {}",
                    clientId, dh.cause().message
                )
            }
        }
    }

    override fun subscribe(topic: String) {
        client.subscribe(topic, mqttProps.qosLevel) { sh ->
            if (sh.succeeded()) {
                logger.info("Client {} subscribed to topic {}", clientId, topic)
            } else {
                logger.error(
                    "Client {} failed to subscribe to topic: {}. Reason: {}",
                    clientId, topic, sh.cause().message
                )
            }
        }
    }

    override fun unsubscribe(topic: String) {
        client.unsubscribe(topic) { sh ->
            if (sh.succeeded()) {
                logger.info("Client {} unsubscribed to topic {}", clientId, topic)
            } else {
                logger.error(
                    "Client {} failed to unsubscribe to topic: {}. Reason: {}",
                    clientId, topic, sh.cause().message
                )
            }
        }
    }

    override fun publish(topic: String, msgBytes: ByteArray) {
        client.publish(
            topic, Buffer.buffer(msgBytes),
            MqttQoS.valueOf(mqttProps.qosLevel), false, false
        ) { sh ->
            if (sh.succeeded()) {
                logger.info("Client {} published msg to topic {}", clientId, topic)
            } else {
                logger.error(
                    "Client {} failed to publish msg to topic: {}. Reason: {}",
                    clientId, topic, sh.cause().message
                )
            }
        }
    }

    override fun publish(topic: String, msg: String) {
        publish(topic, msg.toByteArray())
    }

}