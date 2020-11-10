package com.handcontrol.server.mqtt.command

import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.dto.Id
import com.handcontrol.server.mqtt.command.dto.SetSettingsDto
import com.handcontrol.server.mqtt.command.dto.enums.ModuleTypeWork
import com.handcontrol.server.mqtt.command.enums.ApiMqttDynamicTopic
import com.handcontrol.server.mqtt.command.enums.ApiMqttStaticTopic.SET_ONLINE
import com.handcontrol.server.util.ObjectSerializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

@ExperimentalUnsignedTypes
@SpringBootTest
class SetOnlineTest(@Autowired val mqttWrapper: MqttClientWrapper) {

    @BeforeEach
    fun setUp() {
        SetOnline.pActive.clear()
    }

    @Test
    @DisplayName("test that pActive is not empty after publishing to SetOnline topic")
    fun testSetOnline() {
        val id = UUID.randomUUID().toString()
        val key = Id(id)
        mqttWrapper.publish(SET_ONLINE.topicName, ObjectSerializer.serialize(key))

        // need to give a publish handler some time
        Thread.sleep(100)
        val p = SetOnline.pActive[key]

        assertTrue(SetOnline.pActive.isNotEmpty())
        assertTrue(p!!)
    }


    @Test
    @DisplayName("run correct dynamic topic with write mode")
    fun runCorrectDynamicTopicWithWriteMode() {
        val id = UUID.randomUUID().toString()
        val settings =
                SetSettingsDto(ModuleTypeWork.WORK, 1u,
                        false, false, false, false, false)

        SetSettings.mqttWrapper = mqttWrapper
        val topic = ApiMqttDynamicTopic.SET_SETTINGS.topicName.replace("+", id)


        assertDoesNotThrow {
            ApiMqttDynamicTopic.SET_SETTINGS.getContentHandler()
                .invoke(id, ObjectSerializer.serialize(settings)) }
    }



}