package emulator.dto.settings

import emulator.dto.enums.ModeType
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