package com.handcontrol.server.mqtt.command.dto.gesture

import com.handcontrol.server.protobuf.Gestures
import kotlinx.serialization.Serializable

@Serializable
data class GestureActionDto(
        val pointerFingerPosition: Int = 0,
        val middleFingerPosition: Int = 0,
        val ringFinderPosition: Int = 0,
        val littleFingerPosition: Int = 0,
        val thumbFingerPosition: Int = 0,
        val delay: Int = 0
) {
    companion object {
        fun createFrom(from: Gestures.GestureAction): GestureActionDto {
            return GestureActionDto(
                    pointerFingerPosition = from.pointerFingerPosition,
                    middleFingerPosition = from.middleFingerPosition,
                    ringFinderPosition = from.ringFingerPosition,
                    littleFingerPosition = from.littleFingerPosition,
                    thumbFingerPosition = from.thumbFingerPosition,
                    delay = from.delay
            )
        }

        fun createFrom(from: List<Gestures.GestureAction>): List<GestureActionDto> {
            return from.map { createFrom(it) }
        }
    }
}