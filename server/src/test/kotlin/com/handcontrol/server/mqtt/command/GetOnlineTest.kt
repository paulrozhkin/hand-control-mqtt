package com.handcontrol.server.mqtt.command

import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.dto.UuidDto
import com.handcontrol.server.mqtt.command.dto.gesture.GestureActionDto
import com.handcontrol.server.mqtt.command.dto.gesture.GestureDto
import com.handcontrol.server.mqtt.command.dto.gesture.GetGesturesDto
import com.handcontrol.server.mqtt.command.dto.settings.GetSettingsDto
import com.handcontrol.server.mqtt.command.get.GetGestures
import com.handcontrol.server.mqtt.command.get.GetOffline
import com.handcontrol.server.mqtt.command.get.GetOnline
import com.handcontrol.server.mqtt.command.get.GetSettings
import com.handcontrol.server.mqtt.command.set.SetSettings
import com.handcontrol.server.protobuf.Enums.ModeType
import com.handcontrol.server.protobuf.Settings
import com.handcontrol.server.service.ProthesisService
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.*

@SpringBootTest
@ActiveProfiles("dev")
@ExperimentalSerializationApi
class GetOnlineTest(@Autowired val mqttWrapper: MqttClientWrapper, @Autowired val prosthesisSvc: ProthesisService) {

    @Autowired
    lateinit var getGesturesCommand: GetGestures

    @Autowired
    lateinit var getSettingsCommand: GetSettings

    @Autowired
    lateinit var setSettingsCommand: SetSettings

    @Autowired
    lateinit var getOnlineCommand: GetOnline

    @Autowired
    lateinit var getOfflineCommand: GetOffline

    private val cachedUuids = mutableListOf<String>()

    @AfterEach
    fun tearDown() {
        cachedUuids.forEach { println(prosthesisSvc.getProthesisById(it)) }
        cachedUuids.asSequence()
            .onEach { prosthesisSvc.delete(it) }
        cachedUuids.clear()
    }

    // TODO for mqtt: check correct choice of handlers

    @Test
    @DisplayName("redis has an active entry after handling by GetOnline and inactive after handle by GetOffline")
    fun testGetOnlineOffline() {
        val id = UUID.randomUUID().toString()
        cachedUuids.add(id)

        getOnlineCommand.handlePayload(id.toByteArray())
        val active = prosthesisSvc.isOnline(id)
        assertTrue(active)
        val onlineProstheses = prosthesisSvc.getAllOnlineProtheses()
        assertTrue(onlineProstheses.isNotEmpty())

        getOfflineCommand.handlePayload(id.toByteArray())
        val inactive = prosthesisSvc.isOnline(id)
        assertFalse(inactive)

        val id2 = UUID.randomUUID().toString()
        cachedUuids.add(id2)

        val inactiveOpt = prosthesisSvc.isOnline(id2)
        assertFalse(inactiveOpt)
    }

    @Test
    fun testGetSettingsSave() {
        val id = UUID.randomUUID().toString()
        cachedUuids.add(id)

        val getSettings = GetSettingsDto(
            ModeType.MODE_AUTO, enableEmg = true, enableDisplay = false, enableGyro = false, enableDriver = true
        )
        getSettingsCommand.handlePayloadAndId(id, ProtobufSerializer.serialize(getSettings))

        val prosthesis = prosthesisSvc.getProthesisById(id)
        assertEquals(getSettings, prosthesis.get().settings)
    }

    @Test
    fun testGetGesturesSave() {
        val id = UUID.randomUUID().toString()
        cachedUuids.add(id)

        val unixTime = 1605088323L

        val a1 = GestureActionDto(1, 1, 1, 1, 1, 1)
        val a2 = GestureActionDto(2, 2, 2, 2, 2, 1)
        val g1 = GestureDto(UuidDto("1"), "name_gesture", 155L, true, 6, listActions = listOf(a1, a2))
        val g2 = GestureDto(UuidDto("2"), "name_gesture", 155L, true, 6, listActions = listOf(a1, a2))
        val getGestures = GetGesturesDto(unixTime, listGestures = listOf(g1, g2))

        getGesturesCommand.handlePayloadAndId(id, ProtobufSerializer.serialize(getGestures))

        val prosthesis = prosthesisSvc.getProthesisById(id)
        assertEquals(getGestures, prosthesis.get().gestures)
    }

    @Test
    @DisplayName("run correct dynamic topic with write mode")
    fun runCorrectDynamicTopicWithWriteMode() {
        val id = UUID.randomUUID().toString()
        val grpcSettings = Settings.SetSettings.newBuilder()
            .setEnableDisplay(true)
            .setEnableDriver(false)
            .setEnableEmg(true)
            .setEnableGyro(false)
            .setTelemetryFrequency(55)
            .setTypeWork(ModeType.MODE_AUTO)
            .build()

        setSettingsCommand.writeToProsthesis(id, grpcSettings)
        // todo mock mqttWrpapper and check sending
    }
}