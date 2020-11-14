package com.handcontrol.server.mqtt.command.dto.gesture

import com.handcontrol.server.mqtt.command.dto.UuidDto
import kotlinx.serialization.Serializable

@Serializable
data class PerformGestureByIdDto(
        val id: UuidDto = UuidDto(""),
)