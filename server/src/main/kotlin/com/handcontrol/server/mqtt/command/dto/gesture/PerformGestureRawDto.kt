package com.handcontrol.server.mqtt.command.dto.gesture

import com.handcontrol.server.protobuf.Gestures
import kotlinx.serialization.Serializable

@Serializable
data class PerformGestureRawDto(
        val gesture: GestureDto
) {
    companion object {
        fun createFrom(from: Gestures.PerformGestureRaw): PerformGestureRawDto {
            return PerformGestureRawDto(
                    gesture = GestureDto.createFrom(from.gesture)
            )
        }
    }
}