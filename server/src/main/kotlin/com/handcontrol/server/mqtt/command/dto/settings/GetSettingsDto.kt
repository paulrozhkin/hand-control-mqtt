package com.handcontrol.server.mqtt.command.dto.settings

import com.handcontrol.server.mqtt.command.dto.enums.ModeType
import kotlinx.serialization.Serializable

/**
 * Add prosthesis configuration
 */
@Serializable
data class GetSettingsDto(
        val typeWork: ModeType = ModeType.MODE_MIO,
        val enableEmg: Boolean = false,
        val enableDisplay: Boolean = false,
        val enableGyro: Boolean = false,
        val enableDriver: Boolean = false,
)