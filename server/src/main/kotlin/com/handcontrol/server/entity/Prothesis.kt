package com.handcontrol.server.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("Prothesis")
data class Prothesis(
    @Id @Indexed val id: String,
    var isOnline: Boolean,
    var settings: Settings? //todo: check if settings load from redis by getProthesis
) {
    companion object {
        fun createWith(id: String): Prothesis {
            return Prothesis(id, false, null)
        }
    }
}