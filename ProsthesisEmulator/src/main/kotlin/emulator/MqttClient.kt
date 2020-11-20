package emulator

import emulator.models.MqttDataModel
import emulator.models.Topics
import io.netty.handler.codec.mqtt.MqttQoS
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.mqtt.MqttClient
import io.vertx.mqtt.MqttClientOptions
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import kotlin.concurrent.timer

@ExperimentalUnsignedTypes
class MqttClient : io.vertx.core.AbstractVerticle() {
    private companion object {
        const val BROKER_HOST = "localhost"
        const val BROKER_PORT = 1883
        const val ONLINE_TIMEOUT_MS: Long = 60000
    }

    private val logger: Logger = LogManager.getLogger(MqttClient::class.java.name)

    private val clientId = UUID.randomUUID().toString()

    private val client: MqttClient = MqttClient.create(
            Vertx.vertx(),
            MqttClientOptions()
                    .setUsername("controllers")
                    .setPassword("controllers")
                    .setClientId(clientId)
                    .setWillFlag(true)
                    .setWillQoS(2)
                    .setWillRetain(true)
                    .setWillTopic(Topics.Offline)
                    .setWillMessage(clientId)
    )

    /**
     * Rx PublishSubject for received data.
     */
    private val dataSubject: PublishSubject<MqttDataModel> = PublishSubject.create()

    /**
     * Rx PublishSubject for connection status.
     */
    private val isConnectedSubject: PublishSubject<Boolean> = PublishSubject.create()

    /**
     * Observable for received data.
     */
    fun getDataObservable(): Observable<MqttDataModel> {
        return dataSubject.share()
    }

    /**
     * Observable for connection status.
     */
    fun getIsConnectedObservable(): Observable<Boolean> {
        return isConnectedSubject.share()
    }

    /**
     * Connect to the broker.
     */
    override fun start() {

        // handler will be called when we have a message in topic we subscribing for
        client.publishHandler { publish ->

            logger.info("Received message on [${publish.topicName()}], payload [${publish.payload().bytes.size} bytes], QoS [${publish.qosLevel()}]")

            val mqttData = MqttDataModel(publish.topicName().removePrefix("$clientId/"), publish.payload().bytes)
            dataSubject.onNext(mqttData)
        }

        // handle response on subscribe request
        client.subscribeCompletionHandler { h ->
            logger.info("Receive SUBACK from server with granted QoS : ${h.grantedQoSLevels()}")
        }

        // handle response on unsubscribe request
        client.unsubscribeCompletionHandler {
            logger.info("Receive UNSUBACK from server")
        }

        // connect to a server
        client.connect(BROKER_PORT, BROKER_HOST) { ch ->
            if (ch.succeeded()) {
                logger.info("Connected to a server")
                client.subscribe("controllers", 2)
                isConnectedSubject.onNext(true)

                // Отправляем сообщения, чтобы эмулятор мог быть обнаружен
                timer(null, true, 0, ONLINE_TIMEOUT_MS) {
                    sendData(Topics.SetOnline, clientId, true)
                }
            } else {
                logger.error("Failed to connect to a server")
                logger.error(ch.cause())
                isConnectedSubject.onError(ch.cause())
            }
        }
    }

    /**
     * Subscribe to MQTT topic.
     */
    fun subscribe(topic: String, QoS: Int = 2, topicWithoutId: Boolean = false) {
        val vertxTopic = getTopic(topic, topicWithoutId)
        client.subscribe(vertxTopic, QoS)
    }

    /**
     * Send a binary stream to topic.
     */
    fun sendData(topic: String, data: ByteArray, topicWithoutId: Boolean = false) {
        val vertxTopic = getTopic(topic, topicWithoutId)

        val vertxData = Buffer.buffer(data)
        sendData(vertxTopic, vertxData)
    }

    /**
     * Send a text message to a topic.
     */
    fun sendData(topic: String, message: String, topicWithoutId: Boolean = false) {
        val vertxTopic = getTopic(topic, topicWithoutId)

        val vertxData = Buffer.buffer(message)
        sendData(vertxTopic, vertxData)
    }

    private fun sendData(topic: String, vertxData: Buffer) {
        if (!client.isConnected) {
            throw RuntimeException("Client not connected")
        }

        client.publish(topic, vertxData, MqttQoS.EXACTLY_ONCE, true, true) { s ->
            if (s.failed()) {
                logger.error("Publish sent error to a server on $topic. Exception ${s.cause()}")
            } else {
                logger.info("Publish sent ${vertxData.bytes.size} bytes to a server on $topic")
            }
        }
    }

    private fun getTopic(topic: String, topicWithoutId: Boolean = false): String {
        return if (!topicWithoutId) {
            "$clientId/$topic"
        } else {
            topic
        }
    }
}