package com.handcontrol.server.mqtt.command.dto.gesture

import kotlinx.serialization.Serializable

@Serializable
data class PerformGestureRawDto(
        val gesture: GestureDto
)