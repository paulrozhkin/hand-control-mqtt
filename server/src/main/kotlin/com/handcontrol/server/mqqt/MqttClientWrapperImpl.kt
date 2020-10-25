package com.handcontrol.server.mqqt

import io.netty.handler.codec.mqtt.MqttQoS
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.mqtt.MqttClient
import io.vertx.mqtt.messages.MqttPublishMessage
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class MqttClientWrapperImpl(private val mqttProps: MqttProperties) : MqttClientWrapper {
    private val logger = LoggerFactory.getLogger(MqttClientWrapperImpl::class.java)

    private val client: MqttClient = MqttClient.create(Vertx.vertx())

    @PostConstruct
    fun init() {
        client.publishHandler { s: MqttPublishMessage ->
            val message = String(s.payload().bytes)
            logger.info("Receive message with content: \"{}\" from topic {}", message, s.topicName())
            // handle it
        }

        connect()
    }

    override fun connect() {
        if (client.isConnected) {
            logger.warn("Client is already connected to broker")
            return
        }

        client.connect(mqttProps.brokerPort, mqttProps.brokerAddr) { ch ->
            if (ch.succeeded()) {
                logger.info("Connected to a mqtt broker")
                subscribe("hello/world")
                publish("hello/world", "I'm here")
                // subscribe to necessary topics
            } else {
                logger.error("Failed to connect to a mqtt broker. Reason: {}", ch.cause().message)
            }
        }
    }

    override fun disconnect() {
        logger.info("Disconnected from a mqtt broker")
        client.disconnect();
    }

    override fun subscribe(topic: String) {
        client.subscribe(topic, mqttProps.qosLevel) { ch ->
            if (ch.succeeded()) {
                logger.info("Subscribe to topic {}", topic)
            } else {
                logger.error("Failed to subscribe to topic: {}. Reason: {}", topic, ch.cause().message)
            }
        }
    }

    override fun unsubscribe(topic: String) {
        client.unsubscribe(topic) { ch ->
            if (ch.succeeded()) {
                logger.info("Unsubscribe to topic {}", topic)
            } else {
                logger.error("Failed to unsubscribe to topic: {}. Reason: {}", topic, ch.cause().message)
            }
        }
    }

    override fun publish(topic: String, msg: String) {
        client.publish(topic, Buffer.buffer(msg),
                MqttQoS.valueOf(mqttProps.qosLevel), false, false) { ch ->
            if (ch.succeeded()) {
                logger.info("Publish msg \"{}\" to topic {}", msg, topic)
            } else {
                logger.error("Failed to publish msg to topic: {}. Reason: {}", topic, ch.cause().message)
            }
        }
    }

}