package com.handcontrol.server.mqtt.command.dto.gesture

import com.handcontrol.server.protobuf.Gestures
import kotlinx.serialization.Serializable

@Serializable
data class SaveGestureDto(
        val timeSync: Long = 0,
        val gesture: GestureDto
) {
    companion object {
        fun createFrom(from: Gestures.SaveGesture): SaveGestureDto {
            return SaveGestureDto(
                    timeSync = from.timeSync,
                    gesture = GestureDto.createFrom(from.gesture)
            )
        }
    }
}