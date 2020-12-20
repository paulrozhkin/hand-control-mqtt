package emulator

import emulator.dto.TelemetryDto
import emulator.dto.UuidDto
import emulator.dto.enums.DriverStatusType
import emulator.dto.enums.ModeType
import emulator.dto.enums.ModuleStatusType
import emulator.dto.gesture.DeleteGestureDto
import emulator.dto.gesture.GestureDto
import emulator.dto.gesture.GetGesturesDto
import emulator.dto.gesture.SaveGestureDto
import emulator.dto.settings.GetSettingsDto
import emulator.dto.settings.SetSettingsDto
import emulator.models.MqttDataModel
import emulator.models.Topics
import emulator.util.ProtobufSerializer
import emulator.util.StringGenerator
import emulator.util.TelemetryToMsConverter
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.serialization.ExperimentalSerializationApi
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

/**
 * Bionic hand prosthesis emulator. Supports the prosthesis protocol and sends data that emulates the prosthesis.
 */
@ExperimentalUnsignedTypes
@ExperimentalSerializationApi
class ControllerEmulator {
    private val client: MqttClient = MqttClient()
    private val logger: Logger = LogManager.getLogger(ControllerEmulator::class.java.name)

    private var isConnected: Boolean = false

    private lateinit var currentSettings: SetSettingsDto
    private lateinit var telemetryTimer: Timer
    private lateinit var telemetryResetTimer: Timer
    private var executableGesture: UuidDto = UuidDto("")
    private val gestures: MutableList<GestureDto> = mutableListOf()

    init {
        initializeSettings()
        initializeGestures()
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
            if (it) {
                isConnected = true
                subscribeTopics()
                initializeTopics()
                startTelemetry()
            } else {
                stopTelemetry()
            }
        }, onError = {
            logger.error(it.message)
        }, onComplete = {
            logger.info("IsConnectedObservable complete")
        })

        client.start()
    }

    private fun startTelemetry() {
        telemetryTimer = timer(null, true,
            0, TelemetryToMsConverter.convert(currentSettings.telemetryFrequency)) {
            sendTelemetry()
        }
    }

    private fun stopTelemetry() {
        telemetryTimer.cancel()
    }

    private fun sendTelemetry() {
        val telemetry = TelemetryDto(
            telemetryFrequency = currentSettings.telemetryFrequency,
            emgStatus = ModuleStatusType.MODULE_STATUS_ERROR,
            displayStatus = ModuleStatusType.MODULE_STATUS_ERROR,
            gyroStatus = ModuleStatusType.MODULE_STATUS_ERROR,
            driverStatus = DriverStatusType.DRIVER_STATUS_ERROR,
            lastTimeSync = 0,
            emg = (0..4095).random().toShort(),
            executableGesture = executableGesture,
            power = 100,
            pointerFingerPosition = 127,
            middleFingerPosition = 127,
            ringFinderPosition = 127,
            littleFingerPosition = 127,
            thumbFingerPosition = 127
        )

        client.sendData(Topics.Telemetry, ProtobufSerializer.serialize(telemetry))
    }

    private fun initializeTopics() {
        // Send settings
        sendCurrentSettings()

        // Send gestures
        sendGestures()
    }

    private fun sendCurrentSettings() {
        val newSettings = GetSettingsDto(
            currentSettings.typeWork, currentSettings.enableEmg,
            currentSettings.enableDisplay, currentSettings.enableGyro,
            currentSettings.enableDriver
        )

        client.sendData(Topics.GetSettings, ProtobufSerializer.serialize(newSettings))
    }

    private fun sendGestures() {
        val getGestures = GetGesturesDto(lastTimeSync = 0, listGestures = gestures)
        client.sendData(Topics.GetGestures, ProtobufSerializer.serialize(getGestures))
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
        currentSettings = SetSettingsDto(
            ModeType.MODE_AUTO, 1,
            enableEmg = true, enableDisplay = true,
            enableGyro = true, enableDriver = true
        )
    }

    private fun initializeGestures() {
        val gesture1 = GestureDto(
            UuidDto(UUID.randomUUID().toString()),
            name = StringGenerator.generate((5..10).random()),
            lastTimeSync = 0, iterableGesture = false, numberOfGestureRepetitions = 1
        )

        val gesture2 = GestureDto(
            UuidDto(UUID.randomUUID().toString()),
            name = StringGenerator.generate((5..10).random()),
            lastTimeSync = 0, iterableGesture = false, numberOfGestureRepetitions = 1
        )

        gestures.add(gesture1)
        gestures.add(gesture2)
    }

    /**
     * Emulation of processing input data on the prosthesis.
     */
    private fun receiveDataHandler(data: MqttDataModel) {
        logger.info("receiveDataHandler start on topic [${data.topic}] and data [${data.data.size} bytes]")

        try {
            when (data.topic) {
                Topics.SetSettings -> {
                    updateSettings(ProtobufSerializer.deserialize(data.data))
                }
                Topics.SaveGesture -> {
                    saveGesture(ProtobufSerializer.deserialize(data.data))
                }
                Topics.DeleteGesture -> {
                    deleteGesture(ProtobufSerializer.deserialize(data.data))
                }
                Topics.PerformGestureId -> {
                    // Emulator can't perform gesture
                }
                Topics.PerformGestureRaw -> {
                    // Emulator can't perform gesture
                }
                Topics.SetPositions -> {
                    // Emulator can't set positions
                }
                else -> {
                    logger.warn("Topic ${data.topic} not supported.")
                }
            }
        } catch (exception: Exception) {
            logger.error(exception)
        }
    }

    private fun deleteGesture(deleteGestureDto: DeleteGestureDto) {
        gestures.removeIf { gesture -> gesture.id == deleteGestureDto.id }
        sendGestures()
    }

    private fun saveGesture(saveGestureDto: SaveGestureDto) {
        val newGesture = saveGestureDto.gesture
        gestures.removeIf { gesture -> gesture.id == newGesture.id }
        gestures.add(newGesture)
        sendGestures()
    }

    private fun updateSettings(settings: SetSettingsDto) {
        if (currentSettings.telemetryFrequency != settings.telemetryFrequency) {
            stopTelemetry()
            startTelemetry()

            if (this::telemetryResetTimer.isInitialized) {
                telemetryResetTimer.cancel()
            }

            // Reset telemetry every five minutes
            telemetryResetTimer = timer(null, true, 0, TimeUnit.MINUTES.toMillis(5)) {
                telemetryResetTimer.cancel()

                currentSettings = SetSettingsDto(
                    currentSettings.typeWork, 1, currentSettings.enableEmg,
                    currentSettings.enableDisplay, currentSettings.enableGyro,
                    currentSettings.enableDriver
                )

                stopTelemetry()
                startTelemetry()
            }
        } else {
            if (this::telemetryResetTimer.isInitialized) {
                telemetryResetTimer.cancel()
            }
        }

        currentSettings = settings
        val newSettings = GetSettingsDto(
            currentSettings.typeWork, currentSettings.enableEmg,
            currentSettings.enableDisplay, currentSettings.enableGyro,
            currentSettings.enableDriver
        )

        client.sendData(Topics.GetSettings, ProtobufSerializer.serialize(newSettings))
    }
}