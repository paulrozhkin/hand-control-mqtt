package com.handcontrol.server.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("Prothesis")
data class Prothesis (
        @Id @Indexed val id: String,
        val isOnline: Boolean
)