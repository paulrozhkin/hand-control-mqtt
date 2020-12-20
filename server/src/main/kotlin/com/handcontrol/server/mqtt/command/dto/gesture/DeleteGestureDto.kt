package com.handcontrol.server.mqtt.command.dto.gesture

import com.handcontrol.server.mqtt.command.dto.UuidDto
import com.handcontrol.server.protobuf.Gestures
import kotlinx.serialization.Serializable

@Serializable
data class DeleteGestureDto(
        val timeSync: Long = 0,
        val id: UuidDto = UuidDto(""),
) {
    companion object {
        fun createFrom(from: Gestures.DeleteGesture): DeleteGestureDto {
            return DeleteGestureDto(
                    timeSync = from.timeSync,
                    id = UuidDto.createFrom(from.id)
            )
        }
    }
}