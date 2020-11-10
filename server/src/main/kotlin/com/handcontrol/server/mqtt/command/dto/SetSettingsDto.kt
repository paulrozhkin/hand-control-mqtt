package com.handcontrol.server.mqtt.command.dto

import com.handcontrol.server.mqtt.command.dto.enums.ModuleTypeWork

/**
 * New prosthesis configuration
 */
@ExperimentalUnsignedTypes
data class SetSettingsDto (
        val typeWork: ModuleTypeWork,
        val telemetryFrequency: UShort,
        val enableEmg: Boolean,
        val enableDisplay: Boolean,
        val enableGyro: Boolean,
        val enableDriver: Boolean,
        val enable: Boolean
) : java.io.Serializable