package com.handcontrol.server.mqtt.command

import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.enums.StaticApi.StaticTopic
import com.handcontrol.server.mqtt.command.set.SetSettings
import com.handcontrol.server.protobuf.Enums.ModeType
import com.handcontrol.server.protobuf.Settings
import com.handcontrol.server.service.ProthesisService
import kotlinx.serialization.ExperimentalSerializationApi
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
    lateinit var setSettingsCommand: SetSettings

    @Test
    @DisplayName("redis has an active entry after publishing to GetOnline and inactive after publishing to GetOffline")
    fun testGetOnlineOffline() {
        val id = UUID.randomUUID().toString()
        println(id);
        mqttWrapper.publish(StaticTopic.GET_ONLINE.topicName, id)

        // need to give a publish handler and redis some time
        Thread.sleep(300)
        val active = prosthesisSvc.isOnline(id)
        assertTrue(active)

        mqttWrapper.publish(StaticTopic.GET_OFFLINE.topicName, id)

        Thread.sleep(300)
        val inactive = prosthesisSvc.isOnline(id)
        assertFalse(inactive)

        val id2 = UUID.randomUUID().toString()

        val inactiveOpt = prosthesisSvc.isOnline(id2)
        assertFalse(inactiveOpt)
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