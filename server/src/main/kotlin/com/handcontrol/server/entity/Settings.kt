package com.handcontrol.server.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("Settings")
class Settings (
        @Id val id: String,
        var typeWork: String,
        var telemetryFrequency: String,
        var enableEmg: Boolean,
        var enableDisplay: Boolean,
        var enableGyro: Boolean,
        var enableDriver: Boolean
)