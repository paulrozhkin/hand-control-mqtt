package com.handcontrol.server.entity

import com.handcontrol.server.protobuf.Enums
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

//@RedisHash("Settings")
class Settings (
//        @Id val id: String,
        var typeWork: Enums.ModeType,
        var telemetryFrequency: String,
        var enableEmg: Boolean,
        var enableDisplay: Boolean,
        var enableGyro: Boolean,
        var enableDriver: Boolean
)