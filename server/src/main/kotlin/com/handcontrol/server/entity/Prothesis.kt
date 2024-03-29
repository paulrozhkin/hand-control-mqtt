package com.handcontrol.server.entity

import com.handcontrol.server.mqtt.command.dto.gesture.GetGesturesDto
import com.handcontrol.server.mqtt.command.dto.settings.GetSettingsDto
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("Prothesis")
data class Prothesis(
    @Id @Indexed val id: String,
    @Indexed var isOnline: Boolean,
    var settings: GetSettingsDto?, //todo: check if settings load from redis by getProthesis
    var gestures: GetGesturesDto?
) {
    companion object {
        fun createWith(id: String): Prothesis {
            return Prothesis(id, false, null, null)
        }
    }
}