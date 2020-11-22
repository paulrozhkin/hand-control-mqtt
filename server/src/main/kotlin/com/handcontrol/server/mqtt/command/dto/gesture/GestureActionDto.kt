package com.handcontrol.server.mqtt.command.dto.gesture

import kotlinx.serialization.Serializable

@Serializable
data class GestureActionDto(
        val pointerFingerPosition: Int = 0,
        val middleFingerPosition: Int = 0,
        val ringFinderPosition: Int = 0,
        val littleFingerPosition: Int = 0,
        val thumbFingerPosition: Int = 0,
        val delay: Int = 0
)