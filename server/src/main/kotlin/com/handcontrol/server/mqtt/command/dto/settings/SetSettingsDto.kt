package com.handcontrol.server.mqtt.command.dto.settings

import com.handcontrol.server.protobuf.Enums.ModeType
import com.handcontrol.server.protobuf.Settings
import kotlinx.serialization.Serializable

/**
 * New prosthesis configuration
 */
@Serializable
data class SetSettingsDto(
        val typeWork: ModeType = ModeType.MODE_MIO,
        val telemetryFrequency: Int = 0,
        val enableEmg: Boolean = false,
        val enableDisplay: Boolean = false,
        val enableGyro: Boolean = false,
        val enableDriver: Boolean = false,
        val powerOff: Boolean = false,
) {
    companion object {
        fun createFrom(from: Settings.SetSettings): SetSettingsDto {
            return SetSettingsDto(
                    typeWork = from.typeWork,
                    telemetryFrequency = from.telemetryFrequency,
                    enableEmg = from.enableEmg,
                    enableDisplay = from.enableDisplay,
                    enableGyro = from.enableGyro,
                    enableDriver = from.enableDriver,
                    powerOff = from.powerOff,
            )
        }
    }
}