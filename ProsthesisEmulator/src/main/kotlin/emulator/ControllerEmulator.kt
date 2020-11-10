package emulator

import emulator.models.GetSettings
import emulator.models.MqttDataModel
import emulator.models.SetSettings
import emulator.models.Topics
import emulator.models.enums.TypeWork
import io.reactivex.rxjava3.kotlin.subscribeBy
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.lang.Exception

/**
 * Bionic hand prosthesis emulator. Supports the prosthesis protocol and sends data that emulates the prosthesis.
 */
@ExperimentalUnsignedTypes
class ControllerEmulator() {
    private val client: MqttClient = MqttClient()
    private val logger: Logger = LogManager.getLogger(ControllerEmulator::class.java.name)

    private var isConnected: Boolean = false

    private lateinit var currentSettings: SetSettings

    init {
        initializeSettings()
    }

    /**
     * Launching the emulator. Connects to the Mqtt broker and starts receiving messages.
     */
    fun start() {
        // Subscription to receive data
        client.getDataObservable().subscribeBy(onNext = {
            this.receiveDataHandler(it)
        }, onError = {
            logger.error(it.message)
        }, onComplete = {
            logger.info("DataObservable complete")
        })

        // Connection status subscription
        client.getIsConnectedObservable().subscribeBy(onNext = {
            isConnected = true
            subscribeTopics()
            initializeTopics()
        }, onError = {
            logger.error(it.message)
        }, onComplete = {
            logger.info("IsConnectedObservable complete")
        })

        client.start()
    }

    private fun initializeTopics() {
        // Send settings
        val newSettings = GetSettings(currentSettings.typeWork, currentSettings.enableEmg,
                currentSettings.enableDisplay, currentSettings.enableGyro,
                currentSettings.enableDriver)

        client.sendData(Topics.GetSettings, newSettings.serialize())
    }

    private fun subscribeTopics() {
        client.subscribe(Topics.SetSettings)
        client.subscribe(Topics.SaveGesture)
        client.subscribe(Topics.DeleteGesture)
        client.subscribe(Topics.PerformGestureId)
        client.subscribe(Topics.PerformGestureRaw)
        client.subscribe(Topics.SetPositions)
    }

    private fun initializeSettings() {
        currentSettings = SetSettings(TypeWork.AUTO, 1u, enableEmg = true, enableDisplay = true, enableGyro = true, enableDriver = true)
    }

    /**
     * Emulation of processing input data on the prosthesis.
     */
    private fun receiveDataHandler(data: MqttDataModel) {
        logger.info("receiveDataHandler start on topic [${data.topic}] and data [${data.data.size} bytes]")

        try {
            when (data.topic) {
                Topics.SetSettings -> {
                    updateSettings(data.data)
                }
                Topics.SaveGesture -> {

                }
                Topics.DeleteGesture -> {

                }
                Topics.PerformGestureId -> {

                }
                Topics.PerformGestureRaw -> {

                }
                Topics.SetPositions -> {

                }
                else -> {
                    logger.warn("Topic ${data.topic} not supported.")
                }
            }
        } catch (exception: Exception) {
            logger.error(exception)
        }
    }

    private fun updateSettings(settingsPayload: UByteArray) {
        val settings = SetSettings.deserialize(settingsPayload)
        currentSettings = settings
        val newSettings = GetSettings(currentSettings.typeWork, currentSettings.enableEmg,
                currentSettings.enableDisplay, currentSettings.enableGyro,
                currentSettings.enableDriver)

        client.sendData(Topics.GetSettings, newSettings.serialize())
    }
}