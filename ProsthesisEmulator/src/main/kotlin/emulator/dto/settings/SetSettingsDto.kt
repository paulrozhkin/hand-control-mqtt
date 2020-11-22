package emulator.dto.settings

import emulator.dto.enums.ModeType
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
)