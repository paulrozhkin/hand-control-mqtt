package com.handcontrol.server.mqtt.command.dto.gesture

import kotlinx.serialization.Serializable

@Serializable
data class SaveGestureDto(
        val timeSync: Long = 0,
        val gesture: GestureDto
)