package com.handcontrol.server.mqtt.command.dto.gesture

import com.handcontrol.server.mqtt.command.dto.UuidDto
import kotlinx.serialization.Serializable
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.annotation.Id


@RedisHash
@Serializable
data class GestureDto(
        @Id val id: UuidDto = UuidDto(""),
        val name: String = "",
        val lastTimeSync: Long = 0,
        val iterableGesture: Boolean = false,
        val numberOfGestureRepetitions: Byte = 0,
        val listActions: List<GestureActionDto> = emptyList()
)