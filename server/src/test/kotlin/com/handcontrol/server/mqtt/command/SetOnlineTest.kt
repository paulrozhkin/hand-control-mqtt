package com.handcontrol.server.mqtt.command

import com.handcontrol.server.mqtt.MqttClientWrapper
import com.handcontrol.server.mqtt.command.dto.Id
import com.handcontrol.server.mqtt.command.enums.ApiMqttTopic.SET_ONLINE
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

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
        mqttWrapper.publish(SET_ONLINE.topicName, id)
        val p = SetOnline.pActive[Id(id)]

        assertTrue(SetOnline.pActive.isNotEmpty())
        assertNotNull(p)
        assertTrue(p!!)
    }

}