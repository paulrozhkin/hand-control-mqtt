package com.handcontrol.server.mqtt.command.dto.gesture

import com.handcontrol.server.protobuf.Gestures
import kotlinx.serialization.Serializable

@Serializable
data class SetPositionsDto(
        val pointerFingerPosition: Int = 0,
        val middleFingerPosition: Int = 0,
        val ringFinderPosition: Int = 0,
        val littleFingerPosition: Int = 0,
        val thumbFingerPosition: Int = 0,
) {
    companion object {
        fun createFrom(from: Gestures.SetPositions): SetPositionsDto {
            return SetPositionsDto(
                    pointerFingerPosition = from.pointerFingerPosition,
                    middleFingerPosition = from.middleFingerPosition,
                    ringFinderPosition = from.ringFingerPosition,
                    littleFingerPosition = from.littleFingerPosition,
                    thumbFingerPosition = from.thumbFingerPosition
            )
        }
    }
}