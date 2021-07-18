package com.handcontrol.server.mqtt.command.dto.gesture

import com.handcontrol.server.mqtt.command.dto.UuidDto
import com.handcontrol.server.protobuf.Gestures
import kotlinx.serialization.Serializable

@Serializable
data class PerformGestureByIdDto(
        val id: UuidDto = UuidDto(""),
) {
    companion object {
        fun createFrom(from: Gestures.PerformGestureById): PerformGestureByIdDto {
            return PerformGestureByIdDto(
                    id = UuidDto.createFrom(from.id)
            )
        }
    }
}