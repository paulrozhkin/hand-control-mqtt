package com.handcontrol.server.mqtt.command.dto.gesture

import kotlinx.serialization.Serializable

@Serializable
data class SetPositionDto(
        val pointerFingerPosition: Byte = 0,
        val middleFingerPosition: Byte = 0,
        val ringFinderPosition: Byte = 0,
        val littleFingerPosition: Byte = 0,
        val thumbFingerPosition: Byte = 0,
)