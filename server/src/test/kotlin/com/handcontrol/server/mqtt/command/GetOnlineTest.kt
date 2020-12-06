package com.handcontrol.server.mqtt.command

import com.handcontrol.server.cache.ProsthesisCache
import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.enums.StaticApi.StaticTopic
import com.handcontrol.server.mqtt.command.set.SetSettings
import com.handcontrol.server.protobuf.Enums.ModeType
import com.handcontrol.server.protobuf.Settings
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.*

@SpringBootTest
@ActiveProfiles("dev")
@ExperimentalSerializationApi
class GetOnlineTest(@Autowired val mqttWrapper: MqttClientWrapper) {

    @Autowired
    lateinit var setSettingsCommand: SetSettings

    @BeforeEach
    fun setUp() {
        ProsthesisCache.clear()
    }

    @Test
    @DisplayName("cache has an active entry after publishing to GetOnline and inactive after publishing to GetOffline")
    fun testGetOnlineOffline() {
        val id = UUID.randomUUID().toString()
        mqttWrapper.publish(StaticTopic.GET_ONLINE.topicName, id)

        // need to give a publish handler some time
        Thread.sleep(100)
        val active = ProsthesisCache.getStateById(id)

        assertTrue(active!!)

        mqttWrapper.publish(StaticTopic.GET_OFFLINE.topicName, id)

        Thread.sleep(100)
        val inactive = ProsthesisCache.getStateById(id)

        assertFalse(inactive!!)
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