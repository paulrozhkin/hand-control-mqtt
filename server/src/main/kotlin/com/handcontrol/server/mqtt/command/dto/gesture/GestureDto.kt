package com.handcontrol.server.mqtt.command.dto.gesture

import com.handcontrol.server.mqtt.command.dto.UuidDto
import com.handcontrol.server.protobuf.Gestures
import kotlinx.serialization.Serializable

@Serializable
data class GestureDto(
        val id: UuidDto = UuidDto(""),
        val name: String = "",
        val lastTimeSync: Long = 0,
        val iterableGesture: Boolean = false,
        val numberOfGestureRepetitions: Int = 0,
        val listActions: List<GestureActionDto> = emptyList()
) {
    companion object {
        fun createFrom(from: Gestures.Gesture): GestureDto {
            return GestureDto(
                    id = UuidDto.createFrom(from.id),
                    name = from.name,
                    lastTimeSync = from.lastTimeSync,
                    iterableGesture = from.iterable,
                    numberOfGestureRepetitions = from.repetitions,
                    listActions = GestureActionDto.createFrom(from.actionsList)
            )
        }
    }
}