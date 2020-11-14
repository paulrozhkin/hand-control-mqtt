package com.handcontrol.server.mqtt.command.dto.gesture

import com.handcontrol.server.mqtt.command.dto.UuidDto
import kotlinx.serialization.Serializable

@Serializable
data class GestureDto(
        val id: UuidDto = UuidDto(""),
        val name: String = "",
        val lastTimeSync: Long = 0,
        val iterableGesture: Boolean = false,
        val numberOfGestureRepetitions: Byte = 0,
        val listActions: List<GestureActionDto> = emptyList()
)