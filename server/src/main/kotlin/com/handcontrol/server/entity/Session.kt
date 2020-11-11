package com.handcontrol.server.entity

import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("Session")
data class Session (
    @Indexed val id: String,
    val login: String,
    val prosthesisId: String
){
}