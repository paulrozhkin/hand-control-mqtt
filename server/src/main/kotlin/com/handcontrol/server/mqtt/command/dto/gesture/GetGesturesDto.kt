package com.handcontrol.server.mqtt.command.dto.gesture

import kotlinx.serialization.Serializable

/**
 *  Actual gestures of a prosthesis
 */
@Serializable
data class GetGesturesDto(
        val lastTimeSync: Long = 0,
        val listGestures: List<GestureDto> = emptyList()
)