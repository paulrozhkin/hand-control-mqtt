package emulator.dto

import kotlinx.serialization.Serializable

/**
 * A prosthesis identifier
 */
@Serializable
data class UuidDto(val value: String = "")