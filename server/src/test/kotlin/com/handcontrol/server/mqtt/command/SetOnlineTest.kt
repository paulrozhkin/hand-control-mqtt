package com.handcontrol.server.mqtt.command

import com.handcontrol.server.cache.ProsthesisCache
import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.dto.enums.ModeType
import com.handcontrol.server.mqtt.command.dto.settings.SetSettingsDto
import com.handcontrol.server.mqtt.command.enums.ApiMqttDynamicTopic
import com.handcontrol.server.mqtt.command.enums.ApiMqttStaticTopic.SET_ONLINE
import com.handcontrol.server.mqtt.command.set.SetOnline
import com.handcontrol.server.mqtt.command.set.SetSettings
import com.handcontrol.server.util.ProtobufSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

@SpringBootTest
@ExperimentalSerializationApi
class SetOnlineTest(@Autowired val mqttWrapper: MqttClientWrapper) {

    @BeforeEach
    fun setUp() {
        ProsthesisCache.clear()
    }

    @Test
    @ExperimentalSerializationApi
    @DisplayName("test that pActive is not empty after publishing to SetOnline topic")
    fun testSetOnline() {
        val id = UUID.randomUUID().toString()
        mqttWrapper.publish(SET_ONLINE.topicName, ProtobufSerializer.serialize(id))

        // need to give a publish handler some time
        Thread.sleep(100)
        val p = ProsthesisCache.getStateById(id)

        assertTrue(p!!)
    }


    @Test
    @ExperimentalSerializationApi
    @DisplayName("run correct dynamic topic with write mode")
    fun runCorrectDynamicTopicWithWriteMode() {
        val id = UUID.randomUUID().toString()
        val settings = SetSettingsDto(
                ModeType.MODE_MIO, 1,
                false, false, false, false
        )

        SetSettings.mqttWrapper = mqttWrapper
        val topic = ApiMqttDynamicTopic.SET_SETTINGS.topicName.replace("+", id)


        assertDoesNotThrow {
            ApiMqttDynamicTopic.SET_SETTINGS.getContentHandler()
                .invoke(id, ProtobufSerializer.serialize(settings)) }
    }



}