package com.handcontrol.server.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("Session")
data class Session (
        @Id @Indexed val login: String,
        var prothesisId: String?
)